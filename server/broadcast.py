class Broadcast():
    """Responsible for broadcasting to all clients
    
    Attributes:
        clients - the list of currently connected clients
    """
    __instance = None
    @staticmethod
    def get():
        """Get singleton instance of broadcast"""
        if Broadcast.__instance==None:
            Broadcast.__instance = Broadcast()
        return Broadcast.__instance
    def __init__(self):
        self.clients = []
    def addClient(self, client):
        """Add a new client to the list

        Params:
            client - the new client to add
        """
        self.clients.append(client)
    def removeClient(self, client):
        """Remove an inactive client from the internal list

        Params:
            client - the client to remove
        """
        self.clients.remove(client)
    def broadcast(self, data):
        """Broadcast a message to all clients

        Params:
            data - the message to broadcast
        """
        for curClient in self.clients:
            try:
                curClient.respond(data)
            except:
                self.removeClient(curClient)
    def mapClients(self, func):
        """Return a function mapped to all clients

        Params:
            func - function to send to all clients

        Returns:
            The array after mapping, with None if mapping crashed
        """
        def wrappedFunc(client):
            try:
                return func(client)
            except:
                return None
        return map(wrappedFunc, self.clients)
