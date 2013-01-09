# Overview #
This project uses Maven for most build tasks. Read up on Maven since there's not much info here.

# Getting Started #
1. Make sure to uninstall/reinstall the Android SDK (SDK folder/file layouts have changed and setup is based on SDK r21+)
2. Install the Android 2.3.3 (API 10) and Android Support Package from the SDK tools
3. Proceed with any of the setup instructions below
4. Make sure your system has Maven installed.

## Eclipse ##
1. Make sure the ADT Plugin is installed. (http://developer.android.com/sdk/installing/installing-adt.html)
2. Start up Eclipse and go to "File->Import..."
3. Select "Maven->Existing Maven Projects"
4. Browse to the root folder of the android project and select it.
5. Select the sub-projects to include (choose all of them if you aren't sure) and click "Finish".

## Maven/CLI ##
1. Done. Run some maven commands. :)

# Basic Maven Commands #
Below are some basic Maven commands to do some common tasks. (Assumes you are in the project root directory.)

    # debug build, with unit tests
    mvn clean install

    # debug build, no unit tests (faster)
    mvn -pl app clean compile package android:deploy

    # release build, no unit tests
    mvn -pl app -P release clean install android:zipalign -Djarsigner.keystore='<absolute path to keystore>' -Djarsigner.alias=<alias> -Djarsigner.storepass='<store_password>' -Djarsigner.keypass='<alias_password>'