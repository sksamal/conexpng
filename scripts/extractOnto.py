import json

f = open('pizza.json', 'r')
values = json.load(f)
f.close()
#for criteria in values['header']:
#    print(criteria)
#for obj in values['class']:
#    print(obj['id'] + "=>" + obj['type'])
for cattr in values['classAttribute']:
    try:
        print("id=>" + cattr['id']) 
        print("label=>" + cattr['label']['IRI-based'])
        if 'prefLabel' in cattr['annotations'] :
            for obj1 in cattr['annotations']['prefLabel']:
                print(" " + obj1['identifier'] + "=>" + obj1['value'])
        else :
            print(" prefLabel=>")

        labels=""
        if 'altLabel' in cattr['annotations'] :
            for obj1 in cattr['annotations']['altLabel']:
                labels= labels + obj1['value'] + ";"
        print(" altLabel=>"+labels)

        if 'definition' in cattr['annotations'] :
            for obj1 in cattr['annotations']['definition']:
                print(" " + obj1['identifier'] + "=>" + obj1['value'])
        else :
            print(" definition=>")
        if 'intersection' in cattr :
            for obj1 in cattr['intersection']:
                print(" " + "intersection=>"+obj1)
        else :
            print(" intersection=>")
        if 'attributes' in cattr :
            for obj1 in cattr['attributes']:
                print(" " + "attr=>",obj1)
        else :
            print(" attributes=>")
        if 'superClasses' in cattr :
            for obj1 in cattr['superClasses']: 
                print(" " + "superId=>"+obj1)
        else :
            print(" superId=>")
    except:
            pass
exit()
'''    for key, value in criteria.iteritems():
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
   # print ('') '''
