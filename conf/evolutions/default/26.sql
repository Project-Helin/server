# ADd unique constriant to user

# --- !Ups
ALTER TABLE users ADD CONSTRAINT cx_users_email UNIQUE (email);

# --- !Downs
ALTER TABLE users drop CONSTRAINT cx_users_email;

