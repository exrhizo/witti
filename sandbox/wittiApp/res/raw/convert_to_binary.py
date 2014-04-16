#!/usr/bin/python
"""
A simple script to convert from the text file
of LiDAR data to a binary file.
"""

from sys import argv, byteorder
from array import array
from random import random

RANDOM_THRESHOLD = 0

def main():
    """
    Main function that allows this module to
    operate as a script.
    """
    if len(argv) != 3:
        print "Usage ./convert_to_binary.py [input] [output]"
    create_binary_frames(argv[1], argv[2], 10000)

def create_binary_frames(in_file_name, out_file_name, 
            chunk_size = None, threshold = RANDOM_THRESHOLD):
    """
    Function that converts from txt to binary,
    given command line args.
    """
    file_index = 0
    out_file_name  = out_file_name + "_{:0>4}.bin"
    in_file = open(in_file_name, 'r')
    out_file = open(out_file_name.format(file_index), 'wb')
    point_counter = 0
    print "Byte order: {} Endian".format(byteorder)
    is_little_endian = byteorder == 'little'
    for line in in_file:
        if random() < threshold:
            continue
        yxz = line.split()
        if len(yxz) != 3:
            print "non-triple: " + repr(yxz)
            continue
        point_counter += 1
        array_buffer = array('d', [float(x) for x in yxz])
        if is_little_endian:
            array_buffer.byteswap()
        array_buffer.tofile(out_file)
        if chunk_size and point_counter % chunk_size == 0:
            out_file.close()
            file_index += 1
            out_file = open(out_file_name.format(file_index), 'wb')
            
    print "Points: {}".format(point_counter)
    print "Files:  {}".format(file_index+1)


if __name__ == "__main__":
    main()
