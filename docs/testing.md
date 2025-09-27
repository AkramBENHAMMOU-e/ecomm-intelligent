# Guide de test rapide — ecomm-intelligent

Ce guide vous aide à tester l’API avec des données de test préchargées automatiquement au démarrage de l’application (base H2 en mémoire).

Au démarrage, les données suivantes sont créées:
- Produits (IDs générés, consultez l’API pour confirmer):
  - Laptop — price: 899.99, category: PC
  - Phone — price: 499.00, category: Phones
  - Headphones — price: 149.50, category: Audio
- Un panier de démonstration (id probable = 1) avec:
  - 1 × Laptop
  - 2 × Phone

Base URL par défaut: http://localhost:8080

1) Démarrer l’application
- Lancez l’application Spring Boot (profil par défaut). La base H2 est en mémoire.
- Console H2 (facultatif): http://localhost:8080/h2-console
  - JDBC URL: jdbc:h2:mem:ecomm
  - User: sa (sans mot de passe)

2) Vérifier les produits seedés
curl http://localhost:8080/api/products

3) Vérifier le panier de démo
- Liste des paniers:
curl http://localhost:8080/api/carts
- Consulter le panier 1 (si existant):
curl http://localhost:8080/api/carts/1

4) Ajouter un produit dans un panier
- Exemple: ajouter 1 Headphones (supposons id produit = 3) au panier 1:
curl -X POST http://localhost:8080/api/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId":3, "quantity":1}'

5) Créer une commande via checkout
- Depuis le panier 1:
curl -X POST "http://localhost:8080/api/orders/checkout?cartId=1"

6) Lister les commandes ou consulter une commande par id
curl http://localhost:8080/api/orders
curl http://localhost:8080/api/orders/1

7) Créer un nouveau panier (optionnel)
curl -X POST http://localhost:8080/api/carts -H "Content-Type: application/json" -d '{}'

8) Créer un nouveau produit (optionnel)
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Mouse","description":"Wireless","image":"mouse.jpg","price":29.9,"quantity":100,"category":"Accessories"}'

Erreurs fréquentes
- 404 Not Found: id inexistant (produit, panier, commande)
- 400 Bad Request: quantité <= 0 lors de l’ajout d’un item

Notes
- Les IDs sont générés à chaque démarrage (mémoire). Utilisez GET pour vérifier les IDs courants.
- Après un checkout réussi, le panier est vidé. La commande conserve un "snapshot" des prix au moment de l’achat.
