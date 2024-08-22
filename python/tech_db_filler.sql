use tech_db;


insert into workers(w_login, w_passhash, w_name) value ('test', 'test', 'test name');
insert into workers(w_login, w_passhash, w_name) value ('editor', 'editor', 'editor name');
insert into workers(w_login, w_passhash, w_name) value ('view', 'view', 'view name');
insert into workers(w_login, w_passhash, w_name) value ('worker', 'test', 'username');


insert into user_groups(g_name, access) value ('admins', 3);
insert into user_groups(g_name, access) value ('editors', 2);
insert into user_groups(g_name, access) value ('viewers', 0);
insert into user_groups(g_name, access) value ('workers' 1);





insert into wg_bonds(w_login, g_name) value ('test', 'workers');
insert into wg_bonds(w_login, g_name) value ('editor', 'editors');
insert into wg_bonds(w_login, g_name) value ('view', 'viewers');
insert into wg_bonds(w_login, g_name) value ('worker', 'workers');