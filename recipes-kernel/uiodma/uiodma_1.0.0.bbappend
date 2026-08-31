# NXP's uiodma recipe (new as of wrynose, meta-imx-ml) and our own
# kernel-module-uiodma (recipes-kernel/kernel-modules) both build the
# uiodma.ko kernel module, so both produce a "kernel-module-uiodma"
# package via kernel-module-split auto-discovery — a do_packagedata
# shared-area collision when both are pulled into the same build
# (kernel-module-uiodma via ara2_1.2.1.bb, this one via
# imx-nxp-ara2_2.1.1.bb's RDEPENDS on bare "uiodma").
#
# Skip this one in favor of our own, which now also RPROVIDES "uiodma"
# so imx-nxp-ara2's dependency still resolves, and already carries
# KERNEL_MODULE_AUTOLOAD for Ara-2 boot-time readiness.
SKIP_RECIPE[uiodma] = "Superseded by kernel-module-uiodma (meta-kinara recipes-kernel/kernel-modules) to avoid a kernel-module-uiodma do_packagedata collision"
