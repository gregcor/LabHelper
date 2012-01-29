import json
class SettingsLookup(type):
    """Metclass to allow Settings.whatever to be called.
    Settings are read from disk every time."""
    def __getattr__(self, name):
        if name.startswith("_"):
            return None
        f = open('settings.json','r')
        text = f.read()
        f.close()
        settingsText = json.loads(text)
        return settingsText[name]    

class Settings():
    """Class that looks up settings. Call Settings.whatever"""
    __metaclass__ = SettingsLookup
        
