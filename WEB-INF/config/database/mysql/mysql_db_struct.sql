-- MySQL dump 9.07
--
-- Host: localhost    Database: jforum
---------------------------------------------------------
-- Server version	4.0.12-nt

--
-- Table structure for table 'jforum_banlist'
--

DROP TABLE IF EXISTS jforum_banlist;
CREATE TABLE jforum_banlist (
  banlist_id mediumint(8) NOT NULL auto_increment,
  user_id mediumint(8) NOT NULL default '0',
  banlist_ip varchar(8) NOT NULL default '',
  banlist_email varchar(255) default NULL,
  PRIMARY KEY  (banlist_id),
  INDEX idx_user (user_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_categories'
--

DROP TABLE IF EXISTS jforum_categories;
CREATE TABLE jforum_categories (
  categories_id mediumint(8) NOT NULL auto_increment,
  title varchar(100) NOT NULL default '',
  display_order mediumint(8) NOT NULL default '0',
  PRIMARY KEY  (categories_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_config'
--

DROP TABLE IF EXISTS jforum_config;
CREATE TABLE jforum_config (
  config_name varchar(255) NOT NULL default '',
  config_value varchar(255) NOT NULL default '',
  config_id int not null auto_increment,
  PRIMARY KEY(config_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_forums'
--

DROP TABLE IF EXISTS jforum_forums;
CREATE TABLE jforum_forums (
  forum_id smallint(5) NOT NULL auto_increment,
  categories_id mediumint(8) NOT NULL default '1',
  forum_name varchar(150) NOT NULL default '',
  forum_desc varchar(255) default NULL,
  forum_order mediumint(8) default '1',
  forum_topics mediumint(8) NOT NULL default '0',
  forum_last_post_id mediumint(8) NOT NULL default '0',
  moderated smallint(6) default '0',
  PRIMARY KEY  (forum_id),
  KEY (categories_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_groups'
--

DROP TABLE IF EXISTS jforum_groups;
CREATE TABLE jforum_groups (
  group_id mediumint(8) NOT NULL auto_increment,
  group_name varchar(40) NOT NULL default '',
  group_description varchar(255) default NULL,
  parent_id mediumint(8) default '0',
  PRIMARY KEY  (group_id)
) TYPE=InnoDB;


DROP TABLE IF EXISTS jforum_user_groups;
CREATE TABLE jforum_user_groups (
	group_id INT NOT NULL,
	user_id INT NOT NULL,
	INDEX idx_group (group_id),
	INDEX idx_user (user_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_roles'
--

DROP TABLE IF EXISTS jforum_roles;
CREATE TABLE jforum_roles (
  role_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  group_id mediumint(8) default '0',
  user_id mediumint(8) default '0',
  name varchar(255) NOT NULL,
  role_type TINYINT(1) DEFAULT 1,
  INDEX idx_group (group_id),
  INDEX idx_user (user_id),
  INDEX idx_name (name)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_role_values'
--
DROP TABLE IF EXISTS jforum_role_values;
CREATE TABLE jforum_role_values (
  role_id INT NOT NULL,
  role_value VARCHAR(255),
  role_type TINYINT(1) DEFAULT 1,
  INDEX idx_role(role_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_posts'
--

DROP TABLE IF EXISTS jforum_posts;
CREATE TABLE jforum_posts (
  post_id mediumint(8) NOT NULL auto_increment,
  topic_id mediumint(8) NOT NULL default '0',
  forum_id smallint(5) NOT NULL default '0',
  user_id mediumint(8) default NULL,
  post_time datetime default NULL,
  poster_ip varchar(15) default NULL,
  enable_bbcode tinyint(1) NOT NULL default '1',
  enable_html tinyint(1) NOT NULL default '1',
  enable_smilies tinyint(1) NOT NULL default '1',
  enable_sig tinyint(1) NOT NULL default '1',
  post_edit_time datetime default NULL,
  post_edit_count smallint(5) NOT NULL default '0',
  status tinyint(1) default '1',
  PRIMARY KEY  (post_id),
  KEY (user_id),
  KEY (topic_id),
  KEY (forum_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_posts_text'
--
DROP TABLE IF EXISTS jforum_posts_text;
CREATE TABLE jforum_posts_text (
	post_id MEDIUMINT(8) NOT NULL PRIMARY KEY,
	post_text TEXT,
	post_subject VARCHAR(100)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_privmsgs'
--

DROP TABLE IF EXISTS jforum_privmsgs;
CREATE TABLE jforum_privmsgs (
  privmsgs_id mediumint(8) NOT NULL auto_increment,
  privmsgs_type tinyint(4) NOT NULL default '0',
  privmsgs_subject varchar(255) NOT NULL default '',
  privmsgs_from_userid mediumint(8) NOT NULL default '0',
  privmsgs_to_userid mediumint(8) NOT NULL default '0',
  privmsgs_date datetime default null,
  privmsgs_ip varchar(8) NOT NULL default '',
  privmsgs_enable_bbcode tinyint(1) NOT NULL default '1',
  privmsgs_enable_html tinyint(1) NOT NULL default '0',
  privmsgs_enable_smilies tinyint(1) NOT NULL default '1',
  privmsgs_attach_sig tinyint(1) NOT NULL default '1',
  PRIMARY KEY  (privmsgs_id)
) TYPE=InnoDB;

DROP TABLE IF EXISTS jforum_privmsgs_text;
CREATE TABLE jforum_privmsgs_text (
	privmsgs_id MEDIUMINT(8) NOT NULL,
	privmsgs_text TEXT,
	PRIMARY KEY ( privmsgs_id )
) Type=InnoDB;

--
-- Table structure for table 'jforum_ranks'
--

DROP TABLE IF EXISTS jforum_ranks;
CREATE TABLE jforum_ranks (
  rank_id smallint(5) NOT NULL auto_increment,
  rank_title varchar(50) NOT NULL default '',
  rank_min mediumint(8) NOT NULL default '0',
  rank_special tinyint(1) default NULL,
  rank_image varchar(255) default NULL,
  PRIMARY KEY  (rank_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_sessions'
--

DROP TABLE IF EXISTS jforum_sessions;
CREATE TABLE jforum_sessions (
  session_id varchar(50) NOT NULL default '',
  session_user_id mediumint(8) NOT NULL default '0',
  session_start datetime default null,
  session_time bigint default '0',
  session_ip varchar(8) NOT NULL default '',
  session_page int(11) NOT NULL default '0',
  session_logged_int tinyint(1) default NULL
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_smilies'
--

DROP TABLE IF EXISTS jforum_smilies;
CREATE TABLE jforum_smilies (
  smilie_id smallint(5) NOT NULL auto_increment,
  code varchar(50) NOT NULL default '',
  url varchar(100) default NULL,
  disk_name varchar(255),
  PRIMARY KEY  (smilie_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_themes'
--

DROP TABLE IF EXISTS jforum_themes;
CREATE TABLE jforum_themes (
  themes_id mediumint(8) NOT NULL auto_increment,
  template_name varchar(30) NOT NULL default '',
  style_name varchar(30) NOT NULL default '',
  PRIMARY KEY  (themes_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_topics'
--

DROP TABLE IF EXISTS jforum_topics;
CREATE TABLE jforum_topics (
  topic_id mediumint(8) NOT NULL auto_increment,
  forum_id smallint(8) NOT NULL default '0',
  topic_title varchar(100) NOT NULL default '',
  user_id mediumint(8) NOT NULL default '0',
  topic_time datetime default null,
  topic_views mediumint(8) default '1',
  topic_replies mediumint(8) default '0',
  topic_status tinyint(3) default '0',
  topic_vote tinyint(1) default '0',
  topic_type tinyint(3) default '0',
  topic_first_post_id mediumint(8) default '0',
  topic_last_post_id mediumint(8) NOT NULL default '0',
  moderated smallint(1) default '0',
  PRIMARY KEY  (topic_id),
  KEY (forum_id),
  KEY(user_id),
  KEY(topic_first_post_id),
  KEY(topic_last_post_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_topics_watch'
--

DROP TABLE IF EXISTS jforum_topics_watch;
CREATE TABLE jforum_topics_watch (
  topic_id mediumint(8) NOT NULL default '0',
  user_id mediumint(8) NOT NULL default '0',
  is_read tinyint(1) NOT NULL DEFAULT '0',
  INDEX idx_topic (topic_id),
  INDEX idx_user (user_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_users'
--

DROP TABLE IF EXISTS jforum_users;
CREATE TABLE jforum_users (
  user_id mediumint(8) NOT NULL auto_increment,
  user_active tinyint(1) default NULL,
  username varchar(50) NOT NULL default '',
  user_password varchar(32) NOT NULL default '',
  user_session_time bigint default 0,
  user_session_page smallint(5) NOT NULL default '0',
  user_lastvisit datetime default null,
  user_regdate datetime default null,
  user_level tinyint(4) default NULL,
  user_posts mediumint(8) NOT NULL default '0',
  user_timezone varchar(5) NOT NULL default '',
  user_style tinyint(4) default NULL,
  user_lang varchar(255) NOT NULL default '',
  user_dateformat varchar(20) NOT NULL default '%d/%M/%Y %H:%i',
  user_new_privmsg smallint(5) NOT NULL default '0',
  user_unread_privmsg smallint(5) NOT NULL default '0',
  user_last_privmsg datetime NULL,
  user_emailtime datetime default NULL,
  user_viewemail tinyint(1) default '0',
  user_attachsig tinyint(1) default '1',
  user_allowhtml tinyint(1) default '0',
  user_allowbbcode tinyint(1) default '1',
  user_allowsmilies tinyint(1) default '1',
  user_allowavatar tinyint(1) default '1',
  user_allow_pm tinyint(1) default '1',
  user_allow_viewonline tinyint(1) default '1',
  user_notify tinyint(1) default '1',
  user_notify_pm tinyint(1) default '1',
  user_popup_pm tinyint(1) default '1',
  rank_id smallint(5) default '1',
  user_avatar varchar(100) default NULL,
  user_avatar_type tinyint(4) NOT NULL default '0',
  user_email varchar(255) NOT NULL default '',
  user_icq varchar(15) default NULL,
  user_website varchar(100) default NULL,
  user_from varchar(100) default NULL,
  user_sig text,
  user_sig_bbcode_uid varchar(10) default NULL,
  user_aim varchar(255) default NULL,
  user_yim varchar(255) default NULL,
  user_msnm varchar(255) default NULL,
  user_occ varchar(100) default NULL,
  user_interests varchar(255) default NULL,
  user_actkey varchar(32) default NULL,
  gender char(1) default NULL,
  themes_id mediumint(8) default NULL,
  deleted tinyint(1) default NULL,
  user_viewonline tinyint(1) default '1',
  security_hash varchar(32),
  user_karma DECIMAL(10,2),
  PRIMARY KEY  (user_id)
) TYPE=InnoDB;


--
-- Table structure for table 'jforum_vote_desc'
--

DROP TABLE IF EXISTS jforum_vote_desc;
CREATE TABLE jforum_vote_desc (
  vote_id mediumint(8) NOT NULL auto_increment,
  topic_id mediumint(8) NOT NULL default '0',
  vote_text text NOT NULL,
  vote_start int(11) NOT NULL default '0',
  vote_length int(11) NOT NULL default '0',
  PRIMARY KEY  (vote_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_vote_results'
--

DROP TABLE IF EXISTS jforum_vote_results;
CREATE TABLE jforum_vote_results (
  vote_id mediumint(8) NOT NULL default '0',
  vote_option_id tinyint(4) NOT NULL default '0',
  vote_option_text varchar(255) NOT NULL default '',
  vote_result int(11) NOT NULL default '0'
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_vote_voters'
--

DROP TABLE IF EXISTS jforum_vote_voters;
CREATE TABLE jforum_vote_voters (
  vote_id mediumint(8) NOT NULL default '0',
  vote_user_id mediumint(8) NOT NULL default '0',
  vote_user_ip char(8) NOT NULL default ''
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_words'
--

DROP TABLE IF EXISTS jforum_words;
CREATE TABLE jforum_words (
  word_id mediumint(8) NOT NULL auto_increment,
  word varchar(100) NOT NULL default '',
  replacement varchar(100) NOT NULL default '',
  PRIMARY KEY  (word_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_search_words'
--
DROP TABLE IF EXISTS jforum_search_words;
CREATE TABLE jforum_search_words (
  word_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  word VARCHAR(100) NOT NULL,
  word_hash INT,
  KEY(word),
  KEY(word_hash)
) TYPE=InnoDB;

-- 
-- Table structure for table 'jforum_search_wordmatch'
--
DROP TABLE IF EXISTS jforum_search_wordmatch;
CREATE TABLE jforum_search_wordmatch (
  post_id INT NOT NULL,
  word_id INT NOT NULL,
  title_match TINYINT(1) DEFAULT '0',
  KEY(post_id),
  KEY(word_id),
  KEY(title_match)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_search_results'
--
DROP TABLE IF EXISTS jforum_search_results;
CREATE TABLE jforum_search_results (
  topic_id INT NOT NULL,
  session VARCHAR(50),
  search_time DATETIME,
  KEY (topic_id)
) TYPE=InnoDB;


DROP TABLE IF EXISTS jforum_search_topics;
CREATE TABLE jforum_search_topics (
  topic_id mediumint(8) NOT NULL,
  forum_id smallint(8) NOT NULL default '0',
  topic_title varchar(60) NOT NULL default '',
  user_id mediumint(8) NOT NULL default '0',
  topic_time datetime default null,
  topic_views mediumint(8) default '1',
  topic_replies mediumint(8) default '0',
  topic_status tinyint(3) default '0',
  topic_vote tinyint(1) default '0',
  topic_type tinyint(3) default '0',
  topic_first_post_id mediumint(8) default '0',
  topic_last_post_id mediumint(8) NOT NULL default '0',
  moderated smallint(1) default '0',
  session varchar(50),
  search_time datetime,
  KEY  (topic_id),
  KEY (forum_id),
  KEY(user_id),
  KEY(topic_first_post_id),
  KEY(topic_last_post_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_karma'
--
DROP TABLE IF EXISTS jforum_karma;
CREATE TABLE jforum_karma (
	karma_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	post_id INT NOT NULL,
	topic_id INT NOT NULL,
	post_user_id INT NOT NULL,
	from_user_id INT NOT NULL,
	points INT NOT NULL
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_bookmark'
--
DROP TABLE IF EXISTS jforum_bookmarks;
CREATE TABLE jforum_bookmarks (
	bookmark_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	relation_id INT NOT NULL,
	relation_type INT NOT NULL,
	public_visible INT DEFAULT '1',
	title varchar(255),
	description varchar(255),
	INDEX book_idx_relation (relation_id)
) TYPE=InnoDB;
-- 
-- Table structure for table 'jforum_quota_limits'
--
DROP TABLE IF EXISTS jforum_quota_limits;
CREATE TABLE jforum_quota_limits (
	quota_limit_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	quota_desc VARCHAR(50) NOT NULL,
	quota_limit INT NOT NULL,
	quota_type TINYINT(1) DEFAULT '1'
) TYPE=InnoDB;