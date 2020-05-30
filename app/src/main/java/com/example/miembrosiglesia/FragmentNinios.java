package com.example.miembrosiglesia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miembrosiglesia.sqlite.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;


public class FragmentNinios extends Fragment{
    View v;
    private RecyclerView myrecyclerview;
    private List<Miembro> lstMiembros;

    private ArrayList<String> arrNombres;
    private ArrayList<String> arrApellidos;
    private ArrayList<String> arrSociedades;
    private int contador =1;
    private SQLiteHandler db;


    public FragmentNinios() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.ninios_fragment, container, false);

        myrecyclerview = v.findViewById(R.id.niniosReciclerview);
        RecyclerViewAdapterSeniores recyclerAdapter = new RecyclerViewAdapterSeniores(getContext(), lstMiembros);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SqLite database handler
        db = new SQLiteHandler(getContext());
        //traer datos de sqlite
        ArrayList<Miembro> miembrosList = db.getMiembrosDetails();

        lstMiembros = new ArrayList<>();
        for (int i = 0; i < miembrosList.size(); i++) {

            if (miembrosList.get(i).getSociedad().equals("Niños")) {
                lstMiembros.add(miembrosList.get(i));
            }


        }

    }

}
