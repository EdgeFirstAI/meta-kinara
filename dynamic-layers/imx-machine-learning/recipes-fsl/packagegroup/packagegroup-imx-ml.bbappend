# NXP's ML packagegroup pulls imx-nxp-ara2 in through ARA240_PKGS. When the
# image is built against this layer's ARA-2 runtime instead, drop it here
# rather than skipping the recipe: other recipes (nnstreamer-ara2, the
# demo-experience examples) still build against it, and SKIP_RECIPE would
# take them out too.
#
# ARA240_PKGS is set per machine (ARA240_PKGS:mx8mp-nxp-bsp,
# ARA240_PKGS:mx95-nxp-bsp), so a plain assignment here would lose to those
# overrides. :remove is applied after them and covers any machine NXP adds
# later. An empty value removes nothing.

ARA240_PKGS:remove = "${@bb.utils.contains('KINARA_ARA2_PROVIDER', 'kinara', 'imx-nxp-ara2', '', d)}"
