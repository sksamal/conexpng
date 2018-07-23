git add .
echo "Build " `git log -1 | grep Date` > .build.txt
echo "Host:" `hostname` >> .build.txt
git commit -m "$1"
git push origin master

