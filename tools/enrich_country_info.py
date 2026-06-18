#!/usr/bin/env python3
"""Complète les fiches pays : langue principale + monnaie (en français) pour TOUS les pays.

Source : dataset statique mledoze/countries (langues, monnaies, traductions FR).
Met à jour app/src/main/assets/world_countries.json (champs mainLanguage / currency).

Régénérer : python tools/enrich_country_info.py
"""
import json
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "app" / "src" / "main" / "assets"
SRC_URL = "https://raw.githubusercontent.com/mledoze/countries/master/countries.json"

LANG_FR = {
    "English": "Anglais", "French": "Français", "Spanish": "Espagnol",
    "Portuguese": "Portugais", "German": "Allemand", "Italian": "Italien",
    "Dutch": "Néerlandais", "Russian": "Russe", "Arabic": "Arabe",
    "Mandarin Chinese": "Chinois (mandarin)", "Chinese": "Chinois",
    "Japanese": "Japonais", "Korean": "Coréen", "Hindi": "Hindi",
    "Bengali": "Bengali", "Urdu": "Ourdou", "Persian": "Persan",
    "Turkish": "Turc", "Greek": "Grec", "Polish": "Polonais",
    "Swedish": "Suédois", "Norwegian": "Norvégien", "Danish": "Danois",
    "Finnish": "Finnois", "Czech": "Tchèque", "Slovak": "Slovaque",
    "Hungarian": "Hongrois", "Romanian": "Roumain", "Bulgarian": "Bulgare",
    "Croatian": "Croate", "Serbian": "Serbe", "Ukrainian": "Ukrainien",
    "Swahili": "Swahili", "Amharic": "Amharique", "Hausa": "Haoussa",
    "Yoruba": "Yoruba", "Zulu": "Zoulou", "Afrikaans": "Afrikaans",
    "Hebrew": "Hébreu", "Thai": "Thaï", "Vietnamese": "Vietnamien",
    "Indonesian": "Indonésien", "Malay": "Malais", "Filipino": "Filipino",
    "Tagalog": "Tagalog", "Burmese": "Birman", "Khmer": "Khmer",
    "Lao": "Laotien", "Nepali": "Népalais", "Sinhala": "Cinghalais",
    "Tamil": "Tamoul", "Pashto": "Pachto", "Dari": "Dari",
    "Albanian": "Albanais", "Armenian": "Arménien", "Azerbaijani": "Azéri",
    "Georgian": "Géorgien", "Kazakh": "Kazakh", "Uzbek": "Ouzbek",
    "Mongolian": "Mongol", "Icelandic": "Islandais", "Irish": "Irlandais",
    "Estonian": "Estonien", "Latvian": "Letton", "Lithuanian": "Lituanien",
    "Slovene": "Slovène", "Macedonian": "Macédonien", "Belarusian": "Biélorusse",
    "Catalan": "Catalan", "Maltese": "Maltais", "Luxembourgish": "Luxembourgeois",
    "Somali": "Somali", "Malagasy": "Malgache", "Kinyarwanda": "Kinyarwanda",
    "Kirundi": "Kirundi", "Wolof": "Wolof", "Berber": "Berbère",
    "Quechua": "Quechua", "Guarani": "Guarani", "Aymara": "Aymara",
    "Dzongkha": "Dzongkha", "Tibetan": "Tibétain", "Maldivian": "Maldivien",
    "Tetum": "Tétoum", "Bislama": "Bichlamar", "Samoan": "Samoan",
    "Tongan": "Tongien", "Fijian": "Fidjien", "Hawaiian": "Hawaïen",
    "Haitian Creole": "Créole haïtien", "Papiamento": "Papiamento",
}

