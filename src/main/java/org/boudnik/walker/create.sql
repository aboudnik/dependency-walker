use test
go

drop table dbo.Q1, dbo.Q2, dbo.Q3, dbo.P1, dbo.P2, dbo.P3, dbo.N1, dbo.N2, dbo.M;
GO

-- sample tables with FKs
create table dbo.M (id int primary key, M_c1 int not null, M_c2 datetime not null, M_c3 char(10) not null, M_c4 datetime);
create unique index ux1_A on dbo.M (M_c1);
create unique index ux2_A on dbo.M (M_c2, M_c3);

create table dbo.N1 (id int primary key, N1_c1 int, N1_c2 datetime, N1_c3 char(10), N1_c4 datetime);
create unique index ux1_N1 on dbo.N1 (N1_c1, N1_c3);
create unique index ux2_N1 on dbo.N1 ( N1_c2);
alter table dbo.N1 add constraint FK_N1_M foreign key (N1_c1) references dbo.M (M_c1);

create table dbo.N2 (id int primary key, N2_c1 int, N2_c2 datetime, N2_c3 char(10), N2_c4 datetime);
create unique index ux1_N2 on dbo.N2 (N2_c1);
create unique index ux2_N2 on dbo.N2 (N2_c3, N2_c4);
alter table dbo.N2 add constraint FK1_N2_M foreign key (N2_c2, N2_c3) references dbo.M (M_c2, M_c3);

create table dbo.P1 (id int primary key, P1_c1 int, P1_c2 datetime, P1_c3 char(10), P1_c4 datetime);
create unique index ux1_P1 on dbo.P1 (P1_c1);
alter table dbo.P1 add constraint FK_P1_N1 foreign key (P1_c1, P1_c3) references dbo.N1 (N1_c1, N1_c3);

create table dbo.P2 (id int primary key, P2_c1 int, P2_c2 datetime, P2_c3 char(10), P2_c4 datetime);
create unique index ux1_P2 on dbo.P2 (P2_c1);
create unique index ux2_P2 on dbo.P2 (P2_c2, P2_c3);
alter table dbo.P2 add constraint FK_P2_N1 foreign key (P2_c2) references dbo.N1 (N1_c2);
alter table dbo.P2 add constraint FK_P2_N2 foreign key (P2_c3, P2_c4) references dbo.N2 (N2_c3, N2_c4);

create table dbo.P3 (id int primary key, P3_c1 int, P3_c2 datetime, P3_c3 char(10), P3_c4 datetime);
alter table dbo.P3 add constraint FK_P3_N2 foreign key (P3_c1) references dbo.N2 (id);

create table dbo.Q1 (id int primary key, Q1_c1 int, Q1_c2 datetime, Q1_c3 char(10), Q1_C4 datetime);
alter table dbo.Q1 add constraint FK_Q1_P1 foreign key (Q1_c1) references dbo.P1 (P1_c1);

create table dbo.Q2 (id int primary key, Q2_c1 int, Q2_c2 datetime, Q2_c3 char(10), Q2_c4 datetime);
alter table dbo.Q2 add constraint FK_Q2_P2 foreign key (Q2_c1) references dbo.P2 (id);

create table dbo.Q3 (id int primary key, Q3_c1 int, Q3_c2 datetime, Q3_c3 char(10), Q3_c4 datetime);
alter table dbo.Q3 add constraint FK1_Q3_N2 foreign key (Q3_c1) references dbo.N2 (id);
alter table dbo.Q3 add constraint FK2_Q3_P2 foreign key (Q3_c2, Q3_c3) references dbo.P2 (P2_c2, P2_c3);
GO

-- populate all tables
insert into dbo.M (id, M_c1, M_c2, M_c3, M_c4)
select 1, 10, '2015-01-01', 'AB1', '2015-01-02'
union all
select 2, 20, '2015-01-02', 'AB2', '2015-01-03'
union all
select 3, 30, '2015-01-03', 'AB3', '2015-01-04';

