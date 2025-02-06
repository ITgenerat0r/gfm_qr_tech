import telebot
from telebot import types
import getpass
import sys


from includes import *
from thread import Threads
from MDataBase import Techno # disabled db
from security import Security



# admins
admins = []

version = "1.0"


DB_timeout = 2147483
max_lives = 5000
max_delay_between_errors = 60
delay_between_errors = 1

token = "7626405899:AAHGQaU0TgFyVSJ6rrNIyL2EeFNJ9egFF_w"
DB_HOST = "127.0.0.1"
DB_USER = ""
DB_PASS = ""
DB_NAME = "tech_db"

prod = True


start_time = datetime.datetime.now()
last_err_time = start_time

last_arg = ""
for i in sys.argv:
    if i[0] == '-':
        last_arg = i
        if i == "-log":
            print("Log enabled")
            logs = True
        elif i == "-help":
            print(f"Version {VERSION}")
            print(help_data)
            sys.exit()
    else:
        if last_arg == "-u":
            pwd = getpass.getpass(f"Password for user {i}: ")
            users[i] = pwd
        elif last_arg == "-f":
            filename = i
        elif last_arg == "-ip":
            HOST = i
        elif last_arg == "-port":
            PORT = i
        elif last_arg == "-name":
            station_name = i
        elif last_arg == "-token":
            token = i
        elif last_arg == "-dbhost":
            DB_HOST = i
        elif last_arg == "-dbuser":
            DB_USER = i
        elif last_arg == "-dbpass":
            DB_PASS = i
        elif last_arg == "-dbname":
            DB_NAME = i
        elif last_arg == "-admin" or last_arg == "-admins":
            admins.append(i)


# checking params
if not DB_USER:
    DB_USER = input("Enter user login for database: ")
if not DB_PASS:
    DB_PASS = getpass.getpass(f"Password for {DB_USER}: ")
if not token:
    token = input("Enter token:")

if not admins:
    print("Warning: There is no admin!")
    admins.append(int(input("Enter your id: ")))


print('bot init...')
bot = telebot.TeleBot(token)
print('threads init...')
thr = Threads()

db = Techno(DB_HOST, DB_USER, DB_PASS, DB_NAME) # disabled db

update_state = True

black_list = []
is_sending = []
client_file_id = None

forms = {}
form_keys = {}
form_func = {}
numbers = {}
buttons = {}
group_requests={}

last_err = ""

live_countdown = max_lives

def start_bot():
    try:
        global last_err_time
        print(yellow_text(get_time()), "Starting...")
        last_err_time = datetime.datetime.now()
        global db # disabled db
        global DB_timeout
        db.connect()
        db.set_time_out(DB_timeout) # disabled db
        print(yellow_text(get_time()), "Runned.")
        bot.polling(none_stop=True, timeout=100)
        # bot.infinity_polling(timeout=10, long_polling_timeout = 5)
    except Exception as e:
        global last_err
        global delay_between_errors
        global max_delay_between_errors
        print(yellow_text(get_time()), "Exception raised.")
        print(e)
        if str(e).find(last_err) > -1:
            if delay_between_errors < max_delay_between_errors:
                delay_between_errors += 1
        else:
            last_err = str(e)[:int(len(str(e))/3)]
            delay_between_errors = 1


@bot.message_handler(commands=['drop', 'stop'])
def drop_bot(message):

    if message.from_user.id in admins:
        if message.text.lower() in ['yes', 'y'] or not prod:
            print(yellow_text(get_time()), f"Bot has dropped by {message.from_user.id}({green_text(str(message.from_user.username))})")
            live_countdown = 0
            bot.send_message(message.chat.id, "Bot has ruined!")
            bot.stop_polling()
            bot.stop_bot()
            os._exit(0)
        elif message.text.lower() in ['n', 'no']:
            bot.send_message(message.chat.id, 'ok')
        else:
            bot.send_message(message.chat.id, "Are you sure?")
            bot.register_next_step_handler(message, drop_bot)

