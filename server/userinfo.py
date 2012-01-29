import string
class UserInfo():
    """Container for user information"""
    def __init__(self, firstname, lastname, coursename):
        self.firstname = firstname
        self.lastname = lastname
        self.coursename = coursename
        self.safe_charset = " " + string.ascii_uppercase + string.ascii_lowercase + string.digits
    def getInfoString(self):
        """Return the user's lastname, firstname, and course username
        exactly as specified by the user"""
        return "%s, %s (%s)" % (self.lastname, self.firstname, self.coursename)
        pass
    def getSafeInfoString(self):
        """Return safe version of user string containing only spaces
        and alphanumeric characters"""
        return "".join([x for x in list(self.getInfoString()) if x in self.safe_charset])
