
htmldir = @datadir@/doc/plconfig-@VERSION@

html_DATA = \
  plconfig.html \
  plid.html


$(html_DATA): %.html: %.src.html copyright
	@echo Adding legal notices to: $@
	@awk -- '/@COPYRIGHT@/ { system("cat copyright") } \
               !/@COPYRIGHT@/ { print }' $< > $@

%: ../legal/%.html
	@(cnt=`wc -l $< | awk '{print $$1}'`; \
         tail --lines=+9 $< | head --lines=$$(($$cnt-10)) > $@)

clean:
	rm -f $(html_DATA) 
	rm -f copyright

