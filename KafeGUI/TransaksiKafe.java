package KafeGUI;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Abstract Class TransaksiKafe
 * Menyimpan data pelanggan, nomor meja, dan logika perhitungan dasar.
 * Mengandung Inner Class DetailPesanan untuk merangkum data satu pesanan.
 */
public abstract class TransaksiKafe {

    protected String namaPelanggan;
    protected String nomorMeja;

    TransaksiKafe(String namaPelanggan, String nomorMeja) {
        this.namaPelanggan = namaPelanggan;
        this.nomorMeja     = nomorMeja;
    }

    // ── Abstract methods ────────────────────────────────────────────────────
    abstract long hitungSubtotal(int jumlah, long hargaSatuan);
    abstract long hitungPajak(long subtotal);

    // ── Inner Class ─────────────────────────────────────────────────────────
    class DetailPesanan {

        String namaMenu;
        String kategori;
        int    jumlah;
        long   hargaSatuan;

        DetailPesanan(String namaMenu, String kategori,
                      int jumlah, long hargaSatuan) {
            this.namaMenu    = namaMenu;
            this.kategori    = kategori;
            this.jumlah      = jumlah;
            this.hargaSatuan = hargaSatuan;
        }

        /** Mengembalikan struk detail pesanan dalam format teks. */
        String tampilDetail() {
            NumberFormat fmt = NumberFormat.getInstance(
                    new Locale("id", "ID"));

            return String.format(
                "  Pelanggan    : %s%n" +
                "  Nomor Meja   : %s%n" +
                "  ─────────────────────────────────%n" +
                "  Menu         : %s%n" +
                "  Kategori     : %s%n" +
                "  Jumlah       : %d item%n" +
                "  Harga Satuan : Rp %s",
                namaPelanggan,
                nomorMeja,
                namaMenu,
                kategori,
                jumlah,
                fmt.format(hargaSatuan)
            );
        }
    }
}
