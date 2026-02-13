SUMMARY = "Kinara SDK - development libraries for cross-compilation"
DESCRIPTION = "Installs Kinara Ara-2 -dev and -staticdev packages \
into the SDK toolchain sysroot for cross-compilation support."

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    ara2-dev \
    ara2-staticdev \
"
