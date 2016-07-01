call SYSCS_UTIL.IMPORT_DATA ('movielens', 'user_rating',null,'/data/movielens/u.data','\t',null, null, null,null,-1,'/BAD',true,null);
call SYSCS_UTIL.IMPORT_DATA ('movielens', 'movies',null,'/data/movielens/u.item','|',null, null, 'dd-MMM-yyyy',null,-1,'/BAD',true,null);
call SYSCS_UTIL.IMPORT_DATA ('movielens', 'domain_genre',null,'/data/movielens/u.genre','|',null, null, null,null,-1,'/BAD',true,null);
call SYSCS_UTIL.IMPORT_DATA ('movielens', 'domain_occupations',null,'/data/movielens/u.occupation','|',null, null, null,null,-1,'/BAD',true,null);
call SYSCS_UTIL.IMPORT_DATA ('movielens', 'user_demographics',null,'/data/movielens/u.user','|',null, null, null,null,-1,'/BAD',true,null);
call SYSCS_UTIL.IMPORT_DATA ('movielens', 'data_feature',null,'/data/movielens/data_feature.csv',',',null, null, null,null,-1,'/BAD',true,null);
