package com.tianyisun.eventsearch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tianyisun.eventsearch.artifact.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class FavoriteStorage {

    private Context mContext;
    private static final String FILE_NAME = "fav";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<String> ids;
    private Map<String, Event> events;
    private static FavoriteStorage instance;

    private FavoriteStorage() {
    }

    public static FavoriteStorage getInstance(Context context) {
        if (instance == null) {
            instance = new FavoriteStorage();
            instance.mContext = context;
            instance.init();
        }
        return instance;
    }

    private void init() {
        sharedPreferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        ids = new LinkedList<>(Arrays.asList(
                TextUtils.split(sharedPreferences.getString("ids",""), ",")));
        events = getEventsFromPreferences();
    }

    public List<String> getIds()
    {
        return ids;
    }

    public Set<String> getIdsSet() {
        return events.keySet();
    }

    public void addFav(String id, Event event) {
        if (!events.containsKey(id)) {
            ids.add(id);
            events.put(id, event);
        }
    }

    public void removeFav(String id) {
        ids.remove(id);
        events.remove(id);
    }

    public Event getEvent(String id) {
        return events.get(id);
    }

    public void commit() {
        editor.putString("ids", idsToString());
        for (Map.Entry<String, Event> entry: events.entrySet()) {
            editor.putString(entry.getKey(), eventToString(entry.getValue()));
        }
        editor.commit();
    }

    private String idsToString() {
        StringBuilder sb = new StringBuilder();
        ListIterator<String> iter = ids.listIterator();
        while (iter.hasNext()) {
            if (iter.nextIndex() == 0) {
                String id = iter.next();
                sb.append(id);
            } else {
                String id = iter.next();
                sb.append("," + id);
            }
        }
        return sb.toString();
    }

    private Map<String, Event> getEventsFromPreferences() {
        Map<String, Event> events = new HashMap<>();
        ListIterator<String> iter = ids.listIterator();
        while (iter.hasNext()) {
            String id = iter.next();
            String eventStr = sharedPreferences.getString(id,"");
            events.put(id, parseEvent(eventStr));
        }
        return events;
    }

    private Event parseEvent(String jsonStr) {
        Event event = null;
        try {
            JSONObject json = new JSONObject(jsonStr);
            Event.EventBuilder builder = new Event.EventBuilder();
            builder.setId(json.getString("id"))
                    .setVenue(json.getString("venue"))
                    .setSegment(json.getString("segment"))
                    .setGenre(json.getString("genre"))
                    .setTime(json.getString("time"))
                    .setDate(json.getString("date"))
                    .faved(json.getBoolean("fav"))
                    .setName(json.getString("name"));
            List<String> teams = new ArrayList<>();
            JSONArray teamsJson = json.getJSONArray("teams");
            for (int i = 0; i < teamsJson.length(); i++) {
                teams.add(teamsJson.getJSONObject(i).getString("name"));
            }
            builder.setTeams(teams);
            event = builder.build();
            return event;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    private String eventToString(Event event) {
        JSONObject json = new JSONObject();
        String name = event.getName() == null ? "" : event.getName();
        String date = event.getDate() == null ? "" : event.getDate();
        String time = event.getTime() == null ? "" : event.getTime();
        String venue = event.getVenue() == null ? "" : event.getVenue();
        String id = event.getId() == null ? "" : event.getId();
        String genre = event.getGenre() == null ? "" : event.getGenre();
        String segment = event.getSegment() == null ? "" : event.getSegment();
        boolean fav = event.isFaved();
        List<String> teams = event.getTeams();

        JSONArray jsonArray = new JSONArray();
        ListIterator<String> iter = teams.listIterator();
        while (iter.hasNext()) {
            String team = iter.next();
            JSONObject teamJson = new JSONObject();
            try {
                teamJson.put("name", team);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(teamJson);
        }
        try {
            json.put("name", name);
            json.put("date", date);
            json.put("time", time);
            json.put("venue", venue);
            json.put("id", id);
            json.put("genre", genre);
            json.put("segment", segment);
            json.put("fav", fav);
            json.put("teams", jsonArray);
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
