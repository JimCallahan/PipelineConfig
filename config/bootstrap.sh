#!/bin/sh

rm -rf i686-pc-linux-gnu-dbg i686-pc-linux-gnu-opt
mkdir  i686-pc-linux-gnu-dbg i686-pc-linux-gnu-opt

plsrcdir=$HOME/code/src/plconfig

pushd $plsrcdir
  sh autogen.sh
popd

pushd i686-pc-linux-gnu-dbg
  $plsrcdir/configure \
    --disable-opt \
    --with-compiler=GNU \
    --prefix=/usr \
    --datadir=/usr/share \
    --with-pipeline=$plsrcdir/../pipeline \
    --with-temerity=$plsrcdir/../temerity
popd

pushd i686-pc-linux-gnu-opt
  $plsrcdir/configure \
    --enable-opt \
    --with-compiler=GNU \
    --prefix=/usr \
    --datadir=/usr/share \
    --with-pipeline=$plsrcdir/../pipeline \
    --with-temerity=$plsrcdir/../temerity
popd
