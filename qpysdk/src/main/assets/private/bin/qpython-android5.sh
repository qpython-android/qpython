#!/system/bin/sh
DIR=${0%/*}
. $DIR/init.sh && $DIR/python-android5 "$@" && $DIR/end.sh

