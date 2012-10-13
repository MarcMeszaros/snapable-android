# Overview #
This project uses Maven for most build tasks. Read up on Maven since there's not much info here.

# Getting Started #
To be filled out...

# Basic Maven Commands #

    # debug build, with unit tests
    mvn clean install

    # debug build, no unit tests (faster)
    mvn -pl myapp clean compile package android:deploy

    # release build, no unit tests
    mvn -pl myapp -P release clean install android:zipalign -Djarsigner.keystore='<absolute path to keystore>' -Djarsigner.alias=<alias> -Djarsigner.storepass='<store_password>' -Djarsigner.keypass='<alias_password>'