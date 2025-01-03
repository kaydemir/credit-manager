-- TODO: use flyweight for migration
ALTER TABLE customer ALTER COLUMN id BIGINT DEFAULT NEXTVAL('customer_id_seq');
ALTER TABLE loan ALTER COLUMN id BIGINT DEFAULT NEXTVAL('loan_id_seq');
ALTER TABLE loan_installment ALTER COLUMN id BIGINT DEFAULT NEXTVAL('loan_installment_id_seq');

INSERT INTO customer (id, name, surname, credit_limit, used_credit_limit)
VALUES (1, 'John', 'Doe', 10000.00, 0.00);

INSERT INTO app_user (id, username, password, customer_id)
VALUES
    (1, 'admin', '$2a$10$3dRjC2uSG19xx0hSEc3oNuZKmAmVgvLcXVOIe4Xa8JAsp05K/x.oO', NULL),
    (2, 'customer', '$2a$10$hSMAdB/P1GIveeNO3WzI.uRUwQzMnbCQPY5s4FdqqUivbWd2neXJK', 1);

    INSERT INTO app_user_roles (user_id, role)
VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_CUSTOMER');