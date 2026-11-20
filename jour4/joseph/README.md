# Collecte · projet de Joseph

Un outil de **collecte** : l'opérateur remplit un formulaire (ou importe des relevés depuis le cloud), l'appli
**retraite** les données en un fichier **CSV** (ouvrable dans Excel) et l'**envoie par mail** en pièce jointe.

## Lancer

Le cahier de l'atelier : [`Kotlin-J4-Joseph-Collecte.pdf`](Kotlin-J4-Joseph-Collecte.pdf).

1. Android Studio : **File → Open** → le dossier **`jour4/joseph/Collecte`** (pas la racine du dépôt).
2. **Run ▶**. Ajouter des relevés, ou **☁️ Importer** (lit `jour4/joseph/Collecte/cloud/releves.json`, publié sur GitHub).
3. **📧 Envoyer** : choisir Gmail, le fichier `releves-….csv` est joint.

Sur l'émulateur, un compte Google doit être configuré dans Gmail pour que l'envoi aboutisse.
