import os
from includes import green_text, yellow_text, red_text, blue_text
from security import Security


# for exchange between client and server
class Controller():
	"""docstring for Controller"""
	def __init__(self, connection, logs = False):
		self.__connection = connection
		self.__logs = logs
		self.__package_size = 1024
		self.__code_size = 2
		self.__cipher = Security(self.__logs)


	def __prt(self, text=""):
		if self.__logs:
			print(text)

	def __send_bit(self, text):
		self.__connection.sendall(text.encode('utf-8'))
		self.__prt(blue_text(f" => {text}"))

	def __recv_bit(self):
		res = self.__connection.recv(self.__package_size).decode('utf-8')
		self.__prt(blue_text(f" <= {str(res)}"))
		return res

	def send(self, text):
		# self.__prt(f"send({text})")
		# text = self.__cipher.encrypt(text_clear)
		self.__prt()
		while len(text) > self.__package_size - self.__code_size:
			self.__send_bit(f"b_{text[:self.__code_size]}")
			text = text[self.__code_size:]
			self.__recv_bit()
		else:
			self.__send_bit(f"e_{text}")

	def recv(self):
		# dt = self.__recv_bit()
		# res = dt[self.__code_size:]
		self.__prt()
		res = ""
		while True:
			dt = self.__recv_bit()
			res += dt[self.__code_size:]
			if dt[:self.__code_size] == 'b_':
				self.__send_bit('ok')
			else:
				break
		# self.__prt(f"recv() = {res}")
		return res
		res_clear = self.__cipher.decrypt(res)
		return res_clear
