rm -rf ingsdata.csv
rm -rf with_ingredients/*.json
python parse_ingredients.py

rm -rf alllbls.txt allings.txt
python recipExtractall.py
rm -rf uniqings.txt
grep -v '^$' allings.txt | sort -u > uniqings.txt
python recipExtract.py