@bot.message_handler(commands=['reborn'])
def reborn(message):

    print(yellow_text(get_time()), f"reborn {message.from_user.id} ({green_text(str(message.from_user.username))})")
    if message.from_user.id in admins:
        reset_live_countdown()
        bot.send_message(message.chat.id, "Done!")


def reset_live_countdown():
    global live_countdown
    global max_lives
    global is_sending
    # bot = telebot.TeleBot(token)
    # thr = Threads()
    with thr.rlock():
        is_sending = []
        live_countdown = max_lives

@bot.message_handler(commands=['status'])
def get_drop_status(message):
    print(yellow_text(get_time()), f"STATUS {message.from_user.id} ({green_text(str(message.from_user.username))})")
    if message.from_user.id in admins:
        text = f"live_countdown: <{live_countdown}>"
        print(text)
        bot.send_message(message.chat.id, text)



def parse_date_value(raw_data):
    bindex = raw_data.find('(')
    if bindex >= 0:
        eindex = raw_data[bindex:].find(')')
        if eindex >= 0:
            return raw_data[bindex+1:bindex+eindex]
    return ""

def is_number(text):
    alphabet = set({'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'})
    for c in text:
        if not c in alphabet:
            return False
    return True

@bot.message_handler(commands=['send'])
def send_message_to_user(message):
    if message.from_user.id in admins:
        print(message.text)
        mdata = message.text.split()
        # for i in mdata:
        #     print(f"- {i}")
        if len(mdata) >= 3:
            user_id = mdata[1]
            beginid = message.text.find(mdata[2])
            # print(beginid)
            # print(message.text[beginid:])
            try:
                bot.send_message(user_id, message.text[beginid:])
            except Exception as e:
                print(e)




class Field():
    def __init__(self, key, hint="", single_options=[], multi_options=[], limit=1, value="", hidden=False):
        self.key = key
        self.hint = hint
        self.value = value
        self.single_options = single_options
        self.multi_options = multi_options
        self.options_limit = limit
        self.is_default = True
        self.hidden = hidden

    def __str__(self):
        return f"{self.key}: {self.value}"



def new_bind_form(id):
    print("new_bind_form()")
    bind_form = []
    bind_form.append(Field('login', 'Логин:'))
    bind_form.append(Field('pass', 'Пароль:', hidden=True))
    forms[id] = bind_form[:]


def new_reg_form(id):
    print("new_reg_form()")
    reg_form = []
    reg_form.append(Field('name', "Введите ФИО"))
    reg_form.append(Field('login', "Придумайте логин:"))
    reg_form.append(Field('pass', "Придумайте пароль:", hidden=True))
    reg_form.append(Field('group', "Выберите роль для добавления в группу:", single_options=['Администратор', 'Сотрудник', 'Зритель'])) # for DB change to int. (value in mm)
    forms[id] = reg_form[:]


def new_operations_form(id, m_options=[]):
    print("new_form()")
    # m_options = 
    device_form = []
    device_form.append(Field('number', 'Заводской номер устройства:'))
    device_form.append(Field('action', 'Выберите операции', multi_options=m_options, limit=32))
    device_form.append(Field('state', 'Применить?', single_options=['Применить', 'Отмена']))
    forms[id] = device_form[:]


def form_to_map(form):
    r = {}
    for i in form:
        r[i.key] = i
    return r


def print_form(form):
    print("{")
    for i in form:
        print(f" {i}")
    print("}")

# empty_form = {'number': "", 'address': "", 'size': 0, 'noise_insulation':"", 'surfase':"", 'mount':"", 'new_build': False, 'elevators':""}

