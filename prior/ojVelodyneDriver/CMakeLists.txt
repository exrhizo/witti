IF (IPHONESIM OR IPHONEDEVICE)
	MESSAGE( "NOTE: Not building the VelodyneHardwareDriver (we are cross compiling)")
ELSE (IPHONESIM OR IPHONEDEVICE)

# HACK this is only going to work locally; should actually probably
# have a FIND_MATLAB cmake thingie
INCLUDE_DIRECTORIES( /Applications/MATLAB_R2010a.app/extern/include/ )
INCLUDE_DIRECTORIES( ../libjaus/include ../libopenJaus/include ../libazcar/include ./include 
  ../libscatter )

LINK_DIRECTORIES( /Applications/MATLAB_R2010a.app/bin/maci64 )
LINK_DIRECTORIES( /Applications/MATLAB_R2010a.app/sys/os/maci64 )
LINK_DIRECTORIES( /Applications/MATLAB_R2010a.app/runtime/maci64 )
LINK_DIRECTORIES( /Users/sprinkle/work/research/azcar/touchdemo/openjaus-touchdemo/libscatter )


add_executable( ojVelodyneHardwareDriver 
	src/velodyneHardwareReader.cpp 
	# src/velodyneDataReader.cpp 
	src/velodyneDataDriver.cpp 
	src/main.cpp 
	include/velodyneReader.h
	include/velodyneHardwareReader.h
	# include/velodyneDataReader.h
	include/velodyneDataDriver.h )
  
SET_TARGET_PROPERTIES( ojVelodyneHardwareDriver PROPERTIES COMPILE_DEFINITIONS "HARDWARE=1")

target_link_libraries( ojVelodyneHardwareDriver jaus openJaus azcar pcap scatter)

add_executable( ojVelodyneDataDriver 
	src/velodyneDataReader.cpp 
	src/velodyneDataDriver.cpp 
	src/main.cpp 
	include/velodyneReader.h
	include/velodyneDataReader.h
	include/velodyneDataDriver.h )
	
SET_TARGET_PROPERTIES( ojVelodyneDataDriver PROPERTIES COMPILE_DEFINITIONS "FAKEDATA=1")

target_link_libraries( ojVelodyneDataDriver mat mx jaus openJaus azcar pcap scatter)

ENDIF (IPHONESIM OR IPHONEDEVICE)
