
SUBDIRS = \
  src \
  docs \
  rpm


EXTRA_DIST = \
  autogen.sh \
  config/bootstrap.sh \
  config/config.guess \
  config/config.sub \
  config/depcomp \
  config/install-sh \
  config/missing \
  config/mkinstalldirs



install-src: all
	$(INSTALL_PROGRAM) rpm/plconfig-@VERSION@-i686-rpm.bin \
          $(PIPELINE_SOURCE_ROOT)/plconfig
	$(INSTALL_DATA) src/java/us/temerity/plconfig/crypto/CryptoApp.class \
          $(PIPELINE_SOURCE_ROOT)/plconfig
