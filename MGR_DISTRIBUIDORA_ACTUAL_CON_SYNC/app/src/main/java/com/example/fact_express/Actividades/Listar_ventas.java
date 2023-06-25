package com.example.fact_express.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fact_express.Adaptador_ventas;
import com.example.fact_express.CRUD.Access_PE;
import com.example.fact_express.CRUD.Access_Timbrado;
import com.example.fact_express.CRUD.Access_Venta;
import com.example.fact_express.Modelos.DetalleVentaSync;
import com.example.fact_express.Modelos.Fecha;
import com.example.fact_express.Modelos.Numero_a_Letra;
import com.example.fact_express.Modelos.ReimpresionVenta;
import com.example.fact_express.Modelos.Ventas;
import com.example.fact_express.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class Listar_ventas extends AppCompatActivity {
    private ListView lv;
    private ArrayList<Ventas> lista = new ArrayList<>();
    private ArrayList<DetalleVentaSync> listaDetalle = new ArrayList<>();
    private Adaptador_ventas adaptadorVenta;
    private int ventaseleccionado = -1;
    private Object mActionMode;
    private TextView pie, totalvf, totalva;
    private String fechaactual;
    private EditText etFecha;
    private int ultimoAnio, ultimoMes, ultimoDiaDelMes;
    private ArrayList<ReimpresionVenta> listaparaReimpresion = new ArrayList<>();
    static private Numero_a_Letra d;
    private String idvendedor, vendedor;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    Resources resources;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    public static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};
    public static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    public static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    public static final byte[] ESC_CANCEL_BOLD = new byte[]{0x1B, 0x45, 0};


    public static byte[] format = {27, 33, 0};
    public static byte[] arrayOfByte1 = {27, 33, 0};


    // Crear un listener del datepicker;
    private final DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anio, int mes, int diaDelMes) {
            // Esto se llama cuando seleccionan una fecha. Nos pasa la vista, pero más importante, nos pasa:
            // El año, el mes y el día del mes. Es lo que necesitamos para saber la fecha completa


            // Refrescamos las globales
            ultimoAnio = anio;
            ultimoMes = mes;
            ultimoDiaDelMes = diaDelMes;

            // Y refrescamos la fecha
            refrescarFechaEnEditText();

        }
    };

    public void refrescarFechaEnEditText() {
        // Formateamos la fecha pero podríamos hacer cualquier otra cosa ;)
        String fecha = String.format(Locale.getDefault(), "%02d/%02d/%02d", ultimoDiaDelMes, ultimoMes + 1, ultimoAnio);
        // La ponemos en el editText
        etFecha.setText(fecha);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_ventas);
        d = new Numero_a_Letra();
        pie = findViewById(R.id.id_ventas_pie);
        totalvf = findViewById(R.id.txtTotalVR);
        totalva = findViewById(R.id.txtTotalVRA);
        etFecha = findViewById(R.id.etFecha);
        long ahora = System.currentTimeMillis();
        Date fecha = new Date(ahora);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        fechaactual = df.format(fecha);

        // Poner último año, mes y día a la fecha de hoy

        final Calendar calendario = Calendar.getInstance();
        ultimoAnio = calendario.get(Calendar.YEAR);
        ultimoMes = calendario.get(Calendar.MONTH);
        ultimoDiaDelMes = calendario.get(Calendar.DAY_OF_MONTH);

        // Refrescar la fecha en el EditText
        refrescarFechaEnEditText();

        // Hacer que el datepicker se muestre cuando toquen el EditText; recuerda
        // que se podría invocar en el click de cualquier otro botón, o en cualquier
        // otro evento
        ((ImageButton) findViewById(R.id.bntBTReimpresion)).setEnabled(false);

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí es cuando dan click así que mostramos el DatePicker

                // Le pasamos lo que haya en las globales
                DatePickerDialog dialogoFecha = new DatePickerDialog(Listar_ventas.this, listenerDeDatePicker, ultimoAnio, ultimoMes, ultimoDiaDelMes);
                //Mostrar
                dialogoFecha.show();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            idvendedor = bundle.getString("idVendedor");
            vendedor = bundle.getString("Vendedor");
        }

        llenarLista(etFecha.getText().toString());
        llenarListaDetalle();
        MostrarTotal(etFecha.getText().toString());
        MostrarAnulados(etFecha.getText().toString());
        onClick();

        etFecha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lista.removeAll(lista);
                llenarLista(s.toString());
                MostrarTotal(etFecha.getText().toString());
                MostrarAnulados(etFecha.getText().toString());
            }
        });

        findViewById(R.id.bntBTReimpresion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openBT();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btnImprimirReimpresion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendData();
                    ((Button) findViewById(R.id.btnImprimirReimpresion)).setVisibility(View.INVISIBLE);
                    //((ImageButton)findViewById(R.id.bntBTReimpresion)).setEnabled(false);
                    ((ImageButton) findViewById(R.id.bntBTReimpresion)).setVisibility(View.INVISIBLE);
                    ((Button) findViewById(R.id.btnRImpDuplicado)).setVisibility(View.VISIBLE);
                } catch (Exception eo) {
                }
            }
        });

        findViewById(R.id.btnRImpDuplicado).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataDuplicado();
                ((Button) findViewById(R.id.btnRImpDuplicado)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.btnfinalReimpresion)).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btnfinalReimpresion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    closeBT();
                    findViewById(R.id.layoutReimpresion).setVisibility(View.GONE);
                    finish();
                } catch (Exception e) {

                }
            }
        });
    }

    public void onClick() {
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ventaseleccionado = position;
                mActionMode = Listar_ventas.this.startActionMode(amc);
                view.setSelected(true);
                return true;
            }
        });
    }

    private ActionMode.Callback amc = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.opciones_del2, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_eliminar) {
                AlertaEliminacion();
                mode.finish();
            }
            if (item.getItemId() == R.id.item_reimprimir) {
                AlertaReimpresion();
                mode.finish();
                ((ImageButton) findViewById(R.id.bntBTReimpresion)).setEnabled(true);
            }
            if (item.getItemId() == R.id.item_refacturar) {
                //Toast.makeText(getApplicationContext(),"contado",Toast.LENGTH_LONG).show();
                AlertaRefacturacionContado();
                mode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }


    };

    private void AlertaEliminacion() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("¿Esta seguro que desea anular este COMPROBANTE de venta?");
        alertDialog.setTitle("ANULACIÓN DE COMPROBANTE");
        alertDialog.setIcon(R.drawable.ic_descartar);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("SI, ANULAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                eliminarVenta();
            }
        });
        alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void AlertaRefacturacionContado() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("¿Esta seguro que desea generar un nuevo COMPROBANTE reutilizando los detalles de transacción de esta venta?");
        alertDialog.setTitle("RE-FACTURACIÓN DE VENTA");
        alertDialog.setIcon(R.drawable.vender48);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("SI, RE-FACTURAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                irVenta();
            }
        });
        alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void AlertaReimpresion() {
        ObtenerVentaParaReimpresion();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("¿Esta seguro que desea re-imprimir el siguiente comprobante de venta?\n\nINFORMACIÓN:\n" +
                "- FACTURA " + listaparaReimpresion.get(0).getCondicion() + " N° " + listaparaReimpresion.get(0).getEstablecimiento() + "-" + listaparaReimpresion.get(0).getPuntoemision() + "-" + listaparaReimpresion.get(0).getFactura() + "\n" +
                "- CLIENTE: " + listaparaReimpresion.get(0).getClienteRZ() + "\n" +
                "- RUC: " + listaparaReimpresion.get(0).getClienteRUC());
        alertDialog.setTitle("RE-IMPRESIÓN DE COMPROBANTE");
        alertDialog.setIcon(R.drawable.ic_print);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("RE-IMPRIMIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                findViewById(R.id.lv_listarventas).setEnabled(false);
                try {
                    findBT();
                    findViewById(R.id.layoutReimpresion).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }
        });
        alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                listaparaReimpresion.removeAll(listaparaReimpresion);
            }

        });
        alertDialog.show();
    }

    public void eliminarVenta() {
        try {
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            Ventas ventas = lista.get(ventaseleccionado);
            db.openWritable();
            Cursor eliminado = db.VentaEliminado(ventas.getId());
            if (eliminado.getCount() == 0) {
                Snackbar.make(findViewById(R.id.linearLayout31), "NO PROCESADO:\n" +
                                "Este comprobante de venta ya se encuentra anulado.", Snackbar.LENGTH_SHORT)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(Color.parseColor("#FF6F00"))
                        .setActionTextColor(Color.parseColor("#FFFFFF")).show();
            } else {
                long resultado = db.EliminarVenta(ventas.getId());
                if (resultado > 0) {
                    Snackbar.make(findViewById(R.id.linearLayout31), "PROCESO REALIZADO:\n" +
                                    "La venta ha sido anulada satisfactoriamente.", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(Color.parseColor("#FF6F00"))
                            .setActionTextColor(Color.parseColor("#FFFFFF")).show();
                    lista.removeAll(lista);
                    llenarLista(etFecha.getText().toString());
                    MostrarTotal(etFecha.getText().toString());
                    MostrarAnulados(etFecha.getText().toString());
                } else {
                    //Toast.makeText(getApplicationContext(),"Se produjo un error al anular la venta", Toast.LENGTH_LONG).show();
                    Snackbar.make(findViewById(R.id.linearLayout31), "ERRON EN PROCESO:\n" +
                                    "Se produjo un error al intentar anular esta venta.", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(Color.parseColor("#FF6F00"))
                            .setActionTextColor(Color.parseColor("#FFFFFF")).show();
                }
            }

            db.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Fatal: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void llenarLista(String fechaactual) {
        try {
            lv = (ListView) findViewById(R.id.lv_listarventas);
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            db.openWritable();
            Cursor c = db.getv_venta(fechaactual);
            if (c.moveToFirst()) {
                do {
                    lista.add(new Ventas(c.getInt(0), c.getInt(13), c.getInt(14), c.getInt(15),
                            c.getInt(16), c.getString(2), c.getString(3), c.getString(4),
                            c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(11),
                            c.getString(12), c.getString(18), c.getString(19), c.getInt(10),
                            c.getInt(17), c.getInt(5), c.getInt(1), c.getString(20)));
                } while (c.moveToNext());
            }
            adaptadorVenta = new Adaptador_ventas(this, lista);
            //adaptadorVenta.notifyDataSetChanged();
            lv.setAdapter(adaptadorVenta);
            int cant = lv.getCount();
            if (cant == 0) {
                pie.setText("Lista vacía");
            } else if (cant == 1) {
                pie.setText(cant + " venta listada");
            } else {
                pie.setText(cant + " ventas listadas");
            }
            db.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error cargando lista: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarListaDetalle() {
        try {
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            db.openWritable();
            Cursor c = db.getDetalleSync();
            if (c.moveToFirst()) {
                do {
                    listaDetalle.add(new DetalleVentaSync(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3),
                            c.getInt(4), c.getInt(5), c.getInt(6), c.getString(7), c.getString(8)));
                } while (c.moveToNext());
            }
            db.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error cargando listaDetalle: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void MostrarTotal(String fechaactual) {
        Access_Venta db = Access_Venta.getInstance(getApplicationContext());
        db.openWritable();
        Cursor c = db.getTotal(fechaactual);
        if (c.moveToNext()) {
            totalvf.setText("ACTIVOS: " + c.getInt(0));
        } else {
            totalvf.setText("");
        }

    }

    public void MostrarAnulados(String fechaactual) {
        Access_Venta db = Access_Venta.getInstance(getApplicationContext());
        db.openWritable();
        Cursor c = db.getTotalA(fechaactual);
        if (c.moveToNext()) {
            totalva.setText("ANULADOS: " + c.getInt(0));
        } else {
            totalva.setText("");
        }

    }

    public void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                //myLabel.setText("No hay adaptador bluetooth disponible.");
            }
            if (mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.startActivity(enableBluetooth);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    Log.e("device: ", device.getName());
                    if (device.getName().equals("MTP-III")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            if (mmDevice == null) {
                // myLabel.setText("No fue posible conectar con la impresora intente de nuevo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openBT() {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            try {
                beginListenForData();
            } catch (Exception e) {
            }
            ((Button) findViewById(R.id.btnImprimirReimpresion)).setVisibility(View.VISIBLE);
            ((ImageButton) findViewById(R.id.bntBTReimpresion)).setImageResource(R.drawable.ic_bluetooth_connected);
        } catch (Exception e) {
            Log.e("openBT: ", e.getMessage());
        }
    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 100;
            //final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            //readBuffer = new byte[40960];
            readBuffer = new byte[20480];
            // readBuffer = new byte[1024];
            workerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(
                                            readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length
                                    );
                                    // specify US-ASCII encoding
                                    final String data = new String(encodedBytes, "iso-8859-1");

                                    readBufferPosition = 0;
                                    // tell the user data were sent to bluetooth printer device
                                    handler.post(() -> {
                                        //lblPrinterName.setText(data);
                                    });

                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (IOException ex) {
                        stopWorker = true;
                    }

                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getContext(),"beginListenForData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    /*public void sendData(){
        try {
            if (mmSocket != null) {
                try {
                                String msg = "\n";
                                msg += "\n";
                                msg += "          "+listaparaReimpresion.get(0).getEmpresaRZ()+"\n";
                                msg += "     VENTAS DE LACTEOS LACTOLANDA\n";
                                msg += "            RUC: "+listaparaReimpresion.get(0).getEmpresaRUC()+"\n";
                                msg += "   Cel: "+listaparaReimpresion.get(0).getEmpresaTelefono()+"\n";
                                msg += " "+listaparaReimpresion.get(0).getEmpresaDireccion()+"\n";
                                msg += "        CNEL. OVIEDO - PARAGUAY"+"\n";
                                msg += "--------------------------------------\n";
                                msg += "        Timbrado N: "+listaparaReimpresion.get(0).getTimbrado()+"\n";
                                msg += "  Vigencia: "+listaparaReimpresion.get(0).getDesde()+" - "+listaparaReimpresion.get(0).getHasta()+"\n";
                                msg += "             IVA INCLUIDO      \n";
                                msg += "--------------------------------------\n";
                                msg += "Factura "+listaparaReimpresion.get(0).getCondicion()+" Nro: "+listaparaReimpresion.get(0).getEstablecimiento()+"-"+listaparaReimpresion.get(0).getPuntoemision()+"-"+listaparaReimpresion.get(0).getFactura()+"\n";
                                msg += "Fecha/Hora: "+listaparaReimpresion.get(0).getFecha()+" "+listaparaReimpresion.get(0).getHora()+"\n";
                                msg += "Vendedor: "+listaparaReimpresion.get(0).getVendedor()+"\n";
                                msg += "--------------------------------------\n";
                                msg += "CLIENTE: "+listaparaReimpresion.get(0).getClienteRZ()+"\n";
                                msg += "RUC/CI: "+listaparaReimpresion.get(0).getClienteRUC()+"\n";
                                msg += "--------------------------------------\n";
                                msg += String.format("%1$1s %2$8s %3$1s %4$9s %5$12s", "IVA", "CANT","", "PRECIO","   SUBTOTAL" );
                                msg += "\n";
                                msg += "--------------------------------------\n";
                                for(int i=1; i<=listaparaReimpresion.size();i++) {
                                    int position = (i - 1);
                                    ReimpresionVenta item = listaparaReimpresion.get(position);
                                    //msg += String.format("%1$-1s" , item.getCodBarra()+" "+item.getProducto()+"\n");
                                    msg += String.format("%1$-1s" , item.getCodBarra()+"\n");
                                    msg += String.format("%1$-1s" , item.getProducto()+"\n");
                                    msg += String.format("%1$-7s %2$-9s %3$-10s %4$-9s" , item.getImpuesto()+"%",item.getCantidad()+" "+item.getUm(), item.getPrecio(), item.getTotal());
                                }
                                msg +="\n";
                                msg += "--------------------------------------\n";
                                msg += "TOTAL                        "+listaparaReimpresion.get(0).getTotalfinal()+"\n";
                                msg += "--------------------------------------\n";
                                msg += "EFECTIVO: 0 \n";
                                msg += "VUELTO:   0"+"\n";
                                msg += "\n";
                                msg += "*************** TOTALES **************"+"\n";
                                msg += "EXENTAS   ---->              "+listaparaReimpresion.get(0).getExenta()+"\n";
                                msg += "GRAV. 5%  ---->              "+listaparaReimpresion.get(0).getCinco()+"\n";
                                msg += "GRAV. 10% ---->              "+listaparaReimpresion.get(0).getDiez()+"\n\n";
                                msg += "******** LIQUIDACION DEL IVA  ********"+"\n";
                                msg += "IVA 5%    ---->              "+Math.round(Double.parseDouble(listaparaReimpresion.get(0).getCinco())/21)+"\n";
                                msg += "IVA 10%   ---->              "+Math.round(Double.parseDouble(listaparaReimpresion.get(0).getDiez())/11)+"\n";
                                msg += "--------------------------------------\n";
                                long totaliva=(Math.round(Double.parseDouble(listaparaReimpresion.get(0).getCinco())/21)+Math.round(Double.parseDouble(listaparaReimpresion.get(0).getDiez())/11));
                                msg += "TOTAL IVA                    "+totaliva+"\n";
                                msg += "--------------------------------------\n";
                                msg += "Original:  Cliente\n";
                                msg += "Duplicado: Archivo Tributario\n";
                                msg += "\n       GRACIAS POR SU PREFERENCIA\n";
                                msg += "\n\n";

                                //myLabel.setText("Espere que finalice la impresion para cerrar.");
                                // Para que acepte caracteres espciales
                               // mmOutputStream.write(0x1C); mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                                //mmOutputStream.write(0x1B); mmOutputStream.write(0x74); mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252

                                mmOutputStream.write(getByteString(msg,0,0,0,0));
                                //mmOutputStream.write(msg.getBytes());
                                mmOutputStream.write("\n\n".getBytes());

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al interntar imprimir texto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("MENSAJE: ", "Socket nulo");
                //myLabel.setText("Impresora no conectada");
            }

        } catch (Exception e) {
            //Toast.makeText(getContext(),"sendData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }*/
    public void sendData() {
        try {
            if (mmSocket != null) {
                try {
                    //String msg = "\n";
                    //msg += "\n";
                    DecimalFormat nformat = new DecimalFormat("##,###,###");
                    String msg = listaparaReimpresion.get(0).getEmpresaRZ() + "\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg, 0, 0, 0, 1));
                    String msg1 = "VENTAS DE LACTEOS LACTOLANDA\n";
                    msg1 += "RUC: " + listaparaReimpresion.get(0).getEmpresaRUC() + "\n";
                    msg1 += "CEL: " + listaparaReimpresion.get(0).getEmpresaTelefono() + "\n";
                    msg1 += listaparaReimpresion.get(0).getEmpresaDireccion() + "\n";
                    msg1 += "VILLARRICA - PARAGUAY\n";
                    msg1 += "I.V.A. INCLUIDO\n";
                    msg1 += "------------------------------------------------\n";//48
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg1, 0, 0, 0, 0));
                    String msg2 = "TIMBRADO: " + listaparaReimpresion.get(0).getTimbrado() + "\n";
                    msg2 += "VALIDO DESDE: " + listaparaReimpresion.get(0).getDesde() + " HASTA: " + listaparaReimpresion.get(0).getHasta() + "\n";
                    msg2 += "FACTURA " + listaparaReimpresion.get(0).getCondicion() + " NRO: " + listaparaReimpresion.get(0).getEstablecimiento() + "-" + listaparaReimpresion.get(0).getPuntoemision() + "-" + listaparaReimpresion.get(0).getFactura() + "\n";
                    msg2 += "FECHA/HORA: " + listaparaReimpresion.get(0).getFecha() + " " + listaparaReimpresion.get(0).getHora() + "\n";
                    msg2 += "VENDEDOR: " + listaparaReimpresion.get(0).getVendedor() + "\n";
                    msg2 += "\n";
                    msg2 += "CLIENTE: " + listaparaReimpresion.get(0).getClienteRZ() + "\n";
                    msg2 += "RUC/CI: " + listaparaReimpresion.get(0).getClienteRUC() + "\n";
                    msg2 += "------------------------------------------------\n";
                    msg2 += String.format("%1$1s %2$10s %3$1s %4$12s %5$17s", "IVA", "CANT", "", "PRECIO", "   SUB-TOTAL");
                    msg2 += "\n";
                    msg2 += "------------------------------------------------\n";
                    for (int i = 1; i <= listaparaReimpresion.size(); i++) {
                        int position = (i - 1);
                        ReimpresionVenta item = listaparaReimpresion.get(position);
                        //msg2 += String.format("%1$-1s" , item.getCodBarra()+"/"+item.getProducto()+"\n");
                        msg2 += String.format("%1$-1s", item.getCodBarra() + " " + item.getPromo() + "\n");
                        msg2 += String.format("%1$-1s", item.getProducto() + "\n");
                        msg2 += String.format("%1$-9s %2$-12s %3$-15s %4$-9s", item.getImpuesto() + "%", item.getCantidad() + " " + item.getUm(), nformat.format(item.getPrecio()), nformat.format(item.getTotal()));
                    }
                    msg2 += "\n";
                    mmOutputStream.write(ESC_ALIGN_LEFT);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg2, 0, 0, 0, 0));
                    String msg3 = "------------------------\n";

                    msg3 += "TOTAL Gs." + nformat.format(Integer.parseInt(listaparaReimpresion.get(0).getTotalfinal())) + "\n";
                    msg3 += "------------------------\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg3, 0, 0, 1, 1));
                    String msg4 = d.Convertir(listaparaReimpresion.get(0).getTotalfinal(), true) + "\n";
                    msg4 += "------------------------------------------------\n";
                    msg4 += "\n";
                    msg4 += "TOTALES GRAVADA" + "\n";
                    msg4 += "EXENTAS   -------->              " + nformat.format(Integer.parseInt(listaparaReimpresion.get(0).getExenta())) + "\n";
                    msg4 += "GRAV.  5% -------->              " + nformat.format(Integer.parseInt(listaparaReimpresion.get(0).getCinco())) + "\n";
                    msg4 += "GRAV. 10% -------->              " + nformat.format(Integer.parseInt(listaparaReimpresion.get(0).getDiez())) + "\n\n";
                    msg4 += "LIQUIDACION DEL I.V.A." + "\n";
                    msg4 += "IVA 5%    -------->              " + nformat.format(Math.round(Double.parseDouble(listaparaReimpresion.get(0).getCinco()) / 21)) + "\n";
                    msg4 += "IVA 10%   -------->              " + nformat.format(Math.round(Double.parseDouble(listaparaReimpresion.get(0).getDiez()) / 11)) + "\n";
                    mmOutputStream.write(ESC_ALIGN_LEFT);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg4, 0, 0, 0, 0));
                    String msg5 = "------------------------------------------------\n";
                    long totaliva = (Math.round(Double.parseDouble(listaparaReimpresion.get(0).getCinco()) / 21) + Math.round(Double.parseDouble(listaparaReimpresion.get(0).getDiez()) / 11));
                    msg5 += "TOTAL I.V.A.: " + nformat.format(Integer.parseInt(String.valueOf(totaliva))) + "\n";
                    msg5 += "------------------------------------------------\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg5, 0, 0, 0, 0));
                    String msg6 = "EFECTIVO: 0 \n";
                    msg6 += "VUELTO:   0" + "\n";
                    msg6 += "\n";
                    msg6 += "ORIGINAL: CLIENTE\n";
                    msg6 += "\n";
                    msg6 += "\n";
                    mmOutputStream.write(ESC_ALIGN_LEFT);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg6, 0, 0, 0, 0));
                    String msg7 = listaparaReimpresion.get(0).getEmpresaRZ() + "\n";
                    msg7 += "AGRADECE SU PREFERENCIA";
                    msg7 += "\n\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg7, 0, 0, 0, 0));
                    mmOutputStream.write("\n\n".getBytes());

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al interntar imprimir texto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("MENSAJE: ", "Socket nulo");
                //myLabel.setText("Impresora no conectada");
            }

        } catch (Exception e) {
            //Toast.makeText(getContext(),"sendData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void sendDataDuplicado() {
        try {
            if (mmSocket != null) {
                try {
                    //String msg = "\n";
                    //msg += "\n";
                    DecimalFormat nformat = new DecimalFormat("##,###,###");
                    String msg = listaparaReimpresion.get(0).getEmpresaRZ() + "\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg, 0, 0, 0, 1));
                    String msg1 = "VENTAS DE LACTEOS LACTOLANDA\n";
                    msg1 += "RUC: " + listaparaReimpresion.get(0).getEmpresaRUC() + "\n";
                    msg1 += "CEL: " + listaparaReimpresion.get(0).getEmpresaTelefono() + "\n";
                    msg1 += listaparaReimpresion.get(0).getEmpresaDireccion() + "\n";
                    msg1 += "VILLARRICA - PARAGUAY\n";
                    msg1 += "I.V.A. INCLUIDO\n";
                    msg1 += "------------------------------------------------\n";//48
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg1, 0, 0, 0, 0));
                    String msg2 = "TIMBRADO: " + listaparaReimpresion.get(0).getTimbrado() + "\n";
                    msg2 += "VALIDO DESDE: " + listaparaReimpresion.get(0).getDesde() + " HASTA: " + listaparaReimpresion.get(0).getHasta() + "\n";
                    msg2 += "FACTURA " + listaparaReimpresion.get(0).getCondicion() + " NRO: " + listaparaReimpresion.get(0).getEstablecimiento() + "-" + listaparaReimpresion.get(0).getPuntoemision() + "-" + listaparaReimpresion.get(0).getFactura() + "\n";
                    msg2 += "FECHA/HORA: " + listaparaReimpresion.get(0).getFecha() + " " + listaparaReimpresion.get(0).getHora() + "\n";
                    msg2 += "VENDEDOR: " + listaparaReimpresion.get(0).getVendedor() + "\n";
                    msg2 += "\n";
                    msg2 += "CLIENTE: " + listaparaReimpresion.get(0).getClienteRZ() + "\n";
                    msg2 += "RUC/CI: " + listaparaReimpresion.get(0).getClienteRUC() + "\n";
                    msg2 += "------------------------------------------------\n";
                    msg2 += String.format("%1$1s %2$10s %3$1s %4$12s %5$17s", "IVA", "CANT", "", "PRECIO", "   SUB-TOTAL");
                    msg2 += "\n";
                    msg2 += "------------------------------------------------\n";
                    for (int i = 1; i <= listaparaReimpresion.size(); i++) {
                        int position = (i - 1);
                        ReimpresionVenta item = listaparaReimpresion.get(position);
                        //msg2 += String.format("%1$-1s" , item.getCodBarra()+"/"+item.getProducto()+"\n");
                        msg2 += String.format("%1$-1s", item.getCodBarra() + " " + item.getPromo() + "\n");
                        msg2 += String.format("%1$-1s", item.getProducto() + "\n");
                        msg2 += String.format("%1$-9s %2$-12s %3$-15s %4$-9s", item.getImpuesto() + "%", item.getCantidad() + " " + item.getUm(), nformat.format(item.getPrecio()), nformat.format(item.getTotal()));
                    }
                    msg2 += "\n";
                    mmOutputStream.write(ESC_ALIGN_LEFT);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg2, 0, 0, 0, 0));
                    String msg3 = "------------------------\n";

                    msg3 += "TOTAL Gs." + nformat.format(Integer.parseInt(listaparaReimpresion.get(0).getTotalfinal())) + "\n";
                    msg3 += "------------------------\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg3, 0, 0, 1, 1));
                    String msg4 = d.Convertir(listaparaReimpresion.get(0).getTotalfinal(), true) + "\n";
                    msg4 += "------------------------------------------------\n";
                    msg4 += "\n";
                    msg4 += "TOTALES GRAVADA" + "\n";
                    msg4 += "EXENTAS   -------->              " + nformat.format(Integer.parseInt(listaparaReimpresion.get(0).getExenta())) + "\n";
                    msg4 += "GRAV.  5% -------->              " + nformat.format(Integer.parseInt(listaparaReimpresion.get(0).getCinco())) + "\n";
                    msg4 += "GRAV. 10% -------->              " + nformat.format(Integer.parseInt(listaparaReimpresion.get(0).getDiez())) + "\n\n";
                    msg4 += "LIQUIDACION DEL I.V.A." + "\n";
                    msg4 += "IVA 5%    -------->              " + nformat.format(Math.round(Double.parseDouble(listaparaReimpresion.get(0).getCinco()) / 21)) + "\n";
                    msg4 += "IVA 10%   -------->              " + nformat.format(Math.round(Double.parseDouble(listaparaReimpresion.get(0).getDiez()) / 11)) + "\n";
                    mmOutputStream.write(ESC_ALIGN_LEFT);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg4, 0, 0, 0, 0));
                    String msg5 = "------------------------------------------------\n";
                    long totaliva = (Math.round(Double.parseDouble(listaparaReimpresion.get(0).getCinco()) / 21) + Math.round(Double.parseDouble(listaparaReimpresion.get(0).getDiez()) / 11));
                    msg5 += "TOTAL I.V.A.: " + nformat.format(Integer.parseInt(String.valueOf(totaliva))) + "\n";
                    msg5 += "------------------------------------------------\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg5, 0, 0, 0, 0));
                    String msg6 = "EFECTIVO: 0 \n";
                    msg6 += "VUELTO:   0" + "\n";
                    msg6 += "\n";
                    msg6 += "DUPLICADO: ARCHIVO TRIBUTARIO\n";
                    msg6 += "\n";
                    msg6 += "\n";
                    mmOutputStream.write(ESC_ALIGN_LEFT);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg6, 0, 0, 0, 0));
                    String msg7 = listaparaReimpresion.get(0).getEmpresaRZ() + "\n";
                    msg7 += "AGRADECE SU PREFERENCIA";
                    msg7 += "\n\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg7, 0, 0, 0, 0));
                    mmOutputStream.write("\n\n".getBytes());

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al interntar imprimir texto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("MENSAJE: ", "Socket nulo");
                //myLabel.setText("Impresora no conectada");
            }

        } catch (Exception e) {
            //Toast.makeText(getContext(),"sendData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // close the connection to bluetooth printer.
    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            //myLabel.setText("Bluetooth cerrado");

        } catch (Exception e) {
            e.printStackTrace();
            /*Toast.makeText(getContext(),"closeBT: "+e.getMessage(),Toast.LENGTH_LONG).show();//e.printStackTrace();*/
        }
    }



    /*private static byte[] getByteString(String str) {
        if (str.length() == 0)
            return null;
        byte[] strData = null;
        try {
            strData = str.getBytes("iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] command = new byte[strData.length + 9];
        command[0] = 27;// caracter ESC para darle comandos a la impresora
        command[1] = 69;
        command[3] = 27;
        command[4] = 77;
        command[6] = 29;
        command[7] = 33;
        System.arraycopy(strData, 0, command, 9, strData.length);
        return command;
    }*/

    public static byte[] getByteString(String str, int bold, int font, int widthsize, int heigthsize) {

        if (str.length() == 0 | widthsize < 0 | widthsize > 3 | heigthsize < 0 | heigthsize > 3
                | font < 0 | font > 1)
            return null;

        byte[] strData = null;
        try {
            //strData = str.getBytes("iso-8859-1");
            strData = str.getBytes("US-ASCII");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        byte[] command = new byte[strData.length + 9];

        byte[] intToWidth = {0x00, 0x10, 0x20, 0x30};//
        byte[] intToHeight = {0x00, 0x01, 0x02, 0x03};//

        command[0] = 27;// caracter ESC para darle comandos a la impresora
        command[1] = 69;
        command[2] = ((byte) bold);
        command[3] = 27;
        command[4] = 77;
        command[5] = ((byte) font);
        command[6] = 29;
        command[7] = 33;
        command[8] = (byte) (intToWidth[widthsize] + intToHeight[heigthsize]);

        System.arraycopy(strData, 0, command, 9, strData.length);
        return command;
    }

    public void ObtenerVentaParaReimpresion() {
        try {
            Access_Venta db = Access_Venta.getInstance(getApplicationContext());
            Ventas ventas = lista.get(ventaseleccionado);
            db.openWritable();
            Cursor c = db.getReimpresion(ventas.getId());
            if (c.moveToFirst()) {
                do {
                    listaparaReimpresion.add(new ReimpresionVenta(c.getString(0), c.getString(1), c.getString(2),
                            c.getString(3), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                            c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13),
                            c.getString(14), c.getString(15), c.getString(23), c.getString(24), c.getString(25),
                            c.getString(29), c.getInt(26), c.getInt(27), c.getInt(28), c.getString(16)
                            , c.getString(17), c.getString(18), c.getString(19), c.getString(20), c.getString(30)));
                } while (c.moveToNext());
            }
            db.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error cargando lista para reimpresion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void irVenta() {
        Ventas ventas = lista.get(ventaseleccionado);
        Intent i = new Intent(this, Listar_ClientesVenta.class);
        i.putExtra("R", "R");
        i.putExtra("idVendedor", idvendedor);
        i.putExtra("Vendedor", vendedor);
        i.putExtra("idventa", ventas.getId());
        i.putExtra("idemision", ventas.getIdemision());
        startActivity(i);
        finish();
    }

}