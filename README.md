# Help Abroad

# Help Abroad

Help Abroad est une application Android native développée en Kotlin qui fournit les numéros d'urgence pour les pays du monde entier. Conçue pour les voyageurs et les expatriés, elle est entièrement accessible hors connexion grâce à sa base de données embarquée.

![Capture d'écran de l'application Help Abroad](assets/images/app_screenshot.png)

## Fonctionnalités

* **Base de Données Complète & Hors Ligne** : Accédez aux numéros d'urgence même sans connexion internet.
* **Recherche Intelligente** : Trouvez un pays par son nom (dans plusieurs langues), son acronyme (ex: "USA") ou des termes associés (ex: "Ecosse" pour le Royaume-Uni).
* **Appel Facilité** : Ouvrez le clavier ou lancez un appel directement en un clic, avec une option de confirmation pour éviter les erreurs.
* **Multi-langues** : Interface et données disponibles en Français, Anglais, Espagnol, Allemand, Italien et Portugais, avec un fallback intelligent vers l'anglais.
* **Thème Personnalisable** : Choisissez entre les thèmes Clair, Sombre, ou celui de votre système.

## Technologies Utilisées

* **Langage** : 100% [Kotlin](https://kotlinlang.org/)
* **UI** : [Jetpack Compose](https://developer.android.com/jetpack/compose) pour une interface déclarative et moderne.
* **Architecture** : MVVM (Model-View-ViewModel)
* **Base de Données** : [Room](https://developer.android.com/jetpack/androidx/releases/room) avec une base de données pré-chargée depuis les `assets`.
* **Recherche** : Index de recherche plein texte (FTS4) pour des performances optimales.
* **Injection de Dépendances** : [Hilt](https://dagger.dev/hilt/) pour une gestion propre des dépendances.
* **Asynchronisme** : Coroutines et Flow de Kotlin.
* **Sauvegarde des Préférences** : [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore).
* **Gestion de Version** : Git + GitHub.

## Comment Contribuer

1.  Forker ce dépôt.
2.  Créer une branche (`feature/ma-fonctionnalite`).
3.  Faire un commit clair de vos modifications.
4.  Ouvrir une Pull Request.

## Licence

[MIT License](LICENSE.md)