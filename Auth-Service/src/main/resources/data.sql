INSERT INTO client (id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name,
                    client_authentication_methods, authorization_grant_types, redirect_uris, post_logout_redirect_uris,
                    scopes, client_settings, token_settings)
SELECT 'd9cd8704-a670-46bc-af5d-7695362386f4',
       'client',
       CURRENT_TIMESTAMP,
       '$2a$12$2us28pQsxxJ7cX59nQfVDu1m2fya/ncp8oUs5W8BA78APWFOzLQ6O',
       NULL,
       'abbc70f1-fb59-4b42-b1e4-c52fa0080bea',
       'client_secret_basic',
       'refresh_token,client_credentials,authorization_code,custom_password',
       'http://127.0.0.1:8080/login/oauth2/code/client',
       '',
       'read,profile',
       '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}',
       '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token' ||
       '.access-token-time-to-live":["java.time.Duration",60.000000000],"settings.token' ||
       '.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings' ||
       '.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration",' ||
       '3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}' WHERE NOT EXISTS (SELECT 'd9cd8704-a670-46bc-af5d-7695362386f4' FROM client WHERE client_id = 'client');


-- add initial super admin role

INSERT INTO role(id, name, status, created_at)
SELECT 'ff23eb3b-cc89-46f6-a567-0c4f90d5d2b4',
       'Super Admin',
       'ACTIVE',
       CURRENT_TIMESTAMP WHERE NOT EXISTS(SELECT 'ff23eb3b-cc89-46f6-a567-0c4f90d5d2b4'
                 FROM role
                 WHERE name = 'Super Admin');

-- add initial super admin user

INSERT INTO users(id, email_address, mobile_number, is_account_non_expired, is_account_non_locked,
                  is_credentials_non_expired,
                  is_enabled, status, password, role_ids, created_at)
SELECT 'cc934a28-bda1-4292-8861-f088ce86603f',
       'superadmin@hrmsuite.com',
       '9766919210',
       true,
       true,
       true,
       true,
       'ACTIVE',
       '$2a$12$w0tMEu35demh3kKj8Lgs.uGIu4RnVdaWE6cUGNAbCNTbScpM8h7mW',
       '["ff23eb3b-cc89-46f6-a567-0c4f90d5d2b4"]'::jsonb,
       CURRENT_TIMESTAMP WHERE NOT EXISTS(SELECT 1 FROM users WHERE id = 'cc934a28-bda1-4292-8861-f088ce86603f');

INSERT INTO user_role(user_id, role_id)
SELECT 'cc934a28-bda1-4292-8861-f088ce86603f', 'ff23eb3b-cc89-46f6-a567-0c4f90d5d2b4'
WHERE NOT EXISTS (
    SELECT 1 FROM user_role
    WHERE user_id = 'cc934a28-bda1-4292-8861-f088ce86603f'
      AND role_id = 'ff23eb3b-cc89-46f6-a567-0c4f90d5d2b4'
);


--- initial super admin permission

INSERT INTO permission(id, name, code, description, status, created_at)
SELECT '8e3e5f90-a242-4920-953c-c8ead9c122a1',
       'Super Admin',
       'SUPER_ADMIN',
       'Super Admin can access all features.',
       'ACTIVE',
       CURRENT_TIMESTAMP WHERE NOT EXISTS(SELECT '8e3e5f90-a242-4920-953c-c8ead9c122a1' FROM permission WHERE code = 'SUPER_ADMIN');


INSERT INTO permission(id, name, code, description, status, created_at)
SELECT '0f226755-8c7b-4428-9de4-d5d0fa2fa035',
       'Create User',
       'CREATE_USER',
       'User creation permission.',
       'ACTIVE',
       CURRENT_TIMESTAMP WHERE NOT EXISTS(SELECT '0f226755-8c7b-4428-9de4-d5d0fa2fa035' FROM permission WHERE code = 'CREATE_USER');


--- initial super admin role permission

INSERT INTO role_permission(role_id, permission_id)
SELECT 'ff23eb3b-cc89-46f6-a567-0c4f90d5d2b4',
       '8e3e5f90-a242-4920-953c-c8ead9c122a1' WHERE NOT EXISTS(SELECT '8e3e5f90-a242-4920-953c-c8ead9c122a1' FROM role_permission WHERE permission_id = '8e3e5f90-a242-4920-953c-c8ead9c122a1');