# @bot.message_handler(commands=['new_order'])
def new_device(message):
    print("new_device()")
    global db
    worker = db.get_worker_by_tg(int(message.chat.id))
    if worker:
        ops = db.get_operation_for_device(message.text)
        # show data
        data = ""
        for op in ops:
            if len(data):
                data += f"\n"
            row = f"{op['operation']}: {op['worker']} ({op['dt']})"
            data += f"{row}"
            # bot.send_message(message.chat.id, f"{i}")
        if len(data):
            bot.send_message(message.chat.id, data)
        else:
            bot.send_message(message.chat.id, "Нет операций по этому номеру!")
        nops = db.get_operations_to_worker(worker['id'])
        if nops:
            new_operations_form(message.chat.id, nops)
            form_func[message.chat.id] = done_form
            form_keys[message.chat.id] = 'number'
            take_form(message, is_first=True)

def done_form(message, form):
    print("done_form()")
    dt = form_to_map(form)
    ops = dt['action'].value.split(',')
    if dt['state'].value == "Применить":
        global db 
        worker = db.get_worker_by_tg(message.chat.id)
        if worker:
            for op in ops:
                op = op.strip()
                print(f"+{op}")
                db.add_operation(dt['number'].value, worker['w_login'], op)
            bot.send_message(message.chat.id, f"Готово!")
        else:
            bot.send_message(message.chat.id, f"Вы не авторизованы!")
            unbind(message)
    else:
        bot.send_message(message.chat.id, f"Действие отменено!")
    bot.send_message(message.chat.id, f"Введите номер устройства:")


@bot.message_handler(commands=['registration'])
def new_reg(message):
    print("new_reg()")
    global db
    worker = db.get_worker_by_tg(message.chat.id)
    if not worker:
        new_reg_form(message.chat.id)
        form_func[message.chat.id] = done_reg
        take_form(message, is_first=True)
    else:
        bot.send_message(message.chat.id, f"Ваш аккаунт уже привязан к учетной записи. Используйте /unbind что бы отвязать.")

def done_reg(message, form):
    global db
    print("done_reg()")
    dt = form_to_map(form)
    sc = Security()
    db.add_worker(dt['login'].value, sc.sha256(dt['pass'].value), dt['name'].value, message.chat.id)
    bot.send_message(message.chat.id, f"Учетная запись создана! \nПолномочия группы будут доступны после подтверждения администратором.")
    
    worker = db.get_worker_by_tg(message.chat.id)
    groups = db.get_list_groups()
    lnk = ""
    if message.chat.username:
        lnk = f"@{message.chat.username}"

    title = f"{message.chat.id}|{message.chat.first_name} {message.chat.last_name} ({lnk}) \n"
    data = ""
    for fld in form:
        data += f"\n{fld.hint} \n{fld.value}\n"
    note = f"\nworker id: {worker['id']}"
    for group in groups:
        note += f"\n{group['id']}: {group['g_name']} (access:{group['access']})"
    note += f"\n Use /add <user_id> <group_id>"
    for admin in admins:
        bot.send_message(admin, title+data+note)

@bot.message_handler(commands=['add'])
def add_user_group(message):
    if message.chat.id in admins:
        dt = message.text.split(' ')
        if len(dt) > 2:
            res = db.add_user_to_group(dt[1], dt[2])
            if res == 1:
                bot.send_message(message.chat.id, f"Пользователь добавлен в группу.")
            else:
                bot.send_message(message.chat.id, f"Пользователь уже есть в этой группе.")
            # bot.send_message(message.chat.id, f"Result: {res}")
            return
        bot.send_message(message.chat.id, "Недостаточно параметров!")
    else:
        bot.send_message(message.chat.id, f"Требуются права администратора!")



@bot.message_handler(commands=['bind'])
def new_bind(message):
    print("new_bind()")
    global db
    worker = db.get_worker_by_tg(message.chat.id)
    if not worker:
        new_bind_form(message.chat.id)
        form_func[message.chat.id] = done_bind
        take_form(message, is_first=True)
    else:
        bot.send_message(message.chat.id, f"Ваш аккаунт уже привязан к учетной записи. Используйте /unbind что бы отвязать.")

