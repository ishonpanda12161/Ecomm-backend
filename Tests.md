# Ecommerce API — Manual Test Guide (Postman)

Base URL: http://localhost:8080   (change the port if yours differs)

## How auth works (IMPORTANT)
- Auth uses an **HttpOnly cookie** named `requestJwtCookie`.
- Flow: call `POST /api/open/signin` first. Postman automatically stores the
  cookie for the localhost domain. Every subsequent request is then authenticated.
- To "log out", call `POST /api/open/signout` (clears the cookie).
- Endpoints under `/api/open/**` need NO auth.
- Endpoints under `/api/public/**` need a USER cookie.
- Endpoints under `/api/admin/**` need an ADMIN cookie.
  ⚠️ KNOWN BUG: nested admin paths are currently NOT admin-restricted
  (see Issues.md C1). A USER cookie will succeed where it shouldn't.

Set header `Content-Type: application/json` for all POST/PUT requests.

====================================================================
## 1. AUTH  (/api/open — no auth)
====================================================================

### 1.1 Signup
POST  http://localhost:8080/api/open/signup
Body:
{
  "name": "John Doe",
  "username": "johndoe",
  "password": "secret123",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
Notes:
- `roles` is optional (defaults to ROLE_USER).
- Do NOT send ROLE_ADMIN here unless you intend a self-made admin.
- ✅ Expected 200. ⚠️ Currently returns the raw User entity (password hash leak, see Issues C2).

### 1.2 Signin  (run this FIRST to get the cookie)
POST  http://localhost:8080/api/open/signin
Body:
{
  "username": "johndoe",
  "password": "secret123"
}
Notes:
- ✅ Expected 200. The `requestJwtCookie` is set automatically.
- ❌ Negative: wrong password → currently 404 (should be 401, see Issues H4).

### 1.3 Signout
POST  http://localhost:8080/api/open/signout
Notes: clears the cookie. ✅ Expected 200.

### 1.4 Get username
GET  http://localhost:8080/api/open/username
Notes: ⚠️ permitAll but currently throws for anonymous callers (broken).

### 1.5 Get current user
GET  http://localhost:8080/api/open/user
Notes: ⚠️ permitAll but returns UserDetailsImpl and LEAKS the password hash
(JSON includes the BCrypt hash). Must be fixed (Issues C2).

====================================================================
## 2. ADDRESS  (/api/public — needs USER cookie)
====================================================================

### 2.1 Create address
POST  http://localhost:8080/api/public/address
Body:
{
  "street": "123 Main Street",
  "building": "Building A",
  "city": "New York",
  "state": "New York",
  "country": "USA",
  "pincode": "10001"
}
✅ Expected 201.

### 2.2 List addresses
GET  http://localhost:8080/api/public/address
✅ Expected 200.

### 2.3 Get address by id
GET  http://localhost:8080/api/public/address/1
✅ Expected 200. ❌ Negative: id 9999 → 404.

### 2.4 Update address
PUT  http://localhost:8080/api/public/address/1/update
Body: (same shape as create)
{
  "street": "456 Updated Street",
  "building": "Building B",
  "city": "Boston",
  "state": "Massachusetts",
  "country": "USA",
  "pincode": "02101"
}
✅ Expected 200.

### 2.5 Delete address
DELETE  http://localhost:8080/api/public/address/1
✅ Expected 200 (should be 204, see Issues L1).

====================================================================
## 3. CART  (/api/public — USER; /api/admin/carts — ADMIN)
====================================================================

### 3.1 Add product to cart
POST  http://localhost:8080/api/public/cart/add/product/1/quantity/2
(1 = productId, 2 = quantity)
✅ Expected 201. ❌ Negative: quantity -1 or 0 is accepted (unvalidated).

### 3.2 View my cart
GET  http://localhost:8080/api/public/cart/user
✅ Expected 200.

### 3.3 Update cart item quantity
PUT  http://localhost:8080/api/public/cart/udpate/product/1/operation/add
(1 = productId; operation = "delete" removes it, anything else increments by 1)
⚠️ Path contains a typo "udpate" (Issues L2).

### 3.4 Delete cart item
DELETE  http://localhost:8080/api/public/cart/delete/product/1
✅ Expected 200.

### 3.5 Admin: list all carts
GET  http://localhost:8080/api/admin/carts
Needs ADMIN cookie. ✅ Expected 200.

====================================================================
## 4. CATEGORY  (/api/public — USER list; /api/admin — ADMIN rest)
====================================================================

### 4.1 List categories
GET  http://localhost:8080/api/public/category
✅ Expected 200.

### 4.2 Bulk create categories  (ADMIN)
POST  http://localhost:8080/api/admin/categoryBulk
Body:
[
  { "categoryName": "Electronics" },
  { "categoryName": "Books" }
]
✅ Expected 200.

### 4.3 Create single category  (ADMIN)
POST  http://localhost:8080/api/admin/category
Body:
{ "categoryName": "Electronics" }
✅ Expected 201. ⚠️ Returns the input DTO, NOT the saved one (no generated id).

### 4.4 Update category  (ADMIN)
PUT  http://localhost:8080/api/admin/category
Body:
{ "categoryId": 1, "categoryName": "Electronics Updated" }
✅ Expected 200.

### 4.5 Delete category  (ADMIN)
DELETE  http://localhost:8080/api/admin/category/1
⚠️ DATA LOSS BUG: also deletes all products in this category
(Issues H1). Only delete empty categories while testing.

====================================================================
## 5. ORDER  (/api/public — USER)
====================================================================

### 5.1 Place order
POST  http://localhost:8080/api/public/order/payment/CARD
Body:
{
  "addressId": 1,
  "pgPaymentId": "pay_abc123",
  "pgStatus": "SUCCESS",
  "pgResponseMessage": "Payment successful",
  "pgName": "John Doe"
}
Notes:
- Needs at least one item in cart and a valid addressId.
- ⚠️ `addressId` is NOT validated → null/garbage currently accepted (Issues M2).
- CARD / COD / any string is accepted as paymentMethod (no allow-list).
✅ Expected 201.

====================================================================
## 6. PRODUCT  (/api/public — USER reads; /api/admin — ADMIN writes)
====================================================================

### 6.1 List all products
GET  http://localhost:8080/api/public/products
✅ Expected 200.

### 6.2 Products by category
GET  http://localhost:8080/api/public/categories/1/products
✅ Expected 200.

### 6.3 Products by keyword
GET  http://localhost:8080/api/public/products/keyword/laptop
✅ Expected 200.

### 6.4 Create product  (ADMIN)
POST  http://localhost:8080/api/admin/categories/1/product
Body:
{
  "productName": "Wireless Laptop",
  "description": "A powerful laptop",
  "quantity": 10,
  "price": 999.99,
  "discount": 10.0,
  "specialPrice": 899.99
}
✅ Expected 201. ⚠️ Negative price/quantity accepted (Issues M2).

### 6.5 Update product  (ADMIN)
PUT  http://localhost:8080/api/admin/products/1
Body: (same as create)
{
  "productName": "Wireless Laptop v2",
  "description": "Updated description",
  "quantity": 5,
  "price": 899.99,
  "discount": 15.0,
  "specialPrice": 764.99
}
✅ Expected 200.

### 6.6 Update product image  (ADMIN, multipart/form-data)
PUT  http://localhost:8080/api/admin/products/1/image
Body type: form-data
  Key: image   (type: File)   Value: <choose a file>
✅ Expected 200.

### 6.7 Delete product  (ADMIN)
DELETE  http://localhost:8080/api/admin/products/1
⚠️ If the product was ever ordered, this throws a 500 FK error (Issues H2).

====================================================================
## 7. NEGATIVE / SECURITY CHECKLIST
====================================================================
[ ] Call any /api/public/** endpoint with NO cookie → expect 401/403.
[ ] Call /api/admin/category with a USER cookie → currently 200 (BUG C1).
[ ] Signup with password "ab" (too short) → 400.
[ ] Signin with wrong password → currently 404 (should be 401, H4).
[ ] Send expired/manufactured cookie → currently 500 (should be 401, C4).
