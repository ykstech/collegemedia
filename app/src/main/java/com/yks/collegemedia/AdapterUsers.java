package com.yks.collegemedia;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {



    Context context;

    private  Context cxm;




    public AdapterUsers(Context context, List<DataModal> list) {

        this.context = context;

        this.list = list;

    }



    List<DataModal> list;



    @NonNull

    @Override

    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);

        return new MyHolder(view);

    }



    @Override

    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {



        String username = list.get(position).getName();

        String username2 = list.get(position).getName();

        holder.name.setText(username);

        holder.name2.setText(username2.substring(0,1).toUpperCase());

        String branch=list.get(position).getBranch();
        String year=list.get(position).getYear();
        String branchtxt="";
        String yeartxt="";
        switch (branch){
            case "0":branchtxt="CSE";break;
            case "1":branchtxt="IT";break;
            case "2":branchtxt="EXTC";break;
            case "3":branchtxt="ELPO";break;
            case "4":branchtxt="MECH";break;
        }
        switch (year){
            case "0":yeartxt="First year :";break;
            case "1":yeartxt="Second year :";break;
            case "2":yeartxt="Third year :";break;
            case  "3":yeartxt="Fourth year :";break;
        }

        holder.subyear.setText(yeartxt);
        holder.subbranch.setText(branchtxt);
        if(list.get(position).getverified().equals("null")){
            holder.verified.setVisibility(View.GONE);
        }else {
            holder.verified.setVisibility(View.VISIBLE);
        }





//        holder.name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent i = new Intent(context, profileActivity.class);
////                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                i.putExtra("key", list.get(position).getNumber());
//                context.startActivity(i);
//
//            }
//        });
//

    }



    @Override

    public int getItemCount() {

        return list.size();

    }



    class MyHolder extends RecyclerView.ViewHolder {




        TextView name, name2,subbranch,subyear;
        ImageView verified;

    //    Context context;


        public MyHolder(@NonNull View itemView) {

            super(itemView);

            cxm=itemView.getContext();

            name = itemView.findViewById(R.id.namep);

            name2 = itemView.findViewById(R.id.namep2);

            subbranch = itemView.findViewById(R.id.subbranch);
            subyear = itemView.findViewById(R.id.subyear);

            verified = itemView.findViewById(R.id.verified);

            context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int itemPosition =   getLayoutPosition();
                  //  Toast.makeText(context, "" + itemPosition, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,profileActivity.class);
                    intent.putExtra("key", list.get(itemPosition).getNumber());

                    intent.putExtra("key2", "search");
                    context.startActivity(intent);

                }
            });
        }


    }
}
