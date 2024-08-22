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
		self.__iv = Random.new().read(AES.block_size)


	def enable_log(self, log=True):
		self.__logs = log


	def str2hex(self, data):
		return f"{data.hex()}"

	def hex2str(self, data):
		return b'' + bytes.fromhex(data)


	def set_iv(self, new_iv):
		self.__iv = self.hex2str(new_iv)

	def get_iv(self):
		return self.str2hex(self.__iv)



	def __pad(self, s):
		return s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s) % BLOCK_SIZE)


	def __unpad(self, s):
		return s[: -ord(s[len(s) - 1 :])]


	def encrypt(self, plain_text, key):
		print(f"key: {self.str2hex(key.encode('utf-8'))}")
		private_key = hashlib.sha256(key.encode("utf-8")).digest()
		print("private_key: ", self.str2hex(private_key))
		plain_text = self.__pad(plain_text)
		# print("After padding:", plain_text)

		# iv = Random.new().read(AES.block_size)
		# iv = self.hex2str('c33bfeae1263c98633bc9e66c6ab8746')

		cipher = AES.new(private_key, AES.MODE_CBC, self.__iv)
		# res = base64.b64encode(iv + cipher.encrypt(plain_text.encode('utf-8')))
		enc_data = cipher.encrypt(plain_text.encode('utf-8'))
		# res = self.__iv + enc_data
		# print(f"only encrypted {self.str2hex(enc_data)}")
		return self.str2hex(enc_data)


	def decrypt(self, data, key):
		cipher_text = self.hex2str(data)
		private_key = hashlib.sha256(key.encode("utf-8")).digest()
		# cipher_text = base64.b64decode(cipher_text)
		# iv = cipher_text[:16]
		cipher = AES.new(private_key, AES.MODE_CBC, self.__iv)
		return self.__unpad(cipher.decrypt(cipher_text))



cp = Security()
message = "asdfasdf"
key = "develop"
encrypted_msg = cp.encrypt(message, key)
print("Encrypted Message:", encrypted_msg)
decrypted_msg = cp.decrypt(encrypted_msg, key)
print("Decrypted Message:", bytes.decode(decrypted_msg))