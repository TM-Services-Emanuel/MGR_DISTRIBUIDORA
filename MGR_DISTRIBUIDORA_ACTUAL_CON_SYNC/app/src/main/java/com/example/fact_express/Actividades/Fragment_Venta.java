package com.example.fact_express.Actividades;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fact_express.Adaptador_Productos_venta;
import com.example.fact_express.CRUD.Access_Productos;
import com.example.fact_express.CRUD.Access_UnicadMedida;
import com.example.fact_express.Modelos.Productos;
import com.example.fact_express.Modelos.UnidadMedida;
import com.example.fact_express.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.fact_express.Actividades.Editar_Producto.getIndexSpinnerUM;

public class Fragment_Venta extends Fragment {

    public View vista;
    public ListView lv;
    public ArrayList<Productos> lista = new ArrayList<>();
    public Adaptador_Productos_venta adaptadorProductos;
    public static int productoseleccionado = -1;
    public EditText buscar, txtCantidad;
    public static int productoEditar;
    //public Button calcular;
    public Button ocultarFormularioCantidad;
    public Spinner comboUM;
    public static ArrayList<String> listaum;
    public static ArrayList<UnidadMedida> umlist;
    public static int exenta,iva5,iva10;
    public LinearLayout formularioCantidad;


    public TextView txtidproducto,txtcodbarra,txtproduto,txtprecio,txtumCant,txtumdescripcion,txtTotalVenta,txtiva,txttotaliva;
    public TextView txtprom, txtcant_prom, txtprecio_prom, txtporc_prom, txtprecio_normal, txtMensaje;

