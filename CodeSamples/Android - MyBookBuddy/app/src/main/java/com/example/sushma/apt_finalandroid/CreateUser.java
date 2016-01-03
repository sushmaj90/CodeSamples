package com.example.sushma.apt_finalandroid;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
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


/**
 * Created by sushma on 11/19/15.
 */

public class CreateUser extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    Context context = this;
    private static final int PICK_IMAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String EMAIL_PARAM = "package com.example.sushma.apt_finalandroid.EMAIL";
    public String user_name;
    public String user_email;
    public String user_university;
    public Bitmap bitmapimg;
    String email;
    private String TAG  = "Create User";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL_PARAM");

        ImageView uploadProfilePic = (ImageView) findViewById(R.id.thumbnail);
        uploadProfilePic.setOnClickListener(
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
                                    // To do this, go to AndroidManifest.xml to add permission
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    // Start the Intent
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
        Button createProfile = (Button) findViewById(R.id.btnCreateProfile);

        createProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView errVer = (TextView) findViewById(R.id.errVer);
                        errVer.setText("");
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
                        EditText userName = (EditText) findViewById(R.id.profileUserName);
                        user_name = userName.getText().toString();
                        EditText userUniversity = (EditText) findViewById(R.id.profileUniversity);
                        user_university = userUniversity.getText().toString();
                        EditText userEmail = (EditText) findViewById(R.id.profileUniEmail);
                        user_email = userEmail.getText().toString();
                        if (user_name.isEmpty()) {
                            //TextView errVer = (TextView) findViewById(R.id.errVer);
                            errVer.setText("User Name cannot be empty!");
                            errVer.setVisibility(View.VISIBLE);
                        } else {
                            getUploadURL(b, user_name, user_email, user_university);
                        }
                    }
                });
    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
    private void getUploadURL(final byte[] encodedImage, final String userName, final String userEmail, final String userUniversity){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String request_url = "http://engineapp-1084.appspot.com/getURLCreateUser";
        System.out.println(request_url);
        //mGoogleApiClient.connect()
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            String upload_url;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    upload_url = jObject.getString("upload_url");
                    postToServer(encodedImage, userName, userEmail, userUniversity, upload_url);

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
    private void postToServer(byte[] encodedImage, final String userName, String userEmail, String userUniversity, String upload_url) {
        System.out.println(upload_url);
        RequestParams params = new RequestParams();
        params.put("file", new ByteArrayInputStream(encodedImage));
        params.put("userName", userName);
        params.put("userEmail", userEmail);
        params.put("userUniversity", userUniversity);
        params.put("userID", email);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Creation Successful", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(context, ImageUpload.class);
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


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Image upload connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println(TAG+"Image upload connection failed");
    }
}
