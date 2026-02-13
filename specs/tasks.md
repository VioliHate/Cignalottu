# Implementation Plan: Cignalottu

## Overview

This implementation plan breaks down the Cignalottu barber e-commerce platform into discrete, incremental coding tasks. The approach follows a bottom-up strategy: starting with foundational infrastructure (database, security), then building core business logic (products, cart, orders), followed by advanced features (courses, bookings, representative dashboard), and finally the Angular frontend. Each task builds on previous work, with property-based tests integrated throughout to validate correctness early.

## Tasks

- [ ] 1. Set up project infrastructure and database schema
  - Initialize Spring Boot project with Maven dependencies (Spring Web, Spring Security, Spring Data JPA, H2, JWT, OAuth2, JUnit-Quickcheck)
  - Initialize Angular project with Tailwind CSS
  - Create H2 database schema with all tables (users, products, carts, cart_items, orders, order_items, courses, bookings, audit_logs)
  - Configure application.properties for H2, JPA, and security settings
  - Set up CORS configuration for Angular frontend
  - _Requirements: 1.1, 1.2, 13.1, 17.1, 17.5_

- [ ] 2. Implement authentication and user management
  - [ ] 2.1 Create User entity, repository, and DTOs
    - Implement User entity with role enum (CUSTOMER, BARBER, REPRESENTATIVE, ADMIN)
    - Create UserRepository with JPA
    - Create UserDTO, RegisterRequest, LoginRequest, AuthResponse DTOs
    - _Requirements: 1.1, 1.2, 2.1_
  
  - [ ] 2.2 Implement JWT token provider
    - Create JwtTokenProvider with token generation, validation, and extraction methods
    - Configure token expiration to 24 hours
    - _Requirements: 1.4, 14.1_
  
  - [ ]* 2.3 Write property test for JWT token generation
    - **Property 3: JWT Token Generation**
    - **Validates: Requirements 1.4**
  
  - [ ] 2.4 Implement authentication service
    - Create AuthService with registration, login, and OAuth2 methods
    - Implement password hashing with BCrypt
    - Add password complexity validation
    - Add duplicate email checking
    - _Requirements: 1.1, 1.2, 1.3, 1.5, 1.6_
  
  - [ ]* 2.5 Write property tests for authentication
    - **Property 1: Password Complexity Enforcement**
    - **Property 2: Invalid Credentials Rejection**
    - **Property 4: Duplicate Email Prevention**
    - **Validates: Requirements 1.3, 1.5, 1.6**
  
  - [ ] 2.6 Create authentication controller and endpoints
    - Implement AuthController with /register, /login, /oauth2/google, /logout endpoints
    - Add request validation
    - _Requirements: 1.1, 1.2, 1.3_
  
  - [ ] 2.7 Configure Spring Security with JWT filter
    - Create JwtAuthenticationFilter
    - Configure SecurityConfig with endpoint permissions
    - Add OAuth2 client configuration for Google
    - _Requirements: 1.1, 14.1, 14.4_
  
  - [ ]* 2.8 Write property tests for security
    - **Property 30: Expired Token Rejection**
    - **Property 31: Token Invalidation on Logout**
    - **Validates: Requirements 14.2, 14.3**

- [ ] 3. Implement user profile management
  - [ ] 3.1 Create user service and controller
    - Implement UserService with profile retrieval, update, and password change methods
    - Create UserController with /profile, /password endpoints
    - Add email uniqueness validation on update
    - Add role immutability enforcement
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_
  
  - [ ]* 3.2 Write property tests for profile management
    - **Property 21: Profile Role Immutability**
    - **Property 22: Email Uniqueness on Update**
    - **Validates: Requirements 9.4, 9.5**

