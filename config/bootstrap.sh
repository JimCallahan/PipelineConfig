#!/bin/sh

rm -rf debug opt
mkdir  debug opt

plsrcdir=$HOME/code-dimetrodon/src/plconfig

pushd $plsrcdir
  sh autogen.sh
popd

pushd debug
  $plsrcdir/configure \
    --disable-opt \
    --with-compiler=GNU \
    --prefix=/usr \
    --datadir=/usr/share \
    --with-pipeline=$plsrcdir/../pipeline \
    --with-temerity=$HOME/code/src/temerity
popd

pushd opt
  $plsrcdir/configure \
    --enable-opt \
    --with-compiler=GNU \
    --prefix=/usr \
    --datadir=/usr/share \
    --with-pipeline=$plsrcdir/../pipeline \
    --with-temerity=$HOME/code/src/temerity
popd
