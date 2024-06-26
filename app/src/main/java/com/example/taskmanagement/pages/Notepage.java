package com.example.taskmanagement.pages;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.taskmanagement.FireeBase.FirebaseServices;
import com.example.taskmanagement.FireeBase.Note;
import com.example.taskmanagement.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Notepage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notepage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String path;
    FirebaseServices fbs;

    Note note;
    EditText noteval;
    TextView delete,change;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Notepage() {
        // Required empty public constructor
    }
    public Notepage(String path, Note note){
        this.path=path;
       this.note=note;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Notepage.
     */
    // TODO: Rename and change types and number of parameters
    public static Notepage newInstance(String param1, String param2) {
        Notepage fragment = new Notepage();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notepage, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        fbs=FirebaseServices.getInstance();
        delete=getView().findViewById(R.id.Deletnote);
        change=getView().findViewById(R.id.Changenote);
        noteval=getView().findViewById(R.id.noteinfo);
        noteval.setText(note.getDescription());
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference();

                // Reference to the specific path
                DatabaseReference ref = mDatabase.child(path);
                fbs.getFire().document(path).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
                String userEmail = fbs.getAuth().getCurrentUser().getEmail();
                fbs.getFire().collection("Users").whereEqualTo("email",userEmail).get()
                        .addOnSuccessListener((QuerySnapshot querySnapshot) -> {
                            if (querySnapshot.isEmpty()) {
                                System.out.println("No users found.");
                                return;
                            }
                            System.out.println("Number of users: " + querySnapshot.size());

                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                String userId = doc.getId();
                                ArrayList<String> notes = (ArrayList<String>) doc.get("notes");
                                notes.remove(path);
                                doc.getReference().update("notes", notes)
                                        .addOnSuccessListener(aVoid -> {
                                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                            ft.replace(R.id.frameLayout, new Home());
                                            ft.commit();
                                            System.out.println("ArrayList updated successfully.");
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Error updating ArrayList: " + e.getMessage());
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error retrieving users: " + e.getMessage());
                        });

            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference userRef = fbs.getFire().document(path);
                userRef.get()
                        .addOnSuccessListener((DocumentSnapshot documentSnapshot) -> {
                            if (documentSnapshot.exists()) {
                                note.setDescription(noteval.getText().toString());
                                userRef.set(note)
                                        .addOnSuccessListener(aVoid -> {
                                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                            ft.replace(R.id.frameLayout, new Home());
                                            ft.commit();
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Error updating ArrayList: " + e.getMessage());
                                        });
                            } else {
                                System.out.println("User document doesn't exist.");
                            }
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error retrieving user: " + e.getMessage());
                        });
            }
        });
    }
}