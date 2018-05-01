package com.example.aydogan.TravApp;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by aydogan on 26.03.18.
 */

public class FragmentWheater extends Fragment {
    public static final String TAG = "CurrentLocNearByPlaces";
    protected static final int LOC_REQ_CODE = 1;
    protected View myFragmentView;
    protected RecyclerView recyclerView;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected String lat;
    protected String lon;
    protected String temp;
    protected String weerbericht;
    protected String locatie;
    protected String luchtdruk;
    protected String vochtigheid;
    protected String tijd;
    protected List<String[]> weatherList;
    protected String[] weather;

    public static String EpochToDate(long time, String formatString) {
        Date updatedate = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(updatedate);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        isLocatieServicesActief();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getCurrentPlace();
        getJSONData();
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        myFragmentView = inflater.inflate(R.layout.fragment_wheater, container, false);
        recyclerView = myFragmentView.findViewById(R.id.weather_lst);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), recyclerLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        getCurrentPlace();
        getJSONData();
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Wheater");
    }

    private void requestLocationAccessPermission() {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
    }

    public void isLocatieServicesActief() {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this.getActivity());
            dialog.setMessage("gps is niet geactiveerd");
            dialog.setPositiveButton("open locatie instellingen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    private void getCurrentPlace() {
        //kijken of er permission is gegeven aan de app om gebruikt te maken van gps
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //is dit niet het geval dan wordt er om toestemming gevraagd
            requestLocationAccessPermission();
            return;
        }
        //De werking van volgende code wordt in detail bescheven op de google API service site
        //Ruw genomen wordt er met gps data gevoerd aan de places API en die geeft meerdere nabij gelegeven locaties door met bij
        //elke locatie een waarschijnlijkheid dat de gebruiker zich daar bevindt
        //al deze locaties worden dan in een lijst bijgehouden en gevoerd aan de recyclerview
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                List<Place> placesList = new ArrayList<>();
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g", placeLikelihood.getPlace().getName(), placeLikelihood.getLikelihood()));
                        placesList.add(placeLikelihood.getPlace());
                    }
                    likelyPlaces.release();
                lat = String.valueOf(placesList.get(0).getLatLng().latitude);
                lon = String.valueOf(placesList.get(0).getLatLng().longitude);
            }
        });

    }

        protected void getJSONData() {
            getCurrentPlace();
            getCurrentPlace();
            String urldisplay = "http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&APPID=dbc34f5cdcffd9d33c323cc03d196aaa";
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, urldisplay, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray list = response.getJSONArray("list");
                        weatherList = new ArrayList<>();
                        for (int i = 0; i <= list.length() - 1; i++) {
                            JSONObject huidigeresponse = list.getJSONObject(i);
                            JSONObject main = huidigeresponse.getJSONObject("main");
                            tijd = huidigeresponse.getString("dt_txt");
                            JSONArray array = huidigeresponse.getJSONArray("weather");
                            JSONObject object = array.getJSONObject(0);
                            temp = "temperatuur: " + String.valueOf(Math.round(main.getDouble("temp")) - 273) + "°C";
                            vochtigheid = "luchtvochtigheid: " + String.valueOf(main.getDouble("humidity")) + "%";
                            luchtdruk = "Druk: " + String.valueOf(main.getDouble("pressure")) + "hPa";
                            weerbericht = "weersvoorspelling: " + object.getString("main") + ", " + object.getString("description");
                            String ico = object.getString("icon");
                            //locatie = "locatie: " + huidigeresponse.getString("name");
                            weather = new String[5];
                            weather[0] = temp;
                            weather[1] = vochtigheid;
                            weather[2] = luchtdruk;
                            weather[3] = weerbericht;
                            weather[4] = tijd;
                            weatherList.add(weather);
                        }
                        //new GetImageFromURL(icoon).execute("http://openweathermap.org/img/w/" + ico + ".png");
                        WeatherRecyclerViewAdapter recyclerViewAdapter = new WeatherRecyclerViewAdapter(weatherList, getContext());
                        recyclerView.setAdapter(recyclerViewAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(jor);
        }
    }

