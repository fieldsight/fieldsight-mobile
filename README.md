# This project is no longer maintained!

# FieldSight Mobile 
![Platform](https://img.shields.io/badge/platform-Android-green)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![CircleCI](https://circleci.com/gh/fieldsight/fieldsight-mobile.svg?style=svg)](https://circleci.com/gh/fieldsight/fieldsight-mobile)


FieldSight is a tool to remotely monitor, supervise and manage your projects. Major objectives of FieldSight includes, 

#### Data Collection
Assure quality and consistency throughout the project lifespan by returning to monitor a site with custom forms collecting 22 kinds of data even when offline and out of range.

#### Project Management
Replicate the organization structure and assign user specific roles, with differentiated access and dialogue through several communication channels and supporting educational materials.

#### Reporting, Monitoring and Evaluating.
Track project progress with user friendly dashboards that visualize data in the form of interactive maps, charts and more with a donor viewing option. Export all data simply, for further custom reporting.

## Table Of Contents
* [Setting up your development environment](#setting-up-your-development-environment)
* [Creating signed releases for Google Play Store](#creating-signed-releases-for-google-play-store)
* [Troubleshooting](#troubleshooting)


## Setting up your development environment
1. Download and install [Git](https://git-scm.com/downloads) and add it to your PATH
1. Download and install [Android Studio](https://developer.android.com/studio/index.html) 
1. Clone the project locally. At the command line:

        git clone https://github.com/fieldsight/fieldsight-mobile

    If you prefer not to use the command line, you can use Android Studio to create a new project from version control using `https://github.com/fieldsight/fieldsight-mobile`.
1. Use Android Studio to import the project from its Gradle settings. To run the project, click on the green arrow at the top of the screen.

## Creating signed releases for Google Play Store
Maintainers keep a folder with a clean checkout of the code

Maintainers have a `local.properties` file in the root folder with the following:
```
sdk.dir=/path/to/android/sdk
```

Maintainers have a `secrets.properties` file in the `collect_app` folder with the following:
```
// collect_app/secrets.properties
RELEASE_STORE_FILE=/path/to/collect.keystore
RELEASE_STORE_PASSWORD=secure-store-password
RELEASE_KEY_ALIAS=key-alias
RELEASE_KEY_PASSWORD=secure-alias-password
```

Maintainers also have a `google-services.json` [file](https://firebase.google.com/docs/android/setup) in the `collect_app/google-services.json` folder.

To generate official signed releases, you'll need the keystore file, the keystore passwords, a configured `collect_app/secrets.properties` file, Then run ` ./gradlew assembleRelease`. If successful, a signed release will be at `collect_app/build/outputs/apk`.


## Troubleshooting

#### Android Studio Error: `SDK location not found. Define location with sdk.dir in the local.properties file or with an ANDROID_HOME environment variable.`
When cloning the project from Android Studio, click "No" when prompted to open the `build.gradle` file and then open project.

#### Execution failed for task ':collect_app:transformClassesWithInstantRunForDebug'.

We have seen this problem happen in both IntelliJ IDEA and Android Studio, and believe it to be due to a bug in the IDE, which we can't fix.  As a workaround, turning off [Instant Run](https://developer.android.com/studio/run/#set-up-ir) will usually avoid this problem. The problem is fixed in Android Studio 3.5 with the new [Apply Changes](https://medium.com/androiddevelopers/android-studio-project-marble-apply-changes-e3048662e8cd) feature.


`

