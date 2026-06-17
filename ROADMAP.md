# Roadmap — World Kids Explorer (kid-worldmap-app)

## V1 — MVP actuelle ✅

- App Android native Kotlin + Jetpack Compose
- Planisphère stylisé avec marqueurs et animations
- 16 pays + 8 matchs démo en local (`DemoData.kt`)
- Fiches pays ludiques pour enfants 8–10 ans
- Recherche pays
- Galerie images (Coil + fallback)
- Bouton Cast → réglages Android Smart View / Screen Cast
- Mode TV (textes agrandis, plein écran)
- APK debug installable sur Samsung S25

---

## V1.1 — Vrais matchs Coupe du monde

- Remplacer `demoMatches` par le calendrier réel
- Ajouter phases (groupes, 8es, quarts, etc.)
- Badges « match du jour » / « à venir »

## V1.2 — Meilleure carte du monde

- SVG monde simplifié intégré dans Compose
- Contours continents plus reconnaissables
- Zoom sur région lors de la sélection d'un pays

## V1.3 — Quiz enfant

- 3 questions courtes après chaque fiche pays
- Score local (SharedPreferences)
- Récompenses visuelles (étoiles, confettis)

## V1.4 — Mode « Devine le pays »

- Indice progressif (continent → capitale → fait)
- Chronomètre optionnel
- Mode multijoueur famille sur TV

## V1.5 — Google Cast SDK

- Receiver Chromecast dédié si nécessaire
- Contrôle à distance depuis la TV
- UI second écran optimisée

## V2 — Mini-jeux géographie

- Placer le pays sur la carte
- Relier capitale et drapeau
- Course contre la montre entre amis

---

## Idées bonus (non planifiées)

- Mode hors-ligne images (cache Coil)
- Audio : prononciation du nom du pays
- Thème sombre pour soirée TV
- Export / partage de la fiche pays
- Support tablette layout dédié
