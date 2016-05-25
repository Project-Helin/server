# Updated fields on customer

# --- !Ups
ALTER TABLE customers RENAME display_name to given_name;
ALTER TABLE customers add column family_name VARCHAR(255);
ALTER TABLE customers drop column token;

# --- !Downs
ALTER TABLE customers RENAME given_name to display_name;
ALTER TABLE customers ADD COLUMN TOKEN VARCHAR(255);
ALTER TABLE customers drop column TOKEN;

