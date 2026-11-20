# Formation Kotlin & Android

Tout le code et les cahiers de la formation, rangés **jour par jour**. Une seule branche : `main`.

```
jour1/   Kotlin : le projet Popote et les exercices (kata)       + cahier du jour 1
jour2/   Trois applis : Convertisseur, Mes Courses, Recettes      + cahier du jour 2
jour3/   MusicPlayer : le point de départ et chaque étape         + cahier du jour 3
jour4/   Un dossier par participant (projet + cahier de l'atelier) + mise en production
```

## Récupérer le code

- **Sans Git** : bouton vert **Code → Download ZIP**, puis dézipper.
- **Avec Git** : `git clone https://github.com/ndjerrou/popote-formation.git` (puis `git pull` pour les mises à jour).

**Ouvrir un projet dans Android Studio :** **File → Open**, puis **le dossier du projet** (celui qui contient
`settings.gradle.kts`, par exemple `jour3/MusicPlayer`), **jamais la racine du dépôt**. Attendez la
synchronisation Gradle, puis **Run ▶**.

## Jour 1 · Kotlin

| Quoi | Où |
|---|---|
| Cahier | [`jour1/Popote-Eleve-J1.pdf`](jour1/Popote-Eleve-J1.pdf) |
| Exercices Kotlin et leurs tests | `jour1/Popote/kata/` (`./gradlew :kata:test` depuis `jour1/Popote`) |
| Popote, une appli complète à explorer (Room, Retrofit, Koin, WorkManager, tests, signature) | `jour1/Popote/app/` |

Projet à ouvrir : **`jour1/Popote`**.

## Jour 2 · Trois applis

| Quoi | Où |
|---|---|
| Cahier | [`jour2/Kotlin-J2-Eleve-Trois-applis.pdf`](jour2/Kotlin-J2-Eleve-Trois-applis.pdf) |
| Appli 1 · Convertisseur | `jour2/1-Convertisseur` |
| Appli 2 · Mes Courses | `jour2/2-MesCourses` |
| Appli 3 · Recettes du monde | `jour2/3-Recettes` |

## Jour 3 · MusicPlayer

| Quoi | Où |
|---|---|
| Cahier | [`jour3/Kotlin-J3-Eleve-MusicPlayer.pdf`](jour3/Kotlin-J3-Eleve-MusicPlayer.pdf) |
| Le point de départ | `jour3/MusicPlayer` |
| L'appli **à la fin** de chaque étape | `jour3/etapes/…` (tableau ci-dessous) |

| Dossier | Étape |
|---|---|
| `jour3/etapes/1-gradle` | 1 · Les dépendances |
| `jour3/etapes/2-contrat` | 2 · `PlayerUiState` et `PlayerAction` |
| `jour3/etapes/3-viewmodel` | 3 · `PlayerViewModel` + tests |
| `jour3/etapes/4-route-screen` | 4 · `PlayerRoute` et `PlayerScreen` |
| `jour3/etapes/5-process-death` | 5 · `SavedStateHandle` |
| `jour3/etapes/6-navigation` | 6 · Plusieurs écrans |
| `jour3/etapes/7-son` | 7 · Le vrai son (fin du jour 3) |

Pour comparer avec votre code : ouvrez le fichier de l'étape directement sur GitHub. Pour repartir d'une étape :
**File → Open** sur son dossier. Les morceaux de MusicPlayer sont des mélodies synthétiques créées pour la
formation : aucun droit d'auteur.

## Jour 4 · Ateliers individuels

Chaque participant a **son dossier**, avec son projet et le cahier de son atelier.

| Participant | Projet | Dossier à ouvrir | Cahier |
|---|---|---|---|
| Sébastien | ConfigSync : API REST signée → `config.ini` (téléphone ou carte SD) | `jour4/sebastien/ConfigSync` | [PDF](jour4/sebastien/Kotlin-J4-Sebastien-ConfigSync.pdf) |
| Joseph | Collecte : formulaire / import → CSV → mail | `jour4/joseph/Collecte` | [PDF](jour4/joseph/Kotlin-J4-Joseph-Collecte.pdf) |
| Keltoum | MediaHub : navigation, module `:filmlib`, Fragment dans Compose | `jour4/keltoum/MediaHub` | [PDF](jour4/keltoum/Kotlin-J4-Keltoum-MediaHub.pdf) |

**Pour tous : de l'appli finie au Play Store** (debug / release, dev / prod, signature, AAB / APK, Play Console),
avec l'appli Recettes du jour 2 : [`jour4/Kotlin-J4-Eleve-Publier-Recettes.pdf`](jour4/Kotlin-J4-Eleve-Publier-Recettes.pdf).
