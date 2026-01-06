# Leave Management Frontend

This is the frontend for the Leave Management System, built with pure HTML, CSS, and JavaScript using Express.

## Prerequisites
- Node.js 16+ 
- npm

## Installation

```bash
npm install
```

## Running

Development:
```bash
npm run dev
```

Production:
```bash
npm start
```

The frontend will be available at `http://localhost:3000`

## Environment Variables

- `PORT` - Port for the Express server (default: 3000)

## Project Structure

```
frontend/
├── public/
│   ├── index.html         # Entry point, redirects to login
│   ├── login.html         # Login page
│   ├── create-leave.html  # Create leave request form
│   ├── leaves.html         # List of leave requests
│   ├── evaluate-leave-request.html  # Evaluate leave request
│   ├── css/
│   │   └── leave.css
│   └── js/
│       ├── login.js
│       ├── leave.js
│       └── evaluate-leave-request.js
├── server.js              # Express server
├── package.json
└── Dockerfile
```

## API Configuration

The frontend communicates with the backend API running at `http://localhost:8080`.

Make sure the backend service is running before accessing the frontend.
