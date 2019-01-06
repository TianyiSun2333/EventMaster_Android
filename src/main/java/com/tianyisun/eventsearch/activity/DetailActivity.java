package com.tianyisun.eventsearch.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.Toast;

import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.artifact.Event;
import com.tianyisun.eventsearch.fragment.ArtistsFragment;
import com.tianyisun.eventsearch.fragment.InfoFragment;
import com.tianyisun.eventsearch.fragment.UpcomingFragment;
import com.tianyisun.eventsearch.fragment.VenueFragment;
import com.tianyisun.eventsearch.util.FavoriteStorage;

public class DetailActivity extends AppCompatActivity implements InfoFragment.OnInfoFragmentInteractionListener {

    private Event event;
    private FavoriteStorage favoriteStorage;
    private ImageView favImg;
    private ImageView twitterImg;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("event");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(event.getName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.onBackPressed();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_detail);
        tabLayout.getTabAt(0).setIcon(R.drawable.info_tab);
        tabLayout.getTabAt(1).setIcon(R.drawable.artist_tab);
        tabLayout.getTabAt(2).setIcon(R.drawable.venue_tab);
        tabLayout.getTabAt(3).setIcon(R.drawable.upcoming_tab);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        twitterImg = findViewById(R.id.img_twitter);

        favImg = findViewById(R.id.img_fav);
        updateFavImg(event.isFaved());
        favImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFaved = !event.isFaved();
                event.fav(isFaved);
                updateFavImg(isFaved);
                int toastId = isFaved ? R.string.toast_add_fav_suffix : R.string.toast_remove_fav_suffix;
                Toast.makeText(DetailActivity.this, event.getName() + getString(toastId), Toast.LENGTH_SHORT).show();
                favoriteStorage = FavoriteStorage.getInstance(DetailActivity.this);
                if (isFaved) {
                    favoriteStorage.addFav(event.getId(), event);
                } else {
                    favoriteStorage.removeFav(event.getId());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        favoriteStorage = FavoriteStorage.getInstance(this);
        favoriteStorage.commit();
    }

    @Override
    public void onSetTwitter(final String url) {
        twitterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent twitterIntent = new Intent();
                twitterIntent.setAction(Intent.ACTION_VIEW);
                twitterIntent.addCategory(Intent.CATEGORY_BROWSABLE);

                String twitterUrl = "https://twitter.com/intent/tweet?text=Check%20out%20"
                        + event.getName()
                        + "%20located%20at%20"
                        + event.getVenue()
                        + ".%20Website%3A&url="
                        + url
                        + "&hashtags=CSCI571EventSearch";
                twitterIntent.setData(Uri.parse(twitterUrl));
                startActivity(twitterIntent);
            }
        });
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return InfoFragment.newInstance(event.getId(), DetailActivity.this);
                case 1:
                    return ArtistsFragment.newInstance(event.getTeams(), event.getSegment());
                case 2:
                    return VenueFragment.newInstance(event.getVenue());
                case 3:
                    return UpcomingFragment.newInstance(event.getVenue());
            }
            return InfoFragment.newInstance(event.getId(), DetailActivity.this);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private void updateFavImg(boolean isFaved) {
        int imgId = isFaved ? R.drawable.heart_fill_red : R.drawable.heart_fill_white;
        favImg.setImageResource(imgId);
    }

}
