#!/usr/bin/env bash

#copy the db file to destination
function pd() {
 echo "test"
 adb pull /sdcard/fieldsight/metadata/fieldsight_database ~/Desktop/fieldsight_database
}

#clear all sdcard files
cs() {
 rm -rf /sdcard/fieldsight
}

#uninstall but keep all the files and database
uninstall() {
 adb uninstall -k org.bcss.collect.android
}

#hard uninstall
hu() {
 adb uninstall org.bcss.collect.android
}

#install with permisson
iwp() {
    adb install -r -g "$(find . -name "*.apk" -type f)"
    adb shell am start -n org.bcss.collect.android/org.odk.collect.android.activities.SplashScreenActivity
}
#build
build() {
# run adb
[! -f "gradlew"] && echo "gradlew file not exists" && exit
# build and run the debug apk
./gradlew assembleDebug
}

