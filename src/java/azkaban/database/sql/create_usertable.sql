CREATE TABLE user_accounts (
  id INT NOT NULL PRIMARY KEY,
  username VARCHAR(16) NOT NULL,
  email VARCHAR(64),
  password_hash BINARY(32),
  password_salt BINARY(8),
  update_time TIMESTAMP,
  create_time TIMESTAMP
);
CREATE UNIQUE INDEX username ON user_accounts;
