package com.example.fact_express.Actividades;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fact_express.Adaptador_Productos_venta;
import com.example.fact_express.CRUD.Access_Productos;
import com.example.fact_express.Modelos.Productos;
import com.example.fact_express.R;

import java.util.ArrayList;

public class Listar_ProductosVenta extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Productos> lista = new ArrayList<>();
    private Adaptador_Productos_venta adaptadorProductos;
    private int productoseleccionado = -1;
    private Object mActionMode;
    private TextView pie;
    private EditText buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_productos_venta);

        buscar = findViewById(R.id.id_buscarproductosVenta);

        llenarLista();
        onClick();

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filtro= String.valueOf(s.toString());
                if(filtro.length()>=0){
                    Log.i("",filtro);
                    lista.removeAll(lista);
                    llenarListaFiltrada(filtro);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onResume() {
        super.onResume();
        lista.removeAll(lista);
        llenarLista();
    }

    public void onClick() {
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                productoseleccionado = position;
                mActionMode = Listar_ProductosVenta.this.startActionMode(amc);
                view.setSelected(true);
                return true;
            }
        });
    }

    private ActionMode.Callback amc = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.opciones_vender, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_vender) {
                Productos productos = lista.get(productoseleccionado);
                Intent bundle = new Intent(getApplicationContext(), Registrar_venta.class);
                bundle.putExtra("idproducto", productos.getIdproducto());
                bundle.putExtra("descripcion", productos.getDescripcion());
                bundle.putExtra("precio", productos.getPrecio());
                bundle.putExtra("um", productos.getIdunidad());
                bundle.putExtra("umdescripcion", productos.getUnidad());
                startActivity(bundle);
                mode.finish();
                finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }


    };
    public void llenarLista(){
        try{
            lv = (ListView) findViewById(R.id.id_lista_productosVenta);
            Access_Productos db = Access_Productos.getInstance(getApplicationContext());
            Cursor c = db.getProductos();
            if (c.moveToFirst()){
                do {
                    lista.add( new Productos (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getString(8),
                            c.getString(9),c.getString(6),c.getInt(7),
                            c.getString(10),c.getString(11), c.getInt(12), c.getString(13)));
                }while (c.moveToNext());
            }
            adaptadorProductos = new Adaptador_Productos_venta(this,lista);
            lv.setAdapter(adaptadorProductos);
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarListaFiltrada(String filtro){
        try{
            lv = (ListView) findViewById(R.id.id_lista_productosVenta);
            Access_Productos db = Access_Productos.getInstance(getApplicationContext());
            Cursor c = db.getFiltrarProductos(filtro);
            if (c.moveToFirst()){
                do {
                    lista.add( new Productos (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getString(8),
                            c.getString(9),c.getString(6),c.getInt(7),
                            c.getString(10),c.getString(11), c.getInt(12), c.getString(13)));
                }while (c.moveToNext());
            }
            adaptadorProductos = new Adaptador_Productos_venta(this,lista);
            lv.setAdapter(adaptadorProductos);
            db.close();

        }catch (Exception e){
            Toast.makeText(this, "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}