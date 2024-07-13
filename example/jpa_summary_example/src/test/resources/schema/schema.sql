create table book
(
    id           bigint         not null    auto_increment      comment 'id',
    name         varchar(70)    not null    comment '도서명',
    publisher_id bigint         not null    comment '출판사 id',
    primary key (id)
);

create table publisher
(
    id          bigint          not null    auto_increment      comment 'id',
    name        varchar(70)     not null    comment '출판사명',
    primary key (id)
);

create table review
(
    id          bigint          not null    auto_increment      comment 'id',
    book_id     bigint          not null    comment '책 id',
    title       varchar(100)    not null    comment '책 제목',
    contents    varchar(3000)   not null    comment '리뷰 내용',
    primary key (id)
);