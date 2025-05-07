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
        if (activePopups.isEmpty()) return;

        // Cari popup dengan class yang sama
        for (int i = 0; i < activePopups.size(); i++) {
            JDialog currentPopup = activePopups.get(i);
            
            // Skip jika popup pertama (indeks 0)
            if (i == 0) continue;
            
            // Cek apakah popup sebelumnya berbeda class
            JDialog previousPopup = activePopups.get(i-1);
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
}