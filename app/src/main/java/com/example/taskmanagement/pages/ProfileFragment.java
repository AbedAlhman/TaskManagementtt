package com.example.taskmanagement.pages;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taskmanagement.FireeBase.FirebaseServices;
import com.example.taskmanagement.FireeBase.User;
import com.example.taskmanagement.MainActivity;
import com.example.taskmanagement.R;
import com.example.taskmanagement.Utilites.Utilss;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView email,FName,LName,UName;

    private Button SignOut;

    ImageView Profile;

    private FirebaseServices fbs;

    private Utilss utils;

    private String imageS;
    User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void onStart(){
        super.onStart();
        init();

    }
    private void init(){
        fbs=FirebaseServices.getInstance();
        email=getView().findViewById(R.id.tvemail);
        FName=getView().findViewById(R.id.tvFname);
        LName=getView().findViewById(R.id.tvLname);
        UName=getView().findViewById(R.id.tvUname);
        Profile=getView().findViewById(R.id.ivPimage);
        SignOut=getView().findViewById(R.id.Signoutbtn);
        utils=Utilss.getInstance();
        Getuser();

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbs.getAuth().signOut();
                goToLogin();
            }
        });


    }




    private void Getuser() {
        fbs.getFire().collection("Users").whereEqualTo("email", fbs.getAuth().getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener((QuerySnapshot querySnapshot) -> {
                    if (querySnapshot.isEmpty()) {
                        System.out.println("No users found.");
                        return;
                    }

                    System.out.println("Number of users: " + querySnapshot.size());

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userId = doc.getId();
                        user=doc.toObject(User.class);
                    }
                    Connect();
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error retrieving users: " + e.getMessage());
                });
    }

    private void Connect() {
        email.setText("Email:"+user.getEmail());
        FName.setText("FirstName:"+user.getFirstName());
        LName.setText("LastName:"+user.getLastName());
        UName.setText("UserName:"+user.getUsername());
        StorageReference storageRef= fbs.getStorage().getInstance().getReference().child(user.getPhoto());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity())
                        .load(uri)
                        .into(Profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur when downloading the image
            }

        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    private void goToLogin() {
        ((MainActivity) getActivity()).getBottomNavigationView().setVisibility(View.GONE);

        FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new LoginFragment());
        ft.commit();
    }

}