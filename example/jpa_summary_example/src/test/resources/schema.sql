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
