#!/system/bin/sh
DIR=${0%/*}
. $DIR/init.sh && $DIR/python "$@" && $DIR/end.sh

