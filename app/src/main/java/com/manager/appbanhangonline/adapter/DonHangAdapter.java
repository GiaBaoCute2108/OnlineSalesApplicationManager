package com.manager.appbanhangonline.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.appbanhangonline.Interface.ItemClickListener;
import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.activity.ChiTietDonHangActivity;
import com.manager.appbanhangonline.model.DonHang;
import com.manager.appbanhangonline.model.EventBus.DonHangEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    Context context;
    List<DonHang> list;

    public DonHangAdapter(Context context, List<DonHang> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DonHangAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DonHangAdapter.MyViewHolder holder, int position) {
        DonHang donHang = list.get(position);
        if (donHang != null) {
            holder.id.setText("Id đơn hàng: " + donHang.getId());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            holder.tongtien.setText("Tổng tiền: " + decimalFormat.format(Double.parseDouble(donHang.getTongtien())) + "đ");
            holder.sdt.setText("Số điện thoại: " + donHang.getSodienthoai());
            holder.diachi.setText("Địa chỉ: " + donHang.getDiachi());
            holder.trangthai.setText(getTrangThai(donHang.getTrangthai()));
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {
                    if (isLongClick) {
                        EventBus.getDefault().postSticky(new DonHangEvent(donHang));
                    } else {
                        Intent chitietdonhang = new Intent(context, ChiTietDonHangActivity.class);
                        chitietdonhang.putExtra("iddonhang", donHang.getId());
                        chitietdonhang.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(chitietdonhang);
                    }
                }
            });
        }
    }

    public String getTrangThai(int num) {
        String result = "";
        switch (num) {
            case 0: result = "Đơn hàng đang được xử lý";
                    break;
            case 1: result = "Đơn hàng đã được xử lý";
                    break;
            case 2: result = "Đơn hàng đã được giao cho đơn vị vận chuyển";
                break;
            case 3: result = "Đơn hàng đang được giao";
                break;
            case 4: result = "Giao hàng thành công";
                break;
            case 5: result = "Đơn hàng đã hủy";
                break;
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView id, tongtien, sdt, diachi, trangthai;
        ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id_donhang);
            tongtien = itemView.findViewById(R.id.tongtienDonHang);
            sdt = itemView.findViewById(R.id.sdtKhachhang);
            diachi = itemView.findViewById(R.id.diachiDonHang);
            trangthai = itemView.findViewById(R.id.trangthai);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }
}
