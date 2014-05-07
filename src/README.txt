Application Name: Wirelessly Integrating Teleoperation Instrument  (WITTI)
Team: Witty
Authors: Brianna Heersink, Brian Smith, Alex Warren
Date: 06 May 2014

README
------

RUNNING WITTI APP 
-----------------
The Witti app can run on android devices using API14 and higher.
From the main menu you are given the options:
Launch       o- Displays data from a configured HTTP address
Demo         o- Displays data local to the phone
Settings     o- Allows modification of the default server location, sequences to load,
                and data refresh mode.
				

KNOWN TESTING ISSUES 
--------------------
The DemoPlaybackTest.java sometimes fails when run with the other tests. If this occurs, 
please re-test DemoPlaybackTest.java individually.


SERVER SETUP
------------
To test the local server requirements. You will need to host certain files.

copy all files from:
./wittiApp/res/raw/

into your server directory and in the witti app's settings, just point it to that directory.

Example:

cp ./wittiApp/res/raw/* /var/www/lidar
cp ./server/* ./var/www/lidar