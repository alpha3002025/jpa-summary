insert into publisher(name) values ('아침사과');
insert into publisher(name) values ('갤리온');
insert into publisher(name) values ('열린 책들');

insert into book(name, publisher_id) values('글루코스 혁명', 1);
insert into book(name, publisher_id) values('생각 중독', 1);
insert into book(name, publisher_id) values('퀸의 대각선 1', 3);

insert into item(name, price, DTYPE) values('레드제플린 명곡 모음', 14440, 'album');
insert into album(artist) values('레드제플린');
insert into item(name, price, DTYPE) values('비틀즈 명곡 모음', 14440, 'album');
insert into album(artist) values('비틀즈');


insert into item(name, price, DTYPE) values('레드제플린 명곡 모음', 14440, 'album');
insert into book(artist) values('레드제플린');
