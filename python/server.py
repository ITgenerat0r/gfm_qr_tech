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

version = "1.0"

HOST = '0.0.0.0'  # Standard loopback interface address (localhost)
PORT = 11200      # Port to listen on (non-privileged ports are > 1023)



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

		if data[:2] == "ns":
			iv = cn.get_new_iv()
			session_id = db.new_session(iv=iv)
			cn.send(f"{session_id} {iv}")
		elif data.find(' ') >= 0:
			sp = LData(data)
			session_id = sp.get(0)
			en_data = sp.get(1)
			session = db.get_session(session_id)
			print(session)
			iv = session['iv']
			aes_key = session['aes_key']
			cn.set_iv(iv)
			cn.enable_encryption()
			data = cn.decrypt(en_data)
			ldata = LData(data)
			cn.set_iv(en_data[-32:])
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
			elif ldata.get(0) == "getdevicedata":
				number = ldata.get(1)
				device = db.get_device(number)
				if device:
					print("device")
					print(device)
					device_data = f"serial_number:{device['serial_number']}|"
					device_data += f"decimal_number:{device['num']}|"
					device_data += f"d_name:{device['d_name']}|"
					device_data += f"d_type:{device['d_type']}"
					cn.send(device_data)
				else:
					cn.send("none")
			elif ldata.get(0) == "getworkergroups":
				data = db.get_worker_groups(ldata.get(1))
				tx = ""
				for g in data:
					if tx:
						tx += "|"
					tx += f"{g['g_name']}"
				if tx:
					cn.send(tx)
				else:
					cn.send("none")
			elif ldata.get(0) == "getdeviceoperations":
				ops = db.get_operation_for_device(ldata.get(1))
				print(ldata.get_all())
				print("ops", ops)
				tx = ""
				for o in ops:
					if len(tx) > 0:
						tx += "•"
					w = db.get_worker(o['worker'])
					name = o['worker']
					if w:
						name = w['w_name']
					tx += f"id:{o['id']}|date:{o['dt']}|worker:{o['worker']}|name:{name}|operation:{o['operation']}"
				if not tx:
					tx = "none"
				cn.send(tx)
			elif ldata.get(0) == "createdevice":
				login = ldata.get(1)
				data = ldata.get(2)
				serial = ""
				decimal = ""
				name = ""
				tp = ""
				for i in data.split('|'):
					kv = i.split(':')
					print(kv)
					key = kv[0]
					value = kv[1]
					if key == "serial":
						serial = value
					elif key == "decimal":
						decimal = value
					elif key == "name":
						name = value
					elif key == "type":
						tp = value
				cn.send('ok')
				dd = db.get_decimal_by_num(decimal)
				decimal_id = 0
				if len(dd):
					decimal_id = dd['id']
				res = db.add_device(serial, decimal_id)
				print(f"add status: {res}")
			elif ldata.get(0) == "updatedevice":
				login = ldata.get(1)
				data = ldata.get(2)
				serial = ""
				decimal = ""
				name = ""
				tp = ""
				for i in data.split('|'):
					kv = i.split(':')
					print(kv)
					key = kv[0]
					value = kv[1]
					if key == "serial":
						serial = value
					elif key == "decimal":
						decimal = value
					elif key == "name":
						name = value
					elif key == "type":
						tp = value
				cn.send('ok')
				dev = db.get_device(serial)
				decimal_id = 0
				res = 0
				if len(dev):
					decimal_id  = dev['decimal_id']
					dec = db.get_decimal(decimal_id)
					if len(dec):
						if decimal == dec['num']:
							db.update_decimal_name(decimal_id, name)
							db.update_decimal_type(decimal_id, type)
							res = 1
						else:
							decbn = db.get_decimal_by_num(decimal)
							if len(decbn):
								res = db.update_device(serial, decbn['id'])
								db.update_decimal_name(decbn['id'], name)
								db.update_decimal_type(decbn['id'], type)
							else:
								res = db.add_decimal(decimal, name, type)
				# res = db.update_device(serial, decimal_id)
				print(f"add status: {res}")
			elif ldata.get(0) == "deletedevice":
				login = ldata.get(1)
				number = 0
				# check login
				grps = db.get_worker_groups(login)
				for grp in grps:
					if grp['g_name'] in {"editors", "admins"}:
						number = int(ldata.get(2))
						db.delete_device(number)
						print(f"deleted {number}")
						cn.send('ok')
						break
				if number == 0:
					cn.send('permission denied')
			elif ldata.get(0) == "getoperationtypes":
				tx = db.get_stages()
				cn.send(tx)
			elif ldata.get(0) == "operation":
				resp = ""
				if ldata.get(1) == "add":
					data = ldata.get_from(2).split('|')
					print(f"len data: {len(data)}")
					print(data)
					if len(data) > 2:
						login = data[0]
						number = data[1]
						operation = data[2]
						resp = f" {db.add_operation(number, login, operation)}"
				elif ldata.get(1) == "delete":
					db.delete_operation_by_id(ldata.get(2))
				cn.send(f'ok{resp}')
			elif ldata.get(0) == "test":
				cn.send("test_ok")
			else:
				cn.send('ok')
			iv = cn.get_iv()
			db.set_iv(session_id, iv)

		else:
			cn.send('bad')



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
