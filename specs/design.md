# Design Document - Cignalottu

## Overview

Cignalottu is an enterprise-level barber e-commerce platform built with a modern, scalable architecture. The system uses Spring Boot for the backend REST API, Angular for the frontend SPA, H2 in-memory database for data persistence, and Tailwind CSS for styling. The platform supports multi-role user access (Customer, Barber, Representative, Admin) with role-based pricing, OAuth2 and traditional authentication, product catalog management, shopping cart operations, order processing, and training course booking with different participation types.

The architecture follows clean architecture principles with clear separation between presentation, business logic, and data access layers. Security is enforced through JWT-based authentication and role-based authorization at both the API and service layers.

## Architecture

### System Architecture

The system follows a three-tier architecture:

1. **Presentation Layer (Angular Frontend)**
   - Single Page Application (SPA)
   - Responsive UI with Tailwind CSS
   - HTTP client for API communication
   - Route guards for authorization
   - State management for cart and user session

2. **Application Layer (Spring Boot Backend)**
   - RESTful API endpoints
   - JWT authentication filter
   - Service layer for business logic
   - Repository layer for data access
   - DTO pattern for data transfer

3. **Data Layer (H2 Database)**
   - In-memory relational database
   - JPA entities with relationships
   - Transaction management
   - Connection pooling

### Technology Stack

**Backend:**
- Spring Boot 3.x
- Spring Security with OAuth2
- Spring Data JPA
- H2 Database
- JWT (JSON Web Tokens)
- Maven for dependency management

**Frontend:**
- Angular 17+
- TypeScript
- Tailwind CSS
- RxJS for reactive programming
- Angular Router for navigation
- HttpClient for API calls

**Authentication:**
- OAuth2 with Google
- JWT token-based authentication
- BCrypt password hashing

### Security Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Angular Frontend                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ Auth Guard   │  │ HTTP         │  │ Token        │       │
│  │              │  │ Interceptor  │  │ Storage      │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTPS + JWT
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Spring Boot Backend                     │
│  ┌──────────────────────────────────────────────────────┐   │
│  │            JWT Authentication Filter                 │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ OAuth2       │  │ Security     │  │ Role-Based   │       │
│  │ Integration  │  │ Config       │  │ Authorization│       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

## Components and Interfaces

### Backend Components

#### 1. Authentication Module

**AuthController**
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Authenticate user
- `POST /api/v1/auth/oauth2/google` - OAuth2 Google login
- `POST /api/v1/auth/logout` - Invalidate session
- `POST /api/v1/auth/refresh` - Refresh JWT token

**AuthService**
- `registerUser(RegisterRequest): UserDTO` - Create new user account
- `authenticateUser(LoginRequest): AuthResponse` - Validate credentials and generate JWT
- `authenticateOAuth2(OAuth2Request): AuthResponse` - Process OAuth2 authentication
- `validateToken(String token): boolean` - Verify JWT validity
- `refreshToken(String token): AuthResponse` - Generate new JWT from refresh token

**JwtTokenProvider**
- `generateToken(UserDetails): String` - Create JWT token
- `getUsernameFromToken(String): String` - Extract username from JWT
- `validateToken(String): boolean` - Verify token signature and expiration
- `getRolesFromToken(String): List<Role>` - Extract user roles

#### 2. User Management Module

**UserController**
- `GET /api/v1/users/profile` - Get current user profile
- `PUT /api/v1/users/profile` - Update user profile
- `PUT /api/v1/users/password` - Change password
- `GET /api/v1/users/{id}` - Get user by ID (Admin only)

**UserService**
- `getUserProfile(Long userId): UserDTO` - Retrieve user details
- `updateProfile(Long userId, UpdateProfileRequest): UserDTO` - Modify user information
- `changePassword(Long userId, ChangePasswordRequest): void` - Update password with validation
- `getUsersByRole(Role): List<UserDTO>` - Query users by role

