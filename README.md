# Kid Worldmap App — Backend API

Backend FastAPI pour **kid-worldmap-app** : PostgreSQL (Supabase), Docker et Google Cloud Run.

> Projet dérivé du template [template-project](https://github.com/TehBakker/template-project).

---

## Présentation du projet

Ce dépôt contient l'API backend de **kid-worldmap-app**. Il repose sur le template FastAPI réutilisable et fournit :

- Une API FastAPI moderne avec typage Python
- Une connexion PostgreSQL via SQLAlchemy 2 et psycopg
- La création automatique des tables au démarrage (sans Alembic)
- Un endpoint CRUD minimal (`/notes`) pour valider la stack
- Des scripts shell pour le développement, les tests et le déploiement
- Un Dockerfile optimisé pour Google Cloud Run
- Une documentation pas-à-pas pour Supabase et Google Cloud

**Stack :** Python 3.12 · FastAPI · PostgreSQL · SQLAlchemy 2 · psycopg · Supabase · Docker · Cloud Run · dotenv

**Coût visé :** 0 € pour un usage personnel léger (quotas gratuits Supabase + Google Cloud).

---

## Architecture

```
project-root/
│
├── app/
│   ├── main.py          # Point d'entrée FastAPI + lifespan (init DB)
│   ├── settings.py      # Variables d'environnement
│   ├── db.py            # Engine SQLAlchemy, sessions, init tables
│   ├── models.py        # Modèle Note
│   ├── schemas.py       # Schémas Pydantic
│   └── routers/
│       ├── health.py    # /, /health, /db-check
│       └── notes.py     # POST/GET /notes
│
├── scripts/
│   ├── dev.sh           # Lancement local avec reload
│   ├── check_env.sh     # Vérification des variables
│   ├── test_api.sh      # Smoke tests curl
│   ├── init_db.sh       # Test connexion PostgreSQL
│   ├── setup_gcloud.sh  # Configuration GCP
│   └── deploy_cloudrun.sh
│
├── tests/               # Tests pytest
├── Dockerfile           # Image Cloud Run
└── requirements.txt
```

### Flux de démarrage

1. L'application charge `.env` via `python-dotenv`
2. Au démarrage (`lifespan`), `init_db()` crée la table `notes` si elle n'existe pas
3. Les routes utilisent des sessions SQLAlchemy injectées via `Depends(get_db)`

### Endpoints

| Méthode | Route       | Description                    |
|---------|-------------|--------------------------------|
| GET     | `/health`   | Santé de l'API                 |
| GET     | `/`         | Message de bienvenue           |
| GET     | `/db-check` | Test connexion PostgreSQL      |
| POST    | `/notes`    | Créer une note                 |
| GET     | `/notes`    | Lister toutes les notes        |

---

## Installation locale

### Prérequis

- Python 3.12
- Git
- Un terminal Bash (Git Bash, WSL ou Linux/macOS) pour les scripts `.sh`

### 1. Cloner le projet

```bash
git clone <votre-repo> mon-projet
cd mon-projet
```

### 2. Créer un environnement virtuel Python

**Linux / macOS / Git Bash :**

```bash
python3.12 -m venv .venv
source .venv/bin/activate
```

**Windows PowerShell :**

```powershell
py -3.12 -m venv .venv
.\.venv\Scripts\Activate.ps1
```

### 3. Installer les dépendances

```bash
pip install --upgrade pip
pip install -r requirements.txt
```

### 4. Copier et configurer `.env`

```bash
cp .env.example .env
```

Éditez `.env` et renseignez au minimum `DATABASE_URL` (voir section Supabase ci-dessous).

### 5. Vérifier l'environnement

```bash
chmod +x scripts/*.sh
./scripts/check_env.sh
```

### 6. Tester la connexion PostgreSQL

```bash
./scripts/init_db.sh
```

### 7. Lancer l'API en local

```bash
./scripts/dev.sh
```

L'API est disponible sur `http://127.0.0.1:8080`.

Documentation interactive : `http://127.0.0.1:8080/docs`

### 8. Tests locaux

**Tests automatisés (pytest) :**

```bash
pytest -v
```

**Smoke tests API (curl) — serveur démarré :**

```bash
./scripts/test_api.sh
```

---

## Création du compte Supabase

Supabase héberge une base **PostgreSQL managée** avec un tier gratuit adapté aux projets personnels.

### Étape 1 — Créer un compte

1. Allez sur [https://supabase.com](https://supabase.com)
2. Cliquez sur **Start your project**
3. Connectez-vous avec GitHub (recommandé) ou email

### Étape 2 — Créer une organisation

1. Après la première connexion, Supabase vous demande de créer une **Organization**
2. Donnez-lui un nom (ex. `personal-projects`)
3. Choisissez le plan **Free**

### Étape 3 — Créer un projet

1. Cliquez sur **New project**
2. Sélectionnez votre organisation
3. Renseignez :
   - **Name** : ex. `fastapi-backend`
   - **Database Password** : choisissez un mot de passe fort et **notez-le**
   - **Region** : choisissez la région la plus proche (ex. `West EU (Paris)`)
4. Cliquez sur **Create new project**
5. Attendez 1 à 2 minutes que l'infrastructure s'initialise

### Étape 4 — Récupérer `DATABASE_URL`

1. Dans le dashboard Supabase, ouvrez votre projet
2. Cliquez sur **Project Settings** (icône engrenage en bas à gauche)
3. Allez dans **Database**
4. Descendez jusqu'à la section **Connection string**
5. Choisissez l'onglet **URI**
6. Copiez la chaîne de connexion **Session mode** ou **Direct connection**

**Format attendu (exemple anonymisé) :**

```
postgresql://postgres.xxxxxxxxxxxxx:YOUR_PASSWORD@aws-0-eu-west-3.pooler.supabase.com:6543/postgres
```

### Étape 5 — Adapter l'URL pour ce template

Convertissez l'URL pour utiliser le driver **psycopg** (SQLAlchemy 2) :

```
postgresql+psycopg://postgres.xxxxxxxxxxxxx:YOUR_PASSWORD@aws-0-eu-west-3.pooler.supabase.com:6543/postgres?sslmode=require
```

**Modifications importantes :**

| Élément | Valeur |
|---------|--------|
| Schéma  | `postgresql+psycopg://` (pas `postgresql://` seul) |
| Mot de passe | Remplacez `[YOUR-PASSWORD]` par votre mot de passe réel |
| SSL | Ajoutez `?sslmode=require` à la fin |

### Pourquoi `sslmode=require` ?

Supabase impose une connexion chiffrée (SSL/TLS). Sans `sslmode=require`, la connexion échoue souvent avec une erreur SSL ou de certificat. Ce template l'ajoute automatiquement si absent, mais il est préférable de l'inclure explicitement dans `.env`.

Collez la valeur finale dans votre fichier `.env` :

```env
DATABASE_URL=postgresql+psycopg://postgres.xxxxx:MonMotDePasse@db.xxxxx.supabase.co:5432/postgres?sslmode=require
```

---

## Création du compte Google Cloud

### Étape 1 — Créer un compte Google Cloud

1. Allez sur [https://cloud.google.com](https://cloud.google.com)
2. Cliquez sur **Get started for free**
3. Connectez-vous avec un compte Google
4. Acceptez les conditions

### Étape 2 — Créer un projet

1. Ouvrez la [console Google Cloud](https://console.cloud.google.com)
2. En haut, cliquez sur le sélecteur de projet → **New project**
3. Donnez un nom (ex. `fastapi-personal`)
4. Notez le **Project ID** (ex. `fastapi-personal-123456`)

### Étape 3 — Activer la facturation

Cloud Run nécessite un compte de facturation, même pour rester dans le quota gratuit.

1. Menu **Billing** → **Link a billing account**
2. Ajoutez une carte bancaire (Google offre des crédits gratuits aux nouveaux comptes)
3. Surveillez votre consommation dans la console

> **Coût personnel typique :** avec peu de trafic, Cloud Run reste souvent à 0 € grâce au quota gratuit (2 millions de requêtes/mois, 360 000 Go-secondes de mémoire).

### Étape 4 — Installer gcloud CLI

Suivez le guide officiel : [Install the gcloud CLI](https://cloud.google.com/sdk/docs/install)

Vérifiez l'installation :

```bash
gcloud --version
```

### Étape 5 — Authentification et projet actif

```bash
gcloud auth login
gcloud config set project PROJECT_ID
```

Remplacez `PROJECT_ID` par votre identifiant de projet Google Cloud.

Ajoutez aussi ces variables dans `.env` :

```env
PROJECT_ID=fastapi-personal-123456
REGION=europe-west1
SERVICE_NAME=fastapi-backend
```

---

## Déploiement Cloud Run

### Qu'est-ce que Cloud Run ?

**Cloud Run** est un service serverless de Google qui exécute des conteneurs Docker. Vous poussez une image, Google gère l'infrastructure, le scaling et HTTPS.

**Pourquoi c'est adapté aux projets personnels :**

- Pas de serveur à administrer
- Scale à zéro (pas de coût quand personne n'appelle l'API)
- HTTPS automatique
- Déploiement en une commande
- Tier gratuit généreux

### Quota gratuit (ordre de grandeur)

- 2 millions de requêtes par mois
- 360 000 Go-secondes de mémoire
- 180 000 vCPU-secondes

Consultez la [page tarifaire Cloud Run](https://cloud.google.com/run/pricing) pour les détails à jour.

### Déployer

**1. Rendre les scripts exécutables :**

```bash
chmod +x scripts/*.sh
```

**2. Configurer Google Cloud (APIs, projet) :**

```bash
./scripts/setup_gcloud.sh
```

Ce script :

- Vérifie que `gcloud` est installé
- Affiche la version
- Vérifie l'authentification
- Demande ou lit `PROJECT_ID`
- Active les APIs :
  - `run.googleapis.com`
  - `cloudbuild.googleapis.com`
  - `artifactregistry.googleapis.com`

**3. Déployer sur Cloud Run :**

```bash
./scripts/deploy_cloudrun.sh
```

Ce script :

- Vérifie Docker, gcloud et `DATABASE_URL`
- Construit l'image Docker
- Pousse l'image vers Artifact Registry
- Déploie avec `gcloud run deploy`
- Injecte `DATABASE_URL` et `ENVIRONMENT=production`
- Affiche l'URL du service
- Teste automatiquement `/health`

---

## Variables d'environnement

| Variable       | Obligatoire | Défaut   | Description |
|----------------|-------------|----------|-------------|
| `DATABASE_URL` | Oui (prod)  | —        | URL PostgreSQL Supabase avec driver `postgresql+psycopg://` et `sslmode=require` |
| `PORT`         | Non         | `8080`   | Port d'écoute (Cloud Run injecte `8080`) |
| `ENVIRONMENT`  | Non         | `local`  | `local`, `test` ou `production` |
| `PROJECT_ID`   | Déploiement | —        | ID du projet Google Cloud |
| `REGION`       | Déploiement | `europe-west1` | Région Cloud Run |
| `SERVICE_NAME` | Déploiement | `fastapi-backend` | Nom du service Cloud Run |

---

## Exemples curl

Remplacez `BASE_URL` par `http://127.0.0.1:8080` en local ou l'URL Cloud Run en production.

### Santé

```bash
curl -s BASE_URL/health
```

Réponse :

```json
{"status":"ok"}
```

### Vérification base de données

```bash
curl -s BASE_URL/db-check
```

Réponse (succès) :

```json
{"database":"connected","detail":null}
```

### Créer une note

```bash
curl -s -X POST BASE_URL/notes \
  -H "Content-Type: application/json" \
  -d '{"title":"Ma note","content":"Contenu"}'
```

### Lister les notes

```bash
curl -s BASE_URL/notes
```

---

## Sécurité

- **Ne jamais commiter `.env`** — il est dans `.gitignore`
- **Ne jamais commiter `DATABASE_URL`** dans le code ou les issues GitHub
- **Secret Manager** : pour la production avancée, stockez `DATABASE_URL` dans [Google Secret Manager](https://cloud.google.com/secret-manager) au lieu de variables d'environnement en clair
- **Authentification** : ce template expose une API publique. Ajoutez JWT, API keys ou OAuth avant toute exposition à un public non contrôlé
- **Supabase** : limitez les accès réseau si possible, utilisez des mots de passe forts, activez la MFA sur votre compte
- **Cloud Run** : `--allow-unauthenticated` est pratique pour tester ; retirez-le et ajoutez IAM/IAP pour un usage réel

---

## Troubleshooting

### `DATABASE_URL` manquant

**Symptôme :** `./scripts/check_env.sh` échoue, `/db-check` retourne `disconnected`.

**Solution :** Copiez `.env.example` vers `.env` et renseignez `DATABASE_URL`.

---

### Erreur PostgreSQL / connexion refusée

**Symptôme :** `connection refused`, timeout, ou `could not connect to server`.

**Causes possibles :**

- Mauvaise URL (host, port, projet Supabase)
- Projet Supabase en pause (tier gratuit inactif)
- Pare-feu local

**Solution :** Vérifiez l'URL dans Supabase → Project Settings → Database. Testez avec `./scripts/init_db.sh`.

---

### Erreur SSL Supabase

**Symptôme :** `SSL connection required`, `certificate verify failed`.

**Solution :** Ajoutez `?sslmode=require` à `DATABASE_URL`. Utilisez le format `postgresql+psycopg://`.

---

### Mauvais mot de passe PostgreSQL

**Symptôme :** `password authentication failed for user "postgres"`.

**Solution :** Réinitialisez le mot de passe dans Supabase → Project Settings → Database → **Reset database password**. Mettez à jour `.env`.

---

### Cloud Run — port incorrect

**Symptôme :** Le conteneur démarre mais Cloud Run renvoie 502/503.

**Solution :** L'application doit écouter sur `0.0.0.0` et le port défini par la variable `PORT` (8080). Le Dockerfile et `uvicorn` sont déjà configurés ainsi.

---

### API Google non activée

**Symptôme :** `API run.googleapis.com has not been used in project...`

**Solution :** Relancez `./scripts/setup_gcloud.sh` ou activez manuellement :

```bash
gcloud services enable run.googleapis.com cloudbuild.googleapis.com artifactregistry.googleapis.com
```

---

### gcloud non connecté

**Symptôme :** `You do not currently have an active account selected`.

**Solution :**

```bash
gcloud auth login
gcloud auth application-default login
```

---

### Facturation Google absente

**Symptôme :** `Billing account for project is not found`.

**Solution :** Liez un compte de facturation dans la console Google Cloud (Billing).

---

### Déploiement échoué

**Symptôme :** `docker push` ou `gcloud run deploy` échoue.

**Vérifications :**

1. Docker Desktop est démarré
2. `gcloud auth configure-docker REGION-docker.pkg.dev` a été exécuté
3. `PROJECT_ID`, `REGION`, `DATABASE_URL` sont définis
4. Consultez les logs : `gcloud run services logs read SERVICE_NAME --region REGION`

---

## Commandes de zéro à API déployée

Voici l'ordre exact des commandes pour un nouveau projet :

```bash
# 1. Cloner et entrer dans le projet
git clone <repo> mon-projet && cd mon-projet

# 2. Environnement Python
python3.12 -m venv .venv
source .venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt

# 3. Configuration locale
cp .env.example .env
# Éditer .env : DATABASE_URL (Supabase), PROJECT_ID, REGION, SERVICE_NAME

# 4. Vérifications
chmod +x scripts/*.sh
./scripts/check_env.sh
./scripts/init_db.sh

# 5. Tests
pytest -v
./scripts/dev.sh
# Dans un autre terminal :
./scripts/test_api.sh

# 6. Google Cloud
gcloud auth login
gcloud config set project PROJECT_ID
./scripts/setup_gcloud.sh

# 7. Déploiement
./scripts/deploy_cloudrun.sh
```

---

## Licence

MIT — voir [LICENSE](LICENSE).
