all: water_flow_module dam types.h

SMALL=1

CONTIKI=$(HOME)/contiki
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"


APPS += er-coap
APPS += rest-engine
APPS += json

CONTIKI_WITH_IPV6 = 1
include $(CONTIKI)/Makefile.include 
