#!/usr/bin/env python3
"""Génère country_borders.json : frontières réelles des pays (Natural Earth 110m),
projetées en coordonnées normalisées 0..1 (équirectangulaire) et associées aux
identifiants internes de l'app via le code ISO2 (déduit du drapeau emoji) + alias de noms.

Sortie : app/src/main/assets/country_borders.json
Format  : { "<id>": [ [[x,y],[x,y],...], ... ] }   # liste d'anneaux extérieurs

Régénérer : python tools/generate_borders.py
"""
import json
import re
import unicodedata
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "app" / "src" / "main" / "assets"
NE_URL = (
    "https://raw.githubusercontent.com/nvkelso/natural-earth-vector/"
    "master/geojson/ne_110m_admin_0_countries.geojson"
)

# Précision des coordonnées normalisées + seuils de simplification
COORD_DECIMALS = 4
MIN_RING_POINTS = 4
# bbox (largeur*hauteur en unités normalisées) minimale pour garder un anneau secondaire
MIN_SECONDARY_RING_AREA = 6e-5


def norm_name(s: str) -> str:
    s = unicodedata.normalize("NFKD", s or "")
    s = "".join(c for c in s if not unicodedata.combining(c))
    s = s.lower()
    s = re.sub(r"[^a-z0-9]+", " ", s).strip()
    return s


# Noms NE -> id interne quand l'ISO2 ne suffit pas / diffère
NAME_TO_ID = {
    "united states of america": "usa",
    "united kingdom": "england",
    "south korea": "south_korea",
    "north korea": "north_korea",
    "republic of korea": "south_korea",
    "ivory coast": "ivory_coast",
    "cote d ivoire": "ivory_coast",
    "czechia": "czechia",
    "czech republic": "czechia",
    "republic of serbia": "serbia",
    "the bahamas": "bahamas",
    "united republic of tanzania": "tanzania",
    "democratic republic of the congo": "dr_congo",
    "republic of the congo": "congo",
    "guinea bissau": "guinea_bissau",
    "eswatini": "eswatini",
    "north macedonia": "north_macedonia",
    "bosnia and herzegovina": "bosnia_and_herzegovina",
    "russia": "russia",
    "syria": "syria",
    "laos": "laos",
    "vietnam": "vietnam",
    "brunei": "brunei",
    "east timor": "timor_leste",
    "myanmar": "myanmar",
    "turkey": "turkey",
    "turkiye": "turkey",
    "cape verde": "cape_verde",
    "cabo verde": "cape_verde",
    "curacao": "curacao",
    "western sahara": "western_sahara",
    "falkland islands": "falkland_islands",
    "northern cyprus": "cyprus",
    "somaliland": "somalia",
    "kosovo": "kosovo",
}


def iso2_from_flag(flag: str) -> str | None:
    """Déduit le code ISO2 d'un drapeau emoji composé de 2 indicateurs régionaux."""
    cps = [ord(ch) for ch in flag]
    ri = [cp for cp in cps if 0x1F1E6 <= cp <= 0x1F1FF]
    if len(ri) != 2:
        return None
    return "".join(chr(cp - 0x1F1E6 + ord("A")) for cp in ri)


def project(lon: float, lat: float) -> tuple[float, float]:
    nx = (lon + 180.0) / 360.0
    ny = (90.0 - lat) / 180.0
    return round(nx, COORD_DECIMALS), round(ny, COORD_DECIMALS)


def simplify_ring(coords: list) -> list:
    out = []
    prev = None
    for lon, lat in coords:
        p = project(lon, lat)
        if p != prev:
            out.append(p)
            prev = p
    # ferme proprement
    if len(out) >= 2 and out[0] != out[-1]:
        out.append(out[0])
    return out


def ring_bbox_area(ring: list) -> float:
    xs = [p[0] for p in ring]
    ys = [p[1] for p in ring]
    return (max(xs) - min(xs)) * (max(ys) - min(ys))


def extract_rings(geom: dict) -> list:
    """Retourne la liste des anneaux extérieurs (exterieurs uniquement)."""
    rings = []
    t = geom["type"]
    if t == "Polygon":
        polys = [geom["coordinates"]]
    elif t == "MultiPolygon":
        polys = geom["coordinates"]
    else:
        return rings
    for poly in polys:
        if not poly:
            continue
        exterior = poly[0]  # on ignore les trous
        r = simplify_ring(exterior)
        if len(r) >= MIN_RING_POINTS:
            rings.append(r)
    return rings


def keep_rings(rings: list) -> list:
    if not rings:
        return rings
    # garde toujours le plus grand anneau + les anneaux assez gros
    biggest = max(rings, key=ring_bbox_area)
    kept = [r for r in rings if r is biggest or ring_bbox_area(r) >= MIN_SECONDARY_RING_AREA]
    return kept


def main():
    print("Téléchargement Natural Earth 110m…")
    req = urllib.request.Request(NE_URL, headers={"User-Agent": "kid-worldmap-app/borders"})
    ne = json.loads(urllib.request.urlopen(req, timeout=120).read().decode())

    countries = json.loads((ASSETS / "world_countries.json").read_text(encoding="utf-8"))

    # index app : iso2 -> id, name -> id
    iso2_to_id = {}
    name_to_id = {}
    for c in countries:
        cid = c["id"]
        iso2 = iso2_from_flag(c.get("flagEmoji", ""))
        if iso2 and iso2 not in iso2_to_id:
            iso2_to_id[iso2] = cid
        name_to_id[norm_name(c.get("nameEn", ""))] = cid

    borders: dict[str, list] = {}
    matched = set()
    unmatched_features = []

    for feat in ne["features"]:
        props = feat["properties"]
        iso2 = props.get("ISO_A2_EH") or props.get("ISO_A2") or ""
        iso2 = iso2.strip().upper()
        nm = norm_name(props.get("NAME_EN") or props.get("ADMIN") or props.get("NAME") or "")

        cid = None
        if iso2 and iso2 not in ("-99", "") and iso2 in iso2_to_id:
            cid = iso2_to_id[iso2]
        if cid is None and nm in NAME_TO_ID:
            cid = NAME_TO_ID[nm]
        if cid is None and nm in name_to_id:
            cid = name_to_id[nm]

        if cid is None:
            unmatched_features.append(props.get("NAME_EN") or props.get("ADMIN"))
            continue

        rings = keep_rings(extract_rings(feat["geometry"]))
        if not rings:
            continue
        if cid in borders:
            borders[cid].extend(rings)
        else:
            borders[cid] = rings
        matched.add(cid)

    ASSETS.mkdir(parents=True, exist_ok=True)
    out = ASSETS / "country_borders.json"
    out.write_text(json.dumps(borders, separators=(",", ":")), encoding="utf-8")

    total_points = sum(len(r) for rings in borders.values() for r in rings)
    size_kb = out.stat().st_size / 1024
    print(f"OK : {len(borders)} pays avec frontières, {total_points} points, {size_kb:.0f} Ko")

    wc_ids = {c["id"] for c in countries if c.get("isWorldCup2026")}
    missing_wc = sorted(wc_ids - matched - {
        "playoff_uefa_a", "playoff_uefa_b", "playoff_uefa_c", "playoff_uefa_d",
        "playoff_fifa_1", "playoff_fifa_2",
    })
    if missing_wc:
        print("[WARN] Pays CDM sans frontieres :", missing_wc)
    if unmatched_features:
        print(f"[INFO] {len(unmatched_features)} entites NE non associees (ex.):",
              unmatched_features[:15])


if __name__ == "__main__":
    main()