    public Fragment_Venta() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment__venta, container, false);
        formularioCantidad = (LinearLayout) vista.findViewById(R.id.formulario_cantidad);
        buscar = vista.findViewById(R.id.id_buscarproductosV);
        txtidproducto = vista.findViewById(R.id.id_idprodven);
        txtcodbarra = vista.findViewById(R.id.txt_idcodbarra);
        txtproduto = vista.findViewById(R.id.id_txtproducto);
        txtprecio = vista.findViewById(R.id.id_txtprecio);
        txtumCant = vista.findViewById(R.id.txtidumcant);
        txtumdescripcion = vista.findViewById(R.id.txtiddescripum);
        txtCantidad = vista.findViewById(R.id.id_txtcant);
        txtTotalVenta = vista.findViewById(R.id.id_txttotal);
        txtiva = vista.findViewById(R.id.txt_idiva);
        txttotaliva = (TextView) vista.findViewById(R.id.txt_totaliva);
        comboUM = vista.findViewById(R.id.spinner_umv);
        //calcular = (Button) vista.findViewById(R.id.btnCalcular);
        ocultarFormularioCantidad = (Button) vista.findViewById(R.id.btnOcularFormularioCantidad);
        txtprom = vista.findViewById(R.id.id_si_prom);
        txtcant_prom = vista.findViewById(R.id.id_cant_prom_control);
        txtprecio_prom = vista.findViewById(R.id.id_precio_prom_a_utilizar);
        txtporc_prom = vista.findViewById(R.id.id_porc_prom_a_mostrar);
        txtprecio_normal = vista.findViewById(R.id.id_precio_normal);
        txtMensaje = vista.findViewById(R.id.idMensajePromo);

        return vista;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull final View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        formularioCantidad.setVisibility(View.GONE);
        llenarLista();
        onClick();
        ocultarFormularioCantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bajarTeclado();
                formularioCantidad.setVisibility(View.GONE);

            }
        });

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

        consultarlistaUM();
        ArrayAdapter<String> adaptadorUM = new ArrayAdapter(getContext(), R.layout.spinner_item_ldventa,listaum);
        comboUM.setAdapter(adaptadorUM);
        comboUM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtumCant.setText("");
                txtCantidad.setText("");
                txtTotalVenta.setText("");
                try{
                    if(position!=0){
                        txtumCant.setText(String.valueOf(umlist.get(position-1).getCant()));
                        try{Calculadora();}catch(Exception e){};
                    }else{
                        txtumCant.setText("");
                        txtCantidad.setText("");
                        txtTotalVenta.setText("");
                    }
                }catch (Exception e){
                    //Toast.makeText(getContext(), "error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        txtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{Calculadora();}catch(Exception e){};
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((Button)view.findViewById(R.id.btnEnviar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(((TextView) view.findViewById(R.id.id_txttotal)).getText().toString().trim().isEmpty()){
                        //Toast.makeText(getActivity(),"NO SE HA CALCULADO EL TOTAL",Toast.LENGTH_SHORT).show();
                        Snackbar.make(view.findViewById(R.id.linearLayout61), "INGRESE LA CANTIDAD PARA CALCULAR EL TOTAL", Snackbar.LENGTH_SHORT)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(Color.parseColor("#FF6F00"))
                                .setActionTextColor(Color.parseColor("#FFFFFF")).show();
                    }else if(Integer.parseInt(((TextView) view.findViewById(R.id.id_txttotal)).getText().toString().trim())<=0){
                        //Toast.makeText(getActivity(),"VALOR DE TOTAL NO VÁLIDO.",Toast.LENGTH_SHORT).show();
                        Snackbar.make(view.findViewById(R.id.linearLayout61), "EL VALOR 0 COMO TOTAL NO ES VÁLIDO", Snackbar.LENGTH_SHORT)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(Color.parseColor("#FF6F00"))
                                .setActionTextColor(Color.parseColor("#FFFFFF")).show();
                    }else{
                        String id=((TextView) view.findViewById(R.id.id_idprodven)).getText().toString();
                        String codbarra=((TextView) view.findViewById(R.id.txt_idcodbarra)).getText().toString();
                        String producto=((TextView  ) view.findViewById(R.id.id_txtproducto)).getText().toString();
                        String precio = ((TextView) view.findViewById(R.id.id_txtprecio)).getText().toString();
                        String cantidad = ((EditText) view.findViewById(R.id.id_txtcant)).getText().toString();
                        String um= ((Spinner) view.findViewById(R.id.spinner_umv)).getSelectedItem().toString();
                        int total= Integer.parseInt(((TextView) view.findViewById(R.id.id_txttotal)).getText().toString());
                        String ivadescripcion = ((TextView) view.findViewById(R.id.txt_idiva)).getText().toString();
                        String mensaje = txtMensaje.getText().toString();

                        Bundle enviar = new Bundle();
                        enviar.putString("id",id);
                        enviar.putString("codbarra",codbarra);
                        enviar.putString("producto",producto);
                        enviar.putString("precio",precio);
                        enviar.putString("cantidad",cantidad);
                        enviar.putString("um",um);
                        enviar.putInt("total",total);
                        enviar.putString("ivadescripcion",ivadescripcion);
                        enviar.putInt("exenta",exenta);
                        enviar.putInt("iva5",iva5);
                        enviar.putInt("iva10",iva10);
                        enviar.putString("mensaje",mensaje);

                        getParentFragmentManager().setFragmentResult("enviar",enviar);
                        limpiar();
                        buscar.requestFocus();
                        formularioCantidad.setVisibility(View.GONE);
                        bajarTeclado();
                    }

                }catch (Exception e){}

            }
        });

    }

    public void bajarTeclado() {
        View view = getActivity().getCurrentFocus();
        if(view !=null){
            InputMethodManager imn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imn.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public void onClick() {
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                productoseleccionado = position;
                Productos productos = lista.get(productoseleccionado);

                txtidproducto.setText(String.valueOf(productos.getIdproducto()));
                txtcodbarra.setText(productos.getCod_barra());
                txtproduto.setText(productos.getDescripcion());
                txtprecio.setText(productos.getPrecio());
                txtumdescripcion.setText(productos.getUnidad());
                txtiva.setText(String.valueOf(productos.getImpuesto()));
                txtCantidad.setText("");
                txtTotalVenta.setText("");
                txtprom.setText(productos.getProm());
                txtcant_prom.setText(productos.getCant_prom().replace(".00",""));
                txtprecio_prom.setText(String.valueOf(productos.precio_prom));
                txtprecio_normal.setText(productos.getPrecio());
                txtporc_prom.setText("PROMOCIÓN HABILITADO (-"+productos.getPorc_prom()+"%)");

                int indexUM= getIndexSpinnerUM(comboUM, txtumdescripcion.getText().toString());
                comboUM.setSelection(indexUM);

                txtCantidad.requestFocus();

                view.setSelected(true);

                formularioCantidad.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }

    public void llenarLista(){
        try{
            lv = (ListView) vista.findViewById(R.id.list_prodavender);
            Access_Productos db = Access_Productos.getInstance(getContext());
            Cursor c = db.getProductos();
            if (c.moveToFirst()){
                do {
                    lista.add( new Productos (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getString(8),
                            c.getString(9),c.getString(6),c.getInt(7),
                            c.getString(10),c.getString(11), c.getInt(12), c.getString(13)));
                }while (c.moveToNext());
            }
            adaptadorProductos = new Adaptador_Productos_venta(getContext(),lista);
            lv.setAdapter(adaptadorProductos);
            db.close();

        }catch (Exception e){
            Toast.makeText(getContext(), "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void llenarListaFiltrada(String filtro){
        try{
            lv = (ListView) vista.findViewById(R.id.list_prodavender);
            Access_Productos db = Access_Productos.getInstance(getContext());
            Cursor c = db.getFiltrarProductos(filtro);
            if (c.moveToFirst()){
                do {
                    lista.add( new Productos (c.getInt(0),c.getString(1),c.getString(2),c.getString(3)
                            ,c.getString(4),c.getString(8),
                            c.getString(9),c.getString(6),c.getInt(7),
                            c.getString(10),c.getString(11), c.getInt(12), c.getString(13)));
                }while (c.moveToNext());
            }
            adaptadorProductos = new Adaptador_Productos_venta(getContext(),lista);
            lv.setAdapter(adaptadorProductos);
            db.close();

        }catch (Exception e){
            Toast.makeText(getContext(), "Error cargando lista: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void consultarlistaUM() {
        Access_UnicadMedida db = Access_UnicadMedida.getInstance(getContext());
        db.openReadable();
        UnidadMedida unidadMedida=null;
        umlist = new ArrayList<UnidadMedida>();
        Cursor cursor = db.getUnidadMedida();
        while (cursor.moveToNext()){
            unidadMedida = new UnidadMedida();
            unidadMedida.setIdunidad(cursor.getInt(0));
            unidadMedida.setUm(cursor.getString(1));
            unidadMedida.setCant(cursor.getInt(2));

            umlist.add(unidadMedida);
        }
        obtenerlistaUM();
    }
    public void obtenerlistaUM(){
        listaum = new ArrayList<String>();
        listaum.add("Selec.");
        for(int i=0; i < umlist.size();i++) {
            listaum.add(umlist.get(i).getUm().toString());
        }
    }

    private void limpiar(){
        txtidproducto.setText("");
        txtproduto.setText("");
        txtprecio.setText("");
        txtumdescripcion.setText("");
        txtCantidad.setText("");
        txtTotalVenta.setText("");
        txtMensaje.setText("");
    }

    private void Calculadora(){
        if(txtidproducto.getText().toString().trim().isEmpty()){
            //Toast.makeText(getContext(),"No ha seleccionado ningún producto",Toast.LENGTH_SHORT).show();
        }else if(txtCantidad.getText().toString().trim().isEmpty()){
            //Toast.makeText(getContext(),"Ingrese cantidad",Toast.LENGTH_SHORT).show();
            txtCantidad.requestFocus();
            txtTotalVenta.setText("");
        }else if(Double.parseDouble(txtCantidad.getText().toString())<0){
            // Toast.makeText(getContext(),"Ingrese cantidad válida",Toast.LENGTH_SHORT).show();
            txtCantidad.requestFocus();
        }else{
            try{
                int cantum = Integer.parseInt(txtumCant.getText().toString());
                double cantventa = Double.parseDouble(txtCantidad.getText().toString());
                String promo = txtprom.getText().toString();
                double cant_promo = Double.parseDouble(txtcant_prom.getText().toString());
                int precio_promocion = Integer.parseInt(txtprecio_prom.getText().toString());
                int precio_normal= Integer.parseInt(txtprecio_normal.getText().toString());
                int precioventa = Integer.parseInt(txtprecio.getText().toString());
                int total=0;
                if(promo.equals("S")){
                    if(cantventa < cant_promo) {
                        txtprecio.setText(String.valueOf(precio_normal));
                        total = (int) ((cantum * cantventa) * precio_normal);
                        txtMensaje.setText(" ");
                    }else if(cantventa >= cant_promo){
                        txtprecio.setText(String.valueOf(precio_promocion));
                        total= (int) ((cantum*cantventa)*precio_promocion);
                        txtMensaje.setText(txtporc_prom.getText().toString());
                    }
                }else{
                    txtprecio.setText(String.valueOf(precioventa));
                    total = (int) ((cantum * cantventa) * precioventa);
                    txtMensaje.setText(" ");
                }

                //int total= (int) ((cantum*cantventa)*precioventa);
                txtTotalVenta.setText(String.valueOf(total));
                int imp=Integer.parseInt(((TextView) vista.findViewById(R.id.txt_idiva)).getText().toString());
                switch (imp){
                    case 0:
                        exenta=0;
                        iva5=0;
                        iva10=0;
                        txttotaliva.setText(String.valueOf(exenta));
                        break;
                    case 5:
                        exenta=0;
                        //iva5=total/21;
                        iva5=total;
                        iva10=0;
                        txttotaliva.setText(String.valueOf(iva5));
                        break;
                    case 10:
                        exenta=0;
                        iva5=0;
                        //iva10=total/11;
                        iva10=total;
                        txttotaliva.setText(String.valueOf(iva10));
                        break;
                }

            }catch (Exception e){
                Toast.makeText(getContext(),"ERROR_CALCULADORA: "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            }

    }

}