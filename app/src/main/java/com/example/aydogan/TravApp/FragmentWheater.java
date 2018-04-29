package com.example.aydogan.TravApp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aydogan on 26.03.18.
 */

public class FragmentWheater extends Fragment {
    protected static final String TAG = "CurrentLocNearByPlaces";
    protected static final int LOC_REQ_CODE = 1;
    protected View myFragmentView;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected ImageView icoon;
    protected TextView temperatuur;
    protected TextView plaats;
    protected TextView tijd_datum;
    protected TextView weerbeschrijving;
    protected TextView vochtigheid;
    protected TextView luchtdruk;
    protected Button btnMakeJSONRequest;
    protected String lat;
    protected String lon;
    protected String temp_c;
    protected String weerbericht_c;
    protected String locatie_c;
    protected String luchtdruk_c;
    protected String vochtigheid_c;
    protected long tijd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_wheater, container, false);
        btnMakeJSONRequest = myFragmentView.findViewById(R.id.refresh);
        temperatuur = myFragmentView.findViewById(R.id.temp);
        plaats = myFragmentView.findViewById(R.id.locatie);
        tijd_datum = myFragmentView.findViewById(R.id.tijd_datum);
        weerbeschrijving = myFragmentView.findViewById(R.id.weerbericht);
        icoon = myFragmentView.findViewById(R.id.weericoon);
        luchtdruk = myFragmentView.findViewById(R.id.druk);
        vochtigheid = myFragmentView.findViewById(R.id.luchtvochtigheid);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        btnMakeJSONRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                makeJsonJSONRequest();
            }
        });
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Wheater");
        isLocatieServicesActief();
        makeJsonJSONRequest();
        makeJsonJSONRequest();
        makeJsonJSONRequest();
    }

    public void makeJsonJSONRequest() {
        getCurrentPlaceData();
        //Dit is een test we zullen deze url vervangen door een dynamisch aangemaakte waarbij wordt gekeken naar de locatie waar de persoon zich bevind
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lon) + "&APPID=dbc34f5cdcffd9d33c323cc03d196aaa";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main = response.getJSONObject("main");
                    tijd = response.getLong("dt");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    temp_c = "temperatuur: " + String.valueOf(Math.round(main.getDouble("temp")) - 273) + "°C";
                    vochtigheid_c = "luchtvochtigheid: " + String.valueOf(main.getDouble("humidity")) + "%";
                    luchtdruk_c = "Druk: " + String.valueOf(main.getDouble("pressure")) + "hPa";
                    weerbericht_c = "weersvoorspelling: " + object.getString("main") + ", " + object.getString("description");
                    String ico = object.getString("icon");
                    locatie_c = "locatie: " + response.getString("name");
                    luchtdruk.setText(luchtdruk_c);
                    vochtigheid.setText(vochtigheid_c);
                    tijd_datum.setText("Datum: " + EpochToDate(tijd, "yyyy-mm-dd"));
                    temperatuur.setText(temp_c);
                    plaats.setText(locatie_c);
                    weerbeschrijving.setText(weerbericht_c);
                    new GetImageFromURL(icoon).execute("http://openweathermap.org/img/w/" + ico + ".png");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(jor);
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

    private void getCurrentPlaceData() {
        //kijken of er permission is gegeven aan de app om gebruikt te maken van gps
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                List<Place> placesList = new ArrayList<Place>();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                    if (1 - placeLikelihood.getLikelihood() <= 0.5) {
                        lat = String.valueOf(placeLikelihood.getPlace().getLatLng().latitude);
                        lon = String.valueOf(placeLikelihood.getPlace().getLatLng().longitude);
                    }
                    placesList.add(placeLikelihood.getPlace().freeze());
                }
                likelyPlaces.release();
            }

        });

    }

    public class GetImageFromURL extends AsyncTask<String, Void, Bitmap> {
        ImageView img;
        Bitmap bitm;

        public GetImageFromURL(ImageView imgV) {
            this.img = imgV;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay = url[0];
            bitm = null;
            try {
                InputStream is = new URL(urldisplay).openStream();
                bitm = BitmapFactory.decodeStream(is);
                float aspectRatio = bitm.getWidth() /
                        (float) bitm.getHeight();
                int width = 240;
                int height = Math.round(width / aspectRatio);
                bitm = Bitmap.createScaledBitmap(bitm, width, height, false);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            img.setImageBitmap(bitmap);
        }
    }

    public static String EpochToDate(long time, String formatString) {
        Date updatedate = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(updatedate);

    }
}
