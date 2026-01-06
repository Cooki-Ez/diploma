INSERT INTO Department (location, name) VALUES 
('Warsaw', 'IT Department'),
('Krakow', 'HR Department'),
('Gdansk', 'Finance Department');

INSERT INTO Employee (name, surname, email, password, date_of_birth, salary, points, roles, department_id) VALUES 
('John', 'Doe', 'john.doe@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1990-01-15', 5000.00, 0, 'ADMIN', 1),
('Jane', 'Smith', 'jane.smith@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1985-05-20', 7000.00, 0, 'MANAGER', 1),
('Bob', 'Johnson', 'bob.johnson@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1992-03-10', 4500.00, 0, 'EMPLOYEE', 1),
('Alice', 'Williams', 'alice.williams@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1988-11-25', 5500.00, 0, 'EMPLOYEE', 2),
('Charlie', 'Brown', 'charlie.brown@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1995-07-08', 4000.00, 0, 'EMPLOYEE', 3);

INSERT INTO Project (name, description, start_date, end_date, importance) VALUES 
('Website Redesign', 'Redesign company website', '2024-01-01 00:00:00', '2024-06-30 00:00:00', 'CRUCIAL'),
('Mobile App Development', 'Develop mobile application', '2024-02-15 00:00:00', '2024-12-31 00:00:00', 'IMPORTANT'),
('Internal Tools', 'Update internal tools', '2024-03-01 00:00:00', '2024-08-15 00:00:00', 'MODERATE');

INSERT INTO project_employee (project_id, employee_id) VALUES 
(1, 2),
(1, 3),
(2, 2),
(2, 4),
(3, 3),
(3, 5);

INSERT INTO Leave_Evaluation (date, comment, employee_id) VALUES 
('2024-01-10 10:00:00', 'Approved for vacation', 3),
('2024-02-15 14:30:00', 'Sick leave approved', 4);

INSERT INTO Leave_Request (start_date, end_date, comment, status, use_points, employee_id, manager_id, leave_evaluation_id) VALUES 
('2024-02-01 00:00:00', '2024-02-05 00:00:00', 'Family vacation', 'APPROVED', TRUE, 3, 2, 1),
('2024-03-10 00:00:00', '2024-03-11 00:00:00', 'Sick leave', 'APPROVED', FALSE, 4, 2, 2),
('2024-04-15 00:00:00', '2024-04-20 00:00:00', 'Personal time', 'PENDING', TRUE, 5, 2, NULL),
('2024-05-01 00:00:00', '2024-05-03 00:00:00', 'Conference attendance', 'PENDING', FALSE, 3, 2, NULL);
