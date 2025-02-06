import datetime
from time import sleep
import os
import colorama
from colorama import Fore, Back, Style
colorama.init()
# from getpass import getpass
# import subprocess
from sys import platform
import shutil
import requests
import json
from uuid import getnode as get_mac
import socket

def get_ip_address(url):
    try:
        ip_address = socket.gethostbyname(url)
        return ip_address
    except socket.error as err:
      pass
        # print(f"Error: {err}")


url = "google.com"
ip = get_ip_address(url)

mac = get_mac()


operating_system = 'unknown'

if platform == "linux" or platform == "linux2":
  operating_system = 'linux'
elif platform == "darwin":
  operating_system = 'x'
elif platform == "win32":
  operating_system = 'windows'







LNKINFO: str = "lnkinfo"
CODEPAGE: str = "windows-1251"
LOCAL_PATH: str = "Local path"
NETWORK_PATH: str = "Network path"





document_type = {".pdf", ".txt", ".bin", ".doc", ".docx", ".zip", ".rar", ".7z", ""}
image_type = {".img", ".png", ".bmp", ".jpg"}
video_type = {".mp4"}
audio_type = {".mp3"}




def get_time():
  return datetime.datetime.now().strftime("<%Y-%m-%d, %H:%M:%S.%f")[:-3]+">"


def red_text(text):
  return Fore.RED + text + Style.RESET_ALL

def blue_text(text):
  return Fore.BLUE + text + Style.RESET_ALL

def green_text(text):
  return Fore.GREEN + text + Style.RESET_ALL

def yellow_text(text):
  return Fore.YELLOW + text + Style.RESET_ALL






def sendFileByRequest(tk, chat_id, fname, flocation, fnewname='document.png'):
    fabsname = fname
    if flocation:
      fabsname = f"{flocation}/{fname}"
    document = open(fabsname, "rb")
    url = f"https://api.telegram.org/bot{tk}/sendDocument"
    response = requests.post(url, data={'chat_id': chat_id}, files={'document': (fnewname, document)})
    # part below, just to make human readable response for such noobies as I
    content = response.content.decode("utf8")
    js = json.loads(content)
    # print()
    # print(f"js: {js['result']['document']}")

    return js['result']['document']['file_id']



def check_symbol(symbol):
  if operating_system == 'windows':
    if ch > 127 and ch < 176:
      return ch + 912
    elif ch > 223 and ch < 240:
      return ch + 864
    elif ch == 240:
      return 1025
    elif ch == 241:
      return 1105
    return ch














# for telebot
from telebot import types



def inline_btns(list=[], callback_tag=""):
  markup = types.InlineKeyboardMarkup()
  i = 0
  while i < len(list):
    if len(list[i]) > 21:
      list[i] = list[i][:21]
    if not callback_tag:
      callback_tag = list[i]
    btn1 = types.InlineKeyboardButton(list[i], callback_data=f"{callback_tag}|{list[i]}")
    if i + 1 < len(list) and list[i + 1] != " ":
        btn2 = types.InlineKeyboardButton(list[i + 1], callback_data=f"{callback_tag}|{list[i+1]}")
        markup.row(btn1, btn2)
    else:
        markup.row(btn1)
    i += 2
    # markup.row(btn1)
    # i += 1
  return markup


def keyboard_btns(list=[]):
  markup = types.ReplyKeyboardMarkup()
  i = 0
  while i < len(list):
      btn1 = types.KeyboardButton(list[i])
      if i + 1 < len(list) and list[i + 1] != " ":
          btn2 = types.KeyboardButton(list[i + 1])
          markup.row(btn1, btn2)
      else:
          markup.row(btn1)
      i += 2
  return markup







kb = types.InlineKeyboardMarkup(row_width=1)
btn_types = types.InlineKeyboardButton(text='label1', callback_data='btn_types')
kb.add(btn_types)





def get_value_from_str(data, key):
  x = data.find(key)
  if x >= 0:
    dt = data[x:]
    b_ind = dt.find(':')
    if b_ind >= 0:
      e_ind = dt.find(',')
      if e_ind >= 0:
        return dt[b_ind+1:e_ind]
      else:
        return dt[b_ind+1:]
  return ''








