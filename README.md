# World Kids Explorer

Application Android native (**kid-worldmap-app**) pour explorer un planisphère coloré avec des enfants de 9 ans environ, avec support Cast / Smart View vers une TV LG.

## 1. Objectif de l'app

World Kids Explorer permet de :
- visualiser un planisphère ludique ;
- choisir un match de Coupe du monde (données démo) et mettre en avant les deux pays ;
- chercher un pays et afficher une fiche éducative adaptée aux enfants ;
- caster l'écran du téléphone vers une TV via les réglages Android (Smart View / Screen Cast).

Toutes les données sont **locales** : pas de backend, pas de base de données externe.

## 2. Fonctionnalités

- Planisphère stylisé avec marqueurs animés (pulse, zoom auto, labels, ligne entre deux pays)
- **~250 pays et territoires** dans `world_countries.json` (recherche avec autocomplétion)
- **48 équipes CDM 2026** réparties en **12 poules** (onglet dédié)
- Matchs **Coupe du monde 2026** dans `matches.json`
- Fiches enrichies pour les grands pays (`country_details.json`) + fiches auto pour les autres
- Bouton Cast (ouvre les réglages Android)
- Mode TV (paysage, textes plus grands, plein écran)
- Images externes via Coil avec fallback coloré

## 3. Prérequis

- **Android Studio** (version récente, ex. Ladybug ou plus)
- **JDK 17** (inclus avec Android Studio)
- **Téléphone Samsung S25** (ou autre Android 8.0+)
- **Câble USB** pour le debug
- **Mode développeur** activé sur le téléphone
- **Debug USB** autorisé

## 4. Lancer dans Android Studio

1. Ouvrir Android Studio
2. **File → Open** → sélectionner le dossier `kid-worldmap-app`
3. Attendre la synchronisation Gradle (première fois : quelques minutes)
4. Connecter le Samsung S25 en USB
5. Sur le téléphone : autoriser le débogage USB
6. Sélectionner l'appareil en haut, cliquer **Run** (▶)

## 5. Générer un APK debug

Dans le terminal, à la racine du projet `kid-worldmap-app` :

```bash
.\gradlew.bat assembleDebug
```

> **Première ouverture** : si `gradlew.bat` n'existe pas encore ou échoue, ouvre le projet dans Android Studio — il générera automatiquement le Gradle Wrapper. Tu peux aussi lancer **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

APK généré :

```
app\build\outputs\apk\debug\app-debug.apk
```

## 6. Installer sur le Samsung S25

### Via Android Studio
Cliquer **Run** avec le téléphone connecté.

### Via adb

```bash
adb install app\build\outputs\apk\debug\app-debug.apk
```

Si l'app existe déjà :

```bash
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 7. Utiliser le Cast avec une TV LG

1. Ouvrir **World Kids Explorer** sur le téléphone
2. Appuyer sur l'icône **Cast** (en haut à droite)
3. Android ouvre les réglages Cast / Connexions
4. Choisir **Smart View** ou **Screen Cast**
5. Sélectionner la **TV LG**
6. Revenir dans l'app
7. Activer le **Mode TV** et poser le téléphone à l'horizontale

> Le MVP n'intègre pas le Google Cast SDK complet : c'est du **miroir d'écran** via les réglages système Samsung/Android.

## 8. Modifier les pays

- **Tous les pays** : `app/src/main/assets/world_countries.json` (régénérer avec `python tools/generate_world_data.py`)
- **Fiche enrichie** (faits enfants, images) : `app/src/main/assets/country_details.json`
- **Modèle** : `docs/country_template.json`

## 9. Modifier les matchs et poules CDM 2026

- **Matchs** : `app/src/main/assets/matches.json`
- **Poules** : `app/src/main/assets/worldcup_2026_groups.json` (tirage officiel déc. 2025 + barrages en placeholders)
- Script de régénération : `python tools/generate_world_data.py`

## 10. Limites connues

- Pas de Google Cast SDK natif dans ce MVP
- Les images sont chargées depuis Internet (URLs Wikimedia) — nécessite une connexion
- Les matchs CDM 2026 sont partiels (calendrier complet à venir sur FIFA.com)
- 6 places de barrage encore en « placeholder » jusqu'en mars 2026
- Le planisphère est stylisé, pas une carte géographique précise

## 11. Prochaines améliorations

Voir [ROADMAP.md](ROADMAP.md).

---

## Refonte UI (v1.2)

### Changements visuels appliqués

- **Thème Material 3** entièrement refondu : fond crème chaud, bleu marine profond, teal doux, accents jaune/orange
- **Top Bar compacte** : titre + sous-titre élégants, bouton Cast compact, toggle TV discret, respect des safe areas Android
- **Navigation par segmented control** : Explorer / Coupe du monde — plus clair que des onglets
- **Recherche pays** : carte blanche arrondie, chips de pays populaires (France, Brésil, Japon…), suggestions élégantes
- **Carrousel de matchs** : cartes compactes avec drapeaux, VS en couleur, noms courts
- **Fiche pays** : drapeau grand, infos structurées, section "À retenir" fond jaune, 3 faits, galerie
- **Fiche match** : deux cartes pays cliquables, invitation à en apprendre plus
- **Mode TV** : échelle de typographie +28%, bandeau discret en haut

### Comment tester sur Samsung S25

```bash
# Brancher le S25 en USB, mode débogage activé
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Checklist visuelle

