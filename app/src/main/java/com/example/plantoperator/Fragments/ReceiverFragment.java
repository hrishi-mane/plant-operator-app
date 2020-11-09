package com.example.plantoperator.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.Adapters.ProgrammingAdapter;
import com.example.plantoperator.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class ReceiverFragment extends Fragment implements OnMapReadyCallback {
    List<Double> source_latitudes = new ArrayList<>();
    List<Double> source_longitudes = new ArrayList<>();
    List<Double> destination_latitudes = new ArrayList<>();
    List<Double> destination_longitudes = new ArrayList<>();
    List<String> distance_array = new ArrayList<>();
    List<String> duration_array = new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GeoApiContext mGeoApiContext = new GeoApiContext.Builder()
            .apiKey("AIzaSyB_A_3lfpzS0oSp0p9Q2XTqOtBe9TmbDuo").build();
    SupportMapFragment mapFragment;
    List<Marker> marks = new ArrayList<Marker>();
    List<String> data = new ArrayList<String>();
    Double lat = null;
    Double lng = null;
    GeoPoint g;
    RecyclerView programmingList;
    BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mMap;
    private String mParam1;
    private String mParam2;

    public ReceiverFragment() {
        // Required empty public constructor
    }

    public static ReceiverFragment newInstance(String param1, String param2) {
        ReceiverFragment fragment = new ReceiverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receiver, container, false);
        Log.d("recycler", "arraylistCreation: " + data);
        ProgrammingAdapter.RecyclerViewOnClickListener listener;

        ConstraintLayout mCustomBottomSheet;
        LinearLayout mHeaderLayout;
        final ImageView mHeaderImage;

        mCustomBottomSheet = view.findViewById(R.id.custom_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mCustomBottomSheet);
        mHeaderLayout = view.findViewById(R.id.header_layout);
        mHeaderImage = view.findViewById(R.id.header_arrow);
        programmingList = (RecyclerView) view.findViewById(R.id.programmingList);
        programmingList.setLayoutManager(new LinearLayoutManager(getActivity()));


        mHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });

        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mHeaderImage.setRotation(slideOffset * 180);
            }
        });



        // Inflate the layout for this fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return view;
    }

    public void initializeRecyclerView(RecyclerView programmingList, final BottomSheetBehavior mBottomSheetBehavior) {
        ProgrammingAdapter.RecyclerViewOnClickListener listener;//initialize recycler view
        listener = new ProgrammingAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d("recycler", "onClick: " + position);
                Marker mark = marks.get(position);
                showMessage(mark);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        };
        Log.d("recycler", "adding to adapter: " + data);
        ProgrammingAdapter adapter = new ProgrammingAdapter(data, listener);
        programmingList.setAdapter(adapter);
    }

    //this method is called by main method when message is received from sender fragment
    public void showMessage(Marker marker) {
        for (Marker m : marks) {
            m.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_local_shipping_24));
        }

        marker.showInfoWindow();
        marker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_blue_local_shipping_24));
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16);
        mMap.animateCamera(cu);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        //Dynamic FireStore Data Retieval
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Session").document("session123").collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot value,
                                        @androidx.annotation.Nullable FirebaseFirestoreException e) {
                        if (value.size() != 0) {

                            if (e != null) {
                                Log.d("Firestore Connection", "Listen failed.", e);
                                return;
                            }

                            mMap.clear();
                            marks.clear();
                            data.clear();
/**************************************Fetch Documents in an ArrayList*****************************************************************/

                            //Loop for all th documents in a collection
                            for (QueryDocumentSnapshot doc : value) {
                                Map user_doc = doc.getData();

                                //Get co-ordinates
                                g = (GeoPoint) user_doc.get("geoPoint");
                                if (g != null) {


                                    //Store double latitude and longitude
                                    lat = g.getLatitude();
                                    lng = g.getLongitude();

                                    //store in source array
                                    source_latitudes.add(lat);
                                    source_longitudes.add(lng);

                                    //vehicle number
                                    Map user = (Map) user_doc.get("userDetails");
                                    String name = String.valueOf(user.get("name"));

                                    //Customer Location
                                    Map customer = (Map) user_doc.get("customerDetails");
                                    GeoPoint destination = (GeoPoint) customer.get("location");

                                    //store in destination array
                                    destination_latitudes.add(destination.getLatitude());
                                    destination_longitudes.add(destination.getLongitude());


                                    data.add(name);
                                    //Mark Location
                                    LatLng location = new LatLng(lat, lng);
                                    Marker m = mMap.addMarker(new MarkerOptions().position(location).title("Name:"+name).icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_local_shipping_24)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                                    marks.add(m);

                                    for(Double source_lat:source_latitudes){
                                        Log.d("source_lat",""+source_lat);
                                    }

                                    for(Double source_lng:source_longitudes){
                                        Log.d("source_lng",""+source_lng);
                                    }

                                    for(Double destination_lat:destination_latitudes){
                                        Log.d("destination_lat",""+destination_lat);
                                    }

                                    for(Double destination_lng:destination_longitudes){
                                        Log.d("destination_lng",""+destination_lng);
                                    }

                                }
                            }
                            initializeRecyclerView(programmingList, mBottomSheetBehavior);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            if (marks.size() != 0) {
                                for (Marker marker : marks) {
                                    builder.include(marker.getPosition());
                                }
                                LatLngBounds bounds = builder.build();

                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.40);
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                mMap.animateCamera(cu);

                                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(LatLng latLng) {
                                        for (Marker m : marks) {
                                            m.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_local_shipping_24));
                                        }

                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        for (Marker marker : marks) {
                                            builder.include(marker.getPosition());
                                        }
                                        LatLngBounds bounds = builder.build();

                                        int width = getResources().getDisplayMetrics().widthPixels;
                                        int height = getResources().getDisplayMetrics().heightPixels;
                                        int padding = (int) (width * 0.40);
                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                        mMap.animateCamera(cu);
                                    }
                                });

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        enableStrictMode();
                                        OkHttpClient client = new OkHttpClient();
                                        Request request = new Request.Builder().url("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+source_latitudes.get(0)+","+source_longitudes.get(0)+"&destinations="+destination_latitudes.get(0)+","+destination_longitudes.get(0)+"&key=AIzaSyB_A_3lfpzS0oSp0p9Q2XTqOtBe9TmbDuo").build();
                                        Response response = null;
                                        try {
                                            response = client.newCall(request).execute();
                                            Log.d("before_result:","Before Result");
                                            Log.d("after_result:","After Result");
                                            String responseToString = response.body().string();
                                            Log.d("after_response:","After Response to String");
                                            Log.d("responseToString: ", ""+responseToString);
                                            Log.d("after_response_log:","After Response to String log");
                                            JSONObject Jobject = new JSONObject(responseToString);
                                            JSONArray Jarray = Jobject.getJSONArray("rows");
                                            JSONObject rowsObject = Jarray.getJSONObject(0);
                                            JSONArray elementsArray = rowsObject.getJSONArray("elements");
                                            JSONObject elementsObject = elementsArray.getJSONObject(0);
                                            JSONObject distanceObject = elementsObject.getJSONObject("distance");
                                            Log.d("distance_object",""+distanceObject.toString());
                                            JSONObject durationObject = elementsObject.getJSONObject("duration");
                                            Log.d("duration_object",""+durationObject.toString());
                                            String distance = (String) distanceObject.get("text");
                                            String duration = (String) durationObject.get("text");
                                            Log.d("distance: ",""+distance);
                                            Log.d("duration: ",""+duration);
                                            distance_array.add(distance);
                                            duration_array.add(duration);
                                        } catch (IOException | JSONException ioException) {
                                            ioException.printStackTrace();
                                        }
                                        marker.setTitle("name:"+data.get(0)+" ,distance:"+distance_array.get(0)+" ,duration:"+duration_array.get(0));
                                        showMessage(marker);
                                        return true;
                                    }
                                });
                            }
/****************************************End of For Loop**************************************************************************************/

                        } else {
                            data.clear();
                            initializeRecyclerView(programmingList, mBottomSheetBehavior);;
                            mMap.clear();
                        }
                    }
                });

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void enableStrictMode(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


}




