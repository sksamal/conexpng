for i in {1201..2000..30}
 do
	 echo $i-$(($i+3))
	 curl "https://api.edamam.com/search?q=pizza&app_id=a12a0d05&app_key=66658dca1254fd3932117c4cb31801b0&from=0&to=100&calories=$i-$(($i+29))" -o recipe$i-$(($i+29))cal.json
	 sleep 40s
done
