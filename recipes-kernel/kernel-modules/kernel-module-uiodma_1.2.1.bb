SUMMARY = "Linux Driver User Space DMA"
DESCRIPTION = "${SUMMARY}"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

inherit module

SRC_URI = "git://github.com/EdgeFirstAI/kernel-module-uiodma.git;protocol=https;branch=main"
SRCREV = "467119448498c76ee0c93d97a9cd31e760ee2480"

S = "${WORKDIR}/git"

RPROVIDES:${PN} += "kernel-module-uiodma"
