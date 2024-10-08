package com.manager.appbanhangonline.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.retrofit.ApiBanHang;
import com.manager.appbanhangonline.retrofit.RetrofitClient;
import com.manager.appbanhangonline.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThongKeActivity extends AppCompatActivity {
    Toolbar toolbar;
    PieChart pieChart;
    BarChart barChart;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<PieEntry> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        initView();
        getSupportBar();
        getThongkemathang();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_thongke, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.thongkedoanhthu) {
            getThongkethang();
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            return true;
        } else if (id == R.id.thongkemathang) {
            getThongkemathang();
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getThongkemathang() {
        compositeDisposable.add(apiBanHang.thongke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    thongKeModel -> {
                        if (thongKeModel.isSuccess()) {
                            for (int i = 0; i < thongKeModel.getResult().size(); i++) {
                                int idsp = thongKeModel.getResult().get(i).getIdsp();
                                String tensp = thongKeModel.getResult().get(i).getTensp();
                                int tong = thongKeModel.getResult().get(i).getTong();
                                list.add(new PieEntry(idsp, tensp, tong));
                            }
                            PieDataSet pieDataSet = new PieDataSet(list, "Thống kê ");
                            PieData data = new PieData();
                            data.setDataSet(pieDataSet);
                            data.setValueTextSize(12f);
                            data.setValueFormatter(new PercentFormatter());
                            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                            pieChart.setData(data);
                            pieChart.animateXY(1000, 1000);
                            pieChart.setUsePercentValues(true);
                            pieChart.getDescription().setEnabled(true);
                            pieChart.invalidate();
                        }
                    },
                    throwable -> {
                        Log.d("Error", throwable.getMessage());
                    }
            ));
    }

    private void getThongkethang() {
        settingBarChart();
        compositeDisposable.add(apiBanHang.thongkedoanhthu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        thongKeDoanhThuModel -> {
                            if (thongKeDoanhThuModel.isSuccess()) {
                                List<BarEntry> listData = new ArrayList<>();
                                for (int i = 0; i < thongKeDoanhThuModel.getResult().size(); i++) {
                                    int thang = thongKeDoanhThuModel.getResult().get(i).getThang();
                                    float tongdoanhthu = thongKeDoanhThuModel.getResult().get(i).getTongdoanhthu();
                                    listData.add(new BarEntry(thang, tongdoanhthu));
                                }
                                BarDataSet barDataSet = new BarDataSet(listData, "Thống kê doanh thu");
                                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                barDataSet.setValueTextSize(12f);
                                barDataSet.setValueTextColor(Color.RED);

                                BarData data = new BarData(barDataSet);
                                barChart.setData(data);
                                barChart.invalidate();
                                barChart.animateXY(1000, 1000);
                            }
                        },
                        throwable -> {

                        }
                ));
    }

    private void settingBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        XAxis axis = barChart.getXAxis();
        axis.setAxisMinimum(1);
        axis.setAxisMaximum(12);
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setAxisMinimum(0);
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
    }

    private void getSupportBar() {
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
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        list = new ArrayList<PieEntry>();
        toolbar = findViewById(R.id.toolbarThongKe);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
    }
}