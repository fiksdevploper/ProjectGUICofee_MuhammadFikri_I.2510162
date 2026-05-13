package KafeGUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ╔══════════════════════════════════════════╗
 *  KafeGUI — Sistem Kasir Kafe Arjuna
 *  Menggunakan: Abstract Class, Interface,
 *               Inner Class, Inheritance, GUI
 * ╚══════════════════════════════════════════╝
 *
 * Konsep OOP yang diterapkan:
 *  - Extends  : KafeGUI -> TransaksiKafe   (Inheritance + Abstract Class)
 *  - Implements: KafeGUI -> Pembayaran     (Interface)
 *  - Inner Class: DetailPesanan di dalam TransaksiKafe
 */
public class KafeGUI extends TransaksiKafe implements Pembayaran {

    // ── Warna Tema ────────────────────────────────────────────────────────
    private static final Color BG         = new Color(0xFAF3EC);
    private static final Color HEADER_BG  = new Color(0x2C1503);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color ACCENT     = new Color(0xB5651D);
    private static final Color BTN_PROSES = new Color(0x6F4E37);
    private static final Color BTN_RESET  = new Color(0x8B7355);
    private static final Color BTN_KELUAR = new Color(0x7B3F00);
    private static final Color STRUK_BG   = new Color(0x1C1C1C);
    private static final Color STRUK_FG   = new Color(0xE8D5B7);
    private static final Color LABEL_FG   = new Color(0x5C3317);
    private static final Color BORDER_CLR = new Color(0xE0C9A6);

    // ── Data Menu ─────────────────────────────────────────────────────────
    private static final String[] MENU_NAMES = {
        "── Pilih Menu ──",
        "Espresso",
        "Cappuccino",
        "Caffe Latte",
        "Matcha Latte",
        "Vietnamese Coffee",
        "Americano",
        "── Makanan ──",
        "Croissant Butter",
        "Club Sandwich",
        "Pancake Stack",
        "Banana Waffle"
    };
    private static final long[] MENU_PRICES = {
        0,
        18_000, 25_000, 28_000, 30_000, 22_000, 20_000,
        0,
        22_000, 35_000, 30_000, 32_000
    };
    private static final String[] MENU_CAT = {
        "",
        "Kopi", "Kopi", "Kopi", "Non-Kopi", "Kopi", "Kopi",
        "",
        "Makanan", "Makanan", "Makanan", "Makanan"
    };

    // ── Komponen GUI ──────────────────────────────────────────────────────
    private JFrame     frame;
    private JTextField txtNama, txtMeja;
    private JComboBox<String> cmbMenu, cmbBayar;
    private JSpinner   spnJumlah;
    private JLabel     lblHarga;
    private JTextArea  txtStruk;
    private JButton    btnProses, btnReset, btnKeluar;

    // ── State ─────────────────────────────────────────────────────────────
    private String statusBayar = "-";

    // ── Constructor ───────────────────────────────────────────────────────
    KafeGUI() {
        super("", "");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        buatUI();
    }

    // ─────────────────────────────────────────────────────────────────────
    //  ABSTRACT METHOD IMPLEMENTATION (dari TransaksiKafe)
    // ─────────────────────────────────────────────────────────────────────

    @Override
    long hitungSubtotal(int jumlah, long hargaSatuan) {
        return (long) jumlah * hargaSatuan;
    }

    @Override
    long hitungPajak(long subtotal) {
        return Math.round(subtotal * (PAJAK_PERSEN / 100.0));
    }

    // ─────────────────────────────────────────────────────────────────────
    //  INTERFACE METHOD IMPLEMENTATION (dari Pembayaran)
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public void prosesBayar(String metodeBayar) {
        statusBayar = switch (metodeBayar) {
            case "Tunai (Cash)"   -> "✓ LUNAS — Tunai";
            case "Kartu Debit"    -> "✓ LUNAS — Kartu Debit";
            case "QRIS / E-Wallet"-> "✓ LUNAS — QRIS";
            default               -> "✓ LUNAS";
        };
    }

