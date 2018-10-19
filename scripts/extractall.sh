rm -rf alling.csv
for file in $(ls recipe_json/recipe*.json)
  do
     echo $file
     python recipExtract.py $file >> alling.csv
  done
