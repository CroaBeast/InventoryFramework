#!/usr/bin/env bash
# Installs the Spigot server jars the NMS modules compile against into ~/.m2, skipping the ones already there.
# gradle.yml keys its Spigot cache on this file, so only a change to this list builds a new cache.
set -euo pipefail

mkdir -p buildtools && cd buildtools

# Arguments: JDK home, BuildTools revision, Spigot artifact version, optional --remapped
build() {
  local jar="$HOME/.m2/repository/org/spigotmc/spigot/$3/spigot-$3${4:+-remapped-mojang}.jar"
  if [ -f "$jar" ]; then
    echo "$3 is cached"
    return
  fi
  [ -f BuildTools.jar ] || wget -q https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
  "$1/bin/java" -jar BuildTools.jar --rev "$2" ${4:-}
}

for version in 1.14.4 1.15.2 1.16.1 1.16.3 1.16.5; do
  build "$JAVA_HOME_11_X64" $version $version-R0.1-SNAPSHOT
done
# --rev 1.20, 1.20.5 and 1.21 resolve to 1.20.1, 1.20.6 and 1.21.1; these build numbers pin the original
# releases (3798 = 1.20, 4132 = 1.20.5, 4289 = 1.21)
build "$JAVA_HOME_17_X64" 3798 1.20-R0.1-SNAPSHOT --remapped
for version in 1.17.1 1.18.2 1.19.4 1.20.1 1.20.2 1.20.4; do
  build "$JAVA_HOME_17_X64" $version $version-R0.1-SNAPSHOT --remapped
done
build "$JAVA_HOME_21_X64" 4132 1.20.5-R0.1-SNAPSHOT --remapped
build "$JAVA_HOME_21_X64" 4289 1.21-R0.1-SNAPSHOT --remapped
for version in 1.20.6 1.21.1 1.21.3 1.21.4 1.21.5 1.21.8 1.21.10; do
  build "$JAVA_HOME_21_X64" $version $version-R0.1-SNAPSHOT --remapped
done
build "$JAVA_HOME_21_X64" 1.21.11 1.21.11-R0.2-SNAPSHOT --remapped
build "$JAVA_HOME_25_X64" 26.1.2 26.1.2-R0.1-SNAPSHOT
