import json

f = open('pizza.json', 'r')
values = json.load(f)
f.close()
#for criteria in values['header']:
#    print(criteria)
#for obj in values['class']:
#    print(obj['id'] + "=>" + obj['type'])
print("id$label$prefLabel$atlLabel$definition$intersection$attributes$subId$superId")
for cattr in values['classAttribute']:
    try:
        id = cattr['id']
        label = cattr['label']['IRI-based']
#        print("id=>" + cattr['id'] + "," + cattr['label']['IRI-based']) 

        plabels=""
        if 'prefLabel' in cattr['annotations'] :
            for obj1 in cattr['annotations']['prefLabel']:
                plabels=plabels + obj1['value'] + ";"
#        print(" prefLabel=>")

        labels=""
        if 'altLabel' in cattr['annotations'] :
            for obj1 in cattr['annotations']['altLabel']:
                labels= labels + obj1['value'] + ";"
#        print(" altLabel=>"+labels)

        defs=""
        if 'definition' in cattr['annotations'] :
            for obj1 in cattr['annotations']['definition']:
                defs= defs + obj1['value'] + ";"
 #       print(" definition=>"+defs)

        ints=""
        if 'intersection' in cattr :
            for obj1 in cattr['intersection']:
                ints= ints + obj1 + ";"
  #      print(" intersection=>"+ints)

        attrs=""
        if 'attributes' in cattr :
            for obj1 in cattr['attributes']:
                attrs= attrs + obj1 + ";"
   #     print(" attributes=>" + attrs)

        subs=""
        if 'subClasses' in cattr :
            for obj1 in cattr['subClasses']: 
                subs= subs + obj1 + ";"

        supers=""
        if 'superClasses' in cattr :
            for obj1 in cattr['superClasses']: 
                supers= supers + obj1 + ";"
   #     print(" superId=>" + supers)
        print(id+'$'+label+'$'+plabels+'$'+labels+'$'+defs+'$'+ints+'$'+attrs+'$'+subs+'$'+supers)
    
    except:
            pass
