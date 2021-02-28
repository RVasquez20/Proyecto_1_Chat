package com.example.chatjavafirebase.adapters;

import android.content.Context;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatjavafirebase.R;
import com.example.chatjavafirebase.pojos.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.viewHolderAdapter> {

    List<Users> usersList;
    Context context;

    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database=FirebaseDatabase.getInstance();

    public AdapterUsuarios(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.row_usuarios,parent,false);
        viewHolderAdapter holder=new viewHolderAdapter(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolderAdapter holder, int position) {
        final Users userss=usersList.get(position);

        final Vibrator vibrator=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        Glide.with(context).load(userss.getFoto()).into(holder.img_user);
        holder.tv_usuario.setText(userss.getNombre());
        if (userss.getId().equals(user.getUid())){
            holder.cardView.setVisibility(View.GONE);
        }else{
            holder.cardView.setVisibility(View.VISIBLE);
        }

        DatabaseReference ref_mis_botones=database.getReference("Users").child(user.getUid()).child("Solicitudes").child(userss.getId());
        ref_mis_botones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String estado=dataSnapshot.child("estado").getValue(String.class);
                if (dataSnapshot.exists()) {
                    if (estado.equals("enviado")) {
                        holder.send.setVisibility(View.VISIBLE);
                        holder.add.setVisibility(View.GONE);
                        holder.amigos.setVisibility(View.GONE);
                        holder.tengosolicitud.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);

                    }

                    if (estado.equals("amigos")) {
                        holder.send.setVisibility(View.GONE);
                        holder.add.setVisibility(View.GONE);
                        holder.amigos.setVisibility(View.VISIBLE);
                        holder.tengosolicitud.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);

                    }

                    if (estado.equals("solicitud")) {
                        holder.send.setVisibility(View.GONE);
                        holder.add.setVisibility(View.GONE);
                        holder.amigos.setVisibility(View.GONE);
                        holder.tengosolicitud.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.GONE);

                    }
                }else{
                    holder.send.setVisibility(View.GONE);
                    holder.add.setVisibility(View.VISIBLE);
                    holder.amigos.setVisibility(View.GONE);
                    holder.tengosolicitud.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.GONE);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference A=database.getReference("Users").child(user.getUid()).child("Solicitudes");
                A.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            A.child(userss.getId()).child("estado").setValue("enviado");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                final DatabaseReference B=database.getReference("Users").child(userss.getId()).child("Solicitudes");
                B.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            B.child(user.getUid()).child("estado").setValue("solicitud");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final DatabaseReference count=database.getReference("Users").child(userss.getId()).child("solicitudes");
                count.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Integer val=dataSnapshot.getValue(Integer.class);
                            if(val==0){
                                count.setValue(1);
                            }else{
                                count.setValue(val+1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                vibrator.vibrate(300);
            }
        });

       holder.tengosolicitud.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final DatabaseReference A=database.getReference("Users").child(user.getUid()).child("Solicitudes");
               A.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       A.child(userss.getId()).child("estado").setValue("amigos");

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

               final DatabaseReference B=database.getReference("Users").child(userss.getId()).child("Solicitudes");
               B.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       B.child(user.getUid()).child("estado").setValue("amigos");

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
               vibrator.vibrate(300);
           }
       });
       holder.amigos.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(context, "Ahora Somos Amigos", Toast.LENGTH_SHORT).show();
           }
       });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class viewHolderAdapter extends RecyclerView.ViewHolder {

        TextView tv_usuario;
        ImageView img_user;
        CardView cardView;
        Button add,send,amigos,tengosolicitud;
        ProgressBar progressBar;

        public viewHolderAdapter(@NonNull View itemView) {
            super(itemView);
            tv_usuario=itemView.findViewById(R.id.tv_user);
            img_user=itemView.findViewById(R.id.img_user);
            cardView=itemView.findViewById(R.id.cardview);
            add=itemView.findViewById(R.id.btn_add);
            send=itemView.findViewById(R.id.btn_send);
            amigos=itemView.findViewById(R.id.btn_amigos);
            tengosolicitud=itemView.findViewById(R.id.btn_tengosolicitud);
            progressBar=itemView.findViewById(R.id.progressbar);
        }
    }
}

