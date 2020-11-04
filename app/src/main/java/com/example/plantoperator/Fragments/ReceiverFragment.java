package com.example.plantoperator.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class ReceiverFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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
        ;
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


                                    //vehicle number
                                    Map user = (Map) user_doc.get("userDetails");
                                    String name = String.valueOf(user.get("name"));

                                    data.add(name);
                                    //Mark Location
                                    LatLng location = new LatLng(lat, lng);
                                    Marker m = mMap.addMarker(new MarkerOptions().position(location).title(name).icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_local_shipping_24)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                                    marks.add(m);
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


}




