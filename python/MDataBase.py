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
        values = f"{device_number}, '{operation}', {worker_login}, {self.get_current_time()}"
        self._commit(f"insert into operations(serial_number, operation, worker, dt) values({values})")

    def delete_operation(self, device_number, operation):
        self._commit(f"delete from operations where serial_number = {device_number} and operation = '{operation}'")

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


    def get_device(self, number):
        data = self._fetchall(f"select * from devices where serial_number = {number}")
        if len(data):
            return data[0]
        return {}

    def add_device(self, number, decimal="", name="", type=""):
        if number:
            dt = self._fetchall(f"select * from devices where serial_number = {number}")
            if len(dt):
                return 0
            fields = "serial_number"
            values = f"{number}"
            if decimal:
                fields += ", decimal_number"
                values += f", '{decimal}'"
            if name:
                fields += ", d_name"
                values += f", '{name}'"
            if type:
                fields += ", d_type"
                values += f", '{type}'"
            self._commit(f"insert into devices({fields}) value({values})")
            return 1



    def delete_device(self, number):
        self._commit(f"delete from operations where serial_number = {number}")
        self._commit(f"delete from devices where serial_number = {number}")

    def login(self, login, password):
        worker = self.get_worker(login)
        if worker:
            if worker['w_passhash'] == password:
                return True
        return False


    def get_worker_groups(self, worker_login):
        data = self._fetchall(f"select * from wg_bonds where w_login = '{worker_login}'")
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



        