insert into regions(ID, NAME) values (1, '행신동');
insert into roles values (1, 'GENERAL');
insert into categories values (1, '동네 맛집');
insert into users(create_at, updated_at, email, name, nick_name, password, region_id, role_id) values (current_timestamp, current_timestamp,'test@gmail.com', 'choi', '0kwon', '1234',1,1);
insert into posts(contents, create_at, updated_at, category_id, user_id) values ('테스트 게시물 입니다.', current_timestamp, current_timestamp, 1, 1)
