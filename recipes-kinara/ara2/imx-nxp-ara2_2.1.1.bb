SUMMARY = "NXP RT-SDK ARA2"
DESCRIPTION = "Runtime SDK for AI/ML acceleration with Ara240 NPU on i.MX SoCs"
HOMEPAGE = "https://github.com/nxp-imx/rt-sdk-ara2"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=bc649096ad3928ec06a8713b8d787eac"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PROVIDES += "ara2"
RPROVIDES:${PN} += "ara2 ara2-python"
RPROVIDES:${PN}-dev += "ara2-dev"
RPROVIDES:${PN}-staticdev += "ara2-staticdev"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base"

IMX_SRCREV_ABBREV = "063d56c"
SRC_URI[sha256sum] = "7fcbd5206b0b12358dd4757d4e1cb8785d2db8ce601aecd290634c047b85851a"

inherit fsl-eula2-unpack2 fsl-eula-recent systemd

SRC_URI:append = "\
	file://uiodma.sh \
"

# libaraclient_aarch64 is unversioned, so clear FILES_SOLIBSDEV
# and then explicitly set the libraries in main/dev package
FILES_SOLIBSDEV = ""

FILES:${PN} += " \
    ${libdir}/gstreamer-1.0 \
    ${libdir}/libaraclient_aarch64.so \
    ${libdir}/libara_vision_inference.so \
    ${libdir}/libaraclient.so.1 \
    ${libexecdir}/ara2 \
    ${datadir}/python-wheels \
    ${datadir}/rt-sdk-ara240* \
"

FILES:${PN}-dev += "${includedir}/sdk_ara"

SYSTEMD_SERVICE:${PN} = "rt-sdk-ara2.service"

RDEPENDS:${PN} += " \
    dbus \
    dbus-lib \
    eiq-aaf-connector \
    gstreamer1.0 \
    uiodma \
    libusb1 \
    systemd \
"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}
    install -d ${D}${libexecdir}/ara2

    install -m 0644 ${THISDIR}/files/ara2.yaml ${D}${sysconfdir}/ara2.yaml
    install -m 0755 ${THISDIR}/files/uiodma.sh ${D}${libexecdir}/ara2

    # Expose legacy headers at the include root for nnstreamer
    ln -sf sdk_ara/dvapi.h ${D}${includedir}/dvapi.h
    ln -sf sdk_ara/dv_status_codes.h ${D}${includedir}/dv_status_codes.h

    # GStreamer nnstreamer backend expects this exact library name
    ln -sf libaraclient_aarch64.so ${D}${libdir}/libaraclient.so.1
}

INSANE_SKIP:${PN} += "useless-rpaths rpaths buildpaths dev-deps dev-so already-stripped"
