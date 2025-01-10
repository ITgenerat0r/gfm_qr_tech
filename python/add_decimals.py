from MDataBase import Techno
import Config
from includes import *

db = Techno(Config.host, Config.user, Config.password, Config.db_name)
db.connect()



with open("raw_data.txt") as file:
	for line in file:
		ind = line.rstrip().rfind("МКЦБ.")
		if ind >= 0:
			mkcb = line.rstrip()[ind:]
			name = line[:ind-1].rstrip()
			tp = ""
			lk = name.find('"')
			rk = name.rfind('"')
			if lk >= 0 and rk >= 0:
				tp = name[lk:rk]
				if "Кедр" in tp:
					tp = tp[5:]
			tp = tp.rstrip()
			db.add_decimal(mkcb, name, tp
			print(yellow_text(f"{mkcb}:{tp}({name})"))
		else:
			print(red_text(line.rstrip()))