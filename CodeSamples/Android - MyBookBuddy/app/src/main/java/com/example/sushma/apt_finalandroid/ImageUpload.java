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
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.plus.Plus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ImageUpload extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    //private GoogleApiClient mGoogleApiClient;
    //public Location mLastLocation;
    //public double lati;
    //public double longi;
    private GoogleApiClient mGoogleApiClient;
    static final String EMAIL_PARAM = "package com.example.sushma.apt_finalandroid.EMAIL";
    private static final int PICK_IMAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Context context = this;
    public String book_name;
    public String author_name;
    public String category_name;
    public String pickup_address;
    public String book_cost;
    public String request_flag;
    public RadioGroup radioRequestGroup;
    public RadioButton radioRequestButton;
    public Button button;
    public Bitmap bitmapimg;
    public String email;
    public String userName;
    final ArrayList<String> SUGGESTIONS = new ArrayList<String>();
    public SimpleCursorAdapter mAdapter;
    private String TAG  = "Create Listing";

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
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL_PARAM");
        userName = intent.getStringExtra("USER_NAME");
        setContentView(R.layout.activity_image_upload);

        radioRequestGroup = (RadioGroup) findViewById(R.id.radioRequest);

        button = (Button)findViewById(R.id.createListingButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView errField = (TextView) findViewById(R.id.errField);
                errField.setText("");
                byte[] b;
                if (bitmapimg != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmapimg.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    b = baos.toByteArray();
                } else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    b = baos.toByteArray();
                }
                TextView searchInput = (TextView) findViewById(R.id.bookName);
                book_name = searchInput.getText().toString().trim();
                TextView searchInput1 = (TextView) findViewById(R.id.authorName);
                author_name = searchInput1.getText().toString();
                TextView searchInput2 = (TextView) findViewById(R.id.categoryName);
                category_name = searchInput2.getText().toString().trim();
                TextView searchInput3 = (TextView) findViewById(R.id.pickupLocation);
                pickup_address = searchInput3.getText().toString();
                TextView searchInput4 = (TextView) findViewById(R.id.costprice);
                book_cost = searchInput4.getText().toString();


                int selectedId = radioRequestGroup.getCheckedRadioButtonId();
                System.out.println(selectedId);
                radioRequestButton = (RadioButton) findViewById(selectedId);
                request_flag = (String) radioRequestButton.getText();
                if(book_name.isEmpty() || author_name.isEmpty() || category_name.isEmpty() || pickup_address.isEmpty() || book_cost.isEmpty()){
                    //TextView errField = (TextView) findViewById(R.id.errField);
                    errField.setText("Some fields are missing!");
                }
                else {
                    getUploadURL(b, book_name, author_name, category_name, pickup_address, book_cost, request_flag);
                }

            }
        });


//        Button chooseFromLibraryButton = (Button) findViewById(R.id.choose_from_library);
//        chooseFromLibraryButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        // To do this, go to AndroidManifest.xml to add permission
//                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        // Start the Intent
//                        startActivityForResult(galleryIntent, PICK_IMAGE);
//                    }
//                }
//        );
//
//        Button chooseFromCameraButton = (Button) findViewById(R.id.camera_button);
//        chooseFromCameraButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, CameraApp.class);
//                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//
//                    }
//                }
//        );
        ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);
        thumbnail.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CharSequence picOptions[] = new CharSequence[]{"Choose from Gallery", "Launch Camera"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Upload options");
                        builder.setItems(picOptions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (picOptions[which] == "Choose from Gallery") {
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(galleryIntent, PICK_IMAGE);
                                } else {
                                    Intent cameraIntent = new Intent(context, CameraApp.class);
                                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        });
                        builder.show();

                    }
                }
        );
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
    public void startActivity(Intent intent) {
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra("EMAIL_PARAM", email);
            intent.putExtra("USER_NAME",userName);
        }

        super.startActivity(intent);
    }

    public void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "bookName" });
        for (int i=0; i<SUGGESTIONS.size(); i++) {
            if (SUGGESTIONS.get(i).toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, SUGGESTIONS.get(i)});
        }
        mAdapter.changeCursor(c);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("result code= " + resultCode);

        if(resultCode == RESULT_OK){

            String result=data.getStringExtra("result");
            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);

            bitmapimg = BitmapFactory.decodeFile(result);
            imgView.setImageBitmap(bitmapimg);

        }
        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();

            // User had pick an image.

            String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            // Link to the image

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imageFilePath = cursor.getString(columnIndex);
            cursor.close();

            // Bitmap imaged created and show thumbnail

            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);
            bitmapimg = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapimg);

        }
    }

    private void getUploadURL(final byte[] encodedImage, final String book_name, final String author_name, final String category_name, final String pickup_address, final String book_cost, final String request_flag) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String request_url = "http://engineapp-1084.appspot.com/getUploadURL";
        System.out.println(request_url);
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            String upload_url;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    upload_url = jObject.getString("upload_url");
                    postToServer(encodedImage, book_name, author_name, category_name, pickup_address, book_cost, request_flag, upload_url);

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Get_serving_url", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }

    private void postToServer(byte[] encodedImage, String book_name, String author_name, String category_name, String pickup_address, String book_cost, String request_flag, String upload_url) {
        System.out.println(upload_url);
        RequestParams params = new RequestParams();
        params.put("file", new ByteArrayInputStream(encodedImage));
        params.put("book_name", book_name);
        params.put("author_name", author_name);
        params.put("category_name", category_name);
        params.put("pickup_address", pickup_address);
        params.put("book_cost", book_cost);
        params.put("request_flag", request_flag);
        params.put("userID", email);
        params.put("userName", userName);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Creation Successful", Toast.LENGTH_SHORT).show();
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

}
