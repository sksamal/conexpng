echo "Build " `git log -1 | grep Date` > .build.txt
echo "Host:" `hostname` >> .build.txt
git add .
git commit -m "$1"
git push origin master

