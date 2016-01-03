package com.example.sushma.mylogin;

import android.content.Context;

import android.content.Intent;
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
import android.app.Dialog;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * Created by Vedhapriya on 10/22/2015.
 */
public class ViewActivity extends ActionBarActivity{
    Context context = this;
    private String TAG  = "Display Images from single stream";
    public final static String EXTRA_MESSAGE2 = "package com.example.vedhapriya.miniproject.MESSAGE2";
    public final static String EMAIL_PARAM = "package com.example.vedhapriya.miniproject.EMAIL";
    String stream_name;
    String email;

    public void sendViewStreams(View view){
        Intent intent = new Intent(this, ViewAllStreams.class);
        intent.putExtra(EMAIL_PARAM, email);
        startActivity(intent);
    }

    public void sendUploadImage(View view){
        Intent intent = new Intent(this, ImageUpload.class);
        intent.putExtra(EXTRA_MESSAGE2, stream_name);
        intent.putExtra(EMAIL_PARAM, email);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsinglestream);
        Intent intent = getIntent();
        stream_name = intent.getStringExtra(ViewAllStreams.EXTRA_MESSAGE1);
        if (stream_name == null) {
            stream_name = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE3);

        }
        email = intent.getStringExtra(ViewAllStreams.EMAIL_PARAM);
        if (email == null){
                email = intent.getStringExtra(SearchActivity.EMAIL_PARAM);
                if (email == null){
                    email = intent.getStringExtra(SubscribeActivity.EMAIL_PARAM);
                }
                }
        final String request_url = "http://miniproject1-1084.appspot.com/mobileStreamPics?stream_name=" + stream_name;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> imageURLs = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray displayImages = jObject.getJSONArray("displayImages");

                    for (int i = 0; i < displayImages.length(); i++) {

                        imageURLs.add(displayImages.getString(i));
                        System.out.println(displayImages.getString(i));
                    }

                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new ImageAdapter(context, imageURLs));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                            Picasso.with(context).load(imageURLs.get(position)).into(image);

                            imageDialog.show();

                        }
                    });

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

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
