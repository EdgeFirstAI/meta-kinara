SUMMARY = "Kinara Ara-2 NPU support"
DESCRIPTION = "Installs the Ara-2 runtime, plus the edgefirst-ara2 Python \
bindings when meta-edgefirst is in the build."

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# edgefirst-ara2 lives in meta-edgefirst, which this layer does not depend on.
RDEPENDS:${PN} = " \
    imx-nxp-ara2 \
    ${@bb.utils.filter('BBFILE_COLLECTIONS', 'meta-edgefirst', d) and 'edgefirst-ara2' or ''} \
"
