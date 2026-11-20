# MediaHub · projet de Keltoum

Une appli **Compose** avec une **barre de navigation** (Musique, Films, et un onglet Podcast ajouté **à la volée**).
L'onglet Films affiche un **Fragment** (XML) fourni par un **module bibliothèque**, `:filmlib`, développé « par une
autre équipe », sans le réécrire en Compose.

```
MediaHub/
├── app/       ← notre appli Compose (NavigationBar, Navigation, AndroidFragment)
└── filmlib/   ← la bibliothèque de l'équipe Film (Fragment + XML + métier)
```

## Lancer

Le cahier de l'atelier : [`Kotlin-J4-Keltoum-MediaHub.pdf`](Kotlin-J4-Keltoum-MediaHub.pdf).

1. Android Studio : **File → Open** → le dossier **`jour4/keltoum/MediaHub`** (pas la racine du dépôt).
2. **Run ▶**. Onglet Films → un film → le Fragment s'affiche. Retour → on revient à la liste.
3. **Logcat**, filtre `tag:FilmLib | tag:MediaHub` : le cycle de vie, étape par étape.

## Produire la bibliothèque

`./gradlew :filmlib:assembleRelease` → `filmlib/build/outputs/aar/filmlib-release.aar`
