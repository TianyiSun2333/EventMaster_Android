package com.tianyisun.eventsearch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.artifact.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingEventsAdapter extends RecyclerView.Adapter<UpcomingEventsAdapter.UpcomingEventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private OnClickUpcomingEventListener mListener;

    public UpcomingEventsAdapter(Context context, List<Event> events, OnClickUpcomingEventListener listener) {
        this.context = context;
        this.eventList = events == null ? new ArrayList<Event>() : events;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public UpcomingEventsAdapter.UpcomingEventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UpcomingEventsAdapter.UpcomingEventViewHolder(LayoutInflater.from(context).inflate(R.layout.upcoming_event_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingEventsAdapter.UpcomingEventViewHolder viewHolder, int i) {
        final Event event = eventList.get(i);
        String name = event.getName();
        List<String> artists = event.getTeams();
        String date = event.getDate();
        String time = event.getTime();
        String type = event.getGenre();
        if (name != null) {
            viewHolder.nameTv.setText(name);
        } else {
            viewHolder.nameTv.setVisibility(View.GONE);
        }

        if (artists != null && artists.size() > 0) {
            viewHolder.artistTv.setText(artists.get(0));
        }

        if (date != null || time != null) {
            String dateStr = null;
            String timeStr;
            if (date != null) {
                String patternFrom = "yyyy-MM-dd";
                String patternTo = "MMM dd, yyyy";
                DateFormat from = new SimpleDateFormat(patternFrom);
                DateFormat to = new SimpleDateFormat(patternTo, Locale.ENGLISH);
                try {
                    dateStr = to.format((Date) from.parse(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            timeStr = time == null ? "" : time;
            if (dateStr != null) {
                viewHolder.timeTv.setText(dateStr + " " + timeStr);
            } else {
                viewHolder.timeTv.setText(timeStr);
            }
        } else {
            viewHolder.timeTv.setVisibility(View.GONE);
        }

        if (type != null) {
            viewHolder.typeTv.setText("Type: " + type);
        } else {
            viewHolder.typeTv.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickEvent(event.getUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class UpcomingEventViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private TextView artistTv;
        private TextView timeTv;
        private TextView typeTv;

        public UpcomingEventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tv_upcoming_item_name);
            artistTv = itemView.findViewById(R.id.tv_upcoming_item_artist);
            timeTv = itemView.findViewById(R.id.tv_upcoming_item_time);
            typeTv = itemView.findViewById(R.id.tv_upcoming_item_type);
        }
    }

    public interface OnClickUpcomingEventListener {
        void onClickEvent(String url);
    }
}
