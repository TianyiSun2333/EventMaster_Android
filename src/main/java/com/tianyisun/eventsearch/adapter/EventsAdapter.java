package com.tianyisun.eventsearch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.artifact.Event;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<Event> events;
    private Context context;
    private OnClickEventListener listener;

    public EventsAdapter(Context context, List<Event> events, OnClickEventListener listener) {
        this.context = context;
        this.events = events == null ? new ArrayList<Event>() : events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.event_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsAdapter.EventViewHolder viewHolder, final int i) {
        Event event = events.get(i);
        if (event.getName() != null) {
            viewHolder.nameTv.setText(event.getName());
        } else {
            viewHolder.nameTv.setVisibility(View.GONE);
        }
        if (event.getSegment() != null) {
            switch (event.getSegment()) {
                case "Sports":
                    viewHolder.eventTypeImg.setImageResource(R.drawable.sport_icon);
                    break;
                case "Arts & Theatre":
                    viewHolder.eventTypeImg.setImageResource(R.drawable.art_icon);
                    break;
                case "Miscellaneous":
                    viewHolder.eventTypeImg.setImageResource(R.drawable.miscellaneous_icon);
                    break;
                case "Music":
                    viewHolder.eventTypeImg.setImageResource(R.drawable.music_icon);
                    break;
                case "Film":
                    viewHolder.eventTypeImg.setImageResource(R.drawable.film_icon);
                    break;
            }
        } else {
            viewHolder.eventTypeImg.setImageResource(R.drawable.sport_icon);
        }
        if (event.getVenue() != null) {
            viewHolder.venueTv.setText(event.getVenue());
        } else {
            viewHolder.venueTv.setVisibility(View.GONE);
        }
        if (event.getDate() != null || event.getTime() != null) {
            String date = event.getDate() == null ? "" : event.getDate();
            String time = event.getTime() == null ? "" : event.getTime();
            String value = date == "" ? time : date + " " + time;
            viewHolder.timeTv.setText(value);
        } else {
            viewHolder.timeTv.setVisibility(View.GONE);
        }
        if (event.isFaved()) {
            viewHolder.favImg.setImageResource(R.drawable.heart_fill_red);
        } else {
            viewHolder.favImg.setImageResource(R.drawable.heart_outline_black);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("onclickeventInAdapter, ", events.get(i).getTeams().size() + "");
//                Log.d("onclickeventInAdapter, ", events.get(i).getTeams().get(0) + "");
                listener.onClickEvent(i);

            }
        });
        viewHolder.favImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Event event = events.get(i);
//                boolean isFaved = event.isFaved();
//                event.fav(!isFaved);
                listener.onClickFav(i, viewHolder);
//                int favImgId = !isFaved ? R.drawable.heart_fill_red : R.drawable.heart_outline_black;
//                ((ImageView) v).setImageResource(favImgId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventTypeImg;
        private TextView nameTv;
        private TextView venueTv;
        private TextView timeTv;
        private ImageView favImg;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTypeImg = itemView.findViewById(R.id.img_event_type);
            nameTv = itemView.findViewById(R.id.tv_event_name);
            venueTv = itemView.findViewById(R.id.tv_event_venue);
            timeTv = itemView.findViewById(R.id.tv_event_time);
            favImg = itemView.findViewById(R.id.img_fav);
        }
    }

    public interface OnClickEventListener {
        void onClickEvent(int index);

        void onClickFav(int index, RecyclerView.ViewHolder viewHolder);
    }
}
