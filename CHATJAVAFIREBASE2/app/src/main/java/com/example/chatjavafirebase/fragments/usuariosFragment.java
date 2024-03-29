package com.example.chatjavafirebase.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatjavafirebase.R;
import com.example.chatjavafirebase.adapters.AdapterUsuarios;
import com.example.chatjavafirebase.Models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class usuariosFragment extends Fragment {



    public usuariosFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ProgressBar progressBar;
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        final View view=inflater.inflate(R.layout.fragment_usuarios, container, false);
        TextView tv_user=view.findViewById(R.id.tv_user);
        ImageView img_user=view.findViewById(R.id.img_user);
        progressBar=view.findViewById(R.id.progressbar);
        assert user != null;
        tv_user.setText(user.getDisplayName());
        Glide.with(this).load(user.getPhotoUrl()).into(img_user);

        final RecyclerView rv;
        final ArrayList<Users> usersArrayList;
        final AdapterUsuarios adapter;
        LinearLayoutManager mlayoutManager;

        mlayoutManager=new LinearLayoutManager(getContext());
        mlayoutManager.setReverseLayout(true);
        mlayoutManager.setStackFromEnd(true);
        rv=view.findViewById(R.id.rv);
        rv.setLayoutManager(mlayoutManager);

        usersArrayList=new ArrayList<>();
        adapter=new AdapterUsuarios(usersArrayList,getContext());
        rv.setAdapter(adapter);

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myref=database.getReference("Users");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    rv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    usersArrayList.removeAll(usersArrayList);
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Users user=snapshot.getValue(Users.class);
                        usersArrayList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "NO EXISTEN USUARIOS", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }
}