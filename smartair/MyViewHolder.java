package com.example.smartair;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView textViewName, textViewModelNo;

    public MyViewHolder (@NonNull View itemView){
        super(itemView);

        textViewName = itemView.findViewById(R.id.textviewDeviceName);
        textViewModelNo = itemView.findViewById(R.id.textviewModelNo);
    }
}
