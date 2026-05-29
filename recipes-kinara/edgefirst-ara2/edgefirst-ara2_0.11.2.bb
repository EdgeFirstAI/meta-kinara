SUMMARY = "EdgeFirst Ara-2 Python Bindings"
DESCRIPTION = "Python bindings for the Kinara Ara-2 Runtime built from \
the EdgeFirst ara2-rs Rust crate. Exposes Session/Model APIs with typed \
input/output tensors, qmode-9 dequantization, and InputPreprocess \
metadata for Python applications driving the Ara-2 NPU."
HOMEPAGE = "https://github.com/EdgeFirstAI/ara2-rs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=5be2388f3daa0021cf03f632f6b4b2f2"

SRC_URI = " \
    https://github.com/EdgeFirstAI/ara2-rs/releases/download/v${PV}/edgefirst_ara2-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/ara2-rs/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "2ae4d6fcebc5889a6d989c583dd97e8505b79efccacca845712b93a8c89d0f73"
SRC_URI[python.sha256sum] = "d633d45de9e7d7748b5069ad36b88aa42a137a92438827057755b8f6d92e213c"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "python3 unzip-native"
RDEPENDS:${PN} = "python3 ara2"

do_install() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip ${S}/edgefirst_ara2-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl \
        -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"

INSANE_SKIP:${PN} += "ldflags already-stripped"
