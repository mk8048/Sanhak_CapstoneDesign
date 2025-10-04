CREATE TABLE `users` (
  `id`               BIGINT  NOT NULL,
  `password`         VARCHAR(255) NOT NULL,
  `name`             VARCHAR(100) NOT NULL,
  `email`            VARCHAR(255) NOT NULL,
  `phone`            VARCHAR(255) NULL,
  `location`         VARCHAR(100) NULL,
  `job`              VARCHAR(255) NULL,
  `purpose`          VARCHAR(255) NULL DEFAULT NULL,
  `nickname`         VARCHAR(50)  NULL,
  `github_url`       VARCHAR(255) NULL,
  `profile_image_url` VARCHAR(255) NULL,
  `created_at`       TIMESTAMP NOT NULL,
  `updated_at`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `notes` (
  `id`         BIGINT NOT NULL,
  `project_id` BIGINT NULL,
  `author_id`  BIGINT NULL,
  `content`    TEXT   NULL,
  `created_at` TIMESTAMP NOT NULL,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `project_members` (
  `project_id` BIGINT NOT NULL,
  `user_id`    BIGINT NOT NULL,
  `role`       ENUM('PM','PL','MEMBER') NOT NULL,
  `joined_at`  TIMESTAMP NULL
);

CREATE TABLE `projects` (
  `prj_id`     BIGINT NOT NULL,
  `prj_name`   VARCHAR(100) NOT NULL,
  `description` TEXT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `users_id`   BIGINT NULL
);

CREATE TABLE `deliverables` (
  `id`           BIGINT NOT NULL,
  `user_id`      BIGINT NOT NULL,
  `project_id`   BIGINT NOT NULL,
  `title`        VARCHAR(255) NOT NULL,
  `type`         ENUM('REPORT','PRESENTATION','SOURCE_CODE','DOCUMENT','ETC') NOT NULL,
  `file_url`     VARCHAR(500) NULL,
  `description`  TEXT NULL,
  `created_at`   TIMESTAMP NOT NULL,
  `updated_at`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `meetings` (
  `id`            BIGINT NOT NULL,
  `user_id`       BIGINT NOT NULL,
  `project_id`    BIGINT NOT NULL,
  `title`         VARCHAR(255) NOT NULL,
  `content`       TEXT NULL,
  `recording_url` VARCHAR(500) NULL,
  `summary`       TEXT NULL,
  `meeting_date`  TIMESTAMP NOT NULL
);

CREATE TABLE `gantt_tasks` (
  `id`          BIGINT NOT NULL,
  `project_id`  BIGINT NOT NULL,
  `name`        VARCHAR(255) NOT NULL,
  `start_date`  DATE NOT NULL,
  `end_date`    DATE NOT NULL,
  `progress`    INT NOT NULL DEFAULT 0,
  `assigned_to` BIGINT NULL
);

CREATE TABLE `issues` (
  `id`         BIGINT NOT NULL,
  `project_id` BIGINT NOT NULL,
  `author_id`  BIGINT NOT NULL,
  `title`      VARCHAR(255) NOT NULL,
  `content`    TEXT NULL,
  `status`     ENUM('OPEN','IN_PROGRESS','RESOLVED') NOT NULL DEFAULT 'OPEN',
  `created_at` TIMESTAMP NOT NULL
);

CREATE TABLE `memos` (
  `id`         BIGINT NOT NULL,
  `project_id` BIGINT NOT NULL,
  `author_id`  BIGINT NOT NULL,
  `content`    TEXT NOT NULL,
  `x_pos`      INT NULL,
  `y_pos`      INT NULL,
  `color`      VARCHAR(50) NULL,
  `created_at` TIMESTAMP NOT NULL
);

CREATE TABLE `contributions` (
  `id`             BIGINT NOT NULL,
  `project_id`     BIGINT NOT NULL,
  `user_id`        BIGINT NOT NULL,
  `commit_count`   INT NOT NULL DEFAULT 0,
  `lines_added`    INT NOT NULL DEFAULT 0,
  `lines_deleted`  INT NOT NULL DEFAULT 0,
  `last_synced_at` TIMESTAMP NOT NULL
);

CREATE TABLE `notifications` (
  `id`         BIGINT NOT NULL,
  `project_id` BIGINT NULL,
  `user_id`    BIGINT NOT NULL,
  `type`       ENUM('ISSUE','DEADLINE','PERMISSION','GENERAL') NOT NULL,
  `message`    TEXT NOT NULL,
  `is_read`    BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` TIMESTAMP NOT NULL
);

CREATE TABLE `work_sessions` (
  `id`           BIGINT NOT NULL,
  `user_id`      BIGINT NOT NULL,
  `project_id`   BIGINT NOT NULL,
  `start_time`   TIMESTAMP NOT NULL,
  `end_time`     TIMESTAMP NULL,
  `duration_min` INT NULL,
  `is_active`    BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE `messages` (
  `id`         BIGINT NOT NULL,
  `project_id` BIGINT NOT NULL,
  `author_id`  BIGINT NOT NULL,
  `content`    TEXT NOT NULL,
  `created_at` TIMESTAMP NOT NULL
);

CREATE TABLE `calendar_events` (
  `id`          BIGINT NOT NULL,
  `project_id`  BIGINT NOT NULL,
  `user_id`     BIGINT NOT NULL,
  `title`       VARCHAR(255) NOT NULL,
  `description` TEXT NULL,
  `start_time`  TIMESTAMP NOT NULL,
  `end_time`    TIMESTAMP NOT NULL
);

ALTER TABLE `users`           ADD CONSTRAINT `PK_USERS` PRIMARY KEY (`id`);
ALTER TABLE `notes`           ADD CONSTRAINT `PK_NOTES` PRIMARY KEY (`id`);
ALTER TABLE `project_members` ADD CONSTRAINT `PK_PROJECT_MEMBERS` PRIMARY KEY (`project_id`, `user_id`);
ALTER TABLE `projects`        ADD CONSTRAINT `PK_PROJECTS` PRIMARY KEY (`prj_id`);
ALTER TABLE `deliverables`    ADD CONSTRAINT `PK_DELIVERABLES`     PRIMARY KEY (`id`);
ALTER TABLE `meetings`        ADD CONSTRAINT `PK_MEETINGS`         PRIMARY KEY (`id`);
ALTER TABLE `gantt_tasks`     ADD CONSTRAINT `PK_GANTT_TASKS`      PRIMARY KEY (`id`);
ALTER TABLE `issues`          ADD CONSTRAINT `PK_ISSUES`           PRIMARY KEY (`id`);
ALTER TABLE `memos`           ADD CONSTRAINT `PK_MEMOS`            PRIMARY KEY (`id`);
ALTER TABLE `contributions`   ADD CONSTRAINT `PK_CONTRIBUTIONS`    PRIMARY KEY (`id`);
ALTER TABLE `notifications`   ADD CONSTRAINT `PK_NOTIFICATIONS`    PRIMARY KEY (`id`);
ALTER TABLE `work_sessions`   ADD CONSTRAINT `PK_WORK_SESSIONS`    PRIMARY KEY (`id`);
ALTER TABLE `messages`        ADD CONSTRAINT `PK_MESSAGES`         PRIMARY KEY (`id`);
ALTER TABLE `calendar_events` ADD CONSTRAINT `PK_CALENDAR_EVENTS`  PRIMARY KEY (`id`);


ALTER TABLE `project_members` ADD CONSTRAINT `FK_projects_TO_project_members_1`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
  
ALTER TABLE `project_members` ADD CONSTRAINT `FK_users_TO_project_members_1`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `notes` ADD CONSTRAINT `FK_notes`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `notes` ADD CONSTRAINT `FK_notes`
  FOREIGN KEY (`author_id`) REFERENCES `users` (`id`);

ALTER TABLE `deliverables` ADD CONSTRAINT `FK_deliverables_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `deliverables` ADD CONSTRAINT `FK_deliverables_project`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `meetings` ADD CONSTRAINT `FK_meetings_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `meetings` ADD CONSTRAINT `FK_meetings_project`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `gantt_tasks` ADD CONSTRAINT `FK_gantt_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `gantt_tasks` ADD CONSTRAINT `FK_gantt_user`
  FOREIGN KEY (`assigned_to`) REFERENCES `users` (`id`);

ALTER TABLE `issues` ADD CONSTRAINT `FK_issues_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `issues` ADD CONSTRAINT `FK_issues_author`
  FOREIGN KEY (`author_id`) REFERENCES `users` (`id`);

ALTER TABLE `memos` ADD CONSTRAINT `FK_memos_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `memos` ADD CONSTRAINT `FK_memos_author`
  FOREIGN KEY (`author_id`) REFERENCES `users` (`id`);

ALTER TABLE `contributions` ADD CONSTRAINT `FK_contrib_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `contributions` ADD CONSTRAINT `FK_contrib_user`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `notifications` ADD CONSTRAINT `FK_notifications_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `notifications` ADD CONSTRAINT `FK_notifications_user`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `work_sessions` ADD CONSTRAINT `FK_sessions_user`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
ALTER TABLE `work_sessions` ADD CONSTRAINT `FK_sessions_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);

ALTER TABLE `messages` ADD CONSTRAINT `FK_messages_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `messages` ADD CONSTRAINT `FK_messages_author`
  FOREIGN KEY (`author_id`) REFERENCES `users` (`id`);

ALTER TABLE `calendar_events` ADD CONSTRAINT `FK_calendar_project`
  FOREIGN KEY (`project_id`) REFERENCES `projects` (`prj_id`);
ALTER TABLE `calendar_events` ADD CONSTRAINT `FK_calendar_user`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
