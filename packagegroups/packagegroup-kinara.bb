SUMMARY = "Kinara Ara-2 NPU support"
DESCRIPTION = "Installs the Kinara Ara-2 runtime, Python bindings, and \
development libraries for NPU inference."

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    ara2 \
    ara2-python \
"
