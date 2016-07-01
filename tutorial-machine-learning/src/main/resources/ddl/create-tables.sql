create schema movielens;
set schema movielens;

-- This table definition corresponds to the u.data file
-- The data in this file is tab delimited
create table user_rating (
	user_id integer,
	movie_id integer,
	rating integer,
	create_dt bigint,
	primary key (user_id, movie_id)
);

-- This table definition corresponds to the u.item
-- The data in this file is tab delimited
create table movies (
	movie_id integer,
	movie_title varchar(250),
	release_date date, 
	video_release_date date,
	imdb_url varchar(250),
	unknown_genre varchar(1),
	action varchar(1), 
	adventure varchar(1), 
	animation varchar(1), 
	childrens varchar(1),  
	comedy varchar(1),  
	crime varchar(1), 
	documentary varchar(1),  
	drama varchar(1), 
	fantasy varchar(1),              
	film_noir varchar(1),  
	horror varchar(1),  
	musical varchar(1),  
	mystery varchar(1),  
	romance varchar(1),  
	sci_fi varchar(1),           
	thriller varchar(1),  
	war varchar(1), 
	western varchar(1),
	primary key(movie_id)
);

-- This table definition corresponds to the u.genre
-- The data in that file is pipe delimited
create table domain_genre (
	genre varchar(50),
	genre_id integer
);

-- This table definition corresponds to the u.occupation
-- The data in that file with one column - no delimiter
create table domain_occupations (
	occupation varchar(50)
);

-- This table definition corresponds to the u.user
-- The data in that file is pipe delimited
create table user_demographics (
	user_id integer,
	age integer,
	gender char(1),
	occupation varchar(20),
	zip_code varchar(10),
	primary key (user_id)
);

create table data_feature(
	table_name varchar(100),
	column_name varchar(100),
	data_type varchar(20),
	continuous char(1),
	discrete char(1),
	parse_method varchar(100)
);



