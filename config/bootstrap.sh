#!/bin/sh

rm -rf debug opt
mkdir  debug opt

plsrcdir=$HOME/code-dimetrodon/src/plconfig

echo "---------------------------------------------------------------------------------------"
echo "  AUTOGEN: $HOSTNAME"
echo "---------------------------------------------------------------------------------------"

pushd $plsrcdir
  sh autogen.sh
popd


echo 
echo "---------------------------------------------------------------------------------------"
echo "  CONFIGURING (debug): $HOSTNAME"
echo "---------------------------------------------------------------------------------------"


pushd debug
  time \
  JAVA_HOME=/usr/lib64/jvm/java-1.6.0-sun-1.6.0.u7 \
  PATH="$JAVA_HOME/bin:$PATH" \
  $plsrcdir/configure \
    --disable-opt \
    --with-compiler=GNU \
    --prefix=/usr \
    --datadir=/usr/share \
    --with-pipeline=$plsrcdir/../pipeline \
    --with-temerity=$HOME/code/src/temerity
popd


echo 
echo "---------------------------------------------------------------------------------------"
echo "  CONFIGURING (opt): $HOSTNAME"
echo "---------------------------------------------------------------------------------------"

pushd opt
  time \
  JAVA_HOME=/usr/lib64/jvm/java-1.6.0-sun-1.6.0.u7 \
  PATH="$JAVA_HOME/bin:$PATH" \
  $plsrcdir/configure \
    --enable-opt \
    --with-compiler=GNU \
    --prefix=/usr \
    --datadir=/usr/share \
    --with-pipeline=$plsrcdir/../pipeline \
    --with-temerity=$HOME/code/src/temerity
popd
