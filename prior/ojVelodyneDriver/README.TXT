Velodyne (spinning) LIDAR driver.
Author: Jonathan Sprinkle, Erick Ruiz, Max Weisel

In order to compile and link the test version, you have to install MATLAB 
(currently, it only works with MATLAB r2010a), and to run the hardware 
interfacing version, you have to install pcap. 

Velodyne Hardware Driver:
You need pcap:
 sudo port install pcap

Velodyne Data Driver:
In order to run the velodyne data (test) driver, add this line to your ~/.bash_profile:

  export DYLD_LIBRARY_PATH=${DYLD_LIBRARY_PATH}:/Applications/MATLAB_R2010a.app/bin/maci64:/Applications/MATLAB_R2010a.app/sys/os/maci64

(note that line breaks should not appear in that file, even though they may appear here). You will then need to logout and login, or re-source your bash_profile.

RUNNING THE COMPONENT:
The component only 'runs' when there is someone listening for it. In this way, it doesn't chew up valuable resources (and, it's also easy to detect whether someone is actually connected correctly). Thus, you need to run the ojXGVListener component in order to listen to messages coming from the velodyne. Of course, you also need the ojNodeManager running:

From openjaus-touchdemo:
 ./build/ojNodeManager/ojNodeManager
then, in another tab/window
 ./build/ojXGVListener

RUNNING THE TEST DRIVER (ojVelodyneDataDriver)
The 'test' version uses MATLAB, and reads packets directly from a velodyne_data.mat file that was produced by Erick Ruiz (SIE) from previous experience with the sensor. If you want to run that version, you have to compile and link against MATLAB libraries, which is a trivial affair but requires some CMake knowledge (and also, requires you to have MATLAB installed). You will have to edit/hack the CMakeLists.txt file to have the correct library and include paths if you want to do this, as they are currently coded for Jonathan Sprinkle's machine.

Once you have that compiled and linked, you need a copy of the mat file somewhere nearby. It is currently (but not forever) available in a tgz file at:

http://bracton.ece.arizona.edu/azcar/

If you don't have a user/pass, contact Jonathan (or it may indicate that you shouldn't have that file). Then, when running the velodyne component, you will type:

 ./build/ojVelodyneDriver/ojVelodyneDataDriver ../path/to/velodyne_data.mat

It takes a few seconds to load the matfile, and then it produces an infinitely long series of velodyne sample packets (each sample is one frame), based on that matfile. At that time you should see messages go by showing which frame was just sent, and in the ojXGVListener tab, you should see new velodyne messages being received.

RUNNING THE HARDWARE DRIVER
The hardware driver need to read packets from the actual velodyne. This requires the ethernet output from the velodyne to be hooked up to the wired ethernet adapter of your machine. You need to have that adapter turned on, but it does not need any particular address, as packets that come out of the velodyne are (I think) just broadcast. Because this component uses pcap, you have to be root on mac/linux. However, you do not need any cmd line params:

 sudo ./build/ojVelodyneDriver/ojVelodyneHardwareDriver

Again, you should now see frames flying by, and looking at the XGVlistener, you should see messages being received.

JMS
