#!/bin/sh

rm -rf i686-pc-linux-gnu-dbg
mkdir  i686-pc-linux-gnu-dbg

pushd $HOME/code2/src/plconfig
  sh autogen.sh
popd

pushd i686-pc-linux-gnu-dbg
  $HOME/code2/src/plconfig/configure \
    --disable-opt \
    --with-compiler=GNU \
    --prefix=/usr \
    --datadir=/usr/share \
    --with-plsrc=$HOME/code2/src/pipeline
popd
