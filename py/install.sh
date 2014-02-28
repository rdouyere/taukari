#!/bin/bash

virtualenv-2.7 a
cd a
source bin/activate
pip install pandas
pip install ruffus
pip install numexpr
pip install cython
pip install tables

export FCFLAGS="-arch x86_64"
export CFLAGS="-arch x86_64"
export FFLAGS=""
export LDFLAGS="-Wall -undefined dynamic_lookup -arch x86_64"
export CC=gcc
export CXX="g++ -arch x86_64"

pip install scipy   
pip install matplotlib
pip install scikit-learn
pip install ipython

