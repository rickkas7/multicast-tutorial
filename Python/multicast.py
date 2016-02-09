#!/usr/bin/python

import socket
import struct

# http://stackoverflow.com/questions/603852/multicast-in-python
MCAST_GRP = '239.1.1.234'
MCAST_PORT = 7234

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)

sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

sock.bind((MCAST_GRP, MCAST_PORT))
mreq = struct.pack("4sl", socket.inet_aton(MCAST_GRP), socket.INADDR_ANY)

sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

while True:
  data = sock.recv(16)
  print (ord(data[0]) << 8) | ord(data[1]);
