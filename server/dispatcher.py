import clientfunctions
import sys
import traceback

class Dispatcher():
    __instance = None
    COMMANDS = {
        "adminlogin": clientfunctions.adminLogin,
        "listclients": clientfunctions.listClients,
        "login": clientfunctions.login,
        "putfile": clientfunctions.putFile,
        "getactivity": clientfunctions.getActivity,
        "broadcast": clientfunctions.broadcast
        }
    def __init__(self):
        
        pass
    @staticmethod
    def get():
        if Dispatcher.__instance==None:
            Dispatcher.__instance = Dispatcher()
        return Dispatcher.__instance

    def dispatchCommand(self, connection, request):
        print(request)
        try:
            connection.respond(Dispatcher.COMMANDS[request["request"]](connection.session, request))
        except:
            print("Bad request!")
            traceback.print_exc(file=sys.stdout)
