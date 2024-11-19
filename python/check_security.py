from security import *
# ------ TEST ---------------------------------------


# RSA

print("RSA")
cl = Security(True)
key_len = 2048
priv_key, pub_key = cl.generate_rsa_keys(key_len)
print(f"Private key({len(priv_key)}): ", priv_key)
print(f"Public key({len(pub_key)}): ", pub_key)

data = "<testing rsa>"
print("init data: ", data)

#
en_data = cl.rsa_encrypt(data, pub_key)
print("Encrypted data:", en_data)

#
de_data = cl.rsa_decrypt(en_data, priv_key)
print("Decrypted data:", de_data)

print("-----")
print("\n\n\n")

# ------








# AES

print("AES")
cp = Security(True)
# message = "asdfasdf-0123456789abcef0123456789abcdef"
message = "<DATA: default: 1235215ffasdfasdfп>"
# message = input("->")
key = cp.sha256("develop")
# iv = "c33bfeae1263c98633bc9e66c6ab8746"
iv = cp.new_iv()

print(f"Init text({len(message)}): ", message)
encrypted_msg = cp.encrypt(message, key, iv)
print(f"Encrypted Message({len(encrypted_msg)}):", encrypted_msg)
decrypted_msg = cp.decrypt(encrypted_msg, key, iv)
print("Decrypted Message:", decrypted_msg)

# ---------------------------------------------------