**User Entity**
```java
@Entity
class User {
    Long id;
    String email;
    String password;
    String firstName;
    String lastName;
    Role role; // CUSTOMER, BARBER, REPRESENTATIVE, ADMIN
    AuthProvider authProvider; // LOCAL, GOOGLE
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean enabled;
}
```

#### 3. Product Management Module

**ProductController**
- `GET /api/v1/products` - List products with pagination and filters
- `GET /api/v1/products/{id}` - Get product details
- `POST /api/v1/products` - Create product (Admin only)
- `PUT /api/v1/products/{id}` - Update product (Admin only)
- `DELETE /api/v1/products/{id}` - Soft delete product (Admin only)
- `GET /api/v1/products/search` - Search products by keyword

**ProductService**
- `getAllProducts(Pageable, ProductFilter): Page<ProductDTO>` - Retrieve filtered product list
- `getProductById(Long): ProductDTO` - Get single product with role-based pricing
- `createProduct(CreateProductRequest): ProductDTO` - Add new product
- `updateProduct(Long, UpdateProductRequest): ProductDTO` - Modify product details
- `deleteProduct(Long): void` - Soft delete product
- `searchProducts(String keyword): List<ProductDTO>` - Full-text search
- `getPriceForRole(Product, Role): BigDecimal` - Calculate role-specific pricing

**Product Entity**
```java
@Entity
class Product {
    Long id;
    String name;
    String description;
    String category;
    BigDecimal basePrice;
    BigDecimal barberPrice;
    BigDecimal representativePrice;
    Integer stockQuantity;
    String imageUrl;
    boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```

#### 4. Shopping Cart Module

**CartController**
- `GET /api/v1/cart` - Get current user's cart
- `POST /api/v1/cart/items` - Add item to cart
- `PUT /api/v1/cart/items/{itemId}` - Update item quantity
- `DELETE /api/v1/cart/items/{itemId}` - Remove item from cart
- `DELETE /api/v1/cart` - Clear entire cart

**CartService**
- `getCart(Long userId): CartDTO` - Retrieve user's cart with calculated totals
- `addItem(Long userId, AddCartItemRequest): CartDTO` - Add product to cart
- `updateItemQuantity(Long userId, Long itemId, Integer quantity): CartDTO` - Modify quantity
- `removeItem(Long userId, Long itemId): CartDTO` - Delete cart item
- `clearCart(Long userId): void` - Empty cart
- `calculateTotal(Cart, Role): BigDecimal` - Compute cart total with role pricing

**Cart Entity**
```java
@Entity
class Cart {
    Long id;
    User user;
    List<CartItem> items;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

@Entity
class CartItem {
    Long id;
    Cart cart;
    Product product;
    Integer quantity;
    BigDecimal priceAtAdd; // Snapshot of price when added
}
```

#### 5. Order Management Module

**OrderController**
- `POST /api/v1/orders` - Create order from cart
- `GET /api/v1/orders` - Get user's order history
- `GET /api/v1/orders/{id}` - Get order details
- `PUT /api/v1/orders/{id}/status` - Update order status (Admin only)
- `POST /api/v1/orders/{id}/cancel` - Cancel order

**OrderService**
- `createOrder(Long userId, CreateOrderRequest): OrderDTO` - Process checkout
- `getOrderHistory(Long userId, Pageable): Page<OrderDTO>` - Retrieve user orders
- `getOrderById(Long orderId): OrderDTO` - Get order details
- `updateOrderStatus(Long orderId, OrderStatus): OrderDTO` - Change order status
- `cancelOrder(Long orderId): OrderDTO` - Cancel and restore inventory
- `processPayment(Order, PaymentRequest): PaymentResult` - Handle payment processing

**Order Entity**
```java
@Entity
class Order {
    Long id;
    User user;
    List<OrderItem> items;
    BigDecimal totalAmount;
    OrderStatus status; // PENDING, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    String paymentId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

@Entity
class OrderItem {
    Long id;
    Order order;
    Product product;
    Integer quantity;
    BigDecimal priceAtPurchase;
}
```

