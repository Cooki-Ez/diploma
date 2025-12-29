# Frontend API Documentation

## Overview

This document describes how each frontend page communicates with the backend API, including authorization handling and data retrieval.

## Authentication & Authorization

### JWT Token Storage
- All authenticated requests use a JWT token stored in `localStorage` under the key `jwt-token`
- The token is retrieved using the `getAuthToken()` helper function in all JS files
- Token is included in the `Authorization` header as: `Bearer <token>`

### Authorization Changes Made
To simplify the application for this frontend implementation, the following authorization changes were made in the backend:

1. **Public Access to UI Pages**: Added `permitAll()` for `/login`, `/create-leave-request`, `/evaluate-leave-request` in `SecurityConfig.java`
2. **Public Access to Static Resources**: Added `permitAll()` for `/css/**` and `/js/**` to serve static files
3. **Current Employee Retrieval**: The `/employees/current` endpoint was added to fetch the currently logged-in employee's data

### Where Current Employee is Retrieved

#### Login Page (`login.js`)
- **Location**: Line 26-27 in `login.js`
- **Method**: After successful login, the JWT token is stored and the user is redirected to the create-leave-request page
- **Code**:
  ```javascript
  if (response.ok && data['jwt-token']) {
      localStorage.setItem('jwt-token', data['jwt-token']);
      window.location.href = '/create-leave-request';
  }
  ```

#### Create Leave Request Page (`create-leave-request.js`)
- **Location**: Lines 72-96 in `create-leave-request.js`
- **Method**: `loadUserData()` function
- **Endpoint**: `GET /employees`
- **Purpose**: Fetches the current user's data including:
  - User's points balance (for display and validation)
  - User's assigned projects (for display on the form)
- **Code**:
  ```javascript
  async function loadUserData() {
      const response = await fetch('/employees', {
          headers: {
              'Authorization': 'Bearer ' + getAuthToken()
          }
      });

      if (response.ok) {
          const employees = await response.json();
          const currentEmployee = employees[0];

          if (currentEmployee) {
              userPoints = currentEmployee.points || 0;

              if (currentEmployee.projects && currentEmployee.projects.length > 0) {
                  const projectNames = currentEmployee.projects.map(p => p.name).join(', ');
                  projectDisplay.textContent = projectNames;
              }
          }
      }
  }
  ```

## Page 1: Login Page (`login.html`)

### Purpose
Authenticates users and stores JWT token for subsequent API calls.

### API Calls

#### 1. Login
- **Endpoint**: `POST /auth/login`
- **Location**: Lines 28-38 in `login.js`
- **Payload**:
  ```json
  {
    "username": "user@email.com",
    "password": "password123"
  }
  ```
- **Response**:
  ```json
  {
    "jwt-token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```
- **Success Handling**: Stores token in localStorage and redirects to `/create-leave-request`
- **Error Handling**: Displays error message to user

---

## Page 2: Create Leave Request (`create-leave-request.html`)

### Purpose
Allows employees to submit new leave requests with date selection, project display, reasoning, and point usage option.

### API Calls

