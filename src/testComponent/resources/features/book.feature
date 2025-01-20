#language: fr
Fonctionnalité: Gestion des livres
  En tant qu'utilisateur du système de bibliothèque
  Je souhaite gérer les livres
  Afin de les ajouter et les consulter

  Scénario: Réserver un livre déjà réservé
    Étant donné un livre avec le titre "Titre Double" et l'auteur "Auteur Double"
    Quand je crée le livre
    Alors le livre est ajouté avec succès

    Quand je réserve le livre avec le titre "Titre Double"
    Alors le livre est réservé avec succès

    Quand je réserve le livre avec le titre "Titre Double"
    Alors une erreur de domaine indiquant "Book is already reserved" est renvoyée
