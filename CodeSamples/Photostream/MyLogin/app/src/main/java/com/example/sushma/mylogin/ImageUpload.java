package com.example.sushma.mylogin;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class ImageUpload extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener{
  private GoogleApiClient mGoogleApiClient;
  public Location mLastLocation;
  public double lati;
  public double longi;
  private static final int PICK_IMAGE = 1;
  static final int REQUEST_IMAGE_CAPTURE = 1;
  Context context = this;
  String stream_name;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_upload);
    Intent intent = getIntent();
    stream_name = intent.getStringExtra(ViewActivity.EXTRA_MESSAGE2);

    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    mGoogleApiClient.connect();

    // Choose image from library
    Button chooseFromLibraryButton = (Button) findViewById(R.id.choose_from_library);
    chooseFromLibraryButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                // To do this, go to AndroidManifest.xml to add permission
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_IMAGE);
              }
            }
    );

    Button chooseFromCameraButton = (Button) findViewById(R.id.camera_button);
    chooseFromCameraButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent intent = new Intent(context, CameraApp.class);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

              }
            }
    );
  }

              @Override
              public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.image_upload, menu);
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


              protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                if(resultCode == RESULT_OK){

                  String result=data.getStringExtra("result");
                  ImageView imgView = (ImageView) findViewById(R.id.thumbnail);

                  final Bitmap bitmapimg1 = BitmapFactory.decodeFile(result);
                  imgView.setImageBitmap(bitmapimg1);

                  // Enable the upload button once image has been uploaded

                  Button uploadButton = (Button) findViewById(R.id.upload_to_server);
                  uploadButton.setClickable(true);

                  uploadButton.setOnClickListener(
                          new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                              // Get photo caption

                              EditText text = (EditText) findViewById(R.id.upload_message);
                              String photoCaption = text.getText().toString();
                              String LATITUDE = String.valueOf(lati);
                              String LONGITUDE = String.valueOf(longi);

                              ByteArrayOutputStream baos = new ByteArrayOutputStream();
                              bitmapimg1.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                              byte[] b = baos.toByteArray();
                              byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);
                              String encodedImageStr = encodedImage.toString();

                              getUploadURL(b, photoCaption, stream_name, LATITUDE, LONGITUDE);
                            }
                          }
                  );

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
                  final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath);
                  imgView.setImageBitmap(bitmapImage);

                  // Enable the upload button once image has been uploaded

                  Button uploadButton = (Button) findViewById(R.id.upload_to_server);
                  uploadButton.setClickable(true);

                  uploadButton.setOnClickListener(
                          new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                              // Get photo caption

                              EditText text = (EditText) findViewById(R.id.upload_message);
                              String photoCaption = text.getText().toString();
                              String LATITUDE = String.valueOf(lati);
                              String LONGITUDE = String.valueOf(longi);

                              ByteArrayOutputStream baos = new ByteArrayOutputStream();
                              bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                              byte[] b = baos.toByteArray();
                              byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);
                              String encodedImageStr = encodedImage.toString();

                              getUploadURL(b, photoCaption, stream_name, LATITUDE, LONGITUDE);
                            }
                          }
                  );
                }
              }

              private void getUploadURL(final byte[] encodedImage, final String photoCaption, final String stream_name, final String LATITUDE, final String LONGITUDE) {
                AsyncHttpClient httpClient = new AsyncHttpClient();
                String request_url = "http://miniproject1-1084.appspot.com/getUploadURL";
                System.out.println(request_url);
                //mGoogleApiClient.connect();
                System.out.println(String.valueOf(lati) + " " + String.valueOf(longi));
                httpClient.get(request_url, new AsyncHttpResponseHandler() {
                  String upload_url;

                  @Override
                  public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                    try {
                      JSONObject jObject = new JSONObject(new String(response));

                      upload_url = jObject.getString("upload_url");
                      postToServer(encodedImage, photoCaption, stream_name, LATITUDE, LONGITUDE, upload_url);

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

              private void postToServer(byte[] encodedImage, String photoCaption, String stream_name, String LATITUDE, String LONGITUDE, String upload_url) {
                System.out.println(upload_url);
                RequestParams params = new RequestParams();
                params.put("file", new ByteArrayInputStream(encodedImage));
                params.put("photoCaption", photoCaption);
                params.put("streamName", stream_name);
                params.put("LATITUDE", LATITUDE);
                params.put("LONGITUDE", LONGITUDE);
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(upload_url, params, new AsyncHttpResponseHandler() {
                  @Override
                  public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    Log.w("async", "success!!!!");
                    Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                  }
                });
              }

              public void viewAllImages(View view) {
                Intent intent = new Intent(this, DisplayImages.class);

                startActivity(intent);
              }

  @Override
  public void onConnected(Bundle bundle) {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient);
    if (mLastLocation != null) {
      lati = mLastLocation.getLatitude();
      longi = mLastLocation.getLongitude();
      System.out.println("latitude: " + String.valueOf(lati));
      System.out.println("longitude: " + String.valueOf(longi));
      //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
      //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
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
