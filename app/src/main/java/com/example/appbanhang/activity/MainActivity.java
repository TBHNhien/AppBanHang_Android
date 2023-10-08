package com.example.appbanhang.activity;

import android.content.Context;
import android.database.Observable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanhang.R;
import com.example.appbanhang.adapter.LoaiSpAdapter;
import com.example.appbanhang.model.LoaiSp;
import com.example.appbanhang.model.LoaiSpModel;
import com.example.appbanhang.retrofit.ApiBanHang;
import com.example.appbanhang.retrofit.RetrofitClient;
import com.example.appbanhang.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangLoaiSp;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        Anhxa();
        ActionBar();
//        ActionViewFlipper();


       // Toast.makeText(getApplicationContext(),"TEST TOOL CHAY KHONG",Toast.LENGTH_LONG).show();
        // Sử dụng Handler để đặt thời gian delay trước khi kiểm tra kết nối internet
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isConnected(MainActivity.this)) {
//                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "khong co internet", Toast.LENGTH_LONG).show();
//                }
//            }
//        }, 5000); // 5000 milliseconds = 5 giây (hoặc bạn có thể thay đổi thời gian delay tùy ý)

        if(isConnected(this)){
            Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_LONG).show();
            ActionViewFlipper();
            getLoaiSanPham();
        }else{
            Toast.makeText(getApplicationContext(),"khong co internet",Toast.LENGTH_LONG).show();
        }

    }





    private void getLoaiSanPham() {
//        LoaiSpModel loaiSpModel = new LoaiSpModel();
//        loaiSpModel.isSuccess();


        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io()) // Chọn Scheduler phù hợp ở đây
                .observeOn(AndroidSchedulers.mainThread()) // Để làm việc với luồng UI
                .subscribe(lsp -> {
                    if (lsp.isSuccess()) {
                        Toast.makeText(getApplicationContext(), lsp.getResult().get(0).getTensanpham(), Toast.LENGTH_LONG).show();
                    }
                }));

    }

    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png");
        mangquangcao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png");
        mangquangcao.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg");
        for(int i=0; i<mangquangcao.size();i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);

//        set animation bằng viewFlip
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);

    }

    private void ActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Anhxa(){
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewflipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);

        //khoi tao list
        mangLoaiSp = new ArrayList<>();

        //khoi tao adapter
        loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangLoaiSp);
        listViewManHinhChinh.setAdapter(loaiSpAdapter);
    }

    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);// nhớ thêm quyền vào không bị lỗi
        NetworkInfo mobile = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);
        if((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected()) ){
         return true;
        }else{
            return false;
        }
    }
}