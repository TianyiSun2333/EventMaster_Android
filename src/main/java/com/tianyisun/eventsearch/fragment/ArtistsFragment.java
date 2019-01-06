package com.tianyisun.eventsearch.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.adapter.ImagesAdapter;
import com.tianyisun.eventsearch.artifact.Artist;
import com.tianyisun.eventsearch.service.DataService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsFragment extends Fragment {

    private List<String> teams;
    private List<Artist> artists;
    private List<List<String>> imgs;
    private int artistsGot;
    private int imgsGot;
    private boolean errorCheck;
    private String segment;
    private static final int NUMBER_OF_ARTISTS = 2;

    private View view;
    private View progressBarV;
    private View noRecordV;
    private NestedScrollView nestedScrollView;

    private TextView titleTv1;
    private TextView nameTv1;
    private TextView popularityTv1;
    private TextView followersTv1;
    private TextView spotifyTv1;
    private View artistInfoLo1;
    private View nameLo1;
    private View popularityLo1;
    private View followersLo1;
    private View spotifyLo1;
    private RecyclerView imagesRecView1;
    private ImagesAdapter imagesAdapter1;

    private TextView titleTv2;
    private TextView nameTv2;
    private TextView popularityTv2;
    private TextView followersTv2;
    private TextView spotifyTv2;
    private View artistInfoLo2;
    private View nameLo2;
    private View popularityLo2;
    private View followersLo2;
    private View spotifyLo2;
    private RecyclerView imagesRecView2;
    private ImagesAdapter imagesAdapter2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null && teams != null && teams.size() > 0) {
            if (segment != null && segment.equals("Music")) {
                artists = new ArrayList<>();
                for (int i = 0; i < Math.min(NUMBER_OF_ARTISTS, teams.size()); i++) {
                    final int index = i;
                    final Artist.ArtistBuilder builder = new Artist.ArtistBuilder();
                    artists.add(builder.build());
                    DataService.getArtist(getActivity(), teams.get(i),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject json = new JSONObject(response);
                                        if (json.has("name")) {
                                            builder.setName(json.getString("name"));
                                        }
                                        if (json.has("followers")) {
                                            builder.setFollowers(json.getInt("followers"));
                                        }
                                        if (json.has("popularity")) {
                                            builder.setPopularity(json.getInt("popularity"));
                                        }
                                        if (json.has("url")) {
                                            builder.setUrl(json.getString("url"));
                                        }
                                        artists.set(index, builder.build());
                                        artistsGot++;
//                                        test("for loop in requesting artists");
                                        if (artistsGot == Math.min(NUMBER_OF_ARTISTS, teams.size())
                                                && imgsGot == Math.min(NUMBER_OF_ARTISTS, teams.size())
                                                && view != null) {
                                            createView();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
//                                    artistsGot++;
                                    artistsGot = NUMBER_OF_ARTISTS;
                                    imgsGot = NUMBER_OF_ARTISTS;
                                    errorCheck = true;
                                    if (view != null) {
                                        createView();
                                    }
//                                    if (artistsGot == Math.min(NUMBER_OF_ARTISTS, teams.size())
//                                            && imgsGot == Math.min(NUMBER_OF_ARTISTS, teams.size())
//                                            && view != null) {
//
//                                    }
                                }
                            });
                }
            }
            imgs = new ArrayList<>();
            for (int i = 0; i < Math.min(NUMBER_OF_ARTISTS, teams.size()); i++) {
                final int index = i;
                imgs.add(new ArrayList<String>());
                DataService.getImages(getActivity(), teams.get(i),
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.has("images")) {
                                        JSONArray jsonArray = json.getJSONArray("images");
                                        List<String> imgsOfOneArtist = new ArrayList<>();
                                        for (int j = 0; j < jsonArray.length(); j++) {
                                            imgsOfOneArtist.add(jsonArray.getString(j));
                                        }
                                        imgs.set(index, imgsOfOneArtist);
                                    }
                                    imgsGot++;
//                                    test("for loop in requesting imgs");
                                    if (segment != null && segment.equals("Music")
                                            && artistsGot == Math.min(NUMBER_OF_ARTISTS, teams.size())
                                            && imgsGot == Math.min(NUMBER_OF_ARTISTS, teams.size())
                                            && view != null ||
                                            imgsGot == Math.min(NUMBER_OF_ARTISTS, teams.size()) && view != null) {
                                            createView();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                imgsGot = NUMBER_OF_ARTISTS;
                                artistsGot = NUMBER_OF_ARTISTS;
                                errorCheck = true;
                                if (view != null) {
                                    createView();
                                }
//                                imgsGot++;
//                                if (segment != null && segment.equals("Music")
//                                        && artistsGot == Math.min(NUMBER_OF_ARTISTS, teams.size())
//                                        && imgsGot == Math.min(NUMBER_OF_ARTISTS, teams.size())
//                                        && view != null ||
//                                        imgsGot == Math.min(NUMBER_OF_ARTISTS, teams.size()) && view != null) {
//                                    createView();
//
//                                }
                            }
                        });
            }
        } else {
            imgsGot = NUMBER_OF_ARTISTS;
            artistsGot = NUMBER_OF_ARTISTS;
            artists = new ArrayList<>();
            imgs = new ArrayList<>();
            if (view != null) {
                createView();
            }
        }
    }

    public static ArtistsFragment newInstance(List<String> teams, String segment) {
        ArtistsFragment fragment = new ArtistsFragment();
        fragment.teams = teams;
        fragment.segment = segment;
        fragment.artistsGot = 0;
        fragment.imgsGot = 0;
        fragment.errorCheck = false;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
//        this.artistsRecView = view.findViewById(R.id.rec_view_artists);
        this.progressBarV = view.findViewById(R.id.progress_bar);
        this.noRecordV = view.findViewById(R.id.no_record_artists);
        this.nestedScrollView = view.findViewById(R.id.scroll_artists);

        titleTv1 = view.findViewById(R.id.tv_artist1_info_title);
        nameTv1 = view.findViewById(R.id.tv_artist1_name);
        popularityTv1 = view.findViewById(R.id.tv_artist1_popularity);
        followersTv1 = view.findViewById(R.id.tv_artist1_followers);
        spotifyTv1 = view.findViewById(R.id.tv_artist1_spotify);
        artistInfoLo1 = view.findViewById(R.id.lv_artist1);
        nameLo1 = view.findViewById(R.id.lv_artist1_name);
        popularityLo1 = view.findViewById(R.id.lv_artist1_popularity);
        followersLo1 = view.findViewById(R.id.lv_artist1_followers);
        spotifyLo1 = view.findViewById(R.id.lv_artist1_spotify);
        imagesRecView1 = view.findViewById(R.id.rec_view_imgs1);

        titleTv2 = view.findViewById(R.id.tv_artist2_info_title);
        nameTv2 = view.findViewById(R.id.tv_artist2_name);
        popularityTv2 = view.findViewById(R.id.tv_artist2_popularity);
        followersTv2 = view.findViewById(R.id.tv_artist2_followers);
        spotifyTv2 = view.findViewById(R.id.tv_artist2_spotify);
        artistInfoLo2 = view.findViewById(R.id.lv_artist2);
        nameLo2 = view.findViewById(R.id.lv_artist2_name);
        popularityLo2 = view.findViewById(R.id.lv_artist2_popularity);
        followersLo2 = view.findViewById(R.id.lv_artist2_followers);
        spotifyLo2 = view.findViewById(R.id.lv_artist2_spotify);
        imagesRecView2 = view.findViewById(R.id.rec_view_imgs2);

//        test("onviewVreated");
        if (segment != null && segment.equals("Music") && imgsGot >= Math.min(NUMBER_OF_ARTISTS, teams.size()) && artistsGot >= Math.min(NUMBER_OF_ARTISTS, teams.size())
                || imgsGot >= Math.min(NUMBER_OF_ARTISTS, teams.size())) {
            createView();
        }
    }

