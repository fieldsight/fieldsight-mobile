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
if [! $BRANCH != "master" ]; then
   git checkout master
fi
#pull the branch from origin
git pull origin "$1"
#checkout to the branch
git checkout "$1"

# run adb
[! -f "gradlew"] && echo "gradlew file not exists" && exit
./gradlew assembleDebug
./gradlew installDebug
