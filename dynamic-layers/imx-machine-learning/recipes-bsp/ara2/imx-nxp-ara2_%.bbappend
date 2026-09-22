# NXP's ARA-2 runtime supplies the same capability as this layer's ara2
# recipe and cannot be installed alongside it: the two ship different DVAPI
# generations on different proxy sockets, and a client of one connects to
# the other's proxy and then hangs on the first call. No files collide, so
# only these declarations prevent co-installation.
#
# Version-independent, so a bump to NXP's recipe keeps the relationship.

RPROVIDES:${PN} += "ara2-runtime"
RCONFLICTS:${PN} = "ara2"
