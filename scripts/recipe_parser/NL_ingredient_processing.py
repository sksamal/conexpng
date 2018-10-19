from bs4 import BeautifulSoup
import glob
import nltk

import pprint
import json


tokenizer = None
tagger = None


def init_nltk():
    global tokenizer
    global tagger
    tokenizer = nltk.tokenize.RegexpTokenizer(r'\w+|[^\w\s]+')
    tagger = nltk.UnigramTagger(nltk.corpus.brown.tagged_sents())


def tag(text):
    global tokenizer
    global tagger
    if not tokenizer:
        init_nltk()
    tokenized = tokenizer.tokenize(text)
#    print (tokenized)
    tagged = tagger.tag(tokenized)
#    print (tagged)
#    tagged.sort(lambda x,y:cmp(x[1],y[1]))
#    tagged.sort(key = lambda x:x[1])
    return tagged

#parse ingredients from json file and get nouns
#nouns pick up things like sea and pinch....
recipes = glob.glob('with_ingredients/*.json')

for count, file in enumerate(recipes):
    json_data = open(file)
    data = json.load(json_data)
    for criteria in data['hits']:
        ingredsHash = criteria['recipe']['ingredients']
#    ingredsHash = data["ingredients"]
        for ingredient in ingredsHash:
            #ingreds = ingredient["ingredient"]
            ingreds = ingredient["text"]
            tagged = tag(ingreds)
            for words in tagged:
                if words[1] == 'NN':
                    print (words[0])
    json_data.close()