- [ ] 4. Checkpoint - Ensure authentication and user management tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 5. Implement product catalog management
  - [ ] 5.1 Create Product entity, repository, and DTOs
    - Implement Product entity with pricing tiers (base, barber, representative)
    - Create ProductRepository with search and filter methods
    - Create ProductDTO, CreateProductRequest, UpdateProductRequest DTOs
    - _Requirements: 3.1, 10.1, 10.3_
  
  - [ ] 5.2 Implement product service with role-based pricing
    - Create ProductService with CRUD operations
    - Implement getPriceForRole method for role-specific pricing
    - Add search and filter functionality
    - Implement soft delete (mark as inactive)
    - _Requirements: 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 10.5_
  
  - [ ]* 5.3 Write property tests for product operations
    - **Property 5: Role-Based Pricing**
    - **Property 8: Product Search Matching**
    - **Property 9: Category Filtering**
    - **Property 10: Price-Based Sorting**
    - **Property 23: Soft Delete Preservation**
    - **Property 24: Non-Negative Stock Validation**
    - **Validates: Requirements 2.2, 2.3, 2.4, 3.2, 3.3, 3.4, 10.4, 10.5**
  
  - [ ] 5.4 Create product controller with admin endpoints
    - Implement ProductController with /products endpoints
    - Add pagination support (max 50 items per page)
    - Add role-based authorization (admin-only for create/update/delete)
    - _Requirements: 3.1, 3.6, 10.1, 10.2, 17.4_
  
  - [ ]* 5.5 Write property test for pagination
    - **Property 36: Pagination Limit**
    - **Validates: Requirements 17.4**

- [ ] 6. Implement shopping cart functionality
  - [ ] 6.1 Create Cart and CartItem entities, repositories, and DTOs
    - Implement Cart and CartItem entities with relationships
    - Create CartRepository and CartItemRepository
    - Create CartDTO, CartItemDTO, AddCartItemRequest DTOs
    - _Requirements: 4.1, 4.4_
  
  - [ ] 6.2 Implement cart service
    - Create CartService with add, update, remove, clear methods
    - Implement calculateTotal with role-based pricing
    - Add stock quantity validation
    - Ensure cart persistence across sessions
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6_
  
  - [ ]* 6.3 Write property tests for cart operations
    - **Property 11: Cart Addition**
    - **Property 12: Cart Total Calculation**
    - **Property 13: Cart Item Removal**
    - **Property 14: Cart Persistence Round Trip**
    - **Property 15: Stock Quantity Limiting**
    - **Validates: Requirements 4.1, 4.2, 4.3, 4.5, 4.6**
  
  - [ ] 6.4 Create cart controller
    - Implement CartController with /cart endpoints
    - Add user-specific cart retrieval
    - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [ ] 7. Checkpoint - Ensure product and cart tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 8. Implement order processing and management
  - [ ] 8.1 Create Order and OrderItem entities, repositories, and DTOs
    - Implement Order and OrderItem entities with status enum
    - Create OrderRepository and OrderItemRepository
    - Create OrderDTO, OrderItemDTO, CreateOrderRequest DTOs
    - _Requirements: 5.2, 5.4_
  
  - [ ] 8.2 Implement order service with inventory management
    - Create OrderService with createOrder, getOrderHistory, updateStatus, cancelOrder methods
    - Implement atomic inventory decrement on order creation
    - Implement inventory restoration on order cancellation
    - Add payment processing integration (mock for now)
    - Generate unique order identifiers
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 18.2, 18.3_
  
  - [ ]* 8.3 Write property tests for order operations
    - **Property 16: Inventory Decrement on Order**
    - **Property 17: Unique Order Identifiers**
    - **Property 38: Atomic Inventory Decrement**
    - **Property 39: Order Cancellation Inventory Restoration**
    - **Validates: Requirements 5.3, 5.6, 18.2, 18.3**
  
  - [ ] 8.4 Create order controller
    - Implement OrderController with /orders endpoints
    - Add order history with pagination
    - Add admin-only status update endpoint
    - _Requirements: 5.1, 5.2, 5.4, 5.5_
  
  - [ ]* 8.5 Write property test for payment handling
    - **Property 33: Credit Card Storage Prevention**
    - **Validates: Requirements 15.4**

- [ ] 9. Implement inventory management features
  - [ ] 9.1 Add inventory tracking to product service
    - Implement stock update methods
    - Add out-of-stock marking when quantity reaches zero
    - Add low stock threshold flagging
    - _Requirements: 18.1, 18.4, 18.6_
  
  - [ ]* 9.2 Write property tests for inventory
    - **Property 37: Out of Stock Marking**
    - **Validates: Requirements 18.1**
  
  - [ ] 9.3 Create inventory transaction log entity and service
    - Implement InventoryTransaction entity
    - Create InventoryTransactionRepository
    - Add logging for all stock changes
    - _Requirements: 18.5_

