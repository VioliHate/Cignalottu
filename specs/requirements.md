# Requirements Document - Cignalottu

## Introduction

This document specifies the requirements for Cignalottu, an enterprise-level barber e-commerce platform that provides product sales, training course management, and multi-role user access. The system enables individual customers, professional barbers, and distributors to purchase barber supplies while also offering training courses with different participation levels. The platform uses Spring Boot for the backend, Angular for the frontend, H2 database for data persistence, and supports both OAuth2 and traditional authentication.

## Glossary

- **System**: The barber e-commerce platform
- **User**: Any authenticated person using the platform
- **Customer**: An individual end-user who purchases products
- **Barber**: A professional user who purchases products for business use
- **Representative**: A distributor or reseller with special pricing privileges
- **Admin**: A system administrator with full access rights
- **Product**: A barber supply item available for purchase
- **Course**: A training program with limited capacity
- **Spectator_Participation**: Course attendance for observation only
- **Hands_On_Participation**: Course attendance with practical training
- **Cart**: A temporary collection of products before checkout
- **Order**: A completed purchase transaction
- **Booking**: A reserved spot in a training course
- **Price_Tier**: Role-based pricing structure
- **OAuth2_Provider**: External authentication service (Google)

## Requirements

### Requirement 1: User Registration and Authentication

**User Story:** As a new user, I want to register and authenticate using multiple methods, so that I can access the platform securely.

#### Acceptance Criteria

1. WHEN a user chooses OAuth2 registration, THE System SHALL authenticate via Google OAuth2 and create a user account
2. WHEN a user chooses traditional registration, THE System SHALL require email, password, and role selection
3. WHEN a user provides invalid credentials during login, THE System SHALL reject the authentication and return an error message
4. WHEN a user successfully authenticates, THE System SHALL issue a JWT token valid for the session duration
5. THE System SHALL enforce password complexity requirements of minimum 8 characters with at least one uppercase, one lowercase, one digit, and one special character
6. WHEN a user registers with an existing email, THE System SHALL prevent duplicate registration and notify the user

### Requirement 2: Role-Based Access Control

**User Story:** As a system administrator, I want users to have different access levels based on their roles, so that business rules and pricing are properly enforced.

#### Acceptance Criteria

1. WHEN a user authenticates, THE System SHALL assign permissions based on their role (Customer, Barber, Representative, or Admin)
2. WHEN a Representative accesses product pricing, THE System SHALL display distributor-tier pricing
3. WHEN a Barber accesses product pricing, THE System SHALL display professional-tier pricing
4. WHEN a Customer accesses product pricing, THE System SHALL display retail-tier pricing
5. WHEN a non-Admin user attempts to access admin functions, THE System SHALL deny access and return an authorization error
6. THE System SHALL maintain role assignments persistently across sessions

### Requirement 3: Product Catalog Management

**User Story:** As a customer, I want to browse and search barber products, so that I can find items I need to purchase.

#### Acceptance Criteria

1. WHEN a user views the product catalog, THE System SHALL display all available products with name, description, image, and role-appropriate pricing
2. WHEN a user searches for products by keyword, THE System SHALL return all products matching the search term in name or description
3. WHEN a user filters products by category, THE System SHALL display only products in the selected category
4. WHEN a user sorts products by price, THE System SHALL order products according to the user's role-specific pricing
5. THE System SHALL display product availability status for each item
6. WHEN an Admin adds a new product, THE System SHALL validate all required fields and persist the product to the database

### Requirement 4: Shopping Cart Operations

**User Story:** As a customer, I want to add products to a cart and modify quantities, so that I can prepare my order before checkout.

#### Acceptance Criteria

1. WHEN a user adds a product to the cart, THE System SHALL store the product with selected quantity in the user's cart
2. WHEN a user updates product quantity in the cart, THE System SHALL recalculate the cart total using role-appropriate pricing
3. WHEN a user removes a product from the cart, THE System SHALL delete the item and update the cart total
4. WHEN a user views their cart, THE System SHALL display all items with quantities, individual prices, and total cost
5. THE System SHALL persist cart contents across user sessions
6. WHEN a user adds a product quantity exceeding available stock, THE System SHALL limit the quantity to available stock and notify the user

### Requirement 5: Order Processing and Management

**User Story:** As a customer, I want to complete purchases and track my orders, so that I can receive products and monitor delivery status.

#### Acceptance Criteria

1. WHEN a user initiates checkout, THE System SHALL validate cart contents and available inventory
2. WHEN a user completes checkout, THE System SHALL create an order record with timestamp, items, quantities, prices, and total amount
3. WHEN an order is created, THE System SHALL reduce product inventory by ordered quantities
4. WHEN a user views order history, THE System SHALL display all past orders with dates, items, and status
5. WHEN an Admin updates order status, THE System SHALL persist the status change and timestamp
6. THE System SHALL generate a unique order identifier for each completed purchase

### Requirement 6: Training Course Catalog

