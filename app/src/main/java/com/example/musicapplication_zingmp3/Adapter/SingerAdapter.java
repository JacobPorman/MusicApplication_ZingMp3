package com.example.musicapplication_zingmp3.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication_zingmp3.Fragment.Singer.SingerTabFragment;
import com.example.musicapplication_zingmp3.Model.Singer;
import com.example.musicapplication_zingmp3.R;


import java.util.ArrayList;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder> {
    ArrayList<Singer> singers;
    Context context;

    public SingerAdapter(ArrayList<Singer> singers, Context context) {
        this.singers = singers;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.fragment_singer_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Singer singer = singers.get(position);
        holder.txtSingerName.setText(singer.getName().trim());
        Glide.with(context)
                .load(singer.getImage().trim())
                .circleCrop()
                .into(holder.imgSinger);
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("singer", singer);
            SingerTabFragment singerTabFragment = new SingerTabFragment();
            singerTabFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = ((AppCompatActivity)context)
                    .getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout, singerTabFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return singers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgSinger;
        TextView txtSingerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSinger = itemView.findViewById(R.id.imageViewSinger);
            txtSingerName = itemView.findViewById(R.id.txtSingerName);
        }
    }
}
