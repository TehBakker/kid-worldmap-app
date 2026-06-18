#!/usr/bin/env python3
"""Enrichit world_countries.json avec les coordonnées réelles des capitales
(capitalMapX/capitalMapY).

Sources :
  - GeoNames cities15000 (feature_code PPLC) -> capitale + coordonnées par pays (ISO2)
  - dr5hn countries.csv -> ISO2 par nom de pays

Applique aussi des corrections de continent (ex. Russie -> Asie).
Idempotent. Repli sur le centre du pays (mapX/mapY) si la capitale est introuvable.
"""
import csv
import io
import json
import urllib.request
import zipfile
from pathlib import Path

ASSETS = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets"
WORLD = ASSETS / "world_countries.json"

GEONAMES_URL = "https://download.geonames.org/export/dump/cities15000.zip"
DR5HN_CSV = "https://raw.githubusercontent.com/dr5hn/countries-states-cities-database/master/csv/countries.csv"

CONTINENT_OVERRIDES = {
    "russia": "Asie",
}
CONTINENT_COLOR = {
    "Afrique": "#F9A825",
    "Asie": "#E53935",
    "Europe": "#1565C0",
    "Amérique du Nord": "#43A047",
    "Amérique du Sud": "#2E7D32",
    "Océanie": "#FF7043",
    "Antarctique": "#90A4AE",
}

# Coordonnées de capitales (lat, lon) en repli/override (noms divergents, nos extras).
CAPITAL_OVERRIDES = {
    "england": (51.5074, -0.1278),    # Londres
    "scotland": (55.9533, -3.1883),   # Édimbourg
    "wales": (51.4816, -3.1791),      # Cardiff
}

# Alias nom pays (notre nameEn -> nom dr5hn) si besoin
COUNTRY_ALIASES = {
    "the bahamas": "bahamas",
    "democratic republic of the congo": "congo (kinshasa)",
    "ivory coast": "cote d'ivoire",
}


def norm(s: str) -> str:
    if not s:
        return ""
    s = s.strip().lower()
    for a, b in {
        "é": "e", "è": "e", "ê": "e", "ë": "e", "à": "a", "â": "a", "ä": "a",
        "ô": "o", "ö": "o", "î": "i", "ï": "i", "û": "u", "ü": "u",
        "ç": "c", "ñ": "n", "’": "'", "ã": "a", "õ": "o",
    }.items():
        s = s.replace(a, b)
    return s


def latlon_to_map(lat: float, lon: float):
    return round((lon + 180) / 360, 4), round((90 - lat) / 180, 4)


def fetch_iso2_by_name():
    req = urllib.request.Request(DR5HN_CSV, headers={"User-Agent": "kid-worldmap-app/1.2"})
    text = urllib.request.urlopen(req, timeout=90).read().decode("utf-8", "replace")
    reader = csv.DictReader(io.StringIO(text))
    out = {}
    for row in reader:
        name = row.get("name") or ""
        iso2 = (row.get("iso2") or "").upper()
        if name and iso2:
            out[norm(name)] = iso2
    return out


def fetch_capitals_by_iso2():
    req = urllib.request.Request(GEONAMES_URL, headers={"User-Agent": "kid-worldmap-app/1.2"})
    data = urllib.request.urlopen(req, timeout=120).read()
    zf = zipfile.ZipFile(io.BytesIO(data))
    raw = zf.read("cities15000.txt").decode("utf-8")
    caps = {}        # iso2 -> (lat, lon, pop)
    by_name = {}     # norm(asciiname) -> (iso2, lat, lon)
    for ln in raw.splitlines():
        f = ln.split("\t")
        if len(f) < 15:
            continue
        asci, lat, lon, fcode, cc, pop = f[2], f[4], f[5], f[7], f[8], f[14]
        if fcode != "PPLC":
            continue
        try:
            lat, lon, pop = float(lat), float(lon), int(pop or 0)
        except ValueError:
            continue
        prev = caps.get(cc)
        if prev is None or pop > prev[2]:
            caps[cc] = (lat, lon, pop)
        by_name[norm(asci)] = (cc, lat, lon)
    return caps, by_name


def main():
    countries = json.loads(WORLD.read_text(encoding="utf-8"))
    iso2_by_name = fetch_iso2_by_name()
    caps, caps_by_name = fetch_capitals_by_iso2()

    matched = 0
    misses = []
    for c in countries:
        cid = c["id"]

        if cid in CONTINENT_OVERRIDES:
            cont = CONTINENT_OVERRIDES[cid]
            c["continent"] = cont
            c["colorHex"] = CONTINENT_COLOR.get(cont, c.get("colorHex", "#607D8B"))

        coord = None
        if cid in CAPITAL_OVERRIDES:
            coord = CAPITAL_OVERRIDES[cid]
        else:
            name = norm(c.get("nameEn", ""))
            name = COUNTRY_ALIASES.get(name, name)
            iso2 = iso2_by_name.get(name)
            if iso2 and iso2 in caps:
                coord = (caps[iso2][0], caps[iso2][1])
            if coord is None:
                # Repli : matcher le nom de la capitale dans GeoNames
                hit = caps_by_name.get(norm(c.get("capital", "")))
                if hit:
                    coord = (hit[1], hit[2])

        if coord is not None:
            mx, my = latlon_to_map(coord[0], coord[1])
            matched += 1
        else:
            mx, my = c.get("mapX", 0.5), c.get("mapY", 0.5)
            if not cid.startswith("playoff"):
                misses.append(f"{cid} ({c.get('capital')})")

        c["capitalMapX"] = mx
        c["capitalMapY"] = my

    WORLD.write_text(json.dumps(countries, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"{matched}/{len(countries)} capitales géolocalisées.")
    if misses:
        print(f"{len(misses)} replis (centre pays) :")
        print(", ".join(misses))


if __name__ == "__main__":
    main()
