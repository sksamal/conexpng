rm -rf ingredients.csv
for file in $(ls recipe*.json)
  do
     echo $file
     python3 ingExtract.py $file >> ingredients.csv
  done