    @Override
    public String getStatusBayar() {
        return statusBayar;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  LOGIKA PROSES PESANAN
    // ─────────────────────────────────────────────────────────────────────

    private void prosesPesanan() {
        // Validasi input
        if (txtNama.getText().isBlank()) {
            showError("Nama pelanggan tidak boleh kosong!"); return;
        }
        if (txtMeja.getText().isBlank()) {
            showError("Nomor meja tidak boleh kosong!"); return;
        }
        int menuIdx = cmbMenu.getSelectedIndex();
        if (menuIdx <= 0 || MENU_PRICES[menuIdx] == 0) {
            showError("Silakan pilih menu terlebih dahulu!"); return;
        }

        // Ambil data dari form
        namaPelanggan = txtNama.getText().trim();
        nomorMeja     = txtMeja.getText().trim();

        String selectedMenu = MENU_NAMES[menuIdx];
        String kategori     = MENU_CAT[menuIdx];
        long   harga        = MENU_PRICES[menuIdx];
        int    jumlah       = (int) spnJumlah.getValue();
        String metodeBayar  = (String) cmbBayar.getSelectedItem();

        // Buat objek Inner Class
        DetailPesanan detail = new DetailPesanan(selectedMenu, kategori, jumlah, harga);

        // Hitung biaya
        long subtotal = hitungSubtotal(jumlah, harga);
        long pajak    = hitungPajak(subtotal);
        long total    = subtotal + pajak;

        // Proses bayar (interface)
        prosesBayar(metodeBayar);

        // Tampilkan struk
        tampilStruk(detail, subtotal, pajak, total, metodeBayar);
    }

    private void tampilStruk(DetailPesanan detail,
                              long subtotal, long pajak, long total,
                              String metodeBayar) {
        NumberFormat fmt  = NumberFormat.getInstance(new Locale("id", "ID"));
        String       tgl  = new SimpleDateFormat("dd MMM yyyy, HH:mm").format(new Date());
        String       line = "  ═════════════════════════════════";

        String struk =
            "\n" + line + "\n" +
            "      ☕  KAFE ARJUNA  ☕\n" +
            "    Jl. Veteran No.12, Bogor\n" +
            "  ─────────────────────────────────\n" +
            "  " + tgl + "\n" +
            line + "\n" +
            detail.tampilDetail() + "\n" +
            "  ─────────────────────────────────\n" +
            String.format("  Subtotal     : Rp %s%n", fmt.format(subtotal)) +
            String.format("  PPN %.0f%%       : Rp %s%n", PAJAK_PERSEN, fmt.format(pajak)) +
            "  ─────────────────────────────────\n" +
            String.format("  TOTAL BAYAR  : Rp %s%n", fmt.format(total)) +
            "  ─────────────────────────────────\n" +
            "  Metode Bayar : " + metodeBayar + "\n" +
            "  Status       : " + getStatusBayar() + "\n" +
            line + "\n" +
            "    Terima kasih telah berkunjung!\n" +
            "      Sampai jumpa kembali ☕\n" +
            line + "\n";

        txtStruk.setText(struk);
        txtStruk.setCaretPosition(0);
    }

    private void resetForm() {
        txtNama.setText("");
        txtMeja.setText("");
        cmbMenu.setSelectedIndex(0);
        spnJumlah.setValue(1);
        lblHarga.setText("Rp 0");
        cmbBayar.setSelectedIndex(0);
        txtStruk.setText("");
        statusBayar = "-";
        txtNama.requestFocus();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Peringatan",
                JOptionPane.WARNING_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  BUILDER UI
    // ─────────────────────────────────────────────────────────────────────

    private void buatUI() {
        frame = new JFrame("☕ Kafe Arjuna — Sistem Kasir");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 730);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);

        root.add(buatHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(16, 18, 8, 18));
        center.add(buatFormPanel());
        center.add(Box.createVerticalStrut(14));
        center.add(buatStrukPanel());

        root.add(center, BorderLayout.CENTER);
        root.add(buatFooter(), BorderLayout.SOUTH);

        frame.add(root);
        frame.setVisible(true);
    }

