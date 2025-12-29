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

-- Add use_points column to leave_request table if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='leave_request' AND column_name='use_points'
    ) THEN
        ALTER TABLE leave_request ADD COLUMN use_points BOOLEAN DEFAULT TRUE;
    END IF;
END $$;

-- Insert sample data

-- Create default department
INSERT INTO department (name, location)
VALUES ('IT Department', 'Warsaw')
ON CONFLICT DO NOTHING;

-- Insert default admin user (password: Admin123!)
-- BCrypt hash for 'Admin123!'
INSERT INTO employee (name, surname, email, password, date_of_birth, salary, points, roles, department_id)
VALUES ('Admin', 'User', 'admin@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '1990-01-01', 10000, 100, 'ADMIN', 1)
ON CONFLICT (email) DO NOTHING;

-- Insert default manager user (password: Manager123!)
-- BCrypt hash for 'Manager123!'
INSERT INTO employee (name, surname, email, password, date_of_birth, salary, points, roles, department_id)
VALUES ('Manager', 'User', 'manager@company.com', '$2a$10$rKZ6lJ3u3F9E6qM7n2qK9e9J6n3qK9e9J6n3qK9e9J6n3qK9e9J6n3qK', '1985-05-15', 8000, 50, 'MANAGER', 1)
ON CONFLICT (email) DO NOTHING;

-- Insert default regular user (password: User123!)
-- BCrypt hash for 'User123!'
INSERT INTO employee (name, surname, email, password, date_of_birth, salary, points, roles, department_id)
VALUES ('Regular', 'User', 'user@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '1995-10-20', 5000, 25, 'EMPLOYEE', 1)
ON CONFLICT (email) DO NOTHING;

-- Insert sample project
INSERT INTO project (name, description, start_date, end_date, importance)
VALUES ('Sample Project', 'A sample project for testing', '2025-01-01 00:00:00', '2025-12-31 00:00:00', 'IMPORTANT')
ON CONFLICT DO NOTHING;

-- Assign users to the sample project
INSERT INTO project_employee (project_id, employee_id)
SELECT 1, id FROM employee WHERE id IN (1, 2, 3)
ON CONFLICT DO NOTHING;
