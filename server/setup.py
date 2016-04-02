# -*- coding: utf-8 -*-
try:
    from setuptools import setup, find_packages
except ImportError:
    from distutils.core import setup

import os
import sys

sys.path.insert(0, os.path.abspath('src'))

import unwind

setup(
    name='unwind',
    version=unwind.__version__,
    description="Uniform Imperials Notification Dispatching Server",

    url="https://github.com/pirogoeth/uniform_imperials",
    author="Sean Johnson",
    author_email="sean.johnson@maio.me",

    classifiers=[
        "Development Status :: 3 - Alpha",
    ],
    packages=find_packages('src'),
    package_dir={
        '': 'src'
    },
    install_requires=[
        'malibu',
    ],
    include_package_data=True,
    exclude_package_data={
        '': ['README.md'],
    },
    test_suite='nose.collector',
    tests_require=[
        'nose',
        'coverage',
    ],
    entry_points={
        "console_scripts": [
            "unwind-server = unwind.entrypoint:main",
        ],
    },
    zip_safe=True
)
