#!/bin/sh
find . -name '*.java' | xargs xgettext -k -ktrc:1c,2 -kmarktrc:1c,2 -ktr -kmarktr -ktrn:1,2 -ktrnc:1c,2,3
mv -f messages.po i18n
# Backup
cd i18n
mv -f ja.po ja.po.prev
mv -f en.po en.po.prev
msgmerge ja.po.prev messages.po -o ja.po
msgmerge en.po.prev messages.po -o en.po
cd -
echo 'Edit i18n/*.po and exec i18n_deploy.sh'
