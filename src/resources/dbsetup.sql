CREATE TABLE IF NOT EXISTS ah_server
(
    server_ip VARCHAR(32) NOT NULL,
    server_port INT UNSIGNED NOT NULL,
    total_death_bans INT UNSIGNED NOT NULL,
    PRIMARY KEY (server_ip, server_port)
);

CREATE TABLE IF NOT EXISTS ah_player
(
    player_uuid CHAR(36) NOT NULL,
    last_known_name VARCHAR(16) NOT NULL,
    last_known_ip VARCHAR(39),
    lives INT UNSIGNED NOT NULL,
    life_parts INT UNSIGNED NOT NULL,
    spectator_banned TINYINT NOT NULL,
    time_till_next_revive BIGINT UNSIGNED NOT NULL,
    time_till_next_life_part BIGINT UNSIGNED NOT NULL,
    time_till_next_max_health BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (player_uuid)
);

CREATE TABLE IF NOT EXISTS ah_ban
(
    ban_id INT UNSIGNED NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    server_ip VARCHAR(32) DEFAULT NULL,
    server_port INT UNSIGNED DEFAULT NULL,
    start_date DATETIME NOT NULL,
    expiration_date DATETIME NOT NULL,
    ban_time INT NOT NULL,
    damage_cause VARCHAR(45) NOT NULL,
    damage_cause_type VARCHAR(45) NOT NULL,
    world VARCHAR(45) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    has_killer TINYINT NOT NULL,
    killer_name VARCHAR(45) NULL,
    killer_display_name VARCHAR(45) NULL,
    killer_entity_type VARCHAR(45) NULL,
    in_combat TINYINT NULL,
    in_combat_with_name VARCHAR(45) NULL,
    in_combat_with_display_name VARCHAR(45) NULL,
    in_combat_with_entity_type VARCHAR(45) NULL,
    death_message TINYTEXT NOT NULL,
    time_since_previous_death_ban BIGINT UNSIGNED NOT NULL,
    time_since_previous_death BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (ban_id,player_uuid),
    CONSTRAINT player_uuid FOREIGN KEY (player_uuid) REFERENCES ah_player (player_uuid) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_server FOREIGN KEY (server_ip, server_port) REFERENCES ah_server (server_ip, server_port) ON DELETE SET NULL ON UPDATE SET NULL
);