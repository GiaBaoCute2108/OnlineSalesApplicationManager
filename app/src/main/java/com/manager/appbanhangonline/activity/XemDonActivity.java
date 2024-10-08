package com.manager.appbanhangonline.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.adapter.DonHangAdapter;
import com.manager.appbanhangonline.model.DonHang;
import com.manager.appbanhangonline.model.EventBus.DonHangEvent;
import com.manager.appbanhangonline.retrofit.ApiBanHang;
import com.manager.appbanhangonline.retrofit.ApiPushNotification;
import com.manager.appbanhangonline.retrofit.RetrofitClient;
import com.manager.appbanhangonline.retrofit.RetrofitClientNoti;
import com.manager.appbanhangonline.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    Toolbar toolbar;
    RecyclerView recyclerView;
    DonHangAdapter adapter;
    List<DonHang> list = new ArrayList<>();
    DonHang donHang;
    int tinhtrang = 0;
    String body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don_activity);
        initView();
        getToolBarSupport();
        getData();
    }

    private void getToolBarSupport() {
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
        compositeDisposable.add(apiBanHang.getDonHang(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                      donHangModel -> {
                          list.clear();
                              if (donHangModel.isSuccess()) {
                                  if (!donHangModel.getResult().isEmpty()) {
                                      list.addAll(donHangModel.getResult());
                                      adapter = new DonHangAdapter(getApplicationContext(), list);
                                      recyclerView.setAdapter(adapter);
                                  }
                              }
                      }
                      ,throwable -> {

                        }
                ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbar_ls);
        recyclerView = findViewById(R.id.recycleview_donhang);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventDonHang(DonHangEvent event) {
        if (event != null) {
            donHang = event.getDonHang();
            showDonHangDialog();
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    private void showDonHangDialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.dialog_donhang, null);
        Spinner spinner = v.findViewById(R.id.sp_dialog);
        AppCompatButton bt = v.findViewById(R.id.btTrangthai);
        String[] listTrangthai = new String[] {
                "Đơn hàng đang được xử lý",
                "Đơn hàng đã được xử lý",
                "Đơn hàng đã được giao cho đơn vị vận chuyển",
                "Đơn hàng đang được giao",
                "Giao hàng thành công",
                "Đơn hàng đã hủy"
        };
        spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listTrangthai));
        spinner.setSelection(donHang.getTrangthai());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                body = (String) spinner.getItemAtPosition(position);
                tinhtrang = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDonHang(donHang.getId(), tinhtrang, alertDialog, body);
                getData();
            }
        });
    }

    private void updateDonHang(int id, int trangthai, AlertDialog alertDialog, String body) {
        compositeDisposable.add(apiBanHang.updateDonHang(id, trangthai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                getData();
                                pushNotiToUser(id, body);
                            } else {
                                Log.d("ERROR", messageModel.getMessage());
                            }
                        },
                        throwable -> {
                            Log.d("ERROR", throwable.getMessage());
                        }
                ));
    }

    private void pushNotiToUser(int id, String body) {
        //get token
        compositeDisposable.add(apiBanHang.gettoken(0, donHang.getIduser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                for(int i = 0; i < userModel.getResult().size(); i++) {
                                    Map<String, String> notification = new HashMap<>();
                                    notification.put("title", "Đơn hàng id: " + id);
                                    notification.put("body", body);

                                    Map<String, Object> message = new HashMap<>();
                                    message.put("token", userModel.getResult().get(i).getToken());
                                    message.put("notification", notification);

                                    HashMap<String, Object> requestBody = new HashMap<>();
                                    requestBody.put("message", message);

                                    ApiPushNotification apiPushNotification = RetrofitClientNoti.getInstance().create(ApiPushNotification.class);
                                    compositeDisposable.add(apiPushNotification.sendNotification(requestBody)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiResponse -> {
                                                        Log.d("pushNotiToUser", "Notification sent successfully: " + new Gson().toJson(notiResponse));
                                                    },
                                                    throwable -> {
                                                        Log.e("pushNotiToUser", "Error sending notification: " + throwable.getMessage(), throwable);
                                                    }
                                            ));
                                }
                            }
                        },
                        throwable -> {
                            Log.d("token", throwable.getMessage());
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}