- [ ] 10. Implement course management system
  - [ ] 10.1 Create Course entity, repository, and DTOs
    - Implement Course entity with status enum and capacity tracking
    - Create CourseRepository with date-based queries
    - Create CourseDTO, CreateCourseRequest, UpdateCourseRequest DTOs
    - _Requirements: 6.1, 6.4_
  
  - [ ] 10.2 Implement course service
    - Create CourseService with CRUD operations
    - Add capacity checking and availability calculation
    - Implement course sorting by date
    - Add full capacity marking
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6_
  
  - [ ]* 10.3 Write property tests for course operations
    - **Property 25: Capacity Reduction Validation**
    - **Validates: Requirements 11.5**
  
  - [ ] 10.4 Create course controller with admin endpoints
    - Implement CourseController with /courses endpoints
    - Add admin-only create/update/delete endpoints
    - Add enrollment viewing for admins
    - _Requirements: 6.1, 6.4, 11.1, 11.2, 11.4_

- [ ] 11. Implement course booking system
  - [ ] 11.1 Create Booking entity, repository, and DTOs
    - Implement Booking entity with participation type enum
    - Create BookingRepository
    - Create BookingDTO, CreateBookingRequest DTOs
    - _Requirements: 7.2, 7.5_
  
  - [ ] 11.2 Implement booking service
    - Create BookingService with createBooking, cancelBooking methods
    - Add capacity validation before booking
    - Implement capacity decrement on booking creation
    - Implement capacity increment on booking cancellation
    - Add payment processing for bookings
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.6_
  
  - [ ]* 11.3 Write property tests for booking operations
    - **Property 18: Course Capacity Decrement**
    - **Property 19: Full Course Booking Prevention**
    - **Property 20: Booking Cancellation Round Trip**
    - **Validates: Requirements 7.3, 7.4, 7.6**
  
  - [ ] 11.4 Create booking controller
    - Implement BookingController with /bookings endpoints
    - Add user booking history
    - _Requirements: 7.1, 7.2, 7.5, 7.6_

- [ ] 12. Checkpoint - Ensure order, inventory, course, and booking tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 13. Implement representative dashboard
  - [ ] 13.1 Create representative service
    - Implement RepresentativeService with dashboard metrics calculation
    - Add customer association management
    - Add sales history retrieval
    - Add customer note functionality
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_
  
  - [ ] 13.2 Create representative controller
    - Implement RepresentativeController with /representative endpoints
    - Add role-based authorization (representative-only)
    - _Requirements: 8.1, 8.2, 8.3, 8.5_

- [ ] 14. Implement audit logging system
  - [ ] 14.1 Create AuditLog entity and repository
    - Implement AuditLog entity with action tracking
    - Create AuditLogRepository with query methods
    - _Requirements: 20.1, 20.2_
  
  - [ ] 14.2 Create audit logging service and aspect
    - Implement AuditLogService
    - Create AOP aspect to automatically log state-changing operations
    - Add old/new value tracking for sensitive data
    - Add IP address logging for security events
    - _Requirements: 20.1, 20.2, 20.4_
  
  - [ ]* 14.3 Write property tests for audit logging
    - **Property 40: Audit Log Creation**
    - **Property 41: Audit Log Value Tracking**
    - **Property 42: Audit Log Protection**
    - **Validates: Requirements 20.1, 20.2, 20.6**
  
  - [ ] 14.4 Create audit log query controller for admins
    - Implement AuditLogController with query endpoints
    - Add filtering by date range, user, and action type
    - Add admin-only authorization
    - _Requirements: 20.5_

- [ ] 15. Implement validation and error handling
  - [ ] 15.1 Create global exception handler
    - Implement @ControllerAdvice for centralized exception handling
    - Create custom exception classes (BusinessException, ValidationException, etc.)
    - Implement consistent error response format
    - _Requirements: 13.1, 13.5_
  
  - [ ] 15.2 Add comprehensive validation
    - Add email format validation
    - Add monetary amount validation (non-negative, 2 decimal places)
    - Add date format validation (ISO 8601)
    - Add required field validation
    - _Requirements: 13.2, 13.3, 13.4, 13.6_
  
  - [ ]* 15.3 Write property tests for validation
    - **Property 27: Email Format Validation**
    - **Property 28: Monetary Amount Validation**
    - **Property 29: Required Field Validation**
    - **Validates: Requirements 13.2, 13.3, 13.6**
  
  - [ ]* 15.4 Write property tests for error responses
    - **Property 34: HTTP Status Code Correctness**
    - **Property 35: JSON Response Format**
    - **Validates: Requirements 17.2, 17.3**

