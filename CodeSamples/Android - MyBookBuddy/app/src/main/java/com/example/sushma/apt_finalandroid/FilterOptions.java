package com.example.sushma.apt_finalandroid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sushma on 12/24/15.
 */
public class FilterOptions extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final ArrayList<String> SUGGESTIONS = new ArrayList<String>();
    private String TAG  = "Filter Activity";
    private GoogleApiClient mGoogleApiClient;
    public SimpleCursorAdapter mAdapter;
    Context context = this;
    String email;
    String userName;
    String flag;
    String displayFlag;
    public final static String BOOK_NAME = "package com.example.sushma.apt_finalandroid.BOOKNAME";
    public static final String EMAIL_PARAM = "package com.example.sushma.apt_finalandroid.EMAIL";
    public static final String IMAGE_URL = "package com.example.sushma.apt_finalandroid.IMAGEURL";
    public static final String USER_NAME = "package com.example.sushma.apt_finalandroid.USERNAME";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        Intent data = getIntent();
        email = data.getStringExtra("EMAIL_PARAM");
        flag = data.getStringExtra("FLAG");
        userName = data.getStringExtra("USER_NAME");
        if(flag.equals("ALL")){
            displayFlag = "All";
        }
        else if(flag.equals("AVAIL")){
            displayFlag = "Available";
        }
        else if(flag.equals("RENT")){
            displayFlag = "Rented";
        }
        else if(flag.equals("SOLD")){
            displayFlag = "Sold";
        }
        final String request_url = "http://engineapp-1084.appspot.com/getListingForSearch";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray listingForSearch = jObject.getJSONArray("listingForSearch");

                    for (int i = 0; i < listingForSearch.length(); i++) {

                        SUGGESTIONS.add(listingForSearch.getString(i));
                    }
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });
        final String request_url1 = "http://engineapp-1084.appspot.com/getResultsForFilter?Flag="+flag;
        AsyncHttpClient httpClient1 = new AsyncHttpClient();
        httpClient1.get(request_url1, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> imageURLs = new ArrayList<String>();
                final ArrayList<String> imageCaps = new ArrayList<String>();
                final ArrayList<String> imageAuthor = new ArrayList<String>();
                final ArrayList<String> imageStatus = new ArrayList<String>();
                final ArrayList<String> imageCost = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray displayImages = jObject.getJSONArray("bookImages");
                    JSONArray displayCaption = jObject.getJSONArray("bookNameList");
                    JSONArray displayAuthor = jObject.getJSONArray("bookAuthor");
                    JSONArray displayStatus = jObject.getJSONArray("bookStatus");
                    JSONArray displayCost = jObject.getJSONArray("bookCost");

                    for (int i = 0; i < displayImages.length(); i++) {

                        imageURLs.add(displayImages.getString(i));
                        imageCaps.add(displayCaption.getString(i));
                        imageAuthor.add(displayAuthor.getString(i));
                        imageStatus.add(displayStatus.getString(i));
                        imageCost.add(displayCost.getString(i));
                        System.out.println(displayImages.getString(i));
                        System.out.println(displayCaption.getString(i));
                    }

                    TextView dytext = (TextView) findViewById(R.id.dynamictext);
                    dytext.setTextSize(13);
                    if (imageCaps.size() == 0) {
                        dytext.setText("0 results for ' " + displayFlag + " '");
                    } else {
                        dytext.setText(imageCaps.size() + " results with status '" + displayFlag + "', click on the book to view details");
                    }


                    GridView gridview = (GridView) findViewById(R.id.grid);
                    gridview.setAdapter(new ImageAdapter(context, imageURLs, imageCaps, imageAuthor, imageStatus, imageCost));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Toast.makeText(context, "Book Name: " + imageCaps.get(position), Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(context, ListingDetails.class);
                            intent.putExtra(BOOK_NAME, imageCaps.get(position));
                            intent.putExtra(EMAIL_PARAM, email);
                            intent.putExtra(USER_NAME, userName);
                            intent.putExtra(IMAGE_URL, imageURLs.get(position));
                            startActivity(intent);
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

        setContentView(R.layout.activity_filter_options);
        final String[] from = new String[]{"bookName"};
        final int[] to = new int[]{android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setIconifiedByDefault(false);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String query = cursor.getString(1);
                searchView.setQuery(query,false);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return true;
            }
        });

        return true;
    }

    @Override
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra("EMAIL_PARAM", email);
            intent.putExtra("USER_NAME",userName);
        }

        super.startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.logout) {
            handleLogout();
        }
        else if(id == R.id.iconCreate){
            Intent intent= new Intent(context, ImageUpload.class);
            intent.putExtra("EMAIL_PARAM", email);
            intent.putExtra("USER_NAME",userName);
            startActivity(intent);
        }
        else if(id == R.id.iconProfile){
            Intent intent= new Intent(context, UserProfile.class);
            intent.putExtra("EMAIL_PARAM", email);
            intent.putExtra("USER_NAME",userName);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleLogout(){
        // We clear the default account on sign out so that Google Play
        // services will not return an onConnected callback without user
        // interaction.
        signOutFromGplus();
    }
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sign-out from google method to be called in logout button click
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

            Intent intent= new Intent(context, MainActivity.class);
            startActivity(intent);

        }
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle){
        Log.i(TAG, "Connected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    public void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "bookName" });
        for (int i=0; i<SUGGESTIONS.size(); i++) {
            if (SUGGESTIONS.get(i).toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, SUGGESTIONS.get(i)});
        }
        mAdapter.changeCursor(c);
    }
}
