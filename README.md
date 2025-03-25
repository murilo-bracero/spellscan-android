# Spellscan Android App

## Installing

At the moment the app is not available neither through the Play Store nor through Github Releases, but I'm working on it.

To install it you'll need to build it from source.

### Firebase

This project uses Firebase Authenticator to authenticate users. You'll need to create a project on Firebase and add the `google-services.json` file to the root of the 'app' module.

### Build

You can do it by cloning the repository and building it with `./gradlew assembleDebug`.

The debug APK will be available at `app/build/outputs/apk/debug/app-debug.apk`.

### Installing

#### Gradle

The `./gradlew installDebug` will install the app in a running emulator or selected device.

#### ADB

To install using adb, run `adb install app/build/outputs/apk/debug/app-debug.apk`.

If you have a device connected, run `adb install -d app/build/outputs/apk/debug/app-debug.apk`

If you want more information about the generic building process of Android apps, please refer to the [official documentation](https://developer.android.com/build/building-cmdline)

## License

This project is released under the Mozilla Public License 2.0.