def done_bind(message, form):
    global db
    print("done_bind()")
    dt = form_to_map(form)
    sc = Security()
    if db.login(dt['login'].value, sc.sha256(dt['pass'].value)):
        worker = db.get_worker(dt['login'].value)
        db.set_worker_tg_id(worker['id'], int(message.chat.id))
        print("Success!")
        bot.send_message(message.chat.id, "Успешно!")
        bot.send_message(message.chat.id, f"Введите номер устройства:")
    else:
        print("Access denied!")
        bot.send_message(message.chat.id, "Неверный логин или пароль!")


@bot.message_handler(commands=['unbind'])
def unbind(message):
    print('unbind()')
    global db
    worker = db.get_worker_by_tg(message.chat.id)
    if worker:
        db.set_worker_tg_id(worker['id'])
        bot.send_message(message.chat.id, "Успешно!")
    else:
        bot.send_message(message.chat.id, "Вы не зарегистрированы!")
    bot.send_message(message.chat.id, f"Для регистрации используйте команду /registration.\nЕсли есть аккаунт, вы можете привязать /bind.")




def take_form(message, field="", dt="", is_first=False):
    print("take_form()")
    try:
        key_handler = True
        if not message.chat.id in form_keys:
            form_keys[message.chat.id] = ""
        field = form_keys[message.chat.id]
        print(f"field: {field}")
        if not dt:
            dt = message.text
        else:
            key_handler = False
        if not message.chat.id in forms:
            return
        form = forms[message.chat.id]
        if dt[0] == '/':
            if dt.lower().find('cancel') >= 0:
                # new_form(message.chat.id)
                del forms[message.chat.id]
                bot.send_message(message.chat.id, "Заявка отменена!")
                return
            if not is_first:
                common(message)
                return
        else:
            if field:
                for i in form:
                    if i.key == field:
                        if dt in i.multi_options:
                            if i.options_limit > 0:
                                print("MULTI options")
                        else:
                            i.value = dt
                            if i.hidden:
                                bot.delete_message(message.chat.id, message.message_id)
                        i.is_default = False
        done = True
        for fld in form:
            print(fld.key)
            if fld.is_default:
                # if field:
                #     print(f"save field[{field}]...")
                #     for i in form:
                #         if i.key == field:
                #             print("saved")
                #             i.value = dt
                #             i.is_default = False
                #             field = ""
                #             break
                #     continue
                l_ops = fld.multi_options.copy()
                print("fld.multi_options: ", fld.multi_options)
                if fld.is_default:
                    print("fld.single_options: ", fld.single_options)
                    l_ops += fld.single_options.copy()
                print("l_ops:", l_ops)
                ops = inline_btns(l_ops, fld.key)
                print_form(forms[message.chat.id])
                print("waiting")
                bot.send_message(message.chat.id, fld.hint, reply_markup=ops)
                form_keys[message.chat.id] = fld.key
                if key_handler:
                    bot.register_next_step_handler(message, take_form, fld.key)
                done = False
                return
            # field = fld.key
            # print(f"field: {field}")
        res_func = form_func[message.chat.id]
        print(f"done: {done}")
        print(f"func: {res_func}")
        if done:
            if res_func:
                res_func(message, form)
                del form_func[message.chat.id]
            else:
                data = ""
                for fld in form:
                    data += f"\n{fld.hint} \n{fld.value}\n"
                send_to_admins(message, data)
            del form_keys[message.chat.id]
            del forms[message.chat.id]
    except Exception as e:
        print(red_text(f"Error in take_form(): {e}"))
        common(message)






def send_to_admins(message, data):
    print("send_to_admins()")
    lnk = ""
    if message.chat.username:
        lnk = f"@{message.chat.username}"

    title = f"{message.chat.id}|{message.chat.first_name} {message.chat.last_name} ({lnk}) \n"
    for admin in admins:
        bot.send_message(admin, title+data)
    del forms[message.chat.id]
    bot.send_message(message.chat.id, f"Заявка отправлена администратору!")








