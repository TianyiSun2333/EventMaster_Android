package com.tianyisun.eventsearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tianyisun.eventsearch.fragment.EventsFragment;
import com.tianyisun.eventsearch.service.DataService;
import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.artifact.Event;
import com.tianyisun.eventsearch.artifact.User;
import com.tianyisun.eventsearch.util.FavoriteStorage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ResultActivity extends AppCompatActivity implements
        EventsFragment.OnEventsFragmentInteractionListener {

    private List<Event> events;
    private Set<String> ids;
    private Fragment eventsFragment;
    private FavoriteStorage favoriteStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        favoriteStorage = FavoriteStorage.getInstance(this);

        Intent intent = getIntent();
        events = new ArrayList<>();
        DataService.getEvents(this, (User) intent.getSerializableExtra("input"),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ids = favoriteStorage.getIdsSet();
                try {
                    JSONArray eventsJSON = new JSONObject(response).getJSONArray("events");
                    Event.EventBuilder builder = new Event.EventBuilder();

                    for (int i = 0; i < eventsJSON.length(); i++) {
                        JSONObject eventJSON = eventsJSON.getJSONObject(i);
                        builder.clear();
                        List<String> teams = new ArrayList<>();
                        if (eventJSON.has("name")) {
                            builder.setName(eventJSON.getString("name"));
                        }
                        if (eventJSON.has("date")) {
                            builder.setDate(eventJSON.getString("date"));
                        }
                        if (eventJSON.has("time")) {
                            builder.setTime(eventJSON.getString("time"));
                        }
                        if (eventJSON.has("genre")) {
                            builder.setGenre(eventJSON.getString("genre"));
                        }
                        if (eventJSON.has("segment")) {
                            builder.setSegment(eventJSON.getString("segment"));
                        }
                        if (eventJSON.has("venue")) {
                            builder.setVenue(eventJSON.getString("venue"));
                        }
                        if (eventJSON.has("id")) {
                            builder.setId(eventJSON.getString("id"));
                            if (ids.contains(eventJSON.getString("id"))) {
                                builder.faved(true);
                            }
                        }
                        if (eventJSON.has("teams")) {
                            JSONArray teamsJSON = eventJSON.getJSONArray("teams");
                            for (int j = 0; j < teamsJSON.length(); j++) {
                                teams.add(teamsJSON.getJSONObject(j).getString("name"));
                            }
                            builder.setTeams(teams);
                        }
                        events.add(builder.build());
                    }
                    events.sort(new Comparator<Event>() {
                        @Override
                        public int compare(Event o1, Event o2) {
                            String date1 = o1.getDate() == null ? "2000-01-01" : o1.getDate();
                            String date2 = o2.getDate() == null ? "2000-01-01" : o2.getDate();
                            String time1 = o1.getTime() == null ? "00:00:00" : o1.getTime();
                            String time2 = o2.getTime() == null ? "00:00:00" : o2.getTime();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String date1InString = date1 + " " + time1;
                            String date2InString = date2 + " " + time2;
                            Date timeOfO1 = new Date();
                            Date timeOfO2 = new Date();
                            try {
                                timeOfO1 = sdf.parse(date1InString);
                                timeOfO2 = sdf.parse(date2InString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return timeOfO1.compareTo(timeOfO2);
                        }
                    });
                    createView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createView();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FavoriteStorage favoriteStorage = FavoriteStorage.getInstance(this);
        favoriteStorage.commit();
    }

    @Override
    public void onRequestDetail(Event event) {
        Intent intent = new Intent(ResultActivity.this, DetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    @Override
    public void onFav(String id, boolean fav, Event event) {
        FavoriteStorage favoriteStorage = FavoriteStorage.getInstance(this);
        if (fav) {
            favoriteStorage.addFav(id, event);
        } else {
            favoriteStorage.removeFav(id);
        }
    }

    private void createView() {
        eventsFragment = EventsFragment.newInstance(ResultActivity.this, events);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.res_container, eventsFragment)
                .commitAllowingStateLoss();
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }
}
