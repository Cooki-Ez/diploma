INSERT INTO Department (location, name) VALUES 
('Warsaw', 'IT Department'),
('Krakow', 'HR Department');

INSERT INTO Employee (name, surname, email, password, date_of_birth, salary, points, roles, department_id) VALUES 
('Admin', 'User', 'admin@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1985-01-01', 10000.00, 0, 'ADMIN', 1),
('Manager', 'One', 'manager1@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1980-03-15', 8000.00, 0, 'MANAGER', 1),
('Manager', 'Two', 'manager2@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1982-06-20', 8000.00, 0, 'MANAGER', 1),
('Employee', 'One', 'employee1@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1990-02-10', 5000.00, 0, 'EMPLOYEE', 1),
('Employee', 'Two', 'employee2@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1992-05-25', 5000.00, 0, 'EMPLOYEE', 1),
('Manager', 'Three', 'manager3@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1978-09-30', 8000.00, 0, 'MANAGER', 2),
('Manager', 'Four', 'manager4@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1981-12-05', 8000.00, 0, 'MANAGER', 2),
('Employee', 'Three', 'employee3@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1993-04-15', 4500.00, 0, 'EMPLOYEE', 2),
('Employee', 'Four', 'employee4@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '1994-08-20', 4500.00, 0, 'EMPLOYEE', 2);

INSERT INTO Project (name, description, start_date, end_date, importance) VALUES 
('Website Redesign', 'Redesign company website', '2024-01-01 00:00:00', '2024-06-30 00:00:00', 'CRUCIAL'),
('Mobile App Development', 'Develop mobile application', '2024-02-15 00:00:00', '2024-12-31 00:00:00', 'IMPORTANT'),
('Internal Tools', 'Update internal tools', '2024-03-01 00:00:00', '2024-08-15 00:00:00', 'MODERATE'),
('HR System', 'Upgrade HR management system', '2024-04-01 00:00:00', '2024-09-30 00:00:00', 'IMPORTANT');

INSERT INTO project_employee (project_id, employee_id) VALUES 
(1, 2),
(1, 3),
(2, 2),
(2, 4),
(3, 3),
(3, 5),
(4, 6),
(4, 7);

INSERT INTO Leave_Evaluation (date, comment, employee_id) VALUES 
('2024-01-10 10:00:00', 'Approved for vacation', 4),
('2024-02-15 14:30:00', 'Sick leave approved', 8);

INSERT INTO Leave_Request (start_date, end_date, comment, status, use_points, employee_id, manager_id, leave_evaluation_id, creation_date) VALUES
('2024-02-01 00:00:00', '2024-02-05 00:00:00', 'Family vacation', 'APPROVED', TRUE, 4, 2, 1, '2024-01-15 10:00:00'),
('2024-03-10 00:00:00', '2024-03-11 00:00:00', 'Sick leave', 'APPROVED', FALSE, 8, 6, 2, '2024-02-15 14:30:00'),
('2024-04-15 00:00:00', '2024-04-20 00:00:00', 'Personal time', 'PENDING', TRUE, 5, 2, NULL, '2024-04-10 09:00:00'),
('2024-05-01 00:00:00', '2024-05-03 00:00:00', 'Conference attendance', 'PENDING', FALSE, 4, 2, NULL, '2024-05-01 08:00:00');
