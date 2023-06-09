CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE messages (
  id INT NOT NULL AUTO_INCREMENT,
  sender VARCHAR(50) NOT NULL,
  recipient VARCHAR(50) NOT NULL,
  content TEXT NOT NULL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

