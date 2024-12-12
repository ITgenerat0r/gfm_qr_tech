import Config
from includes import *
import pymysql
from uuid import uuid4
from datetime import datetime
from shutil import copy



class Database:
    "Base class for Database"
    host = "localhost"
    user = "root"

    def __init__(self, host, user, password, db_name):
        self.host = host
        self.__port = 3306
        self.user = user
        self.password = password
        self.db_name = db_name
        self.__status = 1
        self.__logs = True
        self.__stop_errors = False


    def set_time_out(self, tm=28800):
        self._commit(f"SET GLOBAL connect_timeout={tm}")
        self._commit(f"SET GLOBAL interactive_timeout={tm}")
        self._commit(f"SET GLOBAL wait_timeout={tm}")

    def __del__(self):
        self.close_connect()

    def set_logs(self, log=True):
        self.__logs = log

    def set_stop_errors(self, stop_err=False):
        self.__stop_errors = stop_err

    def connect(self):
        try:
            self.connection = pymysql.connect(
                host=self.host,
                port=self.__port,
                user=self.user,
                password=self.password,
                database=self.db_name,
                cursorclass=pymysql.cursors.DictCursor
            )
            self.__status = 1
            if self.__logs:
                print(f"success {self.db_name}")
        except Exception as ex:
            if self.__logs:
                print(f"Connection refused {self.db_name}")
                print(ex)
                if self.__stop_errors:
                    input("Press enter to continue...")


    def _checkSlash(self, line):
        return line.replace('\\', '\\\\')

    def _checkQuote(self, line):
        # return line.replace("'", '"')
        return line.replace('"', "'")

    def _commit(self, cmd, err="commit error"):
        with self.connection.cursor() as cursor:
            try:
                if self.__logs:
                    print(f"_commit({cmd})")
                cursor.execute(cmd)
                self.connection.commit()
                return True
            except Exception as ex:
                if self.__logs:
                    print(cmd)
                    print(red_text("Error:"), err)
                    print(ex)
                    if self.__stop_errors:
                        input("Press enter to continue...")
                self.__status = 0
                self.heal()
                return False
            return False

    def _fetchall(self, cmd, err="fetch error"):
         with self.connection.cursor() as cursor:
            try:
                if self.__logs:
                    print(f"_fetchall({cmd})")
                cursor.execute(cmd)
                return cursor.fetchall()
            except Exception as ex:
                if self.__logs:
                    print(red_text("Error:"), err)
                    print(cmd)
                    print(ex)
                    if self.__stop_errors:
                        input("Press enter to continue...")
                self.__status = 0
                self.heal()
                return {}
            return {}

    def heal(self):
        if self.__status != 1:
            self.connect()
        return self.__status == 1

    def close_connect(self):
        self.connection.close()


    def get_current_time(self):
        return datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-3]








