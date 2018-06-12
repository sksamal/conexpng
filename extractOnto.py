import json

f = open('pizza.json', 'r')
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