- [ ] 16. Implement security features
  - [ ] 16.1 Add rate limiting for login attempts
    - Implement rate limiting service with IP tracking
    - Add rate limit filter for authentication endpoints
    - Configure 5 attempts per 15 minutes limit
    - _Requirements: 14.6_
  
  - [ ]* 16.2 Write property test for rate limiting
    - **Property 32: Rate Limiting After Failed Logins**
    - **Validates: Requirements 14.6**
  
  - [ ] 16.3 Add CSRF protection
    - Configure CSRF token generation and validation
    - Add CSRF token to all state-changing endpoints
    - _Requirements: 14.4_
  
  - [ ] 16.4 Implement authorization checks
    - Add method-level security annotations
    - Implement role-based access control for all endpoints
    - _Requirements: 2.5, 2.6_
  
  - [ ]* 16.5 Write property test for authorization
    - **Property 6: Authorization Enforcement**
    - **Property 7: Role Persistence**
    - **Validates: Requirements 2.5, 2.6**

- [ ] 17. Implement search and filter functionality
  - [ ] 17.1 Add advanced product search
    - Implement full-text search in ProductRepository
    - Add multi-filter support (category, price range, availability)
    - Ensure filters combine with AND logic
    - _Requirements: 3.2, 12.2, 12.3, 12.4_
  
  - [ ]* 17.2 Write property test for filtering
    - **Property 26: Multiple Filter Combination**
    - **Validates: Requirements 12.2, 12.3**

- [ ] 18. Checkpoint - Ensure all backend tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 19. Create Angular frontend authentication module
  - [ ] 19.1 Set up Angular project structure
    - Create modules for auth, products, cart, orders, courses, bookings, dashboard
    - Configure routing with lazy loading
    - Set up Tailwind CSS configuration
    - _Requirements: 16.1, 16.2, 16.3, 16.4_
  
  - [ ] 19.2 Create authentication service and components
    - Implement AuthService with login, register, logout methods
    - Create LoginComponent with email/password form and Google OAuth2 button
    - Create RegisterComponent with role selection
    - Add JWT token storage in localStorage
    - Add HTTP interceptor for adding JWT to requests
    - _Requirements: 1.1, 1.2, 1.3, 1.4_
  
  - [ ] 19.3 Create auth guard and route protection
    - Implement AuthGuard for protected routes
    - Add role-based route guards
    - Add redirect logic after login
    - _Requirements: 2.1, 2.5_

- [ ] 20. Create Angular product catalog module
  - [ ] 20.1 Create product service
    - Implement ProductService with API calls for products
    - Add search and filter methods
    - Add pagination support
    - _Requirements: 3.1, 3.2, 3.3, 3.4_
  
  - [ ] 20.2 Create product list component
    - Implement ProductListComponent with grid/list view
    - Add search bar with debounce
    - Add category and price range filters
    - Add sort options
    - Add pagination controls
    - Display role-appropriate pricing
    - _Requirements: 3.1, 3.2, 3.3, 3.4_
  
  - [ ] 20.3 Create product detail component
    - Implement ProductDetailComponent with image gallery
    - Add quantity selector
    - Add "Add to Cart" button
    - Display stock availability
    - _Requirements: 3.1, 4.1_

- [ ] 21. Create Angular shopping cart module
  - [ ] 21.1 Create cart service with state management
    - Implement CartService with BehaviorSubject for cart state
    - Add API calls for cart operations
    - Add cart item count observable
    - _Requirements: 4.1, 4.2, 4.3, 4.4_
  
  - [ ] 21.2 Create cart component
    - Implement CartComponent with items list
    - Add quantity controls
    - Add remove item buttons
    - Display price calculations
    - Add "Proceed to Checkout" button
    - _Requirements: 4.1, 4.2, 4.3, 4.4_
  
  - [ ] 21.3 Create checkout component
    - Implement CheckoutComponent with order summary
    - Add payment form (mock for now)
    - Add order confirmation
    - _Requirements: 5.1, 5.2_

- [ ] 22. Create Angular order management module
  - [ ] 22.1 Create order service
    - Implement OrderService with API calls
    - Add order history retrieval
    - _Requirements: 5.4_
  
  - [ ] 22.2 Create order history component
    - Implement OrderHistoryComponent with orders list
    - Add status badges
    - Add pagination
    - Add "View Details" links
    - _Requirements: 5.4_
  
  - [ ] 22.3 Create order detail component
    - Implement OrderDetailComponent with order information
    - Display items list
    - Show status timeline
    - _Requirements: 5.4_

