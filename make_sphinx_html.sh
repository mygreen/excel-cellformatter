#!/bin/sh -e

echo "Building sphinx documentation for version $1"

SCRIPT_DIR=$(cd $(dirname $0); pwd)
cd $SCRIPT_DIR

cd ./src/site/sphinx
/bin/rm -rf ./build

make html PACKAGE_VERSION=$1

## copy html dir
cd $SCRIPT_DIR
/bin/rm -rf ./target/site/sphinx
/bin/mkdir -p ./target/site/sphinx
cp -vr ./src/site/sphinx/build/html/* ./target/site/sphinx/

## github-pagesのsphinx対応
touch ./target/site/.nojekyll

