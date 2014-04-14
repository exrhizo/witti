#!/usr/bin/python
"""
A simple script to convert from the text file
of LiDAR data to a binary file.
"""

from sys import argv
from array import array
from random import random

RANDOM_THRESHOLD = .8

def main():
    """
    Main function that converts from txt to binary,
    given command line args.
    """
    if len(argv) != 3:
        print "Usage ./convert_to_binary.py [input] [output]"
    in_file_name = argv[1]
    out_file_name = argv[2]
    in_file = open(in_file_name, 'r')
    out_file = open(out_file_name, 'wb')
    point_counter = 0
    for line in in_file:
        if random() < RANDOM_THRESHOLD:
            continue
        yxz = line.split()
        if len(yxz) != 3:
            print "non-triple: " + repr(yxz)
            continue
        point_counter += 1
        array_buffer = array('d', [float(x) for x in yxz])
        array_buffer.tofile(out_file)
    print point_counter


if __name__ == "__main__":
    main()
