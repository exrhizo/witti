PROJECT( ReadVelodyne )
CMAKE_MINIMUM_REQUIRED ( VERSION 2.8 )
FIND_PACKAGE ( Threads REQUIRED )
add_executable( readVelodyne readVelodyne2.cpp 
			velodyne.cpp
			MBmath/MBmatrix.cpp
			MBmath/MBvector.cpp
			MBmath/MBquaternion.cpp
			MBmath/MBbezier.cpp
			MBmath/MBtime.cpp
			MBmath/MBother.cpp
			MBmath/MBlowpassfilter.cpp
			)
# add_executable( readVelodyneGL readVelodyneGL.cpp 
# 			GLvelodyne.cpp
# 			MBmath/MBmatrix.cpp
# 			MBmath/MBvector.cpp
# 			MBmath/MBquaternion.cpp
# 			MBmath/MBbezier.cpp
# 			MBmath/MBtime.cpp
# 			MBmath/MBother.cpp
# 			MBmath/MBlowpassfilter.cpp
# 			)
target_link_libraries( readVelodyne pcap ${CMAKE_THREAD_LIBS_INIT})
# target_link_libraries( readVelodyneGL pcap )
include_directories ( MBmath )