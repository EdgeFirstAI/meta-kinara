SUMMARY = "Linux Driver User Space DMA"
DESCRIPTION = "${SUMMARY}"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

inherit module

SRC_URI = "git://github.com/EdgeFirstAI/kernel-module-uiodma.git;protocol=https;branch=main"
SRCREV = "21475dd51254c75bd5ed6aa20085609e1c7f14d3"

# Dual-compat source dir for git checkouts. Whinlatter unpacks to
# ${UNPACKDIR}/${BB_GIT_DEFAULT_DESTSUFFIX} (= ${BP}) and QA-fatals on a
# raw S = ".../git" assignment; walnascar unpacks to ${UNPACKDIR}/git;
# kirkstone/scarthgap to ${WORKDIR}/git. The inline expression yields the
# actual checkout path on every supported release without tripping the QA
# check.
S = "${@(d.getVar('UNPACKDIR') + '/' + d.getVar('BB_GIT_DEFAULT_DESTSUFFIX')) if d.getVar('BB_GIT_DEFAULT_DESTSUFFIX') else ((d.getVar('UNPACKDIR') or d.getVar('WORKDIR')) + '/git')}"

RPROVIDES:${PN} += "kernel-module-uiodma"

# NXP's own meta-imx-ml uiodma_1.0.0.bb (new as of wrynose) RDEPENDS on
# bare "uiodma" (imx-nxp-ara2_2.1.1.bb). We skip that recipe in favor of
# this one (see uiodma_1.0.0.bbappend) to avoid a kernel-module-uiodma
# do_packagedata collision, so provide the name it expects too.
RPROVIDES:${PN} += "uiodma"

# Load uiodma at boot so Ara-2 NPU is ready without waiting for ara2.service
KERNEL_MODULE_AUTOLOAD += "uiodma"
