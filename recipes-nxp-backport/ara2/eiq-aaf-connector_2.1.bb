# NXP's AI Agentic Framework connector, backported from meta-imx-ml alongside
# imx-nxp-ara2 2.1.1, which RDEPENDS on it. See the note in
# imx-nxp-ara2_2.1.1.bb for why the backport exists and how it is selected.
SUMMARY = "Connector - OpenAI compatible server for AI Agentic Framework."
DESCRIPTION = "REST-based server for inference on LLMs running on i.MX devices with Kinara NPU"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8ff2d641cc45adce1b1882be29d1e35"

SRC_URI = "${EIQ_AAF_CONNECTOR_SRC};branch=${SRCBRANCH}"
EIQ_AAF_CONNECTOR_SRC ?= "git://github.com/nxp-imx-support/eiq-aaf-connector.git;protocol=https"
SRCBRANCH = "lf-6.18.20_2.0.0"
SRCREV = "5052be9f4ed8e58fa99c0f56e7b0b9404f8ce01e"

# Dual-compat unpack base: kirkstone and scarthgap have no UNPACKDIR.
UNPACK_BASE = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"
S = "${UNPACK_BASE}/git"

# The repository carries a prebuilt aarch64 connector and its install script,
# not a Python project: there is nothing to configure, compile, or turn into a
# wheel. meta-imx-ml's recipe inherits python_setuptools_build_meta and
# installs a wheel from the build directory, neither of which applies here.
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/aaf-connector ${D}${bindir}

    install -d ${D}${datadir}/python-wheels

    install -d ${D}${datadir}/eiq/aaf-connector
    install -m 0755 ${S}/install.sh ${D}${datadir}/eiq/aaf-connector/install.sh
    install -m 0644 ${S}/config/server_config.json ${D}${datadir}/eiq/aaf-connector
}

FILES:${PN} += " \
	${datadir}/python-wheels \
	${datadir}/eiq/aaf-connector \
"
RDEPENDS:${PN} = "bash"
