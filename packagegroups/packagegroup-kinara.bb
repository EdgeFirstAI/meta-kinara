SUMMARY = "Kinara Ara-2 NPU support"
DESCRIPTION = "Installs an Ara-2 runtime, Python bindings, and development \
libraries for NPU inference. KINARA_ARA2_PROVIDER selects which runtime."

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# "nxp" names a recipe from meta-imx-ml. Without that layer there is nothing
# for it to select and this layer's runtime is the only one available, so
# fall back rather than fail a build that was correct before the switch
# existed.
python () {
    if d.getVar('KINARA_ARA2_PROVIDER') != 'nxp':
        return
    if 'imx-machine-learning' in (d.getVar('BBFILE_COLLECTIONS') or '').split():
        return
    bb.note('meta-kinara: no NXP ARA-2 packaging in this build, '
            'using KINARA_ARA2_PROVIDER = "kinara"')
    d.setVar('KINARA_ARA2_PROVIDER', 'kinara')
}

# Only the "kinara" provider adds a runtime here; the NXP one arrives
# through packagegroup-imx-ml. edgefirst-ara2 is installed either way,
# because it depends on ara2-runtime, which both packagings provide.
ARA2_RUNTIME_PKGS = "${@bb.utils.contains('KINARA_ARA2_PROVIDER', 'kinara', 'ara2 ara2-python', '', d)}"

RDEPENDS:${PN} = " \
    ${ARA2_RUNTIME_PKGS} \
    edgefirst-ara2 \
"
