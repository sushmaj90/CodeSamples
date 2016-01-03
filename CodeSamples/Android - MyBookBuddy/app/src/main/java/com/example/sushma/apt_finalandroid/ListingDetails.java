package com.example.sushma.apt_finalandroid;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.ArrayList;

/**
 * Created by sushma on 12/20/15.
 */
public class ListingDetails extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Context context = this;
    private GoogleApiClient mGoogleApiClient;
    private String TAG  = "Listing Details";
    final ArrayList<String> SUGGESTIONS = new ArrayList<String>();
    public SimpleCursorAdapter mAdapter;
    public String email;
    public String userName;
    public String bookname;
    public String imageURL;
    public String buySellFlag;
    public String cost;
    public String listingOwnerID;
    public String listingOwner;
    public float finalCost;
    public boolean validInput;
    public static final String EMAIL_PARAM = "package com.example.sushma.apt_finalandroid.EMAIL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        final String request_url = "http://engineapp-1084.appspot.com/getListingForSearch";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray listingForSearch = jObject.getJSONArray("listingForSearch");

                    for(int i=0;i<listingForSearch.length();i++) {

                        SUGGESTIONS.add(listingForSearch.getString(i));
                    }
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

        Intent intent = getIntent();
        email = intent.getStringExtra(SearchActivity.EMAIL_PARAM);
        bookname = intent.getStringExtra(SearchActivity.BOOK_NAME);
        imageURL = intent.getStringExtra(SearchActivity.IMAGE_URL);
        userName = intent.getStringExtra(SearchActivity.USER_NAME);
        setContentView(R.layout.activity_listing_details);
        final String request_url1 = "http://engineapp-1084.appspot.com/retrieveListingDetails?bookName="+bookname;
        AsyncHttpClient httpClient1 = new AsyncHttpClient();
        httpClient1.get(request_url1, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    String author = jObject.getString("author").trim();
                    String category = jObject.getString("category").trim();
                    String pickup_address = jObject.getString("pickup_address");
                    String rent_sell_flag = jObject.getString("rent_sell_flag").trim();
                    cost = jObject.getString("cost").trim();
                    String status = jObject.getString("status").trim();
                    String rental_days_left = jObject.getString("rental_days_left").trim();
                    listingOwnerID = jObject.getString("listingOwnerID").trim();
                    listingOwner = jObject.getString("listingOwner").trim();
                    String currentOwner = jObject.getString("currentOwner").trim();
                    String currentOwnerID = jObject.getString("currentOwnerID").trim();
                    String listingOwnerRating = jObject.getString("listingOwnerRating").trim();
                    if(email.equals(listingOwnerID)){
                        TextView invOwnerStatus = (TextView) findViewById(R.id.invOwnerStatus);
                        invOwnerStatus.setVisibility(View.VISIBLE);
                        invOwnerStatus.setText("You are the owner of this book");
                        invOwnerStatus.setTextColor(Color.RED);
                        TextView list_text5 = (TextView) findViewById(R.id.list_text5);
                        list_text5.setText("Cost: " + cost + "$");
                        Button btnBuyRent = (Button) findViewById(R.id.btnBuyRent);
                        btnBuyRent.setText("Remove this book");
                        buySellFlag = "REM";
                    }
                    else {
                        Button btnBuyRent = (Button) findViewById(R.id.btnBuyRent);
                        if (rent_sell_flag.equals("Rent")) {
                            if(status.equals("Rented") && email.equals(currentOwnerID))
                            {
                                TextView invOwnerStatus = (TextView) findViewById(R.id.invOwnerStatus);
                                invOwnerStatus.setVisibility(View.VISIBLE);
                                invOwnerStatus.setTextColor(Color.RED);
                                invOwnerStatus.setText("You are currently holding on to this book");
                                TextView list_text5 = (TextView) findViewById(R.id.list_text5);
                                list_text5.setText("Cost per day: " + cost + "$");
                                EditText edRating = (EditText) findViewById(R.id.userRating);
                                edRating.setVisibility(View.VISIBLE);
                                btnBuyRent.setText("Return this book");
                                buySellFlag = "RET";

                            }
                            else {
                                btnBuyRent.setText("Rent this book");
                                EditText edDays = (EditText) findViewById(R.id.numDays);
                                edDays.setVisibility(View.VISIBLE);
                                TextView list_text5 = (TextView) findViewById(R.id.list_text5);
                                list_text5.setText("Cost per day: " + cost + "$");
                                buySellFlag = "RENT";
                            }
                        } else if (rent_sell_flag.equals("Sell")) {
                            if(status.equals("Sold")){
                                TextView invOwnerStatus = (TextView) findViewById(R.id.invOwnerStatus);
                                invOwnerStatus.setVisibility(View.VISIBLE);
                                invOwnerStatus.setTextColor(Color.RED);
                                invOwnerStatus.setText("Sold out!!");
                            }
                            else {
                                btnBuyRent.setText("Buy this book");
                                TextView list_text5 = (TextView) findViewById(R.id.list_text5);
                                list_text5.setText("Total Cost: " + cost + "$");
                                buySellFlag = "BUY";
                            }
                        }
                    }
                    ImageView imageView1 = (ImageView) findViewById(R.id.imgBook);
                    Picasso.with(context).load(imageURL).into(imageView1);
                    TextView bookTitle = (TextView) findViewById(R.id.bookTitle);
                    bookTitle.setText(bookname);
                    TextView bookStatus = (TextView) findViewById(R.id.bookStatus);
                    bookStatus.setText("Current Status: " + status);
                    if (status.equals("Rented")) {
                        TextView list_text6 = (TextView) findViewById(R.id.list_text6);
                        list_text6.setText("Rental days left:" + rental_days_left);
                    }
                    TextView list_text0 = (TextView) findViewById(R.id.list_text0);
                    list_text0.setText("Book Owner: " + listingOwner);
                    TextView list_text1 = (TextView) findViewById(R.id.list_text1);
                    list_text1.setText("Current Owner: " + currentOwner);
                    TextView list_text2 = (TextView) findViewById(R.id.list_text2);
                    list_text2.setText("Author: " + author);
                    TextView list_text3 = (TextView) findViewById(R.id.list_text3);
                    list_text3.setText("Category: " + category);
                    TextView list_text4 = (TextView) findViewById(R.id.list_text4);
                    list_text4.setText("Pickup address: " + pickup_address);
                    TextView list_text6 = (TextView) findViewById(R.id.list_text6);
                    list_text6.setText("Owner email: " + listingOwnerID);
                    TextView list_text7 = (TextView) findViewById(R.id.list_text7);
                    if(listingOwnerRating.equals("null")){
                        list_text7.setText("Owner rating:No ratings yet! ");
                    }
                    else {
                        list_text7.setText("Owner rating: " + listingOwnerRating);
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


        Button btnBuyRent = (Button) findViewById(R.id.btnBuyRent);
        btnBuyRent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (buySellFlag.equals("REM")) {
                            RequestParams params = new RequestParams();
                            params.put("flag", "REM");
                            params.put("bookName", bookname);
                            params.put("listingOwner", listingOwner);
                            params.put("buyerEmail", email);
                            params.put("listingOwnerID", listingOwnerID);
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post("http://engineapp-1084.appspot.com/postRentBuyData", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                    Log.w("async", "success!!!!");
                                    Toast.makeText(context, "Transaction Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent= new Intent(context, UserProfile.class);
                                    intent.putExtra("EMAIL_PARAM", email);
                                    intent.putExtra("USER_NAME", userName);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                                }
                            });

                        } else if (buySellFlag.equals("RENT")) {
                            if (validInput) {
                                EditText edDays = (EditText) findViewById(R.id.numDays);
                                String numDays = edDays.getText().toString();
                                RequestParams params = new RequestParams();
                                params.put("flag", "RENT");
                                params.put("bookName", bookname);
                                params.put("listingOwner", listingOwner);
                                params.put("buyerEmail", email);
                                params.put("rentalDays", numDays);
                                params.put("listingOwnerID", listingOwnerID);
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.post("http://engineapp-1084.appspot.com/postRentBuyData", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                        Log.w("async", "success!!!!");
                                        Toast.makeText(context, "Transaction Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent= new Intent(context, UserProfile.class);
                                        intent.putExtra("EMAIL_PARAM", email);
                                        intent.putExtra("USER_NAME", userName);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                        Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                                    }
                                });

                            } else {
                                TextView errField = (TextView) findViewById(R.id.errField);
                                errField.setText("Invalid number of days.");
                            }

                        } else if (buySellFlag.equals("BUY")) {
                            RequestParams params = new RequestParams();
                            params.put("flag", "BUY");
                            params.put("bookName", bookname);
                            params.put("listingOwner", listingOwner);
                            params.put("buyerEmail", email);
                            params.put("listingOwnerID", listingOwnerID);
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post("http://engineapp-1084.appspot.com/postRentBuyData", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                    Log.w("async", "success!!!!");
                                    Toast.makeText(context, "Transaction Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent= new Intent(context, UserProfile.class);
                                    intent.putExtra("EMAIL_PARAM", email);
                                    intent.putExtra("USER_NAME", userName);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                                }
                            });

                        } else if (buySellFlag.equals("RET")) {
                            if(validInput) {
                                EditText edUserRating = (EditText) findViewById(R.id.userRating);
                                String userRating = edUserRating.getText().toString();
                                RequestParams params = new RequestParams();
                                params.put("flag", "RET");
                                params.put("bookName", bookname);
                                params.put("listingOwner", listingOwner);
                                params.put("buyerEmail", email);
                                params.put("listingOwnerID", listingOwnerID);
                                params.put("userRating", userRating);
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.post("http://engineapp-1084.appspot.com/postRentBuyData", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                        Log.w("async", "success!!!!");
                                        Toast.makeText(context, "Transaction Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent= new Intent(context, UserProfile.class);
                                        intent.putExtra("EMAIL_PARAM", email);
                                        intent.putExtra("USER_NAME", userName);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                        Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                                    }
                                });
                            }
                            else{
                                TextView errField = (TextView) findViewById(R.id.errField);
                                errField.setText("Invalid Rating");
                            }

                        }
                    }
                }
        );

        final EditText numDays = (EditText) findViewById(R.id.numDays);
        numDays.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                validInput = false;

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String days = numDays.getText().toString();
                TextView errField = (TextView) findViewById(R.id.errField);
                errField.setText("");
                try {
                    int checkDays = Integer.parseInt(days);
                    finalCost = Float.parseFloat(cost) * checkDays;
                    String fc = Float.toString(finalCost);
                    TextView calcCost = (TextView) findViewById(R.id.calcCost);
                    calcCost.setText("Total cost will be:" + fc);
                    validInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number");
                    TextView calcCost = (TextView) findViewById(R.id.calcCost);
                    calcCost.setText("Invalid number of days");
                    validInput = false;
                }
            }

        });

        final EditText userRating = (EditText) findViewById(R.id.userRating);
        userRating.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                validInput = false;

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Rating = userRating.getText().toString();
                TextView errField = (TextView) findViewById(R.id.calcCost);
                errField.setText("");
                try {
                    float checkRating = Float.parseFloat(Rating);
                    if(checkRating < 0 || checkRating > 5) {
                        validInput = false;
                        errField.setText("Invalid Rating");
                    }
                    else{
                        validInput = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number");
                    errField.setText("Invalid Rating");
                    validInput = false;
                }
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
                return false;
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
    @Override
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra("EMAIL_PARAM", email);
            intent.putExtra("USER_NAME",userName);
        }

        super.startActivity(intent);
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
