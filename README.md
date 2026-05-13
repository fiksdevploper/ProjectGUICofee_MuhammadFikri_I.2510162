<div align="center">

# ☕ KafeGUI — Sistem Kasir Kafe Arjuna

<p>
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/GUI-Java%20Swing-5382A1?style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/OOP-Abstract%20%7C%20Interface%20%7C%20Inner%20Class-4CAF50?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Status-Selesai-brightgreen?style=for-the-badge"/>
</p>

<p>Aplikasi kasir berbasis GUI (Java Swing) untuk simulasi sistem pemesanan di kafe.<br/>
Dibangun sebagai tugas praktikum Pemrograman Berorientasi Objek dengan menerapkan konsep <strong>Abstract Class, Interface, Inner Class, dan Inheritance</strong>.</p>

</div>

---

## 🖥️ Tampilan Aplikasi

```
╔══════════════════════════════════════╗
║   ☕  Kafe Arjuna   |   POS v1.0    ║
╠══════════════════════════════════════╣
║  📋 Data Pesanan                     ║
║  ┌────────────────────────────────┐  ║
║  │ Nama Pelanggan : Budi Santoso  │  ║
║  │ Nomor Meja     : A3            │  ║
║  │ Menu           : Caffe Latte ▼ │  ║
║  │ Harga Satuan   : Rp 28.000     │  ║
║  │ Jumlah (item)  : [2]           │  ║
║  │ Metode Bayar   : QRIS/E-Wallet │  ║
║  └────────────────────────────────┘  ║
║  🧾 Struk Pembayaran                 ║
║  ┌────────────────────────────────┐  ║
║  │ ══ KAFE ARJUNA ══             │  ║
║  │ Pelanggan : Budi Santoso      │  ║
║  │ Subtotal  : Rp 56.000         │  ║
║  │ PPN 11%   : Rp 6.160          │  ║
║  │ TOTAL     : Rp 62.160         │  ║
║  │ Status    : ✓ LUNAS — QRIS    │  ║
║  └────────────────────────────────┘  ║
║  [ ✓ Proses ] [ ↺ Reset ] [ ✕ Keluar ]  ║
╚══════════════════════════════════════╝
```

---

## 📦 Struktur Project

```
KafeGUI/
├── KafeGUI.java          # Main class — GUI Swing + implementasi logika
├── TransaksiKafe.java    # Abstract class + Inner Class
├── Pembayaran.java       # Interface
└── KafeGUI.jar           # Executable JAR (siap dijalankan)
```

---

## 🧠 Konsep OOP yang Diterapkan

### 1. 🔷 Abstract Class & Abstract Method
```java
// TransaksiKafe.java
public abstract class TransaksiKafe {
    protected String namaPelanggan;
    protected String nomorMeja;

    abstract long hitungSubtotal(int jumlah, long hargaSatuan);  // wajib di-override
    abstract long hitungPajak(long subtotal);                    // wajib di-override
}
```

### 2. 🔶 Interface
```java
// Pembayaran.java
public interface Pembayaran {
    double PAJAK_PERSEN = 11.0;      // konstanta PPN

    void prosesBayar(String metodeBayar);
    String getStatusBayar();
}
```

### 3. 🔵 Inner Class
```java
// Di dalam TransaksiKafe.java
class DetailPesanan {
    String namaMenu;
    String kategori;
    int    jumlah;
    long   hargaSatuan;

    String tampilDetail() { ... }    // format struk detail pesanan
}
```

### 4. 🟢 Inheritance + Polymorphism
```java
// KafeGUI.java
public class KafeGUI extends TransaksiKafe implements Pembayaran {

    @Override
    long hitungSubtotal(int jumlah, long hargaSatuan) {
        return (long) jumlah * hargaSatuan;
    }

    @Override
    long hitungPajak(long subtotal) {
        return Math.round(subtotal * (PAJAK_PERSEN / 100.0));
    }

    @Override
    public void prosesBayar(String metodeBayar) { ... }

    @Override
    public String getStatusBayar() { return statusBayar; }
}
```

---

## 📐 Diagram Relasi Kelas (Class Diagram)

