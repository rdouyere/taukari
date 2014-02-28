#!/bin/bash

virtualenv-2.7 a
cd a
bin/pip install pandas
bin/pip install ruffus
bin/pip install numexpr
bin/pip install cython
bin/pip install tables

export FCFLAGS="-arch x86_64"
export CFLAGS="-arch x86_64"
export FFLAGS=""
export LDFLAGS="-Wall -undefined dynamic_lookup -arch x86_64"
export CC=gcc
export CXX="g++ -arch x86_64"

bin/pip install scipy   
bin/pip install matplotlib
bin/pip install scikit-learn
bin/pip install ipython

