
noinst_DATA = \
  RPMS/i686/plconfig-@VERSION@-1.i686.rpm \
  plconfig-@VERSION@-i686-rpm.bin


plconfig-@VERSION@-i686-rpm.bin: RPMS/i686/plconfig-@VERSION@-1.i686.rpm header
	rm -f $@
	cat header RPMS/i686/plconfig-@VERSION@-1.i686.rpm > $@
	chmod 555 $@

header: RPMS/i686/plconfig-@VERSION@-1.i686.rpm \
          headerA headerB $(top_srcdir)/docs/legal/@LICENSE_DOC@.txt
	rm -f $@
	@echo "Building Install Header..."
	@(cd RPMS/i686; \
          checksum=`md5sum plconfig-@VERSION@-1.i686.rpm`; \
          cd ../../; \
          hlen=`wc -l $(top_srcdir)/docs/legal/@LICENSE_DOC@.txt \
                | awk '{ print($$1+70) }'`; \
          cat $(srcdir)/headerA > header; \
          cat $(top_srcdir)/docs/legal/@LICENSE_DOC@.txt >> header; \
          sed -e "s/@CHECKSUM@/$$checksum/g" headerB | \
          sed -e "s/@HEADER_LENGTH@/$$hlen/g" >> header)

RPMS/i686/plconfig-@VERSION@-1.i686.rpm: SPECS/plconfig-@VERSION@.spec
	rpmbuild --rcfile rpmrc -bb SPECS/plconfig-@VERSION@.spec

SPECS/plconfig-@VERSION@.spec: rpm-prep plconfig.spec
	cp -f plconfig.spec $@

rpm-prep: rpm-mkdirs rpm-install

rpm-mkdirs:
	mkdir -p BUILD RPMS SPECS

rpm-install:
	DESTDIR=`pwd`/BUILD $(MAKE) -C ../src install
	DESTDIR=`pwd`/BUILD $(MAKE) -C ../docs install


install: all
	$(INSTALL_PROGRAM) plconfig-@VERSION@-i686-rpm.bin \
          $(PIPELINE_SOURCE_ROOT)/plconfig
	$(INSTALL_DATA) ../src/java/us/temerity/plconfig/crypto/CryptoApp.class \
          $(PIPELINE_SOURCE_ROOT)/plconfig

clean: 
	rm -rf BUILD RPMS SPECS
	rm -f header plconfig-@VERSION@-i686-rpm.bin
	DESTDIR=`pwd`/BUILD $(MAKE) -C .. clean


