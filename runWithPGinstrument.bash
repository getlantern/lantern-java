#!/usr/bin/env bash
export javaArgs="-Xmx2048m -XX:MaxPermSize=512m -Dpginstrument.debug=false -javaagent:../pginstrument/build/libs/pginstrument-0.1.0-shadow.jar -Xbootclasspath/a:../pginstrument/build/libs/pginstrument-0.1.0-shadow.jar $javaArgs"
./quickRun.bash $*