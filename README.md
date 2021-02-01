# Open Hosts Editor for Android 

<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="icon" width="24" style="margin-bottom: -2px" /> [![Build Status](https://travis-ci.com/SirPryderi/open-hosts-editor.svg?branch=main)](https://travis-ci.com/SirPryderi/open-hosts-editor)

--- 

> ⚠ **ROOT REQUIRED**

Open Hosts Editor is a free and open source application to edit the `/etc/hosts` file for Android devices.

It is available for download from [Play Store](https://play.google.com/store/apps/details?id=me.vittorio_io.openhostseditor).

Your device **must be rooted**. Not all devices have the hosts file in the same path, so it might not work on yours. 
You can submit an issue so that I can investigate.

The application is **free** and contains **no ad**.

## Contributions
> 🌍 This project is looking for translators!

Open Hosts Editor needs your help, all contributions are welcome! Open a PR, submit an issue, or suggest a feature!

[![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/I2I42ROKG)

## Screenshots

<span>
  <img src="app/src/main/play/listings/en-GB/graphics/phone-screenshots/1.png" alt="screenshot" width="200"/>
  <img src="app/src/main/play/listings/en-GB/graphics/phone-screenshots/2.png" alt="screenshot" width="200"/>
  <img src="app/src/main/play/listings/en-GB/graphics/phone-screenshots/3.png" alt="screenshot" width="200"/>
</span>

## Building the app
The required android SDK is 29.

The easiest way of building and running the app is by opening the project on Android Studio.

Building from the command line is equally easy, just run:

Windows:
```cmd
gradlew.bat build
```

*nix:
```bash
./gradlew build
```