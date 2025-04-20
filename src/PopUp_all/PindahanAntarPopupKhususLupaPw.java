package PopUp_all;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.lang.reflect.Method;

public class PindahanAntarPopupKhususLupaPw {

    // Daftar semua popup aktif dari berbagai jenis
    private static ArrayList<JDialog> activePopups = new ArrayList<>();

    // Konstanta untuk dimensi dan jarak antar popup
    private static final int POPUP_WIDTH = 250;
    private static final int POPUP_HEIGHT = 50;
    private static final int POPUP_VERTICAL_GAP = -20;

    public static void registerPopup(JDialog popup) {
        // Tambahkan popup baru ke daftar (di awal/index 0 agar berada di paling atas)
        activePopups.add(0, popup);
        // Segera atur ulang posisi semua popup
        repositionAllPopups();
    }

    public static void unregisterPopup(JDialog popup) {
        // Hapus popup dari daftar
        activePopups.remove(popup);
        // Atur ulang posisi popup yang tersisa
        repositionAllPopups();
    }

    public static void repositionAllPopups() {
        JFrame parentFrame = null;

        // Cari parent frame dari popup pertama (jika ada)
        if (!activePopups.isEmpty()) {
            // Dapatkan parent frame dari popup pertama
            if (activePopups.get(0) instanceof JDialog) {
                JDialog dialog = (JDialog) activePopups.get(0);
                Window owner = dialog.getOwner();
                if (owner instanceof JFrame) {
                    parentFrame = (JFrame) owner;
                }
            }
        }

        // Hitung posisi base Y
        int baseY = -10; // Ubah dari 5 menjadi 20 untuk konsistensi dengan PopUp_SmallEmailTidakBolehKosong
        if (parentFrame != null) {
            baseY = parentFrame.getY() - 10;
        }

        // Hitung posisi base X
        int baseX;
        if (parentFrame != null) {
            baseX = parentFrame.getX() + parentFrame.getWidth() - POPUP_WIDTH - 5;
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            baseX = screenSize.width - POPUP_WIDTH - 5;
        }

        // Atur posisi semua popup dari atas ke bawah
        for (int i = 0; i < activePopups.size(); i++) {
            JDialog popup = activePopups.get(i);
            int newY = baseY + (i * (POPUP_HEIGHT + POPUP_VERTICAL_GAP));

            // Set lokasi popup
            popup.setLocation(baseX, newY);

            // Untuk popup yang memiliki metode setLocationOnClose, gunakan itu
            try {
                Method setLocationMethod = popup.getClass().getDeclaredMethod("setLocationOnClose", int.class, int.class);
                if (setLocationMethod != null) {
                    setLocationMethod.setAccessible(true);
                    setLocationMethod.invoke(popup, baseX, newY);
                }
            } catch (Exception e) {
                // Jika tidak memiliki metode tersebut, abaikan
            }

            // Coba set opacity ke 1.0 jika popup memiliki metode setOpacity
            try {
                Method setOpacityMethod = popup.getClass().getDeclaredMethod("setOpacity", float.class);
                if (setOpacityMethod != null) {
                    setOpacityMethod.setAccessible(true);
                    setOpacityMethod.invoke(popup, 1.0f);
                }
            } catch (Exception e) {
                // Jika tidak memiliki metode tersebut, abaikan
            }
            // Pastikan popup berada di depan
            popup.toFront();
        }
    }

    public static void showPasswordPopup(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password
            PopUp_SmallPasswordtidakBolehKosong popup = new PopUp_SmallPasswordtidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopupKhususLupaPw.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopupKhususLupaPw.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showEmailPopup(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup email
            PopUp_SmallEmailTidakBolehKosong popup = new PopUp_SmallEmailTidakBolehKosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopupKhususLupaPw.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopupKhususLupaPw.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }

    public static void showKonfirmpwPopup(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup konfirmasi password
            PopUp_SmallKonfirmasiPwBaru popup = new PopUp_SmallKonfirmasiPwBaru(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopupKhususLupaPw.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopupKhususLupaPw.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    
    public static void showEmailsalah(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup email salah
            PopUp_SmallPenulisanEmailSalah popup = new PopUp_SmallPenulisanEmailSalah(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopupKhususLupaPw.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopupKhususLupaPw.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    
    public static void showPasswordsalah(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallPasswordSalah popup = new PopUp_SmallPasswordSalah(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopupKhususLupaPw.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopupKhususLupaPw.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showSuksesGantiPw(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallSuksesGantiPw popup = new PopUp_SmallSuksesGantiPw(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopupKhususLupaPw.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopupKhususLupaPw.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
    public static void showKonfirmPwTidakBolehKosong(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            // Buat popup password salah
            PopUp_SmallKonfirmasiPasswordtiadkboelhkosong popup = new PopUp_SmallKonfirmasiPasswordtiadkboelhkosong(parent) {
                @Override
                public void setVisible(boolean visible) {
                    if (visible) {
                        // Daftarkan popup ke pengelola saat ditampilkan
                        PindahanAntarPopupKhususLupaPw.registerPopup(this);
                    } else {
                        // Hapus dari daftar saat disembunyikan
                        PindahanAntarPopupKhususLupaPw.unregisterPopup(this);
                    }
                    super.setVisible(visible);
                }
            };
            // Tampilkan popup
            popup.setVisible(true);
        });
    }
}