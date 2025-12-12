/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50730 (5.7.30)
 Source Host           : localhost:3306
 Source Schema         : yolomusic

 Target Server Type    : MySQL
 Target Server Version : 50730 (5.7.30)
 File Encoding         : 65001

 Date: 11/12/2025 21:22:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for music
-- ----------------------------
DROP TABLE IF EXISTS `music`;
CREATE TABLE `music`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '音乐ID，主键，自增',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '歌曲标题',
  `artist` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '歌手/艺术家',
  `album` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '专辑名称',
  `genre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '音乐流派（流行、摇滚、古典等）',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '音乐文件访问URL（上传后生成）',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图片URL（上传后生成）',
  `play_count` int(11) NULL DEFAULT 0 COMMENT '播放次数统计',
  `status` enum('published','draft') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'published' COMMENT '音乐状态：published-已发布，draft-草稿',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，插入时自动填充',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_music_title`(`title`) USING BTREE,
  INDEX `idx_music_artist`(`artist`) USING BTREE,
  INDEX `idx_music_genre`(`genre`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '音乐信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of music
-- ----------------------------
INSERT INTO `music` VALUES (1, '发如雪', '周杰伦', '十一月的萧邦', '流行', '/uploads/music/ed52c1f1-dacb-42d1-b553-24debf47ce7d.mp3', '/uploads/covers/99d906e3-b870-468e-b774-c112cb4b3b5c.jpg', 1500, 'published', '2025-12-07 16:47:46');
INSERT INTO `music` VALUES (2, '青花瓷', '周杰伦', '我很忙', '古典', '/uploads/music/c5aa8113-c338-4a73-a91b-7a57ef4058fb.mp3', '/uploads/covers/58ed28d5-76d5-4a21-a649-d366e47861a6.jpg', 2800, 'published', '2025-12-07 16:47:46');
INSERT INTO `music` VALUES (3, '告白气球', '周杰伦', '周杰伦的床边故事', '爵士', '/uploads/music/1b91b25a-ab6b-401d-a237-4967a84def2f.mp3', '/uploads/covers/14d0bcd1-52bc-4901-846b-0df515fba631.jpg', 3200, 'published', '2025-12-07 16:47:46');
INSERT INTO `music` VALUES (4, '演员', '薛之谦', '绅士', '流行', '/uploads/music/2fd5ed8b-57e8-4cdf-a34c-e0c31e9f20d9.mp3', '/uploads/covers/演员封面.jpg', 2100, 'published', '2025-12-07 16:47:46');
INSERT INTO `music` VALUES (5, '丑八怪', '薛之谦', '意外', '流行', '/uploads/music/薛之谦-丑八怪.mp3', '/uploads/covers/丑八怪封面.jpg', 1800, 'published', '2025-12-07 16:47:46');
INSERT INTO `music` VALUES (6, '起风了', '买辣椒也用券', '起风了', '流行', '/uploads/music/买辣椒也用券-起风了.mp3', '/uploads/covers/起风了封面.jpg', 3500, 'published', '2025-12-07 16:47:46');
INSERT INTO `music` VALUES (7, '成都', '赵雷', '无法长大', '民谣', '/uploads/music/赵雷-成都.mp3', '/uploads/covers/成都封面.jpg', 4200, 'published', '2025-12-07 16:47:46');
INSERT INTO `music` VALUES (8, '光年之外', 'G.E.M.邓紫棋', '另一个童话', '流行', '/uploads/music/邓紫棋-光年之外.mp3', '/uploads/covers/光年之外封面.jpg', 3800, 'published', '2025-12-07 16:47:46');
INSERT INTO `music` VALUES (11, '晴天', '周杰伦', '叶惠美', '流行', '/uploads/music/eb2bfd8f-28d8-413a-9a9e-77a708da3251.mp3', '/uploads/covers/fe49d9e2-93e2-401f-8b33-560dd97d755c.jpg', 9267, 'published', '2025-12-09 20:27:12');
INSERT INTO `music` VALUES (12, 'BANG BANG BANG', 'BIGBANG', 'MADE SERIES《A》', '摇滚', '/uploads/music/cde88835-83ef-4744-8bf5-4a6396697c60.mp3', '/uploads/covers/ea536bca-418d-4ab0-b48e-e08b92c0d135.jpg', 2456, 'published', '2025-12-10 12:21:25');
INSERT INTO `music` VALUES (13, 'IF YOU', 'BIGBANG', 'MADE SERIES《D》', '嘻哈', '/uploads/music/660e2161-93b0-4d1d-b208-3118e92afdaf.mp3', '/uploads/covers/cb9a4e89-5583-4a13-97fb-7b4c46e8514a.jpg', 3462, 'published', '2025-12-10 12:23:53');
INSERT INTO `music` VALUES (14, 'SOBER', 'BIGBANG', 'MADE SERIES《D》', '嘻哈', '/uploads/music/953ee6b0-95fa-4c67-b394-d75bfe52e667.mp3', '/uploads/covers/d19d091a-7239-4f0a-b02b-a35a850dcf01.jpg', 2145, 'published', '2025-12-10 12:24:55');
INSERT INTO `music` VALUES (15, 'Love Story', 'Taylor Swift', 'Fearless', '古典', '/uploads/music/84015c60-914f-4f20-b3b8-70c1bfa3208c.mp3', '/uploads/covers/1fd5f5f3-6a56-4527-8297-29095b4f76a2.jpg', 8536, 'published', '2025-12-10 12:29:06');
INSERT INTO `music` VALUES (16, 'Welcome to NewYork', 'Taylor Swift', '1989', '嘻哈', '/uploads/music/f3599523-2f96-40b5-aa14-c0cc4f4ab606.mp3', '/uploads/covers/a515562c-abd2-43ec-8c09-23eeea236824.jpg', 1896, 'published', '2025-12-10 12:31:03');
INSERT INTO `music` VALUES (17, '心率（like a dream）', '鹿晗', 'XXVII+', 'R&B', '/uploads/music/015dbb37-6150-49ba-b554-207a35dbb7b5.mp3', '/uploads/covers/6c4c48a8-35b5-4d65-9b31-736c21435f78.jpg', 2222, 'published', '2025-12-10 12:34:56');
INSERT INTO `music` VALUES (18, '普通朋友', '陶喆', 'I\'m OK', 'R&B', '/uploads/music/29e9b62e-1fe7-4ace-b532-04b5554b0241.mp3', '/uploads/covers/46f75afb-27a7-46ad-8fad-215e12a0cbb1.jpg', 5565, 'published', '2025-12-10 12:38:45');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键，自增',
  `user_id` int(11) NOT NULL COMMENT '操作用户ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型（如：LOGIN, ADD_MUSIC, DELETE_USER等）',
  `operation_target` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作对象（如：用户ID、音乐ID等）',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间，插入时自动填充',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_log_user`(`user_id`) USING BTREE,
  INDEX `idx_log_type`(`operation_type`) USING BTREE,
  INDEX `idx_log_time`(`created_at`) USING BTREE,
  CONSTRAINT `operation_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 149 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 1, 'LOGIN', '系统登录', '2025-12-07 16:48:37');
