package com.yks.collegemedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class searchActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    AdapterUsers adapterUsers;

    List<DataModal> usersList;
    EditText search;

    AppCompatButton allbranch,cse,it,extc,elpo,mech;
    AppCompatButton allyear,first,second,third,fourth;
    String searchb="-1";
    String searchy="-2";
    ProgressBar progressBar;
    RelativeLayout searchrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recyclep);
        search = findViewById(R.id.search);

        progressBar = findViewById(R.id.searchprogress);
        searchrl = findViewById(R.id.searchrl);


        allbranch = findViewById(R.id.allbranch);
        cse = findViewById(R.id.cse);
        it = findViewById(R.id.it);
        extc= findViewById(R.id.extc);
        elpo= findViewById(R.id.elpo);
        mech= findViewById(R.id.mech);

 allyear= findViewById(R.id.allyear);
 first = findViewById(R.id.first);
 second= findViewById(R.id.second);
 third= findViewById(R.id.third);
 fourth= findViewById(R.id.fourth);


        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersList = new ArrayList<>();



        getAllUsers(searchb,searchy);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count>0) {

                    searchusers(String.valueOf(s),searchb,searchy);

                } else {

                    getAllUsers(searchb,searchy);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }


    @SuppressLint("NonConstantResourceId")
    public void buttonClicked(View view) {


        switch (view.getId()){
            case R.id.allbranch :searchb="-1";  deactivatedall(allbranch,cse,it,extc,elpo,mech);  break;
            case R.id.cse :     searchb="0";     deactivatedall(cse,allbranch,it,extc,elpo,mech);break;
            case R.id.it :      searchb="1";    deactivatedall(it,cse,allbranch,extc,elpo,mech);break;
            case R.id.extc :    searchb="2";   deactivatedall(extc,cse,it,allbranch,elpo,mech);break;
            case R.id.elpo :    searchb="3";   deactivatedall(elpo,cse,it,extc,allbranch,mech);break;
            case R.id.mech :    searchb="4";   deactivatedall(mech,cse,it,extc,elpo,allbranch);break;

            case R.id.allyear :    searchy="-2";        deactivatedallyr(allyear,first,second,third,fourth);break;
            case R.id.first  :     searchy="0";        deactivatedallyr(first,allyear,second,third,fourth);break;
            case R.id.second  :  searchy="1";          deactivatedallyr(second,first,allyear,third,fourth);break;
            case R.id.third  :   searchy="2";          deactivatedallyr(third,first,second,allyear,fourth);break;
            case R.id.fourth  :  searchy="3";          deactivatedallyr(fourth,first,second,third,allyear);break;

        }

        if(search.getText().toString().isEmpty()){
          getAllUsers(searchb,searchy);
        }



    }

    private void deactivatedall(AppCompatButton except,AppCompatButton btn1,AppCompatButton btn2, AppCompatButton btn3,AppCompatButton btn4,AppCompatButton btn5){
       button(except,1);
        button(btn1,0);
        button(btn2,0);
        button(btn3,0);
        button(btn4,0);
        button(btn5,0);
 }
    private void deactivatedallyr(AppCompatButton except,AppCompatButton btn1,AppCompatButton btn2, AppCompatButton btn3,AppCompatButton btn4){
        button(except,1);
        button(btn1,0);
        button(btn2,0);
        button(btn3,0);
        button(btn4,0);
    }
    void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void getAllUsers(String b,String y) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    DataModal modelUsers = dataSnapshot1.getValue(DataModal.class);




                    if(b.equals("-1") && y.equals("-2")){

                            usersList.add(modelUsers);

                    }else if(b.equals("-1")){
                        if (modelUsers.getYear().equals(y) ) {

                            usersList.add(modelUsers);

                        }
                    }else if(y.equals("-2")){
                        if ( modelUsers.getBranch().equals(b) ) {

                            usersList.add(modelUsers);

                        }
                    }else {

                        if ( modelUsers.getBranch().equals(b) && modelUsers.getYear().equals(y)) {

                            usersList.add(modelUsers);

                        }

                    }



                }

                adapterUsers = new AdapterUsers(getApplicationContext(), usersList);

                recyclerView.setAdapter(adapterUsers);

                progressBar.setVisibility(View.GONE);
                searchrl.setVisibility(View.VISIBLE);



            }



            @Override

            public void onCancelled(@NonNull DatabaseError databaseError) {

             toast("ERROR");

            }

        });

    }



    private void searchusers(final String s,final String b,final String y) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    DataModal modelUsers = dataSnapshot1.getValue(DataModal.class);

                       if(b.equals("-1") && y.equals("-2")){
                           if(modelUsers.getName().toLowerCase().contains(s.toLowerCase())){

                               usersList.add(modelUsers);
                           }
                       }else if(b.equals("-1")){
                           if (modelUsers.getName().toLowerCase().contains(s.toLowerCase()) && modelUsers.getYear().equals(y) ) {

                               usersList.add(modelUsers);

                           }
                       }else if(y.equals("-2")){
                           if (modelUsers.getName().toLowerCase().contains(s.toLowerCase()) && modelUsers.getBranch().equals(b) ) {

                               usersList.add(modelUsers);

                           }
                       }else {

                           if (modelUsers.getName().toLowerCase().contains(s.toLowerCase()) && modelUsers.getBranch().equals(b) && modelUsers.getYear().equals(y)) {

                               usersList.add(modelUsers);

                           }

                       }

                }

                adapterUsers = new AdapterUsers(getApplicationContext(), usersList);

                adapterUsers.notifyDataSetChanged();

                recyclerView.setAdapter(adapterUsers);



            }



            @Override

            public void onCancelled(@NonNull DatabaseError databaseError) {

            toast("ERROR");

            }

        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(searchActivity.this, DisplayImagesActivity.class);
        startActivity(i);
        finish();

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private  void button(AppCompatButton button, int state) {
        if (state == 1) {
            button.setTextColor(Color.parseColor("#ffffff"));
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.searchfilter2));

        }else {

            button.setTextColor(Color.parseColor("#666666"));
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.searchfilter));
        }
    }


}