```
         ┌─────────────────────┐
         │  <<interface>>      │
         │    Pembayaran       │
         │─────────────────────│
         │ + PAJAK_PERSEN: 11% │
         │ + prosesBayar()     │
         │ + getStatusBayar()  │
         └──────────┬──────────┘
                    │ implements
                    │
  ┌─────────────────────────┐
  │  <<abstract>>           │
  │    TransaksiKafe        │
  │─────────────────────────│
  │ # namaPelanggan: String │
  │ # nomorMeja: String     │
  │─────────────────────────│
  │ + hitungSubtotal() ✦    │
  │ + hitungPajak() ✦       │
  │                         │
  │  ┌──────────────────┐   │
  │  │  DetailPesanan   │   │  ← Inner Class
  │  │──────────────────│   │
  │  │ namaMenu         │   │
  │  │ kategori         │   │
  │  │ jumlah           │   │
  │  │ hargaSatuan      │   │
  │  │ tampilDetail()   │   │
  │  └──────────────────┘   │
  └──────────┬──────────────┘
             │ extends
             │
  ┌──────────┴──────────────┐
  │       KafeGUI           │
  │─────────────────────────│
  │ - frame: JFrame         │
  │ - txtNama, txtMeja      │
  │ - cmbMenu, cmbBayar     │
  │ - spnJumlah             │
  │─────────────────────────│
  │ + hitungSubtotal() ✔    │
  │ + hitungPajak() ✔       │
  │ + prosesBayar() ✔       │
  │ + getStatusBayar() ✔    │
  │ + prosesPesanan()       │
  │ + main()                │
  └─────────────────────────┘

  ✦ = abstract method    ✔ = override/implement
```

---

## 🍽️ Daftar Menu

| Kategori | Menu | Harga |
|----------|------|-------|
| ☕ Kopi | Espresso | Rp 18.000 |
| ☕ Kopi | Cappuccino | Rp 25.000 |
| ☕ Kopi | Caffe Latte | Rp 28.000 |
| 🍵 Non-Kopi | Matcha Latte | Rp 30.000 |
| ☕ Kopi | Vietnamese Coffee | Rp 22.000 |
| ☕ Kopi | Americano | Rp 20.000 |
| 🍞 Makanan | Croissant Butter | Rp 22.000 |
| 🥪 Makanan | Club Sandwich | Rp 35.000 |
| 🥞 Makanan | Pancake Stack | Rp 30.000 |
| 🧇 Makanan | Banana Waffle | Rp 32.000 |

---

## 🚀 Cara Menjalankan

### Prasyarat
- Java JDK 11 atau lebih baru sudah terinstall

### Opsi 1 — Jalankan JAR (Paling Mudah)
```bash
java -jar KafeGUI.jar
```

### Opsi 2 — Compile dari Source
```bash
# Clone repo
git clone https://github.com/username/KafeGUI.git
cd KafeGUI

# Compile
javac KafeGUI/*.java

# Jalankan
java KafeGUI.KafeGUI
```

### Cek versi Java
```bash
java -version
# output: openjdk version "21.x.x" ...
```

---

## ✅ Fitur Aplikasi

- [x] **Form Input** — nama pelanggan, nomor meja, pilih menu, jumlah item
- [x] **Auto-fill Harga** — harga otomatis muncul saat menu dipilih
- [x] **Perhitungan Otomatis** — subtotal, PPN 11%, dan total bayar
- [x] **3 Metode Pembayaran** — Tunai, Kartu Debit, QRIS / E-Wallet
- [x] **Struk Digital** — tampil lengkap dengan tanggal, waktu, dan status pembayaran
- [x] **Validasi Input** — pesan peringatan jika form belum lengkap
- [x] **Tombol Reset** — bersihkan semua form untuk transaksi baru
- [x] **Konfirmasi Keluar** — dialog konfirmasi sebelum menutup aplikasi
- [x] **Tampilan Modern** — tema warna kopi dengan Swing custom UI

---

## 🛠️ Teknologi

| Komponen | Detail |
|----------|--------|
| Bahasa | Java 21 |
| GUI Framework | Java Swing (`javax.swing`) |
| Build | `javac` + `jar` |
| IDE yang direkomendasikan | IntelliJ IDEA / NetBeans / VS Code |

---

## 📚 Mata Kuliah

> **Pemrograman Berorientasi Objek (PBO)**  
> Program Studi Teknik Informatika  
> Universitas Djuanda Bogor

---

## 👤 Author

**Muhammad Fikri**  
[![GitHub](https://img.shields.io/badge/GitHub-fiksdevploper-181717?style=flat&logo=github)](https://github.com/fiksdevploper)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-muhammad--fikri-0A66C2?style=flat&logo=linkedin)](https://linkedin.com/in/muhammad-fikri-b3766a2b1)

---

<div align="center">
<sub>☕ Dibuat dengan semangat ngopi dan banyak debugging</sub>
</div>
