# Formation Kotlin & Android

Tout ce qu'il vous faut est sur la branche **`main`** : les cahiers, les corrigés et les projets.

## Les cahiers (PDF)

| Jour | Cahier |
|---|---|
| 1 | [`cahier/Popote-Eleve-J1.pdf`](cahier/Popote-Eleve-J1.pdf) · Kotlin (exercices dans `kata/`) |
| 2 | [`cahier/Kotlin-Eleve-J2-Trois-applis.pdf`](cahier/Kotlin-Eleve-J2-Trois-applis.pdf) · Convertisseur, Mes Courses, Recettes |
| 3 | [`cahier/Kotlin-Eleve-J3-MusicPlayer.pdf`](cahier/Kotlin-Eleve-J3-MusicPlayer.pdf) · MusicPlayer : ViewModel, SavedStateHandle, navigation |

## Les projets

| Dossier | Contenu |
|---|---|
| `kata/` | Jour 1 : exercices Kotlin et leurs tests |
| `corriges/jour2/1-Convertisseur/` | Jour 2 : corrigé de l'appli 1 |
| `corriges/jour2/2-MesCourses/` | Jour 2 : corrigé de l'appli 2 |
| `corriges/jour2/3-Recettes/` | Jour 2 : corrigé de l'appli 3 |
| `jour3/MusicPlayer/` | Jour 3 : **le point de départ** du lecteur de musique |

**Ouvrir un projet dans Android Studio :** **File → Open**, puis **le dossier du projet** (par exemple
`jour3/MusicPlayer`), jamais la racine du dépôt. Attendez la synchronisation Gradle, puis **Run ▶**.

## Jour 3 : une branche par étape

Les branches `j3/…` contiennent MusicPlayer **à la fin** de chaque étape. Elles servent à rattraper une étape ou à
comparer avec votre code.

| Branche | Étape |
|---|---|
| `j3/0-depart` | 0 · Le point de départ (identique à `jour3/MusicPlayer`) |
| `j3/1-gradle` | 1 · Les dépendances |
| `j3/2-contrat` | 2 · `PlayerUiState` et `PlayerAction` |
| `j3/3-viewmodel` | 3 · `PlayerViewModel` + tests |
| `j3/4-route-screen` | 4 · `PlayerRoute` et `PlayerScreen` |
| `j3/5-process-death` | 5 · `SavedStateHandle` |
| `j3/6-navigation` | 6 · Plusieurs écrans |
| `j3/7-son` | 7 · Le vrai son (fin du jour 3) |

Le plus simple pour comparer : sur GitHub, choisissez la branche dans la liste déroulante et lisez le fichier en
ligne. Pour repartir d'une étape : **Code → Download ZIP** sur cette branche, puis ouvrez son dossier `MusicPlayer`.

Les morceaux de MusicPlayer sont des mélodies synthétiques créées pour la formation : aucun droit d'auteur.

## Le projet Popote (`app/`)

Le dossier `app/` contient Popote, une appli de recettes et de liste de courses plus complète (Room, Retrofit,
Koin, WorkManager, tests, signature), à explorer comme exemple.

## Commandes utiles (Popote et kata, depuis la racine)

```bash
./gradlew :kata:test                    # tests du Jour 1 (et des katas coroutines)
./gradlew :app:testDebugUnitTest        # tests unitaires de l'app (JUnit + MockK)
./gradlew :app:connectedDebugAndroidTest  # tests UI Compose (émulateur lancé)
./gradlew :app:assembleDebug            # APK debug
./gradlew :app:bundleRelease            # AAB signé (nécessite keystore.properties)
```

## Signature (release)

1. Générer une clé d'upload : `keytool -genkeypair -v -keystore keystore/popote-upload.jks -alias popote -keyalg RSA -keysize 4096 -validity 10000`
2. Copier `keystore.properties.example` vers `keystore.properties` et le remplir (**jamais commité**).
3. `./gradlew :app:bundleRelease`, puis `apksigner verify --print-certs` sur l'APK.
