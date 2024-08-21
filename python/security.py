import base64
import hashlib
# pip install pycryptodome
from Crypto.Cipher import AES
from Crypto import Random

BLOCK_SIZE = 16


class Security():
	def __init__(self, log=False):
		self.__logs = log
		self.__aes_key = "develop"


	def enable_log(self, log=True):
		self.__logs = log


	def str2hex(self, data):
		res = ""
		for i in data:
			# print(hex(i))
			add = f"{hex(i)}"[2:]
			# print(add)
			while len(add) < 2:
				add = f"0{add}"
			res += add
		return res

	def hex2str(self, data):
		res = b'' + bytes.fromhex(data)
		# sw = False
		# add = 16
		# for i in data:
		# 	if sw:
		# 		sw = False
		# 	else:
		# 		sw = True
		# res += add
		return res





	def __pad(self, s):
		return s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s) % BLOCK_SIZE)


	def __unpad(self, s):
		return s[: -ord(s[len(s) - 1 :])]


	def encrypt(self, plain_text, key=""):
		if not key:
			key = self.__aes_key
		private_key = hashlib.sha256(key.encode("utf-8")).digest()
		plain_text = self.__pad(plain_text)
		# print("After padding:", plain_text)
		iv = Random.new().read(AES.block_size)
		print(f"iv: {self.str2hex(iv)}")
		cipher = AES.new(private_key, AES.MODE_CBC)
		encrypted_text = base64.b64encode(iv + cipher.encrypt(plain_text.encode('utf-8')))
		print(f"TEST: {self.str2hex(cipher.encrypt(plain_text.encode('utf-8')))}")
		print(f"Encrypted text: {encrypted_text}")
		return self.str2hex(encrypted_text)


	def decrypt(self, data, key=""):
		cipher_text = b'' + self.hex2str(data)
		print("Encrypted test:", cipher_text)
		if not key:
			key = self.__aes_key
		private_key = hashlib.sha256(key.encode("utf-8")).digest()
		cipher_text = base64.b64decode(cipher_text)
		iv = cipher_text[:16]
		cipher = AES.new(private_key, AES.MODE_CBC, iv)
		return self.__unpad(cipher.decrypt(cipher_text[16:]))



cp = Security()
message = "asdfasdf"
key = "develop00develop"
encrypted_msg = cp.encrypt(message, key)
print("Encrypted Message:", encrypted_msg)
decrypted_msg = cp.decrypt(encrypted_msg, key)
print("Decrypted Message:", bytes.decode(decrypted_msg))