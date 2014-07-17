#!/bin/sh -e
export RESET_BETWEEN_SCENARIOS=1
export SCREENSHOT_VIA_USB=true
# ./gradlew build
calabash-android run app/build/outputs/apk/app-development-debug.apk  
