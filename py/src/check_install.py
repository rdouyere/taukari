
# This script will use the only try to do some basic manipulations to ensure install is ok
import glob
import pandas as p
from ruffus import *

@files("../data/test.csv","data/test.hdf")
def etl(inputFile, outputFile):
    data = p.read_csv(inputFile)
    data.to_hdf(outputFile,"table")

@files("data/test.hdf", None)
@follows(etl)
def stats(inputFile, outputFile):
	comms = p.read_hdf(inputFile,"table")
	print comms.describe()

pipeline_run([stats])
