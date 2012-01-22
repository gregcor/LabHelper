import json
class SettingsLookup(type):
    def __getattr__(self, name):
        print name
        if name.startswith("_"):
            return None
        f = open('settings.json','r')
        text = f.read()
        f.close()
        settingsText = json.loads(text)
        return settingsText[name]    

class Settings():
    __metaclass__ = SettingsLookup
        
