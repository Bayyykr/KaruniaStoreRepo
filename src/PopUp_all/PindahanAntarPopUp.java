package PopUp_all;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.lang.reflect.Method;

public class PindahanAntarPopUp {

    // Daftar semua popup aktif dari berbagai jenis
    private static ArrayList<JDialog> activePopups = new ArrayList<>();

    public static void registerPopup(JDialog popup) {
        // Tambahkan popup baru ke daftar (di awal/index 0 agar berada di paling atas)
        activePopups.add(0, popup);
        repositionAllPopups();
    }

    public static void unregisterPopup(JDialog popup) {
        // Hapus popup dari daftar
        activePopups.remove(popup);
        // Atur ulang posisi popup yang tersisa
        repositionAllPopups();
    }

    public static void repositionAllPopups() {
        if (activePopups.isEmpty()) {
            return;
        }

        // Cari popup dengan class yang sama
        for (int i = 0; i < activePopups.size(); i++) {
            JDialog currentPopup = activePopups.get(i);

            // Skip jika popup pertama (indeks 0)
            if (i == 0) {
                continue;
            }

            // Cek apakah popup sebelumnya berbeda class
            JDialog previousPopup = activePopups.get(i - 1);
            if (!currentPopup.getClass().equals(previousPopup.getClass())) {
                try {
                    // Dapatkan posisi Y popup sebelumnya
                    int prevY = previousPopup.getY();
                    int prevHeight = previousPopup.getHeight();

                    // Geser popup saat ini ke bawah popup sebelumnya
                    int newY = prevY + prevHeight + 10;

                    // Set posisi baru
                    currentPopup.setLocation(currentPopup.getX(), newY);

                    // Jika popup memiliki method setLocationOnClose, update juga
                    try {
                        Method setLocationMethod = currentPopup.getClass().getDeclaredMethod(
                                "setLocationOnClose", int.class, int.class);
                        if (setLocationMethod != null) {
                            setLocationMethod.invoke(currentPopup, currentPopup.getX(), newY);
                        }
                    } catch (Exception e) {
                        // Ignore jika tidak ada method
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void showSuksesTambahBarang(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualTambahBarang popup = new PopUp_SmallTransJualTambahBarang(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showSuksesBayarTransjual(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualSuksesBayar popup = new PopUp_SmallTransJualSuksesBayar(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showScanProdukTerlebihDahulu(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualScanProdukTerlebihDahulu popup = new PopUp_SmallTransJualScanProdukTerlebihDahulu(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTidakAdaItemYangDibeli(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualTidakAdaItemYangDibeli popup = new PopUp_SmallTransJualTidakAdaItemYangDibeli(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showMasukkanUangTerlebihDahulu(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualMasukkanUangTerlebihDahulu popup = new PopUp_SmallTransJualMasukkanUangTerlebihDahulu(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showMasukkanBarangTerlebihDahulu(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualBelumAdaBarangYangDImasukkan popup = new PopUp_SmallTransJualBelumAdaBarangYangDImasukkan(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showProdukTidakDitemukan(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualProdukTidakDitemukan popup = new PopUp_SmallTransJualProdukTidakDitemukan(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showStrukBerhasilDicetak(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_TransJualStrukBerhasilDicetak popup = new PopUp_TransJualStrukBerhasilDicetak(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showProdukBerhasilDihapus(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualProdukBerhasilDiHapus popup = new PopUp_SmallTransJualProdukBerhasilDiHapus(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showProdukBerhasilDiedit(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualProdukBerhasilDiEdit popup = new PopUp_SmallTransJualProdukBerhasilDiEdit(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showMasukSebagaiOwner(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallLoginSebagaiOwnerBerhasil popup = new PopUp_SmallLoginSebagaiOwnerBerhasil(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showMasukSebagaiKasir(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallLoginSebagaiKasirBerhasil popup = new PopUp_SmallLoginSebagaiKasirBerhasil(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTambahKaryawanTIdakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUpSmall_TambahKaryawanTidakBolehKosong popup = new PopUpSmall_TambahKaryawanTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTambahKaryawanEmailHarusBenarPenulisannya(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_TambahKaryawanEmailHarusSesuai popup = new PopUp_TambahKaryawanEmailHarusSesuai(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showEditKaryawanSuksesDiEdit(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditKaryawanSuksesDiEdit popup = new PopUp_SmallEditKaryawanSuksesDiEdit(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showHapuskaryawanSuksesDiHapus(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallHapusKaryawanSuksesDihapus popup = new PopUp_SmallHapusKaryawanSuksesDihapus(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTambahKaryawanBerhasilDiTambah(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahKaryawanBerhasilDiTambah popup = new PopUp_SmallTambahKaryawanBerhasilDiTambah(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTambahKaryawanNomorTeleponTidakValid(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahKaryawanNomorTeleponTidakVAlid popup = new PopUp_SmallTambahKaryawanNomorTeleponTidakVAlid(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showEditDataKaryawanNamaTidakLebihDari30karakter(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditDataKaryawanNamaTidakBolehLebihDari30Karakter popup = new PopUp_SmallEditDataKaryawanNamaTidakBolehLebihDari30Karakter(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showEditDataKaryawanEmailTidakLebihDari30karakter(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditDataKaryawanEmailTidakBolehLebihDari30Karakter popup = new PopUp_SmallEditDataKaryawanEmailTidakBolehLebihDari30Karakter(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showEditDataKaryawanPassswordTidakLebihDari20karakter(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditDataKaryawanPAsswordTidakBolehLebihDari20Karakter popup = new PopUp_SmallEditDataKaryawanPAsswordTidakBolehLebihDari20Karakter(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showEditDataKaryawanNoTlpHarusBerupaAngka(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditDataKaryawanNoTlpHarusBerupaAngka popup = new PopUp_SmallEditDataKaryawanNoTlpHarusBerupaAngka(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showEditDataKaryawanPassswordTidakLebihDari13karakter(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditDataKaryawanPAsswordTidakBolehLebihDari13Karakter popup = new PopUp_SmallEditDataKaryawanPAsswordTidakBolehLebihDari13Karakter(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showGajiKaryawanSuksesBayarGaji(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_GajiKAryawanSuksesBayarGaji popup = new PopUp_GajiKAryawanSuksesBayarGaji(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showDataKaryawanNoRFIDTIdakBolehLebihDari16(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallDataKaryawanNoRFIDTIdakBolehLebihDari16 popup = new PopUp_SmallDataKaryawanNoRFIDTIdakBolehLebihDari16(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTambahProdukNamaTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukNamaProdukTidakBolehKosong popup = new PopUp_SmallTambahProdukNamaProdukTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTambahProdukHargaJualTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukHargaJualTidakBolehKosong popup = new PopUp_SmallTambahProdukHargaJualTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTambahProdukHargaBeliTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukHargaBeliTidakBolehKosong popup = new PopUp_SmallTambahProdukHargaBeliTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showTambahProdukUkuranTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukUkuranTidakBolehKosong popup = new PopUp_SmallTambahProdukUkuranTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukStokTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukStokTidakBolehKosong popup = new PopUp_SmallTambahProdukStokTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukPilihKategoriDulu(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukPilihCategoryTerlebihDahulu popup = new PopUp_SmallTambahProdukPilihCategoryTerlebihDahulu(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukHargaJualLebihDari0(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukHargaJualHaruslebihDari0 popup = new PopUp_SmallTambahProdukHargaJualHaruslebihDari0(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukHargaBeliLebihDari0(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukHargaBeliHaruslebihDari0 popup = new PopUp_SmallTambahProdukHargaBeliHaruslebihDari0(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukhargaJualHarusLebihDariHargaBeli(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukhargaJualHarusLebihDariHargaBeli popup = new PopUp_SmallTambahProdukhargaJualHarusLebihDariHargaBeli(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukStokTidakBolehNegatif(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukStokTidakBolehNegatif popup = new PopUp_SmallTambahProdukStokTidakBolehNegatif(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukHargaDanStokHarusBerupaAngka(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukHargaDanStokHarusBerupaAngkaYgValid popup = new PopUp_SmallTambahProdukHargaDanStokHarusBerupaAngkaYgValid(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukBerhasilDiTambahkan(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukBerhasilDitambahkan popup = new PopUp_SmallTambahProdukBerhasilDitambahkan(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukGenerateBarcodeTerlebihDahulu(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukSilakanGenerateaBarcodeTerlebihDahulu popup = new PopUp_SmallTambahProdukSilakanGenerateaBarcodeTerlebihDahulu(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukMerkTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukMerkTidakBolehKosong popup = new PopUp_SmallTambahProdukMerkTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showEditProductBerhasilDiEdit(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditProductSuksesDiEdit popup = new PopUp_SmallEditProductSuksesDiEdit(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransJualStokProdukTidakTersedia(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransJualStokProdukTidakTersedia popup = new PopUp_SmallTransJualStokProdukTidakTersedia(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showEditProductGenerateBarcodeTidakBisaDiGanti(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditProductGenerateBarcodeTidakBisa popup = new PopUp_SmallEditProductGenerateBarcodeTidakBisa(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showEditTransJualBerhasilDiSimpan(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliSuksesDiSimpan popup = new PopUp_SmallTransBeliSuksesDiSimpan(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showProdukDisplayDiskonSuksesDiTambahkan(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_ProdukDisplayDiskonSuksesDitambahkan popup = new PopUp_ProdukDisplayDiskonSuksesDitambahkan(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showProdukDisplayDiskonSuksesDiHapus(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_ProdukDisplayDiskonSuksesDiHapus popup = new PopUp_ProdukDisplayDiskonSuksesDiHapus(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showProdukDisplayDiskonNamaSudahAda(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_ProdukDisplayDiskonNamaDiskonSudahAda popup = new PopUp_ProdukDisplayDiskonNamaDiskonSudahAda(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showProdukDisplayDiskonSuksesDiPerbarui(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_ProdukDisplayDiskonSuksesDiPerbarui popup = new PopUp_ProdukDisplayDiskonSuksesDiPerbarui(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showProdukDisplayDiskonGagalDiHapus(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_ProdukDisplayDiskonGagalDiHapus popup = new PopUp_ProdukDisplayDiskonGagalDiHapus(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliKodeProdukTidakDitemukan(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliKodeProdukTidakDitemukanDiDatabase popup = new PopUp_SmallTransBeliKodeProdukTidakDitemukanDiDatabase(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliNamaProdukTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliNamaProdukTidakBolehKosong popup = new PopUp_SmallTransBeliNamaProdukTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliSizeProdukTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliSizeProdukTidakBolehKosong popup = new PopUp_SmallTransBeliSizeProdukTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliHargaBeliTidakBolehKosongAtauNol(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliHargaBeliTidakBolehKosongAtauNol popup = new PopUp_SmallTransBeliHargaBeliTidakBolehKosongAtauNol(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliQuantityTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliQuantityTidakBolehKosong popup = new PopUp_SmallTransBeliQuantityTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliQuantityHarusLebihDari0(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliQuantityHarusLebihDari0 popup = new PopUp_SmallTransBeliQuantityHarusLebihDari0(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliHargaBeliHarusLebihDari0(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliHargaBeliHarusLebihDari0 popup = new PopUp_SmallTransBeliHargaBeliHarusLebihDari0(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliProdukBerhasilDitambahkanKeKeranjang(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliProdukBerhasilDitambahkanKeKeranjang popup = new PopUp_SmallTransBeliProdukBerhasilDitambahkanKeKeranjang(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliFormatQtyDanHargaHarusAngka(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliFormatQtyDanHargaHarusAngka popup = new PopUp_SmallTransBeliFormatQtyDanHargaHarusAngka(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showAturDiskonOwnerDiskonTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong popup = new PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showAturDiskonOwnerAngkaHarus0sampai100(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallAturDiskonOwnerAngkaHarus0sampai100 popup = new PopUp_SmallAturDiskonOwnerAngkaHarus0sampai100(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showStartnDateMasukkanrentangtahun(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallStartnDateMasukkanrentangtahun popup = new PopUp_SmallStartnDateMasukkanrentangtahun(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showStartnDateMasukkanangkatahunyangvalid(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallStartnDateMasukkanangkatahunyangvalid popup = new PopUp_SmallStartnDateMasukkanangkatahunyangvalid(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showStartnDatePilihRentangTanggalLengkap(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallStartnDatePilihRentangTanggalLengkap popup = new PopUp_SmallStartnDatePilihRentangTanggalLengkap(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showEditProductFieldTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditProductFieldTidakBolehKosong popup = new PopUp_SmallEditProductFieldTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showEditProductFieldUkuranAtauStokHarusBerupaAngka(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallEditProductFieldUkuranAtauStokHarusBerupaAngka popup = new PopUp_SmallEditProductFieldUkuranAtauStokHarusBerupaAngka(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showNavBarAtasPenulisanEmailSalah(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallNavBarAtasPenulisanEmailSalah popup = new PopUp_SmallNavBarAtasPenulisanEmailSalah(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showDashboardOwnerBiayaOperasionalSuksesDitambahkan(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallDashboardOwnerBiayaOperasionalSuksesDitambahkan popup = new PopUp_SmallDashboardOwnerBiayaOperasionalSuksesDitambahkan(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showDashboardOwnerStokOpnameSuksesDitambahkan(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallDashboardOwnerStokOpnameSuksesDitambahkan popup = new PopUp_SmallDashboardOwnerStokOpnameSuksesDitambahkan(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliEditQtyHarusAngka(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliEditQtyHarusAngka popup = new PopUp_SmallTransBeliEditQtyHarusAngka(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTransBeliEditQtyDanHargaHarusLebihDari0(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTransBeliEditQtyDanHargaHarusLebihDari0 popup = new PopUp_SmallTransBeliEditQtyDanHargaHarusLebihDari0(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showDashboardKaryawanFieldJumlahTidakBolehLebihDari11(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallDashboardKaryawanFieldJumlahTidakBolehLebihDari11 popup = new PopUp_SmallDashboardKaryawanFieldJumlahTidakBolehLebihDari11(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showProdukNamaProdukTidakBolehHanya0AtauNegatif(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukNamaProdukTidakBolehHanya0AtauNegatif popup = new PopUp_SmallTambahProdukNamaProdukTidakBolehHanya0AtauNegatif(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showProdukUkuranTidakBolehLebihDari5Karakter (JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukUkuranTidakBolehLebihDari5Karakter popup = new PopUp_SmallTambahProdukUkuranTidakBolehLebihDari5Karakter(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showProdukUkuranTidakBoleh0AtauNegatif  (JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukUkuranTidakBoleh0AtauNegatif popup = new PopUp_SmallTambahProdukUkuranTidakBoleh0AtauNegatif(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukNamaTidakBolehLebihDari30Karakter  (JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukNamaProdukTidakBolehLebihDari30Karakter popup = new PopUp_SmallTambahProdukNamaProdukTidakBolehLebihDari30Karakter(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showTambahProdukMerkTidakBolehLebihDari15Karakter  (JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukMerkTidakBolehLebihDari15Karakter popup = new PopUp_SmallTambahProdukMerkTidakBolehLebihDari15Karakter(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showMerkTidakBolehHanya0AtauNegatif  (JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallTambahProdukMerkTidakBolehHanya0AtauNegatif popup = new PopUp_SmallTambahProdukMerkTidakBolehHanya0AtauNegatif(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopUp.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopUp.unregisterPopup(this);
                    }   
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
}
