PROJECT( SaveDump )
add_executable( savedump.exe savedump.c )
target_link_libraries( savedump.exe pcap )
