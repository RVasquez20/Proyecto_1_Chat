package com.example.chatjavafirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.example.chatjavafirebase.adapters.PaginasAdapter;
import com.example.chatjavafirebase.Models.Estado;
import com.example.chatjavafirebase.Models.Users;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class homeActivity extends AppCompatActivity {

    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    FirebaseDatabase database=FirebaseDatabase.getInstance();

    DatabaseReference ref_user=database.getReference("Users").child(user.getUid());

    DatabaseReference ref_solicitud_count=database.getReference("Contador").child(user.getUid());
    DatabaseReference ref_estado=database.getReference("Estado").child(user.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewPager2 viewPager2=findViewById(R.id.viewPager);
        viewPager2.setAdapter(new PaginasAdapter(this));

        final TabLayout tabLayout=findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:{
                        tab.setText("Users");
                        tab.setIcon(R.drawable.ic_usuarios);
                        break;
                    }
                    case 1:{
                        tab.setText("Chats");
                        tab.setIcon(R.drawable.ic_chats);
                        break;
                    }
                    case 2:{
                        tab.setText("Solicitudes");
                        tab.setIcon(R.drawable.ic_solicitudes);
                        final BadgeDrawable badgeDrawable=tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(
                                ContextCompat.getColor(getApplicationContext(),R.color.colorAccent)
                        );


                        ref_solicitud_count.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if(dataSnapshot.exists()){
                                   Integer val=dataSnapshot.getValue(Integer.class);
                                   badgeDrawable.setVisible(true);
                                   if(val.equals("0")){
                                       badgeDrawable.setVisible(false);
                                   }else{
                                       badgeDrawable.setNumber(val);
                                   }

                               }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        break;
                    }

                }
            }
        });
        tabLayoutMediator.attach();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BadgeDrawable badgeDrawable=tabLayout.getTabAt(position).getOrCreateBadge();
                badgeDrawable.setVisible(false);

                if(position==2){
                    countacero();
                }

            }
        });
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        userunico();
    }

    private void estadousuario(final String estado) {
    ref_estado.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Estado est=new Estado(estado,"","","");
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

    private void countacero() {
        ref_solicitud_count.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ref_solicitud_count.setValue(0);
                    Toast.makeText(homeActivity.this, "Solicitudes Vistas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    static class Utiles {

        public static String obtenerHoraActual(String zonaHoraria) {
            String formato = "HH:mm:ss";
            return Utiles.obtenerFechaConFormato(formato, zonaHoraria);
        }

        public static String obtenerFechaActual(String zonaHoraria) {
            String formato = "yyyy-MM-dd";
            return Utiles.obtenerFechaConFormato(formato, zonaHoraria);
        }

        @SuppressLint("SimpleDateFormat")
        public static String obtenerFechaConFormato(String formato, String zonaHoraria) {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat(formato);
            sdf.setTimeZone(TimeZone.getTimeZone(zonaHoraria));
            return sdf.format(date);
        }
    }
    private void userunico() {

        ref_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    Users uu= new Users(
                      user.getUid(),
                      user.getDisplayName(),
                      user.getEmail(),
                      user.getPhotoUrl().toString(),
                      "desconectado",
                      Utiles.obtenerFechaActual("America/Mexico_City"),
                      Utiles.obtenerHoraActual("America/Mexico_City"),
                      0,
                      0);
                    ref_user.setValue(uu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}