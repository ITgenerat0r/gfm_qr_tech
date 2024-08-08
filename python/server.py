import socket
import os
from sys import argv
from time import sleep
import subprocess
from subprocess import PIPE
from includes import *
from controller import Controller
from thread import Threads

from data_split_class import LData
from MDataBase import Techno
import Config

version = "0.1"

HOST = '0.0.0.0'  # Standard loopback interface address (localhost)
PORT = 27499      # Port to listen on (non-privileged ports are > 1023)



logs = True

def prt(text=""):
	if logs:
		print(text)

for i in argv:
	if i == "-log":
		try:
			os.system('cls')
			os.system('clear')
		except Exception as e:
			pass
		print("Log enabled")
		logs = True
		print(f"Version {version}")



#define
GOOD_RESPONSE = b"OK"
help_list = ["drop", "test"]
DB_timeout = 2147483


ths = Threads()
db = Techno(Config.host, Config.user, Config.password, Config.db_name)


def db_connect():
	global db
	global DB_timeout
	db.connect()
	db.set_time_out(DB_timeout)



sleep(3)


def handler(conn, addr):
	global server_run
	global ths
	with conn:
		prt(f'Connected by {addr}')
		cn = Controller(conn, logs)

		data = cn.recv()
		if logs:
			print("Received:", data)

		ldata = LData(data)
		if not ldata.get_size():
			return


		if ldata.get(0) == "drop":
			server_run = False
			cn.send('ok')
			prt('RETURN')
			return
		elif ldata.get(0) == "help":
			help_text = f"Server version {version}\r\n"
			for row in help_list:
				help_text += f"   {row}\r\n"
			cn.send(help_text)
		elif ldata.get(0) == 'lg': # login
			res = db.login(ldata.get(1), ldata.get(2))
			if res:
				worker = db.get_worker(ldata.get(1))
				cn.send(f"success {worker['w_name']}")
			else:
				cn.send("error")
		elif ldata.get(0) == "test":
			cn.send("test_ok")
		else:
			cn.send('ok')



server_run = True
db_run = True
while server_run:
	try:
		while db_run:
			try:
				db_connect()
				db_run = False
			except Exception as db_e:
				prt(f"DB error: {db_e}")
				sleep(10)
				db_run = True
		prt()
		prt("Listen...")
		prt()
		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
			s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
			s.bind((HOST, PORT))
			s.listen()
			conn, addr = s.accept()
			prt("Thread.")
			ths.run(handler, (conn, addr))
	except Exception as e:
		prt(f"Err: {e}")
		db_run = True
