rm -rf recipe.csv
for file in $(ls recipe*.json)
  do
     echo $file
     python recipExtract.py $file >> recipe.csv
  done