# @bot.callback_query_handler(func=lambda call: call.data == 'form')
# def save_btn(call):
#     print("save_btn()")
#     message = call.message
#     data = call.data
#     print(message, "/")
#     print(data)
#     chat_id = message.chat.id
#     bot.send_message(chat_id, f'Данные сохранены')



@bot.callback_query_handler(func=lambda callback: True)
def callback_message(callback):
    print("\ncallback()")
    try:
        print(yellow_text(get_time()), f"{callback.message.chat.id}({str(callback.message.from_user.username)}): '{callback.message.text}'")
        print(blue_text(callback.data))
        data = callback.data.split('|')
        if data[0] == "ops":
            if len(data) >= 2:
                number = numbers[callback.message.chat.id]
                operation = data[1]
                print(f"Pressed '{operation}' for {number}")
                ops = db.get_operation_for_device(number)
                worker = db.get_worker_by_tg(callback.message.chat.id)
                groups = db.get_worker_groups(worker['w_login'])
                access_level = 0
                for group in groups:
                    if group['access'] > access_level:
                        access_level = group['access']
                print(f"access_level: {access_level}")
                print(blue_text(f"ops: {ops}"))
                if access_level:
                    is_exist = False
                    for op in ops:
                        if op['operation'] == operation and op['worker'] == worker['w_login']:
                            is_exist = True
                            db.delete_operation_by_id(op['id'])
                    if not is_exist:
                        db.add_operation(number, worker['w_login'], operation)
                    # update ops
                    ops = db.get_operation_for_device(number)
                    sdata = ""
                    for op in ops:
                        if len(sdata):
                            sdata += f"\n"
                        row = f"{op['operation']:15}: {op['worker']:<15} ({op['dt']})"
                        sdata += f"{row}"
                        # bot.send_message(message.chat.id, f"{i}")
                    if not len(sdata):
                        sdata = "Нет операций по этому номеру!"
                    nops = db.get_operations_to_worker(worker['id'])
                    rm = ''
                    if nops:
                        rm = inline_btns(nops, "ops")
                    bot.edit_message_text(chat_id=callback.message.chat.id, message_id=buttons[callback.message.chat.id], text=sdata, reply_markup=rm)
                
                    # bot.edit_message_reply_markup(callback.message.chat.id, message_id=buttons[callback.message.chat.id], reply_markup=rm)
        else:
            # if callback.message:
            #     print_callback(callback.message)
                # for row in callback.message.json['reply_markup']['inline_keyboard']:
                #     print(blue_text(f"{row}"))
                    # if callback.data==row[0]['callback_data']:
                    #     for i in row:
                    #         print("-", i['text'])
            print(f'Текст на нажатой кнопке: {callback.data}')
            dt = callback.data.split('|')
            print(blue_text(f"dt: {dt}"))
            if len(dt) > 1:
                # bot.edit_message_reply_markup(callback.message.chat.id, message_id=callback.message.message_id, reply_markup='')
                # bot.edit_message_text(chat_id=callback.message.chat.id, message_id=callback.message.message_id, text=f"{callback.message.text}\n{row[0]['text']}")
                
                form = forms[callback.message.chat.id]
                for i in form:
                    if i.key == dt[0]:
                        print("Not default!")
                        if len(dt) > 1 and dt[1] in i.multi_options:
                            print(blue_text("in multi_options"))
                            print(i.multi_options)
                            print(i.single_options)
                            if i.options_limit > 0:
                                print(blue_text('limit > 0'))
                                i.multi_options.remove(dt[1])
                                print("deleted")
                                print(i.multi_options)

                                if len(i.value):
                                    i.value += f", "
                                i.value += f"{dt[1]}"
                                i.options_limit -= 1
                                rm = inline_btns(i.multi_options, i.key)
                                print(blue_text(f"{rm}"))
                                bot.edit_message_text(chat_id=callback.message.chat.id, message_id=callback.message.message_id, text=f"{i.hint}\n{i.value}")
                                if i.options_limit < 1:
                                    bot.edit_message_reply_markup(callbac.message.chat.id, message_id=callback.message.message_id, reply_markup='')
                                else:
                                    bot.edit_message_reply_markup(callback.message.chat.id, message_id=callback.message.message_id, reply_markup=rm)

                                print("limit", i.options_limit)

                                if i.is_default:
                                    i.is_default = False
                                    print(blue_text("is default"))
                                    take_form(callback.message, dt[0], dt[1])
                                else:
                                    print(blue_text("not default"))
                                return
                            else:
                                return
                        # else:
                        #     # i.value = row[0]["text"]
                        #     bot.edit_message_reply_markup(callback.message.chat.id, message_id=callback.message.message_id, reply_markup='')
                        #     bot.edit_message_text(chat_id=callback.message.chat.id, message_id=callback.message.message_id, text=f"{callback.message.text}\n{row[0]['text']}")    
                    # else:
                print("empty reply_markup")
                # bot.edit_message_reply_markup(callback.message.chat.id, message_id=callback.message.message_id, reply_markup='')
                bot.edit_message_text(chat_id=callback.message.chat.id, message_id=callback.message.message_id, text=f"{callback.message.text}\n{dt[1]}", reply_markup='')    
                take_form(callback.message, dt[0], dt[1])
    except Exception as e:
        print(red_text(f"Error in callback: {e}"))
                

