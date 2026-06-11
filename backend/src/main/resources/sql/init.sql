CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    weight DECIMAL(5,2),
    color VARCHAR(50),
    description TEXT,
    status VARCHAR(20) DEFAULT 'available',
    owner_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_pets_type (type),
    INDEX idx_pets_status (status),
    INDEX idx_pets_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT,
    user_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(255),
    caption TEXT,
    like_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    is_main BOOLEAN DEFAULT FALSE,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_photos_pet (pet_id),
    INDEX idx_photos_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    photo_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT,
    comment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (photo_id) REFERENCES photos(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE SET NULL,
    INDEX idx_comments_photo (photo_id),
    INDEX idx_comments_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    photo_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (photo_id) REFERENCES photos(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_likes_photo_user (photo_id, user_id),
    INDEX idx_likes_photo (photo_id),
    INDEX idx_likes_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    UNIQUE KEY uk_favorites_user_pet (user_id, pet_id),
    INDEX idx_favorites_user (user_id),
    INDEX idx_favorites_pet (pet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS photo_collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    photo_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (photo_id) REFERENCES photos(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_collections_photo_user (photo_id, user_id),
    INDEX idx_collections_photo (photo_id),
    INDEX idx_collections_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS adoptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    message TEXT,
    phone VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_adoptions_pet (pet_id),
    INDEX idx_adoptions_user (user_id),
    INDEX idx_adoptions_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50),
    related_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO users (id, username, password, email, phone, avatar_url, role, created_at) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', 'admin@petplatform.com', '13800138000', '/static/avatars/admin_avatar.jpg', 'admin', '2024-01-01 00:00:00'),
(2, 'manager', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', 'manager@petplatform.com', '13800138001', '/static/avatars/manager_avatar.jpg', 'manager', '2024-01-01 00:00:00'),
(3, 'user1', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', 'user1@example.com', '13800138002', '/static/avatars/user1_avatar.jpg', 'user', '2024-01-02 00:00:00'),
(4, 'user2', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', 'user2@example.com', '13800138003', '/static/avatars/user2_avatar.jpg', 'user', '2024-01-03 00:00:00');

INSERT IGNORE INTO pets (id, name, type, breed, age, gender, description, status, owner_id, created_at) VALUES
(1, '旺财', 'dog', 'Golden Retriever', 3, 'male', '非常可爱的金毛犬，性格温顺，喜欢玩耍', 'available', NULL, '2024-01-05 00:00:00'),
(2, '咪咪', 'cat', 'British Shorthair', 2, 'female', '优雅的英短猫咪，毛色漂亮', 'available', NULL, '2024-01-06 00:00:00'),
(3, '球球', 'dog', 'Poodle', 1, 'male', '聪明活泼的泰迪犬，不掉毛', 'available', NULL, '2024-01-07 00:00:00'),
(4, '可乐', 'cat', 'Persian', 4, 'female', '高贵的波斯猫，毛发柔顺', 'available', NULL, '2024-01-08 00:00:00');

INSERT IGNORE INTO photos (id, pet_id, user_id, image_url, caption, like_count, view_count, is_main, upload_time) VALUES
(1, 1, 3, '/static/images/pet1_1.jpg', '旺财的第一张照片', 10, 50, TRUE, '2024-01-10 10:00:00'),
(2, 1, 3, '/static/images/pet1_2.jpg', '旺财在玩耍', 5, 30, FALSE, '2024-01-10 11:00:00'),
(3, 2, 4, '/static/images/pet2_1.jpg', '咪咪很优雅', 8, 40, TRUE, '2024-01-11 09:00:00'),
(4, 2, 4, '/static/images/pet2_2.jpg', '咪咪在睡觉', 3, 20, FALSE, '2024-01-11 10:00:00');

INSERT IGNORE INTO comments (id, photo_id, user_id, content, comment_time) VALUES
(1, 1, 4, '好可爱的狗狗！', '2024-01-10 12:00:00'),
(2, 1, 3, '谢谢！它很调皮的', '2024-01-10 12:30:00'),
(3, 3, 3, '猫咪看起来很高贵', '2024-01-11 11:00:00');

INSERT IGNORE INTO adoptions (id, pet_id, user_id, status, message, phone, address, created_at) VALUES
(1, 1, 4, 'pending', '我非常喜欢狗狗，家里有院子，可以给它很好的生活环境', '13800138003', '北京市朝阳区xxx小区', '2024-01-12 09:00:00');

INSERT IGNORE INTO notifications (id, user_id, title, content, type, related_id) VALUES
(1, 3, '有人评论了你的照片', 'user2 评论了你的照片: 好可爱的狗狗！', 'comment', 1),
(2, 4, '领养申请已提交', '你的领养申请已成功提交，请等待审核', 'adoption', 1);