#### 6. Course Management Module

**CourseController**
- `GET /api/v1/courses` - List all courses
- `GET /api/v1/courses/{id}` - Get course details
- `POST /api/v1/courses` - Create course (Admin only)
- `PUT /api/v1/courses/{id}` - Update course (Admin only)
- `DELETE /api/v1/courses/{id}` - Cancel course (Admin only)
- `GET /api/v1/courses/{id}/enrollments` - Get course enrollments (Admin only)

**CourseService**
- `getAllCourses(Pageable): Page<CourseDTO>` - Retrieve course catalog
- `getCourseById(Long): CourseDTO` - Get course with availability
- `createCourse(CreateCourseRequest): CourseDTO` - Add new course
- `updateCourse(Long, UpdateCourseRequest): CourseDTO` - Modify course details
- `cancelCourse(Long): void` - Cancel course and process refunds
- `getEnrollments(Long courseId): List<BookingDTO>` - Get course participants
- `checkAvailability(Long courseId): Integer` - Get remaining spots

**Course Entity**
```java
@Entity
class Course {
    Long id;
    String title;
    String description;
    LocalDateTime startDate;
    Integer durationHours;
    Integer capacity;
    Integer enrolledCount;
    BigDecimal spectatorPrice;
    BigDecimal handsOnPrice;
    CourseStatus status; // SCHEDULED, ONGOING, COMPLETED, CANCELLED
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```

#### 7. Booking Module

**BookingController**
- `POST /api/v1/bookings` - Create course booking
- `GET /api/v1/bookings` - Get user's bookings
- `GET /api/v1/bookings/{id}` - Get booking details
- `DELETE /api/v1/bookings/{id}` - Cancel booking

**BookingService**
- `createBooking(Long userId, CreateBookingRequest): BookingDTO` - Reserve course spot
- `getUserBookings(Long userId): List<BookingDTO>` - Get user's course bookings
- `getBookingById(Long): BookingDTO` - Retrieve booking details
- `cancelBooking(Long bookingId): void` - Cancel and restore capacity
- `processBookingPayment(Booking, PaymentRequest): PaymentResult` - Handle payment

**Booking Entity**
```java
@Entity
class Booking {
    Long id;
    User user;
    Course course;
    ParticipationType participationType; // SPECTATOR, HANDS_ON
    BigDecimal amountPaid;
    BookingStatus status; // CONFIRMED, CANCELLED, COMPLETED
    String paymentId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```

#### 8. Representative Dashboard Module

**RepresentativeController**
- `GET /api/v1/representative/dashboard` - Get sales dashboard
- `GET /api/v1/representative/customers` - List associated customers
- `GET /api/v1/representative/sales` - Get sales history
- `POST /api/v1/representative/customers/{id}/notes` - Add customer note

**RepresentativeService**
- `getDashboard(Long repId): DashboardDTO` - Calculate sales metrics
- `getCustomers(Long repId): List<CustomerDTO>` - Get customer list
- `getSalesHistory(Long repId, Pageable): Page<OrderDTO>` - Retrieve sales
- `addCustomerNote(Long repId, Long customerId, String note): void` - Add note

### Frontend Components

#### 1. Authentication Components

**LoginComponent**
- Email/password login form
- OAuth2 Google button
- Form validation
- Error handling
- Redirect after successful login

**RegisterComponent**
- Registration form with role selection
- Password strength indicator
- Email validation
- Terms acceptance
- OAuth2 registration option

**AuthService (Frontend)**
- `login(credentials): Observable<AuthResponse>` - Authenticate user
- `register(userData): Observable<UserDTO>` - Create account
- `loginWithGoogle(): Observable<AuthResponse>` - OAuth2 flow
- `logout(): void` - Clear session
- `isAuthenticated(): boolean` - Check auth status
- `getCurrentUser(): UserDTO` - Get logged-in user
- `getToken(): string` - Retrieve JWT

