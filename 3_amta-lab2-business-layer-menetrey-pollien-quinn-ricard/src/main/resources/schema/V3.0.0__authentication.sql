-- Creation of the roles table
CREATE TABLE role (
                       id BIGINT PRIMARY KEY,
                       role TEXT NOT NULL
);

-- Creation of the association table between staff and roles
CREATE TABLE staff_role (
                            users_staff_id INT,
                            roles_id BIGINT,
                            PRIMARY KEY (users_staff_id, roles_id),
                            FOREIGN KEY (users_staff_id) REFERENCES staff(staff_id),
                            FOREIGN KEY (roles_id) REFERENCES role(id)
);