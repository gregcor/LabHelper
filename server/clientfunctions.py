from userinfo import UserInfo
from settings import Settings
from broadcast import Broadcast
import os.path
import os
import base64
import json

class ClientAction():
    """Respose from the client.
    Used to provide an easy wrapper for a response
    with the action wrapped properly. Should be 
    stringified before being sent"""
    def __init__(self, action, args):
        self.data = args
        args["action"] = action
    def __str__(self):
        return json.dumps(self.data)

#decorator
def requireAdmin(func):
    """Decorator to restrict a function to admin
    authentication privilege"""
    def newFunc(session, request):
        if "admin" in session and session["admin"] == True:
            return func(session, request)
        else:
            return str(ClientAction("autherror",{}))
    return newFunc


def adminLogin(session, request):
    """Attempt to authenticate current session as admin"""
    if "password" in request:
        if request["password"] == Settings.adminpassword:
            session["admin"] = True
@requireAdmin
def listClients(session, request):
    """Dump list of clients to file specified in config"""
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
    """Attempt to authenticate the user based on request"""
    session["user"] = UserInfo(request["fname"], request["lname"], request["uname"])
    return str(ClientAction("loggedin",{"fname":request["fname"],"lname":request["lname"],"uname":request["uname"], "activity":Settings.activity }))
    
######
def putFile(session, request):
    """Store a file on the server, as sent by any client.
    Only works with ZIP files."""
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
    """Get the name of the current activity from config file"""
    f = open(Settings.activityfile,'rb')
    binData = f.read()
    toSend = base64.encodestring(binData).replace("\r","").replace("\n","")
    return str(ClientAction("putfile",{"data":toSend}))
#####
@requireAdmin
def broadcast(session, request):
    """Send a message to all currently connected clients"""
    Broadcast.get().broadcast(json.dumps(request["data"]))
    return str(ClientAction("broadcasted",{}))
