README
------

RUNNING WITTI APP 
-----------------
There are no special requirements for the witti app.
From the main menu you are given the options:
Launch       o- Displays data from a configured HTTP address
Demo         o- Displays data local to the phone
Settings     o- Allows modification of the default server location
             `- and sequence to load.
Path Drawing o- Demo of path drawing capability.


TESTING
-------
Before running all tests the app must be downloaded and launched through its Android icon.
Then choose 'Launch' and select the 'Dummy' files to visualize. After this, the app
can be exited and successfully tested.


SERVER SETUP
------------
To test the local server requirements. You will need to host certian files.

copy all files from:
./wittiApp/res/raw/

into your server directory and in the witti app's settings, just point it to that directory.

Example:

cp ./wittiApp/res/raw/* /var/www/lidar
cp ./server/* ./var/www/lidar
# php scripts are optional for beta release