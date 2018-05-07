package com.indomaret.klikindomaret.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.indomaret.klikindomaret.MainActivity;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.SessionManager;

public class IntroActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance("Scan Barcode", "Dengan melakukan scan barcode pada produk, Anda dapat menemukan produk dengan mudah.", R.drawable.intro_scan_barcode, Color.parseColor("#f9c828")));
        addSlide(AppIntroFragment.newInstance("Keranjang Belanja", "Tambahkan Produk ke keranjang Belanja dan dapatkan Promo menarik sehingga Andapun lebih Hemat", R.drawable.intro_keranjang_belanja, Color.parseColor("#f9c828")));
        addSlide(AppIntroFragment.newInstance("Pengiriman Fleksibel", "Anda dapat menentukan hari dan jam pengambilan / pengantaran pesanan sesuai dengan yang inginkan.", R.drawable.intro_pengiriman_fleksibel, Color.parseColor("#f9c828")));
    }

    @Override
    public void onDonePressed() {
        new SessionManager(IntroActivity.this).setIntro();
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNextPressed() {}

    @Override
    public void onSlideChanged() {}
}
