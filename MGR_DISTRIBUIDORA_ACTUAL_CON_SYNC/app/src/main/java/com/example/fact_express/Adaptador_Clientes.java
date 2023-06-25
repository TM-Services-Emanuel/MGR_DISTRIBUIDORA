package com.example.fact_express;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fact_express.Modelos.Clientes;

import java.util.ArrayList;

public class Adaptador_Clientes extends BaseAdapter {
    private final Context context;
    private final ArrayList<Clientes> listItems;

    public Adaptador_Clientes(Context context, ArrayList<Clientes> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Clientes item = (Clientes) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_clientes, null);
        TextView idcliente = convertView.findViewById(R.id.id_codcliente);
        TextView cod_int = convertView.findViewById(R.id.id_cod_interno_c);
        TextView nombre_f = convertView.findViewById(R.id.id_nombre_f);
        TextView razonsocial = convertView.findViewById(R.id.id_razonsocial);
        TextView ruc = convertView.findViewById(R.id.id_ruc);
        TextView direccion = convertView.findViewById(R.id.id_ciudad);
        TextView ref = convertView.findViewById(R.id.id_ref);


        idcliente.setText(String.valueOf(item.getIdcliente()));
        razonsocial.setText(item.getRazon_social()+" | "+item.getRuc());
        cod_int.setText(item.getCod_int());
        nombre_f.setText(item.getNombre_f());
        ruc.setText(item.getRuc());
        direccion.setText(item.getDireccion());
        ref.setText(item.ref1+" / "+item.ref2);
        return convertView;
    }
}
