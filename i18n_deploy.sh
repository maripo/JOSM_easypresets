#!/bin/sh
perl ../../i18n/i18n.pl --basedir=data --potfile=i18n/messages.po i18n/en.po i18n/ja.po i18n/hu.po
ant clean

