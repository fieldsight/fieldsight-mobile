#!/usr/bin/env bash

BRANCH_NAME = "master"

installApp() {
# run adb
[! -f "gradlew"] && echo "gradlew file not exists" && exit
# build and run the debug apk
./gradlew assembleDebug
}

runApp() {
   adb install "$(find . -name "*.apk" -type f)"
}

askToTestRelease() {
    read -p "Do you want to test realease (y/n)?" choice
    case "$choice" in
      y|Y ) echo "pulling master" ;;
      n|N ) exit;;
      * ) echo "invalid" && exit;;
    esac
}

#check the branch name is provided in the command or not
if [ "$#" -ne 1 ]; then
    echo "Need to provide pullrequest(branch) name";
    echo "https://github.com/fieldsight/fieldsight-mobile/pulls";
    # confirm whether want to test default master
    askToTestRelease
else BRANCH_NAME = $1
fi

echo "$BRANCH_NAME";

# get the current branch name
BRANCH=$(git branch)

# check the current branch is master or not
[[ $BRANCH != "master" ]] && git checkout master

#pull the branch from origin
git pull origin "$BRANCH_NAME"

if [ $? -eq 0 ]; then
    echo "$1 is fetched successfully";
    #checkout to the branch
    git checkout "$BRANCH_NAME"
    runApp
else
    echo "Failed to pull, please check branch name"
fi