#### 2. Product Components

**ProductListComponent**
- Product grid/list view
- Search bar
- Category filters
- Price range filter
- Sort options
- Pagination
- Add to cart button

**ProductDetailComponent**
- Product image gallery
- Detailed description
- Role-based pricing display
- Stock availability
- Quantity selector
- Add to cart action
- Related products

**ProductService (Frontend)**
- `getProducts(filters, page): Observable<Page<Product>>` - Fetch products
- `getProductById(id): Observable<Product>` - Get details
- `searchProducts(keyword): Observable<Product[]>` - Search

#### 3. Cart Components

**CartComponent**
- Cart items list
- Quantity controls
- Remove item button
- Price calculations
- Proceed to checkout button
- Empty cart state

**CartService (Frontend)**
- `getCart(): Observable<Cart>` - Fetch cart
- `addItem(productId, quantity): Observable<Cart>` - Add to cart
- `updateQuantity(itemId, quantity): Observable<Cart>` - Update item
- `removeItem(itemId): Observable<Cart>` - Remove item
- `clearCart(): Observable<void>` - Empty cart
- Cart state management with BehaviorSubject

#### 4. Order Components

**CheckoutComponent**
- Order summary
- Payment form
- Billing information
- Order confirmation

**OrderHistoryComponent**
- Orders list with filters
- Order status badges
- View details link
- Pagination

**OrderDetailComponent**
- Order information
- Items list
- Status timeline
- Invoice download

#### 5. Course Components

**CourseListComponent**
- Course cards with details
- Date filtering
- Capacity indicators
- Book now button

**CourseDetailComponent**
- Course information
- Participation type selector
- Pricing display
- Booking form
- Availability status

**BookingHistoryComponent**
- User's bookings list
- Course details
- Participation type
- Cancel booking option

#### 6. Dashboard Components

**UserDashboardComponent**
- Profile summary
- Recent orders
- Upcoming bookings
- Quick actions

**RepresentativeDashboardComponent**
- Sales metrics cards
- Revenue charts
- Customer list
- Recent sales table

**AdminDashboardComponent**
- System statistics
- Recent orders
- Low stock alerts
- User management links

## Data Models

### Entity Relationship Diagram

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│    User     │1       *│    Cart     │1       *│  CartItem   │
│─────────────│◄────────│─────────────│◄────────│─────────────│
│ id          │         │ id          │         │ id          │
│ email       │         │ user_id     │         │ cart_id     │
│ password    │         │ created_at  │         │ product_id  │
│ role        │         │ updated_at  │         │ quantity    │
│ ...         │         └─────────────┘         │ price       │
└─────────────┘                                 └─────────────┘
       │1                                              │*
       │                                               │
       │                                               │
       │*                                              │
┌─────────────┐         ┌─────────────┐               │
│    Order    │1       *│  OrderItem  │               │
│─────────────│◄────────│─────────────│               │
│ id          │         │ id          │               │
│ user_id     │         │ order_id    │               │
│ total       │         │ product_id  │◄──────────────┘
│ status      │         │ quantity    │
│ payment_id  │         │ price       │
│ ...         │         └─────────────┘
└─────────────┘                │*
       │1                      │
       │                       │
       │                       │1
       │*                ┌─────────────┐
┌─────────────┐         │   Product   │
│   Booking   │         │─────────────│
│─────────────│         │ id          │
│ id          │         │ name        │
│ user_id     │         │ description │
│ course_id   │         │ base_price  │
│ type        │         │ stock       │
│ amount      │         │ category    │
│ status      │         │ ...         │
│ payment_id  │         └─────────────┘
└─────────────┘
       │*
       │
       │
       │1
