package com.tianyisun.eventsearch.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tianyisun.eventsearch.adapter.AutoCompleteAdapter;
import com.tianyisun.eventsearch.service.DataService;
import com.tianyisun.eventsearch.R;
import com.tianyisun.eventsearch.activity.MainActivity;
import com.tianyisun.eventsearch.artifact.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FormFragment.OnFormFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String DEFAULT_UNIT = "Miles";
    private static final String DEFAULT_CATEGORY = "All";
    private static final String ERROR_MESSAGE = "Please fix all fields with errors";

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoCompleteAdapter autoCompleteAdapter;

//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private static final String[] test = new String[] {"one", "two", "three", "four"};

    private Location location;
    private String distance;
    private static User input;

    private AppCompatAutoCompleteTextView keywordAuto;
//    private EditText keywordEt;
    private EditText addressEt;
    private EditText distanceEt;
    private Spinner categories;
    private Spinner units;
    private TextView keywordAlert;
    private TextView addressAlert;
    private RadioGroup fromRadioGroup;
    private Button searchBtn;
    private Button clearBtn;

    private ArrayAdapter<CharSequence> categoriesAdapter;
    private ArrayAdapter<CharSequence> unitsAdapter;

    private OnFormFragmentInteractionListener mListener;

    public FormFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FormFragment newInstance() {
        FormFragment fragment = new FormFragment();
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
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchBtn = view.findViewById(R.id.btn_search);
        clearBtn = view.findViewById(R.id.btn_clear);
//        keywordEt = view.findViewById(R.id.et_keyword);
//        keywordAuto = view.findViewById(R.id.et_keyword_auto);
        addressEt = view.findViewById(R.id.et_address);
        distanceEt = view.findViewById(R.id.et_distance);
        keywordAlert = view.findViewById(R.id.tv_alert_keyword);
        addressAlert = view.findViewById(R.id.tv_alert_address);
        fromRadioGroup = view.findViewById(R.id.radio_group_from);
        categories = view.findViewById(R.id.spinner_categories);
        units = view.findViewById(R.id.spinner_units);

        if (input != null) {
            distanceEt.setText(input.getDistance());
        }


        if (getActivity() != null) {
            categoriesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.categories, android.R.layout.simple_spinner_item);
            categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categories.setAdapter(categoriesAdapter);
//            categories.setOnItemSelectedListener(this);

            unitsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.units, android.R.layout.simple_spinner_item);
            unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            units.setAdapter(unitsAdapter);
//            units.setOnItemSelectedListener(this);

//            ArrayAdapter<String> keywordAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, test);
//            keywordAuto.setAdapter(keywordAdapter);
        }




        addressEt.setEnabled(false);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    User userInput = onSubmit();
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).submit(userInput);
                    }
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        fromRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_other) {
                    addressEt.setEnabled(true);
                    addressEt.requestFocus();
//                    addressEt.setFocusable(true);
                } else {
                    addressEt.setEnabled(false);
                }
            }
        });


        keywordAuto = view.findViewById(R.id.et_keyword_auto);

        //Setting up the adapter for AutoSuggest
        autoCompleteAdapter = new AutoCompleteAdapter(getActivity(),
                android.R.layout.simple_dropdown_item_1line);
        keywordAuto.setAdapter(autoCompleteAdapter);
//        keywordAuto.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view,
//                                            int position, long id) {
//                        selectedText.setText(autoSuggestAdapter.getObject(position));
//                    }
//                });

        keywordAuto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(keywordAuto.getText())) {
                        getRecommendation(keywordAuto.getText().toString());
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        distance = distanceEt.getText().toString();
    }

    private void getRecommendation(String keyword) {
        DataService.getRecommendation(getActivity(), keyword,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("names");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoCompleteAdapter.setData(stringList);
                autoCompleteAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private User onSubmit() {
        User.UserBuilder builder = new User.UserBuilder();
        builder.setKeyword(keywordAuto.getText().toString())
                .setCategory(categories.getSelectedItem().toString())
                .setUnit(units.getSelectedItem().toString())
                .setHere(fromRadioGroup.getCheckedRadioButtonId() == R.id.radio_here)
                .setAddress(addressEt.getText().toString())
                .setLat(location.getLatitude())
                .setLng(location.getLongitude());
        if (distanceEt.getText().toString().length() < 1) {
            builder.setDistance(10);
        } else {
            builder.setDistance(Integer.parseInt(distanceEt.getText().toString()));
        }
        User userInput = builder.build();
        System.out.println(userInput.getAddress());
        System.out.println(userInput.getCategory());
        System.out.println(userInput.getDistance());
        System.out.println(userInput.getKeyword());
        System.out.println(userInput.getLat());
        System.out.println(userInput.getLng());
        System.out.println(userInput.getUnit());
        return userInput;
    }

    private boolean validate() {
        String keyword = keywordAuto.getText().toString();
        boolean check = true;
        if (!checkInput(keyword)) {
            keywordAlert.setVisibility(View.VISIBLE);
            check = false;
        }
        if (fromRadioGroup.getCheckedRadioButtonId() == R.id.radio_other
                && !checkInput(addressEt.getText().toString())) {
            addressAlert.setVisibility(View.VISIBLE);
            check = false;
        } else {
            location = ((MainActivity) getActivity()).getLocation();
            if (location == null) {
                check = false;
                Toast.makeText(getActivity(), "Cannot get current location.", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        if (!check) {
            Toast.makeText(getActivity(), ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    private boolean checkInput(String str) {
        return str.length() != 0 && str.trim().length() != 0;
    }

    private void clear() {
        keywordAuto.setText("");
        keywordAuto.requestFocus();
        distanceEt.setText("");
        addressEt.setText("");
        keywordAlert.setVisibility(View.GONE);
        addressAlert.setVisibility(View.GONE);
        units.setSelection(unitsAdapter.getPosition(DEFAULT_UNIT));
        categories.setSelection(categoriesAdapter.getPosition(DEFAULT_CATEGORY));
        fromRadioGroup.check(R.id.radio_here);
        addressEt.setEnabled(false);
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFormFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFormFragmentInteractionListener) {
            mListener = (OnFormFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFormFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        switch (parent.getId()) {
//            case R.id.spinner_categories:
////                Log.d("spinner selected", "categories");
//                break;
//            case R.id.spinner_units:
////                Log.d("spinner selected", "units");
//                break;
//        }
//
////        String text = parent.getItemAtPosition(position).toString();
////        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }

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
    public interface OnFormFragmentInteractionListener {
        // TODO: Update argument type and name
        Location getLocation();
        void submit(User userInput);
    }
}
