import string
class UserInfo():
    def __init__(self, firstname, lastname, coursename):
        self.firstname = firstname
        self.lastname = lastname
        self.coursename = coursename
        self.safe_charset = " " + string.ascii_uppercase + string.ascii_lowercase + string.digits
    def getInfoString(self):
        return "%s, %s (%s)" % (self.lastname, self.firstname, self.coursename)
        pass
    def getSafeInfoString(self):
        return "".join([x for x in list(self.getInfoString()) if x in self.safe_charset])
