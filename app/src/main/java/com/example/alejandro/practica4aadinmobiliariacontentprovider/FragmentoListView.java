package com.example.alejandro.practica4aadinmobiliariacontentprovider;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoListView extends Fragment {

    private View v;


    public FragmentoListView(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_fragmento_list_view, container, false);
        return v;
    }

}
