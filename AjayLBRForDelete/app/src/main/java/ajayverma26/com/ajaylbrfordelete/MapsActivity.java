package ajayverma26.com.ajaylbrfordelete;


import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    FloatingActionButton fabTick;
    private TextView mInfoWindow;
    EditText dialogTitle;
    RadioButton radioBtnOnce, radioBtnAlways, radioButtonType;
    String reminderTitle = "", reminderType = "", reminderTitleEdit = "", reminderTypeEdit = "", Flow = "AddReminder", ToShowOnInfoWindow = "", result,reminderTitleDelete = "", reminderTypeDelete = "";
    int idEdit,idDelete;
    Intent intent, intentEdit, intentModify, intentDelete, intentDeleteModify;
    MarkerOptions markerOptions;
    Geocoder geocoder;
    RadioGroup radioGroupType;
    double reminderLat, reminderLng, reminderLatEdit, reminderLngEdit, reminderLatDelete, reminderLngDelete;

    DiscreteSeekBar mSeekBar;
    double circleRadius = 100, reminderRadiusEdit ,reminderRadiusDelete;
    int alpha;
    private Circle circleDraw;

    MapsActivity mapsActivity;

    List<String> listOfGeofences;
           // Collections.singletonList("1");



    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";

    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(NOTIFICATION_MSG, msg);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fabTick = (FloatingActionButton) findViewById(R.id.fabOkClick);
        mSeekBar = (DiscreteSeekBar) findViewById(R.id.seekBar);

        geocoder = new Geocoder(this, Locale.getDefault());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent = getIntent();
        intentEdit = getIntent();
        intentModify = getIntent();
        intentDelete = getIntent();
        intentDeleteModify = getIntent();


        Flow = intentEdit.getStringExtra("Flow");
        Reminder editReminder = (Reminder) intentEdit.getSerializableExtra("EditReminder");

        if (Flow != null && Flow.equalsIgnoreCase("EditReminder")) {
            idEdit = editReminder.getId();
            reminderTitleEdit = editReminder.getTitle();
            reminderTypeEdit = editReminder.getType();
            reminderLatEdit = editReminder.getLat();
            reminderLngEdit = editReminder.getLng();
            reminderRadiusEdit = editReminder.getRadius();

            mSeekBar.setProgress(((int) reminderRadiusEdit)/10);

        }

        Flow = intentDelete.getStringExtra("Flow");
        Reminder deleteReminder = (Reminder) intentDelete.getSerializableExtra("DeleteReminder");

        if (Flow != null && Flow.equalsIgnoreCase("DeleteReminder")) {
            idDelete = deleteReminder.getId();
            reminderTitleDelete = deleteReminder.getTitle();
            reminderTypeDelete = deleteReminder.getType();
            reminderLatDelete = deleteReminder.getLat();
            reminderLngDelete = deleteReminder.getLng();
            reminderRadiusDelete = deleteReminder.getRadius();

            mSeekBar.setProgress(((int) reminderRadiusDelete)/10);
            System.out.println("Seekbar Value:-"+reminderRadiusDelete);
            System.out.println("SeekBar int:-"+(((int) reminderRadiusDelete)/10));

            deleteDialog();

        }

        fabTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Flow != null && Flow.equalsIgnoreCase("EditReminder")) {
                    Reminder reminderModify = new Reminder(idEdit, reminderTitle, reminderType, reminderLat, reminderLng, circleRadius);
                    intentModify.putExtra("modify", reminderModify);
                    //intentModify.putExtra("modifyId",idEdit);
                    setResult(2, intentModify);
                    finish();
                } else {
                    Reminder reminder = new Reminder(reminderTitle, reminderType, reminderLat, reminderLng, circleRadius);
                    System.out.println("at Click:- " + reminderTitle.toString());
                    intent.putExtra("data", reminder);
                    setResult(1, intent);
                    finish();
                }

                startGeofence();

            }
        });

        mSeekBar.setOnProgressChangeListener(new mOnSeekBarChangeListener());


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        addFewMarkers();





        /*if (Flow!=null && Flow.equalsIgnoreCase("EditReminder")){

            LatLng editLatLng = new LatLng(reminderLatEdit,reminderLngEdit);
            mMap.addMarker(new MarkerOptions().position(editLatLng).title("Edit Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(editLatLng));

        }*/
    }

    private void addFewMarkers() {

        mMap.setOnMarkerClickListener(new MyMarkerClickListener());

        mMap.setOnMapClickListener(new MyMapClickListener());

        mMap.setOnInfoWindowClickListener(new MyInfoWindowClickListener());

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        if (Flow != null && Flow.equalsIgnoreCase("EditReminder")) {

            LatLng editLatLng = new LatLng(reminderLatEdit, reminderLngEdit);
            markerOptions = new MarkerOptions();
            markerOptions.position(editLatLng);
            markerOptions.title("Edit Reminder");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            mCurrLocationMarker.showInfoWindow();

            reminderLat = editLatLng.latitude;
            reminderLng = editLatLng.longitude;

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(editLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            CircleOptions circleOptions = new CircleOptions()
                    .center(mCurrLocationMarker.getPosition())
                    .radius(reminderRadiusEdit);
            circleDraw = mMap.addCircle(circleOptions);

        }
        else if (Flow != null && Flow.equalsIgnoreCase("DeleteReminder")) {

            LatLng deleteLatLng = new LatLng(reminderLatDelete, reminderLngDelete);
            markerOptions = new MarkerOptions();
            markerOptions.position(deleteLatLng);
            markerOptions.title("Delete Reminder");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //mCurrLocationMarker.showInfoWindow();

            reminderLat = deleteLatLng.latitude;
            reminderLng = deleteLatLng.longitude;

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(deleteLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            CircleOptions circleOptions = new CircleOptions()
                    .center(mCurrLocationMarker.getPosition())
                    .radius(reminderRadiusDelete);
            circleDraw = mMap.addCircle(circleOptions);

        }
        else {
            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("To Add Reminder Click On This Window...");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            mCurrLocationMarker.showInfoWindow();

            reminderLat = latLng.latitude;
            reminderLng = latLng.longitude;

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            CircleOptions circleOptions = new CircleOptions()
                    .center(mCurrLocationMarker.getPosition())
                    .radius(circleRadius);
            circleDraw = mMap.addCircle(circleOptions);
        }


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    /*@Override
    public void onResult(@NonNull Status status) {

    }*/

    class MyMapClickListener implements GoogleMap.OnMapClickListener {
        @Override
        public void onMapClick(LatLng latLng) {


            mCurrLocationMarker.setPosition(latLng);

            mCurrLocationMarker.showInfoWindow();

            reminderLat = latLng.latitude;
            reminderLng = latLng.longitude;

            circleDraw.setRadius(circleRadius);
            circleDraw.setCenter(latLng);

        }
    }

    class MyMarkerClickListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            mt(" Marker Click ");
            return false;
        }
    }

    class MyInfoWindowClickListener implements GoogleMap.OnInfoWindowClickListener {
        @Override
        public void onInfoWindowClick(Marker marker) {
            mt("InfoWindow Click ");

            final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            LayoutInflater inflater = (MapsActivity.this).getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.layout_addreminder_dialog, null));

            if (Flow != null && Flow.equalsIgnoreCase("EditReminder")) {
                builder.setMessage("EDIT REMINDER");
            } else {
                builder.setMessage("ADD REMINDER");
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Dialog f = (Dialog) dialog;

                    dialogTitle = (EditText) f.findViewById(R.id.dialogTitleReminder);
                    radioGroupType = (RadioGroup) f.findViewById(R.id.radioType);
                    int selectedId = radioGroupType.getCheckedRadioButtonId();
                    radioButtonType = (RadioButton) f.findViewById(selectedId);

                    reminderTitle = dialogTitle.getText().toString();

                    System.out.println("dialoge DAta:- " + reminderTitle.toString());

                    reminderType = radioButtonType.getText().toString();

                    mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(reminderTitle));
                    mCurrLocationMarker.showInfoWindow();

                }
            })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public MyInfoWindowAdapter(String reminderTitle) {
            ToShowOnInfoWindow = reminderTitle;
        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {

            View view = getLayoutInflater().inflate(R.layout.layout_infowindow, null);

            mInfoWindow = (TextView) view.findViewById(R.id.infoWindowTxtView);

            mInfoWindow.setText(ToShowOnInfoWindow);


            return view;

        }
    }

    public class mOnSeekBarChangeListener implements DiscreteSeekBar.OnProgressChangeListener {

        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            alpha = mSeekBar.getProgress();
            System.out.println("Seek Bar Value:-"+alpha);
            int a = alpha * 10;
            circleRadius = a;
            circleDraw.setRadius(circleRadius);

        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

        }
    }

    public void startGeofence() {
        //Log.i(TAG, "startGeofence()");
        if (mCurrLocationMarker != null) {
            Geofence geofence = createGeofence(mCurrLocationMarker.getPosition(), (float) circleRadius);

            System.out.println("Geofence Radius Float:- "+ (float) circleRadius );

            GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
            addGeofence(geofenceRequest);
        } else {
            //Log.e(TAG, "Geofence marker is null");
        }
    }

    //private static final long GEO_DURATION = 60 * 60 * 1000;
    //private static final String GEOFENCE_REQ_ID = "My Geofence";
    //private static final float GEOFENCE_RADIUS = 500.0f; // in meters

    // Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius) {
        //Log.d(TAG, "createGeofence");
        return new Geofence.Builder()

                .setRequestId(reminderTitle)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                      //  | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest createGeofenceRequest(Geofence geofence) {
        //Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent createGeofencePendingIntent() {
        //Log.d(TAG, "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);



    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        //Log.d(TAG, "addGeofence");


        // to change if error occurs********************


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, request, createGeofencePendingIntent());

            /*LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);*/
    }

    public void clearGeofence(String title) {
        //Log.d(TAG, "clearGeofence()");

        //LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,createGeofencePendingIntent());

        listOfGeofences =
        Collections.singletonList(title.toString());
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,listOfGeofences);

        /*LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                createGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if ( status.isSuccess() ) {
                    // remove drawing
                    removeGeofenceDraw();
                }
            }
        });*/
    }

    public void getCompleteAddress(double latitude, double longitude){

        result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }
                //sb.append(address.getLocality()).append("\n");
                //sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();
            }
        } catch (IOException e) {

        } finally {

            if (result != null) {

                /*result = "Latitude: " + latitude + " Longitude: " + longitude +
                        "\nAddress:\n" + result;*/
                result = "Address:- " + result;

            } else {

                /*result = "Latitude: " + latitude + " Longitude: " + longitude +
                        "\n Unable to get address for this lat-long.";*/
                result = " Unable to get address for this lat-long.";

            }
        }
    }


    public void deleteDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

        builder.setMessage("Are You Sure You Want To Delete")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        clearGeofence(reminderTitleDelete);

                        Reminder reminderModifyDelete = new Reminder(idDelete, reminderTitleDelete, reminderTypeDelete, reminderLatDelete, reminderLngDelete, reminderRadiusDelete);
                        intentDeleteModify.putExtra("delete", reminderModifyDelete);
                        //intentModify.putExtra("modifyId",idEdit);
                        setResult(3, intentDeleteModify);
                        finish();


                    }})
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        mapsActivity.finish();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


        /*final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        LayoutInflater inflater = (MapsActivity.this).getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.layout_addreminder_dialog, null))

            .setMessage("Are You Sure You Want To Delete")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    clearGeofence(reminderTitleDelete);

                    Reminder reminderModifyDelete = new Reminder(idDelete, reminderTitleDelete, reminderTypeDelete, reminderLatDelete, reminderLngDelete, reminderRadiusDelete);
                    intentDeleteModify.putExtra("delete", reminderModifyDelete);
                    //intentModify.putExtra("modifyId",idEdit);
                    setResult(3, intentDeleteModify);
                    finish();


                }})
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        mapsActivity.finish();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();*/


    }

    private void mt( String text ) {
        Toast.makeText( this, text, Toast.LENGTH_LONG).show();
    }

}