- [ ] Le titre ne chevauche pas la barre d'état Android
- [ ] La carte est visible sans écraser tout l'écran
- [ ] La recherche est compacte (pas un grand champ)
- [ ] Les pays populaires s'affichent en chips
- [ ] Un match sélectionné met bien deux pays en avant
- [ ] La fiche pays s'affiche avec drapeau grand et faits
- [ ] Le Mode TV grossit l'interface sans la casser
- [ ] Le bouton Cast ouvre les réglages Android (icône écran)
- [ ] L'écran reste agréable en portrait
- [ ] L'écran reste lisible en paysage / cast TV

---

## Fiche pays enrichie (v1.3)

### Nouvelles fonctionnalités

- **Silhouette du pays** dans l'en-tête de la fiche, à droite du nom (`CountryShapeBadge`).
  Le tracé utilise les vraies frontières (`country_borders.json`). Si une forme manque,
  un *fallback* propre affiche les initiales du pays dans un bloc arrondi.
- **Symbole de monnaie** affiché à côté du nom (ex. « Euro € », « Yen ¥ »).
  Aucun symbole moche n'est forcé : si le rendu n'est pas propre, seul le nom s'affiche.
- **Étoiles Coupe du monde** (façon maillot, 1 titre = 1 étoile) via `WorldCupStars`,
  à la place du texte « X fois championne ».
- **Label « CDM »** sur mobile (au lieu de « Coupe du monde » qui passait sur deux lignes).
  Le libellé long reste sur les écrans larges / Mode TV.
- **Section « À retenir »** repensée : 3 phrases courtes, mémorables et adaptées aux
  enfants (l'ancienne section « cerveau » + mots-clés a été supprimée).
- **Galerie cliquable** : miniatures emblématiques (`CountryImage`) ouvrant une
  visionneuse plein écran (`FullscreenImageViewer`) avec fond sombre, légende, bouton
  fermer et fermeture par le bouton retour Android. Fallback coloré si l'image ne charge pas.

### Où modifier les données

Tout le contenu éditorial se trouve dans `app/src/main/java/com/example/worldkids/data/CountryContent.kt` :

| Quoi | Où | Détail |
|------|-----|--------|
| **Symboles de monnaie** | `CountryContent.currencySymbol()` | table de mots-clés Unicode (€, £, ¥, ₩, R$, $…). Mets `null` pour ne rien afficher. |
| **Nombre d'étoiles CDM** | `CountryContent.WORLD_CUP_TITLES` | `"brazil" to 5`, `"france" to 2`… (surchargeable par `Country.worldCupTitles`). |
| **Silhouettes des pays** | `country_borders.json` | vraies frontières. Surcharge future possible via `Country.shapePathData`. Fallback initiales automatique. |
| **Phrases « À retenir »** | `CountryContent.MEMORABLE_FACTS` ou `country_details.json` (clé `memorableFacts`) | 3 phrases max, kid-friendly. |
| **Images de galerie** | `CountryContent.GALLERIES` | `CountryImage(title, imageUrl, caption, sourceLabel, sourceUrl)` — URLs Wikimedia Commons, faciles à remplacer. |

> Astuce galerie : la fonction `wiki(title, caption, path)` construit l'URL de vignette
> 320 px à partir d'un chemin Wikimedia Commons (ex. `a/a8/Tour_Eiffel_Wikimedia_Commons.jpg`).

### Checklist de test (v1.3)

- [ ] La fiche pays affiche **drapeau + nom + silhouette**
- [ ] La France affiche bien **€**
- [ ] Le Japon affiche bien **¥**
- [ ] Les pays sans symbole propre n'affichent rien de bizarre (juste le nom)
- [ ] Le label mobile affiche **« CDM »** et ne passe pas sur deux lignes
- [ ] La France affiche **2 étoiles**
- [ ] Le Brésil affiche **5 étoiles**
- [ ] La section cerveau / mots-clés a disparu
- [ ] La nouvelle section « À retenir » affiche 3 phrases utiles
- [ ] Les miniatures de galerie s'affichent
- [ ] Un clic sur une image ouvre le plein écran
- [ ] Le bouton retour Android ferme le plein écran
- [ ] Aucun crash si une image ne charge pas
- [ ] Le Mode TV reste cohérent (silhouette + étoiles lisibles)
- [ ] Le rendu téléphone portrait reste impeccable

## Checklist de tests manuels

- [ ] L'app démarre sans crash
- [ ] Le planisphère s'affiche
- [ ] Je peux choisir un match
- [ ] Les 2 pays du match sont mis en avant sur la carte
- [ ] L'onglet **Coupe du monde** affiche les poules et matchs
- [ ] Je peux toucher une poule pour mettre 4 pays en avant
- [ ] La recherche avec autocomplétion trouve n'importe quel pays (ex. Islande)
- [ ] Les chips populaires affichent France, Brésil, Japon…
- [ ] La fiche France s'affiche avec faits et images
- [ ] Les images en erreur affichent un placeholder (pas de crash)
- [ ] Le bouton Cast ouvre les réglages Android
- [ ] Le Mode TV agrandit l'interface
- [ ] L'APK s'installe sur le S25

## Structure du projet

```
kid-worldmap-app/
  app/src/main/java/com/example/worldkids/
    MainActivity.kt
    data/          → Country, Match, DemoData
    ui/            → écrans et composants
    theme/         → couleurs et typographie
    utils/         → CastUtils
```

**Nom du dépôt** : `kid-worldmap-app`  
**Nom affiché de l'app** : World Kids Explorer
