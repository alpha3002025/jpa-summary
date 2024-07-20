drop table if exists album;
drop table if exists book;
drop table if exists item;
drop table if exists movie;

drop table if exists publisher;
drop table if exists review;
drop table if exists review_user;
drop table if exists users;

create table item
(
    id          bigint          not null     auto_increment      comment 'id',
    name        varchar(70)     comment '상품명',
    price       decimal         comment '가격',
    dtype       varchar(70)     comment '상품 타입',
    primary key (id)
) engine=InnoDB;

create table book
(
    id           bigint         comment 'id',
    publisher_id bigint         not null    comment '출판사 id',
    primary key (id)
) engine=InnoDB;

create table album
(
    id          bigint not null,
    artist      varchar(150),
    primary key (id)
) engine=InnoDB;

create table movie
(
    id          bigint not null,
    actor       varchar(150),
    director    varchar(150),
    primary key (id)
) engine=InnoDB;

create table publisher
(
    id          bigint          not null    auto_increment      comment 'id',
    name        varchar(70)     not null    comment '출판사명',
    primary key (id)
) engine=InnoDB;

create table review
(
    id          bigint          not null    auto_increment      comment 'id',
    book_id     bigint          not null    comment '책 id',
    title       varchar(100)    not null    comment '리뷰 제목',
    contents    varchar(3000)   not null    comment '리뷰 내용',
    primary key (id)
) engine=InnoDB;

create table review_user
(
    id          bigint          not null    auto_increment      comment 'id',
    user_id     bigint          not null    comment '작성자 id',
    review_id   bigint          not null    comment '리뷰 id',
    primary key (id)
) engine=InnoDB;

create table users
(
    id          bigint          not null    auto_increment      comment 'id',
    email       varchar(100)    not null    comment 'email',
    name        varchar(150)    not null    comment 'name',
    primary key (id)
) engine=InnoDB;