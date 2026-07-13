# API Documentation - Ecommerce Backend

Base URL: `http://localhost:8080`

---

## Table of Contents

1. [Authentication System](#1-authentication-system)
2. [Security Rules](#2-security-rules)
3. [Global Error Responses](#3-global-error-responses)
4. [Auth Endpoints (Open)](#4-auth-endpoints-open)
5. [Product Endpoints](#5-product-endpoints)
6. [Category Endpoints](#6-category-endpoints)
7. [Cart Endpoints](#7-cart-endpoints)
8. [Address Endpoints](#8-address-endpoints)
9. [Order Endpoints](#9-order-endpoints)
10. [DTO Reference](#10-dto-reference)
11. [Step-by-Step Backend Testing Workflow](#11-step-by-step-backend-testing-workflow)

---

## 1. Authentication System

This backend uses JWT stored in an HTTP-only cookie. There are no Bearer tokens in the Authorization header. The cookie is automatically sent and received by the browser on every request to the `/api` path.

**Cookie name:** `requestJwtCookie`

**Cookie properties:**
- Path: `/api`
- Max age: 86400 seconds (1 day)
- HttpOnly: true
- Secure: false (local dev only; must be true in production)
- SameSite: Lax

**How login works:**
1. Client sends `POST /api/open/signin` with username and password.
2. Server validates credentials, generates a JWT, and sets it in the `requestJwtCookie` cookie via the `Set-Cookie` response header.
3. The response body contains the token, user info, and roles.
4. On all subsequent requests, the browser automatically sends the cookie. The JWT filter extracts the token from the cookie, validates it, and sets the authentication context.
5. To logout, call `POST /api/open/signout` which clears the cookie (sets max age to 0).

**For non-browser clients (Postman, curl, etc.):**
- After signing in, extract the `Set-Cookie` header from the response.
- On subsequent requests, include the `Cookie` header with the full cookie value (e.g., `Cookie: requestJwtCookie=eyJhbGciOiJIUz...`).

---

## 2. Security Rules

| URL Pattern | Access Level | Required Role                 |
|---|---|-------------------------------|
| `/api/open/**` | Public | None (anyone can access)      |
| `/api/public/**` | Authenticated | `ROLE_USER` or `ROLE_ADMIN`   |
| `/api/admin/**` | Admin only | `ROLE_ADMIN` or `ROLE_SELLER` |
| All other routes | Authenticated | Any authenticated user        |

**CSRF:** Disabled. No CSRF tokens are needed.

**Roles seeded on startup:** `ROLE_USER`, `ROLE_ADMIN`, `ROLE_SELLER`

**Default role on signup:** `ROLE_USER` (if no roles are provided in the request body)

---

## 3. Global Error Responses

The backend has a global exception handler (`@RestControllerAdvice`). All errors return JSON.

### 3.1 Validation Error (422 or 400)

When request body fields fail `@Valid` checks.

**Status:** 422 (or 400 depending on Spring version)

**Response body:** `Map<String, String>` where keys are field names and values are error messages.

```json
{
  "name": "Name contain at least 2 characters.",
  "email": "Must be a well-formed email address.",
  "password": "Must contain at least 3 characters."
}
```

### 3.2 Resource Not Found (404)

**Status:** 404

**Response body:** String message.

```
Product not found with productId: 5.
```

### 3.3 Resource Already Exists (400)

**Status:** 400

**Response body:** String message.

```
User already exists with username: john.
```

### 3.4 SQL Integrity Constraint Violation (409)

**Status:** 409

**Response body:**

```json
{
  "message": "Resource already exists.",
  "error": "<SQL error message>"
}
```

### 3.5 Generic API Error (400)

**Status:** 400

**Response body:** String message.

```
One or more roles are invalid.
Cart is empty.
Quantity not available in stock.
```

### 3.6 JWT / Authentication Error (401)

**Status:** 401

**Response body:** String message.

```
JWT Error - Type: Validation. Message: Invalid token.
```

### 3.7 Access Denied (401)

**Status:** 401

**Response body:** String message.

```
Access is denied
```

---

## 4. Auth Endpoints (Open)

All endpoints under `/api/open` are public. No authentication is required.

---

### 4.1 POST /api/open/signup

Register a new user.

**Request body:** `SignupDTO` (JSON)

| Field | Type | Required | Validation | Default |
|---|---|---|---|---|
| `name` | String | Yes | `@NotBlank`, `@Size(min=2)` | - |
| `username` | String | Yes | `@NotBlank`, `@Size(min=3)` | - |
| `password` | String | Yes | `@NotBlank`, `@Size(min=3)` | - |
| `email` | String | Yes | `@Email`, `@NotBlank`, `@Size(min=11)` | - |
| `roles` | Set of String | No | Valid role names | `{"ROLE_USER"}` |

Valid role names: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_SELLER`

**Request example:**

```json
{
  "name": "John Doe",
  "username": "john",
  "password": "password123",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

**Success response:**

**Status:** 201

**Response body:** `UserDTO`

```json
{
  "name": "John Doe",
  "username": "john",
  "email": "john@example.com",
  "roles": [
    {
      "id": 1,
      "roleName": "ROLE_USER"
    }
  ],
  "products": [],
  "addresses": []
}
```

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | Username already exists | `"User already exists with username: john."` |
| 400 | Invalid role provided | `"One or more roles are invalid."` |
| 400 | Missing required fields or validation fails | `{"fieldName": "error message"}` |

---

### 4.2 POST /api/open/signin

Login with username and password. Returns JWT in both the response body and the `Set-Cookie` header.

**Request body:** `LoginRequest` (JSON)

| Field | Type | Required | Validation |
|---|---|---|---|
| `username` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `password` | String | Yes | `@NotBlank`, `@Size(min=3)` |

**Request example:**

```json
{
  "username": "john",
  "password": "password123"
}
```

**Success response:**

**Status:** 200

**Response headers:** `Set-Cookie: requestJwtCookie=eyJhbGciOiJIUz...; Path=/api; Max-Age=86400; HttpOnly; SameSite=Lax`

**Response body:** `LoginResponse`

```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john",
  "roles": ["ROLE_USER"],
  "email": "john@example.com"
}
```

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 401 | Wrong username or password | `"JWT Error - Type: Authentication. Message: Could not authenticate"` |
| 400 | Missing required fields or validation fails | `{"fieldName": "error message"}` |

**Important for frontend:** After receiving the response, the browser stores the cookie automatically. For non-browser clients, extract the `Set-Cookie` header and use it in subsequent requests.

---

### 4.3 POST /api/open/signout

Logout. Clears the JWT cookie.

**Request body:** None

**Success response:**

**Status:** 200

**Response headers:** `Set-Cookie: requestJwtCookie=; Path=/api; Max-Age=0`

**Response body:**

```json
{
  "message": "You have been signed out."
}
```

---

### 4.4 GET /api/open/username

Returns the currently authenticated username. Works if the user has a valid JWT cookie set.

**Request body:** None

**Success response:**

**Status:** 200

**Response body:** String (plain text, not JSON)

```
john
```

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | No authentication found (null) | `"NULL"` |

---

### 4.5 GET /api/open/user

Returns the currently authenticated user's details.

**Request body:** None

**Success response:**

**Status:** 200

**Response body:** `UserResponseDTO`

```json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "roles": [
    {
      "authority": "ROLE_USER"
    }
  ]
}
```

**Note:** The `roles` array contains `GrantedAuthority` objects. Each object has an `authority` field containing the role name string (e.g., `"ROLE_USER"`, `"ROLE_ADMIN"`).

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | No authentication found (null) | `"NULL"` |

---

## 5. Product Endpoints

### 5.1 GET /api/public/products

Get all products with pagination and sorting. Requires `ROLE_USER` or `ROLE_ADMIN`.

**Query parameters (all optional):**

| Param | Type | Default | Description |
|---|---|---|---|
| `pageNum` | Integer | 0 | Page number (0-indexed) |
| `pageSize` | Integer | 20 | Number of items per page |
| `sortBy` | String | `"id"` | Field to sort by |
| `sortDir` | String | `"asc"` | Sort direction: `"asc"` or `"desc"` |

**Success response:**

**Status:** 200

**Response body:** `ProductResponseDTO`

```json
{
  "content": [
    {
      "id": 1,
      "productName": "iPhone 15",
      "description": "Apple smartphone",
      "image": "default.png",
      "quantity": 50,
      "price": 999.99,
      "discount": 10.0,
      "specialPrice": 899.991
    }
  ],
  "pageNum": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1,
  "lastPage": true
}
```

**Note:** `specialPrice` is calculated as `price * (1 - discount/100)`.

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | No products found | `"No products found."` |
| 401 | Not authenticated or invalid JWT | JWT error message |

---

### 5.2 GET /api/public/categories/{categoryId}/products

Get all products in a specific category with pagination and sorting.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `categoryId` | Long | The category ID |

**Query parameters:** Same as 5.1 (pageNum, pageSize, sortBy, sortDir)

**Success response:**

**Status:** 200

**Response body:** Same structure as `ProductResponseDTO` (same as 5.1)

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | No products found in this category | `"No products found."` |
| 404 | Category not found | `"Category not found with categoryId: 999."` |

---

### 5.3 GET /api/public/products/keyword/{keyword}

Search products by name (case-insensitive partial match).

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `keyword` | String | Search term |

**Query parameters:** Same as 5.1 (pageNum, pageSize, sortBy, sortDir)

**Success response:**

**Status:** 200

**Response body:** Same structure as `ProductResponseDTO` (same as 5.1)

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | No products match the keyword | `"No products found."` |

---

### 5.4 POST /api/admin/categories/{categoryId}/product

Add a new product to a category. Requires `ROLE_ADMIN`.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `categoryId` | Long | The category to add the product to |

**Request body:** `ProductDTO` (JSON)

| Field | Type | Required | Validation |
|---|---|---|---|
| `id` | Long | No | Ignored on create |
| `productName` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `description` | String | Yes | `@NotBlank`, `@Size(min=5)` |
| `image` | String | No | Set to `"default.png"` automatically |
| `quantity` | Integer | Yes | `@NotNull`, `@Positive` |
| `price` | Double | Yes | `@NotNull`, `@Positive` |
| `discount` | Double | Yes | `@NotNull` |
| `specialPrice` | Double | No | Calculated automatically |

**Request example:**

```json
{
  "productName": "iPhone 15",
  "description": "Apple smartphone 128GB",
  "quantity": 50,
  "price": 999.99,
  "discount": 10.0
}
```

**Success response:**

**Status:** 201

**Response body:** `ProductDTO`

```json
{
  "id": 1,
  "productName": "iPhone 15",
  "description": "Apple smartphone 128GB",
  "image": "default.png",
  "quantity": 50,
  "price": 999.99,
  "discount": 10.0,
  "specialPrice": 899.991
}
```

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | Product name already exists | `"Product already exists with productName: iPhone 15."` |
| 404 | Category not found | `"Category not found with categoryId: 999."` |
| 401 | Not admin | Access denied |

---

### 5.5 PUT /api/admin/products/{productId}

Update an existing product. Requires `ROLE_ADMIN`.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `productId` | Long | The product ID |

**Request body:** `ProductDTO` (JSON) - same fields as 5.4

**Success response:**

**Status:** 200

**Response body:** `ProductDTO` (updated)

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Product not found | `"Product not found with productId: 999."` |

---

### 5.6 PUT /api/admin/products/{productId}/image

Update a product's image. Requires `ROLE_ADMIN`.

**Content type:** `multipart/form-data`

**Form parameters:**

| Param | Type | Required | Description |
|---|---|---|---|
| `image` | File | Yes | The image file |

**Success response:**

**Status:** 200

**Response body:** `ProductDTO` (updated with new image filename)

**Note:** The image is saved to the `images/` directory with a UUID filename to prevent collisions.

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Product not found | `"Product not found with productId: 999."` |
| 404 | Empty file uploaded | `"File not found with Image: []."` |

---

### 5.7 DELETE /api/admin/products/{productId}

Soft-delete a product (sets `isActive` to false and removes associated cart items). Requires `ROLE_ADMIN`.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `productId` | Long | The product ID |

**Success response:**

**Status:** 204 (No Content)

**Response body:** Empty

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Product not found | `"Product not found with productId: 999."` |

---

## 6. Category Endpoints

### 6.1 GET /api/public/category

Get all categories with pagination and sorting. Requires `ROLE_USER` or `ROLE_ADMIN`.

**Query parameters (all optional):**

| Param | Type | Default | Description |
|---|---|---|---|
| `pageNum` | Integer | 0 | Page number (0-indexed) |
| `pageSize` | Integer | 20 | Number of items per page |
| `sortBy` | String | `"id"` | Field to sort by |
| `sortDir` | String | `"asc"` | Sort direction: `"asc"` or `"desc"` |

**Success response:**

**Status:** 200

**Response body:** `CategoryResponseDTO`

```json
{
  "content": [
    {
      "id": 1,
      "categoryName": "Electronics"
    }
  ],
  "pageNum": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1,
  "lastPage": true
}
```

---

### 6.2 POST /api/admin/category

Create a single category. Requires `ROLE_ADMIN`.

**Request body:** `CategoryDTO` (JSON)

| Field | Type | Required | Validation |
|---|---|---|---|
| `id` | Long | No | Ignored on create |
| `categoryName` | String | Yes | `@NotBlank`, `@Size(min=3, max=25)` |

**Request example:**

```json
{
  "categoryName": "Electronics"
}
```

**Success response:**

**Status:** 201

**Response body:** `Category` entity (raw, not a DTO)

```json
{
  "id": 1,
  "categoryName": "Electronics"
}
```

**Note:** This endpoint returns the raw `Category` entity, not a `CategoryDTO`. This is inconsistent with other endpoints.

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | Category name already exists | `"Category already exists with id: null."` |

---

### 6.3 POST /api/admin/categoryBulk

Create multiple categories at once. Requires `ROLE_ADMIN`.

**Request body:** `List<CategoryDTO>` (JSON array)

```json
[
  { "categoryName": "Electronics" },
  { "categoryName": "Clothing" },
  { "categoryName": "Books" }
]
```

**Success response:**

**Status:** 200

**Response body:** String

```
Created
```

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | Empty list provided | `"Nothing to Create. List Empty"` |

---

### 6.4 PUT /api/admin/category

Update an existing category. Requires `ROLE_ADMIN`.

**Request body:** `CategoryDTO` (JSON)

| Field | Type | Required | Validation |
|---|---|---|---|
| `id` | Long | Yes | Must match an existing category |
| `categoryName` | String | Yes | `@NotBlank`, `@Size(min=3, max=25)` |

**Request example:**

```json
{
  "id": 1,
  "categoryName": "Electronics & Gadgets"
}
```

**Success response:**

**Status:** 200

**Response body:** String

```
Updated!
```

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Category not found | `"Category not found with ID: 999."` |

---

### 6.5 DELETE /api/admin/category/{categoryId}

Delete a category. Requires `ROLE_ADMIN`. Fails if the category contains products.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `categoryId` | Long | The category ID |

**Success response:**

**Status:** 204 (No Content)

**Response body:** Empty

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Category not found | `"Category not found with ID: 999."` |
| 400 | Category has products | `"Cannot delete. Category contains products."` |

---

## 7. Cart Endpoints

Cart operations are per-user. Each user gets a cart automatically when they add their first item.

### 7.1 POST /api/public/cart/add/product/{productId}/quantity/{quantity}

Add a product to the logged-in user's cart. Requires `ROLE_USER` or `ROLE_ADMIN`.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `productId` | Long | The product to add |
| `quantity` | Integer | Quantity to add (must be >= 1) |

**Success response:**

**Status:** 201

**Response body:** `CartItemDTO`

```json
{
  "id": 1,
  "productDto": {
    "id": 1,
    "productName": "iPhone 15",
    "description": "Apple smartphone 128GB",
    "image": "default.png",
    "quantity": 50,
    "price": 999.99,
    "discount": 10.0,
    "specialPrice": 899.991
  },
  "quantity": 2,
  "availableQuantity": 50
}
```

**Note:** `availableQuantity` shows the remaining stock for the product.

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 400 | Quantity less than 1 | `"Please add at least 1 product."` |
| 400 | Product already in cart | `"Product already exists with id: 1."` |
| 400 | Insufficient stock | `"Quantity not available in stock."` |
| 404 | Product not found | `"Product not found with productId: 999."` |

---

### 7.2 GET /api/public/cart/user

Get the logged-in user's cart. Requires `ROLE_USER` or `ROLE_ADMIN`.

**Success response:**

**Status:** 200

**Response body:** `CartDTO`

```json
{
  "id": 1,
  "totalPrice": 1799.982,
  "cartItems": [
    {
      "id": 1,
      "productDto": {
        "id": 1,
        "productName": "iPhone 15",
        "description": "Apple smartphone 128GB",
        "image": "default.png",
        "quantity": 50,
        "price": 999.99,
        "discount": 10.0,
        "specialPrice": 899.991
      },
      "quantity": 2,
      "availableQuantity": 50
    }
  ]
}
```

**Note:** `totalPrice` is the sum of `specialPrice * quantity` for all cart items.

---

### 7.3 PUT /api/public/cart/update/product/{productId}/operation/{operation}

Update the quantity of a product in the cart. Requires `ROLE_USER` or `ROLE_ADMIN`.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `productId` | Long | The product in the cart |
| `operation` | String | `"add"` to increment by 1, `"delete"` to decrement by 1 |

**Behavior:**
- If `operation` is `"delete"` and quantity becomes 0 or less, the item is removed from the cart.
- If the product stock is 0, the item is removed from the cart.
- Quantity is capped at available stock (cannot exceed product quantity).

**Success response:**

**Status:** 200

**Response body:** `CartDTO` (updated cart, same structure as 7.2)

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Product not in cart | `"Item not found with id: 1."` |
| 404 | Product not found | `"Product not found with productId: 999."` |

---

### 7.4 DELETE /api/public/cart/delete/product/{productId}

Remove a product from the logged-in user's cart. Requires `ROLE_USER` or `ROLE_ADMIN`.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `productId` | Long | The product to remove |

**Success response:**

**Status:** 204 (No Content)

**Response body:** Empty

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Product not in cart | `"Item not found with id: 1."` |

---

### 7.5 GET /api/admin/carts

Get all user carts. Requires `ROLE_ADMIN`.

**Success response:**

**Status:** 200

**Response body:** `List<CartDTO>` (array of cart objects, same structure as 7.2)

```json
[
  {
    "id": 1,
    "totalPrice": 1799.982,
    "cartItems": [
      {
        "id": 1,
        "productDto": { ... },
        "quantity": 2,
        "availableQuantity": 50
      }
    ]
  }
]
```

---

## 8. Address Endpoints

Addresses are per-user. Each user can have multiple addresses. Requires `ROLE_USER` or `ROLE_ADMIN`.

### 8.1 POST /api/public/address

Create a new address for the logged-in user.

**Request body:** `AddressDTO` (JSON)

| Field | Type | Required | Validation |
|---|---|---|---|
| `id` | Long | No | Ignored on create |
| `street` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `building` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `city` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `state` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `country` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `pincode` | String | Yes | `@NotBlank`, `@Size(min=5)` |

**Request example:**

```json
{
  "street": "123 Main Street",
  "building": "Apartment 4B",
  "city": "New York",
  "state": "NY",
  "country": "USA",
  "pincode": "10001"
}
```

**Success response:**

**Status:** 201

**Response body:** `AddressDTO`

```json
{
  "id": 1,
  "street": "123 Main Street",
  "building": "Apartment 4B",
  "city": "New York",
  "state": "NY",
  "country": "USA",
  "pincode": "10001"
}
```

---

### 8.2 GET /api/public/address

Get all addresses for the logged-in user.

**Success response:**

**Status:** 200

**Response body:** `List<AddressDTO>` (JSON array)

```json
[
  {
    "id": 1,
    "street": "123 Main Street",
    "building": "Apartment 4B",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "pincode": "10001"
  }
]
```

---

### 8.3 GET /api/public/address/{id}

Get a specific address by ID. Only returns the address if it belongs to the logged-in user.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `id` | Long | The address ID |

**Success response:**

**Status:** 200

**Response body:** `AddressDTO` (same as 8.1)

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Address not found or not owned by user | `"Addres not found with id: 999."` |

**Note:** There is a typo in the error message: `"Addres"` instead of `"Address"`.

---

### 8.4 PUT /api/public/address/{id}/update

Update an existing address. Only works on addresses owned by the logged-in user.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `id` | Long | The address ID |

**Request body:** `AddressDTO` (JSON) - same fields as 8.1

**Success response:**

**Status:** 200

**Response body:** `AddressDTO` (updated)

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Address not found or not owned by user | `"Address not found with id: 999."` |

---

### 8.5 DELETE /api/public/address/{id}

Delete an address. Only works on addresses owned by the logged-in user.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `id` | Long | The address ID |

**Success response:**

**Status:** 204 (No Content)

**Response body:** Empty

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Address not found or not owned by user | `"Address not found with id: 999."` |

---

## 9. Order Endpoints

### 9.1 POST /api/public/order/payment/{paymentMethod}

Place an order. Requires `ROLE_USER` or `ROLE_ADMIN`. This is a transactional endpoint.

**Path parameters:**

| Param | Type | Description |
|---|---|---|
| `paymentMethod` | String | Payment method (free text, e.g., `"CARD"`, `"CASH"`, `"UPI"`) |

**Request body:** `OrderRequestDTO` (JSON)

| Field | Type | Required | Validation |
|---|---|---|---|
| `addressId` | Long | Yes | `@NotNull` - must be an address owned by the logged-in user |
| `pgPaymentId` | String | No | Payment gateway payment ID |
| `pgStatus` | String | No | Payment gateway status |
| `pgResponseMessage` | String | No | Payment gateway response message |
| `pgName` | String | No | Payment gateway name |

**Request example:**

```json
{
  "addressId": 1,
  "pgPaymentId": "pg_12345",
  "pgStatus": "SUCCESS",
  "pgResponseMessage": "Payment completed",
  "pgName": "Stripe"
}
```

**Success response:**

**Status:** 201

**Response body:** `OrderDTO`

```json
{
  "id": 1,
  "email": "john@example.com",
  "orderItemsDTOList": [
    {
      "id": 1,
      "productDTO": {
        "id": 1,
        "productName": "iPhone 15",
        "description": "Apple smartphone 128GB",
        "image": "default.png",
        "quantity": 48,
        "price": 999.99,
        "discount": 10.0,
        "specialPrice": 899.991
      },
      "quantity": 2,
      "discount": 10.0,
      "price": 999.99
    }
  ],
  "date": "2026-07-14",
  "total": 1799.982,
  "status": "Order Accepted",
  "addressId": 1,
  "paymentDTO": {
    "id": 1,
    "paymentMethod": "CARD",
    "pgPaymentId": "pg_12345",
    "pgStatus": "SUCCESS",
    "pgResponseMessage": "Payment completed",
    "pgName": "Stripe"
  }
}
```

**What happens during order placement (server-side):**
1. Gets the logged-in user and their cart.
2. Validates the address exists and belongs to the user.
3. Checks the cart is not empty.
4. Checks all products have sufficient stock.
5. Creates an `Order` with status `"Order Accepted"` and today's date.
6. Creates a `Payment` record linked to the order.
7. Creates `OrderItem` records for each cart item (records price and discount at time of purchase).
8. Decrements product quantities in stock.
9. Clears the user's cart.
10. Returns the complete order.

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| 404 | Cart not found | `"Cart not found with email: john@example.com."` |
| 404 | Address not found | `"Address not found with id: 999."` |
| 400 | Address does not belong to user | `"Address does not belong to the logged-in user"` |
| 400 | Cart is empty | `"Cart is empty."` |
| 400 | Insufficient stock for a product | `"iPhone 15 has insufficient stock."` |

---

## 10. DTO Reference

### SignupDTO

| Field | Type | Required | Validation |
|---|---|---|---|
| `name` | String | Yes | `@NotBlank`, `@Size(min=2)` |
| `username` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `password` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `email` | String | Yes | `@Email`, `@NotBlank`, `@Size(min=11)` |
| `roles` | Set of String | No | Default: `{"ROLE_USER"}` |

### LoginRequest

| Field | Type | Required | Validation |
|---|---|---|---|
| `username` | String | Yes | `@NotBlank`, `@Size(min=3)` |
| `password` | String | Yes | `@NotBlank`, `@Size(min=3)` |

### LoginResponse

| Field | Type |
|---|---|
| `id` | Long |
| `token` | String (JWT) |
| `username` | String |
| `roles` | Set of String (e.g., `["ROLE_USER"]`) |
| `email` | String |

### UserDTO

| Field | Type |
|---|---|
| `name` | String |
| `username` | String |
| `email` | String |
| `roles` | Set of Role objects |
| `products` | Set of Product objects |
| `addresses` | List of Address objects |

### UserResponseDTO

| Field | Type |
|---|---|
| `id` | Long |
| `username` | String |
| `email` | String |
| `roles` | Collection of GrantedAuthority objects (each has `authority` field) |

### ProductDTO

| Field | Type | Notes |
|---|---|---|
| `id` | Long | Auto-generated on create |
| `productName` | String | Min 3 chars |
| `description` | String | Min 5 chars |
| `image` | String | Defaults to `"default.png"` |
| `quantity` | Integer | Must be positive |
| `price` | Double | Must be positive |
| `discount` | Double | Percentage (e.g., 10.0 for 10%) |
| `specialPrice` | Double | Calculated: `price * (1 - discount/100)` |

### ProductResponseDTO (Paginated)

| Field | Type |
|---|---|
| `content` | List of ProductDTO |
| `pageNum` | Integer |
| `pageSize` | Integer |
| `totalElements` | Long |
| `totalPages` | Integer |
| `lastPage` | boolean |

### CategoryDTO

| Field | Type | Validation |
|---|---|---|
| `id` | Long | - |
| `categoryName` | String | `@NotBlank`, `@Size(min=3, max=25)` |

### CategoryResponseDTO (Paginated)

| Field | Type |
|---|---|
| `content` | List of CategoryDTO |
| `pageNum` | Integer |
| `pageSize` | Integer |
| `totalElements` | Long |
| `totalPages` | Integer |
| `lastPage` | boolean |

### AddressDTO

| Field | Type | Validation |
|---|---|---|
| `id` | Long | - |
| `street` | String | `@NotBlank`, `@Size(min=3)` |
| `building` | String | `@NotBlank`, `@Size(min=3)` |
| `city` | String | `@NotBlank`, `@Size(min=3)` |
| `state` | String | `@NotBlank`, `@Size(min=3)` |
| `country` | String | `@NotBlank`, `@Size(min=3)` |
| `pincode` | String | `@NotBlank`, `@Size(min=5)` |

### CartDTO

| Field | Type |
|---|---|
| `id` | Long |
| `totalPrice` | Double |
| `cartItems` | List of CartItemDTO |

### CartItemDTO

| Field | Type |
|---|---|
| `id` | Long |
| `productDto` | ProductDTO |
| `quantity` | Integer |
| `availableQuantity` | Integer |

### OrderDTO

| Field | Type |
|---|---|
| `id` | Long |
| `email` | String |
| `orderItemsDTOList` | List of OrderItemDTO |
| `date` | LocalDate (format: `YYYY-MM-DD`) |
| `total` | Double |
| `status` | String (always `"Order Accepted"` on creation) |
| `addressId` | Long |
| `paymentDTO` | PaymentDTO |

### OrderItemDTO

| Field | Type |
|---|---|
| `id` | Long |
| `productDTO` | ProductDTO |
| `quantity` | Integer |
| `discount` | Double |
| `price` | Double |

### OrderRequestDTO

| Field | Type | Required |
|---|---|---|
| `addressId` | Long | Yes |
| `pgPaymentId` | String | No |
| `pgStatus` | String | No |
| `pgResponseMessage` | String | No |
| `pgName` | String | No |

### PaymentDTO

| Field | Type |
|---|---|
| `id` | Long |
| `paymentMethod` | String |
| `pgPaymentId` | String |
| `pgStatus` | String |
| `pgResponseMessage` | String |
| `pgName` | String |

---

## 11. Step-by-Step Backend Testing Workflow

Follow these steps in order to test the entire backend. Each step builds on the previous one.

### Step 1: Start the Backend

1. Make sure PostgreSQL is running on `localhost:5432` with database `ecomm`.
2. Run the Spring Boot application. It starts on port `8080`.
3. On startup, three roles are created in the database: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_SELLER`.

### Step 2: Register an Admin User

```
POST http://localhost:8080/api/open/signup
Content-Type: application/json

{
  "name": "Admin User",
  "username": "admin",
  "password": "admin123",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"]
}
```

Expected: 201 with user details. The user now has admin privileges.

### Step 3: Register a Regular User

```
POST http://localhost:8080/api/open/signup
Content-Type: application/json

{
  "name": "John Doe",
  "username": "john",
  "password": "password123",
  "email": "john@example.com"
}
```

Expected: 201. Default role is `ROLE_USER`.

### Step 4: Login as Admin

```
POST http://localhost:8080/api/open/signin
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Expected: 200 with `Set-Cookie` header containing the JWT. Save this cookie for all subsequent admin requests.

### Step 5: Create Categories (Admin)

Create a single category:

```
POST http://localhost:8080/api/admin/category
Content-Type: application/json
Cookie: requestJwtCookie=<token from step 4>

{
  "categoryName": "Electronics"
}
```

Expected: 201.

Create bulk categories:

```
POST http://localhost:8080/api/admin/categoryBulk
Content-Type: application/json
Cookie: requestJwtCookie=<token from step 4>

[
  { "categoryName": "Clothing" },
  { "categoryName": "Books" }
]
```

Expected: 200 with body `"Created"`.

### Step 6: Create Products (Admin)

```
POST http://localhost:8080/api/admin/categories/1/product
Content-Type: application/json
Cookie: requestJwtCookie=<token from step 4>

{
  "productName": "iPhone 15",
  "description": "Apple smartphone 128GB",
  "quantity": 50,
  "price": 999.99,
  "discount": 10.0
}
```

Expected: 201 with product details. `specialPrice` is calculated automatically.

### Step 7: Verify Public Endpoints

These work without any authentication or with any valid user token:

```
GET http://localhost:8080/api/public/products
GET http://localhost:8080/api/public/category
GET http://localhost:8080/api/public/products/keyword/iPhone
GET http://localhost:8080/api/public/categories/1/products
```

Expected: 200 with paginated results.

### Step 8: Login as Regular User

```
POST http://localhost:8080/api/open/signin
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

Save the cookie for subsequent user requests.

### Step 9: Create Address (User)

```
POST http://localhost:8080/api/public/address
Content-Type: application/json
Cookie: requestJwtCookie=<user token>

{
  "street": "123 Main Street",
  "building": "Apartment 4B",
  "city": "New York",
  "state": "NY",
  "country": "USA",
  "pincode": "10001"
}
```

Expected: 201 with address details including the assigned `id`.

### Step 10: Add Products to Cart (User)

```
POST http://localhost:8080/api/public/cart/add/product/1/quantity/2
Cookie: requestJwtCookie=<user token>
```

Expected: 201 with cart item details.

### Step 11: View Cart (User)

```
GET http://localhost:8080/api/public/cart/user
Cookie: requestJwtCookie=<user token>
```

Expected: 200 with cart containing the items and `totalPrice`.

### Step 12: Update Cart (User)

Increment quantity:

```
PUT http://localhost:8080/api/public/cart/update/product/1/operation/add
Cookie: requestJwtCookie=<user token>
```

Decrement quantity:

```
PUT http://localhost:8080/api/public/cart/update/product/1/operation/delete
Cookie: requestJwtCookie=<user token>
```

Expected: 200 with updated cart.

### Step 13: Place Order (User)

```
POST http://localhost:8080/api/public/order/payment/CARD
Content-Type: application/json
Cookie: requestJwtCookie=<user token>

{
  "addressId": 1,
  "pgPaymentId": "pg_12345",
  "pgStatus": "SUCCESS",
  "pgResponseMessage": "Payment completed",
  "pgName": "Stripe"
}
```

Expected: 201 with full order details. The cart is now cleared, and product stock is decremented.

### Step 14: Verify Product Stock Decremented

```
GET http://localhost:8080/api/public/products
Cookie: requestJwtCookie=<user token>
```

The product's `quantity` should be reduced by the ordered amount.

### Step 15: Update Product (Admin)

```
PUT http://localhost:8080/api/admin/products/1
Content-Type: application/json
Cookie: requestJwtCookie=<admin token>

{
  "productName": "iPhone 15 Pro",
  "description": "Apple smartphone 256GB",
  "quantity": 30,
  "price": 1199.99,
  "discount": 5.0
}
```

Expected: 200 with updated product.

### Step 16: Upload Product Image (Admin)

```
PUT http://localhost:8080/api/admin/products/1/image
Content-Type: multipart/form-data
Cookie: requestJwtCookie=<admin token>

Form field: image = <file>
```

Expected: 200 with product details showing new image filename.

### Step 17: Update Category (Admin)

```
PUT http://localhost:8080/api/admin/category
Content-Type: application/json
Cookie: requestJwtCookie=<admin token>

{
  "id": 1,
  "categoryName": "Electronics & Gadgets"
}
```

Expected: 200 with body `"Updated!"`.

### Step 18: Delete Product (Admin)

```
DELETE http://localhost:8080/api/admin/products/1
Cookie: requestJwtCookie=<admin token>
```

Expected: 204 No Content. Product is soft-deleted (isActive = false).

### Step 19: Delete Category (Admin)

```
DELETE http://localhost:8080/api/admin/category/2
Cookie: requestJwtCookie=<admin token>
```

Expected: 204. Only works if the category has no products.

### Step 20: Test Error Cases

- Try signing up with an existing username: expect 400.
- Try accessing `/api/public/products` without authentication: expect 401.
- Try accessing `/api/admin/category` with a regular user token: expect 401.
- Try adding a product with a name shorter than 3 characters: expect validation error.
- Try deleting a category that has products: expect 400.
- Try placing an order with an empty cart: expect 400.
- Try placing an order with an address that belongs to another user: expect 400.

### Step 21: Logout

```
POST http://localhost:8080/api/open/signout
Cookie: requestJwtCookie=<token>
```

Expected: 200 with cookie cleared. Subsequent authenticated requests should fail.

### Step 22: Verify Admin Can View All Carts

Login as admin again, then:

```
GET http://localhost:8080/api/admin/carts
Cookie: requestJwtCookie=<admin token>
```

Expected: 200 with a list of all user carts.

---

## Quick Reference: All Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/open/signup` | Public | Register new user |
| POST | `/api/open/signin` | Public | Login, get JWT cookie |
| POST | `/api/open/signout` | Public | Logout, clear cookie |
| GET | `/api/open/username` | Public | Get current username |
| GET | `/api/open/user` | Public | Get current user details |
| GET | `/api/public/products` | USER/ADMIN | List all products (paginated) |
| GET | `/api/public/categories/{categoryId}/products` | USER/ADMIN | List products by category |
| GET | `/api/public/products/keyword/{keyword}` | USER/ADMIN | Search products by keyword |
| POST | `/api/admin/categories/{categoryId}/product` | ADMIN | Create product |
| PUT | `/api/admin/products/{productId}` | ADMIN | Update product |
| PUT | `/api/admin/products/{productId}/image` | ADMIN | Update product image |
| DELETE | `/api/admin/products/{productId}` | ADMIN | Delete product |
| GET | `/api/public/category` | USER/ADMIN | List all categories (paginated) |
| POST | `/api/admin/category` | ADMIN | Create category |
| POST | `/api/admin/categoryBulk` | ADMIN | Create multiple categories |
| PUT | `/api/admin/category` | ADMIN | Update category |
| DELETE | `/api/admin/category/{categoryId}` | ADMIN | Delete category |
| POST | `/api/public/cart/add/product/{productId}/quantity/{quantity}` | USER/ADMIN | Add to cart |
| GET | `/api/public/cart/user` | USER/ADMIN | Get user cart |
| PUT | `/api/public/cart/update/product/{productId}/operation/{operation}` | USER/ADMIN | Update cart quantity |
| DELETE | `/api/public/cart/delete/product/{productId}` | USER/ADMIN | Remove from cart |
| GET | `/api/admin/carts` | ADMIN | Get all carts |
| POST | `/api/public/address` | USER/ADMIN | Create address |
| GET | `/api/public/address` | USER/ADMIN | Get all user addresses |
| GET | `/api/public/address/{id}` | USER/ADMIN | Get address by ID |
| PUT | `/api/public/address/{id}/update` | USER/ADMIN | Update address |
| DELETE | `/api/public/address/{id}` | USER/ADMIN | Delete address |
| POST | `/api/public/order/payment/{paymentMethod}` | USER/ADMIN | Place order |

---

## Notes for Frontend Developers

1. **Cookie-based auth:** The JWT is stored in an HTTP-only cookie. The browser sends it automatically. You do not need to manually attach tokens to requests in browser-based apps.

2. **CORS:** CORS is not configured yet. If your frontend runs on a different origin (e.g., `localhost:3000`), you will need the backend to add CORS configuration, or use a proxy during development.

3. **Pagination:** Product and category list endpoints are paginated. The response includes `pageNum`, `pageSize`, `totalElements`, `totalPages`, and `lastPage` to help you build pagination UIs.

4. **Image URLs:** Product images are saved as files in the `images/` directory. The `image` field in `ProductDTO` contains just the filename (e.g., `"default.png"` or `"uuid.jpg"`). Your frontend needs to construct the full URL yourself.

5. **Special price:** The `specialPrice` field is calculated as `price * (1 - discount/100)`. Use this for display purposes.

6. **Order status:** Orders are always created with status `"Order Accepted"`. There is no order status update endpoint yet.

7. **Cart auto-creation:** A cart is created automatically for a user when they add their first item.

8. **Address ownership:** Users can only see and modify their own addresses. The backend enforces this.

9. **Category deletion:** Categories with products cannot be deleted. You must delete or move all products first.

10. **Product deletion:** Products are soft-deleted (`isActive = false`). They remain in the database but may not appear in public listings.
