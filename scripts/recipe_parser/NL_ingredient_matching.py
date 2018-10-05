import glob
import json
from collections import Counter


#parse ingredients from json file and get nouns
#nouns pick up things like sea and pinch....
recipes = glob.glob('recipes/*.json')
ingredsList = []
for count, file in enumerate(recipes):
    json_data = open(file)
    data = json.load(json_data)
    for criteria in data['hits']:
        ingredsHash = criteria['recipe']['ingredients']
#    ingredsHash = data["ingredients"]
        for ingredient in ingredsHash:
#            ingreds = ingredient["ingredient"].split()
            ingreds = ingredient["text"].split()
            for ingred in ingreds:
                ingredsList.append(ingred.encode('utf-8'))
        json_data.close()

ingredsCount = Counter(ingredsList)
#print ingredsCount
dest = 'ingredients.txt'
#'w' to overwrite, 'a' to append
fo = open(dest, 'w')
for key, value in ingredsCount.items():
    #print (key.strip(',.'), ',', value)
    #print (key.decode('utf-8').strip('(,:;.*)'), ',', value)
    fo.write("%s,%d\n" % (key.decode('utf-8').strip(',.:;()*{}/[0-9]'), value))
fo.close()
