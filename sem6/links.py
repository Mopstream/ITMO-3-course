#!/bin/python3

import os
import sys

subj = sys.argv[1:]

links = {"fs": "https://docs.google.com/spreadsheets/d/e/2PACX-1vQmE3chHy7as4yUxZdmXBVyPNWfBNfgrwr9GAqtGXL7rahjVM3x9qhnC23SvZVOf1HB9209c49HtJRz/pubhtml#",
         "networks" : "https://docs.google.com/spreadsheets/d/1KUbix_gKgeJtz6JnFRO3I0TFEqdho1Wb_zOMLEZRwZE/edit#gid=0",
         "networks2": "https://docs.google.com/spreadsheets/d/1n9kUU5YEHQJJTQ7gsIvgaS31NwUp_zXJCBzLvyyGcS8/edit#gid=1930046114",
         "spo": "https://docs.google.com/spreadsheets/d/1SjW-qbwmqpJZyLmrW-xcKKq_0CqBWpXaVa71osuAYrM/edit#gid=0",
         "compilers": "https://docs.google.com/spreadsheets/d/1ifnPXDIFqeIQkS9eFwG0OaH_47gAhY5L7A33t110GWE/edit?usp=sharing",
         "io": "https://docs.google.com/spreadsheets/d/1njN-3Ibgb6AZ_3oA8dAn-l-_16uR-OKxw9nV2vF86XA/edit#gid=1933065632",
         "tpo": "https://docs.google.com/spreadsheets/d/1YwySu1lnCybpHXiRpPI29xrmCBRJELCbFJKf0v46nqY/edit#gid=2066167636",
         }

for s in subj:
    if links.get(s) is not None:
        os.system("xdg-open " + links[s] + " 2>/dev/null 1>/dev/null")
    else:
        print("pls use " + " ".join([l for l in links.keys() if links.get(l) is not None]))
