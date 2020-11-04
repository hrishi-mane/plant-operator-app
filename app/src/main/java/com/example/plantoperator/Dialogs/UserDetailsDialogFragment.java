package com.example.plantoperator.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.plantoperator.POJO.UserDetails;
import com.example.plantoperator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class UserDetailsDialogFragment extends DialogFragment {

    TextInputEditText name, phone, city, address;
    DialogToRecycler dialogToRecycler;
    
    String recycler_name = "", recycler_phone = "";

    Integer adapterPosition = null;
    String string_for_editText_city, string_for_editText_address;

    public UserDetailsDialogFragment() {
    }

    public UserDetailsDialogFragment(String recycler_name, String recycler_phone, String city, String address, Integer adapterPosition) {
        this.recycler_name = recycler_name;
        this.recycler_phone = recycler_phone;
        this.adapterPosition = adapterPosition;
        this.string_for_editText_city = city;
        this.string_for_editText_address = address;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.fragment_user_details_dialog, null);


        name = view.findViewById(R.id.editText_name);
        phone = view.findViewById(R.id.editText_number);
        city = view.findViewById(R.id.editText_city);
        address = view.findViewById(R.id.editText_address);

        if (!(recycler_name.isEmpty() && recycler_phone.isEmpty())) {
            name.setText(recycler_name);
            phone.setText(recycler_phone);
            city.setText(string_for_editText_city);
            address.setText(string_for_editText_address);
        }

        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String var_name = name.getText().toString();
                        String var_phone = phone.getText().toString();
                        String var_city = city.getText().toString();
                        String var_address = address.getText().toString();

                        UserDetails userDetails = new UserDetails(var_name, var_phone, var_city, var_address);

                        if (!(recycler_name.isEmpty() && recycler_phone.isEmpty())) {
                            updateUserToFireStore(userDetails);
                            dialogToRecycler.passEditData(userDetails, adapterPosition);


                        } else {
                            addUserToFirestore(userDetails);


                            dialogToRecycler.passData(userDetails);
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    private void sendOtpToUser(String phone, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserToFireStore(final UserDetails userDetails) {

        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("name", recycler_name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    updateDoc(document, userDetails);
                }
            }

        });
    }

    private void updateDoc(DocumentSnapshot document, UserDetails userDetails) {
        String document_id = document.getId();
        FirebaseFirestore.getInstance().collection("Users").document(document_id).set(userDetails);
    }

    void addUserToFirestore(UserDetails userDetails) {
        FirebaseFirestore.getInstance().collection("Users").add(userDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("ADD STATUS", "Data added");
                sendOtpToUser(phone.getText().toString(), "Your user Id is:");
                sendOtpToUser(phone.getText().toString(), documentReference.getId());
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dialogToRecycler = (DialogToRecycler) context;

    }

    public interface DialogToRecycler {
        void passData(UserDetails userDetails);

        void passEditData(UserDetails userDetails, Integer adapterPosition);
    }

}
