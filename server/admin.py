import socket
import getpass
import json

HOST="127.0.0.1"
PORT=8000

MENU = """1) Alert
2) URL
3) Lock
4) Unlock
5) Roster
"""

def main():
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((HOST,PORT))
    password = getpass.getpass()
    req = {"request":"adminlogin", "password":password}
    sock.sendall(json.dumps(req) + "\r\n")
    #retData = sock.recv(2048)
    while True:
        print(MENU)
        choice = raw_input("-->")
        try:
            choice = int(choice)
        except:
            print "Bad choice"
            continue
        req = None
        direct = None
        if choice==1:
            text = raw_input("Text: ")
            req = {"action":"alert","text":text}
        elif choice==2:
            text = raw_input("URL: ")
            req = {"action":"url","text":text}
        elif choice==3:
            req = {"action":"lock"}
        elif choice==4:
            req = {"action":"unlock"}
        elif choice==5:
            direct = {"request":"listclients"}
        toSend = None
        if req != None:
            toSend = {"request":"broadcast", "data":req}

        if direct != None:
            toSend = direct
        if toSend != None:
            sock.sendall(json.dumps(toSend) + "\r\n")
        retData = sock.recv(2048)
        print(retData)
        
if __name__=="__main__":
    main()
