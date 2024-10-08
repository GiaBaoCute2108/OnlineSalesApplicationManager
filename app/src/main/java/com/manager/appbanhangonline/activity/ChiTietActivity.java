package com.manager.appbanhangonline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.model.SanPhamMoi;
import com.manager.appbanhangonline.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;

public class ChiTietActivity extends AppCompatActivity {

    TextView txttensp, txtgiasp, mota, txtsoluong;
    ImageView image;
    Toolbar toolbar;
    SanPhamMoi sanPhamMoi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);
        initView();
        ActionToolBar();
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        sanPhamMoi = (SanPhamMoi) getIntent().getSerializableExtra("sanpham");
        if (sanPhamMoi != null) {
            Glide.with(getApplicationContext()).load(sanPhamMoi.getHinhanh()).into(image);
            txttensp.setText(sanPhamMoi.getTensp());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            txtgiasp.setText("Giá: " + decimalFormat.format(Double.parseDouble(sanPhamMoi.getGia())) + "đ");
            txtsoluong.setText("Số lượng tồn kho: " + sanPhamMoi.getSoluong());
            mota.setText(sanPhamMoi.getMota());
        } else {
            Toast.makeText(this, "Khong lay duoc du lieu", Toast.LENGTH_SHORT).show();
        }
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView() {
        txttensp=findViewById(R.id.txttensp);
        txtgiasp=findViewById(R.id.txtgiasp);
        txtsoluong = findViewById(R.id.txtsoluong);
        mota=findViewById(R.id.txtChitiet);
        image=findViewById(R.id.imgchitiet);
        toolbar=findViewById(R.id.toolbarCT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}