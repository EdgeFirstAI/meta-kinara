# NXP's rt-sdk-ara2 hw_bringup.sh only binds the uiodma driver to the
# Ara-2 PCIe device the first time the kernel module is modprobed in a
# boot (the "new_id" sysfs write lives in the same branch as modprobe).
# rt-sdk-ara2.service runs this script with Restart=always; once an
# early attempt binds the device then loses it (e.g. an aborted DDR
# bringup), every later restart within that boot sees "module already
# loaded", skips rebinding, and fails immediately with "uiodma file
# path doesnt exist" -- observed as an unrecoverable crash loop on
# imx95-frdm on wrynose-6.18.20. Patch it to verify the actual PCI
# driver binding on every run instead of trusting module residency.
#
# NXP's 3.0 runtime replaced hw_bringup.sh with ara2-uiodma-bind.service, which
# binds the device on every start, so the patch applies to 2.1.1 only.
#
# Local workaround pending upstream report to NXP.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

python () {
    if d.getVar("PV") == "2.1.1":
        d.appendVar("SRC_URI", " file://0001-hw_bringup-fix-uiodma-rebind-after-failed-attempt.patch")
}

# NXP's packaging ships libaraclient as the unversioned libaraclient_aarch64.so
# and keeps the headers under include/sdk_ara, while nnstreamer's Ara-2
# sub-plugin and the EdgeFirst bindings load the library by its versioned
# soname and resolve the legacy headers at the include root. Both 2.1.1 and 3.0
# need the links.
#
# ara2-python is deliberately not provided: the Python API for either packaging
# comes from edgefirst-ara2, which probes the DVAPI generation of whichever
# libaraclient is installed.
PROVIDES += "ara2"
RPROVIDES:${PN} += "ara2"
RPROVIDES:${PN}-dev += "ara2-dev"
RPROVIDES:${PN}-staticdev += "ara2-staticdev"

FILES:${PN}-dev += "${includedir}/sdk_ara"

do_install:append() {
    ln -sf libaraclient_aarch64.so ${D}${libdir}/libaraclient.so.1
    ln -sf sdk_ara/dvapi.h ${D}${includedir}/dvapi.h
    ln -sf sdk_ara/dv_status_codes.h ${D}${includedir}/dv_status_codes.h
}

# The runtime ships a GStreamer plugin, which puts gstreamer1.0 in the main
# package's RDEPENDS. Images that reach the NPU through the client library
# alone would pull the stack in with the runtime, so the plugin is split into
# its own package and the dependency moves with it.
PACKAGES =+ "${PN}-gstreamer"
FILES:${PN}:remove = "${libdir}/gstreamer-1.0"
FILES:${PN}-gstreamer = "${libdir}/gstreamer-1.0"
RDEPENDS:${PN}:remove = "gstreamer1.0"
RDEPENDS:${PN}-gstreamer = "${PN} gstreamer1.0"

# Activation stays NXP's: the tarball ships 99-ara2.rules with
# ara2-device.target and rt-sdk-ara2.service, so udev starts the runtime when
# an Ara-2 appears on PCIe or USB and leaves it stopped when none is fitted.
# The unit must not be registered through SYSTEMD_SERVICE, which would enable
# it at boot and start the runtime on hardware that is not there.
