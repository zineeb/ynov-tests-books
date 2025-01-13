#language: fr
Fonctionnalité: Gestion des livres
  En tant qu'utilisateur du système de bibliothèque
  Je souhaite gérer les livres
  Afin de les ajouter et les consulter

  Scénario: Créer un livre valide
    Étant donné un livre avec le titre "Le Petit Prince" et l'auteur "Antoine de Saint-Exupéry"
    Quand je crée le livre
    Alors le livre est ajouté avec succès

  Scénario: Créer un livre sans titre
    Étant donné un livre avec le titre "" et l'auteur "Inconnu"
    Quand je crée le livre
    Alors une erreur indiquant "Le titre ne peut pas être vide" est renvoyée

  Scénario: Lister les livres
    Étant donné une collection de livres existante
    Quand je demande la liste des livres
    Alors la liste des livres est renvoyée triée par titre
