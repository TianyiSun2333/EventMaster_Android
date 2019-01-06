package com.tianyisun.eventsearch.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.activity.MainActivity;
import com.tianyisun.eventsearch.activity.ResultActivity;
import com.tianyisun.eventsearch.adapter.EventsAdapter;
import com.tianyisun.eventsearch.artifact.Event;
import com.tianyisun.eventsearch.util.FavoriteStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment implements EventsAdapter.OnClickEventListener {

    private List<Event> events = new ArrayList<>();

    private RecyclerView eventsRecView;
    private TextView noFavTv;
    private View noResTv;
    private EventsAdapter eventsAdapter;

    private OnEventsFragmentInteractionListener mListener;
    private FavoriteStorage favoriteStorage;

    public EventsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(OnEventsFragmentInteractionListener listener, List<Event> events) {
        EventsFragment fragment = new EventsFragment();
        fragment.mListener = listener;
        fragment.events = events;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsRecView = view.findViewById(R.id.rec_view_events);
        noFavTv = view.findViewById(R.id.tv_fav_no_event);
        noResTv = view.findViewById(R.id.tv_res_no_event);
        if (getActivity() != null) {
            eventsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
            eventsAdapter = new EventsAdapter(getActivity(), events, this);
            eventsRecView.setAdapter(eventsAdapter);
            eventsRecView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            if (events != null && events.size() > 0) {
                hideNoRecordPrompt();
            } else if (getActivity().getClass() == MainActivity.class) {
                showNoEventsInFav();
            } else if (getActivity().getClass() == ResultActivity.class) {
                showNoEventsInRes();
            }
        }
//        view.findViewById(R.id.img_fav);
    }

    @Override
    public void onStart() {
        super.onStart();

//        Toast.makeText(getActivity(), "start", Toast.LENGTH_SHORT).show();
        if (getActivity() != null && getActivity().getClass() == MainActivity.class) {
            events.clear();
            favoriteStorage = FavoriteStorage.getInstance(getActivity());
            List<String> ids = favoriteStorage.getIds();
            ListIterator<String> iter = ids.listIterator();
            while (iter.hasNext()) {
                String id = iter.next();
                Event event = favoriteStorage.getEvent(id);
                events.add(event);
            }
            if (events.size() > 0) {
                noFavTv.setVisibility(View.GONE);
                eventsRecView.setVisibility(View.VISIBLE);
                eventsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
                eventsRecView.setAdapter(new EventsAdapter(getActivity(), events, this));
                eventsRecView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            } else {
                if (getActivity().getClass() == MainActivity.class) {
                    showNoEventsInFav();
                } else if (getActivity().getClass() == ResultActivity.class) {
                    showNoEventsInRes();
                }
            }
        }

        if (getActivity() != null) {
            favoriteStorage = FavoriteStorage.getInstance(getActivity());
            Set<String> ids = favoriteStorage.getIdsSet();
            ListIterator<Event> iter = events.listIterator();
            while (iter.hasNext()) {
                int index = iter.nextIndex();
                Event event = iter.next();
                boolean fav = event.isFaved();
                String id = event.getId();
                if (fav && !ids.contains(id)) {
                    event.fav(false);
//                    Toast.makeText(getActivity(), "set fav in start in result table", Toast.LENGTH_SHORT).show();
                    System.out.println("set fav in start in result table");
                    System.out.println(((TextView)eventsRecView.getChildAt(index).findViewById(R.id.tv_event_name)).getText());
                    ((ImageView) eventsRecView.getChildAt(index).findViewById(R.id.img_fav)).setImageResource(R.drawable.heart_outline_black);
                } else if (!fav && ids.contains(id)) {
                    event.fav(true);
//                    Toast.makeText(getActivity(), "set unfav in start in result table", Toast.LENGTH_SHORT).show();
                    System.out.println("set unfav in start in result table");
                    System.out.println(((TextView)eventsRecView.getChildAt(index).findViewById(R.id.tv_event_name)).getText());
                    ((ImageView) eventsRecView.getChildAt(index).findViewById(R.id.img_fav)).setImageResource(R.drawable.heart_fill_red);
                }
//                if (ids.contains(event.getId())) {
//                    event.fav(true);
//                    ((ImageView) eventsRecView.findViewHolderForAdapterPosition(index).itemView.findViewById(R.id.img_fav)).setImageResource(R.drawable.heart_fill_red);
//                } else {
//                    event.fav(false);
//                    ((ImageView) eventsRecView.findViewHolderForAdapterPosition(index).itemView.findViewById(R.id.img_fav)).setImageResource(R.drawable.heart_outline_black);
//                }
            }
//            eventsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            favoriteStorage = FavoriteStorage.getInstance(getActivity());
            Set<String> ids = favoriteStorage.getIdsSet();
            ListIterator<Event> iter = events.listIterator();
            while (iter.hasNext()) {
                int index = iter.nextIndex();
                Event event = iter.next();
                boolean fav = event.isFaved();
                String id = event.getId();
                if (fav && !ids.contains(id)) {
                    event.fav(false);
                    ((ImageView) eventsRecView.getChildAt(index).findViewById(R.id.img_fav)).setImageResource(R.drawable.heart_fill_red);
                } else if (!fav && ids.contains(id)) {
                    event.fav(true);
                    ((ImageView) eventsRecView.getChildAt(index).findViewById(R.id.img_fav)).setImageResource(R.drawable.heart_outline_black);
                }
//                if (ids.contains(event.getId())) {
//                    event.fav(true);
//                    ((ImageView) eventsRecView.findViewHolderForAdapterPosition(index).itemView.findViewById(R.id.img_fav)).setImageResource(R.drawable.heart_fill_red);
//                } else {
//                    event.fav(false);
//                    ((ImageView) eventsRecView.findViewHolderForAdapterPosition(index).itemView.findViewById(R.id.img_fav)).setImageResource(R.drawable.heart_outline_black);
//                }
            }
//            eventsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText(getActivity(), "commit in events result", Toast.LENGTH_SHORT).show();
        favoriteStorage = FavoriteStorage.getInstance(getActivity());
        favoriteStorage.commit();
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onReqestDetail(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventsFragmentInteractionListener) {
            mListener = (OnEventsFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClickEvent(int index) {
        if (getActivity() != null) {
            Event event = events.get(index);
//            Log.d("onclickevent, ", event.getTeams().get(0));
//            Log.d("onclickevent, ", event.getTeams().size() + "");
            mListener.onRequestDetail(event);
        }
    }

    @Override
    public void onClickFav(int index, RecyclerView.ViewHolder viewHolder) {
        if (getActivity() != null) {
            favoriteStorage = FavoriteStorage.getInstance(getActivity());
            Event event = events.get(index);
            String id = event.getId();
            boolean prevFav = event.isFaved();
            event.fav(!prevFav);
            int favImgId = !prevFav ? R.drawable.heart_fill_red : R.drawable.heart_outline_black;
            ((ImageView) viewHolder.itemView.findViewById(R.id.img_fav)).setImageResource(favImgId);
            String messageSuffix = !prevFav ? getString(R.string.toast_add_fav_suffix)
                    : getString(R.string.toast_remove_fav_suffix);
            Toast.makeText(getActivity(),
                    events.get(index).getName() + messageSuffix,
                    Toast.LENGTH_SHORT)
                    .show();
            mListener.onFav(id, event.isFaved(), event);

            if (getActivity().getClass() == MainActivity.class) {
                String a = "remove event from fav";

//                String idStr = events.get(index).getId();

                for (int i = 0; i < events.size(); i++) {
                    System.out.println(events.get(i).getName());
                }
                System.out.println(" ");


                events.remove(index);
//                eventsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
                eventsRecView.setAdapter(new EventsAdapter(getActivity(), events, this));
                favoriteStorage.removeFav(id);
//                eventsRecView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
//                eventsRecView.removeViewAt(index);
//                eventsRecView.getChildAt(index).setVisibility(View.GONE);

//                for (int i = index; i < events.size(); i++) {
//                    eventsAdapter.notifyItemRemoved(i);
//                    eventsAdapter.notifyItemRangeChanged(i, events.size());
//                }


//                for (int i = 0; i < events.size(); i++) {
//                    if (events.get(i).getId().equals(id)) {
//                        events.remove(events.get(i));
//                        eventsAdapter.remove(events.get(index).getId());
//                        eventsRecView.removeViewAt(i);
////                        eventsAdapter.notifyItemRemoved(i);
////                        eventsAdapter.notifyDataSetChanged();
////                        eventsAdapter.notifyItemRangeChanged(i, events.size());
//                    }
//                }

                for (int i = 0; i < events.size(); i++) {
                    System.out.println(events.get(i).getName());
                }
                if (events.size() < 1) {
                    showNoEventsInFav();
                }
            } else {
                if (!prevFav) {
                    favoriteStorage.addFav(id, event);
                } else {
                    favoriteStorage.removeFav(id);
                }
            }
        }
    }

    private void showNoEventsInFav() {
        noFavTv.setVisibility(View.VISIBLE);
        noResTv.setVisibility(View.GONE);
        eventsRecView.setVisibility(View.GONE);
    }

    private void showNoEventsInRes() {
        noResTv.setVisibility(View.VISIBLE);
        noFavTv.setVisibility(View.GONE);
        eventsRecView.setVisibility(View.GONE);
    }

    private void hideNoRecordPrompt() {
        noResTv.setVisibility(View.GONE);
        noFavTv.setVisibility(View.GONE);
        eventsRecView.setVisibility(View.VISIBLE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnEventsFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRequestDetail(Event event);
        void onFav(String id, boolean fav, Event event);
    }
}
