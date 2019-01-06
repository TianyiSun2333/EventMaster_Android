package com.tianyisun.eventsearch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.artifact.Artist;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder> {
    private Context context;
    private List<String> titles;
    private List<Artist> artists;
    private List<List<String>> allImages;
    private String segment;

    public ArtistsAdapter(Context context, List<String> titles, List<Artist> artists, List<List<String>> allImages, String segment) {
        this.context = context;
        this.titles = titles == null ? new ArrayList<String>() : titles;
        this.artists = artists == null ? new ArrayList<Artist>() : artists;
        this.segment = segment == null ? "" : segment;
        this.allImages = allImages == null ? new ArrayList<List<String>>() : allImages;
    }

    @NonNull
    @Override
    public ArtistsAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ArtistsAdapter.ArtistViewHolder(LayoutInflater.from(context).inflate(R.layout.artist_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsAdapter.ArtistViewHolder viewHolder, int i) {
        viewHolder.titleTv.setText(titles.get(i));
        if (segment.equals("Music")) {
            Artist artist = artists.get(i);
            if (artist.getName() != null) {
                viewHolder.nameTv.setText(artist.getName());
            } else {
                viewHolder.nameLo.setVisibility(View.GONE);
            }
            if (artist.getPopularity() != -1) {
                viewHolder.popularityTv.setText(Integer.toString(artist.getPopularity()));
            } else {
                viewHolder.popularityLo.setVisibility(View.GONE);
            }
            if (artist.getFollowers() != -1) {
                viewHolder.followersTv.setText(NumberFormat.getNumberInstance(Locale.US).format(artist.getFollowers()));
            } else {
                viewHolder.followersLo.setVisibility(View.GONE);
            }
            if (artist.getUrl() != null) {
                String html = "<a href='" + artist.getUrl() + "'>" + context.getString(R.string.artist_spotify_value) + "</a>";
                viewHolder.spotifyTv.setMovementMethod(LinkMovementMethod.getInstance());
                viewHolder.spotifyTv.setText(Html.fromHtml(html));
            } else {
                viewHolder.spotifyLo.setVisibility(View.GONE);
            }
        } else {
            viewHolder.artistInfoLo.setVisibility(View.GONE);
        }

        List<String> imgs = allImages.get(i);
        viewHolder.imagesRecView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.imagesAdapter = new ImagesAdapter(context, imgs);
        viewHolder.imagesRecView.setAdapter(viewHolder.imagesAdapter);

    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTv;
        private TextView nameTv;
        private TextView popularityTv;
        private TextView followersTv;
        private TextView spotifyTv;
        private View artistInfoLo;
        private View nameLo;
        private View popularityLo;
        private View followersLo;
        private View spotifyLo;
        private RecyclerView imagesRecView;
        private ImagesAdapter imagesAdapter;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_artist_info_title);
            nameTv = itemView.findViewById(R.id.tv_artist_name);
            popularityTv = itemView.findViewById(R.id.tv_artist_popularity);
            followersTv = itemView.findViewById(R.id.tv_artist_followers);
            spotifyTv = itemView.findViewById(R.id.tv_artist_spotify);
            artistInfoLo = itemView.findViewById(R.id.lv_artist);
            nameLo = itemView.findViewById(R.id.lv_artist_name);
            popularityLo = itemView.findViewById(R.id.lv_artist_popularity);
            followersLo = itemView.findViewById(R.id.lv_artist_followers);
            spotifyLo = itemView.findViewById(R.id.lv_artist_spotify);
            imagesRecView = itemView.findViewById(R.id.rec_view_imgs);
        }
    }
}
