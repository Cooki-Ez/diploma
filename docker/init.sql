-- PostgreSQL initialization script based on JPA models

-- Create Department table
CREATE TABLE IF NOT EXISTS department (
    id SERIAL PRIMARY KEY,
    location VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL
);

-- Create Employee table
CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    salary DECIMAL(10, 2) NOT NULL CHECK (salary >= 500),
    age INTEGER,
    date_of_birth DATE NOT NULL,
    points INTEGER NOT NULL DEFAULT 0,
    roles VARCHAR(255),
    department_id INTEGER NOT NULL REFERENCES department(id) ON DELETE RESTRICT
);

-- Create Project table
CREATE TABLE IF NOT EXISTS project (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    importance VARCHAR(20) CHECK (importance IN ('CRUCIAL', 'IMPORTANT', 'MODERATE', 'LOW'))
);

-- Create Leave_Evaluation table
CREATE TABLE IF NOT EXISTS leave_evaluation (
    id SERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    comment VARCHAR(255),
    employee_id INTEGER REFERENCES employee(id) ON DELETE SET NULL
);

-- Create Leave_Request table
CREATE TABLE IF NOT EXISTS leave_request (
    id SERIAL PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    comment VARCHAR(255),
    status VARCHAR(20) CHECK (status IN ('PENDING', 'APPROVED', 'CANCELLED', 'DECLINED', 'DECLINED_S', 'MANUAL')),
    employee_id INTEGER REFERENCES employee(id) ON DELETE CASCADE,
    manager_id INTEGER REFERENCES employee(id) ON DELETE SET NULL,
    leave_evaluation_id INTEGER REFERENCES leave_evaluation(id) ON DELETE SET NULL
);

-- Create Project_Employee join table (ManyToMany)
CREATE TABLE IF NOT EXISTS project_employee (
    project_id INTEGER NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    employee_id INTEGER NOT NULL REFERENCES employee(id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, employee_id)
);

-- Create indexes for foreign keys
CREATE INDEX IF NOT EXISTS idx_employee_department ON employee(department_id);
CREATE INDEX IF NOT EXISTS idx_leave_request_employee ON leave_request(employee_id);
CREATE INDEX IF NOT EXISTS idx_leave_request_manager ON leave_request(manager_id);
CREATE INDEX IF NOT EXISTS idx_leave_request_evaluation ON leave_request(leave_evaluation_id);
CREATE INDEX IF NOT EXISTS idx_leave_evaluation_employee ON leave_evaluation(employee_id);
CREATE INDEX IF NOT EXISTS idx_project_employee_project ON project_employee(project_id);
CREATE INDEX IF NOT EXISTS idx_project_employee_employee ON project_employee(employee_id);
