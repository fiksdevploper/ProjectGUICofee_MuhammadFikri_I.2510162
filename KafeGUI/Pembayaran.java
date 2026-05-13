package KafeGUI;

/**
 * Interface Pembayaran
 * Mendefinisikan kontrak untuk semua proses pembayaran
 */
public interface Pembayaran {

    // Konstanta pajak (PPN 11%)
    double PAJAK_PERSEN = 11.0;

    void prosesBayar(String metodeBayar);

    String getStatusBayar();
}
