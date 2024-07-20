-- publisher
insert into publisher(name) values ('아침사과');
insert into publisher(name) values ('갤리온');
insert into publisher(name) values ('열린 책들');

-- book
insert into item(name, price, dtype) values('글루코스 혁명', 10000, 'Book');
insert into book(name, publisher_id) values('글루코스 혁명', 1);

insert into item(name, price, dtype) values('생각 중독', 10000, 'Book');
insert into book(name, publisher_id) values('생각 중독', 2);

insert into item(name, price, dtype) values('퀸의 대각선 1', 10000, 'Book');
insert into book(name, publisher_id) values('퀸의 대각선 1', 3);