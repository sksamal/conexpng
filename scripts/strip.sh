file=$1
sed -e 's/^[0-9]//g' $file > $file.0 
sed -e 's/^tablespoons//g' $file.0 > $file.1
sed -e 's/^tablespoon//g' $file.1 > $file.2
sed -e 's/^cups//g' $file.2 > $file.3
sed -e 's/^cup//g' $file.3 > $file.4
sed -e 's/^[%|&|#|-|,| ]//g' $file.4 > $file.5
sed -e 's/^  //g' $file.5 > $file.6
sed -e 's/^tbsp//g' $file.6 > $file.7
sed -e 's/^tsp//g' $file.7 > $file.8
sed -e 's/^[oz|ounces|ounce]//g' $file.7 > $file.8
