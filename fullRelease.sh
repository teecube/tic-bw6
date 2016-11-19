#!/bin/sh

if [ "$#" -ne 6 ]; then
    echo "Usage: $0 t3ReleaseVersion t3DevVersion ticReleaseVersion ticDevVersion releaseVersion devVersion" >&2
    exit 1
fi

# releasing T3
git clone https://git.teecu.be/teecube/t3.git
cd t3
git checkout release
RELEASE_VERSION=$1 && sed -i "s/\(RELEASE_VERSION=\).*\$/\1${RELEASE_VERSION}/" release.properties
DEV_VERSION=$2 && sed -i "s/\(DEV_VERSION=\).*\$/\1${DEV_VERSION}/" release.properties
git add release.properties && git commit -m "Triggering release"
git push origin release

cd ..
rm -rf ./t3/

# releasing TIC
git clone https://git.teecu.be/teecube/tic.git
cd tic
git checkout release
RELEASE_VERSION=$3 && sed -i "s/\(RELEASE_VERSION=\).*\$/\1${RELEASE_VERSION}/" release.properties
DEV_VERSION=$4 && sed -i "s/\(DEV_VERSION=\).*\$/\1${DEV_VERSION}/" release.properties
git add release.properties && git commit -m "Triggering release"
git push origin release

cd ..
rm -rf ./tic/

# releasing
RELEASE_VERSION=$5 && sed -i "s/\(RELEASE_VERSION=\).*\$/\1${RELEASE_VERSION}/" release.properties
DEV_VERSION=$6 && sed -i "s/\(DEV_VERSION=\).*\$/\1${DEV_VERSION}/" release.properties
git add . && git commit -m "Triggering release"
git push origin release
