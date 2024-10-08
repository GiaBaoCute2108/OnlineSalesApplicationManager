package com.manager.appbanhangonline.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.databinding.ActivityThemspBinding;
import com.manager.appbanhangonline.model.MessageModel;
import com.manager.appbanhangonline.model.SanPhamMoi;
import com.manager.appbanhangonline.retrofit.ApiBanHang;
import com.manager.appbanhangonline.retrofit.RetrofitClient;
import com.manager.appbanhangonline.utils.Utils;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemSPActivity extends AppCompatActivity {
    Spinner spinner;
    int loai = 0;
    ActivityThemspBinding binding;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String mediaPath;
    SanPhamMoi sanPhamMoi;
    boolean flag;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemspBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initData();
        sanPhamMoi = (SanPhamMoi) getIntent().getSerializableExtra("sanpham");
        if (sanPhamMoi == null) {
            flag = false;
        } else {
            flag = true;
            binding.tensp.setText(sanPhamMoi.getTensp());
            binding.hinhanhsp.setText(sanPhamMoi.getHinhanh());
            binding.giasp.setText(sanPhamMoi.getGia());
            binding.motasp.setText(sanPhamMoi.getMota());
            binding.videpsp.setText(sanPhamMoi.getLinkvideo());
            binding.soluongtonkho.setText(sanPhamMoi.getSoluong()+"");
            for (int i = 0; i < binding.spinnerLoai.getCount(); i++) {
                if (binding.spinnerLoai.getItemAtPosition(i).equals("Điện thoại") && sanPhamMoi.getLoai() == 1) {
                    binding.spinnerLoai.setSelection(i);
                } else if (binding.spinnerLoai.getItemAtPosition(i).equals("Laptop") && sanPhamMoi.getLoai() == 2) {
                    binding.spinnerLoai.setSelection(i);
                }
            }
            binding.btnThem.setText("Sửa sản phẩm");
        }
    }

    private void suaSanPham() {
        String str_ten = String.valueOf(binding.tensp.getText());
        String str_mota = String.valueOf(binding.motasp.getText());
        String str_gia = String.valueOf(binding.giasp.getText());
        String str_hinhanh = String.valueOf(binding.hinhanhsp.getText());
        String str_linkvideo = String.valueOf(binding.videpsp.getText());
        String str_soluong = String.valueOf(binding.soluongtonkho.getText());
        try {
            if (str_ten.isEmpty()) {
                throw new IllegalArgumentException("Không được bỏ trống tên!");
            } else if (str_mota.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống mô tả!");
            } else if (str_hinhanh.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống hình ảnh!");
            } else if (str_gia.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống giá sản phẩm!");
            } else if (str_linkvideo.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống link video!");
            } else if (str_soluong.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống số lượng!");
            } else {
                compositeDisposable.add(apiBanHang.suasanpham(sanPhamMoi.getId(),str_ten, str_hinhanh, str_gia, loai, str_mota, str_linkvideo, Integer.parseInt(str_soluong))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                messageModel -> {
                                    if (messageModel.isSuccess()) {
                                        Toast.makeText(this, messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        Intent back = new Intent(getApplicationContext(), QuanLyActivity.class);
                                        startActivity(back);
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        ));
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        String[] arr = new String[]{"Vui lòng chọn loại sản phẩm", "Điện thoại", "Laptop"};
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, arr));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loai = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    suaSanPham();
                } else {
                    themsp();
                }
            }
        });
        binding.btnhinhanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ThemSPActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    private String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }

        return result;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath = data.getDataString();
        uploadMultipleFiles();
        Log.d("log", "onActivityResult: " + mediaPath);
    }

    // Uploading Image/Video
    private void uploadMultipleFiles() {
        Uri uri = Uri.parse(mediaPath);

        File file = new File(getPath(uri));
        // Parsing any Media type file
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        Call<MessageModel> call = apiBanHang.uploadFile(fileToUpload1);
        call.enqueue(new Callback< MessageModel >() {
            @Override
            public void onResponse(Call < MessageModel > call, Response< MessageModel > response) {
                MessageModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSuccess()) {
                        binding.hinhanhsp.setText(serverResponse.getName());
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.v("Response", serverResponse.toString());
                }
            }
            @Override
            public void onFailure(Call < MessageModel > call, Throwable t) {
                Log.d("log", t.getMessage());
            }
        });
    }

    public void themsp() {
        String str_ten = String.valueOf(binding.tensp.getText());
        String str_mota = String.valueOf(binding.motasp.getText());
        String str_gia = String.valueOf(binding.giasp.getText());
        String str_hinhanh = String.valueOf(binding.hinhanhsp.getText());
        String str_linkvideo = String.valueOf(binding.videpsp.getText());
        String str_soluong = String.valueOf(binding.soluongtonkho.getText());
        try {
            if (str_ten.isEmpty()) {
                throw new IllegalArgumentException("Không được bỏ trống tên!");
            } else if (str_mota.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống mô tả!");
            } else if (str_hinhanh.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống hình ảnh!");
            } else if (str_gia.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống giá sản phẩm!");
            } else if (str_linkvideo.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống link video!");
            } else if (str_soluong.isEmpty()) {
                throw new IllegalArgumentException("không được bỏ trống số lượng!");
            } else {
                compositeDisposable.add(apiBanHang.themsp(str_ten, str_hinhanh, str_gia, loai, str_mota, str_linkvideo, Integer.parseInt(str_soluong))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                messageModel -> {
                                    if (messageModel.isSuccess()) {
                                        Toast.makeText(this, messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        Intent back = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(back);
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        ));
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        spinner = findViewById(R.id.spinner_loai);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}