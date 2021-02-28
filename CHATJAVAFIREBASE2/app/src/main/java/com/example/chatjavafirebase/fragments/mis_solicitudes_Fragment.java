package com.example.chatjavafirebase.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatjavafirebase.R;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class mis_solicitudes_Fragment extends Fragment {


    public mis_solicitudes_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mis_solicitudes_, container, false);
    }
}