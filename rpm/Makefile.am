
noinst_DATA = \
  RPMS/i686/plconfig-@VERSION@-1.i686.rpm \
  plconfig-@VERSION@-i686-rpm.bin


plconfig-@VERSION@-i686-rpm.bin: RPMS/i686/plconfig-@VERSION@-1.i686.rpm header
	rm -f $@
	cat header RPMS/i686/plconfig-@VERSION@-1.i686.rpm > $@
	chmod 555 $@

header: RPMS/i686/plconfig-@VERSION@-1.i686.rpm install-header
	(cd RPMS/i686; checksum=`md5sum plconfig-@VERSION@-1.i686.rpm`; cd ../../; \
         sed -e "s/@CHECKSUM@/$$checksum/g" install-header > header)

RPMS/i686/plconfig-@VERSION@-1.i686.rpm: SPECS/plconfig-@VERSION@.spec
	rpmbuild --rcfile rpmrc -bb SPECS/plconfig-@VERSION@.spec

SPECS/plconfig-@VERSION@.spec: rpm-prep plconfig.spec
	cp -f plconfig.spec $@

rpm-prep: rpm-mkdirs rpm-install

rpm-mkdirs:
	mkdir -p BUILD RPMS SOURCES SPECS SRPMS

rpm-install:
	DESTDIR=`pwd`/BUILD $(MAKE) -C ../src install
	DESTDIR=`pwd`/BUILD $(MAKE) -C ../docs install

clean:
	rm -f SPECS/plconfig-@VERSION@.spec 
	rm -f RPMS/i686/plconfig-@VERSION@-1.rpm

