package com.example.fact_express.Actividades;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fact_express.Adaptador_Productos_detalleventa;
import com.example.fact_express.CRUD.Access_Empresa;
import com.example.fact_express.CRUD.Access_Timbrado;
import com.example.fact_express.CRUD.Access_Venta;
import com.example.fact_express.Modelos.DetalleVenta;
import com.example.fact_express.Modelos.Numero_a_Letra;
import com.example.fact_express.Modelos.Ventas;
import com.example.fact_express.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Fragment_DetalleV extends Fragment {
    View vista;

    private static final String ARG_IDVENTA = "idventas";
    private static final String ARG_NESTABLECIMIENTO = "nestablecimiento";
    private static final String ARG_NEXPEDICION = "nemision";
    private static final String ARG_NROFACTURA = "nrofactura";
    private static final String ARG_CONDICION = "condicion";
    private static final String ARG_FECHA = "fecha";
    private static final String ARG_HORA = "hora";
    private static final String ARG_IDCLIENTE = "idcliente";
    private static final String ARG_CRZ = "Vcliente";
    private static final String ARG_CRUC = "Vruc";
    private static final String ARG_IDUSUARIO = "idusuario";
    private static final String ARG_VENDEDOR = "vendedor";
    private static final String ARG_IDTIMBRADO = "idtimbrado";
    private static final String ARG_IDEMISION = "idemision";
    private static final String ARG_M = "M";
    private static final String ARG_IDV = "IDV";
    private static final String ARG_IDE = "IDE";

    public ListView lv;
    public final ArrayList<DetalleVenta> lista = new ArrayList<>();
    public Adaptador_Productos_detalleventa adaptadorProductos;


    private static String codbarra, producto, precio, cantidad, um, ivadescripcion, mensaje;
    private static int id, total, exenta, iva5, iva10;
    private static int exentaT;
    private static int iva5T;
    private static int iva10T;
    private static int totalfinal;


    private static int idventas;
    private static String nestablecimiento;
    private static String nemision;
    private static String nrofactura;
    private static String condicion;
    private static String fecha;
    private static String hora;
    private static int idcliente;
    private static String vcliente;
    private static String vruccliente;
    private static int idusuario;
    private static String vendedor;
    private static int idtimbrado;
    private static int idemision;
    private static String MM;
    private static int IDVV;

    private final int BrutoExenta = 0;
    private final int Bruto5 = 0;
    private final int Bruto10 = 0;


    public String timbActual, fdesde, fhasta;
    public String empRZ, empRUC, empDir, empTel, empCiu;


    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    public TextView myLabel;
    public TextView txtEfectivo, txtVuelto;

    // needed for communication to bluetooth device / network
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

    static private Numero_a_Letra d;

    public Fragment_DetalleV() {
        // Required empty public constructor

    }

    public static Fragment_DetalleV newInstance(int idventas, String nestablecimiento, String nemision, String nrofactura, String condicion, String fecha, String hora,
                                                int idcliente, String cliente, String ruccliente, int idusuario, String vendedor, int idtimbrado, int idemision, String M, int IDV, int IDE) {
        Fragment_DetalleV fragment = new Fragment_DetalleV();
        Bundle args = new Bundle();
        args.putInt(ARG_IDVENTA, idventas);
        args.putString(ARG_NESTABLECIMIENTO, nestablecimiento);
        args.putString(ARG_NEXPEDICION, nemision);
        args.putString(ARG_NROFACTURA, nrofactura);
        args.putString(ARG_CONDICION, condicion);
        args.putString(ARG_FECHA, fecha);
        args.putString(ARG_HORA, hora);
        args.putInt(ARG_IDCLIENTE, idcliente);
        args.putString(ARG_CRZ, cliente);
        args.putString(ARG_CRUC, ruccliente);
        args.putInt(ARG_IDUSUARIO, idusuario);
        args.putString(ARG_VENDEDOR, vendedor);
        args.putInt(ARG_IDTIMBRADO, idtimbrado);
        args.putInt(ARG_IDEMISION, idemision);
        args.putString(ARG_M, M);
        args.putInt(ARG_IDV, IDV);
        args.putInt(ARG_IDE, IDE);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idventas = getArguments().getInt(ARG_IDVENTA);
            nestablecimiento = getArguments().getString(ARG_NESTABLECIMIENTO);
            nemision = getArguments().getString(ARG_NEXPEDICION);
            nrofactura = getArguments().getString(ARG_NROFACTURA);
            condicion = getArguments().getString(ARG_CONDICION);
            fecha = getArguments().getString(ARG_FECHA);
            hora = getArguments().getString(ARG_HORA);
            idcliente = getArguments().getInt(ARG_IDCLIENTE);
            vcliente = getArguments().getString(ARG_CRZ);
            vruccliente = getArguments().getString(ARG_CRUC);
            idusuario = getArguments().getInt(ARG_IDUSUARIO);
            vendedor = getArguments().getString(ARG_VENDEDOR);
            idtimbrado = getArguments().getInt(ARG_IDTIMBRADO);
            idemision = getArguments().getInt(ARG_IDEMISION);
            IDVV = getArguments().getInt(ARG_IDV);
            MM = getArguments().getString(ARG_M);
        }
        d = new Numero_a_Letra();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment__detalle_v2, container, false);
        myLabel = (TextView) vista.findViewById(R.id.myLabel);
        lv = vista.findViewById(R.id.id_listadetallev);
        txtEfectivo = (TextView) vista.findViewById(R.id.id_txtefectivo);
        txtVuelto = (TextView) vista.findViewById(R.id.it_txtvuelto);
        exentaT = 0;
        iva5T = 0;
        iva10T = 0;
        totalfinal = 0;

        if (MM.equals("R")) {
            int exentaR = 0;
            int iva5R = 0;
            int iva10R = 0;
            Access_Venta db = Access_Venta.getInstance(getContext());
            db.openWritable();
            Cursor c = db.getReimpresion2(IDVV);
            if (c.moveToFirst()) {
                do {
                    int idR = c.getInt(0);
                    String codbarraR = c.getString(1);
                    String productoR = c.getString(2);
                    String precioR = String.valueOf(c.getInt(3));
                    String cantidadR = c.getString(4);
                    String umR = c.getString(5);
                    int totalR = c.getInt(6);
                    String ivadescripcionR = String.valueOf(c.getInt(7));
                    if (Integer.parseInt(ivadescripcionR) == 0) {
                        exentaR = totalR;
                        iva5R = 0;
                        iva10R = 0;
                    } else if (Integer.parseInt(ivadescripcionR) == 5) {
                        exentaR = 0;
                        iva5R = totalR;
                        iva10R = 0;
                    } else if (Integer.parseInt(ivadescripcionR) == 10) {
                        exentaR = 0;
                        iva5R = 0;
                        iva10R = totalR;
                    }
                    String mensajeR = c.getString(8);

                    obtenerEmpresa();
                    obtenerTimbrado();
                    cargarItems2(idR, codbarraR, productoR, precioR, cantidadR, umR, totalR, ivadescripcionR, exentaR, iva5R, iva10R, mensajeR);
                } while (c.moveToNext());
                db.close();
            }
        }

        return vista;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull final View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("enviar", this, (requestKey, result) -> {
            if (!result.isEmpty()) {
                id = Integer.parseInt(result.getString("id"));
                codbarra = result.getString("codbarra");
                producto = result.getString("producto");
                precio = result.getString("precio");
                cantidad = result.getString("cantidad");
                um = result.getString("um");
                total = result.getInt("total");
                ivadescripcion = result.getString("ivadescripcion");
                exenta = result.getInt("exenta");
                iva5 = result.getInt("iva5");
                iva10 = result.getInt("iva10");
                mensaje = result.getString("mensaje");

                obtenerEmpresa();
                obtenerTimbrado();
                cargarItems();
            }

        });


        view.findViewById(R.id.btn_idFVenta).setOnClickListener(v -> {
            bajarTeclado();
            int vuelto = Integer.parseInt(txtVuelto.getText().toString());
            if (vuelto < 0) {
                Snackbar.make(vista.findViewById(R.id.linearLayout66), "NO SE PUEDE GUARDAR LA VENTA.\n" +
                                "Verifique los montos de efectivo/vuelto e intente de nuevo.", Snackbar.LENGTH_SHORT)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(Color.parseColor("#FF6F00"))
                        .setActionTextColor(Color.parseColor("#FFFFFF")).show();
            } else {
                if (lista.size() == 0) {
                    Snackbar.make(vista.findViewById(R.id.linearLayout66), "EL CARRITO DE VENTA SE ENCUETRA VACIO.\n" +
                                    "Cargue los productos a vender y vuelve a intentarlo.", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(Color.parseColor("#FF6F00"))
                            .setActionTextColor(Color.parseColor("#FFFFFF")).show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setMessage("¿Esta seguro que desea registrar esta venta y proceder a emitir el comprobante legal?");
                    alertDialog.setTitle("REGISTRO DE VENTA");
                    alertDialog.setIcon(R.drawable.vender48);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("SI, GUARDAR VENTA", (dialog, which) -> {
                        Guardar();
                        txtEfectivo.setEnabled(false);
                        try {
                            findBT();
                        } catch (Exception i) {
                            Snackbar.make(vista.findViewById(R.id.linearLayout66), "No se encuetra ninguna impresora BT", Snackbar.LENGTH_SHORT)
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                    .setBackgroundTint(Color.parseColor("#FF6F00"))
                                    .setActionTextColor(Color.parseColor("#FFFFFF")).show();
                        }
                        ((LinearLayout) view.findViewById(R.id.idLLyBT)).setVisibility(View.VISIBLE);
                    });
                    alertDialog.setNegativeButton("CANCELAR", (dialog, which) -> dialog.cancel());
                    alertDialog.show();
                }
            }

        });

        ((ImageButton) view.findViewById(R.id.btnBT)).setOnClickListener(v -> {
            try {
                openBT();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ((Button) view.findViewById(R.id.btnImprimir)).setOnClickListener(v -> {
            try {
                sendData();
                ((Button) view.findViewById(R.id.btnImprimir)).setVisibility(View.INVISIBLE);
                ((LinearLayout) view.findViewById(R.id.idLLyBT)).setVisibility(View.INVISIBLE);
                ((Button) view.findViewById(R.id.btn_impDuplicado)).setVisibility(View.VISIBLE);
            } catch (Exception eo) {
            }
        });
        ((Button) view.findViewById(R.id.btn_impDuplicado)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataDuplicado();
                ((Button) view.findViewById(R.id.btn_impDuplicado)).setVisibility(View.INVISIBLE);
                ((ImageButton) view.findViewById(R.id.btnBT)).setEnabled(false);
                ((Button) view.findViewById(R.id.btnfinal)).setVisibility(View.VISIBLE);
            }
        });


        ((Button) view.findViewById(R.id.btnfinal)).setOnClickListener(v -> {
            try {
                closeBT();
                getActivity().finish();
            } catch (Exception e) {

            }
        });
        lv.setOnItemLongClickListener((parent, view1, position, id) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setMessage("¿Esta seguro que desea excluir este item?.\nEsto modificara su carrito de facturación y los valores relacionados a la venta?");
            alertDialog.setTitle("ELIMINAR PRODUCTO DEL CARRITO");
            alertDialog.setIcon(R.drawable.deletes);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("SI, QUITAR PRODUCTO", (dialog, which) -> {
                DetalleVenta item = lista.get(position);
                lista.remove(position);
                calcularExentaRemove(item.getExenta());
                calcular5Remove(item.getIva5());
                calcular10Remove(item.getIva10());
                calculartotalventaRemove(item.getTotal());
                adaptadorProductos = new Adaptador_Productos_detalleventa(getContext(), lista);
                adaptadorProductos.notifyDataSetChanged();
                lv.setAdapter(adaptadorProductos);
                view1.setSelected(true);
            });
            alertDialog.setNegativeButton("CANCELAR", (dialog, which) -> dialog.cancel());
            alertDialog.show();

            return true;
        });

        txtEfectivo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (txtEfectivo.getText().toString().trim().isEmpty()) {
                        txtVuelto.setText("0");
                    } else {
                        DecimalFormat nformat = new DecimalFormat("##,###,###");
                        int efectivo = Integer.parseInt(txtEfectivo.getText().toString());
                        String total = ((TextView) vista.findViewById(R.id.idtotalfinal)).getText().toString().replace(".", "").replace(",", "");
                        int vuelto = efectivo - Integer.parseInt(total);
                        txtVuelto.setText(String.valueOf(nformat.format(vuelto)));
                    }

                } catch (Exception e) {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void cargarItems() {
        try {
            lista.add(new DetalleVenta(id, codbarra, producto, precio, cantidad, um, total, ivadescripcion, exenta, iva5, iva10, mensaje));
            adaptadorProductos = new Adaptador_Productos_detalleventa(getContext(), lista);
            adaptadorProductos.notifyDataSetChanged();
            lv.setAdapter(adaptadorProductos);
            calcularExenta();
            calcular5();
            calcular10();
            calculartotalventa();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error cargando lista: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarItems2(int idR, String codbarraR, String productoR, String precioR, String cantidadR, String umR, int totalR, String ivadescripcionR, int exentaR, int iva5R, int iva10R, String mensajeR) {
        try {
            lista.add(new DetalleVenta(idR, codbarraR, productoR, precioR, cantidadR, umR, totalR, ivadescripcionR, exentaR, iva5R, iva10R, mensajeR));
            adaptadorProductos = new Adaptador_Productos_detalleventa(getContext(), lista);
            adaptadorProductos.notifyDataSetChanged();
            lv.setAdapter(adaptadorProductos);
            calcularExenta2(exentaR);
            calcular52(iva5R);
            calcular102(iva10R);
            calculartotalventa2(totalR);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error cargando lista: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void calcularExenta() {
        exentaT = exentaT + exenta;
        ((TextView) vista.findViewById(R.id.idtotalexenta)).setText(String.valueOf(exentaT));
    }

    private void calcularExenta2(int exentaR) {
        exentaT = exentaT + exentaR;
        ((TextView) vista.findViewById(R.id.idtotalexenta)).setText(String.valueOf(exentaT));
    }

    private void calcularExentaRemove(int remove) {
        exentaT = exentaT - remove;
        ((TextView) vista.findViewById(R.id.idtotalexenta)).setText(String.valueOf(exentaT));
    }

    private void calcular5() {
        iva5T = iva5T + iva5;
        ((TextView) vista.findViewById(R.id.idtotal5)).setText(String.valueOf(iva5T));
    }

    private void calcular52(int iva5R) {
        iva5T = iva5T + iva5R;
        ((TextView) vista.findViewById(R.id.idtotal5)).setText(String.valueOf(iva5T));
    }

    private void calcular5Remove(int remove) {
        iva5T = iva5T - remove;
        ((TextView) vista.findViewById(R.id.idtotal5)).setText(String.valueOf(iva5T));
    }

    private void calcular10() {
        iva10T = iva10T + iva10;
        ((TextView) vista.findViewById(R.id.idtotal10)).setText(String.valueOf(iva10T));
    }

    private void calcular102(int iva10R) {
        iva10T = iva10T + iva10R;
        ((TextView) vista.findViewById(R.id.idtotal10)).setText(String.valueOf(iva10T));
    }

    private void calcular10Remove(int remove) {
        iva10T = iva10T - remove;
        ((TextView) vista.findViewById(R.id.idtotal10)).setText(String.valueOf(iva10T));
    }

    private void calculartotalventa() {
        DecimalFormat nformat = new DecimalFormat("##,###,###");
        totalfinal += total;
        ((TextView) vista.findViewById(R.id.idtotalfinal)).setText(String.valueOf(nformat.format(totalfinal)));
    }

    private void calculartotalventa2(int totalR) {
        DecimalFormat nformat = new DecimalFormat("##,###,###");
        totalfinal += totalR;
        ((TextView) vista.findViewById(R.id.idtotalfinal)).setText(String.valueOf(nformat.format(totalfinal)));
    }

    private void calculartotalventaRemove(int remove) {
        DecimalFormat nformat = new DecimalFormat("##,###,###");
        totalfinal -= remove;
        ((TextView) vista.findViewById(R.id.idtotalfinal)).setText(String.valueOf(nformat.format(totalfinal)));
    }

    private void Guardar() {
        Access_Venta db = Access_Venta.getInstance(getContext());
        db.openWritable();
        long insertarventa = db.insertarventa(idventas, nrofactura, condicion, fecha, hora,
                Integer.parseInt(((TextView) vista.findViewById(R.id.idtotalfinal)).getText().toString().replace(".", "").replace(",", "")),
                Integer.parseInt(((TextView) vista.findViewById(R.id.idtotalexenta)).getText().toString())
                , Integer.parseInt(((TextView) vista.findViewById(R.id.idtotal5)).getText().toString()),
                Integer.parseInt(((TextView) vista.findViewById(R.id.idtotal10)).getText().toString()),
                idcliente, idusuario, idtimbrado, idemision);
        if (insertarventa > 0) {
            //Toast.makeText(getContext(),"Venta registrada!",Toast.LENGTH_SHORT).show();
            Snackbar.make(vista.findViewById(R.id.linearLayout66), "VENTA REGISTRADA EXITOSAMENTE!.\n" +
                            "Puede proceder a imprimir el comprobante.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(Color.parseColor("#FF6F00"))
                    .setActionTextColor(Color.parseColor("#FFFFFF")).show();
            db.ActualizarOP(idemision, idventas);
            db.ActualizarFacturaActual(Integer.parseInt(nrofactura), idemision);
            GuardarDetalleVenta();
        } else {
            //Toast.makeText(getContext(),"No se pudo registra la venta",Toast.LENGTH_SHORT).show();
            Snackbar.make(vista.findViewById(R.id.linearLayout66), "Error registrado nueva venta.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(Color.parseColor("#FF6F00"))
                    .setActionTextColor(Color.parseColor("#FFFFFF")).show();
        }
        db.close();
    }

    private void GuardarDetalleVenta() {
        Access_Venta db = Access_Venta.getInstance(getContext());
        db.openWritable();
        for (int i = 1; i <= lista.size(); i++) {
            int position = (i - 1);
            DetalleVenta item = lista.get(position);
            try {
                long insertarDetalle = db.insertarDetalle(idventas, idemision, item.getIdproducto(), item.getCantidad(),
                        Integer.parseInt(item.getPrecio()), item.getTotal(), Integer.parseInt(item.getIvadescripcion()), item.getUm(), item.getMensaje());
                Log.i("detalle: ", String.valueOf(insertarDetalle));
            } catch (Exception e) {
                Toast.makeText(getContext(), "FATAL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        vista.findViewById(R.id.btn_idFVenta).setVisibility(View.INVISIBLE);
        vista.findViewById(R.id.btnBT).setVisibility(View.VISIBLE);
        db.close();
    }

    private void obtenerTimbrado() {
        Access_Timbrado tim = new Access_Timbrado(getContext());
        Cursor e = tim.getTimbradoActivo();
        if (e.moveToNext()) {
            timbActual = e.getString(1);
            fdesde = e.getString(2);
            fhasta = e.getString(3);
        } else {
            timbActual = "0";
            fdesde = "0";
            fhasta = "0";
        }
    }

    private void obtenerEmpresa() {
        Access_Empresa emp = new Access_Empresa(getContext());
        Cursor e = emp.getEmpresas();
        if (e.moveToNext()) {
            empRZ = e.getString(1);
            empRUC = e.getString(2);
            empDir = e.getString(3);
            empTel = e.getString(4);
            empCiu = e.getString(6);
        } else {
            empRZ = "sin especificar";
            empRUC = "sin especificar";
            empDir = "sin especificar";
            empTel = "sin especificar";
            empCiu = "sin especificar";
        }

    }

    public void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                myLabel.setText("No hay adaptador bluetooth disponible.");
            }
            if (mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                getContext().startActivity(enableBluetooth);
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
                myLabel.setText("No fue posible conectar con la impresora intente de nuevo");
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
            myLabel.setText("Bluetooth abierto, listo para imprimir.");
            ((Button) vista.findViewById(R.id.btnImprimir)).setVisibility(View.VISIBLE);
            ((ImageButton) vista.findViewById(R.id.btnBT)).setImageResource(R.drawable.ic_bluetooth_connected);
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
            readBuffer = new byte[40960];
            //readBuffer = new byte[20480];
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
    public void sendData() {
        try {
            if (mmSocket != null) {
                try {
                    //String msg = "\n";
                    //msg += "\n";
                    DecimalFormat nformat = new DecimalFormat("##,###,###");
                    String msg = empRZ + "\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg, 0, 0, 0, 1));
                    String msg1 = "VENTAS DE LACTEOS LACTOLANDA\n";
                    msg1 += "RUC: " + empRUC + "\n";
                    msg1 += "CEL: " + empTel + "\n";
                    msg1 += empDir + "\n";
                    msg1 += "VILLARRICA - PARAGUAY\n";
                    msg1 += "I.V.A. INCLUIDO\n";
                    msg1 += "------------------------------------------------\n";//45
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg1, 0, 0, 0, 0));
                    String msg2 = "TIMBRADO: " + timbActual + "\n";
                    msg2 += "VALIDO DESDE: " + fdesde + " HASTA: " + fhasta + "\n";
                    msg2 += "FACTURA " + condicion + " NRO: " + nestablecimiento + "-" + nemision + "-" + nrofactura + "\n";
                    msg2 += "FECHA/HORA: " + fecha + " " + hora + "\n";
                    msg2 += "VENDEDOR: " + vendedor + "\n";
                    msg2 += "\n";
                    msg2 += "CLIENTE: " + vcliente + "\n";
                    msg2 += "RUC/CI: " + vruccliente + "\n";
                    msg2 += "------------------------------------------------\n";
                    msg2 += String.format("%1$1s %2$10s %3$1s %4$12s %5$17s", "IVA", "CANT", "", "PRECIO", "   SUB-TOTAL");
                    msg2 += "\n";
                    msg2 += "------------------------------------------------\n";
                    for (int i = 1; i <= lista.size(); i++) {
                        int position = (i - 1);
                        DetalleVenta item = lista.get(position);
                        //msg2 += String.format("%1$-1s" , item.codbarra+"/"+item.producto+"\n");
                        msg2 += String.format("%1$-1s", item.codbarra + " " + item.mensaje + "\n");
                        msg2 += String.format("%1$-1s", item.producto + "\n");
                        msg2 += String.format("%1$-9s %2$-12s %3$-15s %4$-9s", item.ivadescripcion + "%", item.cantidad + " " + item.um, nformat.format(Integer.parseInt(item.precio)), nformat.format(item.total));
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

                    msg3 += "TOTAL Gs." + ((TextView) vista.findViewById(R.id.idtotalfinal)).getText().toString() + "\n";
                    msg3 += "------------------------\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg3, 0, 0, 1, 1));
                    String msg4 = d.Convertir(((TextView) vista.findViewById(R.id.idtotalfinal)).getText().toString().replace(".", "").replace(",", ""), true) + "\n";
                    msg4 += "------------------------------------------------\n";
                    msg4 += "\n";
                    msg4 += "TOTALES GRAVADA" + "\n";
                    msg4 += "EXENTAS   -------->              " + nformat.format(Integer.parseInt(((TextView) vista.findViewById(R.id.idtotalexenta)).getText().toString())) + "\n";
                    msg4 += "GRAV.  5% -------->              " + nformat.format(Integer.parseInt(((TextView) vista.findViewById(R.id.idtotal5)).getText().toString())) + "\n";
                    msg4 += "GRAV. 10% -------->              " + nformat.format(Integer.parseInt(((TextView) vista.findViewById(R.id.idtotal10)).getText().toString())) + "\n\n";
                    msg4 += "LIQUIDACION DEL I.V.A." + "\n";
                    msg4 += "IVA 5%    -------->              " + nformat.format(Math.round(Double.parseDouble(((TextView) vista.findViewById(R.id.idtotal5)).getText().toString()) / 21)) + "\n";
                    msg4 += "IVA 10%   -------->              " + nformat.format(Math.round(Double.parseDouble(((TextView) vista.findViewById(R.id.idtotal10)).getText().toString()) / 11)) + "\n";
                    mmOutputStream.write(ESC_ALIGN_LEFT);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg4, 0, 0, 0, 0));
                    String msg5 = "------------------------------------------------\n";
                    long totaliva = (Math.round(Double.parseDouble(((TextView) vista.findViewById(R.id.idtotal5)).getText().toString()) / 21) + Math.round(Double.parseDouble(((TextView) vista.findViewById(R.id.idtotal10)).getText().toString()) / 11));
                    msg5 += "TOTAL I.V.A.: " + nformat.format(Integer.parseInt(String.valueOf(totaliva))) + "\n";
                    msg5 += "------------------------------------------------\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg5, 0, 0, 0, 0));
                    String msg6 = "";
                    if (txtEfectivo.getText().toString().isEmpty()) {
                        msg6 += "EFECTIVO: 0 \n";
                    } else {
                        msg6 += "EFECTIVO: " + nformat.format(Integer.parseInt(txtEfectivo.getText().toString())) + "\n";
                    }
                    msg6 += "VUELTO:   " + nformat.format(Integer.parseInt(txtVuelto.getText().toString())) + "\n";
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
                    String msg7 = empRZ + "\n";
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
                    //Toast.makeText(getContext(), "Error al interntar imprimir texto, vuelta a intentarlo", Toast.LENGTH_SHORT).show();
                    Snackbar.make(vista.findViewById(R.id.linearLayout66), "Error al interntar imprimir texto, vuelta a intentarlo", Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(Color.parseColor("#11232E")).show();
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
                    String msg = empRZ + "\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg, 0, 0, 0, 1));
                    String msg1 = "VENTAS DE LACTEOS LACTOLANDA\n";
                    msg1 += "RUC: " + empRUC + "\n";
                    msg1 += "CEL: " + empTel + "\n";
                    msg1 += empDir + "\n";
                    msg1 += "VILLARRICA - PARAGUAY\n";
                    msg1 += "I.V.A. INCLUIDO\n";
                    msg1 += "------------------------------------------------\n";//45
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg1, 0, 0, 0, 0));
                    String msg2 = "TIMBRADO: " + timbActual + "\n";
                    msg2 += "VALIDO DESDE: " + fdesde + " HASTA: " + fhasta + "\n";
                    msg2 += "FACTURA " + condicion + " NRO: " + nestablecimiento + "-" + nemision + "-" + nrofactura + "\n";
                    msg2 += "FECHA/HORA: " + fecha + " " + hora + "\n";
                    msg2 += "VENDEDOR: " + vendedor + "\n";
                    msg2 += "\n";
                    msg2 += "CLIENTE: " + vcliente + "\n";
                    msg2 += "RUC/CI: " + vruccliente + "\n";
                    msg2 += "------------------------------------------------\n";
                    msg2 += String.format("%1$1s %2$10s %3$1s %4$12s %5$17s", "IVA", "CANT", "", "PRECIO", "   SUB-TOTAL");
                    msg2 += "\n";
                    msg2 += "------------------------------------------------\n";
                    for (int i = 1; i <= lista.size(); i++) {
                        int position = (i - 1);
                        DetalleVenta item = lista.get(position);
                        //msg2 += String.format("%1$-1s" , item.codbarra+"/"+item.producto+"\n");
                        msg2 += String.format("%1$-1s", item.codbarra + " " + item.mensaje + "\n");
                        msg2 += String.format("%1$-1s", item.producto + "\n");
                        msg2 += String.format("%1$-9s %2$-12s %3$-15s %4$-9s", item.ivadescripcion + "%", item.cantidad + " " + item.um, nformat.format(Integer.parseInt(item.precio)), nformat.format(item.total));
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

                    msg3 += "TOTAL Gs." + ((TextView) vista.findViewById(R.id.idtotalfinal)).getText().toString() + "\n";
                    msg3 += "------------------------\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg3, 0, 0, 1, 1));
                    String msg4 = d.Convertir(((TextView) vista.findViewById(R.id.idtotalfinal)).getText().toString().replace(".", "").replace(",", ""), true) + "\n";
                    msg4 += "------------------------------------------------\n";
                    msg4 += "\n";
                    msg4 += "TOTALES GRAVADA" + "\n";
                    msg4 += "EXENTAS   -------->              " + nformat.format(Integer.parseInt(((TextView) vista.findViewById(R.id.idtotalexenta)).getText().toString())) + "\n";
                    msg4 += "GRAV.  5% -------->              " + nformat.format(Integer.parseInt(((TextView) vista.findViewById(R.id.idtotal5)).getText().toString())) + "\n";
                    msg4 += "GRAV. 10% -------->              " + nformat.format(Integer.parseInt(((TextView) vista.findViewById(R.id.idtotal10)).getText().toString())) + "\n\n";
                    msg4 += "LIQUIDACION DEL I.V.A." + "\n";
                    msg4 += "IVA 5%    -------->              " + nformat.format(Math.round(Double.parseDouble(((TextView) vista.findViewById(R.id.idtotal5)).getText().toString()) / 21)) + "\n";
                    msg4 += "IVA 10%   -------->              " + nformat.format(Math.round(Double.parseDouble(((TextView) vista.findViewById(R.id.idtotal10)).getText().toString()) / 11)) + "\n";
                    mmOutputStream.write(ESC_ALIGN_LEFT);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg4, 0, 0, 0, 0));
                    String msg5 = "------------------------------------------------\n";
                    long totaliva = (Math.round(Double.parseDouble(((TextView) vista.findViewById(R.id.idtotal5)).getText().toString()) / 21) + Math.round(Double.parseDouble(((TextView) vista.findViewById(R.id.idtotal10)).getText().toString()) / 11));
                    msg5 += "TOTAL I.V.A.: " + nformat.format(Integer.parseInt(String.valueOf(totaliva))) + "\n";
                    msg5 += "------------------------------------------------\n";
                    mmOutputStream.write(ESC_ALIGN_CENTER);
                    mmOutputStream.write(0x1C);
                    mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x74);
                    mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252
                    mmOutputStream.write(getByteString(msg5, 0, 0, 0, 0));
                    String msg6 = "";
                    if (txtEfectivo.getText().toString().isEmpty()) {
                        msg6 += "EFECTIVO: 0 \n";
                    } else {
                        msg6 += "EFECTIVO: " + nformat.format(Integer.parseInt(txtEfectivo.getText().toString())) + "\n";
                    }
                    msg6 += "VUELTO:   " + nformat.format(Integer.parseInt(txtVuelto.getText().toString())) + "\n";
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
                    String msg7 = empRZ + "\n";
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
                    Snackbar.make(vista.findViewById(R.id.linearLayout66), "Error al interntar imprimir texto, vuelta a intentarlo", Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(Color.parseColor("#11232E")).show();
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

    public static byte[] getByteString(String str, int bold, int font, int widthsize, int heigthsize) {

        if (str.length() == 0 | widthsize < 0 | widthsize > 3 | heigthsize < 0 | heigthsize > 3
                | font < 0 | font > 1)
            return null;

        byte[] strData = null;
        try {
            strData = str.getBytes("iso-8859-1");
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

    private void AlertaRegistrar() {
        if (lista.size() == 0) {
            //Toast.makeText(getContext(), "Lista vacía", Toast.LENGTH_SHORT).show();
            Snackbar.make(vista.findViewById(R.id.linearLayout66), "NO ES POSIBLE REGISTRAR LA VENTA!.\n" +
                            "El detalle de venta se encuentra vacía.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(Color.parseColor("#FF6F00"))
                    .setActionTextColor(Color.parseColor("#FFFFFF")).show();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setMessage("¿Esta seguro que desea registrar esta venta y proceder a emitir el comprobante legal?\n");
            alertDialog.setTitle("GUARDAR VENTA");
            alertDialog.setIcon(R.drawable.vender48);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("SI, REGISTRAR VENTA", (dialog, which) -> {
                Guardar();
                txtEfectivo.setEnabled(false);
                try {
                    findBT();
                } catch (Exception i) {
                    i.printStackTrace();
                }
            });
            alertDialog.setNegativeButton("CANCELAR", (dialog, which) -> dialog.cancel());
            alertDialog.show();
        }
    }

    private void bajarTeclado() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imn.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}