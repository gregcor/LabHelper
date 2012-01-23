from twisted.internet import reactor, protocol
from twisted.protocols.basic import LineReceiver
from broadcast import Broadcast
from dispatcher import Dispatcher
from settings import Settings
import json

class CommandServer(protocol.Protocol):
    def connectionMade(self):
        self.session = {}
        self.session["host"] = self.transport.getHost().host
        self.buffer = ""
        Broadcast.get().addClient(self)

    def dataReceived(self, data):
        self.buffer+=data
        if "\r\n" in self.buffer:
            splitData = self.buffer.split("\r\n",1)
            self.buffer = splitData[1]
            self.lineReceived(splitData[0])

    def lineReceived(self, data):
        try:
            reqData = json.loads(data)
        except:
            return
        if "request" in reqData:
            Dispatcher.get().dispatchCommand(self, reqData)
    def respond(self, data):
        print("responding %s" %(data,))
        if data == None:
            return
        self.transport.write(data + "\r\n")
    def connectionLost(self, reason):
        print "lost client! %s"%(reason,)
        Broadcast.get().removeClient(self)

class Response():
    def __init__(self, data):
        return data + "\r\n"

def main():
    factory = protocol.ServerFactory()
    factory.protocol = CommandServer
    reactor.listenTCP(int(Settings.port), factory)
    reactor.run()
    
if __name__=="__main__":
    main()
