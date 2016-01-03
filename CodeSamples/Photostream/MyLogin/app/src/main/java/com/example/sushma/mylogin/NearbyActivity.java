package com.example.sushma.mylogin;

import android.content.Context;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * Created by sushma on 10/26/15.
 */
public class NearbyActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {
    Context context = this;
    private GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public String lati;
    public String longi;
    private String TAG  = "Display Streams";
    public final static String EXTRA_MESSAGE = "package com.example.vedhapriya.miniproject.MESSAGE";
    public final static String EXTRA_MESSAGE1 = "package com.example.vedhapriya.miniproject.VIEWMESSAGE";
    public final static String EMAIL_PARAM = "package com.example.vedhapriya.miniproject.EMAIL";
    String email;

    public void sendViewStreamsSearchInput(View view){
       /* just for test!!!!!*/
        Intent intent = new Intent(this, SearchActivity.class);
        TextView searchInput = (TextView) findViewById(R.id.ViewStreamsSearchText);
        String key_words = searchInput.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, key_words);
        startActivity(intent);
    }

    public void findNearByStreams(View view){
        Intent intent = new Intent(context, NearbyActivity.class);
        intent.putExtra(EMAIL_PARAM, email);
        startActivity(intent);
        finish();
    }

    public void toMySubscribedStreams(View view){
        Intent intent = new Intent(context, SubscribeActivity.class);
        intent.putExtra(EMAIL_PARAM, email);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewallstreams);
        Intent intent = getIntent();
        email = intent.getStringExtra(ViewAllStreams.EMAIL_PARAM);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lati = String.valueOf(mLastLocation.getLatitude());
            longi = String.valueOf(mLastLocation.getLongitude());
            //System.out.println("latitude: " + String.valueOf(lati));
            //System.out.println("longitude: " + String.valueOf(longi));
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            final String request_url = "http://miniproject1-1084.appspot.com/nearby?la=" + lati + "&lo=" + longi;
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.get(request_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    final ArrayList<String> imageURLs = new ArrayList<String>();
                    final ArrayList<String> imageCaps = new ArrayList<String>();
                    try {
                        JSONObject jObject = new JSONObject(new String(response));
                        JSONArray displayImages = jObject.getJSONArray("displayImages");
                        JSONArray displayCaption = jObject.getJSONArray("imageCaptionList");

                        for(int i=0;i<displayImages.length();i++) {

                            imageURLs.add(displayImages.getString(i));
                            imageCaps.add(displayCaption.getString(i));
                            System.out.println(displayImages.getString(i));
                            System.out.println(displayCaption.getString(i));
                        }

                        GridView gridview = (GridView) findViewById(R.id.gridview);
                        gridview.setAdapter(new ImageAdapter(context,imageURLs));
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v,
                                                    int position, long id) {

                                Toast.makeText(context, "Stream Name: " + imageCaps.get(position), Toast.LENGTH_SHORT).show();
                                viewSingleStream(imageCaps.get(position));
                            }
                        });

                    }
                    catch(JSONException j){
                        System.out.println("JSON Error");
                    }

                }

                public void viewSingleStream(String stream_name){
                    // TO SINGLE STREAM ACTIVITY
                    //Intent intent = new Intent(context, ViewActivity.class);
                    //Bundle b = new Bundle();
                    //b.putString("stream_name",stream_name);
                    //intent.putExtras(b);
                    //startActivity(intent);
                    //finish();
                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra(EXTRA_MESSAGE1, stream_name);
                    intent.putExtra(EMAIL_PARAM, email);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
                }
            });
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("GPS connection suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("GPS connection failed");

    }
}
