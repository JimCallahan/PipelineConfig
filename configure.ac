dnl Process this file with autoconf to produce a configure script.

dnl ------------------------------------------------------------------------------------------
dnl   Initialization.
dnl ------------------------------------------------------------------------------------------

AC_INIT
AC_CONFIG_SRCDIR([AUTHORS])
AC_CONFIG_AUX_DIR(config)

AM_CONFIG_HEADER(config.h)
AM_INIT_AUTOMAKE(PipelineConfig, 2.3.2)

AC_MSG_RESULT([ ])


dnl ------------------------------------------------------------------------------------------
dnl   Configuration options.
dnl ------------------------------------------------------------------------------------------

AC_MSG_CHECKING([C++ compiler vendor (version)])

AC_ARG_WITH([compiler], 
            [  --with-compiler=TYPE        C/C++ compiler type: GNU, SGI or KAI.])

AR="ar"

AC_SUBST(EXPORT_DYNAMIC)
EXPORT_DYNAMIC=""

AC_SUBST(DLOPEN_LIB)
DLOPEN_LIB=""

AC_SUBST(GEN_LIB)
GEN_LIB=""

CXX_BRAND=""

case "$with_compiler" in 
  (gnu | GNU | "") \
     CC="gcc";
     CFLAGS="";
     CXX="g++";
     CXXFLAGS="";
     EXPORT_DYNAMIC="-Wl,-export-dynamic";
     DLOPEN_LIB="-ldl";
     AR="ar ";
     CXX_VERSION=`${CXX} --version | head -1 | awk '{print $3}'`;
     if test -z "$CXX_VERSION"; then CXX_VERSION=`${CXX} --version`; fi;
     AC_DEFINE(USING_GNU_CXX, 1, 
               [Define if using GNU's C++ compiler (g++).]);;

  (sgi | SGI) \
     CC="/bin/cc";
     CFLAGS="";
     CXX="/bin/CC";
     CXXFLAGS="-ptnone -prelink -ptv -Wl,-LD_MSG:off=15";
     GEN_LIB="-lgen";
     AR="$CXX -ar -o";
     AC_DEFINE(USING_MIPSPRO_CXX, 1, 
               [Define if using SGI's MIPSPRO C++ compiler (CC).]);;

  (kai | KAI) \
     CFLAGS="";
     CXX="KCC";
     CXXFLAGS="--one_per";
     AR="$CXX -o"; 
     AC_DEFINE(USING_KAI_CXX, 1, 
               [Define if using the KAI C++ compiler (KCC).]);;

  (*) \
     AC_MSG_ERROR([unknown compiler type]);;
esac

AC_MSG_RESULT([$CXX ($CXX_VERSION)])
AC_MSG_RESULT([ ])


dnl ------------------------------------------------------------------------------------------

AC_MSG_CHECKING([Pipeline Source Code Directory])

AC_ARG_WITH([plsrc], 
            [  --with-plsrc=DIR             Root directory of the Pipeline source tree.])

AC_SUBST(PIPELINE_SOURCE_ROOT)
PIPELINE_SOURCE_ROOT=

if test "$with_plsrc" 
then
  PIPELINE_SOURCE_ROOT=$with_plsrc
else
  AC_MSG_ERROR([The --with-plsrc option is required.])
fi

AC_MSG_RESULT([$PIPELINE_SOURCE_ROOT])


dnl ------------------------------------------------------------------------------------------
dnl   Build Information.         
dnl ------------------------------------------------------------------------------------------

AC_MSG_RESULT([Build Information: ])

AC_MSG_CHECKING([Package])
AC_MSG_RESULT([$PACKAGE])

AC_MSG_CHECKING([Version])
AC_MSG_RESULT([$VERSION])

AC_MSG_CHECKING([Release])
AC_SUBST(RELEASE_STAMP)
RELEASE_STAMP=`date`
AC_MSG_RESULT([$RELEASE_STAMP])



dnl ------------------------------------------------------------------------------------------

AC_MSG_CHECKING([toolset])
AC_SUBST(BUILD_TOOLSET)
BUILD_TOOLSET=$TOOLSET
AC_MSG_RESULT([$TOOLSET])


AC_MSG_CHECKING([path])
AC_MSG_RESULT([$PATH])


AC_MSG_RESULT([ ])


dnl ------------------------------------------------------------------------------------------

AC_SUBST(JAVA_C_FLAGS)
JAVA_C_FLAGS=""

AC_SUBST(JAVA_RUNTIME_FLAGS)
JAVA_RUMTIME_FLAGS=""

AC_SUBST(JNI_LDADD)
JNI_LDADD=""

AC_SUBST(JNI_LDFLAGS)
JNI_LDFLAGS=""

AC_SUBST(EXTRA_DEBUG_SUBDIRS)
EXTRA_DEBUG_SUBDIRS=""

AC_SUBST(YXXFLAGS)
YXXFLAGS=""

AC_SUBST(BUILD_MODE)
BUILD_MODE="dbg"

AC_ARG_ENABLE([opt], 
              [  --enable-opt              Use optimization options.])

if test "$enable_opt" = "yes" 
then 
  case "$with_compiler" in 
    (kai | KAI) \
       CXXFLAGS="$CXXFLAGS +K3 -O3";;

    (sgi | SGI) \
       CXXFLAGS="$CXXFLAGS -O2 -OPT:Olimit=8192";;

    (gnu | GNU | "") \
       CXXFLAGS="$CXXFLAGS -O2";
       JNI_LDFLAGS="-shared ";;

    (*) \
       CXXFLAGS="$CXXFLAGS -O2";;
  esac

  JAVA_C_FLAGS="-source 1.5 -Xlint:all,-serial,-depreciation,-unchecked"
  JAVA_RUNTIME_FLAGS="-disableassertions"

  CXXFLAGS="$CXXFLAGS -DNDEBUG"
  EXTRA_DEBUG_SUBDIRS=""
  BUILD_MODE="opt"
else
  case "$with_compiler" in 
    (kai | KAI) \
       CXXFLAGS="$CXXFLAGS +K0 -g";;

    (sgi | SGI) \
       CXXFLAGS="$CXXFLAGS -g";;

    (gnu | GNU | "") \
       CXXFLAGS="$CXXFLAGS -g";
       JNI_LDFLAGS="-shared ";;

    (*) \
       CXXFLAGS="$CXXFLAGS -g";;
  esac

  JAVA_C_FLAGS="-g -source 1.5 -Xlint:all,-depreciation,-unchecked"
  JAVA_RUNTIME_FLAGS="-enableassertions"

  EXTRA_DEBUG_SUBDIRS="tests"
  BUILD_MODE="dbg"
fi


dnl ------------------------------------------------------------------------------------------

AC_ARG_ENABLE([prof], 
              [  --enable-prof             Use profiling options.])

if test "$enable_prof" = "yes" 
then 
  case "$with_compiler" in 
    (gnu | GNU | "") \
       CXXFLAGS="$CXXFLAGS -pg";
       BUILD_MODE="prof";;

    (*) \
       AC_MSG_ERROR([don't know how to profile with this compiler]);;
  esac
fi


dnl ------------------------------------------------------------------------------------------

AC_ARG_ENABLE([64], 
              [  --enable-64               Generate 64 bit objects and executables.])

if test "$enable_64" = "yes" 
then 
  case "$with_compiler" in 
    (kai | KAI) \
       CFLAGS="$CFLAGS -64 -mips4 --backend -TARG:platform=IP30";
       CXXFLAGS="$CXXFLAGS -64 -mips4 --backend -TARG:platform=IP30";;

    (sgi | SGI) \
       CFLAGS="$CFLAGS -64 -mips4 -TARG:platform=IP30";
       CXXFLAGS="$CXXFLAGS -64 -mips4 -TARG:platform=IP30";;

    (gnu | GNU) \
       AC_MSG_ERROR([no 64bit support for gcc]);;

    (*) \
       AC_MSG_ERROR([don't know how to support 64bit on this compiler]);;
  esac
else 
  case "$with_compiler" in 
    (kai | KAI | sgi | SGI) \
       CFLAGS="$CFLAGS -n32 -mips3";
       CXXFLAGS="$CXXFLAGS -n32 -mips3";;
  esac
fi


dnl ------------------------------------------------------------------------------------------

AC_SUBST(PTHREAD_LIB)
PTHREAD_LIB=""

AC_ARG_ENABLE([threads], 
              [  --enable-threads          Multitask with Posix threads.])

if test "$enable_threads" = "yes" 
then  
  AC_DEFINE(USING_PTHREADS, 1, 
            [Define if using Posix threads.])

  CFLAGS="$CFLAGS -D_REENTRANT"
  CXXFLAGS="$CXXFLAGS -D_REENTRANT"
  PTHREAD_LIB="-lpthread"
fi


dnl ------------------------------------------------------------------------------------------

AC_SUBST(RPM_ROOT)
RPM_ROOT=`pwd`/rpm


dnl ------------------------------------------------------------------------------------------
dnl   Checks for programs.
dnl ------------------------------------------------------------------------------------------

dnl -- STANDARD C/C++ TOOLS ------------------------------------------------------------------

AC_MSG_RESULT([The C++ compiler environment:])

AC_LANG([C++])

AC_PROG_CC
AC_PROG_CXX
AC_PROG_CXXCPP
AC_SUBST(AR)
AC_CANONICAL_HOST

AC_HEADER_STDC
AC_CHECK_HEADER([sys/types.h])    
AC_CHECK_HEADER([sys/stat.h])     
AC_CHECK_HEADER([strings.h])      
AC_CHECK_HEADER([unistd.h])       

AC_MSG_RESULT([ ])



dnl -- SIZEOF C/C++ TYPES --------------------------------------------------------------------

AC_MSG_RESULT([Sizes of atomic C++ types:])

AC_CHECK_SIZEOF(char, 1)
AC_CHECK_SIZEOF(short, 2)
AC_CHECK_SIZEOF(int, 4)
AC_CHECK_SIZEOF(long, 4)
AC_CHECK_SIZEOF(long long, 8)

AC_CHECK_SIZEOF(float, 4)
AC_CHECK_SIZEOF(double, 8)
AC_CHECK_SIZEOF(long double, 12)

AC_MSG_RESULT([ ])



dnl -- COMMON SUPPORT TOOLS ------------------------------------------------------------------

AC_MSG_RESULT([Locating common support programs:])

AC_PATH_PROG(RANLIB, ranlib, echo, $PATH)
AC_PROG_INSTALL

unset BASH
AC_PATH_PROG(BASH, bash, $PATH)
AC_PATH_PROG(MAKE, make, $PATH)
AC_PATH_PROG(CVS, cvs, $PATH)
AC_PATH_PROG(CHMOD, chmod, $PATH)
AC_PATH_PROG(CHOWN, chown, $PATH)
AC_PATH_PROG(DIFF, diff, $PATH)
AC_PATH_PROG(XDIFF, xdiff, $PATH)
AC_PATH_PROG(KILL, kill, $PATH)
AC_PATH_PROG(SSH, ssh, $PATH)
AC_PATH_PROG(ID, id, $PATH)
AC_PATH_PROG(DF, df, $PATH) 
AC_PATH_PROG(MOZILLA, mozilla, $PATH)
AC_PATH_PROG(CHECKBOT, checkbot, echo, $PATH)

AC_MSG_RESULT([ ])



dnl -- IMAGE MAJICK --------------------------------------------------------------------------

AC_MSG_RESULT([Locating Image tools:])
AC_PATH_PROG(CONVERT, convert, $PATH)

AC_MSG_RESULT([ ])



dnl -- JAVA TOOLS ----------------------------------------------------------------------------

AC_MSG_RESULT([Locating the Java development tools:])

AC_PATH_PROG(JAVA,    java,    $PATH)
AC_PATH_PROG(JAVA_C,  javac,   $PATH)
AC_PATH_PROG(JAVA_CC, javacc,  $PATH)
AC_PATH_PROG(JAVA_H,  javah,   $PATH)
AC_PATH_PROG(JAVADOC, javadoc, $PATH)
AC_PATH_PROG(JAR,     jar,     $PATH)


AC_MSG_CHECKING([java include])

AC_SUBST(JAVA_INC)
JAVA_INC=`dirname $JAVA`/../include

AC_MSG_RESULT([$JAVA_INC])

AC_MSG_RESULT([ ])



dnl ------------------------------------------------------------------------------------------
dnl   Checks for libraries.	
dnl ------------------------------------------------------------------------------------------


dnl ------------------------------------------------------------------------------------------
dnl   Checks for header files.
dnl ------------------------------------------------------------------------------------------

AC_MSG_RESULT([Locating C system headers:])

AC_CHECK_HEADERS([math.h errno.h pwd.h dlfcn.h libgen.h time.h dirent.h ctype.h])
AC_CHECK_HEADERS([fcntl.h fam.h select.h signal.h])      
AC_CHECK_HEADERS([sys/time.h sys/param.h sys/utsname.h sys/wait.h sys/resource.h])  

AC_MSG_RESULT([ ])


AC_MSG_RESULT([Locating Basic C++ headers:])

AC_CHECK_HEADERS([float.h])
AC_CHECK_HEADERS([cassert assert.h], [break])
AC_CHECK_HEADERS([climits limits.h], [break])
AC_CHECK_HEADERS([cstdlib stdlib.h], [break])
AC_CHECK_HEADERS([cstdio stdio.h], [break])
AC_CHECK_HEADERS([cstring string.h], [break])
AC_CHECK_HEADERS([iostream iostream.h], [break])
AC_CHECK_HEADERS([fstream fstream.h], [break])
AC_CHECK_HEADERS([sstream], [break])
AC_CHECK_HEADERS([iomanip iomanip.h], [break])
AC_CHECK_HEADERS([iterator])
AC_CHECK_HEADERS([algorithm algobase algobase.h], [break])

AC_MSG_RESULT([ ])


AC_MSG_RESULT([Locating STL headers:])

AC_CHECK_HEADERS([hash_map ext/hash_map], [break])
AC_CHECK_HEADERS([slist ext/slist], [break])
AC_CHECK_HEADERS([stack deque list set vector])

AC_MSG_RESULT([ ])


dnl ------------------------------------------------------------------------------------------
dnl   Checks for typedefs.
dnl ------------------------------------------------------------------------------------------


dnl ------------------------------------------------------------------------------------------
dnl   Checks for structures.
dnl ------------------------------------------------------------------------------------------


dnl ------------------------------------------------------------------------------------------
dnl   Checks for compiler characteristics.
dnl ------------------------------------------------------------------------------------------


dnl ------------------------------------------------------------------------------------------
dnl   Checks for library functions.
dnl ------------------------------------------------------------------------------------------


dnl ------------------------------------------------------------------------------------------
dnl   Checks for system services.
dnl ------------------------------------------------------------------------------------------


dnl ------------------------------------------------------------------------------------------
dnl   Miscellaneous stuff.
dnl ------------------------------------------------------------------------------------------

AC_DEFINE_UNQUOTED(TEST_DATA_DIR, ["${srcdir}/data"], 
                   [Directory to find data files used by regression tests])

AC_SUBST(BUILD_NAME)
BUILD_NAME="${host}-${BUILD_MODE}"
AC_DEFINE_UNQUOTED(BUILD_NAME, "$BUILD_NAME", 
                   [Define a unique host architecture and build mode string.])


dnl ------------------------------------------------------------------------------------------
dnl   Output files.
dnl ------------------------------------------------------------------------------------------

AC_MSG_RESULT([Cooking configurable files:])

AC_CONFIG_FILES([
  Makefile

  docs/Makefile 
  docs/javadoc/Makefile
  docs/man/Makefile

  src/Makefile
  src/java/Makefile
  src/java/us/Makefile
  src/java/us/temerity/Makefile
  src/java/us/temerity/plconfig/Makefile
  src/java/us/temerity/plconfig/PackageInfo.java
  src/java/us/temerity/plconfig/tests/Makefile
  src/java/us/temerity/plconfig/tests/TestInfo.java
  src/java/us/temerity/plconfig/crypto/Makefile
  src/java/us/temerity/plconfig/crypto/CryptoApp.java.in

  src/java/scripts/Makefile
  src/java/scripts/plconfig
  src/java/scripts/plid

  rpm/Makefile
  rpm/plconfig.spec
  rpm/rpmrc
  rpm/rpmmacros
])

AC_OUTPUT


