-- ========== 赵氏家谱示例数据 ==========

INSERT INTO family_tree (id, name, surname, hall_name, origin, motto, clan_rules, description, ancestor_name, ancestor_story, visibility, member_count, generation_count)
VALUES (1, '赵氏家谱', '赵', '百忍堂', '甘肃天水',
        '忠孝传家久，诗书继世长。勤俭持家远，和睦万事兴。',
        '一、尊祖敬宗，孝顺父母\n二、兄友弟恭，和睦邻里\n三、勤读诗书，耕读传家\n四、克勤克俭，不事奢华\n五、诚实守信，乐善好施',
        '赵氏一族源自天水，始祖赵德公于明洪武年间迁居江南，历经六百余年，繁衍至今已逾二十余世。族人秉承祖训，耕读传家，人才辈出，为国为民多有建树。',
        '赵德公',
        '始祖赵德公，字润之，原籍甘肃天水。明洪武三年（1370年），奉诏南迁，携家眷定居于江苏南京。公为人刚正不阿，乐善好施，深得乡邻敬重。后世子孙谨记祖训，代代相传。',
        1, 22, 5);

-- ========== 第一世（始祖） ==========
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, biography, parent_id, spouse_id, sort_order)
VALUES (1, 1, '赵德公', 1, '1340-03-15', '1420-11-08', 0, 1, '德', '甘肃天水', '江苏南京', '乡绅',
        '始祖德公，明初南迁江南，开基创业，为赵氏南方一脉之始。', NULL, 2, 0);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (2, 1, '李氏', 0, '1342-06-20', '1418-09-12', 0, 1, NULL, '江苏南京', '江苏南京', NULL, NULL, NULL, 0);

-- ========== 第二世 ==========
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, biography, parent_id, spouse_id, sort_order)
VALUES (3, 1, '赵义忠', 1, '1368-02-10', '1445-07-22', 0, 2, '义', '江苏南京', '江苏南京', '县丞',
        '义忠公，德公长子，少年聪颖，中举后任县丞，为官清廉。', 1, 4, 0);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (4, 1, '王氏', 0, '1370-08-05', '1442-03-18', 0, 2, NULL, '江苏南京', '江苏南京', NULL, NULL, NULL, 0);

INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, biography, parent_id, spouse_id, sort_order)
VALUES (5, 1, '赵义信', 1, '1372-09-18', '1450-12-03', 0, 2, '义', '江苏南京', '浙江杭州', '商人',
        '义信公，德公次子，经商有道，在杭州开设丝绸铺，家业兴旺。', 1, 6, 1);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (6, 1, '张氏', 0, '1374-04-12', '1455-08-20', 0, 2, NULL, '浙江杭州', '浙江杭州', NULL, NULL, NULL, 0);

-- ========== 第三世 ==========
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, biography, parent_id, spouse_id, sort_order)
VALUES (7, 1, '赵礼文', 1, '1395-05-20', '1478-10-15', 0, 3, '礼', '江苏南京', '江苏南京', '教书先生',
        '礼文公，义忠公长子，饱读诗书，一生教书育人，桃李满天下。', 3, 8, 0);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (8, 1, '陈氏', 0, '1398-03-08', '1475-06-30', 0, 3, NULL, '江苏南京', '江苏南京', NULL, NULL, NULL, 0);

INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (9, 1, '赵礼武', 1, '1398-11-03', '1480-04-25', 0, 3, '礼', '江苏南京', '安徽合肥', '武官',
        '礼武公，义忠公次子，从军报国，官至千户。', 3, 10, 1);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (10, 1, '刘氏', 0, '1400-07-14', '1482-11-20', 0, 3, NULL, '安徽合肥', '安徽合肥', NULL, NULL, NULL, 0);

INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (11, 1, '赵礼商', 1, '1400-01-25', '1485-09-10', 0, 3, '礼', '浙江杭州', '浙江杭州', '丝绸商人',
        '礼商公，义信公之子，继承父业，将丝绸生意做到了苏州、扬州。', 5, 12, 0);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (12, 1, '周氏', 0, '1402-12-08', '1488-05-15', 0, 3, NULL, '浙江杭州', '浙江杭州', NULL, NULL, NULL, 0);

-- ========== 第四世 ==========
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, biography, parent_id, spouse_id, sort_order)
VALUES (13, 1, '赵智远', 1, '1425-08-12', '1510-02-28', 0, 4, '智', '江苏南京', '江苏南京', '进士/知府',
        '智远公，礼文公长子，明正统年间中进士，官至知府，政绩卓著。', 7, 14, 0);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (14, 1, '孙氏', 0, '1428-04-18', '1508-10-05', 0, 4, NULL, '江苏南京', '江苏南京', NULL, NULL, NULL, 0);

INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (15, 1, '赵智明', 1, '1428-12-05', '1505-06-18', 0, 4, '智', '江苏南京', '江苏南京', '医者',
        '智明公，礼文公次子，精通岐黄之术，悬壶济世，活人无数。', 7, 16, 1);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (16, 1, '马氏', 0, '1430-09-22', '1502-12-10', 0, 4, NULL, '江苏南京', '江苏南京', NULL, NULL, NULL, 0);

INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (17, 1, '赵智勇', 1, '1430-06-30', '1512-08-14', 0, 4, '智', '安徽合肥', '安徽合肥', '武举人',
        '智勇公，礼武公之子，继承父志从军，中武举，镇守边关。', 9, NULL, 0);

-- ========== 第五世 ==========
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, biography, parent_id, spouse_id, sort_order)
VALUES (18, 1, '赵信达', 1, '1455-03-08', '1540-11-20', 0, 5, '信', '江苏南京', '北京', '翰林院编修',
        '信达公，智远公长子，才华横溢，入翰林院，参与编修国史。', 13, 19, 0);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (19, 1, '黄氏', 0, '1458-07-15', '1538-04-25', 0, 5, NULL, '北京', '北京', NULL, NULL, NULL, 0);

INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (20, 1, '赵信义', 1, '1458-10-22', '1535-05-08', 0, 5, '信', '江苏南京', '江苏南京', '教书先生',
        '信义公，智远公次子，继承祖父礼文公遗风，教书育人。', 13, NULL, 1);

INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (21, 1, '赵信和', 1, '1460-04-18', '1542-09-30', 0, 5, '信', '江苏南京', '浙江杭州', '药铺掌柜',
        '信和公，智明公之子，将祖父医术与经商结合，在杭州开设药铺。', 15, 22, 0);
INSERT INTO family_member (id, tree_id, name, gender, birth_date, death_date, is_alive, generation, generation_name, birthplace, residence, occupation, parent_id, spouse_id, sort_order)
VALUES (22, 1, '吴氏', 0, '1462-11-05', '1540-07-18', 0, 5, NULL, '浙江杭州', '浙江杭州', NULL, NULL, NULL, 0);

-- ========== 字辈排行 ==========
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 1, '德', '以德立身，厚德载物');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 2, '义', '义薄云天，见义勇为');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 3, '礼', '知书达礼，礼贤下士');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 4, '智', '智勇双全，智慧通达');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 5, '信', '诚实守信，言而有信');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 6, '温', '温文尔雅，温润如玉');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 7, '良', '良善正直，积善成德');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 8, '恭', '恭敬谦让，敬业乐群');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 9, '俭', '勤俭持家，克勤克俭');
INSERT INTO generation_rank (tree_id, rank_order, rank_char, description) VALUES (1, 10, '让', '谦让为先，礼让三分');

-- ========== 大事记 ==========
INSERT INTO family_event (tree_id, member_id, title, content, event_date, event_type, importance)
VALUES (1, 1, '始祖南迁', '始祖赵德公奉诏从甘肃天水南迁至江苏南京，开创赵氏南方基业。', '1370-03-15', 4, 2);
INSERT INTO family_event (tree_id, member_id, title, content, event_date, event_type, importance)
VALUES (1, 3, '义忠公中举', '二世祖赵义忠参加乡试，高中举人，后任县丞。', '1390-09-01', 5, 1);
INSERT INTO family_event (tree_id, member_id, title, content, event_date, event_type, importance)
VALUES (1, 13, '智远公中进士', '四世祖赵智远于明正统年间高中进士，官至知府，光耀门楣。', '1450-03-20', 5, 2);
INSERT INTO family_event (tree_id, member_id, title, content, event_date, event_type, importance)
VALUES (1, 18, '信达公入翰林', '五世祖赵信达入翰林院任编修，参与编修国史，为族中最高文职。', '1480-06-15', 7, 2);
INSERT INTO family_event (tree_id, member_id, title, content, event_date, event_type, importance)
VALUES (1, 11, '礼商公扩业', '三世赵礼商将丝绸生意扩展至苏州、扬州，赵氏商号名扬江南。', '1435-08-10', 6, 1);
INSERT INTO family_event (tree_id, title, content, event_date, event_type, importance)
VALUES (1, '修建赵氏宗祠', '赵氏族人集资在南京修建宗祠，供奉列祖列宗，每年春秋两祭。', '1460-10-01', 0, 2);
