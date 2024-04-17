-- 插入角色
INSERT OR IGNORE INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT OR IGNORE INTO roles (name) VALUES ('ROLE_USER');
INSERT OR IGNORE INTO roles (name) VALUES ('ROLE_GUEST');

-- 插入权限（顺序））
INSERT OR IGNORE INTO permissions (name) VALUES ('SystemConfig');
INSERT OR IGNORE INTO permissions (name) VALUES ('DownloadFile');
INSERT OR IGNORE INTO permissions (name) VALUES ('UploadFile');
INSERT OR IGNORE INTO permissions (name) VALUES ('ShareMusic');

-- （获取刚插入的角色和权限的ID）将权限分配给角色
INSERT OR IGNORE INTO role_permissions (role_id, permission_id) VALUES (1, 1);
INSERT OR IGNORE INTO role_permissions (role_id, permission_id) VALUES (1, 2);
INSERT OR IGNORE INTO role_permissions (role_id, permission_id) VALUES (1, 3);
INSERT OR IGNORE INTO role_permissions (role_id, permission_id) VALUES (1, 4);
INSERT OR IGNORE INTO role_permissions (role_id, permission_id) VALUES (2, 2);
INSERT OR IGNORE INTO role_permissions (role_id, permission_id) VALUES (2, 3);
INSERT OR IGNORE INTO role_permissions (role_id, permission_id) VALUES (2, 4);

-- 添加系统配置
INSERT OR IGNORE INTO configs (key, value) VALUES ('oneblue.music-folder-path', '');
INSERT OR IGNORE INTO configs (key, value) VALUES ('oneblue.web-dav.url', '');
INSERT OR IGNORE INTO configs (key, value) VALUES ('oneblue.web-dav.username', '');
INSERT OR IGNORE INTO configs (key, value) VALUES ('oneblue.web-dav.password', '');
INSERT OR IGNORE INTO configs (key, value) VALUES ('isInit', '');
