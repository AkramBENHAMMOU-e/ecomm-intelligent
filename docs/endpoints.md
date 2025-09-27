# API Endpoints — ecomm-intelligent

Ce document explique clairement, et simplement, tous les endpoints disponibles pour tester le flux ecommerce: gérer les produits, manipuler le panier, et créer des commandes.

Base URL (par défaut): http://localhost:8080

- Tous les endpoints exposent et consomment du JSON.
- Les erreurs courantes renvoient des codes HTTP standard (404 si non trouvé, 400 si requête invalide, 201 si création, 204 si suppression).


1) Produits

- GET /api/products
  - Description: Liste tous les produits.
  - Paramètres: Aucun.
  - Réponse 200: tableau de Product.
    Exemple: [{"id":1,"name":"Laptop","description":"","image":"","price":1200.0,"quantity":100,"category":"Electronics"}]

- GET /api/products/{id}
  - Description: Récupère un produit par son id.
  - Paramètres de chemin: id (Long) — obligatoire.
  - Réponses:
    - 200: Product.
    - 404: Product not found (si l'id n'existe pas).

- POST /api/products
  - Description: Crée un nouveau produit.
  - Corps JSON (exemple minimal): {"name":"Laptop","description":"Lightweight","image":"url","price":1200,"quantity":100,"category":"Electronics"}
  - Réponses:
    - 201: Product créé (avec son id).
    - 400: si corps invalide (ex: JSON mal formé).

- DELETE /api/products/{id}
  - Description: Supprime un produit par id.
  - Réponses:
    - 204: supprimé avec succès.
    - 404: Product not found (si l'id n'existe pas).


2) Paniers (Cart)

- GET /api/carts
  - Description: Liste tous les paniers.
  - Réponse 200: tableau de Cart.

- GET /api/carts/{id}
  - Description: Récupère un panier par id.
  - Réponses:
    - 200: Cart (inclut ses items si présents).
    - 404: Cart not found.

- POST /api/carts
  - Description: Crée un nouveau panier. Peut contenir un customer facultatif si vous le passez.
  - Corps JSON (exemple vide): {}
  - Réponses:
    - 201: Cart créé.

- POST /api/carts/{cartId}/items
  - Description: Ajoute un produit au panier ou augmente la quantité s'il est déjà présent.
  - Paramètres de chemin: cartId (Long) — obligatoire.
  - Corps JSON: {"productId": Long, "quantity": int (>0)}
  - Réponses:
    - 201: Cart mis à jour (avec ses items).
    - 400: Quantity must be greater than 0 ou corps invalide.
    - 404: Cart not found ou Product not found.

- DELETE /api/carts/{id}
  - Description: Supprime un panier par id.
  - Réponses:
    - 204: supprimé.
    - 404: Cart not found.

Informations utiles sur le modèle Cart:
- items: liste de CartItem { id, product, quantity }.
- getTotalPrice(): somme des (price du product × quantity) pour chaque item.


3) Commandes (Order)

- GET /api/orders
  - Description: Liste toutes les commandes.
  - Réponse 200: tableau de Order.

- GET /api/orders/{id}
  - Description: Récupère une commande par id.
  - Réponses:
    - 200: Order.
    - 404: Order not found.

- POST /api/orders
  - Description: Crée une commande telle quelle (avancé/optionnel). Généralement, préférez utiliser le checkout ci-dessous.
  - Corps JSON: un objet Order. Si vous ne maîtrisez pas, utilisez plutôt /api/orders/checkout.
  - Réponse 201: Order créée.

- POST /api/orders/checkout?cartId={cartId}
  - Description: Transforme le panier (items) en commande. Fige le prix des produits au moment de l'achat, définit la date du jour et le statut Processing. Vide le panier après succès.
  - Paramètres de requête: cartId (Long) — obligatoire.
  - Réponses:
    - 201: Order créée à partir du panier.
    - 400: Cart is empty (si le panier n'a pas d'items).
    - 404: Cart not found.

- DELETE /api/orders/{id}
  - Description: Supprime une commande par id.
  - Réponses:
    - 204: supprimée.
    - 404: Order not found.

Statut d'une commande: StatusOrder = Processing | Shipped | Completed


Exemples rapides avec curl

# 1) Créer un produit
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","description":"Lightweight","image":"url","price":1200,"quantity":100,"category":"Electronics"}'

# 2) Créer un panier
curl -X POST http://localhost:8080/api/carts -H "Content-Type: application/json" -d '{}'

# 3) Ajouter un produit au panier (2 unités)
curl -X POST http://localhost:8080/api/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'

# 4) Consulter le panier
curl http://localhost:8080/api/carts/1

# 5) Checkout -> créer une commande depuis le panier
curl -X POST "http://localhost:8080/api/orders/checkout?cartId=1"

# 6) Lister les commandes
curl http://localhost:8080/api/orders


Notes
- Les identifiants (id) sont générés par la base (H2 en mémoire). Ils sont retournés dans les réponses de création.
- H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:ecomm, user: sa)
- Le modèle a été gardé simple pour faciliter les tests et l'extension future.