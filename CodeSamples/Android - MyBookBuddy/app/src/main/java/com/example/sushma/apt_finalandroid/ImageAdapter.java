package com.example.sushma.apt_finalandroid;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> imageURLs;
    private ArrayList<String> imageCaps;
    private ArrayList<String> imageAuthor;
    private ArrayList<String> imageStatus;
    private ArrayList<String> imageCost;


    public ImageAdapter(Context c, ArrayList<String> imageURLs, ArrayList<String> imageCaps,
                        ArrayList<String> imageAuthor, ArrayList<String> imageStatus, ArrayList<String> imageCost) {
        mContext = c;
        this.imageURLs = imageURLs;
        this.imageCaps = imageCaps;
        this.imageAuthor = imageAuthor;
        this.imageStatus = imageStatus;
        this.imageCost = imageCost;
    }

    public int getCount() {
        return imageCaps.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            grid = inflater.inflate(R.layout.grid_xml, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            TextView textView1 = (TextView) grid.findViewById(R.id.grid_author);
            TextView textView2 = (TextView) grid.findViewById(R.id.grid_status);
            TextView textView3 = (TextView) grid.findViewById(R.id.grid_cost);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            Picasso.with(mContext).load(imageURLs.get(position)).into(imageView);
            textView.setText(imageCaps.get(position));
            textView1.setText(imageAuthor.get(position));
            textView2.setText("Status :"+imageStatus.get(position));
            textView3.setText("Cost :"+imageCost.get(position)+"$");

        } else {
            grid = (View) convertView;
        }
        return grid;
    }

}

