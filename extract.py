import json
import urllib

url = "https://api.edamam.com/api/food-database/parser?ingr=pizza&app_id=cb658345&app_key=ee43c360ddb92157822faae9ca31c89b&page="

for i in range(0,20):
        f = urllib.urlopen(url + str(i))
        values = json.load(f)
        f.close()
        for criteria in values['hints']:
            for key, value in criteria.iteritems():
                try:
                    if key == "food":
                        print (value["label"] + ":" + value["brand"] + ":" + value["source"]) + ":",
                        for strr in value["foodContentsLabel"].split(";"):
                            print(strr + ":"),
                        print()
                except:
                    pass
    #for key, value in criteria.iteritems():
     #   print (key, 'is:', value)
   # print ('')
