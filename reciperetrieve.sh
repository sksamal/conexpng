for i in {199..1000..4}
 do
	 echo $i-$(($i+3))
	 curl "https://api.edamam.com/search?q=pizza&app_id=a12a0d05&app_key=66658dca1254fd3932117c4cb31801b0&from=0&to=100&calories=$i-$(($i+3))" -o recipe$i-$(($i+3))cal.json
	 sleep 20s
done
