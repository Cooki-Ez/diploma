DROP TABLE IF EXISTS Leave_Request;
DROP TABLE IF EXISTS Leave_Evaluation;
DROP TABLE IF EXISTS project_employee;
DROP TABLE IF EXISTS Project;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Department;

CREATE TABLE Department (
    id SERIAL PRIMARY KEY,
    location VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE Employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    salary DOUBLE PRECISION,
    points INTEGER DEFAULT 0,
    roles VARCHAR(255),
    department_id INTEGER,
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES Department(id)
);

CREATE TABLE Project (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    importance VARCHAR(20)
);

CREATE TABLE project_employee (
    project_id INTEGER NOT NULL,
    employee_id INTEGER NOT NULL,
    PRIMARY KEY (project_id, employee_id),
    CONSTRAINT fk_project_employee_project FOREIGN KEY (project_id) REFERENCES Project(id),
    CONSTRAINT fk_project_employee_employee FOREIGN KEY (employee_id) REFERENCES Employee(id)
);

CREATE TABLE Leave_Evaluation (
    id SERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    comment VARCHAR(255),
    employee_id INTEGER,
    CONSTRAINT fk_leave_evaluation_employee FOREIGN KEY (employee_id) REFERENCES Employee(id)
);

CREATE TABLE Leave_Request (
    id SERIAL PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    comment VARCHAR(255),
    status VARCHAR(20),
    use_points BOOLEAN DEFAULT TRUE,
    employee_id INTEGER,
    manager_id INTEGER,
    leave_evaluation_id INTEGER,
    CONSTRAINT fk_leave_request_employee FOREIGN KEY (employee_id) REFERENCES Employee(id),
    CONSTRAINT fk_leave_request_manager FOREIGN KEY (manager_id) REFERENCES Employee(id),
    CONSTRAINT fk_leave_request_evaluation FOREIGN KEY (leave_evaluation_id) REFERENCES Leave_Evaluation(id)
);
