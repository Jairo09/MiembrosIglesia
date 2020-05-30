package com.example.miembrosiglesia;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.miembrosiglesia.sqlite.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragmentSeniores extends Fragment {
    View v;
    private RecyclerView myrecyclerview;
    private List<Miembro> lstMiembros;

    private ArrayList<String> arrNombres;
    private ArrayList<String> arrApellidos;
    private ArrayList<String> arrSociedades;
    private int contador = 1;
    private SQLiteHandler db;

    public FragmentSeniores() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.seniores_fragment, container, false);

        myrecyclerview = v.findViewById(R.id.senioresReciclerview);
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

            if (miembrosList.get(i).getSociedad().equals("Señores")) {
                lstMiembros.add(miembrosList.get(i));
            }


        }

    }
}
