package com.tianyisun.eventsearch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tianyisun.eventsearch.R;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    
    private List<String> images;
    private Context context;

    public ImagesAdapter(Context context,  List<String> imgs) {
        this.context = context;
        this.images = imgs == null ? new ArrayList<String>() : imgs;
    }

    @NonNull
    @Override
    public ImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImagesAdapter.ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagesAdapter.ImageViewHolder viewHolder, final int i) {
        String url = images.get(i);
        Glide.with(context)
                .load(url)
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_artist_image_item);
        }
    }

}
