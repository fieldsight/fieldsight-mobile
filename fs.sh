#!/usr/bin/env bash
#check the branch name is provided in the command or not
if [ "$#" -ne 1 ]; then
    echo "Need to provide pullrequest(branch) name";
    echo "https://github.com/fieldsight/fieldsight-mobile/pulls";
    exit;
fi
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

installApp() {
# run adb
[! -f "gradlew"] && echo "gradlew file not exists" && exit
./gradlew assembleDebug
./gradlew installDebug

}
