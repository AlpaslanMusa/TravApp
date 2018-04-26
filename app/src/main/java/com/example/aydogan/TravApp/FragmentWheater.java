package com.example.aydogan.TravApp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Created by aydogan on 26.03.18.
 */

public class FragmentWheater extends Fragment {
    protected View myFragmentView;
    private TextView temp;
    private TextView locatie;
    private TextView tijd_datum;
    private TextView weerbericht;
    private Button btnMakeJSONRequest;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_wheater, container, false);
        btnMakeJSONRequest = myFragmentView.findViewById(R.id.refresh);
        temp = myFragmentView.findViewById(R.id.temp);
        locatie = myFragmentView.findViewById(R.id.locatie);
        tijd_datum = myFragmentView.findViewById(R.id.tijd_datum);
        weerbericht = myFragmentView.findViewById(R.id.weerbericht);
        btnMakeJSONRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // making json object request
                makeJsonJSONRequest();
            }
        });
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Wheater");
    }

    public void makeJsonJSONRequest(){
        //Dit is een test we zullen deze url vervangen door een dynamisch aangemaakte waarbij wordt gekeken naar de locatie waarde persoon zich bevind
        String url ="api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&APPID=dbc34f5cdcffd9d33c323cc03d196aaa";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject main = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("wheater");
                    JSONObject object = array.getJSONObject(0);
                    String temp =String.valueOf(main.getDouble("temp"));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }, new ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
