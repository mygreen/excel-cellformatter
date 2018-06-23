#!/bin/sh -e

echo "Building sphinx documentation for version $1"

SCRIPT_DIR=$(cd $(dirname $0); pwd)
cd $SCRIPT_DIR

if [ -e ./src/site/sphinx/build ]; then
  /bin/rm -rf ./src/site/sphinx/build
fi

cd ./src/site/sphinx
make html PACKAGE_VERSION=$1
/usr/bin/chown -R jenkins:jenkins ./src/site/sphinx/build

## copy html dir
cd $SCRIPT_DIR
/bin/rm -rf ./target/site/sphinx
/bin/mkdir -p ./target/site/sphinx
/bin/cp -vr ./src/site/sphinx/build/html/* ./target/site/sphinx/

## github-pagesのsphinx対応
touch ./target/site/.nojekyll

