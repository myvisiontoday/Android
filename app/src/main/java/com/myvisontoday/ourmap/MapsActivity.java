package com.myvisontoday.ourmap;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, com.google.android.gms.location.LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap ourMap;
    private Marker marker;
    private LocationRequest locationRequest;
    private Location mLocation;
    private boolean isLocation = false;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userID;
    private LatLng mylatLng;
    private List<User> userList;
    private List<User> closestUserList;
    private List<Marker> markerList;
    private MapFragment mapFragment;
    private float inputDistance;
    private EditText editText;
    private boolean notified = false;

    // vibration for notification
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        editText = (EditText) findViewById(R.id.textFilter);

        markerList = new ArrayList<Marker>();
        closestUserList = new ArrayList<User>(); // List of closest User
        userList = new ArrayList<User>(); //list of all users
        marker = null;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double lat, lng;
                lat = (Double) dataSnapshot.child("latitude").getValue();
                lng = (Double) dataSnapshot.child("longitude").getValue();
                mylatLng = new LatLng(lat,lng);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        inputDistance = 1000;


        getUsersAndAddMarker(); //get user From database and add marker to the map

        this.vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);



        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);

    }
    private void showMyLocation() {
        if (marker != null) {
            marker.remove();
        }
        //Place current location marker
        if (mylatLng!=null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mylatLng);
            markerOptions.title("You");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            marker = ourMap.addMarker(markerOptions);
            isLocation = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates when Activity is not active
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
        googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        ourMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ourMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);

            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void AddNotification(String name)
    {
        this.vibrator.vibrate(300);
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setTicker("Finder")
                .setContentTitle("Friends near you")
                .setContentText(name + " is closed to you.");

        NotificationManager nm = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }

    private void getUsersAndAddMarker() {

        this.databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName=null, age = null, gender=null;
                //Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child:dataSnapshot.getChildren()) {
                    if (!firebaseUser.getUid().equals(child.getKey())) {//if not the user itself

                        //Iterable<DataSnapshot> children2 = child.getChildren();
                        double lat = Double.NaN;
                        double lan = Double.NaN;
                        for (DataSnapshot ch:child.getChildren())
                        {
                            if(ch.getKey().equals("userName"))
                                userName=ch.getValue(String.class);
                            else if(ch.getKey().equals("age"))
                                age=ch.getValue(String.class);
                            else if (ch.getKey().equals("latitude"))
                                lat = ch.getValue(double.class);
                            else if (ch.getKey().equals("longitude"))
                                lan = ch.getValue(double.class);
                            else if(ch.getKey().equals("gender"))
                                gender=ch.getValue(String.class);
                        }
                        if(userName!=null && !Double.isNaN(lat) && !Double.isNaN(lan))
                        {
                            User user = new User();
                            user.setUserName(userName);
                            user.setAge(age);
                            user.setGender(gender);
                            user.setLatLng(new LatLng(lat,lan));

                            userList.add(user);
                        }
                    }
                }

                if (userList.size()>0)
                {
                    for (User user:userList)
                    {
                        float distance = CalculateDistance(mylatLng, user.getLatLng());
                        String s = getIntent().getExtras().get("distance").toString();
                        inputDistance = Float.parseFloat(s);
                        if (distance <= inputDistance) {
                            Marker marker;
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(user.getLatLng());
                            markerOptions.title(user.getUserName() + ", "+ distance);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            marker = ourMap.addMarker(markerOptions);
                            markerList.add(marker);

                            closestUserList.add(user);

                            //move map camera
                            ourMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user.getLatLng(),15));
                        }
                    }
                }
                if (closestUserList.size()>0 && notified==false){
                    for (User user:closestUserList){
                        AddNotification(user.getUserName()); // notify all user
                    }
                    notified = true;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private float CalculateDistance(LatLng ownLatlng,LatLng otherLatlng){
        float distance = 0;
        Location currentUserLoc, otherUserLoc;

        currentUserLoc = new Location("Current User");
        currentUserLoc.setLatitude(ownLatlng.latitude);
        currentUserLoc.setLongitude(ownLatlng.longitude);

        otherUserLoc = new Location("Other User");
        otherUserLoc.setLatitude(otherLatlng.latitude);
        otherUserLoc.setLongitude(otherLatlng.longitude);

        distance = currentUserLoc.distanceTo(otherUserLoc);
        return distance;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        ourMap.moveCamera(update);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNormal:
                ourMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                ourMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                ourMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                ourMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        mylatLng = new LatLng(location.getLatitude(),location.getLongitude());
        if (!isLocation){
            showMyLocation();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void filterMarker(View view) {
        for (Marker marker : markerList)
        {
            String[] s = marker.getTitle().split(",");
            String string = editText.getText().toString();

            // if name from editText match one in the list, marker with that name with remain, other will be removed.
            if (!s[0].equals(string))
            {
                marker.remove();
            }
            /*for (User user:
                 userList) {
                if (user.getAge().toString().equals(string))
                    marker.remove();
            }*/
        }

    }
}
