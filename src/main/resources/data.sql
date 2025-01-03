-- TODO: use flyweight for migration
ALTER TABLE customer ALTER COLUMN id BIGINT DEFAULT NEXTVAL('customer_id_seq');
ALTER TABLE loan ALTER COLUMN id BIGINT DEFAULT NEXTVAL('loan_id_seq');
ALTER TABLE loan_installment ALTER COLUMN id BIGINT DEFAULT NEXTVAL('loan_installment_id_seq');

INSERT INTO customer (id, name, surname, credit_limit, used_credit_limit)
VALUES (1, 'John', 'Doe', 10000.00, 0.00),
       (2, 'Jerin', 'Parker', 10000.00, 0.00);

INSERT INTO app_user (id, username, password, customer_id)
VALUES
    (1, 'admin', '$2a$10$3dRjC2uSG19xx0hSEc3oNuZKmAmVgvLcXVOIe4Xa8JAsp05K/x.oO', NULL),
    (2, 'customer', '$2a$10$hSMAdB/P1GIveeNO3WzI.uRUwQzMnbCQPY5s4FdqqUivbWd2neXJK', 1),
    (3, 'customer2', '$2a$10$mOAs4K2f4uvV39g3M2ID8.rXaBFyc8nQAWRAqiaM.WouW4smK/EWu', 2);

INSERT INTO app_user_roles (user_id, role)
VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_CUSTOMER'),
    (3, 'ROLE_CUSTOMER');