category: python2
date: 2016-03-06
title: PYTHON2 服务器(Socket/HTTP)
---

## Socket服务器
```python
import socket

sock=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
sock.bind(('localhost',8089))
sock.listen(5)

print('tcpServer listen at: %s:%s\n\r' %('localhost',8089))

while True:
    client_sock,client_addr=sock.accept()
    print('%s:%s connect' %client_addr)
    while True:
        recv=client_sock.recv(4096)
        if not recv:
            client_sock.close()
            break
        print('[Client %s:%s said]:%s' % (client_addr[0],client_addr[1],recv))
        client_sock.send('tcpServer has received your message')
        sock.close()
```

## HttpServer
```python
import BaseHTTPServer
import urlparse
class WebRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def do_GET(self):
        """
        """
        print "8090"

server = BaseHTTPServer.HTTPServer(('0.0.0.0',8090), WebRequestHandler)
server.serve_forever()
```

self 还有如下参数
* self.path
* self.client_address
* self.address_string()
* self.command
* self.path
* self.request_version
* self.server_version
* self.sys_version
* self.protocol_version
* self.headers
* self.send_response(200)
* self.end_headers()
