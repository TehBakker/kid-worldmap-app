#!/usr/bin/env python3
"""Valide les fichiers JSON embarqués (pays, poules, matchs)."""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "app" / "src" / "main" / "assets"

REQUIRED_COUNTRY_KEYS = {
    "id", "nameFr", "nameEn", "flagEmoji", "capital", "population",
    "continent", "mapX", "mapY", "colorHex",
}


def load_json(path: Path):
    with path.open(encoding="utf-8") as f:
        return json.load(f)


def validate_countries(data: list) -> None:
    if len(data) < 100:
        raise ValueError(f"world_countries.json: expected 100+ countries, got {len(data)}")
    ids = set()
    for i, c in enumerate(data):
        missing = REQUIRED_COUNTRY_KEYS - c.keys()
        if missing:
            raise ValueError(f"country[{i}] id={c.get('id')}: missing keys {missing}")
        if c["id"] in ids:
            raise ValueError(f"duplicate country id: {c['id']}")
        ids.add(c["id"])
        if not (0 <= c["mapX"] <= 1 and 0 <= c["mapY"] <= 1):
            raise ValueError(f"country {c['id']}: mapX/mapY must be 0..1")


def validate_groups(data: dict, country_ids: set[str]) -> None:
    groups = data.get("groups", [])
    if len(groups) != 12:
        raise ValueError(f"expected 12 groups for CDM 2026, got {len(groups)}")
    for g in groups:
        for team_id in g.get("teamIds", []):
            if team_id not in country_ids:
                raise ValueError(f"group {g['id']}: unknown team id '{team_id}'")


def validate_matches(data: list, country_ids: set[str]) -> None:
    if not data:
        raise ValueError("matches.json is empty")
    for m in data:
        if m.get("competition", "").find("2026") < 0 and m.get("competition", "").find("CDM 2026") < 0:
            # allow CDM 2026 label variants
            comp = m.get("competition", "")
            if "2026" not in comp:
                raise ValueError(f"match {m.get('id')}: expected CDM 2026, got '{comp}'")
        for key in ("countryAId", "countryBId"):
            cid = m.get(key)
            if cid not in country_ids:
                raise ValueError(f"match {m.get('id')}: unknown {key} '{cid}'")


def main() -> int:
    countries_path = ASSETS / "world_countries.json"
    groups_path = ASSETS / "worldcup_2026_groups.json"
    matches_path = ASSETS / "matches.json"

    for p in (countries_path, groups_path, matches_path):
        if not p.exists():
            print(f"ERROR: missing {p}", file=sys.stderr)
            return 1

    countries = load_json(countries_path)
    groups = load_json(groups_path)
    matches = load_json(matches_path)

    validate_countries(countries)
    country_ids = {c["id"] for c in countries}
    validate_groups(groups, country_ids)
    validate_matches(matches, country_ids)

    print(f"OK: {len(countries)} countries, {len(groups['groups'])} groups, {len(matches)} matches")
    return 0


if __name__ == "__main__":
    sys.exit(main())
