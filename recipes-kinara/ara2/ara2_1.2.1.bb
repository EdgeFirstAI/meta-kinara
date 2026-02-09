DESCRIPTION = "Kinara Ara-2 Runtime for Linux"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://art/LICENSE.txt;md5=609f4b4e754be5a7000f9af9a2d0971e"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    ${KINARA_MIRROR}/ara2-runtime-r${PV}.tar.bz2 \
    file://ara2.service \
    file://ara2.yaml \
    file://uiodma.sh \
"
SRC_URI[sha256sum] = "1fbe03b4db1c099456f2e3d2808866f4bd8a673da650c82e982f6ee785e8fc4a"

PACKAGES = "${PN}-python ${PN}-dev ${PN}-staticdev ${PN}-dbg ${PN}"

DEPENDS = "python3"
RDEPENDS:${PN} = "kernel-module-uiodma"
RDEPENDS:${PN}-python = "python3"

python do_fetch:prepend() {
    mirror = d.getVar('KINARA_MIRROR')
    if not mirror or 'KINARA_MIRROR_NOT_SET' in mirror:
        bb.fatal(
            "KINARA_MIRROR is not set. The Kinara Ara-2 runtime requires NDA access.\n"
            "If you have access, set the download mirror in your local.conf:\n"
            "    KINARA_MIRROR = \"https://<mirror-url-from-kinara>\"\n"
            "Contact Kinara for access: https://www.kinara.ai/"
        )
}

S = "${WORKDIR}/ara2-runtime-r${PV}"

inherit features_check systemd python3-dir

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}
    install -d ${D}${libexecdir}/ara2

    if [ "${UNPACKDIR}" != "" ]; then
        install -m 0644 ${UNPACKDIR}/ara2.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/ara2.yaml ${D}${sysconfdir}/ara2.yaml
        install -m 0755 ${UNPACKDIR}/uiodma.sh ${D}${libexecdir}/ara2
    else
        install -m 0644 ${WORKDIR}/ara2.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/ara2.yaml ${D}${sysconfdir}/ara2.yaml
        install -m 0755 ${WORKDIR}/uiodma.sh ${D}${libexecdir}/ara2
    fi

    install -m 0755 ${S}/art/linux/${TARGET_ARCH}/proxy/proxy_${TARGET_ARCH} ${D}${libexecdir}/ara2/proxy

    install -m 0755 ${S}/art/linux/${TARGET_ARCH}/hw_utils/bins/active_enable_${TARGET_ARCH} ${D}${libexecdir}/ara2/active_enable
    install -m 0755 ${S}/art/linux/${TARGET_ARCH}/hw_utils/bins/ddr_mem_united_config_${TARGET_ARCH} ${D}${libexecdir}/ara2/ddr_mem_united_config
    install -m 0755 ${S}/art/linux/${TARGET_ARCH}/hw_utils/bins/program_flash_${TARGET_ARCH} ${D}${libexecdir}/ara2/program_flash
    install -m 0755 ${S}/art/linux/${TARGET_ARCH}/hw_utils/bins/program_pll_${TARGET_ARCH} ${D}${libexecdir}/ara2/program_pll
    install -m 0755 ${S}/art/linux/${TARGET_ARCH}/hw_utils/bins/chip_info_${TARGET_ARCH} ${D}${libexecdir}/ara2/chip_info
    
    install -d ${D}${bindir}
    ln -sf ../libexec/ara2/chip_info ${D}${bindir}/ara2-info

    install -d ${D}${datadir}/ara2
    install -m 0644 ${S}/art/linux/${TARGET_ARCH}/hw_utils/ddr_config/ddr_cfg_31.bin ${D}${datadir}/ara2
    install -m 0644 ${S}/art/linux/${TARGET_ARCH}/hw_utils/boot_img_32779/willow_therm.hex ${D}${datadir}/ara2
    install -m 0644 ${S}/art/linux/${TARGET_ARCH}/proxy/mcp0/vpu0.hex ${D}${datadir}/ara2

    install -m 0755 ${S}/art/linux/${TARGET_ARCH}/lib/libaraclient_${TARGET_ARCH}.a ${D}${libdir}/libaraclient.a
    install -m 0755 ${S}/art/linux/${TARGET_ARCH}/lib/libaraclient_${TARGET_ARCH}.so ${D}${libdir}/libaraclient.so.${PV}
    ln -sf libaraclient.so.${PV} ${D}${libdir}/libaraclient.so.1
    ln -sf libaraclient.so.1 ${D}${libdir}/libaraclient.so

    install -d ${D}${includedir}
    install -m 0644 ${S}/art/linux/${TARGET_ARCH}/include/dv_status_codes.h ${D}${includedir}
    install -m 0644 ${S}/art/linux/${TARGET_ARCH}/include/dvapi.h ${D}${includedir}

    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}/kinara
    install -m 0644 ${S}/art/linux/${TARGET_ARCH}/include/dvapi.py ${D}/${PYTHON_SITEPACKAGES_DIR}/kinara
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "ara2.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN}-python += "${PYTHON_SITEPACKAGES_DIR}"
FILES:${PN}-dbg += "${libdir}/.debug"
FILES:${PN}-dev += "${includedir}"
FILES:${PN}-dev += "${libdir}/*.so"
FILES:${PN}-staticdev += "${libdir}/*.a"
FILES:${PN} += "${libdir}/*.so.*"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${datadir}"
FILES:${PN} += "${bindir}"
FILES:${PN} += "${libexecdir}"
