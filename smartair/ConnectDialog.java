package com.example.smartair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConnectDialog extends AppCompatDialogFragment {
    private EditText deviceName, modelNo;
    private ConnectDialogListener listener;
    DatabaseReference deviceDbRef;
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Get database reference for Devices field
        deviceDbRef = FirebaseDatabase.getInstance().getReference().child("Devices");

        //Inflate Dialog Layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.device_select, null);
        builder.setView(view).setTitle(R.string.deviceDialogTitle).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = deviceName.getText().toString();
                String number = modelNo.getText().toString();
                listener.applyTexts(name, number);
                insertDevice(name, number);

            }
        });

        deviceName = view.findViewById(R.id.deviceName_connect);
        modelNo = view.findViewById(R.id.modelNumber_connect);
        return builder.create();
    }

    //Function to insert device name and model number to datababe.
    private void insertDevice(String dName, String dModelNo){
        String name = dName;
        String modelNo = dModelNo;

        Device device = new Device(name, modelNo);
        deviceDbRef.setValue(device);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ConnectDialogListener) context;
        } catch (ClassCastException e) {
          throw new ClassCastException(context.toString()+ getString(R.string.err_connectDialog));
        }
    }

    public interface ConnectDialogListener{
        void applyTexts(String name, String modelNo);
    }
}
