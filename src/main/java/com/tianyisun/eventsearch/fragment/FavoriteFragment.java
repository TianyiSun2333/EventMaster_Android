package com.tianyisun.eventsearch.fragment;

import android.content.Context;
import android.net.Uri;
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
import android.widget.TextView;

import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.adapter.EventsAdapter;
import com.tianyisun.eventsearch.artifact.Event;
import com.tianyisun.eventsearch.util.FavoriteStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment implements EventsAdapter.OnClickEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private FavoriteStorage favoriteStorage;
    private List<Event> eventList;
    private OnFragmentInteractionListener mListener;

    private RecyclerView eventsRecView;
    private TextView noFavTv;

    public FavoriteFragment() {
        this.favoriteStorage = FavoriteStorage.getInstance(getActivity());
        this.eventList = new ArrayList<>();
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noFavTv = view.findViewById(R.id.tv_fav_no_fav);
        eventsRecView = view.findViewById(R.id.rec_view_fav);
        if (getActivity() != null) {
//            favoriteStorage = FavoriteStorage.getInstance(getActivity());
//            List<String> ids = favoriteStorage.getIds();
//            ListIterator<String> iter = ids.listIterator();
//            while (iter.hasNext()) {
//                String id = iter.next();
//                Event event = favoriteStorage.getEvent(id);
//                eventList.add(event);
//            }
//            if (eventList.size() > 0) {
//                view.findViewById(R.id.tv_fav_no_fav).setVisibility(View.GONE);
//                eventsRecView = view.findViewById(R.id.rec_view_fav);
//                eventsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                eventsRecView.setAdapter(new EventsAdapter(getActivity(), eventList, this));
//                eventsRecView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
//            }
        }
//        Toast.makeText(getActivity(), "ViewCreated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText(getActivity(), "start", Toast.LENGTH_SHORT).show();
        if (getActivity() != null) {
            favoriteStorage = FavoriteStorage.getInstance(getActivity());
            List<String> ids = favoriteStorage.getIds();
            ListIterator<String> iter = ids.listIterator();
            while (iter.hasNext()) {
                String id = iter.next();
                Event event = favoriteStorage.getEvent(id);
                eventList.add(event);
            }
            if (eventList.size() > 0) {
                noFavTv.setVisibility(View.GONE);
                eventsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
                eventsRecView.setAdapter(new EventsAdapter(getActivity(), eventList, this));
                eventsRecView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
//        else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClickEvent(int index) {

    }

    @Override
    public void onClickFav(int index, RecyclerView.ViewHolder viewHolder) {

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
