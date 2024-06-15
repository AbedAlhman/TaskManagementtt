package com.example.taskmanagement.pages;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.taskmanagement.FireeBase.FirebaseServices;
import com.example.taskmanagement.FireeBase.Note;
import com.example.taskmanagement.MainActivity;
import com.example.taskmanagement.R;
import com.example.taskmanagement.Utilites.Utilss;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNoteFragment extends Fragment {

    private EditText titleInput, descriptionInput;
    private Button saveBtn;

    Spinner spnImp,spntask;
    ImageView img;
    boolean isimage=false;
    private static final int GALLERY_REQUEST_CODE = 123;

    private FirebaseServices fbs;
    private Utilss utils;


    String[] Importance = {"Very Important", "Important", "Not Important"};


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNoteFragment newInstance(String param1, String param2) {
        AddNoteFragment fragment = new AddNoteFragment();
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
        return inflater.inflate(R.layout.fragment_add_note,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        // ---->    פרטי הוספת רכב    <----
        //editText
        fbs = FirebaseServices.getInstance();
        utils = Utilss.getInstance();
        titleInput = getView().findViewById(R.id.etTitleInput);
        descriptionInput = getView().findViewById(R.id.etDescriptionInput);
        saveBtn = getView().findViewById(R.id.savebtn);
        img = getView().findViewById(R.id.ivNoteAddCarFragment);
        spntask=getView().findViewById(R.id.spntask);

        //spinner for the color of car
        spnImp = getView().findViewById(R.id.spnImp);

        spnImp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                   Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        saveBtn.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){
        // adding to firestore  'car' collection

        addToFirestore();
    }
    });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        ((MainActivity)getActivity()).pushFragment(new AddNoteFragment());
    }

    private void addToFirestore() {

        String title,description;
        String importance,istask;
    //get data from screen

        title= titleInput.getText().toString();
        description = descriptionInput.getText().toString();
        importance = spnImp.getSelectedItem().toString();
        istask=spntask.getSelectedItem().toString();
        if (title.trim().isEmpty() || description.trim().isEmpty() || importance.trim().isEmpty()
                ) {
            Toast.makeText(getActivity(), "sorry some data missing incorrect !", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note;
        if (!isimage) {
            note = new Note(title, description,importance, " ",istask.equals("Task"));
        } else {
            note = new Note(title,description,importance, UploadImageToFirebase(),istask.equals("Task"));

        }

        fbs.getFire().collection("Note").add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "ADD Note is Succesed ", Toast.LENGTH_SHORT).show();
                        Log.e("addToFirestore() - add to collection: ", "Successful!");
                        Useradd( documentReference.getPath());
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Home());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("addToFirestore() - add to collection: ", e.getMessage());
                    }
                });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            img.setImageURI(selectedImageUri);
            utils.uploadImage(getActivity(), selectedImageUri);
            isimage=true;
        }
    }
    public void toBigImg(View view) {
    }
    private String UploadImageToFirebase(){
        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
        Bitmap Image = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Image.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[]data= baos.toByteArray();
        StorageReference ref =fbs.getStorage().getReference("Notepicture/"+ UUID.randomUUID().toString());
        UploadTask uploadTask =ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error with the picture", e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
        return ref.getPath();
    }
    private void Useradd(String notepath) {
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
                        notes.add(notepath);
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

}

