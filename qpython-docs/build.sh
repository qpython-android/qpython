#!/bin/bash
rm -fr ../docs
make html

#mv static static
#mv sources sources

cd build/html && find . -name "*.html" -exec sed -i -e 's/_static/static/g' {} \; && rm *-e && mv _static static

cd ../.. && mv build/html  ../docs && cp CNAME ../docs
