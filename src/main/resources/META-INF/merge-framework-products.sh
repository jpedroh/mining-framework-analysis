#!/bin/sh

SCRIPT_LOCATION=$(cd "$(dirname "$0")"; pwd)

PRODUCT_ID=product
IPHONEOS_FRAMEWORK_LOCATION=$1
IPHONESIMULATOR_FRAMEWORK_LOCATION=$2
MERGED_FRAMEWORK_LOCATION=$3
FRAMEWORK_NAME=$4

cp ${IPHONEOS_FRAMEWORK_LOCATION}/${FRAMEWORK_NAME} $PRODUCT_ID.dev
cp ${IPHONESIMULATOR_FRAMEWORK_LOCATION}/${FRAMEWORK_NAME} $PRODUCT_ID.sim
/usr/bin/lipo -create $PRODUCT_ID.dev $PRODUCT_ID.sim -output $PRODUCT_ID
mv $PRODUCT_ID ${IPHONEOS_FRAMEWORK_LOCATION}/${FRAMEWORK_NAME}
rm $PRODUCT_ID.dev
rm $PRODUCT_ID.sim
cp -r ${IPHONESIMULATOR_FRAMEWORK_LOCATION}/Modules ${IPHONEOS_FRAMEWORK_LOCATION}