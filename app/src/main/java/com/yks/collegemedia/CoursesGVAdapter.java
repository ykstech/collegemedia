package com.yks.collegemedia;

import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CoursesGVAdapter extends ArrayAdapter<ImageUploadInfo> {

  //  int i=0;
    // constructor for our list view adapter.
    public CoursesGVAdapter(@NonNull Context context, ArrayList<ImageUploadInfo> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.image_gv_item, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        ImageUploadInfo dataModal = getItem(position);
       // i++;
       // toast("pos "+i);

        TextView subtext = listitemView.findViewById(R.id.subtext);

        ImageView courseIV = listitemView.findViewById(R.id.idIVimage);

       // subtext.setMovementMethod(new ScrollingMovementMethod());

        // initializing our UI components of list view item.
        if(!dataModal.getImageURL().equals("null")) {
            courseIV.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(dataModal.getImageURL()).into(courseIV);
            subtext.setVisibility(View.GONE);
        }else {
            courseIV.setVisibility(View.GONE);
            subtext.setVisibility(View.VISIBLE);

            subtext.setText(dataModal.getImageName());
        }


     //   Toast.makeText(getContext(), "pos:  " +dataModal.getImageURL() , Toast.LENGTH_SHORT).show();

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        //nameTV.setText(dataModal.getName());

        // in below line we are using Picasso to load image
        // from URL in our Image VIew.
      //  Picasso.get().load(dataModal.getImgUrl()).into(courseIV);


        // below line is use to add item
        // click listener for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
               // Toast.makeText(getContext(), "Item clicked is : " + dataModal.getImageURL(), Toast.LENGTH_SHORT).show();
                //  Toast.makeText(context, "" + itemPosition, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),userimages.class);
                intent.putExtra("key", dataModal.getphonenumber());
                intent.putExtra("key2", "display");



                getContext().startActivity(intent);


            }
        });
        return listitemView;
    }

    void toast(String s){
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

}
