package com.example.musicapplication_zingmp3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication_zingmp3.Activity.MainActivity;
import com.example.musicapplication_zingmp3.Model.MediaPlayerSingleton;
import com.example.musicapplication_zingmp3.Model.Song;
import com.example.musicapplication_zingmp3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

public class NewSongAdapter extends RecyclerView.Adapter<NewSongAdapter.ViewHolder>{
    Context context;
    ArrayList<Song> rankSongs;
    ArrayList<Song> songs;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    static public MediaPlayer mediaPlayer;
    RelativeLayout playerView;


    public NewSongAdapter(Context context, ArrayList<Song> rankSongs, ArrayList<Song> songs, RelativeLayout playerView) {
        this.context = context;
        this.rankSongs = rankSongs;
        this.songs = songs;
        this.playerView = playerView;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mediaPlayer = MediaPlayerSingleton.getInstance().getMediaPlayer();
    }

    @NonNull
    @Override
    public NewSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.fragment_new_song_item_recycler_view,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewSongAdapter.ViewHolder holder, int position) {
        Song song;
        if(rankSongs != null){
            song = rankSongs.get(position);
        }else {
            song = songs.get(position);
        }

        holder.rankSong.setText(String.valueOf(position + 1));
        holder.songName.setText(song.getTitle().trim());
        holder.time.setText(song.getDuration().trim());

        Glide.with(context)
                .load(song.getImage().trim())
                .into(holder.imageViewAlbumArt);


        firebaseFirestore.collection("Albums").document(song.getIdAlbum().trim()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String albumName = document.getString("title").trim();
                    String singerName = document.getString("singer").trim();
                    holder.album.setText(albumName);
                    holder.artist.setText(singerName);
                }
            }
        });

        firebaseFirestore.collection("Users").document(firebaseUser.getUid().trim())
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.d("Listen failed.", e.getMessage());
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        ArrayList<String> likedSongIds = (ArrayList<String>) documentSnapshot.get("songLiked");
                        if (likedSongIds != null && likedSongIds.contains(song.getId().trim())) {
                            holder.like_item.setImageResource(R.mipmap.heart_on);
                        } else {
                            holder.like_item.setImageResource(R.mipmap.heart);
                        }
                    }
                });

        holder.like_item.setOnClickListener(view -> {
            firebaseFirestore.collection("Users").document(firebaseUser.getUid().trim())
                    .get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            ArrayList<String> likedSongIds = (ArrayList<String>) documentSnapshot.get("songLiked");
                            if (likedSongIds != null && likedSongIds.contains(song.getId().trim())) {
                                // Remove the song from the liked list
                                firebaseFirestore.collection("Users").document(firebaseUser.getUid().trim())
                                        .update("songLiked", FieldValue.arrayRemove(song.getId().trim()))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("TAG", "Song removed from liked list");
                                            holder.like_item.setImageResource(R.mipmap.heart);
                                            firebaseFirestore.collection("Songs").document(song.getId())
                                                    .update("likes", FieldValue.increment(-1))
                                                    .addOnSuccessListener(aVoid1 -> Log.d("TAG", "onSuccess: Likes updated"))
                                                    .addOnFailureListener(e -> Log.w("TAG", "Error updating likes", e));
                                        })
                                        .addOnFailureListener(e -> Log.d("Error removing song from liked list: ", e.getMessage()));
                            } else {
                                // Add the song to the liked list
                                firebaseFirestore.collection("Users").document(firebaseUser.getUid().trim())
                                        .update("songLiked", FieldValue.arrayUnion(song.getId().trim()))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("TAG", "Song added to liked list");
                                            holder.like_item.setImageResource(R.mipmap.heart_on);
                                            firebaseFirestore.collection("Songs").document(song.getId())
                                                    .update("likes", FieldValue.increment(1))
                                                    .addOnSuccessListener(aVoid1 -> Log.d("TAG", "onSuccess: Likes updated"))
                                                    .addOnFailureListener(e -> Log.w("TAG", "Error updating likes", e));
                                        })
                                        .addOnFailureListener(e -> Log.d("Error adding song to liked list: ", e.getMessage()));
                            }
                        }
                    });
        });

        holder.itemView.setOnClickListener(v -> {
            playSong(song);
            // Tạo một Intent với dữ liệu cần truyền đi
            Intent intent = new Intent("sendSong");
            intent.putExtra("song", song);
            intent.putExtra("songs", songs);
            context.sendBroadcast(intent);
        });
    }

    public void playSong(Song song) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(song.getLink());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        if(rankSongs != null){
            return rankSongs.size();
        }else {
            return songs.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankSong, songName, album, artist, time;
        ImageView imageViewAlbumArt, like_item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAlbumArt = itemView.findViewById(R.id.imageViewAlbumArt);
            rankSong = itemView.findViewById(R.id.rankSong);
            songName = itemView.findViewById(R.id.songName);
            album = itemView.findViewById(R.id.album);
            artist = itemView.findViewById(R.id.artist);
            time = itemView.findViewById(R.id.time);
            like_item = itemView.findViewById(R.id.like_newSong_item);
        }
    }
}
