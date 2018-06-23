#!/bin/sh -e

echo "Building sphinx documentation for version $1"

SCRIPT_DIR=$(cd $(dirname $0); pwd)
cd $SCRIPT_DIR

if [ -e ./src/site/sphinx/build ]; then
  echo "step1"
  sudo /bin/rm -rf ./src/site/sphinx/build
fi

echo "step2"
cd ./src/site/sphinx

echo "step3"
make html PACKAGE_VERSION=$1

echo "step4"
sudo /usr/bin/chown -R jenkins:jenkins ./src/site/sphinx/build

## copy html dir
cd $SCRIPT_DIR
echo "step5"
sudo /bin/rm -rf ./target/site/sphinx
/bin/mkdir -p ./target/site/sphinx
echo "step6"
/bin/cp -vr ./src/site/sphinx/build/html/* ./target/site/sphinx/

## github-pagesのsphinx対応
touch ./target/site/.nojekyll

