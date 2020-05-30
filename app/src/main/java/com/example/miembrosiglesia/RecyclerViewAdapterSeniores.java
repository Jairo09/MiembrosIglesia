package com.example.miembrosiglesia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapterSeniores extends RecyclerView.Adapter<RecyclerViewAdapterSeniores.MyViewHolder> {

    Context mContext;
    List<Miembro> mData;

    public RecyclerViewAdapterSeniores(Context mContext, List<Miembro> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View v;
       v = LayoutInflater.from(mContext).inflate(R.layout.senioreslistview, viewGroup, false);
       MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.tv_name.setText(mData.get(i).getNombre());
        myViewHolder.tv_apellido.setText(mData.get(i).getApellido());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static  class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_apellido;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.lblnombrelst);
            tv_apellido = itemView.findViewById(R.id.lblapellidolst);

        }
    }

}
