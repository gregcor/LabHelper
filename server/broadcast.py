class Broadcast():
    __instance = None
    @staticmethod
    def get():
        if Broadcast.__instance==None:
            Broadcast.__instance = Broadcast()
        return Broadcast.__instance
    def __init__(self):
        self.clients = []
    def addClient(self, client):
        self.clients.append(client)
    def removeClient(self, client):
        self.clients.remove(client)
    def broadcast(self, data):
        for curClient in self.clients:
            try:
                curClient.respond(data)
            except:
                self.removeClient(curClient)
    def mapClients(self, func):
        def wrappedFunc(client):
            try:
                return func(client)
            except:
                return None
        return map(wrappedFunc, self.clients)
