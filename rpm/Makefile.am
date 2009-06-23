
noinst_DATA = \
  RPMS/i686/plconfig-@VERSION@-1.i686.rpm 

RPMS/i686/plconfig-@VERSION@-1.i686.rpm: SPECS/plconfig-@VERSION@.spec rpmrc rpmmacros
	rpmbuild --rcfile rpmrc -bb --target noarch SPECS/plconfig-@VERSION@.spec

SPECS/plconfig-@VERSION@.spec: rpm-prep plconfig.spec
	cp -f plconfig.spec $@

rpm-prep: rpm-mkdirs rpm-install

rpm-mkdirs:
	mkdir -p BUILD RPMS SPECS

rpm-install:
	DESTDIR=`pwd`/BUILD $(MAKE) -C ../src install
	DESTDIR=`pwd`/BUILD $(MAKE) -C ../docs install


install: all
	mkdir -p ../../../../releases/pipeline
	$(INSTALL_DATA) RPMS/noarch/plconfig-@VERSION@-1.noarch.rpm \
          ../../../../releases/pipeline
	$(INSTALL_DATA) RPMS/noarch/plconfig-@VERSION@-1.noarch.rpm \
          $(PIPELINE_SOURCE_ROOT)/plconfig
	$(INSTALL_DATA) ../src/java/us/temerity/plconfig/crypto/CryptoApp.jar \
          $(PIPELINE_SOURCE_ROOT)/plconfig
	$(INSTALL_DATA) RPMS/noarch/plconfig-@VERSION@-1.noarch.rpm \
          $(TEMERITY_SOURCE_ROOT)/www/products/pipeline/plconfig

clean: 
	rm -rf BUILD RPMS SPECS
	DESTDIR=`pwd`/BUILD $(MAKE) -C .. clean