INSERT INTO `operation_log` VALUES (2, 1, 'ADD_MUSIC', '音乐ID: 1 (夜曲)', '2025-12-07 16:48:37');
INSERT INTO `operation_log` VALUES (3, 1, 'ADD_MUSIC', '音乐ID: 2 (青花瓷)', '2025-12-07 16:48:37');
INSERT INTO `operation_log` VALUES (4, 1, 'CREATE_PLAYLIST', '歌单ID: 1 (周杰伦精选)', '2025-12-07 16:48:37');
INSERT INTO `operation_log` VALUES (5, 2, 'ADD_MUSIC', '音乐ID: 4 (演员)', '2025-12-07 16:48:37');
INSERT INTO `operation_log` VALUES (6, 2, 'CREATE_PLAYLIST', '歌单ID: 2 (薛之谦热门)', '2025-12-07 16:48:37');
INSERT INTO `operation_log` VALUES (7, 1, 'DELETE_USER', '用户ID: 4 (王五)', '2025-12-07 16:48:37');
INSERT INTO `operation_log` VALUES (8, 1, 'UPDATE_MUSIC', '音乐ID: 3 (告白气球)', '2025-12-07 16:48:37');
INSERT INTO `operation_log` VALUES (9, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:15');
INSERT INTO `operation_log` VALUES (10, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:19');
INSERT INTO `operation_log` VALUES (11, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:19');
INSERT INTO `operation_log` VALUES (12, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:20');
INSERT INTO `operation_log` VALUES (13, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:20');
INSERT INTO `operation_log` VALUES (14, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:20');
INSERT INTO `operation_log` VALUES (15, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:21');
INSERT INTO `operation_log` VALUES (16, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:21');
INSERT INTO `operation_log` VALUES (17, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:21');
INSERT INTO `operation_log` VALUES (18, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:21');
INSERT INTO `operation_log` VALUES (19, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:21');
INSERT INTO `operation_log` VALUES (20, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:22');
INSERT INTO `operation_log` VALUES (21, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:22');
INSERT INTO `operation_log` VALUES (22, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:23');
INSERT INTO `operation_log` VALUES (23, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:23');
INSERT INTO `operation_log` VALUES (24, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:24');
INSERT INTO `operation_log` VALUES (25, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:24');
INSERT INTO `operation_log` VALUES (26, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:24');
INSERT INTO `operation_log` VALUES (27, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:25');
INSERT INTO `operation_log` VALUES (28, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:25');
INSERT INTO `operation_log` VALUES (29, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:25');
INSERT INTO `operation_log` VALUES (30, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:31');
INSERT INTO `operation_log` VALUES (31, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:31');
INSERT INTO `operation_log` VALUES (32, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:31');
INSERT INTO `operation_log` VALUES (33, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:32');
INSERT INTO `operation_log` VALUES (34, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:32');
INSERT INTO `operation_log` VALUES (35, 2, 'LOGIN', '系统登录', '2025-12-07 20:48:33');
INSERT INTO `operation_log` VALUES (36, 1, 'LOGIN', '系统登录', '2025-12-07 20:49:43');
INSERT INTO `operation_log` VALUES (37, 1, 'LOGIN', '系统登录', '2025-12-07 20:49:44');
INSERT INTO `operation_log` VALUES (38, 2, 'LOGIN', '系统登录', '2025-12-07 20:49:48');
INSERT INTO `operation_log` VALUES (39, 2, 'LOGIN', '系统登录', '2025-12-07 20:50:01');
INSERT INTO `operation_log` VALUES (40, 1, 'LOGIN', '系统登录', '2025-12-07 20:58:43');
INSERT INTO `operation_log` VALUES (41, 1, 'LOGIN', '系统登录', '2025-12-07 20:58:47');
INSERT INTO `operation_log` VALUES (42, 1, 'LOGIN', '系统登录', '2025-12-07 20:58:51');
INSERT INTO `operation_log` VALUES (43, 1, 'LOGIN', '系统登录', '2025-12-08 22:17:38');
INSERT INTO `operation_log` VALUES (44, 1, 'ADD_MUSIC', '添加音乐: 你是猪', '2025-12-08 22:18:10');
INSERT INTO `operation_log` VALUES (45, 1, 'RESET_PASSWORD', '重置用户ID: 2 的密码', '2025-12-08 22:18:48');
INSERT INTO `operation_log` VALUES (46, 1, 'CREATE_PLAYLIST', '创建歌单: xtt是🐖', '2025-12-08 22:19:08');
INSERT INTO `operation_log` VALUES (47, 1, 'LOGIN', '系统登录', '2025-12-08 22:20:58');
INSERT INTO `operation_log` VALUES (48, 1, 'LOGIN', '系统登录', '2025-12-08 22:30:28');
INSERT INTO `operation_log` VALUES (49, 1, 'UPDATE_USER', '更新用户ID: 4', '2025-12-08 22:30:38');
INSERT INTO `operation_log` VALUES (50, 1, 'UPDATE_PLAYLIST', '更新歌单ID: 5', '2025-12-08 22:31:00');
INSERT INTO `operation_log` VALUES (51, 1, 'LOGIN', '系统登录', '2025-12-09 11:50:51');
INSERT INTO `operation_log` VALUES (52, 1, 'UPDATE_USER', '更新用户ID: 4', '2025-12-09 11:51:17');
INSERT INTO `operation_log` VALUES (53, 1, 'LOGOUT', '系统退出', '2025-12-09 11:51:20');
INSERT INTO `operation_log` VALUES (54, 1, 'LOGIN', '系统登录', '2025-12-09 15:18:25');
INSERT INTO `operation_log` VALUES (55, 1, 'LOGOUT', '系统退出', '2025-12-09 15:22:44');
INSERT INTO `operation_log` VALUES (56, 1, 'LOGIN', '系统登录', '2025-12-09 15:23:21');
INSERT INTO `operation_log` VALUES (57, 1, 'LOGOUT', '系统退出', '2025-12-09 15:23:26');
INSERT INTO `operation_log` VALUES (58, 1, 'LOGIN', '系统登录', '2025-12-09 15:23:38');
INSERT INTO `operation_log` VALUES (59, 1, 'LOGOUT', '系统退出', '2025-12-09 15:24:36');
INSERT INTO `operation_log` VALUES (60, 1, 'LOGIN', '系统登录', '2025-12-09 19:47:09');
INSERT INTO `operation_log` VALUES (61, 1, 'LOGOUT', '系统退出', '2025-12-09 19:48:26');
INSERT INTO `operation_log` VALUES (62, 2, 'LOGIN', '系统登录', '2025-12-09 19:48:33');
INSERT INTO `operation_log` VALUES (63, 2, 'DELETE_MUSIC', '删除音乐ID: 10', '2025-12-09 19:48:42');
INSERT INTO `operation_log` VALUES (64, 1, 'LOGIN', '系统登录', '2025-12-09 19:59:02');
INSERT INTO `operation_log` VALUES (65, 1, 'UPDATE_MUSIC', '更新音乐ID: 9', '2025-12-09 20:09:34');
INSERT INTO `operation_log` VALUES (66, 1, 'UPDATE_MUSIC', '更新音乐ID: 9', '2025-12-09 20:11:08');
INSERT INTO `operation_log` VALUES (67, 1, 'ADD_MUSIC', '添加音乐: 晴天', '2025-12-09 20:27:12');
INSERT INTO `operation_log` VALUES (68, 1, 'UPDATE_MUSIC', '更新音乐ID: 11', '2025-12-09 20:40:33');
INSERT INTO `operation_log` VALUES (69, 1, 'LOGIN', '系统登录', '2025-12-10 12:00:42');
INSERT INTO `operation_log` VALUES (70, 1, 'UPDATE_MUSIC', '更新音乐ID: 3', '2025-12-10 12:06:09');
INSERT INTO `operation_log` VALUES (71, 1, 'UPDATE_MUSIC', '更新音乐ID: 3', '2025-12-10 12:07:31');
INSERT INTO `operation_log` VALUES (72, 1, 'UPDATE_MUSIC', '更新音乐ID: 2', '2025-12-10 12:09:09');
INSERT INTO `operation_log` VALUES (73, 1, 'UPDATE_MUSIC', '更新音乐ID: 4', '2025-12-10 12:12:19');
INSERT INTO `operation_log` VALUES (74, 1, 'UPDATE_MUSIC', '更新音乐ID: 1', '2025-12-10 12:15:14');
INSERT INTO `operation_log` VALUES (75, 1, 'DELETE_MUSIC', '删除音乐ID: 9', '2025-12-10 12:17:33');
INSERT INTO `operation_log` VALUES (76, 1, 'ADD_MUSIC', '添加音乐: BANG BANG BANG', '2025-12-10 12:21:25');
INSERT INTO `operation_log` VALUES (77, 1, 'ADD_MUSIC', '添加音乐: IF YOU', '2025-12-10 12:23:53');
INSERT INTO `operation_log` VALUES (78, 1, 'ADD_MUSIC', '添加音乐: SOBER', '2025-12-10 12:24:55');
INSERT INTO `operation_log` VALUES (79, 1, 'ADD_MUSIC', '添加音乐: Love Story', '2025-12-10 12:29:06');
INSERT INTO `operation_log` VALUES (80, 1, 'UPDATE_MUSIC', '更新音乐ID: 15', '2025-12-10 12:29:26');
INSERT INTO `operation_log` VALUES (81, 1, 'ADD_MUSIC', '添加音乐: Welcome to NewYork', '2025-12-10 12:31:03');
INSERT INTO `operation_log` VALUES (82, 1, 'ADD_MUSIC', '添加音乐: 心率（like a dream）', '2025-12-10 12:34:56');
INSERT INTO `operation_log` VALUES (83, 1, 'UPDATE_MUSIC', '更新音乐ID: 2', '2025-12-10 12:35:24');
INSERT INTO `operation_log` VALUES (84, 1, 'UPDATE_MUSIC', '更新音乐ID: 3', '2025-12-10 12:35:39');
INSERT INTO `operation_log` VALUES (85, 1, 'UPDATE_MUSIC', '更新音乐ID: 12', '2025-12-10 12:35:50');
INSERT INTO `operation_log` VALUES (86, 1, 'ADD_MUSIC', '添加音乐: 普通朋友', '2025-12-10 12:38:45');
INSERT INTO `operation_log` VALUES (87, 1, 'DELETE_PLAYLIST', '删除歌单ID: 5', '2025-12-10 12:51:45');
INSERT INTO `operation_log` VALUES (88, 1, 'CREATE_PLAYLIST', '创建歌单: 经典老歌大放送！一人一首代表作！', '2025-12-10 12:53:28');
INSERT INTO `operation_log` VALUES (89, 1, 'LOGIN', '系统登录', '2025-12-10 16:38:09');
INSERT INTO `operation_log` VALUES (90, 1, 'LOGIN', '系统登录', '2025-12-10 17:22:56');
INSERT INTO `operation_log` VALUES (91, 1, 'UPDATE_PLAYLIST', '更新歌单ID: 7', '2025-12-10 18:00:40');
INSERT INTO `operation_log` VALUES (92, 1, 'CREATE_PLAYLIST', '创建歌单: 测试歌单', '2025-12-10 18:14:02');
INSERT INTO `operation_log` VALUES (93, 1, 'LOGIN', '系统登录', '2025-12-10 19:34:45');
INSERT INTO `operation_log` VALUES (94, 1, 'LOGIN', '系统登录', '2025-12-10 19:36:17');
INSERT INTO `operation_log` VALUES (95, 1, 'ADD_MUSIC_TO_PLAYLIST', '向歌单ID: 7 添加音乐ID: 18', '2025-12-10 19:44:29');
INSERT INTO `operation_log` VALUES (96, 1, 'LOGOUT', '系统退出', '2025-12-10 19:45:03');
INSERT INTO `operation_log` VALUES (97, 2, 'LOGIN', '系统登录', '2025-12-10 19:45:15');
INSERT INTO `operation_log` VALUES (98, 2, 'LOGIN', '系统登录', '2025-12-10 20:28:19');
INSERT INTO `operation_log` VALUES (99, 2, 'LOGOUT', '系统退出', '2025-12-10 20:45:23');
INSERT INTO `operation_log` VALUES (100, 1, 'LOGIN', '系统登录', '2025-12-10 20:45:32');
INSERT INTO `operation_log` VALUES (101, 1, 'LOGOUT', '系统退出', '2025-12-10 20:45:45');
INSERT INTO `operation_log` VALUES (102, 2, 'LOGIN', '系统登录', '2025-12-10 20:45:51');
INSERT INTO `operation_log` VALUES (103, 2, 'LOGOUT', '系统退出', '2025-12-10 21:25:22');
INSERT INTO `operation_log` VALUES (104, 3, 'LOGIN', '系统登录', '2025-12-10 21:25:36');
INSERT INTO `operation_log` VALUES (105, 3, 'LOGOUT', '系统退出', '2025-12-10 21:25:51');
INSERT INTO `operation_log` VALUES (106, 1, 'LOGIN', '系统登录', '2025-12-10 21:25:59');
INSERT INTO `operation_log` VALUES (107, 1, 'RESET_PASSWORD', '重置用户ID: 3 的密码', '2025-12-10 21:27:38');
INSERT INTO `operation_log` VALUES (108, 1, 'LOGOUT', '系统退出', '2025-12-10 21:27:41');
INSERT INTO `operation_log` VALUES (109, 3, 'LOGIN', '系统登录', '2025-12-10 21:27:53');
INSERT INTO `operation_log` VALUES (110, 3, 'LOGOUT', '系统退出', '2025-12-10 21:27:58');
INSERT INTO `operation_log` VALUES (111, 1, 'LOGIN', '系统登录', '2025-12-11 15:06:37');
INSERT INTO `operation_log` VALUES (112, 1, 'LOGIN', '系统登录', '2025-12-11 17:01:05');
INSERT INTO `operation_log` VALUES (113, 1, 'UPDATE_USER', '更新用户ID: 2', '2025-12-11 17:16:31');
INSERT INTO `operation_log` VALUES (114, 1, 'LOGOUT', '系统退出', '2025-12-11 17:16:36');
INSERT INTO `operation_log` VALUES (115, 2, 'LOGIN', '系统登录', '2025-12-11 17:16:43');
INSERT INTO `operation_log` VALUES (116, 2, 'UPDATE_USER', '更新用户ID: 2', '2025-12-11 17:17:09');
INSERT INTO `operation_log` VALUES (117, 2, 'LOGOUT', '系统退出', '2025-12-11 17:17:11');
INSERT INTO `operation_log` VALUES (118, 2, 'LOGIN', '系统登录', '2025-12-11 17:17:25');
INSERT INTO `operation_log` VALUES (119, 2, 'LOGOUT', '系统退出', '2025-12-11 17:26:27');
INSERT INTO `operation_log` VALUES (120, 1, 'LOGIN', '系统登录', '2025-12-11 17:26:34');
INSERT INTO `operation_log` VALUES (121, 1, 'LOGOUT', '系统退出', '2025-12-11 17:38:22');
INSERT INTO `operation_log` VALUES (122, 2, 'LOGIN', '系统登录', '2025-12-11 18:01:47');
INSERT INTO `operation_log` VALUES (123, 2, 'LOGOUT', '系统退出', '2025-12-11 18:01:51');
INSERT INTO `operation_log` VALUES (124, 2, 'LOGIN', '系统登录', '2025-12-11 18:08:36');
INSERT INTO `operation_log` VALUES (125, 2, 'LOGOUT', '系统退出', '2025-12-11 18:08:40');
INSERT INTO `operation_log` VALUES (126, 2, 'LOGIN', '系统登录', '2025-12-11 18:12:51');
INSERT INTO `operation_log` VALUES (127, 2, 'LOGOUT', '系统退出', '2025-12-11 18:12:54');
INSERT INTO `operation_log` VALUES (128, 2, 'LOGIN', '系统登录', '2025-12-11 18:29:09');
INSERT INTO `operation_log` VALUES (129, 2, 'LOGOUT', '系统退出', '2025-12-11 18:29:16');
INSERT INTO `operation_log` VALUES (130, 2, 'LOGIN', '系统登录', '2025-12-11 18:32:05');
INSERT INTO `operation_log` VALUES (131, 2, 'LOGOUT', '系统退出', '2025-12-11 18:32:07');
INSERT INTO `operation_log` VALUES (132, 1, 'LOGIN', '系统登录', '2025-12-11 18:37:00');
INSERT INTO `operation_log` VALUES (133, 1, 'LOGOUT', '系统退出', '2025-12-11 18:37:08');
INSERT INTO `operation_log` VALUES (134, 2, 'LOGIN', '系统登录', '2025-12-11 18:37:59');
INSERT INTO `operation_log` VALUES (135, 2, 'LOGOUT', '系统退出', '2025-12-11 18:38:03');
INSERT INTO `operation_log` VALUES (136, 1, 'LOGIN', '系统登录', '2025-12-11 18:40:06');
INSERT INTO `operation_log` VALUES (137, 1, 'LOGOUT', '系统退出', '2025-12-11 18:40:25');
INSERT INTO `operation_log` VALUES (138, 1, 'LOGIN', '系统登录', '2025-12-11 18:53:22');
INSERT INTO `operation_log` VALUES (139, 1, 'LOGOUT', '系统退出', '2025-12-11 19:14:00');
INSERT INTO `operation_log` VALUES (140, 5, 'LOGIN', '系统登录', '2025-12-11 19:14:33');
INSERT INTO `operation_log` VALUES (141, 5, 'LOGOUT', '系统退出', '2025-12-11 19:14:43');
INSERT INTO `operation_log` VALUES (142, 1, 'LOGIN', '系统登录', '2025-12-11 19:14:50');
INSERT INTO `operation_log` VALUES (143, 1, 'LOGOUT', '系统退出', '2025-12-11 20:33:09');
INSERT INTO `operation_log` VALUES (144, 1, 'LOGIN', '系统登录', '2025-12-11 20:33:56');
INSERT INTO `operation_log` VALUES (145, 1, 'LOGOUT', '系统退出', '2025-12-11 20:38:03');
INSERT INTO `operation_log` VALUES (146, 1, 'LOGIN', '系统登录', '2025-12-11 20:38:18');
INSERT INTO `operation_log` VALUES (147, 1, 'LOGOUT', '系统退出', '2025-12-11 21:06:27');
INSERT INTO `operation_log` VALUES (148, 1, 'LOGIN', '系统登录', '2025-12-11 21:07:13');

-- ----------------------------
-- Table structure for playlist
-- ----------------------------
DROP TABLE IF EXISTS `playlist`;
CREATE TABLE `playlist`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '歌单ID，主键，自增',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '歌单名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '歌单描述',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '歌单封面图片URL',
  `creator_id` int(11) NOT NULL COMMENT '创建者用户ID',
  `music_count` int(11) NULL DEFAULT 0 COMMENT '歌单内音乐数量',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，插入时自动填充',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_playlist_name`(`name`) USING BTREE,
  INDEX `idx_playlist_creator`(`creator_id`) USING BTREE,
  CONSTRAINT `playlist_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '歌单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of playlist
-- ----------------------------
INSERT INTO `playlist` VALUES (1, '周杰伦精选', '周杰伦经典歌曲合集，包含夜曲、青花瓷等', '/uploads/playlists/周杰伦精选封面.jpg', 1, 3, '2025-12-07 16:48:01');
INSERT INTO `playlist` VALUES (2, '薛之谦热门', '薛之谦热门歌曲精选', '/uploads/playlists/薛之谦热门封面.jpg', 2, 2, '2025-12-07 16:48:01');
INSERT INTO `playlist` VALUES (3, '华语流行金曲', '最新华语流行音乐排行榜', '/uploads/playlists/华语流行金曲封面.jpg', 1, 6, '2025-12-07 16:48:01');
INSERT INTO `playlist` VALUES (4, '民谣精选', '经典民谣歌曲合集', '/uploads/playlists/民谣精选封面.jpg', 3, 1, '2025-12-07 16:48:01');
INSERT INTO `playlist` VALUES (6, '示例歌单', '这是一份示例歌单', NULL, 1, 0, '2025-12-09 18:51:50');
INSERT INTO `playlist` VALUES (7, '经典老歌大放送！一人一首代表作！', '', '/uploads/playlists/cedaefbf-da9f-4a60-b117-f24324584455.png', 1, 1, '2025-12-10 12:53:28');
INSERT INTO `playlist` VALUES (8, '测试歌单', '测试测试', '', 1, 0, '2025-12-10 18:14:02');

-- ----------------------------
-- Table structure for playlist_music
-- ----------------------------
DROP TABLE IF EXISTS `playlist_music`;
CREATE TABLE `playlist_music`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键，自增',
  `playlist_id` int(11) NOT NULL COMMENT '歌单ID',
  `music_id` int(11) NOT NULL COMMENT '音乐ID',
  `added_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间，插入时自动填充',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_playlist_music`(`playlist_id`, `music_id`) USING BTREE COMMENT '防止重复添加同一音乐到同一歌单',
  INDEX `music_id`(`music_id`) USING BTREE,
  CONSTRAINT `playlist_music_ibfk_1` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `playlist_music_ibfk_2` FOREIGN KEY (`music_id`) REFERENCES `music` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '歌单与音乐关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of playlist_music
-- ----------------------------
INSERT INTO `playlist_music` VALUES (1, 1, 1, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (2, 1, 2, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (3, 1, 3, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (4, 2, 4, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (5, 2, 5, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (6, 3, 1, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (7, 3, 2, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (8, 3, 3, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (9, 3, 4, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (10, 3, 5, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (11, 3, 6, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (12, 4, 7, '2025-12-07 16:48:14');
INSERT INTO `playlist_music` VALUES (13, 7, 18, '2025-12-10 19:44:29');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键，自增',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名，唯一',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（BCrypt加密存储）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `role` enum('admin','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'user' COMMENT '用户角色：admin-管理员，user-普通用户',
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'active' COMMENT '用户状态：active-活跃，inactive-禁用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，插入时自动填充',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `idx_user_username`(`username`) USING BTREE,
  INDEX `idx_user_role`(`role`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$12$kcD0tt1HGPAZiS/yVBlaTeSY4nMz1iotVikWY4a6/MtVWIpv6GlXG', '管理员', 'admin', 'active', '2025-12-07 16:47:21');
INSERT INTO `user` VALUES (2, 'zhangsan', '$2a$10$UerG7FDuGSzdx1rE4DAe7uCxIki3N/tVUON5TDfEJGgq7mnDUO21q', '张三', 'user', 'active', '2025-12-07 16:47:21');
INSERT INTO `user` VALUES (3, 'lisi', '$2a$10$ClhMjrw2j1i3guMPlwZBL.3h4OlcPmdOZHZ6KgeQ8wAxBMUocaYtm', '李四', 'user', 'active', '2025-12-07 16:47:21');
INSERT INTO `user` VALUES (4, 'wangwu', '$2a$12$kcD0tt1HGPAZiS/yVBlaTeSY4nMz1iotVikWY4a6/MtVWIpv6GlXG', '王五', 'user', 'inactive', '2025-12-07 16:47:21');
INSERT INTO `user` VALUES (5, 'xiaott', '$2a$12$kcD0tt1HGPAZiS/yVBlaTeSY4nMz1iotVikWY4a6/MtVWIpv6GlXG', '叉踢踢', 'user', 'active', '2025-12-11 19:12:59');

SET FOREIGN_KEY_CHECKS = 1;
