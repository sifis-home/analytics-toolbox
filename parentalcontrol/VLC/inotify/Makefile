# $Id$
# Author: Luis Colorado <luiscoloradourcola@gmail.com>
# Date: jue nov  5 20:13:03 EET 2015
# Disclaimer: (C) 2015 LUIS COLORADO SISTEMAS S.L.U.
# 			All rights reserved.


outdir = ../bin

targets = $(outdir)/libINotify.so

ARCH = linux

CFLAGS=-I${JAVA_JDK}/include -I${JAVA_JDK}/include/${ARCH} -fPIC # -DDEBUG=1

.SUFFIXES: all clean

all: $(targets)
clean:
	$(RM) $(targets) $(foreach i, $(targets), $($(i)_objs))

$(outdir)/libINotify.so_objs = INotify.o # fprintbuf.o # <--- use only if -DDEBUG=1 in CFLAGS
$(outdir)/libINotify.so_libs =
$(outdir)/libINotify.so: $($(outdir)/libINotify.so_objs)
	$(CC) $(LDFLAGS) -shared -o $@ $($@_objs) $($@_ldflags) $($@_libs)

INotify.o: es_lcssl_linux_inotify_INotify.h

es_lcssl_linux_inotify_INotify.h: /home/giacomo/workspace_inotify/inotify/src/main/java/it/cnr/iit/inotify/INotify.java
	javah it.cnr.iit.inotify.INotify

fprintbuf.o: fprintbuf.h
