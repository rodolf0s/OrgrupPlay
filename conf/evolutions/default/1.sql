# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table administrador (
  usuario                   varchar(20) not null,
  password                  varchar(20) not null,
  constraint pk_administrador primary key (usuario))
;

create table archivo (
  ruta                      varchar(350) not null,
  nombre                    varchar(255) not null,
  fecha                     date not null,
  hora                      time not null,
  usuario_correo            varchar(50),
  reunion_id                bigint,
  constraint pk_archivo primary key (ruta))
;

create table contacto (
  id                        bigint not null,
  usuario1_correo           varchar(50),
  usuario2_correo           varchar(50),
  constraint pk_contacto primary key (id))
;

create table correo (
  id                        bigint not null,
  nombre                    varchar(60) not null,
  correo                    varchar(50) not null,
  mensaje                   varchar(500) not null,
  constraint pk_correo primary key (id))
;

create table grupo (
  id                        bigint not null,
  nombre                    varchar(25) not null,
  distintivo                varchar(10) not null,
  constraint pk_grupo primary key (id))
;

create table integrante (
  id                        bigint not null,
  usuario_correo            varchar(50),
  grupo_id                  bigint,
  tipo                      integer not null,
  fecha_ingreso             date not null,
  constraint pk_integrante primary key (id))
;

create table reunion (
  id                        bigint not null,
  fecha                     date not null,
  hora                      time not null,
  nombre                    varchar(50) not null,
  descripcion               varchar(500),
  grupo_id                  bigint,
  constraint pk_reunion primary key (id))
;

create table tarea (
  id                        bigint not null,
  fecha                     date not null,
  hora                      time not null,
  nombre                    varchar(60) not null,
  descripcion               varchar(500),
  prioridad                 integer not null,
  usuario_correo            varchar(50),
  constraint pk_tarea primary key (id))
;

create table usuario (
  correo                    varchar(50) not null,
  nombre                    varchar(60) not null,
  password                  varchar(20) not null,
  ciudad                    varchar(60) not null,
  leyenda                   varchar(300),
  imagen                    varchar(350) not null,
  id_verificador            integer not null,
  estado                    varchar(11) not null,
  constraint pk_usuario primary key (correo))
;

create sequence administrador_seq;

create sequence archivo_seq;

create sequence contacto_seq;

create sequence correo_seq;

create sequence grupo_seq;

create sequence integrante_seq;

create sequence reunion_seq;

create sequence tarea_seq;

create sequence usuario_seq;

alter table archivo add constraint fk_archivo_usuario_1 foreign key (usuario_correo) references usuario (correo);
create index ix_archivo_usuario_1 on archivo (usuario_correo);
alter table archivo add constraint fk_archivo_reunion_2 foreign key (reunion_id) references reunion (id);
create index ix_archivo_reunion_2 on archivo (reunion_id);
alter table contacto add constraint fk_contacto_usuario1_3 foreign key (usuario1_correo) references usuario (correo);
create index ix_contacto_usuario1_3 on contacto (usuario1_correo);
alter table contacto add constraint fk_contacto_usuario2_4 foreign key (usuario2_correo) references usuario (correo);
create index ix_contacto_usuario2_4 on contacto (usuario2_correo);
alter table integrante add constraint fk_integrante_usuario_5 foreign key (usuario_correo) references usuario (correo);
create index ix_integrante_usuario_5 on integrante (usuario_correo);
alter table integrante add constraint fk_integrante_grupo_6 foreign key (grupo_id) references grupo (id);
create index ix_integrante_grupo_6 on integrante (grupo_id);
alter table reunion add constraint fk_reunion_grupo_7 foreign key (grupo_id) references grupo (id);
create index ix_reunion_grupo_7 on reunion (grupo_id);
alter table tarea add constraint fk_tarea_usuario_8 foreign key (usuario_correo) references usuario (correo);
create index ix_tarea_usuario_8 on tarea (usuario_correo);



# --- !Downs

drop table if exists administrador cascade;

drop table if exists archivo cascade;

drop table if exists contacto cascade;

drop table if exists correo cascade;

drop table if exists grupo cascade;

drop table if exists integrante cascade;

drop table if exists reunion cascade;

drop table if exists tarea cascade;

drop table if exists usuario cascade;

drop sequence if exists administrador_seq;

drop sequence if exists archivo_seq;

drop sequence if exists contacto_seq;

drop sequence if exists correo_seq;

drop sequence if exists grupo_seq;

drop sequence if exists integrante_seq;

drop sequence if exists reunion_seq;

drop sequence if exists tarea_seq;

drop sequence if exists usuario_seq;

