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

- Planisphère stylisé avec marqueurs animés (pulse, zoom, ligne entre deux pays)
- 16 pays avec fiches ludiques (faits, drapeau, capitale, galerie d'images)
- 8 matchs démo personnalisables dans `DemoData.kt`
- Recherche pays instantanée
- Bouton Cast (ouvre les réglages Android)
- Mode TV (textes plus grands, interface aérée, plein écran)
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

Fichier : `app/src/main/java/com/example/worldkids/data/DemoData.kt`

Modifier ou ajouter des entrées dans la liste `countries` (nom, faits, coordonnées `mapX`/`mapY`, URLs images, etc.).

## 9. Modifier les matchs

Même fichier : `DemoData.kt`, liste `demoMatches`.

Un commentaire dans le code indique où remplacer les matchs démo par les vrais matchs de Coupe du monde.

## 10. Limites connues

- Pas de Google Cast SDK natif dans ce MVP
- Les images sont chargées depuis Internet (URLs Wikimedia) — nécessite une connexion
- Les matchs sont des **données démo**, pas officielles
- Le planisphère est stylisé, pas une carte géographique précise

## 11. Prochaines améliorations

Voir [ROADMAP.md](ROADMAP.md).

---

## Checklist de tests manuels

- [ ] L'app démarre sans crash
- [ ] Le planisphère s'affiche
- [ ] Je peux choisir un match
- [ ] Les 2 pays du match sont mis en avant sur la carte
- [ ] Je peux chercher « France »
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
