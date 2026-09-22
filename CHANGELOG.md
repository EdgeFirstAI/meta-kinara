# Changelog — meta-kinara

All notable changes to the `meta-kinara` Yocto layer are documented here.

## [Unreleased]

- `KINARA_ARA2_PROVIDER` selects which Ara-2 runtime an image installs:
  `nxp` (default) for NXP's `imx-nxp-ara2`, or `kinara` for this layer's
  `ara2`. The two ship different DVAPI generations on different proxy
  sockets and a client of one hangs on the other's proxy, so they now
  declare `RCONFLICTS` on each other. No files collide between them, so
  nothing else prevented co-installation.
- `ara2` and `imx-nxp-ara2` both `RPROVIDES` a virtual `ara2-runtime`, and
  `edgefirst-ara2` depends on that rather than on `ara2`. The bindings work
  against either generation, and a future packaging can satisfy them by
  providing the same name.
- Appends to NXP recipes moved under
  `dynamic-layers/imx-machine-learning/`, wired through `BBFILES_DYNAMIC`.
  A `.bbappend` with no matching recipe is a parse error, so keeping them
  in `recipes-*` would break every build without meta-imx-ml. Builds
  without that layer fall back to `KINARA_ARA2_PROVIDER = "kinara"`.
- `LAYERSERIES_COMPAT` extended with `whinlatter` (Yocto 5.3) for the
  NXP imx-6.18.2-1.0.0 BSP. Kirkstone, scarthgap and walnascar remain
  supported.
- Bumped `kernel-module-uiodma` from 1.2.1 to 1.2.2 — sysfs
  `bin_attribute` callbacks constified for Linux 6.13+ (kernel 6.18 in
  the whinlatter BSP), version-gated so 5.15/6.12 kernels still build.
- `ara2` and `kernel-module-uiodma` recipes adapted to whinlatter unpack
  semantics (no raw `${WORKDIR}` in `S`; git checkouts at
  `${UNPACKDIR}/${BP}`).

## v1.2.3 — 2026-05-28

- Bumped `edgefirst-ara2` from 0.5.0 to 0.11.2 — Python bindings updated
  through six intermediate releases. Recipe pulls the
  `cp311-abi3-manylinux_2_17_aarch64` wheel from the ara2-rs v0.11.2
  release; LICENSE hash refreshed.

## v1.2.2 — 2026-04-26

- Bumped `edgefirst-ara2` from 0.4.0 to 0.5.0 — updated Python bindings
  for the Kinara Ara-2 Runtime with improvements from the `ara2-rs` crate.

## v1.2.0 — 2026-04-16

- Added `edgefirst-ara2` recipe (v0.4.0) — Python bindings for the
  Kinara Ara-2 Runtime built from the EdgeFirst `ara2-rs` Rust crate.
  Pulls the prebuilt cp311-abi3 manylinux2014 aarch64 wheel from the
  ara2-rs v0.4.0 release. Adds Session/Model APIs with typed tensor I/O
  and qmode-9 dequantization for Python applications driving the
  Ara-2 NPU.
- `packagegroup-kinara` now pulls in `edgefirst-ara2` alongside the
  existing `ara2` and `ara2-python` packages.

## v1.1 — 2026-03-02

- Autoload `uiodma` kernel module at boot
- Added `scarthgap` to `LAYERSERIES_COMPAT` for Torizon BSP support
- Packagegroups and SDK toolchain integration
- Initial layer release for Kinara Ara-2 NPU support (uiodma driver,
  firmware, SDK runtime)
