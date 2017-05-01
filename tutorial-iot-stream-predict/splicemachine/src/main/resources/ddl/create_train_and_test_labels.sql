set schema IOT;

drop table if exists TRAIN_LABEL_TEMP;
create table TRAIN_LABEL_TEMP 
(
	engine_type int,
	unit int,
	time bigint,
	prediction int
);

insert into IOT.TRAIN_LABEL_TEMP
select a.engine_type, a.unit,  a.time, case when ((b.maxtime - a.time) > 30) then 0  else 1  end  from IOT.TRAIN_DATA a inner join (select engine_type, unit, max(time) maxtime from IOT.TRAIN_DATA group by engine_type, unit) b on a.engine_type = b.engine_type  and a.unit = b.unit ;

update  IOT.TRAIN_DATA u set prediction = (select a.prediction from IOT.TRAIN_LABEL_TEMP a where a.engine_type =1 and a.unit = u.unit  and a.time = u.time) where u.engine_type=1 ;



drop table if exists TEST_LABEL_TEMP;
create table TEST_LABEL_TEMP
(
	engine_type int,
	unit int,
	time bigint,
	prediction int
);

insert into IOT.TEST_LABEL_TEMP
select a.engine_type, a.unit,  a.time, case when ((b.maxtime  +r.rul- a.time) > 30) then 0   else 1  end  from IOT.TETST_DATA a 
inner join (select engine_type, unit, max(time) maxtime from IOT.TEST_DATA group by engine_type, unit) b on a.engine_type = b.engine_type  and a.unit = b.unit  inner join IOT.TEST_RUL_VALUES r on a.engine_type =r.engine_type and a.unit = r.unit;



update  IOT.TEST_DATA u set prediction = (select a.prediction from IOT.TEST_LABEL_TEMP a where a.engine_type =1 and a.unit = u.unit  and a.time = u.time) where u.engine_type=1 ;