    // ── Header ────────────────────────────────────────────────────────────
    private JPanel buatHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(HEADER_BG);
        p.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("☕  Kafe Arjuna");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0xF5C842));

        JLabel sub = new JLabel("Sistem Kasir & Pemesanan");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0xBBA08A));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setBackground(HEADER_BG);
        left.add(title);
        left.add(sub);
        p.add(left, BorderLayout.WEST);

        JLabel badge = new JLabel("POS v1.0", SwingConstants.RIGHT);
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        badge.setForeground(new Color(0x6B4D3A));
        p.add(badge, BorderLayout.EAST);

        return p;
    }

    // ── Form Panel ────────────────────────────────────────────────────────
    private JPanel buatFormPanel() {
        JPanel card = buatCard("📋  Data Pesanan");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(6, 4, 4, 4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(5, 6, 5, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        // Row helper
        int row = 0;

        // Nama Pelanggan
        addFormRow(form, gbc, row++, "Nama Pelanggan", txtNama = buatTextField("Contoh: Budi Santoso"));

        // Nomor Meja
        addFormRow(form, gbc, row++, "Nomor Meja", txtMeja = buatTextField("Contoh: A1, B3, VIP-1"));

        // Pilih Menu
        cmbMenu = new JComboBox<>(MENU_NAMES);
        styleComboBox(cmbMenu);
        cmbMenu.addActionListener(e -> updateHarga());
        addFormRow(form, gbc, row++, "Menu", cmbMenu);

        // Harga
        lblHarga = new JLabel("Rp 0");
        lblHarga.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHarga.setForeground(ACCENT);
        addFormRow(form, gbc, row++, "Harga Satuan", lblHarga);

        // Jumlah
        spnJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        styleSpinner(spnJumlah);
        addFormRow(form, gbc, row++, "Jumlah (item)", spnJumlah);

        // Metode Bayar
        cmbBayar = new JComboBox<>(new String[]{
            "Tunai (Cash)", "Kartu Debit", "QRIS / E-Wallet"
        });
        styleComboBox(cmbBayar);
        addFormRow(form, gbc, row, "Metode Bayar", cmbBayar);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    // ── Struk Panel ───────────────────────────────────────────────────────
    private JPanel buatStrukPanel() {
        JPanel card = buatCard("🧾  Struk Pembayaran");

        txtStruk = new JTextArea(10, 30);
        txtStruk.setEditable(false);
        txtStruk.setBackground(STRUK_BG);
        txtStruk.setForeground(STRUK_FG);
        txtStruk.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtStruk.setBorder(new EmptyBorder(6, 8, 6, 8));
        txtStruk.setCaretColor(STRUK_FG);

        JScrollPane scroll = new JScrollPane(txtStruk);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x3A3A3A), 1));
        scroll.setBackground(STRUK_BG);
        scroll.getVerticalScrollBar().setBackground(STRUK_BG);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Footer / Tombol ───────────────────────────────────────────────────
    private JPanel buatFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 14));
        p.setBackground(new Color(0xEDE0D0));
        p.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));

        btnProses = buatTombol("✓  Proses Pesanan", BTN_PROSES, 170, 40);
        btnReset  = buatTombol("↺  Reset",           BTN_RESET,  100, 40);
        btnKeluar = buatTombol("✕  Keluar",           BTN_KELUAR, 100, 40);

        btnProses.addActionListener(e -> prosesPesanan());
        btnReset.addActionListener(e -> resetForm());
        btnKeluar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                frame, "Yakin ingin menutup aplikasi?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        p.add(btnProses);
        p.add(btnReset);
        p.add(btnKeluar);
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  HELPER METHODS — Styling
    // ─────────────────────────────────────────────────────────────────────

    /** Membuat card panel dengan judul section. */
    private JPanel buatCard(String judul) {
        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(CARD_BG);
        outer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 0, 10, 0)
        ));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        titleBar.setBackground(new Color(0xF5EDE0));
        titleBar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));

        JLabel lbl = new JLabel(judul);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(LABEL_FG);
        titleBar.add(lbl);

        outer.add(titleBar, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(CARD_BG);
        content.setBorder(new EmptyBorder(10, 14, 8, 14));
        outer.add(content, BorderLayout.CENTER);

        // Return content panel so caller can add to it
        return content;
    }

    /** Tambah satu baris label + komponen ke GridBagLayout. */
    private void addFormRow(JPanel form, GridBagConstraints gbc,
                            int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(LABEL_FG);
        form.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(comp, gbc);
    }

    private JTextField buatTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            new EmptyBorder(4, 8, 4, 8)
        ));
        tf.setToolTipText(placeholder);
        tf.setPreferredSize(new Dimension(0, 32));
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        cb.setPreferredSize(new Dimension(0, 32));
    }

    private void styleSpinner(JSpinner sp) {
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sp.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        sp.setPreferredSize(new Dimension(0, 32));
        ((JSpinner.DefaultEditor) sp.getEditor()).getTextField()
                .setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private JButton buatTombol(String text, Color bg, int w, int h) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())
                    g2.setColor(bg.darker());
                else if (getModel().isRollover())
                    g2.setColor(bg.brighter());
                else
                    g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(w, h));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Update label harga saat menu dipilih. */
    private void updateHarga() {
        int idx = cmbMenu.getSelectedIndex();
        long harga = (idx >= 0 && idx < MENU_PRICES.length) ? MENU_PRICES[idx] : 0;
        NumberFormat fmt = NumberFormat.getInstance(new Locale("id", "ID"));
        lblHarga.setText("Rp " + fmt.format(harga));
    }

    // ─────────────────────────────────────────────────────────────────────
    //  MAIN
    // ─────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KafeGUI::new);
    }
}
