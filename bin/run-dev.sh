#!/bin/bash

MODULE_NAME="zuul-oaas-main"
PROJECT_DIR="$(cd ../"$(dirname "$0")" && pwd)"
TARGET_DIR="${PROJECT_DIR}/${MODULE_NAME}/target"

MVN_HELP_PLUGIN='org.apache.maven.plugins:maven-help-plugin:2.2'

JAVA="${JAVA:-java}"
MAVEN="${MAVEN:-mvn}"

HEAP_SIZE="${HEAP_SIZE:-64}"
PERM_SIZE="${PERM_SIZE:-64}"
JAVA_OPTS="${JAVA_OPTS:--Xms${HEAP_SIZE}M -Xmx${HEAP_SIZE}M \
    -XX:PermSize=${PERM_SIZE}m -XX:MaxPermSize=${PERM_SIZE}m}"

ZUUL_OPTS="--spring.profiles.active=dev $@"


##########  F u n c t i o n s  ##########

fail() {
    echo "$1" >&2
    exit 1
}

version=
resolve_version() {
    local mvn_args="${MVN_HELP_PLUGIN}:evaluate -Dexpression=project.version"

    # run in offline mode to be quick
    version="$($MAVEN -o $mvn_args | grep -v '^\[')"
    if [ $? != 0 ]; then
        # try to run in online mode
        version="$($MAVEN $mvn_args | grep -v '^\[')"
    fi
    test -n "$version"; return $?
}

build() {
    $MAVEN package -Pexec-jar -DskipTests=true; return $?
}

start() {
    $JAVA $JAVA_OPTS -jar "${TARGET_DIR}/${MODULE_NAME}-${version}.jar" $ZUUL_OPTS
    return $?
}


##########  M a i n  ##########

cd $PROJECT_DIR || fail "Failed to cd into ${PROJECT_DIR}"

if ! which "$MAVEN" &>/dev/null; then
    fail "You don't have Maven installed, or not on PATH. Try to set MAVEN env variable to the path of mvn binary."
fi

echo -n "Resolving project version... "
resolve_version || fail "failed."
echo "done."

if [ ! -f "${TARGET_DIR}/zuul-oaas-main-${version}.jar" ]; then
    echo "JAR not found, building ${version}..."
    build || "Failed to build"
fi

echo "Starting Zuul OAAS ${version}..."
start || failed "Failed to start"
