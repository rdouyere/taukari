#!/usr/bin/python

# This script will load a CSV into an HDF

import sys
import pandas as p
from sklearn import cluster
#from ruffus import *

#fileName = ""
#cols = ""
#data = None

#if (len(sys.argv) > 1):
#    fileName = 'data/' + sys.argv[1]
#    print 'Loading file', fileName;
#    if (len(sys.argv) > 2):
#    	cols = sys.argv[2].split(',');
#    	print 'with columns', cols

#@transform(fileName, suffix(".csv"), ".hdf")
#def load_in_hdf(input_path, input_file, output_file):
#    if (cols is None):
#        d = p.read_csv(input_file)
#    else:
#        d = p.read_csv(input_file, usecols=cols)
#    d.to_hdf(output_file,"table")
    #print d.describe()


#@follows(load_in_hdf)
#@files(load_in_hdf, "")
#def load_in_memory(input_file, output_file):
#    print "A", input_file
#    global data
#    data = p.read_hdf(input_file,"table")


def getData(fileName_, cols_ = None):
    if (cols_ is None):
        d = p.read_csv(fileName_)
    else:
        d = p.read_csv(fileName_, usecols=cols_.split(','))
    return d

#open(fileName, "w")
#pipeline_run([load_in_memory])