┌─────────────┐
│   Course    │
│─────────────│
│ id          │
│ title       │
│ description │
│ start_date  │
│ capacity    │
│ enrolled    │
│ prices      │
│ ...         │
└─────────────┘
```

### Data Transfer Objects (DTOs)

**AuthResponse**
```java
{
    String token;
    String refreshToken;
    String tokenType = "Bearer";
    Long expiresIn;
    UserDTO user;
}
```

**UserDTO**
```java
{
    Long id;
    String email;
    String firstName;
    String lastName;
    Role role;
    LocalDateTime createdAt;
}
```

**ProductDTO**
```java
{
    Long id;
    String name;
    String description;
    String category;
    BigDecimal price; // Role-specific price
    Integer stockQuantity;
    String imageUrl;
    boolean available;
}
```

**CartDTO**
```java
{
    Long id;
    List<CartItemDTO> items;
    BigDecimal subtotal;
    BigDecimal tax;
    BigDecimal total;
    Integer itemCount;
}
```

**OrderDTO**
```java
{
    Long id;
    List<OrderItemDTO> items;
    BigDecimal totalAmount;
    OrderStatus status;
    LocalDateTime orderDate;
    String paymentId;
}
```

**CourseDTO**
```java
{
    Long id;
    String title;
    String description;
    LocalDateTime startDate;
    Integer durationHours;
    Integer capacity;
    Integer availableSpots;
    BigDecimal spectatorPrice;
    BigDecimal handsOnPrice;
    CourseStatus status;
}
```

**BookingDTO**
```java
{
    Long id;
    CourseDTO course;
    ParticipationType participationType;
    BigDecimal amountPaid;
    BookingStatus status;
    LocalDateTime bookingDate;
}
```

### Database Schema

**users**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) NOT NULL,
    auth_provider VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**products**
```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    base_price DECIMAL(10,2) NOT NULL,
    barber_price DECIMAL(10,2),
    representative_price DECIMAL(10,2),
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_active (active)
);
```

**carts**
```sql
CREATE TABLE carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_user_cart (user_id)
);
```

**cart_items**
```sql
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price_at_add DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY uk_cart_product (cart_id, product_id)
);
```

**orders**
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_orders (user_id),
    INDEX idx_status (status)
);
```

