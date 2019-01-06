package com.tianyisun.eventsearch.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.service.DataService;
import com.tianyisun.eventsearch.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class VenueFragment extends Fragment implements
        OnMapReadyCallback {

    private String venue;

    private View view;
    private TextView nameTv;
    private TextView addressTv;
    private TextView cityTv;
    private TextView numberTv;
    private TextView hoursTv;
    private TextView generalRuleTv;
    private TextView childRuleTv;
    private LinearLayout nameLo;
    private LinearLayout addressLo;
    private LinearLayout cityLo;
    private LinearLayout numberLo;
    private LinearLayout hoursLo;
    private LinearLayout generalRuleLo;
    private LinearLayout childRuleLo;
    private View venueView;
    private View progressBarV;
    private View noRecordV;
    private MapView mMapView;

    private Bundle mapViewBundle;

    private String name;
    private String address;
    private String city;
    private String state;
    private String hours;
    private String number;
    private String generalRule;
    private String childRule;
    private String lat, lng;

    private boolean dataGot;
    private boolean errorCheck;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";


    public VenueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            DataService.getVenue(getActivity(), venue,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                if (json.has("name")) {
                                    name = json.getString("name");
                                }
                                if (json.has("address")) {
                                    address = json.getString("address");
                                }
                                if (json.has("city")) {
                                    city = json.getString("city");
                                }
                                if (json.has("state")) {
                                    state = json.getString("state");
                                }
                                if (json.has("hour")) {
                                    hours = json.getString("hour");
                                }
                                if (json.has("phone")) {
                                    number = json.getString("phone");
                                }
                                if (json.has("generalRule")) {
                                    generalRule = json.getString("generalRule");
                                }
                                if (json.has("childRule")) {
                                    childRule = json.getString("childRule");
                                }
                                if (json.has("lat")) {
                                    lat = json.getString("lat");
                                }
                                if (json.has("lng")) {
                                    lng = json.getString("lng");
                                }
                                dataGot = true;
//                                Log.d("get venue detail from backend", name);
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

        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
    }

    public static VenueFragment newInstance(String venue) {
        VenueFragment fragment = new VenueFragment();
        fragment.venue = venue;
        fragment.dataGot = false;
        fragment.errorCheck = false;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_venue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        nameTv = view.findViewById(R.id.tv_venue_name);
        addressTv = view.findViewById(R.id.tv_venue_address);
        cityTv = view.findViewById(R.id.tv_venue_city);
        numberTv = view.findViewById(R.id.tv_venue_number);
        hoursTv = view.findViewById(R.id.tv_venue_hours);
        generalRuleTv = view.findViewById(R.id.tv_venue_general_rule);
        childRuleTv = view.findViewById(R.id.tv_venue_child_rule);
        nameLo = view.findViewById(R.id.lv_venue_name);
        addressLo = view.findViewById(R.id.lv_venue_address);
        cityLo = view.findViewById(R.id.lv_venue_city);
        numberLo = view.findViewById(R.id.lv_venue_number);
        hoursLo= view.findViewById(R.id.lv_venue_hours);
        generalRuleLo = view.findViewById(R.id.lv_venue_general_rule);
        childRuleLo = view.findViewById(R.id.lv_venue_child_rule);
        venueView = view.findViewById(R.id.layout_venue);
        progressBarV = view.findViewById(R.id.progress_bar);
        noRecordV = view.findViewById(R.id.no_record_venue);
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(mapViewBundle);

        if (dataGot) {
            createView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    private void createView() {
//        Log.d("creating view", address);
        if (!errorCheck) {
            if (name != null) {
                nameTv.setText(name);
            } else {
                nameLo.setVisibility(View.GONE);
            }

            if (address != null) {
                addressTv.setText(address);
            } else {
                addressLo.setVisibility(View.GONE);
            }

            if (city != null || state != null) {
                if (city != null && state != null) {
                    cityTv.setText(city + ", " + Constant.getState(state));
                } else {
                    city = city == null ? "" : city;
                    state = state == null ? "" : Constant.getState(state);
                    cityTv.setText(city + state);
                }
            } else {
                cityLo.setVisibility(View.GONE);
            }

            if (number != null) {
                numberTv.setText(number);
            } else {
                numberLo.setVisibility(View.GONE);
            }

            if (hours != null) {
                hoursTv.setText(hours);
            } else {
                hoursLo.setVisibility(View.GONE);
            }

            if (generalRule != null) {
                generalRuleTv.setText(generalRule);
            } else {
                generalRuleLo.setVisibility(View.GONE);
            }

            if (childRule != null) {
                childRuleTv.setText(childRule);
            } else {
                childRuleLo.setVisibility(View.GONE);
            }
            venueView.setVisibility(View.VISIBLE);
            mMapView.getMapAsync(this);
            progressBarV.setVisibility(View.GONE);
        } else {
            venueView.setVisibility(View.GONE);
            progressBarV.setVisibility(View.GONE);
            noRecordV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        double lat = Double.parseDouble(this.lat);
        double lng = Double.parseDouble(this.lng);
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Marker"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

}
