# --- First database schema

# --- !Ups

set ignorecase true;

create table document (
  id                        bigint not null,
  content                      varchar(255) not null,
  title                      varchar(255) not null,
  author                      varchar(255) not null,
  watermark                      varchar(255),
  topic                      varchar(255),
  constraint pk_company primary key (id))
;

create sequence document_seq start with 1000;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists document;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists document_seq;

