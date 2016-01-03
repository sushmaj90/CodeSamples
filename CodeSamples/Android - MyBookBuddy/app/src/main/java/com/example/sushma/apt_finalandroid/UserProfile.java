package com.example.sushma.apt_finalandroid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;*/
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class UserProfile extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String TAG  = "View user profile";
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGoogleApiClientplus;
    public Location mLastLocation;
    private String userName;
    private String user_Name;
    private String userUniversity;
    private String profilepicURL;
    private String currentRating;
    private String numRatings;
    public String lati;
    public String longi;
    final ArrayList<String> SUGGESTIONS = new ArrayList<String>();
    public SimpleCursorAdapter mAdapter;
    String email;
    Context context = this;
    public final static String BOOK_NAME = "package com.example.sushma.apt_finalandroid.BOOKNAME";
    public static final String EMAIL_PARAM = "package com.example.sushma.apt_finalandroid.EMAIL";
    public static final String IMAGE_URL = "package com.example.sushma.apt_finalandroid.IMAGEURL";
    public static final String USER_NAME = "package com.example.sushma.apt_finalandroid.USERNAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL_PARAM");
        user_Name = intent.getStringExtra("USER_NAME");
        setContentView(R.layout.activity_user_profile);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClientplus = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        mGoogleApiClientplus.connect();
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

        final String request_url1 = "http://engineapp-1084.appspot.com/userProfile?id="+email;
        AsyncHttpClient httpClient1 = new AsyncHttpClient();
        httpClient1.get(request_url1, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> userlist = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray userlistitems = jObject.getJSONArray("userList");

                    for (int i = 0; i < userlistitems.length(); i++) {

                        userlist.add(userlistitems.getString(i));
                        System.out.println(userlistitems.getString(i));
                    }

                    userName = userlist.get(0);
                    userUniversity = userlist.get(1);
                    profilepicURL = userlist.get(2);
                    currentRating = userlist.get(3);
                    numRatings = userlist.get(4);
                    TextView user_name = (TextView) findViewById(R.id.username);
                    user_name.setText(userName);
                    TextView universiy = (TextView) findViewById(R.id.university);
                    universiy.setText(userUniversity);
                    TextView user_email = (TextView) findViewById(R.id.useremail);
                    user_email.setText(email);
                    TextView rating = (TextView) findViewById(R.id.rating);
                    TextView numRating = (TextView) findViewById(R.id.numRating);
                    if(currentRating.equals("null")){
                        rating.setText("No ratings yet!");
                    }
                    else{
                        DecimalFormat df = new DecimalFormat("##.##");
                        df.setRoundingMode(RoundingMode.DOWN);
                        Double fRating = Double.parseDouble(currentRating);
                        String trucRating = df.format(fRating);
                        rating.setText("Current rating: "+trucRating+"/5");
                        numRating.setText("Number of Ratings: "+numRatings);
                    }
                    ImageView imageView = (ImageView) findViewById(R.id.imageview_profile);
                    Picasso.with(context)
                            .load(profilepicURL)
                            .into(imageView);

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });
        final String request_url2 = "http://engineapp-1084.appspot.com/retrieveLOwnedHome?userID=" + email;
        AsyncHttpClient httpClient2 = new AsyncHttpClient();
        httpClient2.get(request_url2, new AsyncHttpResponseHandler() {
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
                    if(displayImages.length() > 0) {

                    for (int i = 0; i < displayImages.length(); i++) {

                        imageURLs.add(displayImages.getString(i));
                        imageCaps.add(displayCaption.getString(i));
                        imageAuthor.add(displayAuthor.getString(i));
                        imageStatus.add(displayStatus.getString(i));
                        imageCost.add(displayCost.getString(i));
                        System.out.println(displayImages.getString(i));
                        System.out.println(displayCaption.getString(i));
                    }

                    GridView gridview = (GridView) findViewById(R.id.gridProfile1);
                    gridview.setAdapter(new ImageAdapter(context, imageURLs, imageCaps, imageAuthor, imageStatus, imageCost));
                    setGridViewHeightBasedOnChildren(gridview, 3);
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Toast.makeText(context, "Book Name: " + imageCaps.get(position), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, ListingDetails.class);
                            intent.putExtra(BOOK_NAME, imageCaps.get(position));
                            intent.putExtra(EMAIL_PARAM, email);
                            intent.putExtra(USER_NAME, user_Name);
                            intent.putExtra(IMAGE_URL, imageURLs.get(position));
                            startActivity(intent);
                        }
                    });
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
        final String request_url3 = "http://engineapp-1084.appspot.com/mostViewed";
        AsyncHttpClient httpClient3 = new AsyncHttpClient();
        httpClient3.get(request_url3, new AsyncHttpResponseHandler() {
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
                    if(displayImages.length() > 0) {

                    for (int i = 0; i < displayImages.length(); i++) {

                        imageURLs.add(displayImages.getString(i));
                        imageCaps.add(displayCaption.getString(i));
                        imageAuthor.add(displayAuthor.getString(i));
                        imageStatus.add(displayStatus.getString(i));
                        imageCost.add(displayCost.getString(i));
                        System.out.println(displayImages.getString(i));
                        System.out.println(displayCaption.getString(i));
                    }

                    GridView gridview = (GridView) findViewById(R.id.gridProfile);
                    gridview.setAdapter(new ImageAdapter(context, imageURLs, imageCaps, imageAuthor, imageStatus, imageCost));
                    setGridViewHeightBasedOnChildren(gridview, 3);
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Toast.makeText(context, "Book Name: " + imageCaps.get(position), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, ListingDetails.class);
                            intent.putExtra(BOOK_NAME, imageCaps.get(position));
                            intent.putExtra(EMAIL_PARAM, email);
                            intent.putExtra(USER_NAME, user_Name);
                            intent.putExtra(IMAGE_URL, imageURLs.get(position));
                            startActivity(intent);
                        }
                    });
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
        final String request_url4 = "http://engineapp-1084.appspot.com/booksToReturn?userID="+email;
        AsyncHttpClient httpClient4 = new AsyncHttpClient();
        httpClient4.get(request_url4, new AsyncHttpResponseHandler() {
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
                    if(displayImages.length() > 0) {

                        for (int i = 0; i < displayImages.length(); i++) {

                            imageURLs.add(displayImages.getString(i));
                            imageCaps.add(displayCaption.getString(i));
                            imageAuthor.add(displayAuthor.getString(i));
                            imageStatus.add(displayStatus.getString(i));
                            imageCost.add(displayCost.getString(i));
                            System.out.println(displayImages.getString(i));
                            System.out.println(displayCaption.getString(i));
                        }

                        GridView gridview = (GridView) findViewById(R.id.gridProfile2);
                        gridview.setAdapter(new ImageAdapter(context, imageURLs, imageCaps, imageAuthor, imageStatus, imageCost));
                        setGridViewHeightBasedOnChildren(gridview, 3);
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v,
                                                    int position, long id) {

                                Toast.makeText(context, "Book Name: " + imageCaps.get(position), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, ListingDetails.class);
                                intent.putExtra(BOOK_NAME, imageCaps.get(position));
                                intent.putExtra(USER_NAME, user_Name);
                                intent.putExtra(EMAIL_PARAM, email);
                                intent.putExtra(IMAGE_URL, imageURLs.get(position));
                                startActivity(intent);
                            }
                        });
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
        final String request_url5 = "http://engineapp-1084.appspot.com/booksRentedOut?userID="+email;
        AsyncHttpClient httpClient5 = new AsyncHttpClient();
        httpClient5.get(request_url5, new AsyncHttpResponseHandler() {
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
                    if(displayImages.length() > 0) {

                        for (int i = 0; i < displayImages.length(); i++) {

                            imageURLs.add(displayImages.getString(i));
                            imageCaps.add(displayCaption.getString(i));
                            imageAuthor.add(displayAuthor.getString(i));
                            imageStatus.add(displayStatus.getString(i));
                            imageCost.add(displayCost.getString(i));
                            System.out.println(displayImages.getString(i));
                            System.out.println(displayCaption.getString(i));
                        }

                        GridView gridview = (GridView) findViewById(R.id.gridProfile3);
                        gridview.setAdapter(new ImageAdapter(context, imageURLs, imageCaps, imageAuthor, imageStatus, imageCost));
                        setGridViewHeightBasedOnChildren(gridview, 3);
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v,
                                                    int position, long id) {

                                Toast.makeText(context, "Book Name: " + imageCaps.get(position), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, ListingDetails.class);
                                intent.putExtra(BOOK_NAME, imageCaps.get(position));
                                intent.putExtra(EMAIL_PARAM, email);
                                intent.putExtra(USER_NAME, user_Name);
                                intent.putExtra(IMAGE_URL, imageURLs.get(position));
                                startActivity(intent);
                            }
                        });
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
        final String[] from = new String[] {"bookName"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        {
            @Override
            public View getView ( int position, View convertView, ViewGroup parent){
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

    /**
     * Sign-out from google method to be called in logout button click
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClientplus.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClientplus);
            mGoogleApiClientplus.disconnect();
            mGoogleApiClientplus.connect();

            Intent intent= new Intent(context, MainActivity.class);
            startActivity(intent);

        }
    }
    protected void onStart() {
        super.onStart();
        mGoogleApiClientplus.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClientplus.isConnected()) {
            mGoogleApiClientplus.disconnect();
        }
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
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lati = String.valueOf(mLastLocation.getLatitude());
            longi = String.valueOf(mLastLocation.getLongitude());
            final String request_url = "http://engineapp-1084.appspot.com/booksNearBy?la=" + lati + "&lo=" + longi;
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.get(request_url, new AsyncHttpResponseHandler() {
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

                        GridView gridview = (GridView) findViewById(R.id.gridNearby);
                        gridview.setAdapter(new ImageAdapter(context, imageURLs, imageCaps, imageAuthor, imageStatus, imageCost));
                        setGridViewHeightBasedOnChildren(gridview,3);
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v,
                                                    int position, long id) {

                                Toast.makeText(context, "Book Name: " + imageCaps.get(position), Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(context, ListingDetails.class);
                                intent.putExtra(BOOK_NAME, imageCaps.get(position));
                                intent.putExtra(EMAIL_PARAM, email);
                                intent.putExtra(USER_NAME,user_Name);
                                intent.putExtra(IMAGE_URL, imageURLs.get(position));
                                startActivity(intent);
                            }
                        });

                    }
                    catch(JSONException j){
                        System.out.println("JSON Error");
                    }

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

    public void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "bookName" });
        for (int i=0; i<SUGGESTIONS.size(); i++) {
            if (SUGGESTIONS.get(i).toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, SUGGESTIONS.get(i)});
        }
        mAdapter.changeCursor(c);
    }
    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }



}

