CREATE TABLE `users` (
	`id`	BIGINT	NOT NULL,
	`password`	VARCHAR(255)	NOT NULL,
	`name`	VARCHAR(100)	NOT NULL,
	`email`	VARCHAR(255)	NOT NULL,
	`phone`	VARCHAR(255)	NULL,
	`location`	VARCHAR(100)	NULL,
	`job`	VARCHAR(255)	NULL,
	`purpose`	VARCHAR(255)	NULL	DEFAULT NULL,
	`nickname`	VARCHAR(50)	NULL,
	`github_url`	VARCHAR(255)	NULL,
	`profile_image_url`	VARCHAR(255)	NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `notes` (
	`id`	BIGINT	NOT NULL,
	`project_id`	BIGINT	NULL,
	`author_id`	BIGINT	NULL,
	`content`	TEXT	NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `project_members` (
	`project_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NOT NULL,
	`role`	ENUM('PM','PL','MEMBER')	NOT NULL,
	`joined_at`	TIMESTAMP	NULL
);

CREATE TABLE `projects` (
	`prj_id`	BIGINT	NOT NULL,
	`prj_name`	VARCHAR(100)	NOT NULL,
	`description`	TEXT	NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`users_id`	BIGINT	NULL
);

ALTER TABLE `users` ADD CONSTRAINT `PK_USERS` PRIMARY KEY (
	`id`
);

ALTER TABLE `notes` ADD CONSTRAINT `PK_NOTES` PRIMARY KEY (
	`id`
);

ALTER TABLE `project_members` ADD CONSTRAINT `PK_PROJECT_MEMBERS` PRIMARY KEY (
	`project_id`,
	`user_id`
);

ALTER TABLE `projects` ADD CONSTRAINT `PK_PROJECTS` PRIMARY KEY (
	`prj_id`
);

ALTER TABLE `project_members` ADD CONSTRAINT `FK_projects_TO_project_members_1` FOREIGN KEY (
	`project_id`
)
REFERENCES `projects` (
	`prj_id`
);

ALTER TABLE `project_members` ADD CONSTRAINT `FK_users_TO_project_members_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `users` (
	`id`
);
