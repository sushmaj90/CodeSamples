package com.example.sushma.mylogin;

import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * Created by Vedhapriya on 10/22/2015.
 */
public class SearchActivity  extends ActionBarActivity{
    Context context = this;
    private String TAG  = "Search stream";
    public final static String EXTRA_MESSAGE = "package com.example.vedhapriya.miniproject.MESSAGE";
    public final static String EXTRA_MESSAGE3 = "package com.example.vedhapriya.miniproject.MESSAGE3";
    public final static String EMAIL_PARAM = "package com.example.vedhapriya.miniproject.EMAIL";
    String email;

    public void sendViewStreamsSearchInput(View view){
       /* just for test!!!!!*/
        Intent intent = new Intent(this, SearchActivity.class);
        TextView searchInput = (TextView) findViewById(R.id.ViewStreamsSearchText);
        String key_words = searchInput.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, key_words);
        intent.putExtra(EMAIL_PARAM, email);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchstreams);
        Intent intent = getIntent();
        final String message = intent.getStringExtra(ViewAllStreams.EXTRA_MESSAGE);
        email = intent.getStringExtra(ViewAllStreams.EMAIL_PARAM);

        final String request_url = "http://miniproject1-1084.appspot.com/searchStream?srch=" + message;
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

                    TextView dytext = (TextView) findViewById(R.id.dynamictext);
                    dytext.setTextSize(20);
                    dytext.setTextColor(Color.BLACK);
                    if (imageCaps.size() == 0) {
                        dytext.setText("0 results for ' " + message + " '");
                    }
                        else{
                            dytext.setText(imageCaps.size() + " results for '" + message + "', click on an image to view stream");
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
//                Intent intent = new Intent(context, ViewActivity.class);
//                Bundle b = new Bundle();
//                b.putString("stream_name",stream_name);
//                intent.putExtras(b);
//                startActivity(intent);
//                finish();
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra(EXTRA_MESSAGE3, stream_name);
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
}
