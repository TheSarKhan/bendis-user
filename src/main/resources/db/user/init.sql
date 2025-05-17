DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM users WHERE email = 'admin@example.com'
        ) THEN
            INSERT INTO users (
                name_and_surname,
                google_id,
                profile_img,
                email,
                refresh_token,
                password,
                gender,
                user_code,
                country_code,
                phone_number,
                birth_date,
                created_at,
                updated_at,
                role,
                seller
            ) VALUES (
                         'Admin Adminov',
                         NULL,
                         'https://example.com/default-admin.png',
                         'admin@example.com',
                         NULL,
                         '$2a$10$7q9pR9fQnbu7R1UdKj2d7OLR7cvhL/xWcZBeCB8N41JrQz8AoKJmK', -- "admin123"
                         'MALE',
                         'ADM001',
                         '+994',
                         '501234567',
                         '{
                             "day": 1,
                             "month": 1,
                             "year": 1990
                         }',
                         NOW(),
                         NOW(),
                         'ADMIN',
                         NULL
                     );
        END IF;
    END
$$;
