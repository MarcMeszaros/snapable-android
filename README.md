# Overview #
This project uses Gradle for most build tasks.

# Getting Started #
1. Make sure to uninstall/reinstall the Android SDK (SDK folder/file layouts have changed and setup is based on SDK r22+)
2. Install the Android 4.0.3 (API 15) and Android Support Package from the SDK tools
3. Proceed with any of the setup instructions below
4. Make sure your system has Gradle 1.10+ installed.

Note: Make sure the "Google Repository" and "Android Repository" to be installed via the SDK manager.

## Gradle/CLI ##
1. Done. Run some Gradle commands. :)

## Android Gradle ##
More details about the [Android Gradle Plugin](http://tools.android.com/tech-docs/new-build-system/user-guide) is on the
tools website.

# Basic Gradle Commands #
Below are some basic Gradle commands to do some common tasks. (Assumes you are in the project root directory.)

    # available tasks
    ./gradlew tasks

    # debug build
    ./gradlew build

    # debug build, and install on device
    ./gradlew installDebug

    # release build, no unit tests
    ./gradlew assembleRelease