**order_items**
```sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price_at_purchase DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

**courses**
```sql
CREATE TABLE courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    duration_hours INTEGER NOT NULL,
    capacity INTEGER NOT NULL,
    enrolled_count INTEGER DEFAULT 0,
    spectator_price DECIMAL(10,2) NOT NULL,
    hands_on_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_start_date (start_date),
    INDEX idx_status (status)
);
```

**bookings**
```sql
CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    participation_type VARCHAR(50) NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    INDEX idx_user_bookings (user_id),
    INDEX idx_course_bookings (course_id)
);
```

**audit_logs**
```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_logs (user_id),
    INDEX idx_created_at (created_at)
);
```


## Correctness Properties

A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.

### Property 1: Password Complexity Enforcement

*For any* registration or password change request, passwords that do not meet complexity requirements (minimum 8 characters, at least one uppercase, one ith an expiration time of 24 hours.

**Validates: Requirements 1.4**

### Property 4: Duplicate Email Prevention

*For any* registration attempt using an email that already exists in the system, the registration should be rejected with an appropriate error message.

**Validates: Requirements 1.6**

### Property 5: Role-Based Pricing

*For any* product and user role combination, the system should return the correct price tier: retail price for Customers, professional price for Barbers, and distributor price for Representatives.

**Validates: Requirements 2.2, 2.3, 2.4**

### Property 6: Authorization Enforcement

*For any* non-Admin user attempting to access admin-only endpoints, the system should deny access and return a 403 authorization error.

**Validates: Requirements 2.5**

### Property 7: Role Persistence

*For any* user, logging out and logging back in should preserve their assigned role unchanged.

**Validates: Requirements 2.6**

### Property 8: Product Search Matching

*For any* search keyword, all returned products should contain the keyword in either their name or description (case-insensitive).

**Validates: Requirements 3.2**

### Property 9: Category Filtering

*For any* selected category, all returned products should belong to that category and no products from other categories should be included.

**Validates: Requirements 3.3**

### Property 10: Price-Based Sorting

*For any* user role, when products are sorted by price, they should be ordered according to that role's specific pricing tier in ascending or descending order.

**Validates: Requirements 3.4**

### Property 11: Cart Addition

*For any* product and quantity, adding the item to a cart should result in the cart containing that product with the specified quantity.

**Validates: Requirements 4.1**

### Property 12: Cart Total Calculation

*For any* cart with items, the total should equal the sum of (quantity × role-specific price) for all items in the cart.

**Validates: Requirements 4.2**

### Property 13: Cart Item Removal

*For any* cart item, removing it should result in the item no longer appearing in the cart and the total being recalculated without that item.

**Validates: Requirements 4.3**

### Property 14: Cart Persistence Round Trip

*For any* cart with items, logging out and logging back in should preserve all cart items with their quantities unchanged.

**Validates: Requirements 4.5**

### Property 15: Stock Quantity Limiting

*For any* attempt to add a product quantity to cart that exceeds available stock, the system should limit the quantity to the available stock amount.

**Validates: Requirements 4.6**

### Property 16: Inventory Decrement on Order

*For any* completed order, each product's inventory should be decremented by exactly the ordered quantity.

**Validates: Requirements 5.3**

### Property 17: Unique Order Identifiers

*For any* set of orders created in the system, all order IDs should be unique with no duplicates.

**Validates: Requirements 5.6**

### Property 18: Course Capacity Decrement

*For any* completed booking, the course's available capacity should be decremented by exactly one.

**Validates: Requirements 7.3**

### Property 19: Full Course Booking Prevention

*For any* course at full capacity (enrolled count equals capacity), attempts to create new bookings should be rejected with an appropriate error message.

**Validates: Requirements 7.4**

### Property 20: Booking Cancellation Round Trip

*For any* course, creating a booking and then canceling it should restore the available capacity to its original value.

**Validates: Requirements 7.6**

### Property 21: Profile Role Immutability

*For any* user attempting to update their profile with a different role, the system should reject the change and maintain the original role assignment.

**Validates: Requirements 9.5**

### Property 22: Email Uniqueness on Update

*For any* user attempting to update their email to one already registered to another account, the system should reject the update with an appropriate error message.

**Validates: Requirements 9.4**

### Property 23: Soft Delete Preservation

*For any* product deleted by an admin, the product should be marked as inactive in the database rather than being removed, and should no longer appear in active product listings.

**Validates: Requirements 10.5**

### Property 24: Non-Negative Stock Validation

*For any* stock update operation, negative quantities should be rejected with a validation error.

**Validates: Requirements 10.4**

### Property 25: Capacity Reduction Validation

*For any* course with existing enrollments, attempts to reduce capacity below the current enrollment count should be rejected.

**Validates: Requirements 11.5**

### Property 26: Multiple Filter Combination

*For any* combination of filters (category, price range, availability), all returned products should match ALL applied filter criteria (AND logic).

**Validates: Requirements 12.2, 12.3**

### Property 27: Email Format Validation

*For any* string submitted as an email address, only strings conforming to standard email format (local@domain) should be accepted.

**Validates: Requirements 13.2**

### Property 28: Monetary Amount Validation

*For any* monetary value submitted, only non-negative numbers with maximum two decimal places should be accepted.

**Validates: Requirements 13.3**

### Property 29: Required Field Validation

*For any* create or update operation, requests missing required fields should be rejected with a 400 status code and specific error messages indicating which fields are missing.

**Validates: Requirements 13.6**

### Property 30: Expired Token Rejection

*For any* JWT token past its expiration time, API requests using that token should be rejected with a 401 unauthorized error.

**Validates: Requirements 14.2**

### Property 31: Token Invalidation on Logout

*For any* user session, after logout, the JWT token should no longer be valid for API requests.

**Validates: Requirements 14.3**

### Property 32: Rate Limiting After Failed Logins

*For any* IP address, after 5 failed login attempts within 15 minutes, subsequent login attempts should be rate-limited or blocked temporarily.

**Validates: Requirements 14.6**

### Property 33: Credit Card Storage Prevention

*For any* payment transaction, the database should never contain complete credit card numbers (only tokenized references or last 4 digits).

**Validates: Requirements 15.4**

### Property 34: HTTP Status Code Correctness

*For any* API request, the response should return appropriate HTTP status codes: 200 for success, 201 for creation, 400 for validation errors, 401 for authentication failures, 403 for authorization failures, 404 for not found, 500 for server errors.

**Validates: Requirements 17.2**

### Property 35: JSON Response Format

*For any* API response, the content should be valid JSON with a consistent structure including data payload and optional error messages.

**Validates: Requirements 17.3**

### Property 36: Pagination Limit

*For any* paginated list endpoint, the number of items returned should not exceed 50 items per page.

**Validates: Requirements 17.4**

### Property 37: Out of Stock Marking

*For any* product, when stock quantity reaches zero, the product should be automatically marked as out of stock.

**Validates: Requirements 18.1**

### Property 38: Atomic Inventory Decrement

*For any* concurrent order operations on the same product, the final inventory should correctly reflect all orders without race conditions (inventory = initial - sum of all ordered quantities).

**Validates: Requirements 18.2**

### Property 39: Order Cancellation Inventory Restoration

*For any* order, canceling it should restore all product quantities back to inventory (inventory after cancel = inventory before order + ordered quantities).

**Validates: Requirements 18.3**

### Property 40: Audit Log Creation

*For any* state-changing operation (create, update, delete), an audit log entry should be created with user ID, timestamp, action type, and affected entity information.

**Validates: Requirements 20.1**

### Property 41: Audit Log Value Tracking

*For any* admin modification of sensitive data, the audit log should contain both the old value and the new value.

**Validates: Requirements 20.2**

### Property 42: Audit Log Protection

*For any* non-Admin user, attempts to modify or delete audit log entries should be rejected with an authorization error.

**Validates: Requirements 20.6**

## Error Handling

### Error Response Format

All API errors follow a consistent JSON structure:

```json
{
    "timestamp": "2024-01-15T10:30:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "errors": [
        {
            "field": "email",
            "message": "Email format is invalid"
        },
        {
            "field": "password",
            "message": "Password must be at least 8 characters"
        }
    ],
    "path": "/api/v1/auth/register"
}
```

### Error Categories

**1. Validation Errors (400 Bad Request)**
- Invalid input format
- Missing required fields
- Constraint violations
- Business rule violations

**2. Authentication Errors (401 Unauthorized)**
- Invalid credentials
- Expired JWT token
- Missing authentication token
- Invalid OAuth2 token

**3. Authorization Errors (403 Forbidden)**
- Insufficient permissions
- Role-based access denial
- Resource ownership violations

**4. Not Found Errors (404 Not Found)**
- Resource does not exist
- Invalid resource ID
- Deleted/inactive resources

**5. Conflict Errors (409 Conflict)**
- Duplicate email registration
- Concurrent modification conflicts
- Stock availability conflicts

**6. Server Errors (500 Internal Server Error)**
- Unexpected exceptions
- Database connection failures
- External service failures

### Exception Handling Strategy

**Global Exception Handler**
- Centralized exception handling using `@ControllerAdvice`
- Consistent error response format
- Logging of all exceptions
- Sanitization of error messages (no internal details exposed)

**Custom Exceptions**
```java
// Business logic exceptions
class BusinessException extends RuntimeException
class ResourceNotFoundException extends BusinessException
class DuplicateResourceException extends BusinessException
class InsufficientStockException extends BusinessException
class InvalidCredentialsException extends BusinessException
class UnauthorizedException extends BusinessException

