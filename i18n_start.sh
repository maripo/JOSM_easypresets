#!/bin/sh
cd src
find . -name '*.java' | xargs xgettext -k -ktrc:1c,2 -kmarktrc:1c,2 -ktr -kmarktr -ktrn:1,2 -ktrnc:1c,2,3
mv -f messages.po ../i18n
cd ..
# Backup
cd i18n
mv -f ja.po ja.po.prev
mv -f en.po en.po.prev
mv -f hu.po hu.po.prev
msgmerge ja.po.prev messages.po -o ja.po
msgmerge en.po.prev messages.po -o en.po
msgmerge hu.po.prev messages.po -o hu.po
cd -
echo 'Edit i18n/*.po and exec i18n_deploy.sh'