def print_callback(dt):
    # print(yellow_text(f"{dt}"))
    for k in dt.reply_markup.keyboard:
        for i in k:
            print(yellow_text(f"{i}"))




@bot.message_handler(commands=['start'])
def start(message):
    bot.send_message(message.chat.id, f"Здравствуйте, {message.from_user.first_name} {message.from_user.last_name}!")
    common(message)



@bot.message_handler(content_types='text')
def common(message):
    global db
    if message.text == None:
        print(red_text("message.text == None"))
        return
    if len(message.text) > 100:
        bot.send_message(message.chat.id, "Слишком длинное сообщение!")
        return
    
    text = f"{message.text}"
    worker = db.get_worker_by_tg(int(message.chat.id))
    if worker:
        if text.isnumeric():
            if len(text) == 8:
                # new_device(message)
                numbers[message.chat.id] = int(message.text)
                if message.chat.id in buttons:
                    # bot.delete_message(message.chat.id, buttons[message.chat.id])
                    bot.edit_message_reply_markup(message.chat.id, message_id=buttons[message.chat.id], reply_markup='')
                # show here
                ops = db.get_operation_for_device(message.text)
                # show data
                data = ""
                for op in ops:
                    if len(data):
                        data += f"\n"
                    row = f"{op['operation']:15}: {op['worker']:<15} ({op['dt']})"
                    data += f"{row}"
                    # bot.send_message(message.chat.id, f"{i}")
                if len(data):
                    bot.send_message(message.chat.id, data)
                else:
                    bot.send_message(message.chat.id, "Нет операций по этому номеру!")
                nops = db.get_operations_to_worker(worker['id'])
                if nops:
                    rm = inline_btns(nops, "ops")
                    bot.edit_message_reply_markup(message.chat.id, message_id=message.message_id+1, reply_markup=rm)
                buttons[message.chat.id] = message.message_id+1
            else:
                bot.send_message(message.chat.id, f"Длина числа должна быть 8 цифр!")
        else:
            bot.send_message(message.chat.id, f"Введите число длиной в 8 цифр!")
    else:
        bot.send_message(message.chat.id, f"Вы не авторизованы.")
        bot.send_message(message.chat.id, f"Для регистрации используйте команду /registration.\nЕсли есть аккаунт, вы можете привязать /bind.")
    


while True:
    print()
    print(f"<<<{red_text(str(live_countdown))}>>>")
    start_bot()
    if live_countdown < 1:
        break
    print(f"Sleep {delay_between_errors}s")
    sleep(delay_between_errors)
    live_countdown -= 1

# print(yellow_text(get_time()), "END")



