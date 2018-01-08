#!/usr/bin/env python

import time
import sys
import socket

import xmlrpclib
from SimpleXMLRPCServer import SimpleXMLRPCServer

title = ""

def set_title(new_title):
	global title
	title = new_title
	return title

def get_title():
	tmp = ""
	if str(title):
		tmp = title
	else:
		tmp = "No title set"
	return tmp + " (Python)"

def get_message(name):
	if str(name):
		return "Hello " + str(name) + ", welcome to PolyScope!"
	else:
		return "No name set"

def connect_TCP(ip):
	s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	ret = s.connect((ip, 502))
	data = bytearray([0x00, 0x00, 0x00, 0x00,0x00,0x06,0x01, 0x06, 0x00, 0x00,0x00,0x12])
	s.send(data)
	data = s.recv(1024)
	s.close()
	return ip

sys.stdout.write("MyDaemon daemon started")
sys.stderr.write("MyDaemon daemon started")

server = SimpleXMLRPCServer(("127.0.0.1", 40404))
server.register_function(set_title, "set_title")
server.register_function(get_title, "get_title")
server.register_function(get_message, "get_message")
server.register_function(connect_TCP, "connect_TCP")
server.serve_forever()