**User Story:** As a barber, I want to view available training courses with details, so that I can decide which courses to attend.

#### Acceptance Criteria

1. WHEN a user views the course catalog, THE System SHALL display all courses with title, description, date, duration, capacity, and available spots
2. WHEN a user views course details, THE System SHALL show both Spectator and Hands_On participation options with respective pricing
3. THE System SHALL display current enrollment count and remaining capacity for each course
4. WHEN an Admin creates a course, THE System SHALL validate required fields including title, date, capacity, and pricing
5. WHEN a course reaches full capacity, THE System SHALL mark it as fully booked
6. THE System SHALL organize courses by upcoming date by default

### Requirement 7: Course Booking and Participation

**User Story:** As a user, I want to book training courses with my preferred participation type, so that I can attend professional development sessions.

#### Acceptance Criteria

1. WHEN a user selects a course and participation type, THE System SHALL verify available capacity before allowing booking
2. WHEN a user completes a course booking, THE System SHALL create a booking record with user, course, participation type, and payment amount
3. WHEN a booking is created, THE System SHALL decrement the course available capacity by one
4. WHEN a user attempts to book a full course, THE System SHALL prevent the booking and notify the user
5. WHEN a user views their bookings, THE System SHALL display all course reservations with dates, participation types, and payment status
6. WHEN a user cancels a booking, THE System SHALL increment the course available capacity and update the booking status

### Requirement 8: Representative Dashboard and Sales Tracking

**User Story:** As a representative, I want to track my sales and manage customer relationships, so that I can monitor my business performance.

#### Acceptance Criteria

1. WHEN a Representative accesses their dashboard, THE System SHALL display total sales volume and revenue
2. WHEN a Representative views customer list, THE System SHALL show all customers associated with the representative
3. WHEN a Representative views sales history, THE System SHALL display all orders with dates, customers, and amounts
4. THE System SHALL calculate and display sales metrics including total orders, average order value, and period-over-period growth
5. WHEN a Representative adds a customer note, THE System SHALL persist the note with timestamp and representative identifier

### Requirement 9: User Profile Management

**User Story:** As a user, I want to manage my profile information, so that I can keep my account details current.

#### Acceptance Criteria

1. WHEN a user views their profile, THE System SHALL display email, name, role, and registration date
2. WHEN a user updates profile information, THE System SHALL validate the new data and persist changes
3. WHEN a user changes their password, THE System SHALL require current password verification and enforce password complexity rules
4. WHEN a user updates their email, THE System SHALL verify the email is not already registered to another account
5. THE System SHALL prevent users from changing their assigned role through profile updates

### Requirement 10: Admin Product Management

**User Story:** As an admin, I want to manage the product catalog, so that I can maintain accurate inventory and pricing.

#### Acceptance Criteria

1. WHEN an Admin creates a product, THE System SHALL require name, description, category, base price, and initial stock quantity
2. WHEN an Admin updates product information, THE System SHALL validate changes and persist updates with timestamp
3. WHEN an Admin sets role-specific pricing, THE System SHALL store separate price values for Customer, Barber, and Representative tiers
4. WHEN an Admin updates product stock, THE System SHALL validate the quantity is non-negative
5. WHEN an Admin deletes a product, THE System SHALL mark it as inactive rather than removing it from the database
6. THE System SHALL maintain an audit log of all product changes with admin identifier and timestamp

### Requirement 11: Admin Course Management

**User Story:** As an admin, I want to manage training courses, so that I can schedule and organize educational offerings.

#### Acceptance Criteria

1. WHEN an Admin creates a course, THE System SHALL require title, description, date, duration, capacity, spectator price, and hands-on price
2. WHEN an Admin updates course details, THE System SHALL validate changes and persist updates
3. WHEN an Admin cancels a course, THE System SHALL notify all enrolled users and process refunds
4. WHEN an Admin views course enrollment, THE System SHALL display all bookings with user details and participation types
5. THE System SHALL prevent Admin from reducing course capacity below current enrollment count

### Requirement 12: Search and Filter Functionality

**User Story:** As a user, I want to search and filter products efficiently, so that I can quickly find what I need.

#### Acceptance Criteria

1. WHEN a user enters a search query, THE System SHALL return results within 2 seconds for catalogs up to 10,000 products
2. WHEN a user applies multiple filters, THE System SHALL combine filters with AND logic
3. WHEN a user applies category and price range filters, THE System SHALL return only products matching both criteria
4. THE System SHALL support filtering by product category, price range, and availability status
5. WHEN search returns no results, THE System SHALL display a message suggesting alternative search terms

### Requirement 13: Data Validation and Error Handling

**User Story:** As a developer, I want comprehensive data validation, so that the system maintains data integrity.

#### Acceptance Criteria

