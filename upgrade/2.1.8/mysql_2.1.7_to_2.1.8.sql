ALTER TABLE jforum_posts ADD INDEX(need_moderate);
ALTER TABLE jforum_topics ADD topic_moved_id INT DEFAULT 0;
ALTER TABLE jforum_topics ADD INDEX(topic_moved_id);
ALTER TABLE jforum_users MODIFY rank_id rank_id INT DEFAULT 1;

DROP TABLE jforum_search_words;
DROP TABLE jforum_search_wordmatch;
DROP TABLE jforum_search_results;
DROP TABLE jforum_search_topics;

DROP TABLE IF EXISTS jforum_moderation_log;
CREATE TABLE jforum_moderation_log (
	log_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	log_description TEXT NOT NULL,
	log_original_message TEXT,
	log_date DATETIME NOT NULL,
	log_type TINYINT DEFAULT 0,
	post_id INT DEFAULT 0,
	topic_id INT DEFAULT 0,
	post_user_id INT DEFAULT 0,
	KEY(user_id),
	KEY(post_user_id)
) TYPE=InnoDB;
