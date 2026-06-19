# 📱 Gestionnaire de Notes - Android

Une application Android native développée en **Java** permettant de créer, modifier, trier et organiser des notes au quotidien. Projet réalisé dans le cadre de l'évaluation sur l'architecture Android.

---

## 🛠️ Architecture & Technologies
* **Langage :** Java
* **Base de données locales :** Room (SQLite)
* **Architecture :** MVVM (Model-View-ViewModel) avec LiveData pour la mise à jour de l'interface en temps réel.
* **Composants matériels :** RecyclerView, CardView, ConstraintLayout, FloatingActionButton.

---

## ✨ Fonctionnalités Principales (TP)
* **CRUD Complet :** Ajout, lecture, modification et suppression de notes stockées localement.
* **Favoris :** Possibilité de marquer des notes importantes d'une étoile (système persistant via Room).
* **Filtrage & Recherche :** Barre de recherche dynamique en temps réel (Lab 10).

---

## 🚀 Bonus Implémentés
Pour aller plus loin et optimiser l'application, les fonctionnalités suivantes ont été ajoutées :

1. **Suppression rapide (Swipe to Delete) :** Glisser une note vers la gauche ou la droite pour la supprimer instantanément avec un message de confirmation (ItemTouchHelper).
2. **Menu de Tri évolué :** Tri dynamique par **date de création** (plus récentes d'abord) ou par **ordre alphabétique** via un bouton dédié.
3. **Compteur dynamique :** Affichage en temps réel du nombre de notes visibles par rapport au total directement dans la barre de recherche (ex: *Trouvé : 2 / 5*).
4. **Partage de note :** Système d'Intent implicite permettant d'envoyer le texte d'une note vers d'autres applications du téléphone (WhatsApp, Gmail, SMS).
5. **Mode Sombre Automatique :** L'application s'adapte automatiquement aux préférences du système (Clair / Sombre) pour un meilleur confort visuel.
6. **Amélioration de l'Expérience Utilisateur (UX) :**
   * Clavier intelligent avec majuscules automatiques.
   * Focus automatique sur le champ "Titre" dès l'ouverture de l'écran d'ajout.

---

## 📦 Installation et Lancement
1. Cloner le projet : `git clone https://github.com/mamadou-esp/Gestionnaire-de-Notes.git`
2. Ouvrir le dossier avec **Android Studio**.
3. Attendre la synchronisation Gradle complète.
4. Lancer l'application sur un émulateur ou un appareil physique.

*Développé par mamadou-esp, binetandiayethiamsylla-beep et adjasiradiop.*
