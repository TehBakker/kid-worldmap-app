#!/usr/bin/env python3
"""Génère world_countries.json, worldcup_2026_groups.json et matches.json depuis REST Countries."""
import json
import urllib.request
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets"
OUT.mkdir(parents=True, exist_ok=True)

CONTINENT_FR = {
    "Africa": "Afrique",
    "Asia": "Asie",
    "Europe": "Europe",
    "North America": "Amérique du Nord",
    "South America": "Amérique du Sud",
    "Oceania": "Océanie",
    "Antarctica": "Antarctique",
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

# Corrections manuelles de continent (id interne -> continent FR).
# Ex. la Russie est rangée en Asie pour une Europe plus lisible.
CONTINENT_OVERRIDES = {
    "russia": "Asie",
}

# Poules officielles CDM 2026 (tirage du 5 déc. 2025 — barrages mars 2026 en placeholders)
WC2026_GROUPS = {
    "A": ["mexico", "south_africa", "south_korea", "playoff_uefa_d"],
    "B": ["canada", "playoff_uefa_a", "qatar", "switzerland"],
    "C": ["brazil", "morocco", "haiti", "scotland"],
    "D": ["usa", "paraguay", "australia", "playoff_uefa_c"],
    "E": ["germany", "curacao", "ivory_coast", "ecuador"],
    "F": ["netherlands", "japan", "playoff_uefa_b", "tunisia"],
    "G": ["belgium", "egypt", "iran", "new_zealand"],
    "H": ["spain", "cape_verde", "saudi_arabia", "uruguay"],
    "I": ["france", "senegal", "playoff_fifa_2", "norway"],
    "J": ["argentina", "algeria", "austria", "jordan"],
    "K": ["portugal", "playoff_fifa_1", "uzbekistan", "colombia"],
    "L": ["england", "croatia", "ghana", "panama"],
}

PLAYOFF_PLACEHOLDERS = [
    ("playoff_uefa_a", "Barrage UEFA A", "UEFA Play-off A", "🏳️", "Europe", "TBD"),
    ("playoff_uefa_b", "Barrage UEFA B", "UEFA Play-off B", "🏳️", "Europe", "TBD"),
    ("playoff_uefa_c", "Barrage UEFA C", "UEFA Play-off C", "🏳️", "Europe", "TBD"),
    ("playoff_uefa_d", "Barrage UEFA D", "UEFA Play-off D", "🏳️", "Europe", "TBD"),
    ("playoff_fifa_1", "Barrage FIFA 1", "FIFA Play-off 1", "🏳️", "Amérique", "TBD"),
    ("playoff_fifa_2", "Barrage FIFA 2", "FIFA Play-off 2", "🏳️", "Amérique", "TBD"),
]

# Alias id internes (REST Countries -> nos ids)
ID_ALIASES = {
    "united states": "usa",
    "united kingdom": "england",  # équipe angleterre CDM
    "korea, south": "south_korea",
    "korea, north": "north_korea",
    "côte d'ivoire": "ivory_coast",
    "cote d'ivoire": "ivory_coast",
    "iran, islamic republic of": "iran",
    "russian federation": "russia",
    "viet nam": "vietnam",
    "lao people's democratic republic": "laos",
    "syrian arab republic": "syria",
    "tanzania, united republic of": "tanzania",
    "bolivia, plurinational state of": "bolivia",
    "venezuela, bolivarian republic of": "venezuela",
    "moldova, republic of": "moldova",
    "micronesia, federated states of": "micronesia",
    "congo, democratic republic of the": "dr_congo",
    "congo": "congo",
    "cabo verde": "cape_verde",
    "curaçao": "curacao",
    "eswatini": "eswatini",
    "north macedonia": "north_macedonia",
    "czechia": "czechia",
    "turkey": "turkey",
    "türkiye": "turkey",
}

WC_ID_SET = {cid for g in WC2026_GROUPS.values() for cid in g if not cid.startswith("playoff")}


def flag_emoji(code: str) -> str:
    if not code or len(code) != 2:
        return "🏳️"
    return "".join(chr(0x1F1E6 + ord(c) - ord("A")) for c in code.upper())


def slug(name: str) -> str:
    s = name.lower().strip()
    if s in ID_ALIASES:
        return ID_ALIASES[s]
    return (
        s.replace("'", "")
        .replace(".", "")
        .replace(",", "")
        .replace("é", "e")
        .replace("è", "e")
        .replace("ê", "e")
        .replace("à", "a")
        .replace("ô", "o")
        .replace("ü", "u")
        .replace("ö", "o")
        .replace("ï", "i")
        .replace("ç", "c")
        .replace(" ", "_")
        .replace("-", "_")
    )


def latlon_to_map(lat: float, lon: float) -> tuple[float, float]:
    return round((lon + 180) / 360, 4), round((90 - lat) / 180, 4)


def fetch_countries():
    """Télécharge les pays depuis un CSV public (fallback fiable)."""
    url = "https://raw.githubusercontent.com/dr5hn/countries-states-cities-database/master/json/countries.json"
    req = urllib.request.Request(url, headers={"User-Agent": "kid-worldmap-app/1.1"})
    with urllib.request.urlopen(req, timeout=90) as r:
        data = json.loads(r.read().decode())
    out = []
    for c in data:
        lat = float(c.get("latitude") or 0)
        lon = float(c.get("longitude") or 0)
        out.append(
            {
                "name": {"common": c.get("name", "?")},
                "translations": {"fra": {"common": c.get("translations", {}).get("fr", c.get("name"))}},
                "capital": [c.get("capital", "—")],
                "population": int(c.get("population") or 0),
                "continents": [c.get("region", "Unknown")],
                "latlng": [lat, lon],
                "languages": {},
                "currencies": {},
                "cca2": c.get("iso2", "XX"),
            }
        )
    return out


def main():
    raw = fetch_countries()
    countries = []
    seen_ids = set()

    for c in raw:
        en = c["name"]["common"]
        fr = c.get("translations", {}).get("fra", {}).get("common", en)
        cid = slug(en)
        if cid in seen_ids:
            cid = f"{cid}_{c['cca2'].lower()}"
        seen_ids.add(cid)

        capitals = c.get("capital") or ["—"]
        capital = capitals[0]
        pop = c.get("population") or 0
        if pop >= 1_000_000:
            pop_s = f"{pop // 1_000_000} millions"
        elif pop >= 1_000:
            pop_s = f"{pop // 1_000} milliers"
        else:
            pop_s = str(pop)

        cont_raw = (c.get("continents") or ["Unknown"])[0]
        cont_map = {
            "Africa": "Africa",
            "Asia": "Asia",
            "Europe": "Europe",
            "Americas": "North America",
            "Oceania": "Oceania",
            "Antarctica": "Antarctica",
            "Unknown": "Unknown",
        }
        cont_en = cont_map.get(cont_raw, cont_raw)
        if cont_en == "North America" and lat < 15 and lon < -30:
            continent = "Amérique du Sud" if lat < 12 else CONTINENT_FR.get("North America", "Amérique du Nord")
        else:
            continent = CONTINENT_FR.get(cont_en, cont_en)
        continent = CONTINENT_OVERRIDES.get(cid, continent)

        langs = c.get("languages") or {}
        main_lang = next(iter(langs.values()), "—")

        currs = c.get("currencies") or {}
        currency = next((v.get("name", k) for k, v in currs.items()), "—")

        lat, lon = (c.get("latlng") or [0, 0])[:2]
        map_x, map_y = latlon_to_map(lat, lon)

        # Angleterre pour CDM : garder aussi united_kingdom si besoin
        wc_group = None
        for g, teams in WC2026_GROUPS.items():
            if cid in teams:
                wc_group = g
                break

        countries.append(
            {
                "id": cid,
                "nameFr": fr,
                "nameEn": en,
                "flagEmoji": flag_emoji(c["cca2"]),
                "capital": capital,
                "population": pop_s,
                "continent": continent,
                "mainLanguage": main_lang,
                "currency": currency,
                "mapX": map_x,
                "mapY": map_y,
                "colorHex": CONTINENT_COLOR.get(continent, "#607D8B"),
                "worldCup2026Group": wc_group,
                "isWorldCup2026": cid in WC_ID_SET or wc_group is not None,
            }
        )

    # Scotland, England not always separate in REST — add if missing
    extras = [
        {
            "id": "england",
            "nameFr": "Angleterre",
            "nameEn": "England",
            "flagEmoji": "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
            "capital": "Londres",
            "population": "56 millions",
            "continent": "Europe",
            "mainLanguage": "Anglais",
            "currency": "Livre sterling",
            "mapX": 0.46,
            "mapY": 0.24,
            "colorHex": "#3949AB",
            "worldCup2026Group": "L",
            "isWorldCup2026": True,
        },
        {
            "id": "scotland",
            "nameFr": "Écosse",
            "nameEn": "Scotland",
            "flagEmoji": "🏴󠁧󠁢󠁳󠁣󠁴󠁿",
            "capital": "Édimbourg",
            "population": "5 millions",
            "continent": "Europe",
            "mainLanguage": "Anglais",
            "currency": "Livre sterling",
            "mapX": 0.45,
            "mapY": 0.22,
            "colorHex": "#3949AB",
            "worldCup2026Group": "C",
            "isWorldCup2026": True,
        },
        {
            "id": "wales",
            "nameFr": "Pays de Galles",
            "nameEn": "Wales",
            "flagEmoji": "🏴󠁧󠁢󠁷󠁬󠁳󠁿",
            "capital": "Cardiff",
            "population": "3 millions",
            "continent": "Europe",
            "mainLanguage": "Anglais",
            "currency": "Livre sterling",
            "mapX": 0.455,
            "mapY": 0.245,
            "colorHex": "#3949AB",
            "worldCup2026Group": None,
            "isWorldCup2026": False,
        },
    ]
    existing = {c["id"] for c in countries}
    for e in extras:
        if e["id"] not in existing:
            countries.append(e)

    for pid, nfr, nen, emoji, cont, cap in PLAYOFF_PLACEHOLDERS:
        wc_g = next(g for g, t in WC2026_GROUPS.items() if pid in t)
        countries.append(
            {
                "id": pid,
                "nameFr": nfr,
                "nameEn": nen,
                "flagEmoji": emoji,
                "capital": cap,
                "population": "—",
                "continent": cont,
                "mainLanguage": "—",
                "currency": "—",
                "mapX": 0.5,
                "mapY": 0.5,
                "colorHex": "#9E9E9E",
                "worldCup2026Group": wc_g,
                "isWorldCup2026": True,
            }
        )

    countries.sort(key=lambda x: x["nameFr"])
    (OUT / "world_countries.json").write_text(
        json.dumps(countries, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    groups_out = {
        "competition": "Coupe du monde 2026",
        "hostCountries": ["canada", "mexico", "usa"],
        "groups": [
            {"id": g, "label": f"Poule {g}", "teamIds": teams}
            for g, teams in sorted(WC2026_GROUPS.items())
        ],
    }
    (OUT / "worldcup_2026_groups.json").write_text(
        json.dumps(groups_out, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    matches = [
        {"id": "wc26-opener", "label": "Mexique vs Afrique du Sud", "countryAId": "mexico", "countryBId": "south_africa",
         "competition": "CDM 2026", "dateLabel": "11 juin 2026", "groupOrStage": "Poule A", "phase": "group", "featured": True},
        {"id": "wc26-b1", "label": "Canada vs Suisse", "countryAId": "canada", "countryBId": "switzerland",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule B", "phase": "group"},
        {"id": "wc26-c1", "label": "Brésil vs Maroc", "countryAId": "brazil", "countryBId": "morocco",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule C", "phase": "group"},
        {"id": "wc26-d1", "label": "États-Unis vs Australie", "countryAId": "usa", "countryBId": "australia",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule D", "phase": "group"},
        {"id": "wc26-e1", "label": "Allemagne vs Équateur", "countryAId": "germany", "countryBId": "ecuador",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule E", "phase": "group"},
        {"id": "wc26-f1", "label": "Pays-Bas vs Japon", "countryAId": "netherlands", "countryBId": "japan",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule F", "phase": "group"},
        {"id": "wc26-g1", "label": "Belgique vs Égypte", "countryAId": "belgium", "countryBId": "egypt",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule G", "phase": "group"},
        {"id": "wc26-h1", "label": "Espagne vs Uruguay", "countryAId": "spain", "countryBId": "uruguay",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule H", "phase": "group"},
        {"id": "wc26-i1", "label": "France vs Sénégal", "countryAId": "france", "countryBId": "senegal",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule I", "phase": "group"},
        {"id": "wc26-j1", "label": "Argentine vs Algérie", "countryAId": "argentina", "countryBId": "algeria",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule J", "phase": "group"},
        {"id": "wc26-k1", "label": "Portugal vs Colombie", "countryAId": "portugal", "countryBId": "colombia",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule K", "phase": "group"},
        {"id": "wc26-l1", "label": "Angleterre vs Croatie", "countryAId": "england", "countryBId": "croatia",
         "competition": "CDM 2026", "dateLabel": "Juin 2026", "groupOrStage": "Poule L", "phase": "group"},
        {"id": "wc26-final", "label": "Finale CDM 2026", "countryAId": "france", "countryBId": "brazil",
         "competition": "CDM 2026", "dateLabel": "Juillet 2026 (à confirmer)", "groupOrStage": "Finale", "phase": "knockout", "featured": True},
    ]
    (OUT / "matches.json").write_text(
        json.dumps(matches, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    print(f"Generated {len(countries)} countries, 12 groups, {len(matches)} matches -> {OUT}")


if __name__ == "__main__":
    main()
