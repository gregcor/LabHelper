from twisted.internet import reactor, protocol
from twisted.protocols.basic import LineReceiver
from broadcast import Broadcast
from dispatcher import Dispatcher
from settings import Settings
import json

class CommandServer(protocol.Protocol):
    """A twisted line server that reacts to input.
    We're not using LineReceiver because it has a buffer
    size restriction
    """
    def connectionMade(self):
        """Store hostname on connection made, add to broadcast list"""
        self.session = {}
        self.session["host"] = self.transport.getHost().host
        self.buffer = ""
        Broadcast.get().addClient(self)

    def dataReceived(self, data):
        """Add data received to the buffer and check for existence
        of a full CRLF terminated command"""
        self.buffer+=data
        if "\r\n" in self.buffer:
            splitData = self.buffer.split("\r\n",1)
            self.buffer = splitData[1]
            self.lineReceived(splitData[0])

    def lineReceived(self, data):
        """Called to dispatch a fully-delimited CRLF command"""
        try:
            reqData = json.loads(data)
        except:
            return
        if "request" in reqData:
            Dispatcher.get().dispatchCommand(self, reqData)
    def respond(self, data):
        """Send a response to the client with a CRLF termination"""
        print("responding %s" %(data,))
        if data == None:
            return
        self.transport.write(data + "\r\n")
    def connectionLost(self, reason):
        """Removes client from broadcast list once connection is closed"""
        print "lost client! %s"%(reason,)
        Broadcast.get().removeClient(self)

def main():
    """Starts twisted server"""
    factory = protocol.ServerFactory()
    factory.protocol = CommandServer
    reactor.listenTCP(int(Settings.port), factory)
    reactor.run()
    
if __name__=="__main__":
    main()
