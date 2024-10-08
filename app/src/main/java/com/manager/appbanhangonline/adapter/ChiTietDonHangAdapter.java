package com.manager.appbanhangonline.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.appbanhangonline.Interface.ItemClickListener;
import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.model.Item;

import java.util.List;

public class ChiTietDonHangAdapter extends RecyclerView.Adapter<ChiTietDonHangAdapter.MyViewHolder> {
    Context context;
    List<Item> list;

    public ChiTietDonHangAdapter(Context context, List<Item> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChiTietDonHangAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_ctdh, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChiTietDonHangAdapter.MyViewHolder holder, int position) {
        Item item = list.get(position);
            Glide.with(context).load(item.getImg()).into(holder.image);
            holder.id.setText("Id sản phẩm: " + item.getId());
            holder.ten.setText(item.getName());
            holder.gia.setText("Giá: " + item.getGiasp());
            holder.soluong.setText("Số lượng: " + item.getSoluong());
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {

                }
            });
    }

    @Override
    public int getItemCount() {
        return !list.isEmpty() ? list.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView ten, id, gia, soluong;
        ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.itemdh_image);
            ten = v.findViewById(R.id.itemdh_ten);
            id = v.findViewById(R.id.itemdh_id);
            gia = v.findViewById(R.id.itemdh_gia);
            soluong = v.findViewById(R.id.itemdh_sl);
            v.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
