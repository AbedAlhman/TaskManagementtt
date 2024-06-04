package com.example.taskmanagement.Utilites;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taskmanagement.FireeBase.FirebaseServices;
import com.example.taskmanagement.FireeBase.Note;
import com.example.taskmanagement.Notepage;
import com.example.taskmanagement.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class Noteadapter extends RecyclerView.Adapter<Noteadapter.MyViewHolder> {


    FirebaseServices fbs;
    Context context;
    ArrayList<Note>noteArrayList;

    ArrayList<String> notepath;


    private OnItemClickListener itemClickListener;

    public Noteadapter(Context context, ArrayList<Note> noteArrayList,ArrayList<String> notepath) {
        this.context = context;
        this.noteArrayList = noteArrayList;
        this.notepath=notepath;
    }

    @NonNull
    @Override
    public  MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
       View v= LayoutInflater.from(context).inflate(R.layout.notes,parent,false);
       return  new MyViewHolder(v);
    }

   @Override
   public void onBindViewHolder(@NonNull MyViewHolder holder,int position){
        Note note= noteArrayList.get(position);
        fbs=FirebaseServices.getInstance();
        holder.note.setText(note.getTitle());
        holder.description.setText(note.getDescription());
        if(note.getImportance().equals("Very Important")) holder.color.setBackgroundResource(R.color.RED);
            else if (note.getImportance().equals("Important"))holder.color.setBackgroundResource(R.color.YELLOW);
            else if (note.getImportance().equals("Not Important")) holder.color.setBackgroundResource(R.color.white);

       StorageReference storageRef= fbs.getStorage().getInstance().getReference().child(note.getPhoto());
       if (!note.isTask())holder.checkbox.setVisibility(View.GONE);
       storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
           @Override
           public void onSuccess(Uri uri) {
               Glide.with(context)
                       .load(uri)
                       .into(holder.noteimage);
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               // Handle any errors that occur when downloading the image
           }

       });
       holder.color.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               AppCompatActivity activity = (AppCompatActivity) context;
               activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Notepage(notepath.get(position),noteArrayList.get(position))).addToBackStack(null).commit();
           }
       });
   }

    @Override
    public int getItemCount(){
        return noteArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView  note,description;
        ImageView noteimage,checkbox;
        Spinner spnn;
        LinearLayout color;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note=itemView.findViewById(R.id.notenamead);
            description=itemView.findViewById(R.id.notedesad);
            noteimage=itemView.findViewById(R.id.noteimage);
            color=itemView.findViewById(R.id.layoutad);
            spnn=itemView.findViewById(R.id.spnImp);
            checkbox=itemView.findViewById(R.id.checkbox);
        }

    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
}
