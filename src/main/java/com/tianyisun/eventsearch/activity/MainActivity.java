package com.tianyisun.eventsearch.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.tianyisun.eventsearch.artifact.Event;
import com.tianyisun.eventsearch.fragment.EventsFragment;
import com.tianyisun.eventsearch.fragment.FormFragment;
import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.artifact.User;
import com.tianyisun.eventsearch.util.FavoriteStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity
        implements FormFragment.OnFormFragmentInteractionListener,
        LocationListener,
        EventsFragment.OnEventsFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static final int REQUEST_FINE_LOCATION_CODE = 1;
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;
    private Location location;
    private LocationManager locationManager;
    private double lat;
    private double lng;

    private FavoriteStorage favoriteStorage;
    private List<Event> favEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favoriteStorage = FavoriteStorage.getInstance(this);
        favEventList = new ArrayList<>();
        List<String> ids = favoriteStorage.getIds();
        ListIterator<String> iter = ids.listIterator();
        while (iter.hasNext()) {
            String id = iter.next();
            favEventList.add(favoriteStorage.getEvent(id));
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    public void onStart() {
        super.onStart();
        initLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @Override
    public Location getLocation() {
        initLocation();
        return location;
    }

    @Override
    public void submit(User input) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("input", input);
        startActivity(intent);
    }

    @Override
    public void onRequestDetail(Event event) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    @Override
    public void onFav(String id, boolean fav, Event event) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FormFragment.newInstance();
                case 1:
                    return EventsFragment.newInstance(MainActivity.this, favEventList);
            }
            return FormFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    private void initLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            retrieveLocation();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void retrieveLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
    }

    private double getLat() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    private double getLng() {
        if (location != null) {
            lng = location.getLongitude();
        }
        return lng;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    retrieveLocation();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