- [ ] 23. Create Angular course and booking module
  - [ ] 23.1 Create course service
    - Implement CourseService with API calls
    - Add course listing and details methods
    - _Requirements: 6.1, 6.2_
  
  - [ ] 23.2 Create course list component
    - Implement CourseListComponent with course cards
    - Display capacity indicators
    - Add date filtering
    - Add "Book Now" buttons
    - _Requirements: 6.1, 6.2, 6.3_
  
  - [ ] 23.3 Create course detail and booking component
    - Implement CourseDetailComponent with course information
    - Add participation type selector (Spectator/Hands-On)
    - Add booking form
    - Display availability status
    - _Requirements: 6.2, 7.1, 7.2_
  
  - [ ] 23.4 Create booking service and history component
    - Implement BookingService with API calls
    - Create BookingHistoryComponent with user's bookings
    - Add cancel booking functionality
    - _Requirements: 7.5, 7.6_

- [ ] 24. Create Angular dashboard modules
  - [ ] 24.1 Create user dashboard component
    - Implement UserDashboardComponent with profile summary
    - Display recent orders
    - Display upcoming bookings
    - Add quick action links
    - _Requirements: 9.1_
  
  - [ ] 24.2 Create representative dashboard component
    - Implement RepresentativeDashboardComponent with sales metrics
    - Add revenue charts (using Chart.js or similar)
    - Display customer list
    - Display recent sales table
    - _Requirements: 8.1, 8.2, 8.3_
  
  - [ ] 24.3 Create admin dashboard component
    - Implement AdminDashboardComponent with system statistics
    - Display recent orders
    - Display low stock alerts
    - Add user management links
    - _Requirements: 10.1, 10.2, 11.1, 11.2_

- [ ] 25. Create Angular profile management module
  - [ ] 25.1 Create profile service and component
    - Implement ProfileService with API calls
    - Create ProfileComponent with profile form
    - Add email and name update functionality
    - Add password change form
    - Display role (read-only)
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_

- [ ] 26. Create Angular admin modules
  - [ ] 26.1 Create admin product management component
    - Implement AdminProductComponent with product table
    - Add create/edit product forms
    - Add stock management
    - Add role-specific pricing inputs
    - Add soft delete functionality
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_
  
  - [ ] 26.2 Create admin course management component
    - Implement AdminCourseComponent with course table
    - Add create/edit course forms
    - Display enrollment details
    - Add course cancellation
    - _Requirements: 11.1, 11.2, 11.4_
  
  - [ ] 26.3 Create admin audit log viewer component
    - Implement AuditLogComponent with logs table
    - Add filtering by date, user, and action
    - Add pagination
    - _Requirements: 20.5_

- [ ] 27. Implement responsive design and styling
  - [ ] 27.1 Apply Tailwind CSS styling to all components
    - Style authentication pages
    - Style product catalog with grid layout
    - Style cart and checkout pages
    - Style dashboards with cards and charts
    - Ensure consistent color scheme and typography
    - _Requirements: 16.1, 16.2, 16.3, 16.4_
  
  - [ ] 27.2 Add responsive breakpoints
    - Test and adjust layouts for mobile (320px-768px)
    - Test and adjust layouts for tablet (768px-1024px)
    - Test and adjust layouts for desktop (1024px+)
    - _Requirements: 16.1, 16.2, 16.3, 16.4_

- [ ] 28. Final integration and testing
  - [ ] 28.1 Connect frontend to backend API
    - Configure API base URL in environment files
    - Test all API integrations
    - Add error handling for API failures
    - Add loading states for async operations
    - _Requirements: 17.1, 17.2, 17.3_
  
  - [ ] 28.2 Test OAuth2 Google integration
    - Configure Google OAuth2 credentials
    - Test OAuth2 login flow
    - Test account creation from OAuth2
    - _Requirements: 1.1_
  
  - [ ]* 28.3 Run all property-based tests
    - Execute all 42 property tests with 100 iterations each
    - Verify all properties pass
    - Fix any failing properties
    - _Requirements: All_
  
  - [ ]* 28.4 Run integration tests
    - Test end-to-end user flows (registration → login → browse → cart → checkout)
    - Test admin workflows (product management, course management)
    - Test representative workflows (dashboard, sales tracking)
    - _Requirements: All_

- [ ] 29. Final checkpoint - Complete system validation
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Property tests validate universal correctness properties with minimum 100 iterations
- Unit tests validate specific examples and edge cases
- The implementation follows a backend-first approach, then frontend integration
- H2 database is used for learning purposes; production would use PostgreSQL or MySQL
- Payment integration uses mocks initially; real payment processor integration comes later
- OAuth2 requires Google Cloud Console configuration for client ID and secret
