import json
import glob
import sys
import csv

count = 0
with open('uniqings.txt', 'rU') as f:
    reader = csv.reader(f)
    masterIngreds = [] 
    attrs=""
    #split text file into array for matching
    for row in reader:
        masterIngreds.append(row[0])
        attrs=attrs+","+row[0]
        count = count + 1
    attrs=attrs+","+"class"
    print(attrs)

recipes = glob.glob('with_ingredients/*.json')
num=1
for filename in recipes:
    with open(filename, 'r') as json_data:
        data = json.load(json_data)
    for criteria in data['hits']:
        label=criteria['recipe']['label']
        image=criteria['recipe']['image']
        source=criteria['recipe']['source']
        ingredients=[0]*count
        for value in criteria['recipe']['ingredients']:
            i=0
            for ing in masterIngreds:
                if ing == value['coreIngred']:
                    ingredients[i]=1
                    break
                i=i+1
        num=num+1
        label = label.replace(",","-"); 
        ingCSV="obj" + str(num) + ","
        for ing in ingredients: 
            ingCSV=ingCSV+ str(ing) + ","
        ingCSV=ingCSV+ label
        print (ingCSV)
           # ingredients= ingredients + ";" + (value["coreIngred"] + ";" + str(value['weight']))
#        print(label + "$" + image + "$" + source + "$" + ingredients)
