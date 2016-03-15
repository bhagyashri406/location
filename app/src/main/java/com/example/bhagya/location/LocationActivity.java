package com.example.bhagya.location;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    List<Transport> Trans = new ArrayList<>();
    TextView carTime;
    TextView trainTime;
    Button navigate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        String Url = "http://express-it.optusnet.com.au/sample.json";
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        carTime = (TextView) findViewById(R.id.car_time);
        trainTime = (TextView) findViewById(R.id.train_time);
        navigate = (Button) findViewById(R.id.button);
        List<String> categories = new ArrayList<String>();
        categories.add("Blue Mountains");
        categories.add("taronga zoo");
        categories.add("Bondi Beach");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        getData(Url);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);


        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Navigate to location ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        if (Trans.size() == 0) {
            carTime.setText("");
            trainTime.setText("");
            Toast.makeText(parent.getContext(),"select location",Toast.LENGTH_LONG).show();
        } else {
            carTime.setText(Trans.get(position).car);
            trainTime.setText(Trans.get(position).train);
            Toast.makeText(parent.getContext(), "Selected: " + "" + Trans.get(position).latitude + "\n" + Trans.get(position).longitude, Toast.LENGTH_LONG).show();
            SharedPreferences sharedPreferences = getSharedPreferences("latLong", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lat" ,Trans.get(position).latitude);
            editor.putString("long",Trans.get(position).longitude);
            editor.putString("location",Trans.get(position).name);
            editor.commit();
        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    void getData(String url) {
        RequestQueueSingleton rqs = RequestQueueSingleton.getInstance(getApplicationContext());
        JsonArrayRequest jreq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Log.i("response", response.toString());
                                JSONObject jo = response.getJSONObject(i);
                                String name = jo.getString("name");
                                JSONObject fromcentral = jo.getJSONObject("fromcentral");
                                String car = fromcentral.optString("car");
                                String train = fromcentral.optString("train");
                                JSONObject location = jo.getJSONObject("location");
                                String latitude = location.getString("latitude");
                                String longitude = location.getString("longitude");
                                Transport transport = new Transport(name, car, train, latitude, longitude);
                                Trans.add(transport);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.i("errorJsonException",  e.toString());

                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error" , error.toString());
            }
        });
        rqs.addToRequestQueue(jreq);
    }
}