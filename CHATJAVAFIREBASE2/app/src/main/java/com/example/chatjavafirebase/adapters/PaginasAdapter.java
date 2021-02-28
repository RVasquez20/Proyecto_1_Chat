package com.example.chatjavafirebase.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chatjavafirebase.fragments.chatsFragment;
import com.example.chatjavafirebase.fragments.mis_solicitudes_Fragment;
import com.example.chatjavafirebase.fragments.solicitudesFragment;
import com.example.chatjavafirebase.fragments.usuariosFragment;

public class PaginasAdapter extends FragmentStateAdapter {

    public PaginasAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new usuariosFragment();
            case 1:
                return new chatsFragment();
            case 2:
                return new solicitudesFragment();
            case 3:
                return new mis_solicitudes_Fragment();
            default:
                return new usuariosFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