class Techno(Database):

    stages = {
        "Сборка до заливки",
        "Проверка",
        "Климат",
        "Сборка после заливки",
        "Проверка после заливки",
        "Калибровка",
        "Отладка"
    }

    def add_worker(self, login, password, name):
        if login:
            columns = "w_login"
            values = f"'{login}'"

            columns += ", w_passhash"
            values += f", '{password}'"

            columns += ", w_name"
            values += f", '{name}'"

            self._commit(f"insert into workers({columns}) values ({values})")


    def set_worker_name(self, id, name):
        if name:
            self._commit(f"update workers set w_name = '{name}' where id = {id}")

    def set_worker_login(self, id, login):
        if name:
            self._commit(f"update workers set w_login = '{login}' where id = {id}")

    def set_worker_password(self, id, phash):
        if name:
            self._commit(f"update workers set w_passhash = '{phash}' where id = {id}")


    def delete_worker(self, id):
        if id:
            self._commit(f"delete from workers where id = {id}")

    def get_worker(self, login):
        r = self._fetchall(f"select * from workers where w_login = '{login}'")
        if r:
            return r[0]
        return {}

    def get_worker_by_id(self, id):
        r = self._fetchall(f"select * from workers where id = {id}")
        if r:
            return r[0]
        return {}



    def add_operation(self, device_number, worker_login, operation):
        values = f"{device_number}, '{operation}', '{worker_login}', '{self.get_current_time()}'"
        self._commit(f"insert into operations(serial_number, operation, worker, dt) values({values})")
        r = self._fetchall(f"SELECT * FROM operations ORDER BY ID DESC LIMIT 1")
        if r:
            return r[0]['id']
        return 0


    def delete_operation(self, device_number, login, operation, date):
        self._commit(f"delete from operations where serial_number = {device_number} and worker = '{login}' and operation = '{operation}' and dt = '{date}'")

    def delete_operation_by_id(self, id):
        self._commit(f"delete from operations where id = {id}")

    def get_operation_for_worker(self, login):
        return self._fetchall(f"select * from operations where worker = {login}")

    def get_operation_for_device(self, number):
        return self._fetchall(f"select * from operations where serial_number = {number}")

    def set_operation(self, device_number, worker_login, operation):
        ls = self.get_operation_for_device(device_number)
        key = ""
        for l in ls:
            print(ls)
            if ls['operation'] == operation:
                key = ls
                break
        if not key:
            self.add_operation(device_number, worker_login, operation)
            return "new"
        else:
            print("This operation already done!")
            worker = self.get_worker(key['worker'])
            print(key)
            if worker:
                return worker[0]
            return ""







    def add_decimal(self, number, name="", type=""):
        if number:
            dt = self._fetchall(f"select * from decimals where num = '{number}'")
            if len(dt):
                return 0
            fields = "num"
            values = f"'{number}'"
            if name:
                fields += ", d_name"
                values += f", '{name}'"
            if type:
                fields += ", d_type"
                values += f", '{type}'"
            self._commit(f"insert into decimals({fields}) value({values})")
            return 1
        return 0


    def get_decimal(self, id):
        data = self._fetchall(f"select * from decimals where id = {id}")
        if len(data):
            return data[0]
        return {}

    def get_decimal_by_num(self, number):
        data = self._fetchall(f"select * from decimals where num = '{number}'")
        if len(data):
            return data[0]
        return {}


    def update_decimal_num(self, id, number):
        if id:
            dt = self._fetchall(f"select * from decimals where id = {id}")
            if len(dt):
                if number:
                    self._commit(f"update devices set num = '{number}' where id = {id}")
                    return 1
        return 0

    def update_decimal_name(self, id, name):
        if id:
            dt = self._fetchall(f"select * from decimals where id = {id}")
            if len(dt):
                if name:
                    self._commit(f"update devices set d_name = '{name}' where id = {id}")
                    return 1
        return 0


    def update_decimal_type(self, id, type):
        if id:
            dt = self._fetchall(f"select * from decimals where id = {id}")
            if len(dt):
                if type:
                    self._commit(f"update devices set d_type = '{type}' where id = {id}")
                    return 1
        return 0










    def get_device(self, number):
        data = self._fetchall(f"select * from devices LEFT JOIN decimals ON devices.decimal_id = decimals.id where serial_number = {number}")
        if len(data):
            return data[0]
        return {}

    def add_device(self, number, decimal=""):
        if number:
            dt = self._fetchall(f"select * from devices where serial_number = {number}")
            if len(dt):
                return 0
            fields = "serial_number"
            values = f"{number}"
            if decimal:
                d_num = self.get_decimal_by_num(decimal)
                if len(d_num):
                    fields += ", decimal_id"
                    values += f", '{d_num['id']}'"
            self._commit(f"insert into devices({fields}) value({values})")
            return 1

    def update_device(self, number, decimal=0):
        if number and decimal:
            dt = self._fetchall(f"select * from devices where serial_number = {number}")
            if len(dt):
                self._commit(f"update devices set decimal_id = '{decimal}' where serial_number = {number}")
                return 1
            return 0



    def delete_device(self, number):
        self._commit(f"delete from operations where serial_number = {number}")
        self._commit(f"delete from devices where serial_number = {number}")





    def get_group(self, id):
        if id:
            dt = self._fetchall(f"select * from user_groups where id = {id}")
            if len(dt):
                return dt[0]
        return {}


    def add_group(self, name, access_level=0):
        if name:
            g = self._fetchall(f"select * from user_groups where g_name = '{name}'")
            if not len(g):
                self._commit(f"insert into user_groups (g_name, access) values ('{name}', {access_level})")
                return 1
        return 0


    def add_user_to_group(self, user_id, group_id):
        if user_id and group_id:
            dt = self._fetchall(f"select * from wg_bonds where w_login = {user_id} and g_name = {group_id}")
            if not len(dt):
                self._commit(f"insert into wg_bonds(w_login, g_name) values ({user_id}, {group_id})")
                return 1
        return 0









    def login(self, login, password):
        worker = self.get_worker(login)
        if worker:
            if worker['w_passhash'] == password:
                return True
        return False


    def get_worker_groups(self, worker_login):
        user = self.get_worker(worker_login)
        if len(user):
            fields = "user_groups.*"
            data = self._fetchall(f"select {fields} from wg_bonds LEFT JOIN user_groups ON wg_bonds.g_name = user_groups.id where w_login = '{user['id']}'")
            if data:
                return data
        return {}

    def get_stages(self):
        rs = ""
        for st in self.stages:
            if rs:
                rs += "|"
            rs += st
        return rs

    def new_session(self, iv="", aes_key=""):
        cols = "date_last_conn"
        vals = f"'{self.get_current_time()}'"
        if iv:
            cols += ", iv"
            vals += f", '{iv}'"
        if aes_key:
            cols += ", aes_key"
            vals += f", '{aes_key}'"
        self._commit(f"insert into sessions ({cols}) value ({vals})")
        r = self._fetchall(f"SELECT * FROM sessions ORDER BY ID DESC LIMIT 1")
        if r:
            return r[0]['id']
        return 0


    def delete_session(self, session_id):
        self._commit(f"delete from sessions where id = {session_id}")

    def delete_old_sessions(self):
        time = self.get_current_time()
        time = time[:10]
        dt = self._fetchall(f"select * from sessions where date_last_conn < '{time}';")
        for i in dt:
            self._commit(f"delete from sessions where id = {i['id']}")
        

    def get_session(self, session_id):
        dt = self._fetchall(f"select * from sessions where id = {session_id}")
        if dt:
            return dt[0]
        return {}

    def set_iv(self, session_id, iv):
        self._commit(f"update sessions set iv = '{iv}' where id = {session_id}")

    def get_iv(self, session_id):
        ss = self.get_session(session_id)
        if ss:
            return ss['iv']

    def set_aes_key(self, session_id, aes_key):
        self._commit(f"update sessions set aes_key = '{aes_key}' where id = {session_id}")


        