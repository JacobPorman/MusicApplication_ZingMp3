package com.example.musicapplication_zingmp3.Fragment.Search;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.musicapplication_zingmp3.Adapter.NewSongAdapter;
import com.example.musicapplication_zingmp3.Adapter.PersonalMusicAdapter;
import com.example.musicapplication_zingmp3.Fragment.HomeFragment;
import com.example.musicapplication_zingmp3.Fragment.SearchFragment;
import com.example.musicapplication_zingmp3.Model.MediaPlayerSingleton;
import com.example.musicapplication_zingmp3.Model.Song;
import com.example.musicapplication_zingmp3.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

public class SeeAllSongFragment extends Fragment {
    View view;
    RecyclerView recyclerViewSearchSongs;
    ArrayList<Song> songs;
    NewSongAdapter newSongAdapter;

    RelativeLayout playerView;
    ImageView backArrow;
    Button btnPlayAll;
    MediaPlayer mediaPlayer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_see_all_song, container, false);
        recyclerViewSearchSongs = view.findViewById(R.id.recyclerViewSearchSongs);
        backArrow = view.findViewById(R.id.backArrow);
        btnPlayAll = view.findViewById(R.id.btnPlayAll);
        playerView = getActivity().findViewById(R.id.playerView);
        mediaPlayer = MediaPlayerSingleton.getInstance().getMediaPlayer();
        songs = new ArrayList<>();
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<Song> ListSongs = bundle.getParcelableArrayList("songs");
            songs.addAll(ListSongs);
        }
        backArrow.setOnClickListener(view1 -> {
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout,homeFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        });
        btnPlayAll.setOnClickListener(view1 -> {
            playSong(songs.get(0));

            Intent intent = new Intent("sendSong");
            intent.putExtra("song", songs.get(0));
            intent.putExtra("songs", songs);
            getContext().sendBroadcast(intent);
        });
        newSongAdapter = new NewSongAdapter(getContext(),null, songs, playerView);
        recyclerViewSearchSongs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerViewSearchSongs.setAdapter(newSongAdapter);
        return view;
    }

    private void playSong(Song firstSong) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(firstSong.getLink());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}