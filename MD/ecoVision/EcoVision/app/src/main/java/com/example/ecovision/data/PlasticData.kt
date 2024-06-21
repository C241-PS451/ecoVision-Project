package com.example.ecovision.data

import com.example.ecovision.R

object PlasticData {
    val plasticTypes = listOf(
        PlasticType(
            name = "PET",
            imageResId = R.drawable.pet_image,
            imageIcon = R.drawable.ic_pet,
            recyclingCode = "PET",
            description = "PET (Polyethylene Terephthalate) adalah jenis plastik yang sering digunakan untuk botol minuman dan kemasan makanan karena ringan, kuat, dan transparan. Plastik ini dapat didaur ulang dan diubah menjadi berbagai produk baru.",
            recyclingProcess = "Plastik PET dapat dikumpulkan, dicuci, dipotong kecil-kecil, dilebur, dan dibentuk kembali menjadi produk baru seperti serat untuk pakaian atau karpet.",
            recyclingProcessTwo = "Setelah proses pengumpulan dan pencucian, plastik PET dipanaskan hingga meleleh sebelum dibentuk kembali menjadi produk baru melalui proses ekstrusi atau pencetakan injeksi.", // Nilai baru
            environmentalImpact = "Waktu terurai: 450 tahun. Plastik jenis PET sangat tahan lama di alam dan bisa mencemari lingkungan, terutama lautan.",
            examples = listOf(R.drawable.petexample1, R.drawable.petexample2, R.drawable.petexample3)
        ),
        PlasticType(
            name = "HDPE",
            imageResId = R.drawable.hdpe_image,
            imageIcon = R.drawable.ic_hdpe,
            recyclingCode = "HDPE",
            description = "HDPE (High-Density Polyethylene) adalah jenis plastik yang kuat dan tahan lama, sering digunakan untuk botol susu, botol deterjen, dan pipa.",
            recyclingProcess = "Plastik HDPE dapat didaur ulang dengan cara dicuci, dipotong kecil-kecil, dilebur, dan dibentuk menjadi produk baru seperti pipa dan bahan konstruksi.",
            recyclingProcessTwo = "Setelah pencucian, plastik HDPE diproses melalui mesin granulasi untuk mengubahnya menjadi pelet yang kemudian digunakan dalam pembuatan produk baru.", // Nilai baru
            environmentalImpact = "Waktu terurai: 500 tahun. Plastik HDPE dapat menyebabkan polusi jika tidak didaur ulang dengan benar.",
            examples = listOf(R.drawable.hdpeexample1, R.drawable.hdpeexample2, R.drawable.hdpeexample3)
        ),
        PlasticType(
            name = "PVC",
            imageResId = R.drawable.pvc_image,
            imageIcon = R.drawable.ic_pvc,
            recyclingCode = "PVC",
            description = "PVC (Polyvinyl Chloride) adalah plastik yang sering digunakan untuk pipa, kabel, dan bahan konstruksi karena tahan terhadap cuaca dan bahan kimia.",
            recyclingProcess = "Plastik PVC dapat didaur ulang melalui proses pemanasan dan pembentukan ulang, namun proses ini lebih sulit dibanding jenis plastik lainnya.",
            recyclingProcessTwo = "PVC memerlukan proses stabilisasi tambahan untuk menghilangkan klorin sebelum dilebur dan dibentuk kembali menjadi produk baru.", // Nilai baru
            environmentalImpact = "Waktu terurai: 1000 tahun. PVC mengandung klorin yang dapat menyebabkan polusi jika dibakar.",
            examples = listOf(R.drawable.pvcexample1, R.drawable.pvcexample2, R.drawable.pvcexample3)
        ),
        PlasticType(
            name = "LDPE",
            imageResId = R.drawable.ldpe_image,
            imageIcon = R.drawable.ic_ldpe,
            recyclingCode = "LDPE",
            description = "LDPE (Low-Density Polyethylene) adalah plastik yang sering digunakan untuk kantong plastik, bungkus makanan, dan botol yang dapat diremas.",
            recyclingProcess = "Plastik LDPE dapat didaur ulang dengan cara dicuci, dipotong kecil-kecil, dilebur, dan dibentuk menjadi produk baru seperti kantong sampah dan ubin lantai.",
            recyclingProcessTwo = "Setelah dipotong kecil-kecil, LDPE dilebur dan diekstrusi menjadi produk baru, seperti lembaran plastik atau kantong sampah.", // Nilai baru
            environmentalImpact = "Waktu terurai: 500-1000 tahun. Plastik LDPE dapat menyebabkan polusi plastik di lautan dan daratan.",
            examples = listOf(R.drawable.ldpeexample1, R.drawable.ldpeexample2, R.drawable.ldpeexample3)
        ),
        PlasticType(
            name = "PP",
            imageResId = R.drawable.pp_image,
            imageIcon = R.drawable.ic_pp,
            recyclingCode = "PP",
            description = "PP (Polypropylene) adalah jenis plastik yang sering digunakan untuk kemasan makanan, sedotan, dan produk otomotif.",
            recyclingProcess = "Plastik PP dapat didaur ulang dengan cara dicuci, dipotong kecil-kecil, dilebur, dan dibentuk menjadi produk baru seperti kontainer dan mainan.",
            recyclingProcessTwo = "Granulasi adalah langkah penting setelah mencuci PP untuk mengubahnya menjadi pelet yang kemudian digunakan untuk proses injeksi atau ekstrusi.", // Nilai baru
            environmentalImpact = "Waktu terurai: 20-30 tahun. Plastik PP lebih mudah didaur ulang dibandingkan beberapa jenis plastik lainnya.",
            examples = listOf(R.drawable.ppexample1, R.drawable.ppexample2, R.drawable.ppexample3)
        ),
        PlasticType(
            name = "PS",
            imageResId = R.drawable.ps_image,
            imageIcon = R.drawable.ic_ps,
            recyclingCode = "PS",
            description = "PS (Polystyrene) adalah plastik yang sering digunakan untuk wadah makanan sekali pakai, cangkir, dan kemasan pelindung.",
            recyclingProcess = "Plastik PS dapat didaur ulang melalui proses pemanasan dan pembentukan ulang, namun biasanya tidak didaur ulang karena murah dan tidak ekonomis.",
            recyclingProcessTwo = "Daur ulang PS membutuhkan proses tambahan untuk menghilangkan kontaminan sebelum dapat dilebur dan dibentuk menjadi produk baru.", // Nilai baru
            environmentalImpact = "Waktu terurai: 500 tahun. Plastik PS dapat menyebabkan polusi lingkungan dan berbahaya bagi kehidupan laut.",
            examples = listOf(R.drawable.psexample1, R.drawable.psexample2, R.drawable.psexample3)
        ),
        PlasticType(
            name = "Other",
            imageResId = R.drawable.other_image,
            imageIcon = R.drawable.ic_other,
            recyclingCode = "Other",
            description = "Kategori ini mencakup semua jenis plastik lainnya yang tidak termasuk dalam kategori di atas. Ini bisa termasuk plastik berbasis biologi dan campuran plastik.",
            recyclingProcess = "Proses daur ulang bervariasi tergantung pada jenis plastiknya.",
            recyclingProcessTwo = "Metode daur ulang berbeda-beda dan dapat mencakup proses kimia atau mekanik untuk mengubah plastik ini menjadi bahan berguna.", // Nilai baru
            environmentalImpact = "Dampak lingkungan sangat bervariasi tergantung pada jenis plastiknya.",
            examples = listOf(R.drawable.otherexample1, R.drawable.otherexample2, R.drawable.otherexample3)
        )
    )
}
