import json
import sys

#for i in range(1,20):
f = open(sys.argv[1],"r")
values = json.load(f)
f.close()

for criteria in values['hits']:
   label=criteria['recipe']['label']
   image=criteria['recipe']['image']
   source=criteria['recipe']['source']
   ingredients=""
   for value in criteria['recipe']['ingredients']:
       ingredients= ingredients + "$" + (value['text'] + "$" + str(value['weight']))
   print(label + "$" + image + "$" + source + "$" + ingredients)
