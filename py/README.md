
Python 2.7.6 + virtualenv

    $ virtualenv-2.7 a
    $ cd a
    $ bin/pip install pandas
    $ bin/pip install ruffus
    $ bin/pip install numexpr
    $ bin/pip install cython
    $ bin/pip install tables
    $ mkdir data
    $ bin/python ../src/check_install.py

At this point you should see something like 

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
