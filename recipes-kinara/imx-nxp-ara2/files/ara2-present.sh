#!/bin/sh
# Exit 0 when an Ara-2 accelerator is attached, 1 when none is.
#
# Used as the ExecCondition of ara2.service: a non-zero exit marks the unit
# skipped rather than failed, so Restart=always does not turn a board with no
# accelerator into a restart loop.
#
# The vendor ids match NXP's own 99-ara2.rules: 0x1e58 on PCIe, 318d on USB.

for vendor in /sys/bus/pci/devices/*/vendor; do
    [ -r "$vendor" ] || continue
    read -r id < "$vendor" || continue
    [ "$id" = "0x1e58" ] && exit 0
done

for vendor in /sys/bus/usb/devices/*/idVendor; do
    [ -r "$vendor" ] || continue
    read -r id < "$vendor" || continue
    [ "$id" = "318d" ] && exit 0
done

exit 1
