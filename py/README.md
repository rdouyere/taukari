
Environment installation
========================

The goal is to install all the required libs into a virtualenv (in order to sandbox and not mess with the system versions of Python).

Prerequisite
------------

* Python 2.7.6
* [virtualenvwrapper](http://virtualenvwrapper.readthedocs.org/en/latest/)
* libhdf5-dev
* libblas3-dev
* libatlas-base-dev
* gfortran
* libpng12-dev

### Virtualenvwrapper

Here are the installation instructions:
```sh
$ mkvirtualenv taukari -p /usr/bin/python2.7 -a /w/taukari/py -r requirements
$ workon taukari
```

### Virtualenv

    $ virtualenv-2.7 a
    $ cd a
	$ source bin/activate
    $ pip install pandas
    $ pip install ruffus
    $ pip install numexpr
    $ pip install cython
    $ pip install tables
    $ pip install scipy   
    $ pip install matplotlib
    $ pip install scikit-learn
    $ pip install ipython

MacOS issue
-----------

For MacOSX scipy installation is not that easy. As of today I did not succeed on 10.9 (was ok on 10.8).

To be continued with the following resources:
* http://blog.zuloo.de/archives/Building-scipy-on-OSX-Mavericks-11.html
* https://gist.github.com/goldsmith/7262122

Probably something like:

    $ export FCFLAGS="-arch x86_64"
    $ export CFLAGS="-arch x86_64"
    $ export FFLAGS=""
    $ export LDFLAGS="-Wall -undefined dynamic_lookup -arch x86_64"
    $ export CC=gcc
    $ export CXX="g++ -arch x86_64"
    $ bin/pip install scipy


Testing the installation
------------------------

Let's make sure that everything is ok:

    $ mkdir data
    $ python ../src/check_install.py

At this point you should see something like :

                 age
    count   2.000000
    mean   33.500000
    std    12.020815
    min    25.000000
    25%    29.250000
    50%    33.500000
    75%    37.750000
    max    42.000000
    
    [8 rows x 1 columns]
        Job = [data/test.hdf -> None] completed
    Completed Task = stats

Playing with data
=================

    $ ipython
    In [1]: %run ../src/load.py
    In [2]: data = getData("../data/test.csv", "col1,col2")


