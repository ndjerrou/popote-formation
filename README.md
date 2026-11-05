# Popote : projet fil rouge de la formation Kotlin & Android

Recettes ([TheMealDB](https://www.themealdb.com/api.php), sans clé) + liste de courses intelligente
(fusion des ingrédients, conversion des unités, articles cochés).

## Une branche par jalon

| Branche  | Contenu |
|----------|---------|
| `j1-fin` | Module `kata/` : exercices Kotlin + modèle métier (parser de mesures, liste de courses) + app « template » |
| `j2-fin` | Ressources, Manifest, cycle de vie, démo Views XML/View Binding, écrans Compose détail + formulaire |
| `j3-fin` | LazyColumn, navigation type-safe, ViewModel + StateFlow, coroutines (katas), Repository, Koin |
| `j4-fin` / `main` | Room, DataStore, Retrofit + kotlinx.serialization, erreurs réseau, WorkManager + notifications, tests, release signée, CI |

En retard ? `git switch j2-fin` pour repartir du début de J3.

## Commandes utiles

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
