package com.example.plantoperator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiverFragment extends Fragment implements OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FrameLayout map;
    ImageButton full;
    SupportMapFragment mapFragment;
    List<Marker> marks = new ArrayList<Marker>();
    List<String> data = new ArrayList<String>();
    //Declaration of global variables to contain latitude and longitude
    Double lat = null;
    Double lng = null;
    private GoogleMap mMap;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReceiverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiverFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        RecyclerView programmingList;

        final BottomSheetBehavior mBottomSheetBehavior;
        ConstraintLayout mCustomBottomSheet;
        LinearLayout mHeaderLayout;
        final ImageView mHeaderImage;

        mCustomBottomSheet = view.findViewById(R.id.custom_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mCustomBottomSheet);
        mHeaderLayout = view.findViewById(R.id.header_layout);
        mHeaderImage = view.findViewById(R.id.header_arrow);;
        programmingList =(RecyclerView) view.findViewById(R.id.programmingList);
        programmingList.setLayoutManager(new LinearLayoutManager(getActivity()));


        mHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }else{
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


        //sender fragment code
        //Dynamic Firestore Data Retieval
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Truck Locations")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot value,
                                        @androidx.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore Connection", "Listen failed.", e);
                            return;
                        }
                        data.clear();

/**************************************Fetch Documents in an ArrayList*****************************************************************/

                        //Loop for all th documents in a collection
                        for (QueryDocumentSnapshot doc : value) {
                            Map user = doc.getData();

                            //vehicle number
                            String var_vehicle_number = String.valueOf(user.get("vehicle_number"));
                            data.add(var_vehicle_number);
                        }
                        Log.d("recycler", "dataAdded: " + data);

/****************************************End of For Loop**************************************************************************************/

                    }
                });

        //initialize recycler view
        listener = new ProgrammingAdapter.RecyclerViewOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                onSendMessage(position);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        };
        Log.d("recycler", "adding to adapter: " + data);
        ProgrammingAdapter adapter = new ProgrammingAdapter(data,listener);
        programmingList.setAdapter(adapter);
        // Inflate the layout for this fragment


        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment == null){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map,mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return view;
    }

    //this method is called by main method when message is received from sender fragment
    public void showMessage(int position) {
        for(Marker m: marks){
            m.setIcon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_local_shipping_24));
        }
        Marker mark = marks.get(position);
        mark.showInfoWindow();
        mark.setIcon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_blue_local_shipping_24));
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(mark.getPosition(),16) ;
        mMap.animateCamera(cu);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        //Dynamic Firestore Data Retieval
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Truck Locations")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot value,
                                        @androidx.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore Connection", "Listen failed.", e);
                            return;
                        }

                        mMap.clear();

/**************************************Fetch Documents in an ArrayList*****************************************************************/

                        //Loop for all th documents in a collection
                        for (QueryDocumentSnapshot doc : value) {
                            Map user = doc.getData();

                            //Get co-ordinates
                            GeoPoint g = (GeoPoint) user.get("geoPoint");

                            //Store double latitude and longitude
                            lat = g.getLatitude();
                            lng = g.getLongitude();


                            //vehicle number
                            String var_vehicle_number = String.valueOf(user.get("vehicle_number"));

                            //Mark Location
                            LatLng location = new LatLng(lat,lng);
                            Marker m = mMap.addMarker(new MarkerOptions().position(location).title(var_vehicle_number).icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_local_shipping_24)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                            marks.add(m);

                        }

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for(Marker marker:marks){
                            builder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = builder.build();

                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.20);
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height,padding);
                        mMap.animateCamera(cu);

                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                for(Marker m: marks){
                                    m.setIcon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_local_shipping_24));
                                }

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for(Marker marker:marks){
                                    builder.include(marker.getPosition());
                                }
                                LatLngBounds bounds = builder.build();

                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.20);
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height,padding);
                                mMap.animateCamera(cu);
                            }
                        });

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                for(Marker m: marks){
                                    m.setIcon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_local_shipping_24));
                                }
                                marker.setIcon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_baseline_blue_local_shipping_24));
                                marker.showInfoWindow();
                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16) ;
                                mMap.animateCamera(cu);

                                return true;
                            }
                        });

/****************************************End of For Loop**************************************************************************************/

                    }
                });

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void onSendMessage(int position){
        //find receiver fragment using supportFragmentManager and fragment id
      //  ReceiverFragment receiverFragment = (ReceiverFragment) getFragmentManager().findFragmentById(R.id.fragment_receiver);

        //make sure that receiver fragment exists
      //  if(receiverFragment != null){
            //send this message to receiver fragment by calling its public method
      //      receiverFragment.showMessage(position);
        }
    }

