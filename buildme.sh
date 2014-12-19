#!/bin/bash
#
# $Id: buildme,v 1.10 2013/09/16 01:50:09 sjg Exp $
#

rm *.class

rm uim.jar

rm $HOME/lib/uim.jar


export CLASSPATH="$HOME/lib/*:$HOME/lib$HOME/lib/*:$HOME/lib:."

export CPPCMD="cpp -D JVER_1_7 -I "$HOME"/include -P -C -nostdinc -"

javac -Xlint UIMaker.java

jar cf uim.jar *.class

mv uim.jar $HOME/lib/.

rm *.class

javac -Xlint uimtest.java

jar cfe uimtest.jar uimtest uimtest.class sjgpanel.in resources/chalet.jpg

rm test/uimtest.jar
rm test/lib/uim.jar

mv uimtest.jar test/.

