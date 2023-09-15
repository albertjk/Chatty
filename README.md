# Chatty

## Overview and Features

This repository contains the code for the native Android Chatty app - an instant messaging app. Chatty allows a user to register and chat to other registered users. A registered user can log in to chat to others. Messages sent are shown in a chat log. If a logged in user opens the app, they can immediately see the latest messages received. 

The app screens consist of fragments and navigation is made possible using the Navigation Component. Firebase Authentication stores user credential data, Storage stores user profile images, while messages and other user data are stored in the Realtime Database.

## Built With

* Kotlin
* XML

Other tools and technologies used:

* Firebase Realtime Database
* Firebase Storage
* Firebase Authentication

## Getting Started

To run the application, the following steps must be performed:
1. Make sure that the Java JDK is installed on your machine. If it is not, you can download it for your OS from [here](
https://www.oracle.com/java/technologies/javase-jdk15-downloads.html). Install the JDK, and then set the environment variable for the Java command.
2. Download the latest version of Android Studio for your OS from [here](https://developer.android.com/studio).
3. Install Android Studio.
4. Get the project files from this repository.
5. Open the project in Android Studio. To do this, in Android Studio click on File -> Open -> then browse the project directory and click OK.
6. Once the project is opened in Android Studio, after the Gradle build is finished, ensure that there is an emulator (virtual device) configured to run the application, or connect a physical Android device. 
7. To run the application, click on ‘Run app’, which is the green right arrow icon.