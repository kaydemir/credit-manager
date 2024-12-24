-- TODO: use flyweight for migration
ALTER TABLE customer ALTER COLUMN id BIGINT DEFAULT NEXTVAL('customer_id_seq');
ALTER TABLE loan ALTER COLUMN id BIGINT DEFAULT NEXTVAL('loan_id_seq');
ALTER TABLE loan_installment ALTER COLUMN id BIGINT DEFAULT NEXTVAL('loan_installment_id_seq');

INSERT INTO customer (name, surname, credit_limit, used_credit_limit)
VALUES
    ('John', 'Doe', 10000.00, 0.00),
    ('Jane', 'Smith', 15000.00, 0.00),
    ('Alice', 'Johnson', 12000.00, 0.00),
    ('Bob', 'Brown', 20000.00, 0.00);