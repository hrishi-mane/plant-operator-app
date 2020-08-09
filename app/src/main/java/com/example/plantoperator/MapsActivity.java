package com.example.plantoperator;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //Declaration of global variables to contain latitude and longitude
    Double lat = null;
    Double lng = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        //Dynamic Firestore Data Retieval
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Truck Locations")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
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
                            mMap.addMarker(new MarkerOptions().position(location).title(var_vehicle_number));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

                        }

/****************************************End of For Loop**************************************************************************************/

                    }
                });




    }
}