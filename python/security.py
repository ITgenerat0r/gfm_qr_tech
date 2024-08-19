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




	def __pad(self, s):
		return s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s) % BLOCK_SIZE)


	def __unpad(self, s):
		return s[: -ord(s[len(s) - 1 :])]


	def encrypt(self, plain_text, key):
		private_key = hashlib.sha256(key.encode("utf-8")).digest()
		plain_text = self.__pad(plain_text)
		# print("After padding:", plain_text)
		iv = Random.new().read(AES.block_size)
		cipher = AES.new(private_key, AES.MODE_CBC, iv)
		return base64.b64encode(iv + cipher.encrypt(plain_text.encode('utf-8')))


	def decrypt(self, cipher_text, key):
		private_key = hashlib.sha256(key.encode("utf-8")).digest()
		cipher_text = base64.b64decode(cipher_text)
		iv = cipher_text[:16]
		cipher = AES.new(private_key, AES.MODE_CBC, iv)
		return self.__unpad(cipher.decrypt(cipher_text[16:]))



# cp = Security()
# message = input("Enter message to encrypt: ")
# key = "develop"
# encrypted_msg = cp.encrypt(message, key)
# print("Encrypted Message:", encrypted_msg)
# decrypted_msg = cp.decrypt(encrypted_msg, key)
# print("Decrypted Message:", bytes.decode(decrypted_msg))