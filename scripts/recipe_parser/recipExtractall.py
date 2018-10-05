import json
import glob
import sys
import csv

def stripNonASCII(s):
    return "".join([c for c in s if ord(c) < 129])

recipes = glob.glob('with_ingredients/*.json')

# Files to write the labels/ings to
allings = open("allings.txt", 'w') 
alllbls = open("alllbls.txt", 'w') 

for filename in recipes:
    with open(filename, 'r') as json_data:
        data = json.load(json_data)
    for criteria in data['hits']:
        label=criteria['recipe']['label']
        label=stripNonASCII(label)
        image=criteria['recipe']['image']
        source=criteria['recipe']['source']
        for value in criteria['recipe']['ingredients']:
            allings.write(value['coreIngred'] + "\n")
        label=label.replace(",","-");   
        alllbls.write(label + "\n")

alllbls.close()
allings.close()
print ("Ingredients extracted to allings.txt")
print ("Labels extracted to alllbls.txt")
