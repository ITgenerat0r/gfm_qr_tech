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


	def bytes2hexstr(self, data):
		return f"{data.hex()}"

	def hexstr2bytes(self, data):
		return b'' + bytes.fromhex(data)

	def new_iv(self):
		return self.bytes2hexstr(Random.new().read(AES.block_size))



	def __pad(self, s):
		return s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s) % BLOCK_SIZE)


	def __unpad(self, s):
		return s[: -ord(s[len(s) - 1 :])]


	def encrypt(self, plain_text, key, iv):
		if self.__logs:
			print("ENCRYPT")
			print(f"plain_text: {plain_text}")
			print(f"key: {self.bytes2hexstr(key.encode('utf-8'))}")
			print(f"iv: {iv}")
		private_key = hashlib.sha256(key.encode("utf-8")).digest()
		# print("private_key: ", self.bytes2hexstr(private_key))
		plain_text = self.__pad(plain_text)
		# print("After padding:", plain_text)
		# iv = Random.new().read(AES.block_size)
		# iv = self.hexstr2bytes('c33bfeae1263c98633bc9e66c6ab8746')
		cipher = AES.new(private_key, AES.MODE_CBC, self.hexstr2bytes(iv))
		# res = base64.b64encode(iv + cipher.encrypt(plain_text.encode('utf-8')))
		enc_data = cipher.encrypt(plain_text.encode('utf-8'))
		# res = self.__iv + enc_data
		# print(f"only encrypted {self.bytes2hexstr(enc_data)}")
		return self.bytes2hexstr(enc_data)


	def decrypt(self, data, key, iv):
		if self.__logs:
			print("DECRYPT")
			print(f"data: {data}")
			print(f"key: {key}")
			print(f"iv: {iv}")
		cipher_text = self.hexstr2bytes(data)
		private_key = hashlib.sha256(key.encode("utf-8")).digest()
		# cipher_text = base64.b64decode(cipher_text)
		# iv = cipher_text[:16]
		cipher = AES.new(private_key, AES.MODE_CBC, self.hexstr2bytes(iv))
		return bytes.decode(self.__unpad(cipher.decrypt(cipher_text)))



cp = Security()
message = "asdfasdf-0123456789abcef0123456789abcdef"
# message = input("->")
key = "develop"
iv = "c33bfeae1263c98633bc9e66c6ab8746"

print("Init text", message)
encrypted_msg = cp.encrypt(message, key, iv)
print(f"Encrypted Message({len(encrypted_msg)}):", encrypted_msg)
decrypted_msg = cp.decrypt(encrypted_msg, key, iv)
print("Decrypted Message:", decrypted_msg)