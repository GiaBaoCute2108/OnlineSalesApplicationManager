package com.manager.appbanhangonline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.adapter.ChiTietDonHangAdapter;
import com.manager.appbanhangonline.model.Item;
import com.manager.appbanhangonline.retrofit.ApiBanHang;
import com.manager.appbanhangonline.retrofit.RetrofitClient;
import com.manager.appbanhangonline.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChiTietDonHangActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<Item> list = new ArrayList<>();
    ChiTietDonHangAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang);
        initView();
        actionToolBar();
        getData();
    }

    private void actionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        int id = getIntent().getIntExtra("iddonhang", 0);
        compositeDisposable.add(apiBanHang.chitietdonhang(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        itemModel -> {
                            list.addAll(itemModel.getResult());
                            adapter = new ChiTietDonHangAdapter(getApplicationContext(), list);
                            recyclerView.setAdapter(adapter);
                        },
                        throwable -> {

                        }
                ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbar_chitietdh);
        recyclerView = findViewById(R.id.recycleview_chitiet);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}