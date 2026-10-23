# NXP's Ara-2 runtime packaging, backported from meta-imx-ml.
#
# meta-imx-ml gained imx-nxp-ara2 with the wrynose BSP. Earlier releases ship
# no Ara-2 recipe at all, which leaves the Kinara SDK runtime (imx-nxp-ara2
# 1.2.1, recipes-kinara) as the only source -- and that tarball is served from
# an NDA mirror. This recipe makes NXP's EULA-licensed packaging available on
# those releases too, so a build without mirror access still has a runtime.
#
# DEFAULT_PREFERENCE keeps it out of the way where both are available: the
# Kinara SDK packaging remains the default and this one is selected with
#
#   PREFERRED_VERSION_imx-nxp-ara2 = "2.1.1"
#
# The recipe is masked wherever meta-imx-ml provides imx-nxp-ara2 itself, and
# wherever meta-imx-bsp is absent, since the fsl-eula2 classes live there.
# See KINARA_BACKPORT_DIR in conf/layer.conf.
DEFAULT_PREFERENCE = "-1"

SUMMARY = "NXP RT-SDK ARA2"
DESCRIPTION = "Runtime SDK for AI/ML acceleration with Ara240 NPU on i.MX SoCs"
HOMEPAGE = "https://github.com/nxp-imx/rt-sdk-ara2"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=bc649096ad3928ec06a8713b8d787eac"

PROVIDES += "ara2"

# ara2-python is deliberately not provided: the Python API for either
# packaging comes from edgefirst-ara2, which probes the DVAPI generation of
# whichever libaraclient is installed.
RPROVIDES:${PN} += "ara2"
RPROVIDES:${PN}-dev += "ara2-dev"
RPROVIDES:${PN}-staticdev += "ara2-staticdev"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base"

IMX_SRCREV_ABBREV = "063d56c"
SRC_URI[sha256sum] = "7fcbd5206b0b12358dd4757d4e1cb8785d2db8ce601aecd290634c047b85851a"

inherit fsl-eula2-unpack2 fsl-eula-recent

# libaraclient_aarch64 is unversioned, so clear FILES_SOLIBSDEV and then
# place the libraries into the main and -dev packages explicitly.
FILES_SOLIBSDEV = ""

FILES:${PN} += " \
    ${libdir}/libaraclient_aarch64.so \
    ${libdir}/libara_vision_inference.so \
    ${libdir}/libaraclient.so.1 \
    ${datadir}/python-wheels \
    ${datadir}/rt-sdk-ara240* \
"

FILES:${PN}-dev += "${includedir}/sdk_ara"

# The GStreamer plugin is packaged separately so the runtime itself carries no
# dependency on GStreamer: images that reach the NPU through the client library
# alone would otherwise pull the stack in through RDEPENDS.
PACKAGES =+ "${PN}-gstreamer"
FILES:${PN}-gstreamer = "${libdir}/gstreamer-1.0"
RDEPENDS:${PN}-gstreamer = "${PN} gstreamer1.0"

# uv is used by the runtime's own wheel installer, which is not reachable on
# the releases this backport serves, and has no recipe there.
RDEPENDS:${PN} += " \
    dbus \
    dbus-lib \
    eiq-aaf-connector \
    uiodma \
    libusb1 \
    systemd \
"

# Activation is NXP's own: the tarball ships 99-ara2.rules together with
# ara2-device.target and rt-sdk-ara2.service, so udev starts the runtime when
# an Ara-2 appears on PCIe or USB and leaves it stopped when none is fitted.
# The unit is deliberately not registered through SYSTEMD_SERVICE, which would
# enable it at boot and start the runtime on hardware that is not there.

do_install:append() {
    # nnstreamer and the EdgeFirst bindings load the client library by its
    # versioned soname, and resolve the legacy headers at the include root.
    ln -sf libaraclient_aarch64.so ${D}${libdir}/libaraclient.so.1
    ln -sf sdk_ara/dvapi.h ${D}${includedir}/dvapi.h
    ln -sf sdk_ara/dv_status_codes.h ${D}${includedir}/dv_status_codes.h
}

INSANE_SKIP:${PN} += "useless-rpaths rpaths buildpaths dev-deps dev-so already-stripped"
