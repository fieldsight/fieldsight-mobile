#!/usr/bin/env bash

installApp() {
# run adb
[! -f "gradlew"] && echo "gradlew file not exists" && exit
# build and run the debug apk
./gradlew installDebug
}

#check the branch name is provided in the command or not
if [ "$#" -ne 1 ]; then
    echo "Need to provide pullrequest(branch) name";
    echo "https://github.com/fieldsight/fieldsight-mobile/pulls";
fi
# confirm whether want to test default master
read -p "Do you want to test realease (y/n)?" choice
case "$choice" in
  y|Y ) $1="master";;
  n|N ) exit;;
  * ) echo "invalid" && exit;;
esac
# get the current branch name
BRANCH=$(git branch)
# check the current branch is master or not
[[ $BRANCH != "master" ]] && git checkout master
#pull the branch from origin
git pull origin "$1"

if [ $? -eq 0 ]; then
    echo "$1 is fetched successfully";
    #checkout to the branch
    git checkout "$1"
    installApp
else
    echo "Failed to pull, please check branch name"
fi

