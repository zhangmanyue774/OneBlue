-- 音乐标签
CREATE TABLE IF NOT EXISTS music_tag (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ARTIST TEXT,
    ALBUM TEXT,
    YEAR TEXT,
    TITLE TEXT,
    LYRICS TEXT,
    COVER_ART TEXT,
    FileName TEXT UNIQUE,
    PLAY_URL TEXT,
    SOURCE TEXT
);
-- WebDav音乐标签
CREATE TABLE IF NOT EXISTS webdav_music_tag (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ARTIST TEXT,
    ALBUM TEXT,
    YEAR TEXT,
    TITLE TEXT,
    LYRICS TEXT,
    COVER_ART TEXT,
    FileName TEXT UNIQUE,
    PLAY_URL TEXT,
    SOURCE TEXT
);
-- 系统配置
CREATE TABLE IF NOT EXISTS configs (
    key TEXT UNIQUE,
    value TEXT
);
-- 用户表
CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY,
                                     email  TEXT UNIQUE NOT NULL,
                                     password TEXT NOT NULL
);
-- 角色表
CREATE TABLE IF NOT EXISTS roles (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     name TEXT UNIQUE NOT NULL
);
-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           name TEXT UNIQUE NOT NULL
);
-- 用户角色表
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id INTEGER,
                                          role_id INTEGER,
                                          FOREIGN KEY(user_id) REFERENCES users(id),
                                          FOREIGN KEY(role_id) REFERENCES roles(id),
                                          PRIMARY KEY(user_id, role_id)
);
-- 角色权限表
CREATE TABLE IF NOT EXISTS role_permissions (
                                                role_id INTEGER,
                                                permission_id INTEGER,
                                                FOREIGN KEY(role_id) REFERENCES roles(id),
                                                FOREIGN KEY(permission_id) REFERENCES permissions(id),
                                                PRIMARY KEY(role_id, permission_id)
);
-- 分享表
CREATE TABLE IF NOT EXISTS shares (
                                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                                      share_id TEXT UNIQUE NOT NULL,
                                      share_song TEXT NOT NULL,
                                      share_expire_time TEXT NOT NULL
);