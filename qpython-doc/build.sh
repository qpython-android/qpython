#!/bin/bash
rm -fr ../docs
make singlehtml

#mv static static
#mv sources sources

cd build/singlehtml && sed -i -e 's/_static/static/g' *.html && rm *-e && mv _static static

cd ../.. && mv build/singlehtml  ../docs && cp CNAME ../docs
