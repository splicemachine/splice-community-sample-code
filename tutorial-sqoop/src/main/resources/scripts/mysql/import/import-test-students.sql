truncate table splice.students;
call syscs_util.import_data('SPLICE','students',null,'<TUTORIAL-DIR>/data/test/students/',null,null,null,null,null,-1,'<TUTORIAL-DIR>/status/test/students/',null,null);