1. WHEN invalid data is submitted to any API endpoint, THE System SHALL return a 400 status code with specific validation error messages
2. THE System SHALL validate all email addresses conform to standard email format
3. THE System SHALL validate all monetary amounts are non-negative with maximum two decimal places
4. THE System SHALL validate all date fields are in ISO 8601 format
5. WHEN a database constraint violation occurs, THE System SHALL return a user-friendly error message without exposing internal details
6. THE System SHALL validate all required fields are present before processing any create or update operation

### Requirement 14: Session Management and Security

**User Story:** As a security-conscious user, I want my session to be secure, so that my account is protected from unauthorized access.

#### Acceptance Criteria

1. WHEN a user authenticates, THE System SHALL generate a JWT token with expiration time of 24 hours
2. WHEN a JWT token expires, THE System SHALL require re-authentication
3. WHEN a user logs out, THE System SHALL invalidate the current session token
4. THE System SHALL include CSRF protection for all state-changing operations
5. THE System SHALL enforce HTTPS for all authentication endpoints
6. WHEN multiple failed login attempts occur from the same IP, THE System SHALL implement rate limiting after 5 failed attempts within 15 minutes

### Requirement 15: Payment Processing Integration

**User Story:** As a customer, I want to complete payments securely, so that I can purchase products and book courses.

#### Acceptance Criteria

1. WHEN a user initiates payment, THE System SHALL securely transmit payment information to the payment processor
2. WHEN payment is successful, THE System SHALL confirm the order and update order status to paid
3. WHEN payment fails, THE System SHALL notify the user and maintain the cart contents
4. THE System SHALL never store complete credit card numbers in the database
5. WHEN processing a refund, THE System SHALL communicate with the payment processor and update order status
6. THE System SHALL log all payment transactions with timestamp, amount, and status

### Requirement 16: Responsive User Interface

**User Story:** As a mobile user, I want the platform to work seamlessly on my device, so that I can shop and book courses on the go.

#### Acceptance Criteria

1. WHEN a user accesses the platform on a mobile device, THE System SHALL render a mobile-optimized layout
2. WHEN a user accesses the platform on a tablet, THE System SHALL adapt the layout to tablet screen dimensions
3. WHEN a user accesses the platform on desktop, THE System SHALL display the full desktop layout
4. THE System SHALL maintain functionality across all viewport sizes from 320px to 2560px width
5. WHEN images are displayed, THE System SHALL serve appropriately sized images based on device resolution

### Requirement 17: API Design and Documentation

**User Story:** As a frontend developer, I want well-designed RESTful APIs, so that I can integrate the frontend efficiently.

#### Acceptance Criteria

1. THE System SHALL expose RESTful endpoints following standard HTTP methods (GET, POST, PUT, DELETE)
2. THE System SHALL return appropriate HTTP status codes (200, 201, 400, 401, 403, 404, 500)
3. THE System SHALL return all responses in JSON format with consistent structure
4. THE System SHALL include pagination for list endpoints with page size limit of 50 items
5. THE System SHALL version all API endpoints with /api/v1 prefix
6. THE System SHALL provide API documentation accessible at /api/docs endpoint

### Requirement 18: Inventory Management

**User Story:** As an admin, I want to track and manage product inventory, so that I can prevent overselling and maintain stock levels.

#### Acceptance Criteria

1. WHEN product stock reaches zero, THE System SHALL mark the product as out of stock
2. WHEN an order is placed, THE System SHALL atomically decrement inventory to prevent race conditions
3. WHEN an order is cancelled, THE System SHALL restore the product quantities to inventory
4. WHEN an Admin receives new stock, THE System SHALL increment inventory by the received quantity
5. THE System SHALL maintain an inventory transaction log with all stock changes, timestamps, and reasons
6. WHEN inventory falls below a configured threshold, THE System SHALL flag the product for reordering

### Requirement 19: Performance and Scalability

**User Story:** As a system architect, I want the platform to perform efficiently, so that users have a responsive experience.

#### Acceptance Criteria

1. WHEN a user loads the product catalog, THE System SHALL respond within 1 second for up to 1000 products
2. WHEN concurrent users access the system, THE System SHALL maintain response times under 2 seconds for up to 100 simultaneous users
3. THE System SHALL implement database connection pooling with minimum 10 and maximum 50 connections
4. THE System SHALL cache frequently accessed data including product catalog and course listings
5. WHEN executing database queries, THE System SHALL use indexed columns for search and filter operations

### Requirement 20: Audit Logging and Compliance

**User Story:** As a compliance officer, I want comprehensive audit logs, so that I can track system activities and ensure regulatory compliance.

#### Acceptance Criteria

1. WHEN a user performs any state-changing operation, THE System SHALL log the action with user identifier, timestamp, and affected entities
2. WHEN an Admin modifies sensitive data, THE System SHALL log both old and new values
3. THE System SHALL retain audit logs for minimum 90 days
4. WHEN a security-relevant event occurs (failed login, permission denial), THE System SHALL log the event with source IP address
5. THE System SHALL provide audit log query functionality for Admins with filtering by date range, user, and action type
6. THE System SHALL protect audit logs from modification or deletion by non-Admin users
