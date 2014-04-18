README
------

RUNNING WITTI APP 
-----------------
There are no special requirements for the witti app.
From the main menu you are given 




TESTING
-------


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