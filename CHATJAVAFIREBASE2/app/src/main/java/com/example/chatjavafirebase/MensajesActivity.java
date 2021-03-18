package com.example.chatjavafirebase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.chatjavafirebase.adapters.AdapterChats;
import com.example.chatjavafirebase.Models.Chats;
import com.example.chatjavafirebase.Models.Estado;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajesActivity extends AppCompatActivity {
    CircleImageView img_user;
    TextView username;
    ImageView ic_conectado, ic_desconectado;
    SharedPreferences mPref;


    //databaseReference = database.getReference("ChatsG");
    private static final int PHOTO_SEND = 1;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref_estado = database.getReference("Estado").child(user.getUid());
    DatabaseReference ref_chat = database.getReference("Chats");
    //DatabaseReference databaseReference;
    EditText et_mensaje_txt;
    ImageButton btn_enviar_mensaje,btn_enviar_imagen;

    private StorageReference storageReference;
    private FirebaseStorage storage;

    //ID chat global
    String id_chat_global;
    Boolean amigoonline = false;
    //RV
    //RecyclerView rv_chats,rvMensajes;
    RecyclerView rv_chats;
    AdapterChats adapter;
    ArrayList<Chats> chatlist;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mensajes);
        database = FirebaseDatabase.getInstance();
       // databaseReference = database.getReference("ChatsG");
        storage = FirebaseStorage.getInstance();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPref= getApplicationContext().getSharedPreferences("usuario_sp",MODE_PRIVATE);

        img_user=findViewById(R.id.img_user);
        username=findViewById(R.id.tv_user);
        ic_conectado=findViewById(R.id.icon_conectado);
        ic_desconectado=findViewById(R.id.icon_desconectado);

        String usuario=getIntent().getExtras().getString("nombre");
        String foto=getIntent().getExtras().getString("img_user");
          final String id_user=getIntent().getExtras().getString("id_user");
        id_chat_global=getIntent().getExtras().getString("id_unico");
        
        colocarenvisto();
        
        et_mensaje_txt=findViewById(R.id.et_txt_mensaje);
        btn_enviar_mensaje=findViewById(R.id.btn_enviar_mensaje);
        btn_enviar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msj=et_mensaje_txt.getText().toString();
                if(!msj.isEmpty()) {
                    final Calendar c=Calendar.getInstance();
                    final SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
                    final SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
                    String id_push=ref_chat.push().getKey();

                    if(amigoonline){
                        Chats chatmsj = new Chats(id_push,user.getUid(), id_user, msj, "si",dateFormat.format(c.getTime()),timeFormat.format(c.getTime()),"1");
                        ref_chat.child(id_chat_global).child(id_push).setValue(chatmsj);
                        Toast.makeText(MensajesActivity.this, "Mensaje Enviado :D", Toast.LENGTH_SHORT).show();
                        et_mensaje_txt.setText("");
                    }else {
                        Chats chatmsj = new Chats(id_push,user.getUid(), id_user, msj, "no",dateFormat.format(c.getTime()),timeFormat.format(c.getTime()),"1");
                        ref_chat.child(id_chat_global).child(id_push).setValue(chatmsj);
                        Toast.makeText(MensajesActivity.this, "Mensaje Enviado :D", Toast.LENGTH_SHORT).show();
                        et_mensaje_txt.setText("");
                    }

                }else{
                    Toast.makeText(MensajesActivity.this, "El Mensaje Esta Vacio", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //Codigo Prueba
        btn_enviar_imagen=findViewById(R.id.btn_enviar_imagen);
        btn_enviar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MensajesActivity.this, "Seleccionar imagen", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);
                /*Intent chooser;
                chooser = Intent.createChooser(i, "Seleciona una foto");

*/
            }
        });
        //

        final String id_user_sp=mPref.getString("usuario_sp","");
        username.setText(usuario);
        Glide.with(this).load(foto).into(img_user);

        final DatabaseReference ref= database.getReference("Estado").child(id_user_sp).child("chatcon");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String chatcon=dataSnapshot.getValue(String.class);
             if(dataSnapshot.exists()){
                 if (chatcon.equals(user.getUid())){
                     amigoonline=true;
                     ic_conectado.setVisibility(View.VISIBLE);
                     ic_desconectado.setVisibility(View.GONE);
                 }else{
                     amigoonline=false;
                     ic_conectado.setVisibility(View.GONE);
                     ic_desconectado.setVisibility(View.VISIBLE);
                 }

             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //RV

        rv_chats=findViewById(R.id.rv);
        rv_chats.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rv_chats.setLayoutManager(linearLayoutManager);
        chatlist=new ArrayList<>();
        adapter=new AdapterChats(chatlist,this);
        rv_chats.setAdapter(adapter);
        /*LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);*/
        Leermensajes();
      /*  adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Chats m = dataSnapshot.getValue(Chats.class);
                adapter.addMensaje(m);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/

    }//fin del oncreate

    /*private void setScrollbar() {
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }*/

    public static boolean verifyStoragePermissions(Activity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else{
            return true;
        }
    }
    private void colocarenvisto() {

        ref_chat.child(id_chat_global).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats=snapshot.getValue(Chats.class);
                    if(chats.getRecibe().equals(user.getUid())&& chats.getTipoMensaje().equals("1")){
                        ref_chat.child(id_chat_global).child(chats.getId()).child("visto").setValue("si");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void Leermensajes() {
        ref_chat.child(id_chat_global).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatlist.removeAll(chatlist);
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Chats chat=snapshot.getValue(Chats.class);
                        chatlist.add(chat);
                        setScroll();
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setScroll() {
        rv_chats.scrollToPosition(adapter.getItemCount()-1);
    }

    private void estadousuario(final String estado) {
        ref_estado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String id_user_sp=mPref.getString("usuario_sp","");
                Estado est=new Estado(estado,"","",id_user_sp);
                ref_estado.setValue(est);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        estadousuario("conectado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        estadousuario("desconectado");
        dameultimafecha();
    }

    private void dameultimafecha() {
        final Calendar c=Calendar.getInstance();
        final SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm");
        final SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        ref_estado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref_estado.child("fecha").setValue(dateFormat.format(c.getTime()));
                ref_estado.child("hora").setValue(timeformat.format(c.getTime()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String id_user2=getIntent().getExtras().getString("id_user");
        Uri u = data.getData();
        storageReference = storage.getReference("imagenes_chat");

        final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());

        fotoReferencia.putFile(u).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return fotoReferencia.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult();
                    final Calendar c=Calendar.getInstance();
                    final SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
                    final SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
                    String id_push=ref_chat.push().getKey();
                    Chats chatmsj = new Chats(id_push,user.getUid(), id_user2, " te ha enviado una foto", "no",dateFormat.format(c.getTime()),timeFormat.format(c.getTime()),uri.toString(),"2");
                    ref_chat.child(id_chat_global).child(id_push).setValue(chatmsj);

                }
            }
        });

    }
}