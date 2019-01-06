package com.tianyisun.eventsearch.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.service.DataService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private String id;
    private String venue;
    private String date;
    private String time;
    private String genre;
    private String segment;
    private String ticketStatus;
    private String buyUrl;
    private String seatMap;
    private String min;
    private String max;
    private List<String> teams;
    private String teamsStr;

    private boolean dataGot;
    private boolean errorCheck;
    private OnInfoFragmentInteractionListener mListener;

    private View view;
    private TextView teamsTv;
    private TextView venueTv;
    private TextView timeTv;
    private TextView categoryTv;
    private TextView priceTv;
    private TextView ticketStatusTv;
    private TextView buyTv;
    private TextView seatMapTv;
    private LinearLayout teamsLo;
    private LinearLayout venueLo;
    private LinearLayout timeLo;
    private LinearLayout categoryLo;
    private LinearLayout priceLo;
    private LinearLayout ticketStatusLo;
    private LinearLayout buyLo;
    private LinearLayout seatMapLo;

    private View progressBarV;
    private View infoLayout;
    private View noRecordV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            DataService.getDetail(getActivity(), id,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                if (json.has("localDate")) {
                                    date = json.getString("localDate");
                                }
                                if (json.has("localTime")) {
                                    time = json.getString("localTime");
                                }
                                if (json.has("genre")) {
                                    genre = json.getString("genre");
                                }
                                if (json.has("segment")) {
                                    segment = json.getString("segment");
                                }
                                if (json.has("venue")) {
                                    venue = json.getString("venue");
                                }
                                if (json.has("buy")) {
                                    buyUrl = json.getString("buy");
                                    mListener.onSetTwitter(buyUrl);
                                }
                                if (json.has("status")) {
                                    ticketStatus = json.getString("status");
                                }
                                if (json.has("seatmap")) {
                                    seatMap = json.getString("seatmap");
                                }
                                if (json.has("min")) {
                                    min = json.get("min").toString();
                                }
                                if (json.has("max")) {
                                    max = json.get("max").toString();
                                }
                                teams = new ArrayList<>();
                                if (json.has("teams")) {
                                    JSONArray teamsJson = json.getJSONArray("teams");
                                    for (int i = 0; i < teamsJson.length(); i++) {
                                        teams.add(teamsJson.getJSONObject(i).getString("name"));
                                    }
                                }
                                dataGot = true;
                                errorCheck = false;
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

    public static InfoFragment newInstance(String id, OnInfoFragmentInteractionListener listener) {
        InfoFragment fragment = new InfoFragment();
        fragment.id = id;
        fragment.dataGot = false;
        fragment.errorCheck = false;
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        teamsTv = view.findViewById(R.id.tv_teams);
        venueTv = view.findViewById(R.id.tv_venue);
        timeTv = view.findViewById(R.id.tv_time);
        categoryTv = view.findViewById(R.id.tv_category);
        priceTv = view.findViewById(R.id.tv_price);
        ticketStatusTv = view.findViewById(R.id.tv_ticket_status);
        buyTv = view.findViewById(R.id.tv_buy);
        seatMapTv = view.findViewById(R.id.tv_seat_map);
        teamsLo = view.findViewById(R.id.lv_teams);
        venueLo = view.findViewById(R.id.lv_venue);
        timeLo = view.findViewById(R.id.lv_time);
        categoryLo = view.findViewById(R.id.lv_category);
        priceLo = view.findViewById(R.id.lv_price);
        ticketStatusLo = view.findViewById(R.id.lv_ticket_status);
        buyLo = view.findViewById(R.id.lv_buy);
        seatMapLo = view.findViewById(R.id.lv_seat_map);
        progressBarV = view.findViewById(R.id.progress_bar);
        infoLayout = view.findViewById(R.id.layout_info);
        noRecordV = view.findViewById(R.id.no_record);

        if (dataGot) {
            createView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void createView() {
        if (!errorCheck) {
            if (teams.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < teams.size(); i++) {
                    if (i == 0) {
                        sb.append(teams.get(i));
                    } else {
                        sb.append(" | " + teams.get(i));
                    }
                }
                teamsStr = sb.toString();
                teamsTv.setText(teamsStr);
            } else {
                teamsLo.setVisibility(View.GONE);
            }

            if (venue != null) {
                venueTv.setText(venue);
            } else {
                venueLo.setVisibility(View.GONE);
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
                    timeTv.setText(dateStr + " " + timeStr);
                } else {
                    timeTv.setText(timeStr);
                }
            } else {
                timeLo.setVisibility(View.GONE);
            }

            if (genre != null || segment != null) {
                segment = segment == null ? "" : segment;
                genre = genre == null ? "" : genre;
                if (segment != "" && genre != "") {
                    categoryTv.setText(segment + " | " + genre);
                } else {
                    categoryTv.setText(segment + genre);
                }
            } else {
                categoryLo.setVisibility(View.GONE);
            }

            if (min != null || max != null) {
                min = min == null ? "" : String.format("%.2f", Double.parseDouble(min));
                max = max == null ? "" : String.format("%.2f", Double.parseDouble(max));
                if (max != "" && max != "") {
                    priceTv.setText("$" + min + " ~ " + "$" + max);
                } else {
                    priceTv.setText("$" + min + max);
                }
            } else {
                priceLo.setVisibility(View.GONE);
            }

            if (ticketStatus != null) {
                ticketStatus = ticketStatus.substring(0, 1).toUpperCase()
                        + ticketStatus.substring(1);
                ticketStatusTv.setText(ticketStatus);
            } else {
                ticketStatusLo.setVisibility(View.GONE);
            }

            if (buyUrl != null) {
                String html = "<a href='" + buyUrl + "'>" + getString(R.string.info_buy_value) + "</a>";
//            String html +
                buyTv.setMovementMethod(LinkMovementMethod.getInstance());
                buyTv.setText(Html.fromHtml(html));
            } else {
                buyLo.setVisibility(View.GONE);
            }

            if (seatMap != null) {
                String html = "<a href='" + seatMap + "'>" + getString(R.string.info_seat_map_value) + "</a>";
//            String html +
                seatMapTv.setMovementMethod(LinkMovementMethod.getInstance());
                seatMapTv.setText(Html.fromHtml(html));
            } else {
                seatMapLo.setVisibility(View.GONE);
            }
            showInfoView();
        } else {
            infoLayout.setVisibility(View.GONE);
            progressBarV.setVisibility(View.GONE);
            noRecordV.setVisibility(View.VISIBLE);
        }
    }

    private void showInfoView() {
        infoLayout.setVisibility(View.VISIBLE);
        progressBarV.setVisibility(View.GONE);
    }

    public interface OnInfoFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSetTwitter(final String url);
    }
}
