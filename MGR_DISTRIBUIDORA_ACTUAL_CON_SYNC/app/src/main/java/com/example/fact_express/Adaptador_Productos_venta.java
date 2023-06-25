package com.example.fact_express;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fact_express.Modelos.Productos;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Adaptador_Productos_venta extends BaseAdapter {
    private final Context context;
    private final ArrayList<Productos> listItems;

    public Adaptador_Productos_venta(Context context, ArrayList<Productos> listItems) {
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

        convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_productos_venta, null);
        TextView descipcion = convertView.findViewById(R.id.id_descripcion);
        TextView precio = convertView.findViewById(R.id.id_precio);
        TextView prom = convertView.findViewById(R.id.id_prom);
        TextView prom_mensaje = convertView.findViewById(R.id.id_prom_mensaje);
        TextView cant_prom = convertView.findViewById(R.id.id_cant_prom);
        TextView precio_prom = convertView.findViewById(R.id.id_precio_prom);
        TextView precio_prom_utilizar = convertView.findViewById(R.id.id_precio_prom_utilizar);
        TextView porc_prom = convertView.findViewById(R.id.id_porc_prom);
        LinearLayout conten = convertView.findViewById(R.id.id_contenedor_promo);
        descipcion.setText(item.getDescripcion());
        DecimalFormat nformat = new DecimalFormat("##,###,###");
        precio.setText(nformat.format(Integer.parseInt(item.getPrecio())) + "  /  " + item.getIva());
        prom.setText(item.getProm());
        if(item.getProm().equals("S")){
            conten.setVisibility(View.VISIBLE);
            prom_mensaje.setText("EN PROMOCIÓN");
            cant_prom.setText("A partir de "+item.cant_prom.replace(".00","")+" unidades");
            precio_prom.setText("Precio Promoción: "+nformat.format((item.getPrecio_prom())));
            precio_prom_utilizar.setText(String.valueOf(item.getPrecio_prom()));
            porc_prom.setText("PROMOCIÓN HABILITADO (-"+item.getPorc_prom()+"%)");
        }else{
            conten.setVisibility(View.GONE);
            prom_mensaje.setText(" ");
            cant_prom.setText("A partir de "+item.cant_prom.replace(".00","")+" unidades");
            precio_prom.setText("Precio Promoción: "+nformat.format((item.getPrecio_prom())));
            precio_prom_utilizar.setText(String.valueOf(item.getPrecio_prom()));
            porc_prom.setText("PROMOCIÓN HABILITADO (-"+item.getPorc_prom()+"%)");
        }

        return convertView;
    }
}
