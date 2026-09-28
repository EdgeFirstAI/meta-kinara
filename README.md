# meta-kinara

Yocto BSP layer providing [Kinara](https://www.kinara.ai/) Ara-2 NPU accelerator
support for NXP i.MX platforms.

## Recipes

| Recipe | Description | License |
|---|---|---|
| `kernel-module-uiodma` | UIO DMA kernel module | GPL-2.0-only |
| `imx-nxp-ara2` (1.2.1) | Kinara SDK Ara-2 runtime (proxy, libraries, firmware) | Proprietary |
| `packagegroup-kinara` | The runtime, plus `edgefirst-ara2` when meta-edgefirst is in the build | Proprietary |

## Choosing an Ara-2 runtime

Two packagings of the Ara-2 runtime exist, and only one can be installed:

| Source | Recipe | DVAPI | Proxy socket | Service |
|---|---|---|---|---|
| NXP, meta-imx-ml | `imx-nxp-ara2_2.1.1.bb` | 1.3.x | `/var/run/proxy.sock` | `rt-sdk-ara2.service` |
| Kinara SDK, this layer | `imx-nxp-ara2_1.2.1.bb` | 1.1.x | `/var/run/ara2.sock` | `ara2.service` |

This layer packages the Kinara SDK under NXP's recipe name, with the
version tracking the Kinara SDK release. A build selects exactly one
version of a recipe, so the two can never be installed together, and every
consumer — NXP's `packagegroup-imx-ml`, `packagegroup-kinara`, and
meta-edgefirst's `edgefirst-ara2` — depends on `imx-nxp-ara2` without
caring which one it gets.

NXP's packaging takes precedence wherever meta-imx-ml ships it (the wrynose 6.18.20-2.0.0 BSP onwards). BitBake compares versions only within the highest-priority layer, and this layer outranks meta-imx-ml, so the Kinara SDK recipe skips itself there rather than relying on the version ordering. To use it anyway, set in `local.conf`:

```
PREFERRED_VERSION_imx-nxp-ara2 = "1.2.1"
```

Builds without meta-imx-ml (Torizon, for example), or with a meta-imx-ml older than the wrynose BSP, have only the Kinara SDK recipe and need no configuration. Appends to NXP recipes live under `dynamic-layers/imx-machine-learning/` and are parsed only when that layer ships the recipes they extend.

The layer sets `KINARA_ARA2_RUNTIME` to `nxp` or `kinara` to say which packaging `imx-nxp-ara2` resolves to. It is read-only: recipes that work with only one runtime test it rather than repeating the selection logic. meta-edgefirst's NNStreamer Ara-2 sub-plugin (`nnstreamer-ara2`), for example, is built only when it is `kinara`.

The Kinara SDK recipe replaces the `ara2` and `ara2-python` packages of
earlier releases, so package-managed targets upgrade in place. It does not
ship the SDK's DVAPI Python module; `edgefirst-ara2` (from
[meta-edgefirst](https://github.com/EdgeFirstAI/meta-edgefirst)) is the
Python API for either runtime.

NXP's demo recipes in meta-nxp-demo-experience (`imx-ara2-vision-examples`,
the LLM and VLM edge studios, `imx-smart-device-gateway`) are written
against NXP's packaging and are not expected to work with the Kinara SDK
one.

## Running the Kinara SDK runtime

`ara2.service` is installed disabled. Enable it once on the target so the proxy starts at boot:

```
systemctl enable --now ara2
```

Never run `ara2-info` or `chip_info` while the service is active; stop it first.

### Ara-2 card boot firmware

The Ara-2 card keeps its boot firmware in on-card flash, and each runtime accepts only certain versions (`dm_supported_firmware_versions` in the proxy configuration):

| Runtime | Accepted firmware versions | Shipped image |
|---|---|---|
| Kinara SDK 1.2.1 (this layer) | 8719, 8720, 8723, 32778, 32779 | `/usr/share/ara2/willow_therm.hex` (32779) |
| NXP rt-sdk-ara2 2.1.1 | 32779, 65794, 131072, 131073 | `Commercial_131072.hex`, via `program_flash.sh` |

A card that has been used with NXP's runtime may carry firmware 65794 or newer. The Kinara SDK proxy then fails with `Unsupported firmware version on device ... DV_ENDPOINT_FIRMWARE_BOOT_FAILURE`. To check and restore the firmware this layer ships:

```
systemctl stop ara2
/usr/libexec/ara2/chip_info -e 0        # firmware_version(raw) should read 32779
/usr/libexec/ara2/program_flash -e 0 -f /usr/share/ara2/willow_therm.hex --version_check 0
reboot
```

`program_flash` refuses to write an older version than the card holds unless `--version_check 0` is given.

## Dependencies

This layer depends on:

- `poky` (meta) — OpenEmbedded core
- NXP BSP kernel with Ara-2 device-tree support

Compatible with Yocto release series: **kirkstone**, **walnascar**.

## Setup

Add the layer to your build:

```
bitbake-layers add-layer sources/meta-kinara
```

### Ara-2 Runtime (NDA required)

The Kinara SDK `imx-nxp-ara2` recipe fetches the proprietary Kinara runtime tarball from a
download mirror. Since the runtime is distributed under NDA, you must
configure the mirror URL in your `local.conf`:

```
KINARA_MIRROR = "https://<mirror-url-provided-by-kinara>"
```

Without this variable set, fetching the Kinara SDK recipe produces a clear error
message with instructions.

Contact [Kinara](https://www.kinara.ai/) for NDA access to the runtime SDK.

### Preparing the Runtime Tarball

Kinara distributes a full SDK (`ara2-sdk-r<version>.tar.gz`, ~6.4 GB) that
includes a large Docker image for the host-side model converter. The
recipe only needs the runtime components (~56 MB). To create the runtime
tarball from the SDK:

```sh
VERSION=1.2.1

# Extract the SDK
mkdir ara2-runtime-r${VERSION}
tar xf ara2-sdk-r${VERSION}.tar.gz 
mv ara2-sdk-r${VERSION} ara2-runtime-r${VERSION}

# Remove the host-only model converter Docker image (~14 GB uncompressed)
rm -rf ara2-runtime-r${VERSION}/dvdocker

# Create a reproducible tarball
tar --sort=name \
    --mtime="2025-07-10 00:00:00Z" \
    --owner=0 --group=0 --numeric-owner \
    -cjf ara2-runtime-r${VERSION}.tar.bz2 \
    ara2-runtime-r${VERSION}/
```

The `--sort`, `--mtime`, `--owner`, and `--group` flags ensure the tarball
is reproducible — anyone starting from the same SDK release will get the
same file with the same sha256 checksum.

Upload the resulting tarball to your `KINARA_MIRROR` host so that the URL
`${KINARA_MIRROR}/ara2-runtime-r${VERSION}.tar.bz2` resolves.

If you need to update the checksum in the recipe (e.g. for a new SDK version):

```sh
sha256sum ara2-runtime-r${VERSION}.tar.bz2
```

Then update `SRC_URI[sha256sum]` in `recipes-kinara/imx-nxp-ara2/imx-nxp-ara2_<version>.bb`.

### Kernel Module (no special config needed)

The `kernel-module-uiodma` recipe builds from the public
[EdgeFirstAI/kernel-module-uiodma](https://github.com/EdgeFirstAI/kernel-module-uiodma)
repository and requires no additional configuration.

## License

This layer's metadata (recipes, configuration, scripts) is licensed under
the Apache License 2.0. See [LICENSE](LICENSE).

The Kinara Ara-2 runtime binary packages fetched by the `ara2` recipe
remain under Kinara's proprietary license.
