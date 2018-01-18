#!/usr/bin/env python

import time
import sys
import socket
import types

import xmlrpclib
from SimpleXMLRPCServer import SimpleXMLRPCServer
from ModbusClient import *

modbusClient = None
connected = False
def connect_TCP(ip):
	global connected, modbusClient
	modbusClient = ModbusClient(ip, 502)
	modbusClient.Connect()
	connected = True
	return connected

def send_Command(value):
	global connected, modbusClient
	ret = False
	if(connected):
		vals = value.split(",")
		ret = modbusClient.WriteSingleRegister(int(vals[0]), int(vals[1]))
	return ret

def get_IO(value):
	global connected, modbusClient
	retStr=""
	if(connected):
		vals = value.split(",")
		ret = modbusClient.ReadHoldingRegisters(int(vals[0]), int(vals[1]))
		for i in range(0, len(ret)):
                	ret[i] = str(ret[i])
		if(len(ret) > 0):
			retStr = ','.join(ret)
	return retStr

#connect_TCP("10.89.34.9")
print get_IO("0,16")

sys.stderr.write("MyDaemon daemon started")

server = SimpleXMLRPCServer(("127.0.0.1", 40404))
server.register_function(connect_TCP, "connect_TCP")
server.register_function(send_Command, "send_Command")
server.register_function(get_IO, "get_IO")
server.serve_forever()

