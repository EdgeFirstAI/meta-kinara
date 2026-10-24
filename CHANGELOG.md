# Changelog — meta-kinara

All notable changes to the `meta-kinara` Yocto layer are documented here.

## [Unreleased]

- `recipes-nxp-backport` carries NXP's `imx-nxp-ara2` 2.1.1 and `eiq-aaf-connector` 2.1 for the releases whose meta-imx-ml ships no Ara-2 recipe, so a build without NDA mirror access has a runtime. Selected with `PREFERRED_VERSION_imx-nxp-ara2 = "2.1.1"` and never chosen implicitly (`DEFAULT_PREFERENCE = "-1"`); masked wherever meta-imx-ml provides the recipes, and wherever meta-imx-bsp is absent. Backported from PHYTEC's fork of this layer, along with its fixes to `S` and the configure, compile and wheel-install steps of NXP's `eiq-aaf-connector`.
- `KINARA_ARA2_RUNTIME` keys on `PREFERRED_VERSION_imx-nxp-ara2` first, so a 2.x selection reports `nxp` on the releases where the backport supplies it rather than `kinara`.
- NXP's `imx-nxp-ara2` gains `ara2` in `PROVIDES` and `RPROVIDES`, `${includedir}/sdk_ara` in its `-dev` package, and links for `libaraclient.so.1`, `dvapi.h` and `dv_status_codes.h`. Neither 2.1.1 nor 3.0 ships the versioned soname or the legacy header paths that nnstreamer and `edgefirst-ara2` resolve.
- NXP's `imx-nxp-ara2` GStreamer plugin is packaged as `imx-nxp-ara2-gstreamer`, so the runtime no longer puts `gstreamer1.0` in the main package's `RDEPENDS` and images that reach the NPU through the client library alone do not pull the stack in.
- `ara2.service` is activated by udev (`99-ara2-kinara.rules`, PCIe vendor `0x1e58` and USB vendor `318d`) rather than enabled by hand, matching the activation NXP ships with its own packaging: a board with no accelerator never starts the runtime, and one with an accelerator needs no configuration. The unit carries `ara2-present.sh` as its `ExecCondition`, so enabling it by hand without an accelerator leaves it skipped instead of looping on `Restart=always`.
- The `hw_bringup` patch is no longer packaged into the rootfs. `fsl-eula2-unpack2` installs the unpacked archive with `cp -r ${S}/*` and the patch machinery leaves quilt's bookkeeping in `${S}`, so the patch and its series file shipped at `/patches`.
- `uiodma.sh` tolerates a missing `new_id`, so it does not fail on a board with no accelerator.

- The Kinara SDK runtime recipe is renamed from `ara2` to `imx-nxp-ara2` at version 1.2.1, the Kinara SDK release, so it and NXP's `imx-nxp-ara2` (2.1.1, meta-imx-ml) are one recipe name and a build installs exactly one of them. Previously both were installed side by side, with different DVAPI generations on different proxy sockets.
- NXP's `imx-nxp-ara2` takes precedence wherever meta-imx-ml ships it (wrynose onwards); the Kinara SDK recipe is used there only with `PREFERRED_VERSION_imx-nxp-ara2 = "1.2.1"`. Builds without meta-imx-ml, or with an earlier meta-imx-ml, use it with no configuration.
- `imx-nxp-ara2` (Kinara SDK) replaces and conflicts with the old `ara2` and `ara2-python` packages so package-managed targets upgrade in place.
- `KINARA_ARA2_RUNTIME` (read-only, set in `layer.conf`) reports which packaging `imx-nxp-ara2` resolves to, `nxp` or `kinara`, for recipes that work with only one of them.
- The DVAPI Python module (`kinara/dvapi.py`) is no longer packaged; `edgefirst-ara2` is the Python API for both runtime packagings.
- `edgefirst-ara2` moved to meta-edgefirst. `packagegroup-kinara` installs it only when meta-edgefirst is in the build.
- `packagegroup-kinara` and `packagegroup-kinara-sdk` depend on `imx-nxp-ara2` and its `-dev`/`-staticdev` packages.
- Appends to NXP recipes (`imx-nxp-ara2`, `uiodma`) live under `dynamic-layers/imx-machine-learning/`, wired through `BBFILES_DYNAMIC`. A `.bbappend` with no matching recipe is a parse error, so keeping them in `recipes-*` broke every build without meta-imx-ml. They are also masked on kirkstone through whinlatter, whose meta-imx-ml has neither recipe.
- NXP's `rt-sdk-ara2` `hw_bringup.sh` is patched to check the actual PCI driver binding of the Ara-2 device before skipping the uiodma bind.
- `LAYERSERIES_COMPAT` extended with `wrynose` (Yocto 5.4) for the NXP imx-6.18.20-2.0.0 BSP. Kirkstone, scarthgap, walnascar, and whinlatter remain supported.
- `LAYERSERIES_COMPAT` extended with `whinlatter` (Yocto 5.3) for the NXP imx-6.18.2-1.0.0 BSP. Kirkstone, scarthgap and walnascar remain supported.
- Bumped `kernel-module-uiodma` from 1.2.1 to 1.2.2 — sysfs `bin_attribute` callbacks constified for Linux 6.13+ (kernel 6.18 in the whinlatter BSP), version-gated so 5.15/6.12 kernels still build.
- `ara2` and `kernel-module-uiodma` recipes adapted to whinlatter unpack semantics (no raw `${WORKDIR}` in `S`; git checkouts at `${UNPACKDIR}/${BP}`).

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
