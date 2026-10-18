SUMMARY = "Flatcar Container Linux guest domain"
DESCRIPTION = "Config files and UEFI firmware for Flatcar guest domain"

PV = "0.1"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit externalsrc systemd

EXTERNALSRC_SYMLINKS = ""

SRC_URI = "\
    file://dom-flatcar-virtio.cfg \
    file://dom-flatcar.service \
    file://virtio-env.conf \
"

FILES:${PN} = " \
    ${sysconfdir}/xen/dom-flatcar.cfg \
    ${libdir}/xen/boot/XEN_EFI.fd \
    ${systemd_unitdir}/system/dom-flatcar.service \
    ${sysconfdir}/systemd/system/dom-flatcar.service.d/virtio-env.conf \
"

SYSTEMD_SERVICE:${PN} = "dom-flatcar.service"
SYSTEMD_AUTO_ENABLE:${PN} = "${XT_AUTOSTART_DOM_FLATCAR}"

do_install() {
    # config for xen flatcar domu
    install -d ${D}${sysconfdir}/xen
    install -m 0644 ${WORKDIR}/dom-flatcar-virtio.cfg ${D}${sysconfdir}/xen/dom-flatcar.cfg

    # UEFI firmware (EDK2 ArmVirtXen)
    install -d ${D}${libdir}/xen/boot
    install -m 0644 ${S}/XEN_EFI.fd ${D}${libdir}/xen/boot/XEN_EFI.fd

    # systemd services
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/dom-flatcar.service ${D}${systemd_unitdir}/system/

    # launch-domain drop-in
    install -d ${D}${sysconfdir}/systemd/system/dom-flatcar.service.d
    install -m 0644 ${WORKDIR}/virtio-env.conf ${D}${sysconfdir}/systemd/system/dom-flatcar.service.d
}

RDEPENDS:${PN}:append = " backend-ready"
RDEPENDS:${PN} += " launch-domain"
