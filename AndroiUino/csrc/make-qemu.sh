#bin/sh

echoerr() { echo "> ERROR: $@" 1>&2; }

echo "Compiling for QEMU..."

if [ ! -f src/main.c ]
then
	echoerr "File 'main.c' not found !"
	exit 1
fi

cd target-qemu/
make $@