insert into dbo.N1 (id, N1_c1, N1_c2, N1_c3, N1_c4)
select 11, 20, '2015-01-01', 'CD1', '2015-01-02'
union all
select 21, 30, '2015-01-02', 'CD2', '2015-01-03'
union all
select 31, 10, '2015-01-03', 'CD3', '2015-01-04';

insert into dbo.N2 (id, N2_c1, N2_c2, N2_c3, N2_c4)
select 11, 11, '2015-01-01', 'AB1', '2015-01-02'
union all
select 12, 22, '2015-01-02', 'AB2', '2015-01-03'
union all
select 13, 33, '2015-01-03', 'AB3', '2015-01-04';

insert into dbo.P1 (id, P1_c1, P1_c2, P1_c3, P1_c4)
select 100, 20, '2014-01-01', 'CD1', '2015-01-02'
union all
select 101, 30, '2014-01-02', 'CD2', '2015-01-03'
union all
select 102, 10, '2014-01-03', 'CD3', '2015-01-04'

insert into dbo.P2 (id, P2_c1, P2_c2, P2_c3, P2_c4)
select 200, 20, '2015-01-01', 'AB1', '2015-01-02'
union all
select 201, 30, '2015-01-02', 'AB2', '2015-01-03'
union all
select 202, 10, '2015-01-03', 'AB3', '2015-01-04'

insert into dbo.P3 (id, P3_c1, P3_c2, P3_c3, P3_c4)
select 301, 11, '2010-01-01', 'EF1', '2015-02-02'
union all
select 302, 13, '2010-01-02', 'EF2', '2015-02-03'
union all
select 303, 12, '2010-01-03', 'EF3', '2015-02-04'

insert into dbo.Q1 (id, Q1_c1, Q1_c2, Q1_c3, Q1_c4)
select 1001, 10, '1999-10-11', 'GH1', '2015-01-02'
union all
select 1011, 20, '2000-10-12', 'GH2', '2015-01-03'
union all
select 1021, 30, '2001-10-13', 'GH3', '2015-01-04'

insert into dbo.Q2 (id, Q2_c1, Q2_c2, Q2_c3, Q2_c4)
select 2001, 201, '2009-11-11', 'IJ1', '2014-01-12'
union all
select 2011, 202, '2000-12-12', 'IJ2', '2013-01-13'
union all
select 2021, 200, '2010-03-13', 'IJ3', '2012-01-14'

insert into dbo.Q3 (id, Q3_c1, Q3_c2, Q3_c3, Q3_c4)
select 3001, 11, '2015-01-01', 'AB1', '2015-01-02'
union all
select 3011, 12, '2015-01-02', 'AB2', '2015-01-03'
union all
select 3021, 13, '2015-01-03', 'AB3', '2015-01-04';
GO

SELECT [Table]='M', ID, C1=M_c1, C2=M_c2, C3=M_c3, C4=M_c4
FROM DBO.M
union all
SELECT [Table]='N1', ID, C1=N1_c1, C2=N1_c2, C3=N1_c3, C4=N1_c4
FROM DBO.N1
union all
SELECT [Table]='N2', ID, C1=N2_c1, C2=N2_c2, C3=N2_c3, C4=N2_c4
FROM DBO.N2
union all
SELECT [Table]='P1', ID, C1=P1_c1, C2=P1_c2, C3=P1_c3, C4=P1_c4
FROM DBO.P1
union all
SELECT [Table]='P2', ID, C1=P2_c1, C2=P2_c2, C3=P2_c3, C4=P2_c4
FROM DBO.P2
union all
SELECT [Table]='P3', ID, C1=P3_c1, C2=P3_c2, C3=P3_c3, C4=P3_c4
FROM DBO.P3
union all
SELECT [Table]='Q1', ID, C1=Q1_c1, C2=Q1_c2, C3=Q1_c3, C4=Q1_c4
FROM DBO.Q1
union all
SELECT [Table]='Q2', ID, C1=Q2_c1, C2=Q2_c2, C3=Q2_c3, C4=Q2_c4
FROM DBO.Q2
union all
SELECT [Table]='Q3', ID, C1=Q3_c1, C2=Q3_c2, C3=Q3_c3, C4=Q3_c4
FROM DBO.Q3
