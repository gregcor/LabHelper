from userinfo import UserInfo
from settings import Settings
from broadcast import Broadcast
import os.path
import os
import base64
import json

class ClientAction():
    def __init__(self, action, args):
        self.data = args
        args["action"] = action
    def __str__(self):
        return json.dumps(self.data)

#decorator
def requireAdmin(func):
    def newFunc(session, request):
        if "admin" in session and session["admin"] == True:
            return func(session, request)
        else:
            return str(ClientAction("autherror",{}))
    return newFunc


def adminLogin(session, request):
    if "password" in request:
        if request["password"] == Settings.adminpassword:
            session["admin"] = True
@requireAdmin
def listClients(session, request):
    def getClientInfo(client):
        output = ""
        if "user" in client.session:
            output += client.session["user"].getInfoString()
        else:
            output += "(Not logged in)"
        if "host" in client.session:
            output += "\t" + client.session["host"]
        return output
    clientData = Broadcast.get().mapClients(getClientInfo)
    toWrite = "\n".join(sorted(clientData))
    f = open(Settings.rosterfile,'w')
    f.write(toWrite)
    f.close()
    return str(ClientAction("rosterwritten",{"file":Settings.rosterfile}))
    pass
#####
def login(session, request):
    session["user"] = UserInfo(request["fname"], request["lname"], request["uname"])
    return str(ClientAction("loggedin",{"fname":request["fname"],"lname":request["lname"],"uname":request["uname"], "activity":Settings.activity }))
    
######
def putFile(session, request):
    fileData = request["data"]
    dirName = os.path.join(Settings.path, 
                         Settings.activity)
    try:
        os.makedirs(dirName)
    except OSError:
        pass
    fname = os.path.join(dirName,
                         session["user"].getSafeInfoString() + ".zip")
    b64decoded = base64.decodestring(fileData)
    f = open(fname, 'wb')
    f.write(b64decoded)
    f.close()
    return str(ClientAction("gotfile",{}))

######
def getActivity(session, request):
    f = open(Settings.activityfile,'rb')
    binData = f.read()
    toSend = base64.encodestring(binData).replace("\r","").replace("\n","")
    return str(ClientAction("putfile",{"data":toSend}))
#####
@requireAdmin
def broadcast(session, request):
    Broadcast.get().broadcast(json.dumps(request["data"]))
    return str(ClientAction("broadcasted",{}))
