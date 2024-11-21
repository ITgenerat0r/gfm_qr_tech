
from security import *


s = Security()
s.enable_log()




g = "operation delete 14"
print(f"Plain text: {g}")

iv = "46c1c17e69cb1d9355485e093bec7493"
key = "947726dd6318753268f3bfbe5e87ae2afe220db399c26e119c181a59227b0c60"


e_data = s.encrypt(g, key, iv)


d_data = s.decrypt(e_data, key, iv)


print(f"Decrypted: {d_data}")


