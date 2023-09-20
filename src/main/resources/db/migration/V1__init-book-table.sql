drop table if exists book;

create table book (
    book_id varchar(36) not null,
    book_isbn bigint not null,
    book_name varchar(50) not null,
    book_genre tinyint not null,
    book_version integer,
    book_creation_date datetime(6),
    book_update_date datetime(6),
    primary key (book_id)
) engine=InnoDB;