CURRENCY_FR = {
    "Euro": "Euro",
    "United States dollar": "Dollar américain",
    "Canadian dollar": "Dollar canadien",
    "Australian dollar": "Dollar australien",
    "New Zealand dollar": "Dollar néo-zélandais",
    "Pound sterling": "Livre sterling",
    "Swiss franc": "Franc suisse",
    "Japanese yen": "Yen",
    "Chinese yuan": "Yuan",
    "Renminbi": "Yuan",
    "Indian rupee": "Roupie indienne",
    "Russian ruble": "Rouble russe",
    "Brazilian real": "Réal brésilien",
    "Mexican peso": "Peso mexicain",
    "Argentine peso": "Peso argentin",
    "Colombian peso": "Peso colombien",
    "Chilean peso": "Peso chilien",
    "South Korean won": "Won sud-coréen",
    "Turkish lira": "Livre turque",
    "Egyptian pound": "Livre égyptienne",
    "Moroccan dirham": "Dirham marocain",
    "Saudi riyal": "Riyal saoudien",
    "Qatari riyal": "Riyal qatari",
    "United Arab Emirates dirham": "Dirham des Émirats",
    "Norwegian krone": "Couronne norvégienne",
    "Swedish krona": "Couronne suédoise",
    "Danish krone": "Couronne danoise",
    "Polish złoty": "Złoty",
    "Czech koruna": "Couronne tchèque",
    "Hungarian forint": "Forint",
    "South African rand": "Rand",
    "Nigerian naira": "Naira",
    "West African CFA franc": "Franc CFA (Ouest)",
    "Central African CFA franc": "Franc CFA (Centre)",
    "Kenyan shilling": "Shilling kenyan",
    "Ghanaian cedi": "Cedi",
    "Algerian dinar": "Dinar algérien",
    "Tunisian dinar": "Dinar tunisien",
    "Jordanian dinar": "Dinar jordanien",
    "Iranian rial": "Rial iranien",
    "Uzbekistani soʻm": "Soum ouzbek",
    "Paraguayan guaraní": "Guarani",
    "Uruguayan peso": "Peso uruguayen",
    "Ecuadorian sucre": "Dollar américain",
    "Cape Verdean escudo": "Escudo cap-verdien",
    "Netherlands Antillean guilder": "Florin antillais",
    "Croatian kuna": "Euro",
    "Indonesian rupiah": "Roupie indonésienne",
    "Thai baht": "Baht",
    "Vietnamese đồng": "Dông",
    "Philippine peso": "Peso philippin",
    "Malaysian ringgit": "Ringgit",
    "Singapore dollar": "Dollar de Singapour",
    "Pakistani rupee": "Roupie pakistanaise",
    "Bangladeshi taka": "Taka",
    "British pound": "Livre sterling",
    "Hong Kong dollar": "Dollar de Hong Kong",
    "Eastern Caribbean dollar": "Dollar des Caraïbes",
    "New Taiwan dollar": "Dollar taïwanais",
    "Serbian dinar": "Dinar serbe",
    "Sri Lankan rupee": "Roupie srilankaise",
    "Nepalese rupee": "Roupie népalaise",
    "Israeli new shekel": "Shekel",
    "Iraqi dinar": "Dinar irakien",
    "Kuwaiti dinar": "Dinar koweïtien",
    "Libyan dinar": "Dinar libyen",
    "Lebanese pound": "Livre libanaise",
    "Syrian pound": "Livre syrienne",
    "Sudanese pound": "Livre soudanaise",
    "Kenyan shilling": "Shilling kenyan",
    "Tanzanian shilling": "Shilling tanzanien",
    "Ugandan shilling": "Shilling ougandais",
    "Ethiopian birr": "Birr",
    "Bahraini dinar": "Dinar bahreïni",
}

# Traduction générique du type de monnaie (repli si le nom complet n'est pas listé)
CURRENCY_WORD_FR = {
    "pound": "Livre", "rupee": "Roupie", "dinar": "Dinar", "franc": "Franc",
    "shilling": "Shilling", "dollar": "Dollar", "peso": "Peso", "rial": "Rial",
    "riyal": "Riyal", "krone": "Couronne", "krona": "Couronne", "ruble": "Rouble",
    "rupiah": "Roupie",
}


def french_currency(name: str) -> str:
    if not name:
        return name
    if name in CURRENCY_FR:
        return CURRENCY_FR[name]
    low = name.lower()
    for word, fr in CURRENCY_WORD_FR.items():
        if low.endswith(word) and " " in name:
            return f"{fr} ({name.rsplit(' ', 1)[0]})"
    return name


def iso2_from_flag(flag: str):
    ri = [ord(ch) for ch in flag if 0x1F1E6 <= ord(ch) <= 0x1F1FF]
    if len(ri) != 2:
        return None
    return "".join(chr(cp - 0x1F1E6 + ord("A")) for cp in ri)


def main():
    print("Telechargement mledoze/countries...")
    req = urllib.request.Request(SRC_URL, headers={"User-Agent": "kid-worldmap-app/enrich"})
    src = json.loads(urllib.request.urlopen(req, timeout=120).read().decode())

    by_iso2 = {}
    for c in src:
        iso2 = (c.get("cca2") or "").upper()
        langs = list((c.get("languages") or {}).values())
        currs = [v.get("name") for v in (c.get("currencies") or {}).values()]
        by_iso2[iso2] = {
            "lang": langs[0] if langs else None,
            "curr": currs[0] if currs else None,
        }

    countries = json.loads((ASSETS / "world_countries.json").read_text(encoding="utf-8"))

    # cas particuliers (drapeaux de subdivision sans ISO2)
    manual = {
        "scotland": ("Anglais", "Livre sterling"),
        "wales": ("Anglais", "Livre sterling"),
    }

    updated = 0
    for c in countries:
        cid = c["id"]
        lang_fr = curr_fr = None
        if cid in manual:
            lang_fr, curr_fr = manual[cid]
        else:
            iso2 = iso2_from_flag(c.get("flagEmoji", ""))
            info = by_iso2.get(iso2) if iso2 else None
            if info:
                if info["lang"]:
                    lang_fr = LANG_FR.get(info["lang"], info["lang"])
                if info["curr"]:
                    curr_fr = french_currency(info["curr"])
        changed = False
        if lang_fr:
            c["mainLanguage"] = lang_fr
            changed = True
        if curr_fr:
            c["currency"] = curr_fr
            changed = True
        if changed:
            updated += 1

    (ASSETS / "world_countries.json").write_text(
        json.dumps(countries, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    missing = [c["id"] for c in countries
               if c.get("mainLanguage") in (None, "", "—") and not c["id"].startswith("playoff")]
    print(f"OK : {updated} pays enrichis / {len(countries)}")
    if missing:
        print(f"[INFO] {len(missing)} sans langue (ex.):", missing[:20])


if __name__ == "__main__":
    main()
