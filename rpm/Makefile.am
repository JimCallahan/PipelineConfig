
noinst_DATA = \
  RPM/i686/plconfig-@VERSION@-1.i686.rpm



RPM/i686/plconfig-@VERSION@-1.i686.rpm: SPECS/plconfig-@VERSION@.spec
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
	rm -f RPM/i686/plconfig-@VERSION@-1.rpm
