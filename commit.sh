git add .
git log -1 | grep Date > .build.txt
echo `hostname` >> .build.txt
git commit -m "$1"
git push origin master

