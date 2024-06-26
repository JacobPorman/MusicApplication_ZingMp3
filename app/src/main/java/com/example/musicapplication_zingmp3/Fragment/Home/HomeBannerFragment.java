package com.example.musicapplication_zingmp3.Fragment.Home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.musicapplication_zingmp3.Fragment.BannerAlbumsFragment;
import com.example.musicapplication_zingmp3.Fragment.BannerSongsFragment;
import com.example.musicapplication_zingmp3.Fragment.Singer.SingerTabFragment;
import com.example.musicapplication_zingmp3.Model.Album;
import com.example.musicapplication_zingmp3.Model.Banner;
import com.example.musicapplication_zingmp3.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeBannerFragment extends Fragment {
    ImageSlider imageSlider;
    FirebaseFirestore firebaseFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_banner, container, false);
        imageSlider = view.findViewById(R.id.imageSlider);
        firebaseFirestore = FirebaseFirestore.getInstance();
        ArrayList<SlideModel> imageList = new ArrayList<>();
        ArrayList<Banner> banners = new ArrayList<>();
        firebaseFirestore.collection("Banner")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Lấy giá trị của field image trong tài liệu hiện tại và add vào imageList
                            String id = document.getString("id");
                            String image = document.getString("image");
                            String checkBanner = document.getString("checkBanner");
                            imageList.add(new SlideModel(image, ScaleTypes.FIT));
                            Banner banner = new Banner(id,image, checkBanner);
                            banners.add(banner);
                        }
                        // Hiển thị danh sách hình ảnh trong imageSlider
                        imageSlider.setImageList(imageList, ScaleTypes.FIT);

                        imageSlider.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int i) {
                                Log.d("TAG", "pos :" + banners.get(i).getId());
                                Log.d("Tag", banners.get(i).getCheckBanner());
                                Bundle bundle = new Bundle();
                                bundle.putString("idBanner", banners.get(i).getId());
                                if(banners.get(i).getCheckBanner().equals("Album")){
                                    BannerAlbumsFragment bannerAlbumsFragment = new BannerAlbumsFragment();
                                    bannerAlbumsFragment.setArguments(bundle);

                                    FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext())
                                            .getSupportFragmentManager()
                                            .beginTransaction();
                                    fragmentTransaction.replace(R.id.fragmentLayout, bannerAlbumsFragment);
                                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();

                                }else {
                                    BannerSongsFragment bannerSongsFragment = new BannerSongsFragment();
                                    bannerSongsFragment.setArguments(bundle);

                                    FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext())
                                            .getSupportFragmentManager()
                                            .beginTransaction();
                                    fragmentTransaction.replace(R.id.fragmentLayout, bannerSongsFragment);
                                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }

                            }

                            @Override
                            public void doubleClick(int i) {
                                //do nothing
                            }
                        });

                    } else {
                        Log.d("TAG", "Error getting documents: ");
                    }
                });
        return view;
    }
}