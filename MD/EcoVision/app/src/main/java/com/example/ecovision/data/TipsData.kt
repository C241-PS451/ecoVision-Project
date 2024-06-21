package com.example.ecovision.data

import java.util.*

class TipsData {

    private val tipsList = listOf(
        "Hindari penggunaan plastik sekali pakai untuk menjaga lingkungan.",
        "Selalu bawa tas belanja kain saat pergi berbelanja.",
        "Gunakan botol minum yang dapat digunakan kembali.",
        "Kurangi penggunaan sedotan plastik, gunakan sedotan stainless atau bambu.",
        "Pilah sampah di rumah untuk memudahkan proses daur ulang.",
        "Gunakan produk yang memiliki kemasan ramah lingkungan.",
        "Buat kompos dari sampah organik di rumah.",
        "Bawa alat makan sendiri untuk mengurangi penggunaan alat makan sekali pakai.",
        "Dukung produk lokal yang menggunakan bahan ramah lingkungan.",
        "Edukasi orang sekitar tentang pentingnya menjaga lingkungan.",
        "Matikan lampu saat tidak digunakan untuk menghemat energi.",
        "Gunakan transportasi umum untuk mengurangi emisi karbon.",
        "Kurangi penggunaan air saat mandi untuk menghemat sumber daya.",
        "Gunakan energi terbarukan seperti panel surya di rumah.",
        "Kurangi penggunaan kertas dengan memanfaatkan teknologi digital.",
        "Tanam pohon di sekitar rumah untuk membantu lingkungan.",
        "Gunakan produk daur ulang untuk mengurangi sampah.",
        "Beli produk dengan sedikit kemasan untuk mengurangi sampah.",
        "Kurangi konsumsi daging untuk mengurangi jejak karbon.",
        "Daur ulang barang elektronik untuk mengurangi limbah berbahaya.",
        "Bawa botol minum sendiri ke mana pun Anda pergi.",
        "Hemat listrik di rumah dengan mematikan perangkat tidak terpakai.",
        "Kurangi emisi karbon dengan memilih transportasi ramah lingkungan.",
        "Gunakan alat makan dari bahan alami untuk mengurangi plastik.",
        "Buat kompos dari sampah dapur untuk mengurangi limbah.",
        "Daur ulang kaca dan logam untuk mengurangi limbah.",
        "Kurangi penggunaan kantong plastik saat berbelanja.",
        "Gunakan sabun ramah lingkungan untuk menjaga kesehatan bumi.",
        "Kurangi pemakaian tisu dengan menggunakan kain lap.",
        "Belanja di pasar lokal untuk mendukung ekonomi lokal.",
        "Gunakan baterai isi ulang untuk mengurangi limbah baterai.",
        "Tanam sayuran di rumah untuk mengurangi jejak karbon.",
        "Kurangi pemakaian AC untuk menghemat energi.",
        "Simpan makanan dalam wadah tahan lama dan bukan plastik.",
        "Bawa mug sendiri ke kafe untuk mengurangi sampah gelas sekali pakai.",
        "Pilih produk dengan bahan alami dan ramah lingkungan.",
        "Gunakan transportasi berkelanjutan seperti sepeda atau berjalan kaki.",
        "Gunakan lampu hemat energi untuk mengurangi konsumsi listrik.",
        "Hemat penggunaan air dengan memeriksa kebocoran.",
        "Edukasi anak tentang pentingnya menjaga lingkungan sejak dini.",
        "Gunakan produk pembersih alami yang aman bagi lingkungan.",
        "Jangan buang sampah sembarangan, selalu buang di tempatnya.",
        "Gunakan kain lap untuk membersihkan, bukan tisu sekali pakai.",
        "Kurangi pembelian barang baru dengan memperbaiki barang rusak.",
        "Perbaiki barang rusak sebelum memutuskan untuk membuangnya.",
        "Donasi barang yang tidak terpakai agar bisa dimanfaatkan orang lain.",
        "Gunakan barang bekas pakai untuk mengurangi limbah.",
        "Pilih produk organik yang baik untuk kesehatan dan lingkungan.",
        "Bawa bekal makanan dari rumah untuk mengurangi penggunaan plastik."
    )

    fun getTipForToday(): String {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val randomIndex = dayOfYear % tipsList.size
        return tipsList[randomIndex]
    }
}
