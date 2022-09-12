


CREATE TABLE db-demo.`user` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`age` INT(11) NULL DEFAULT '0',
	`name` VARCHAR(45) NULL DEFAULT '' COLLATE 'utf8mb4_general_ci',
	`salary` DECIMAL(10,6) NULL DEFAULT '0.000000',
	`create_time` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci' ENGINE=InnoDB ;
