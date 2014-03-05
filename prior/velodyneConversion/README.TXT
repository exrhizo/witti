You will need to install CMake in order to make this work. You will also need MATLAB, and pcap

** Installation

Make sure you have installed matlab and CMake. To install pcap on your machine (MacOS), use port:

sudo port install libpcap

** Compilation

It's always best to do an out-of-source build:

mkdir build && cd build
cmake .. && make

You should now copy savedump.exe back to the root directory

cp savedump.exe ..

** Execution

In order to run on MacOS, you need to run as superuser. We recommend starting MATLAB as superuser:

sudo /Applications/MATLAB_R2012a.app/bin/matlab

If this fails due to licensing issues you may have to change the line:
	dos('./savedump.exe','-echo'); %capture one revolution
to:
	dos('sudo ./savedump.exe','-echo'); %capture one revolution
No other changes were required to solve the problem in the past.

Where you substitute in your appropriate version of MATLAB. Then, in the directory in which you 
find these files, run

plotxdataRT(1000)

Which will capture and show 1000 samples (rotations) from the sensor.


