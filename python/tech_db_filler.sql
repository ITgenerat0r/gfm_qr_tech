use tech_db;


insert into workers(w_login, w_passhash, w_name) value ('test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'test name');
insert into workers(w_login, w_passhash, w_name) value ('editor', '1553cc62ff246044c683a61e203e65541990e7fcd4af9443d22b9557ecc9ac54', 'editor name');
insert into workers(w_login, w_passhash, w_name) value ('view', '2bcb43cbc8f6b7ef66331532881143fcbae60a879db3a8fb853f645bb24c2b3c', 'view name');
insert into workers(w_login, w_passhash, w_name) value ('worker', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'username');


insert into user_groups(g_name, access) value ('admins', 3);
insert into user_groups(g_name, access) value ('editors', 2);
insert into user_groups(g_name, access) value ('viewers', 0);
insert into user_groups(g_name, access) value ('workers', 1);





insert into wg_bonds(w_login, g_name) value ('test', 'workers');
insert into wg_bonds(w_login, g_name) value ('editor', 'editors');
insert into wg_bonds(w_login, g_name) value ('view', 'viewers');
insert into wg_bonds(w_login, g_name) value ('worker', 'workers');