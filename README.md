# meta-kinara

Yocto BSP layer providing [Kinara](https://www.kinara.ai/) Ara-2 NPU accelerator
support for NXP i.MX platforms.

## Recipes

| Recipe | Description | License |
|---|---|---|
| `kernel-module-uiodma` | UIO DMA kernel module | GPL-2.0-only |
| `ara2` | Ara-2 runtime (proxy, libraries, firmware) | Proprietary |

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

The `ara2` recipe fetches the proprietary Kinara runtime tarball from a
download mirror. Since the runtime is distributed under NDA, you must
configure the mirror URL in your `local.conf`:

```
KINARA_MIRROR = "https://<mirror-url-provided-by-kinara>"
```

Without this variable set, `bitbake ara2` will produce a clear error
message with instructions.

Contact [Kinara](https://www.kinara.ai/) for NDA access to the runtime SDK.

### Preparing the Runtime Tarball

Kinara distributes a full SDK (`ara2-sdk-r<version>.tar.gz`, ~6.4 GB) that
includes a large Docker image for the host-side model converter. The `ara2`
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

Then update `SRC_URI[sha256sum]` in `recipes-kinara/ara2/ara2_<version>.bb`.

### Kernel Module (no special config needed)

The `kernel-module-uiodma` recipe builds from the public
[EdgeFirstAI/kernel-module-uiodma](https://github.com/EdgeFirstAI/kernel-module-uiodma)
repository and requires no additional configuration.

## License

This layer's metadata (recipes, configuration, scripts) is licensed under
the Apache License 2.0. See [LICENSE](LICENSE).

The Kinara Ara-2 runtime binary packages fetched by the `ara2` recipe
remain under Kinara's proprietary license.
