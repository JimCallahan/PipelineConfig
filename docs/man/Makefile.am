
htmldir = @datadir@/doc/plconfig-@VERSION@

html_DATA = \
  plconfig.html \
  plid.html


noinst_DATA = \
  checkbot.html


$(html_DATA): %.html: %.src.html copyright
	@echo Adding legal notices to: $@
	@awk -- '/@COPYRIGHT@/ { system("cat copyright") } \
               !/@COPYRIGHT@/ { print }' $< > $@

%: ../legal/%.html
	@(cnt=`wc -l $< | awk '{print $$1}'`; \
         tail --lines=+9 $< | head --lines=$$(($$cnt-10)) > $@)


checkbot.html: $(html_DATA)
	rm -f checkbot.html checkbot-localhost.html
	@CHECKBOT@ `pwd`/*.html


clean:
	rm -f $(html_DATA) 
	rm -f copyright 
	rm -f checkbot.html checkbot-localhost.html

