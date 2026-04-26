# Changelog — meta-kinara

All notable changes to the `meta-kinara` Yocto layer are documented here.

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
