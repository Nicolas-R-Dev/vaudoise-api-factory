INSERT INTO CLIENT (id, type, name, email, phone, created_at, updated_at)
VALUES (1, 'PERSON', 'Jean Dupont', 'jean@mail.com', '+41791234567', NOW(), NOW());

INSERT INTO PERSON_CLIENT (id, birthdate) VALUES (1, '1990-01-01');

INSERT INTO CONTRACT (id, client_id, start_date, end_date, cost_amount, last_updated_at)
VALUES (1, 1, '2024-10-01', NULL, 199.99, NOW());
