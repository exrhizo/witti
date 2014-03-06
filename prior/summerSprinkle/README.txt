README

You need libpcap in order to run this program. It takes a saved FILE.pcap (which is a raw saved file) and exports it as a FILE.pcap.out.bin, which can 
be used by MATLAB's dataviewer.m

BUILDING

 mkdir build
 cd build && cmake ..
 make
 cp pcap2bin ../pcap2bin.exe

The last step permits MATLAB to call in real time (if appropriate)

RUNNING FROM HARDWARE
==============================================================
You need to use 'sudo' on linux to get access to the device.

 sudo ./pcap2bin -s -o herp.bin

This reads continuously (-s) and saves output to herp.bin
You can also restrict the angle as below 

RUNNING FROM A DATA FILE
==============================================================
 ./pcap2bin -i ../filename.pcap

This will produce ../filename.pcap.out.bin

RESTRICTING PASSED ANGLES
==============================================================
You can restrict the angle by using a simple input:

 ./pcap2bin -i ../../test-run-parkinglot.pcap -sssnnn

Where degrees specify these angles (0->360 is clockwise). e.g., 

 ./pcap2bin -i ../../test-run-parkinglot.pcap -330030

Will give a 60\deg field of view, 30 left of local 0 to 30 right

VIEWING
==============================================================

Now, in MATLAB run

  >> dataviewer 'filename.pcap.out.bin'

This will visualize your pcap files as a (x,y,z) point cloud. 


RUNNING MATLAB IN REAL TIME
==============================================================

If you invoke dataviewer correctly, you can watch the Velodyne
in real time from MATLAB. 

 >> dataviewer -realtime

This calls (with 'sudo') the ./pcap2bin.exe that is in this 
directory. If it fails to run, remember to copy it from 'build'

You can have the realtime data viewer restrict angle as well, 
the angle string is passed directly to the ./pcap2bin.exe file

 >> dataviewer -realtime -090270

This shows what is in the "south" hemisphere.

OBSERVING DATA WHILE RECORDING A LARGE FILE
==============================================================
Data is not archived in the real-time operation, only one turn
at a time is saved.

If you want to save your files, you cannot visualize them in true 
real time, because you will always lag behind the data being produced.

You can approximate the behavior by running in a shell:
  cd build && sudo ./pcap2bin -s -o testfile.bin

Now in MATLAB:
  >> dataviewer './build/testfile.bin'

This will show your 'recent' data, but you will almost certainly
see a lag in what is displayed, and the lag will get larger over time.