#### 1. Get Current Employee Data
- **Endpoint**: `GET /employees`
- **Location**: Lines 72-96 in `create-leave-request.js`
- **Function**: `loadUserData()`
- **Purpose**: Retrieve employee points and projects for display
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "John",
      "surname": "Doe",
      "email": "john@company.com",
      "points": 25,
      "projects": [
        {
          "id": 1,
          "name": "Sample Project"
        }
      ]
    }
  ]
  ```
- **Usage**: Stores `points` in `userPoints` variable and displays project names in the UI

#### 2. Create Leave Request
- **Endpoint**: `POST /leaves`
- **Location**: Lines 99-138 in `create-leave-request.js`
- **Event Listener**: Form submit event
- **Payload**:
  ```json
  {
    "startDate": "2025-02-01T00:00:00.000Z",
    "endDate": "2025-02-05T00:00:00.000Z",
    "comment": "Family vacation",
    "usePoints": true
  }
  ```
- **Fields**:
  - `startDate`: ISO 8601 date string from start date input
  - `endDate`: ISO 8601 date string from end date input
  - `comment`: User-provided reasoning (max 255 characters)
  - `usePoints`: `true` if checkbox is unchecked (default), `false` if checked
- **Response**:
  ```json
  {
    "id": 1,
    "startDate": "2025-02-01T00:00:00.000Z",
    "endDate": "2025-02-05T00:00:00.000Z",
    "comment": "Family vacation",
    "status": "PENDING",
    "usePoints": true,
    "employeeId": 1,
    "managerId": null,
    "leaveEvaluationId": null
  }
  ```
- **Success Handling**: Shows success message, resets form
- **Error Handling**: Displays error message from backend

### Client-Side Logic

#### Calculate Days
- **Function**: `calculateDays(startDate, endDate)`
- **Location**: Lines 48-54 in `create-leave-request.js`
- **Purpose**: Calculates the number of days between start and end date
- **Logic**: `(end - start) / milliseconds_per_day + 1`
- **Usage**: Displays points that will be used when `usePoints` is `false`

#### Update Points Display
- **Function**: `updatePointsDisplay()`
- **Location**: Lines 56-65 in `create-leave-request.js`
- **Trigger**: Called when start date, end date, or checkbox changes
- **Logic**:
  - If checkbox is unchecked (usePoints = true) and dates are selected:
    - Calculate days
    - Show "Points that will be used: X"
  - Otherwise: Hide the points display

---

## Page 3: Evaluate Leave Request (`evaluate-leave-request.html`)

### Purpose
Allows managers and admins to review and approve/reject leave requests with comments.

### API Calls

#### 1. Get Leave Request Details
- **Endpoint**: `GET /leaves/{id}`
- **Location**: Lines 98-130 in `evaluate-leave-request.js`
- **Function**: `loadLeaveRequestDetails()`
- **URL Parameter**: `id` from URL search params (e.g., `/evaluate-leave-request?id=5`)
- **Purpose**: Retrieve leave request details for evaluation
- **Response**:
  ```json
  {
    "id": 1,
    "startDate": "2025-02-01T00:00:00.000Z",
    "endDate": "2025-02-05T00:00:00.000Z",
    "comment": "Family vacation",
    "status": "PENDING",
    "usePoints": true,
    "employee": {
      "id": 3,
      "name": "Regular",
      "surname": "User",
      "email": "user@company.com",
      "projects": [
        {
          "id": 1,
          "name": "Sample Project"
        }
      ]
    },
    "employeeId": 3,
    "managerId": null,
    "leaveEvaluationId": null
  }
  ```
- **Usage**:
  - Displays formatted dates with day counts in brackets
  - Shows employee's projects
  - Displays user's reasoning

#### 2. Update Leave Request (Approve/Reject)
- **Endpoint**: `PATCH /leaves/{id}`
- **Location**: Lines 132-168 in `evaluate-leave-request.js`
- **Function**: `evaluateLeaveRequest(approved)`
- **URL Parameter**: `id` from URL search params
- **Payload**:
  ```json
  {
    "status": "APPROVED",
    "comment": "Approved!"
  }
  ```
  OR
  ```json
  {
    "status": "DECLINED",
    "comment": "Rejected!"
  }
  ```
- **Fields**:
  - `status`: `"APPROVED"` or `"DECLINED"` based on button clicked
  - `comment`: Default message from button or custom user input
- **Success Handling**: Shows success message, closes window after 2 seconds
- **Error Handling**: Displays error message from backend

### Client-Side Logic

#### Format Date
- **Function**: `formatDate(dateString)`
- **Location**: Lines 73-76 in `evaluate-leave-request.js`
- **Purpose**: Format ISO date string to readable format (dd/mm/yyyy)
- **Usage**: Display dates in the UI

#### Calculate Days
- **Function**: `calculateDays(startDate, endDate)`
- **Location**: Lines 78-84 in `evaluate-leave-request.js`
- **Purpose**: Calculate days between start and end date
- **Usage**: Display day count in brackets next to dates

---

## Error Handling

All API calls follow this error handling pattern:

1. Try-catch blocks around fetch calls
2. Check `response.ok` status
3. Parse error JSON from backend
4. Display error message to user in designated error div
5. Auto-hide error message after 5 seconds

Example (from `login.js`, lines 41-49):
```javascript
} catch (error) {
    showError('An error occurred during login');
    console.error(error);
}

function showError(message) {
    errorMessage.textContent = message;
    errorMessage.classList.add('visible');
    setTimeout(() => {
        errorMessage.classList.remove('visible');
    }, 5000);
}
```

---

## Helper Functions Used Across Pages

### getAuthToken()
- **Purpose**: Retrieve JWT token from localStorage
- **Returns**: String (token or null)
- **Usage**: Included in Authorization header for all authenticated requests

### showError(message)
- **Purpose**: Display error message to user
- **Behavior**: Shows error message for 5 seconds then auto-hides

### showSuccess(message)
- **Purpose**: Display success message to user
- **Behavior**: Shows success message for 5 seconds then auto-hides

---

## Security Considerations

1. **Token Storage**: JWT tokens are stored in localStorage (note: in production, consider using httpOnly cookies for better security)
2. **Token Expiration**: No token expiration handling is currently implemented
3. **CSRF Protection**: Disabled in Spring Security for API simplicity
4. **Role-Based Access**: Backend enforces role-based access (MANAGER/ADMIN for evaluation)

---

## Testing the Application

### Default Users (created in init.sql):

1. **Admin User**
   - Email: `admin@company.com`
   - Password: `Admin123!`
   - Roles: ADMIN
   - Points: 100

2. **Manager User**
   - Email: `manager@company.com`
   - Password: `Manager123!`
   - Roles: MANAGER
   - Points: 50

3. **Regular User**
   - Email: `user@company.com`
   - Password: `User123!`
   - Roles: EMPLOYEE
   - Points: 25

### Access URLs:
- Frontend (Login): http://localhost/
- Backend API: http://localhost:8080/
- Adminer (DB): http://localhost:8081/
- Create Leave Request: http://localhost/create-leave-request
- Evaluate Leave Request: http://localhost/evaluate-leave-request?id=1
