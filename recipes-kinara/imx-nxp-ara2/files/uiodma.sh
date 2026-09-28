#!/bin/sh

if [ ! -e /sys/bus/pci/drivers/uiodma/0000:01:00.0 ]; then
        echo "1e58 0002" > /sys/bus/pci/drivers/uiodma/new_id
fi
