insert into categories(name)
values ('우리동네질문');

insert into users(created_at, deleted_at, email, name, nickname, password, city, district, neighborhood, updated_at)
values (current_timestamp, null, 'test@gmail.com', 'choi', '0kwon', '1234', '고양시', '덕양구', '행신동', current_timestamp);

insert into posts(contents, created_at, deleted_at, updated_at, category_id, user_id, city, district, neighborhood)
values ('test', current_timestamp, null, current_timestamp, 1, 1, '고양시', '덕양구', '행신동');
