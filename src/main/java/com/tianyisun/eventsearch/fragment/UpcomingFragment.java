package com.tianyisun.eventsearch.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.adapter.UpcomingEventsAdapter;
import com.tianyisun.eventsearch.artifact.Event;
import com.tianyisun.eventsearch.service.DataService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        UpcomingEventsAdapter.OnClickUpcomingEventListener{

    private static final String DEFAULT_SORT_KEY = "Default";
    private static final String DEFAULT_SORT_ORDER = "Ascending";

    private String venue;
    private List<Event> eventList;

    private Spinner sortKeysSpinner;
    private Spinner sortOrdersSpinner;
    private String[] sortKeysArray;
    private String[] sortOrdersArray;

    private ArrayAdapter<CharSequence> sortKeysAdapter;
    private ArrayAdapter<CharSequence> sortOrdersAdapter;

    private boolean dataGot;
    private boolean errorCheck;
    private String sortKey;
    private String sortOrder;

    private RecyclerView upcomingRecView;
    private View progressBarV;
    private View noRecordV;
    private View view;
    private View upcomingView;

    private UpcomingEventsAdapter upcomingEventsAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sortKeysArray = getResources().getStringArray(R.array.sort_key);
        sortOrdersArray = getResources().getStringArray(R.array.sort_order);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            DataService.getUpComingEvents(getActivity(), venue,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            eventList = new ArrayList<>();
                            Event.EventBuilder builder = new Event.EventBuilder();
                            try {
                                JSONObject json = new JSONObject(response);
                                if (json.has("events")) {
                                    JSONArray eventsJson = json.getJSONArray("events");
                                    for (int i = 0; i < Math.min(eventsJson.length(), 5); i++) {
                                        JSONObject eventJson = eventsJson.getJSONObject(i);
                                        builder.clear()
                                                .setIndex(eventJson.getInt("index"));
                                        if (eventJson.has("displayName")) {
                                            builder.setName(eventJson.getString("displayName"));
                                        }

                                        if (eventJson.has("uri")) {
                                            builder.setUrl(eventJson.getString("uri"));
                                        } else {
                                            builder.setUrl("https://www.bilibili.com/");
                                        }

                                        if (eventJson.has("artist")) {
                                            List<String> teams = new ArrayList<>();
                                            teams.add(eventJson.getString("artist"));
                                            builder.setTeams(teams);
                                        }

                                        if (eventJson.has("date")) {
                                            builder.setDate(eventJson.getString("date"));
                                        }

                                        if (eventJson.has("time") && !eventJson.getString("time").equals("null")) {
                                            builder.setTime(eventJson.getString("time"));
                                        }

                                        if (eventJson.has("type")) {
                                            builder.setGenre(eventJson.getString("type"));
                                        }
                                        eventList.add(builder.build());
                                    }
                                }
                                dataGot = true;
                                if (view != null) {
                                    createView();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dataGot = true;
                            errorCheck = true;
                            if (view != null) {
                                createView();
                            }
                        }
                    });
        }
    }

    public static UpcomingFragment newInstance(String venue) {
        UpcomingFragment fragment = new UpcomingFragment();
        fragment.venue = venue;
        fragment.dataGot = false;
        fragment.sortKey = DEFAULT_SORT_KEY;
        fragment.sortOrder = DEFAULT_SORT_ORDER;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            sortKeysSpinner = view.findViewById(R.id.spinner_sort_key);
            sortOrdersSpinner = view.findViewById(R.id.spinner_sort_order);

            sortKeysAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_key, android.R.layout.simple_spinner_item);
            sortKeysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sortKeysSpinner.setAdapter(sortKeysAdapter);
            sortKeysSpinner.setSelection(Arrays.asList(sortKeysArray).indexOf(sortKey));
            sortKeysSpinner.setOnItemSelectedListener(this);
            sortKeysSpinner.setEnabled(false);

            sortOrdersAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_order, android.R.layout.simple_spinner_item);
            sortOrdersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sortOrdersSpinner.setAdapter(sortOrdersAdapter);
            sortOrdersSpinner.setSelection(Arrays.asList(sortOrdersArray).indexOf(sortOrder));
            sortOrdersSpinner.setOnItemSelectedListener(this);
            sortOrdersSpinner.setEnabled(false);
        }

        this.view = view;
        upcomingRecView = view.findViewById(R.id.rec_view_upcoming);
        noRecordV = view.findViewById(R.id.no_record_upcoming);
        progressBarV = view.findViewById(R.id.progress_bar_upcoming);
        upcomingView = view.findViewById(R.id.scroll_upcoming);

        if (dataGot) {
            createView();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (eventList != null && eventList.size() > 0) {
            String param = parent.getItemAtPosition(position).toString();
            switch (parent.getId()) {
                case R.id.spinner_sort_key:
                    sortKey = param;
                    if (sortKey.equals(DEFAULT_SORT_KEY)) {
                        sortOrdersSpinner.setEnabled(false);
                    } else {
                        sortOrdersSpinner.setEnabled(true);
                    }
                    break;
                case R.id.spinner_sort_order:
                    sortOrder = param;
            }
            if (eventList != null) {
                sort();
            }
            upcomingEventsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void sort() {
        eventList.sort(new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
//                Log.d("sort", sortKey + " " + sortOrder);
                int order = sortOrder.equals("Ascending") ? 1 : -1;
//                Log.d("sort, ", "" + order);
                switch (sortKey) {
                    case "Default":
//                        Log.d("sort", o1.getIndex() + " " + o2.getIndex());
//                        Log.d("sort", " " +Integer.compare(o1.getIndex(), o2.getIndex()));
                        return Integer.compare(o1.getIndex(), o2.getIndex());
                    case "Event Name":
                        String name1 = o1.getName() == null ? "" : o1.getName();
                        String name2 = o2.getName() == null ? "" : o2.getName();
//                        Log.d("sort", name1 + " " + name2);
//                        Log.d("sort", " " +name1.compareTo(name2));
                        return order * name1.compareTo(name2);
                    case "Time":
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
//                        Log.d("sort", timeOfO1 + " " + timeOfO2);
//                        Log.d("sort", " " + timeOfO1.compareTo(timeOfO2));
                        return order * timeOfO1.compareTo(timeOfO2);
                    case "Artist":
                        String artist1 = o1.getTeams() == null ? "" : o1.getTeams().get(0);
                        String artist2 = o2.getTeams() == null ? "" : o2.getTeams().get(0);
//                        Log.d("sort", artist1 + " " + artist2);
//                        Log.d("sort", " " + artist1.compareTo(artist2));
                        return order * artist1.compareTo(artist2);
                    case "Type":
                        String type1 = o1.getGenre() == null ? "" : o1.getGenre();
                        String type2 = o2.getGenre() == null ? "" : o2.getGenre();
                        return order * type1.compareTo(type2);
                }
                return 0;
            }
        });
    }

    private void hideNoRecordPrompt() {
        progressBarV.setVisibility(View.GONE);
        noRecordV.setVisibility(View.GONE);
        upcomingView.setVisibility(View.VISIBLE);
    }

    private void showNoUpcomings() {
        progressBarV.setVisibility(View.GONE);
        noRecordV.setVisibility(View.VISIBLE);
        upcomingView.setVisibility(View.GONE);
    }

    private void createView() {
        if (getActivity() != null && !errorCheck && eventList.size() > 0) {
            sortKeysSpinner.setEnabled(true);
            upcomingRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
            upcomingEventsAdapter = new UpcomingEventsAdapter(getActivity(), eventList, this);
            upcomingRecView.setAdapter(upcomingEventsAdapter);
            hideNoRecordPrompt();
        } else {
            showNoUpcomings();
        }
    }

    @Override
    public void onClickEvent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
