package com.example.fact_express;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fact_express.Modelos.Productos;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Adaptador_Productos extends BaseAdapter {
    private Context context;
    private ArrayList<Productos> listItems;

    public Adaptador_Productos(Context context, ArrayList<Productos> listItems) {
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
        Productos item = (Productos) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_productos2,null);
        TextView codI = (TextView) convertView.findViewById(R.id.id_interno);
        TextView codB = (TextView) convertView.findViewById(R.id.id_barra);
        TextView descipcion = (TextView) convertView.findViewById(R.id.id_descripcion);
        TextView clasificacion = (TextView) convertView.findViewById(R.id.id_clasificacion);
        TextView precio = (TextView) convertView.findViewById(R.id.id_precio);

        DecimalFormat nformat = new DecimalFormat("##,###,###");
        codI.setText(item.getCod_interno());
        codB.setText(item.getCod_barra());
        descipcion.setText(item.getDescripcion());
        clasificacion.setText(item.getDivision());
        precio.setText(nformat.format(Integer.parseInt(item.getPrecio())) +"  /  "+item.getIva());
        return convertView;
    }
}
