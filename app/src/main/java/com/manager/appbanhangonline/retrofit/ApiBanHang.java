package com.manager.appbanhangonline.retrofit;

import com.manager.appbanhangonline.model.DonHangModel;
import com.manager.appbanhangonline.model.ItemModel;
import com.manager.appbanhangonline.model.LoaiSpModel;
import com.manager.appbanhangonline.model.MessageModel;
import com.manager.appbanhangonline.model.SanPhamMoiModel;
import com.manager.appbanhangonline.model.ThongKeDoanhThu;
import com.manager.appbanhangonline.model.ThongKeDoanhThuModel;
import com.manager.appbanhangonline.model.ThongKeModel;
import com.manager.appbanhangonline.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiBanHang {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();
    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai
    );

    @POST("dangky.php")
    @FormUrlEncoded
    Observable<UserModel> dangky(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid
    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangnhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> reset_pass(
            @Field("email") String email
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOrder(
            @Field("iduser") int iduser,
            @Field("diachi") String diachi,
            @Field("sodienthoai") String sodienthoai,
            @Field("email") String email,
            @Field("soluong") String soluong,
            @Field("tongtien") String tongtien,
            @Field("chitiet") String chitiet
    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> getDonHang(
            @Field("iduser") int iduser
    );

    @POST("updatedonhang.php")
    @FormUrlEncoded
    Observable<MessageModel> updateDonHang(
            @Field("id") int id,
            @Field("trangthai") int trangthai
    );

    @POST("chitietdonhang.php")
    @FormUrlEncoded
    Observable<ItemModel> chitietdonhang(
            @Field("iddonhang") int iddonhang
    );

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> timkiem(
            @Field("key") String key
    );

    @POST("xoasanpham.php")
    @FormUrlEncoded
    Observable<MessageModel> xoa(
            @Field("id") int id
    );

    @POST("suasanpham.php")
    @FormUrlEncoded
    Observable<MessageModel> suasanpham(
            @Field("id") int id,
            @Field("tensp") String tensp,
            @Field("hinhanh") String hinhanh,
            @Field("giasp") String giasp,
            @Field("loai") int loai,
            @Field("mota") String mota,
            @Field("linkvideo") String linkvideo,
            @Field("soluongtonkho") int soluongtonkho
    );

    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updatetoken(
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> gettoken(
            @Field("status") int status,
            @Field("id") int id
    );

    @POST("themsp.php")
    @FormUrlEncoded
    Observable<MessageModel> themsp(
            @Field("tensp") String tensp,
            @Field("hinhanh") String hinhanh,
            @Field("giasp") String giasp,
            @Field("loai") int loai,
            @Field("mota") String mota,
            @Field("linkvideo") String linkvideo,
            @Field("soluong") int soluongtonkho
    );

    @Multipart
    @POST("upload.php")
    Call<MessageModel> uploadFile(@Part MultipartBody.Part file);

    @GET("thongke.php")
    Observable<ThongKeModel> thongke();

    @GET("thongkedoanhthu.php")
    Observable<ThongKeDoanhThuModel> thongkedoanhthu();
}
