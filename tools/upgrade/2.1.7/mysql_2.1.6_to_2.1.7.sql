ALTER TABLE jforum_users ADD COLUMN user_authhash VARCHAR(32);

DELETE FROM jforum_roles WHERE name <> 'perm_administration';
DELETE FROM jforum_roles WHERE role_type = 0;
DELETE FROM jforum_role_values;

ALTER TABLE jforum_roles DROP COLUMN role_type;
ALTER TABLE jforum_role_values DROP COLUMN role_type;

CREATE TABLE jforum_mail_integration (
	forum_id INT NOT NULL,
	forum_email VARCHAR(100) NOT NULL,
	pop_username VARCHAR(100) NOT NULL,
	pop_password VARCHAR(100) NOT NULL,
	pop_host VARCHAR(100) NOT NULL,
	pop_port INT DEFAULT 110,
	KEY(forum_id)
) TYPE=InnoDB;

CREATE TABLE jforum_api (
	api_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	api_key VARCHAR(32) NOT NULL,
	api_validity DATETIME NOT NULL,
	api_hash VARCHAR(32) NOT NULL
);