//    private void test(String t) {
//        Log.d("Now is in", t);
//        Log.d("segment in artist", segment);
//        Log.d("imgs in artist", "" + imgsGot);
//        Log.d("number of artist", "" + artistsGot);
//    }

    private void createView() {
        if (getActivity() != null && !errorCheck && teams != null && teams.size() > 0) {

            titleTv1.setText(teams.get(0));
            if (segment != null && segment.equals("Music")) {
                Artist artist1 = artists.get(0);
                if (artist1.getName() != null) {
                    nameTv1.setText(artist1.getName());
                } else {
                    nameLo1.setVisibility(View.GONE);
                }
                if (artist1.getPopularity() != -1) {
                    popularityTv1.setText(Integer.toString(artist1.getPopularity()));
                } else {
                    popularityLo1.setVisibility(View.GONE);
                }
                if (artist1.getFollowers() != -1) {
                    followersTv1.setText(NumberFormat.getNumberInstance(Locale.US).format(artist1.getFollowers()));
                } else {
                    followersLo1.setVisibility(View.GONE);
                }
                if (artist1.getUrl() != null) {
                    String html = "<a href='" + artist1.getUrl() + "'>" + getString(R.string.artist_spotify_value) + "</a>";
                    spotifyTv1.setMovementMethod(LinkMovementMethod.getInstance());
                    spotifyTv1.setText(Html.fromHtml(html));
                } else {
                    spotifyLo1.setVisibility(View.GONE);
                }
            } else {
                artistInfoLo1.setVisibility(View.GONE);
            }

            imagesRecView1.setLayoutManager(new LinearLayoutManager(getActivity()));
            imagesAdapter1 = new ImagesAdapter(getActivity(), imgs.get(0));
            imagesRecView1.setAdapter(imagesAdapter1);

            if (teams.size() > 1) {
                titleTv2.setText(teams.get(1));
                if (segment != null && segment.equals("Music")) {
                    Artist artist2 = artists.get(1);
                    if (artist2.getName() != null) {
                        nameTv2.setText(artist2.getName());
                    } else {
                        nameLo2.setVisibility(View.GONE);
                    }
                    if (artist2.getPopularity() != -1) {
                        popularityTv2.setText(Integer.toString(artist2.getPopularity()));
                    } else {
                        popularityLo2.setVisibility(View.GONE);
                    }
                    if (artist2.getFollowers() != -1) {
                        followersTv2.setText(NumberFormat.getNumberInstance(Locale.US).format(artist2.getFollowers()));
                    } else {
                        followersLo2.setVisibility(View.GONE);
                    }
                    if (artist2.getUrl() != null) {
                        String html = "<a href='" + artist2.getUrl() + "'>" + getString(R.string.artist_spotify_value) + "</a>";
                        spotifyTv2.setMovementMethod(LinkMovementMethod.getInstance());
                        spotifyTv2.setText(Html.fromHtml(html));
                    } else {
                        spotifyLo2.setVisibility(View.GONE);
                    }
                } else {
                    artistInfoLo2.setVisibility(View.GONE);
                }

                imagesRecView2.setLayoutManager(new LinearLayoutManager(getActivity()));
                imagesAdapter2 = new ImagesAdapter(getActivity(), imgs.get(1));
                imagesRecView2.setAdapter(imagesAdapter2);
            } else {
                artistInfoLo2.setVisibility(View.GONE);
                titleTv2.setVisibility(View.GONE);
            }
//            imagesRecView1.
            nestedScrollView.setVisibility(View.VISIBLE);
            nestedScrollView.setNestedScrollingEnabled(false);

//                            setNestedScrollEnabled(false);

//            artistsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            artistsAdapter = new ArtistsAdapter(getActivity(), teams, artists, imgs, segment);
//            artistsRecView.setAdapter(artistsAdapter);


//            artistsRecView.setVisibility(View.VISIBLE);
        } else {
            noRecordV.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
        }
        progressBarV.setVisibility(View.GONE);
    }

}
