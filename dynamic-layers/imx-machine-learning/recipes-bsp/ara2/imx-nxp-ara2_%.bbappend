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
