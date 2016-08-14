CALL SYSCS_UTIL.IMPORT_DATA('movielens', 'user_rating',        NULL, '/data/movielens/u.data',          '\t', NULL, NULL, NULL, NULL, -1, '/BAD', TRUE, NULL);
CALL SYSCS_UTIL.IMPORT_DATA('movielens', 'movies',             NULL, '/data/movielens/u.item',           '|', NULL, NULL, 'dd-MMM-yyyy', NULL, -1, '/BAD', TRUE, NULL);
CALL SYSCS_UTIL.IMPORT_DATA('movielens', 'domain_genre',       NULL, '/data/movielens/u.genre',          '|', NULL, NULL, NULL, NULL, -1, '/BAD', TRUE, NULL);
CALL SYSCS_UTIL.IMPORT_DATA('movielens', 'domain_occupations', NULL, '/data/movielens/u.occupation',     '|', NULL, NULL, NULL, NULL, -1, '/BAD', TRUE, NULL);
CALL SYSCS_UTIL.IMPORT_DATA('movielens', 'user_demographics',  NULL, '/data/movielens/u.user',           '|', NULL, NULL, NULL, NULL, -1, '/BAD', TRUE, NULL);
CALL SYSCS_UTIL.IMPORT_DATA('movielens', 'data_feature',       NULL, '/data/movielens/data_feature.csv', ',', NULL, NULL, NULL, NULL, -1, '/BAD', TRUE, NULL);
