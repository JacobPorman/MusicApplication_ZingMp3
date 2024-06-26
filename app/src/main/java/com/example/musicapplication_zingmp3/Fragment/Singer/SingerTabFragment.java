package com.example.musicapplication_zingmp3.Fragment.Singer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.musicapplication_zingmp3.Adapter.ViewPagerSingerAdapter;
import com.example.musicapplication_zingmp3.Fragment.SearchFragment;
import com.example.musicapplication_zingmp3.Model.Singer;
import com.example.musicapplication_zingmp3.R;
import com.google.android.material.tabs.TabLayout;

public class SingerTabFragment extends Fragment {

    TabLayout tablayout;

    ViewPager viewPager;

    ViewPagerSingerAdapter viewPagerSingerAdapter;

    Singer singer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_singer_tab, container, false);
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            singer = bundle.getParcelable("singer");
        }

        tablayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        viewPagerSingerAdapter= new ViewPagerSingerAdapter(getFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerSingerAdapter.setSinger(singer);
        viewPager.setAdapter(viewPagerSingerAdapter);
        tablayout.setupWithViewPager(viewPager);
        return view;
    }
}