// Validation exceptions
class ValidationException extends RuntimeException
class InvalidEmailFormatException extends ValidationException
class PasswordComplexityException extends ValidationException
```

**Transaction Rollback**
- All service methods annotated with `@Transactional`
- Automatic rollback on runtime exceptions
- Explicit rollback for business rule violations
- Inventory operations use pessimistic locking to prevent race conditions

**Retry Logic**
- Payment processing: 3 retries with exponential backoff
- External API calls: 2 retries with 1-second delay
- Database deadlock: automatic retry by Spring

## Testing Strategy

### Dual Testing Approach

The system requires both unit testing and property-based testing for comprehensive coverage:

**Unit Tests:**
- Specific examples demonstrating correct behavior
- Edge cases and boundary conditions
- Error condition handling
- Integration points between components
- Mock external dependencies (payment processor, OAuth2 provider)

**Property-Based Tests:**
- Universal properties that hold for all inputs
- Comprehensive input coverage through randomization
- Minimum 100 iterations per property test
- Each test references its design document property

### Property-Based Testing Configuration

**Library:** We will use **JUnit-Quickcheck** for Java property-based testing.

**Configuration:**
- Minimum 100 iterations per property test
- Custom generators for domain objects (User, Product, Order, Course, etc.)
- Shrinking enabled to find minimal failing examples
- Seed-based reproducibility for failed tests

**Test Tagging Format:**
Each property test must include a comment tag:
```java
// Feature: cignalottu, Property 5: Role-Based Pricing
@Property
public void testRoleBasedPricing(@ForAll User user, @ForAll Product product) {
    // Test implementation
}
```

### Test Coverage Requirements

**Backend Unit Tests:**
- Controller layer: 80% coverage
- Service layer: 90% coverage
- Repository layer: 70% coverage (mostly integration tests)
- Utility classes: 95% coverage

**Backend Property Tests:**
- All 42 correctness properties implemented
- Each property maps to specific requirements
- Generators for all domain entities
- Custom strategies for complex scenarios

**Frontend Unit Tests:**
- Component logic: 80% coverage
- Services: 90% coverage
- Guards and interceptors: 95% coverage
- Pipes and directives: 85% coverage

**Integration Tests:**
- API endpoint tests with TestRestTemplate
- Database integration with @DataJpaTest
- Security integration with @WithMockUser
- OAuth2 flow testing with mock provider

### Test Data Management

**Test Database:**
- H2 in-memory database for tests
- Schema auto-creation from entities
- Test data builders for complex objects
- Database reset between tests

**Test Fixtures:**
- Builder pattern for entity creation
- Factory methods for common scenarios
- Faker library for realistic test data
- Predefined test users for each role

**Property Test Generators:**
```java
// Example generators
@Generator
class UserGenerator {
    @Provide
    User validUser() {
        return User.builder()
            .email(validEmail())
            .password(validPassword())
            .role(randomRole())
            .build();
    }
}

@Generator
class ProductGenerator {
    @Provide
    Product validProduct() {
        return Product.builder()
            .name(randomString(5, 50))
            .basePrice(randomPrice())
            .stockQuantity(randomInt(0, 1000))
            .build();
    }
}
```

### Testing Workflow

1. **Unit Tests First:** Write unit tests for specific examples and edge cases
2. **Property Tests Second:** Implement property-based tests for universal properties
3. **Integration Tests Third:** Test component interactions and API contracts
4. **Manual Testing:** UI/UX validation, OAuth2 flow, payment integration
5. **Performance Testing:** Load testing with JMeter for critical endpoints

### Continuous Integration

- All tests run on every commit
- Property tests run with fixed seed for reproducibility
- Failed property tests report minimal failing example
- Code coverage reports generated and tracked
- Integration tests run in separate pipeline stage
