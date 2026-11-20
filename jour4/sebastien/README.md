# ConfigSync · projet de Sébastien

Une appli qui appelle une **API REST protégée par une paire de clés** (publique / privée), récupère des paires
clé/valeur et les écrit dans un fichier **`config.ini`**, au choix dans le téléphone ou sur la **carte SD**.

## Lancer

Le cahier de l'atelier : [`Kotlin-J4-Sebastien-ConfigSync.pdf`](Kotlin-J4-Sebastien-ConfigSync.pdf).

1. Android Studio : **File → Open** → le dossier **`jour4/sebastien/ConfigSync`** (pas la racine du dépôt).
2. Le faux serveur (pour tester sans le vrai) : dans un terminal, `python3 jour4/sebastien/ConfigSync/server/mock_server.py`.
3. **Run ▶** sur l'émulateur, puis **Synchroniser**.

## Brancher le vrai serveur

Copier `ConfigSync/local.properties.example` dans `ConfigSync/local.properties` (jamais envoyé sur git) et remplir
l'adresse et les deux clés. Vérifier dans la doc de l'API comment la signature est construite (`RequestSigner.kt`).
