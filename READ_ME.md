# Android Engineer - Solution

Before implementing the solution, I started reading some papers regarding how to detect fall devices. 
Most of the articles have similar solutions using acceleration sensors and service in Android devices. 

The sensors used are accelerometer and gyroscope.
Acceleration sensors allow registering how the device is moving on the three axes. I will focus in 
particular on the z ax for the falling and the net force for the shaking.
Gyroscope sensors register the angular speed on the three axes. I used them for detecting the rotation 
of the device.

An Android service constantly runs in the background. One of the requirements is to monitor the movement 
regularly, so when the service suspects a fall, the date and duration of the event are saved in the 
database and a notification is sent. For future implementation, a call button can be implemented.

For testing the app, it requires an Android device with at least version 8.
I tested the app on my device Pocophone.

The app is a simple list of fall events showing the date and the duration of each of them.
The app has a MainActivity which extends the library `fall-library-module` and an Adapter for the 
RecycleView.
The architecture of the library is MVP and the app is written with Kotlin.

`fall-library-module` is the library to detect the fall free of the device and it contains the service 
for detecting fall free, shaking and rotation. In the last two cases, the app won't register the movements 
but identify them in case of future implementation. Currently, there is just a comment. 
In case the device falls, the app saves the date and the duration of the event in the local database 
and sends a notification to the user. The duration time is calculated in MilliSeconds.

The thresholds and slot times are based on researches and then adjusted based on my testing. For the 
gravity, the threshold and the slot were higher, but to know to break my phone I set it lower.

The app runs in foreground.
