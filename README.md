
## Overview

TripTracker is the Android Component of the MAS Software Engineering Master Thesis by Regina and Andrea.

TripTracker is a GPS tracking application tailored for sailors and marine adventurers. It enables users to initiate sailing trips, record their GPS coordinates, and send this data to a backend for AI-enhanced sailing-report and track logging. Designed to bring maritime logging into the 21st century, TripTracker leverages Android 13's capabilities to provide a reliable and user-friendly trip recording, meaning more time for sailors to enjoy their hobby and less time doing log keeping and manually gathering trip data. 

## Features

- Start Sailing Trips: Easily mark the beginning of your sailing adventures directly from your Android phone.
- Real-Time GPS Recording: Record your location with real-time GPS monitoring.
- GPS Data Transmission: Send your location data to a backend API for storage.
- AI enhanced sailing report logging: Use AI to create comprehensive log entries from just a few bullet pointed words

## Developers: getting started with the code

### Prerequisites
- Download Android Studio from here https://developer.android.com/studio 
- Install the components needed to work on this project. Select the "SDK Manager" menu. Select a minimum SDK version of Android 13 (API level 33).
- In the section "SDK Platforms" select the following tools and click on "apply" to install them:
  - Android SDK Build-Tools 33
  - Android Emulator
  - Android SDK-Platform-Tools
  - Android SDK Tools
  - Google USB Driver (so that you can run the app locally on your own Android phone)
  - Intel x86 Emulator Accelerator

### Local Development
- Clone this repository
- Open the repository in Android Studio
- You will probably want to use both an emulated (simulated) phone, as well a physical Android phone. To get both going:
  - Emulator: in the Android SDK menu, click on "Create Virtual Device". Create a "Pixel 4a" with an API of 33. This is the minimum hardware and software that this app will run on.
  - Your own physical Android device: 
    - Go To Settings > About phone.
    - Tap "Build Number" 7 times to enable Developer Mode.
    - Return to Settings and find "Developer Options".
    - In "Developer Options", toggle on USB Debugging.
    - Connect your computer and phone with a USB cable.
    - Allow USB Debugging on your phone when prompted. 
    - Run the app: in Android Studio, select your device from the "device dropdown" and click Run to start the app on your phone.

## Developers: using the app directly

If you simply want to use the app, without creating a full developer environment: a signed APK is available in app/release folder. Simply transfer the file "app-release.apk" in here to your Android phone. You are now able to use the app on your phone, without going via Android Studio.

## Usage
- Starting a Trip: Open the app and use the 'Start Trip' button to begin tracking your journey.
- Click the "Stop Trip" button when finished.
- Add a trip note. 
- Click "Submit Trip". 

## Related repositories:

- Web-Application: https://github.com/ReginaTraber/mas-web-tripmanager 
- Project Wiki: https://github.com/pinefoambath/mas-documentation-triptracker 

## Rights
All usage and copyright rights reserved by the original developers Regina and Andrea. 


