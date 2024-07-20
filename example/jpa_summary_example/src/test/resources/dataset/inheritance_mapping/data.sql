insert into publisher(name) values ('아침사과');
insert into publisher(name) values ('갤리온');
insert into publisher(name) values ('열린 책들');

-- book
insert into item(name, price, dtype) values('글루코스 혁명', 10000, 'Book');
insert into book(id, publisher_id) values(1, 1);

insert into item(name, price, dtype) values('생각 중독', 10000, 'Book');
insert into book(id, publisher_id) values(2, 2);

insert into item(name, price, dtype) values('퀸의 대각선 1', 10000, 'Book');
insert into book(id, publisher_id) values(3, 3);

-- album
insert into item(name, price, dtype) values('레드제플린 명곡 모음', 14440, 'Album');
insert into album(id, artist) values(4, '레드제플린');

insert into item(name, price, dtyp) values('비틀즈 명곡 모음', 14440, 'Album');
insert into album(id, artist) values(5, '비틀즈');

-- movie
insert into item(name, price, dtype) values('엽기적인 그녀', 14440, 'Movie');
insert into movie(id, director, actor) values(6, '곽재용', '전지현');

insert into item(name, price, dtype) values('명량', 14440, 'Movie');
insert into movie(id, director, actor) values(7, '김한민', '최민식');

