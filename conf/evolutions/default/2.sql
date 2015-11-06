# --- Sample dataset

# --- !Ups

insert into document (id,content,title,author,watermark,topic) values (  1,'book','The Dark Code','Bruce Wayne','fd62c0e947a77d519a8903f1fbb6b830','Science');
insert into document (id,content,title,author,watermark,topic) values (  2,'book','How to make money','Dr. Evil','58bbfdb5334c5fa941f0a01f3a9b75d2','Business');
insert into document (id,content,title,author,watermark,topic) values (  3,'journal','Journal of human flight routes','Clark Kent','a116a95db04cc040ebb21caa86fac661',null);

# --- !Downs

delete from document;
