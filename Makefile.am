
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
	$(INSTALL_DATA) rpm/RPMS/i686/plconfig-1.1.0-1.i686.rpm \
          $(PIPELINE_SOURCE_ROOT)/plconfig
	$(INSTALL_DATA) src/java/us/temerity/plconfig/crypto/CryptoApp.class \
          $(PIPELINE_SOURCE_ROOT)/plconfig
