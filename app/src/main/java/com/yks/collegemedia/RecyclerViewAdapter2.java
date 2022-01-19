package com.yks.collegemedia;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.method.LinkMovementMethod;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by AndroidJSon.com on 6/18/2017.
 */

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;

    String myuid;


    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;
    int notificationId = 3;
    String CHANNEL_ID2="2";


    private DatabaseReference likeref, postref;

    boolean mprocesslike = false;


    public RecyclerViewAdapter2(Context context, List<ImageUploadInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        likeref = FirebaseDatabase.getInstance().getReference("likes");

        postref = FirebaseDatabase.getInstance().getReference("newdata");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);

    //    holder.imageNameTextView.setText(UploadInfo.getImageName());
        String s= UploadInfo.getprofilename();
        holder.profilepictext.setText(s.substring(0,1).toUpperCase());
        holder.profilename.setText(UploadInfo.getprofilename());
        holder.profilename2.setText("@"+UploadInfo.getprofilename()+" : "+UploadInfo.getImageName());
        holder.date.setText(UploadInfo.getpostdate());
        String postid2 = UploadInfo.getimageid();
        String postnumber=UploadInfo.getphonenumber();

        setLikes(holder, postid2);
        holder.likecounter.setText(UploadInfo.getlikes() + " likes");

//        if(MainImageUploadInfoList.isEmpty()){
//            back(UploadInfo.getphonenumber());
//        }



        if(UploadInfo.getImageURL().equals("null")){

            holder.imageView.setVisibility(View.GONE);
        }else {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(UploadInfo.getImageURL()).into(holder.imageView);
        }


        //  Toast.makeText(context, "s "+MainImageUploadInfoList.size(), Toast.LENGTH_SHORT).show();

        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    //      Toast.makeText(holder.itemView.getContext(), "onDoubleTap", Toast.LENGTH_SHORT).show();

                    updatelike(UploadInfo);

                    return super.onDoubleTap(e);
                }
                @Override
                public boolean onSingleTapConfirmed(MotionEvent event) {
                    //       Log.d("onSingleTapConfirmed", "onSingleTap");
                    return false;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        holder.likebutton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                updatelike(UploadInfo);

            }

        });

        holder.morebtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {



                showMoreOptions(holder.morebtn, postnumber, myuid, postid2,UploadInfo.getImageURL());

            }

        });



    }


    private void showMoreOptions(ImageButton more, String uid, String myuid, final String postid,final String onlytxt) {

        PopupMenu popupMenu = new PopupMenu(context, more, Gravity.END);

        if (uid.equals(myuid)) {
             popupMenu.getMenu().add(Menu.NONE, 0, 0, "DELETE");
           }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override

            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == 0) {

                    if(onlytxt.equals("null")){
                        deletetxtpost(postid,uid);
                    }else {
                        deltewithImage(postid,uid);
                    }

                }


                return false;

            }

        });

        popupMenu.show();

    }


    private  void  deletetxtpost(final String postid,final String uid){
        createnotification();

        final ProgressDialog pd = new ProgressDialog(context);

        pd.setMessage("Deleting");
        pd.show();

        Query query = FirebaseDatabase.getInstance().getReference("newdata").child(postid);
        Query query2 = FirebaseDatabase.getInstance().getReference("likes").child(postid);




        query.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {






                dataSnapshot.getRef().removeValue();

                RecyclerViewAdapter2.this.notifyDataSetChanged();

                pd.dismiss();

                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                builder.setContentText("Post Deleted Successfully").setProgress(0,0,false);
                notificationManager.notify(notificationId, builder.build());

                if(MainImageUploadInfoList.size()<=1){
                    back(uid);
                }



            }

            @Override

            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(context, "Deleting Cancelled", Toast.LENGTH_SHORT).show();
                builder.setContentText("Post Deleting Cancelled").setProgress(0,0,false);
                notificationManager.notify(notificationId, builder.build());


            }

        });
        query2.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().removeValue();


            }

            @Override

            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(context, "Deleting Cancelled", Toast.LENGTH_SHORT).show();

            }

        });


    }

    private void deltewithImage(final String postid,String uid) {
        createnotification();

        final ProgressDialog pd = new ProgressDialog(context);

        pd.setMessage("Deleting");
        pd.show();

        StorageReference picref = FirebaseStorage.getInstance().getReference("newdata").child(postid);

        picref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override

            public void onSuccess(Void aVoid) {

                Query query = FirebaseDatabase.getInstance().getReference("newdata").child(postid);
                Query query2 = FirebaseDatabase.getInstance().getReference("likes").child(postid);

//                Query query3 = FirebaseDatabase.getInstance().getReference("newdata");



                query.addValueEventListener(new ValueEventListener() {

                    @Override

                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




//                        if (dataSnapshot.getValue()==null) {
//                            back(uid);
//                        }else {


                        dataSnapshot.getRef().removeValue();

                        RecyclerViewAdapter2.this.notifyDataSetChanged();
                        pd.dismiss();

                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                        builder.setContentText("Post Deleted Successfully").setProgress(0,0,false);
                        notificationManager.notify(notificationId, builder.build());


//                        Toast.makeText(context, "s2 "+
//                                MainImageUploadInfoList.size(), Toast.LENGTH_SHORT).show();

                        if(MainImageUploadInfoList.size()<=1){
                            back(uid);
                        }


                        //      }
                    }

                    @Override

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        builder.setContentText("Post Deleting Cancelled").setProgress(0,0,false);
                        notificationManager.notify(notificationId, builder.build());


                    }

                });
                query2.addValueEventListener(new ValueEventListener() {

                    @Override

                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        dataSnapshot.getRef().removeValue();

                        //   pd.dismiss();

                        //   Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();


                    }

                    @Override

                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }

                });


                //   notifyDataSetChanged();


            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override

            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Post Deleting Failed", Toast.LENGTH_SHORT).show();

                builder.setContentText("Post Deleting Failed").setProgress(0,0,false);
                notificationManager.notify(notificationId, builder.build());


            }

        });

    }






    private void  updatelike(ImageUploadInfo UploadInfo){
        final int plike = Integer.parseInt(UploadInfo.getlikes());

        mprocesslike = true;

        String postid = UploadInfo.getimageid();

        DatabaseReference ref=  postref.child(postid).child("likes");
        DatabaseReference ref2 = likeref.child(postid);

        ref2.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mprocesslike) {

                    if (dataSnapshot.hasChild(myuid)) {

                        ref.setValue("" + (plike - 1));

                        ref2.child(myuid).removeValue();


                        mprocesslike = false;

                    } else {

                        ref.setValue("" + (plike + 1));

                        ref2.child(myuid).setValue("liked");


                        mprocesslike = false;

                    }

                }

            }


            @Override

            public void onCancelled(@NonNull DatabaseError databaseError) {


            }

        });

    }

    private void setLikes(final ViewHolder holder, final String postid) {
        DatabaseReference ref2 = likeref.child(postid);

        ref2.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(myuid)) {

                    holder.likebutton.setImageResource(R.drawable.liked);


                } else {

                    holder.likebutton.setImageResource(R.drawable.like);


                }

            }


            @Override

            public void onCancelled(@NonNull DatabaseError databaseError) {


            }

        });

    }


    public  void  createnotification(){

        notificationManager = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context,CHANNEL_ID2 );
        builder.setContentTitle("Post Deleting")
                .setContentText("Deleting in progress")
                .setSmallIcon(R.drawable.ic_baseline_school_24)
                .setPriority(NotificationCompat.PRIORITY_MAX);

// Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(notificationId, builder.build());

    }


    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    private  void back(String position){

        Intent intent = new Intent(context,profileActivity.class);
        intent.putExtra("key", position);
        intent.putExtra("key2", "display");

        context.startActivity(intent);

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView imageNameTextView;
        public  TextView profilename,profilename2,likecounter,date;
        public TextView profilepictext;
        public  ImageButton likebutton;
        public ImageButton morebtn;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
            profilename = (TextView) itemView.findViewById(R.id.profilename);
            profilename2 = (TextView) itemView.findViewById(R.id.profilename2);

            profilepictext = (TextView) itemView.findViewById(R.id.profilepictext);
            likecounter = (TextView) itemView.findViewById(R.id.likecounter);
            likebutton =  itemView.findViewById(R.id.likebutton);
            date =  itemView.findViewById(R.id.date);
        //    imageNameTextView.setMovementMethod(LinkMovementMethod.getInstance());

            morebtn =  itemView.findViewById(R.id.morebtn);

            context = itemView.getContext();

            profilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int itemPosition =   getLayoutPosition();
                    //  Toast.makeText(context, "" + itemPosition, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,profileActivity.class);
                    intent.putExtra("key", MainImageUploadInfoList.get(itemPosition).getphonenumber());
                    intent.putExtra("key2", "display");

                    context.startActivity(intent);

                }
            });





        }
    }
}
