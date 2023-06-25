package com.example.fact_express.Actividades;


import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fact_express.CRUD.Access_Empresa;
import com.example.fact_express.CRUD.Access_Timbrado;
import com.example.fact_express.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Fragment_FinalV extends Fragment {
    View vista;
    TextView myLabel;
    EditText myTextbox;
    TextView txtempRZ, txtempRUC, txtempTel, txtempDir, txtempCiu,txtseparador1;
    TextView txttimbDesc, txttimbD, txttimbH;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    String timbActual, fdesde,fhasta;
    String empRZ, empRUC, empDir, empTel,empCiu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        obtenerEmpresa();
        obtenerTimbrado();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment__final_v, container, false);
        try {
            // more codes will be here
            // we are going to have three buttons for specific functions
            Button openButton = (Button) vista.findViewById(R.id.open);
            Button sendButton = (Button) vista.findViewById(R.id.send);
            Button closeButton = (Button) vista.findViewById(R.id.close);

            // text label and input box
            myLabel = (TextView) vista.findViewById(R.id.label);
            //myTextbox = (EditText) vista.findViewById(R.id.entry);

            txtempRZ= (TextView)vista.findViewById(R.id.empRZ);
            txtempRZ.setText(empRZ);
            txtempRUC= ((TextView)vista.findViewById(R.id.empRUC));
            txtempRUC.setText("RUC: "+empRUC);
            txtempTel= ((TextView)vista.findViewById(R.id.empTel));
            txtempTel.setText("Telefono: "+empTel);
            txtempCiu=((TextView)vista.findViewById(R.id.empCiu));
            txtempCiu.setText(empCiu);
            txtempDir=((TextView)vista.findViewById(R.id.empDir));
            txtempDir.setText(empDir);
            txtseparador1=((TextView)vista.findViewById(R.id.tv_separador1));

            txttimbDesc=((TextView)vista.findViewById(R.id.timbDESC));
            txttimbDesc.setText("Timbrado Nro: "+timbActual);
            txttimbD=((TextView)vista.findViewById(R.id.timbD));
            txttimbD.setText("Inic. Vigencia: "+fdesde);
            txttimbH=((TextView)vista.findViewById(R.id.timbH));
            txttimbH.setText("Fin Vigencia: "+fhasta);

            // open bluetooth connection
            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        findBT();
                        openBT();
                    } catch (IOException ex) {
                        Toast.makeText(getContext(),"open: "+ex.getMessage(),Toast.LENGTH_LONG).show();
                        //ex.printStackTrace();
                    }
                }
            });

            // send data typed by the user to be printed
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sendData();
                    } catch (IOException ex) {
                        Toast.makeText(getContext(),"send: "+ex.getMessage(),Toast.LENGTH_LONG).show();
                        //ex.printStackTrace();
                    }
                }
            });

            // close bluetooth connection
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        closeBT();
                    } catch (IOException ex) {
                        Toast.makeText(getContext(),"close: "+ex.getMessage(),Toast.LENGTH_LONG).show();
                        //ex.printStackTrace();
                    }
                }
            });
        }catch(Exception e) {
            Toast.makeText(getContext(),"FATAL: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
        return vista;
    }
    private void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                myLabel.setText("No hay adaptador bluetooth disponible.");
            }

            if(mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivity(enableBluetooth);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    //if (device.getName().equals("RPP300")) {
                    if (device.getName().equals("MTP-3")) {
                        mmDevice = device;
                        break;
                    }
                }
            }

            myLabel.setText("Se encontró un dispositivo Bluetooth.");

        }catch(Exception e){
            Toast.makeText(getContext(),"findBT: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    private void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            myLabel.setText("Bluetooth abierto.");

        } catch (Exception e) {
            Toast.makeText(getContext(),"openBT: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }


    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    private void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                @Override
                public void run() {

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
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                myLabel.setText(data);
                                            }
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
                }
            });

            workerThread.start();

        } catch (Exception e) {
            Toast.makeText(getContext(),"beginListenForData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    private void sendData() throws IOException {
        try {

            if (mmSocket != null) {
                try {
                        //String texto =txtempRZ.getText().toString() + "\n";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                        Date date = new Date();

                        String fecha = dateFormat.format(date);

                        // the text typed by the user
                        //String msg = mEdtCodClient.getText().toString();
                        String msg = "\n";
                        msg += "\n";
                        msg += "       "+empRZ+"    \n";
                        msg += "          Direccion      \n " +
                                "     Numero de local     \n" +
                                "     RFC:XXXX-XXXXXX-XXX      \n" +
                                "     Tel. o Cel.      \n";
                        msg += "--------------------------------\n";
                        msg += "\n" + "RUC. " + empRUC + " Nombre: " + empRZ + "\n";
                        msg += "--------------------------------\n";
                        msg += "\n" + fecha + "    Folio: " + timbActual + " \n";
                        msg += "--------------------------------\n";
                        msg += String.format("%1$5s %2$11s %3$6s %4$6s", "Prod", "Cant", "Impor", "Total");
                        msg += "\n";
                        msg += "--------------------------------\n";

                        // if (!Edit1.getText().toString().isEmpty()) {
                        //     msg += "\n " + String.format("%1$1s %2$3s %3$6s %4$7s", Pro1.getText(), Edit1.getText(), Impor1.getText(), Resultado.getText()) + "";
                        // } else {

                        //  }

                        //   msg += "\n " + String.format("%1$-8s %2$3s %3$4s %4$6s", Pro20.getText(), text, Impor20.getText(), Tot20.getText());
                        // msg += "\n--------------------------------";
                        msg += "\n\n ";
                        //msg += "  cantidad en deuda: " + "      " + medtCredi.getText() + "\n";
                        //msg += "  Abono: " + "    " + mdabono.getText() + "\n";
                        //msg += "  Resta apagar: " + " resta    " + medtCredi.getText() + "\n";
                        // msg += "  Total Value: " + "     " + TotTotal + "\n";
                        // msg += "Cantidad en Letra: \n";
                        // msg += " " + Info.getText().toString() + "\n";
                        // msg += ""+ TareasProgramacion.cantidadConLetra(s19)+"\n";
                        //Info.setText(NumLetras.Convertir(total + "", true));
                        msg += "--------------------------------\n";
                        msg += "\n\n ";
                        //mmOutputStream.write(msg.getBytes());

                        // tell the user data were sent
                        myLabel.setText("Enviado a imprimir");



















                    // Para que acepte caracteres espciales
                    mmOutputStream.write(0x1C); mmOutputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    mmOutputStream.write(0x1B); mmOutputStream.write(0x74); mmOutputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252

                    mmOutputStream.write( getByteString(msg) );

                    mmOutputStream.write("\n\n".getBytes());

                } catch (IOException e) {
                    Log.e("MENSAJE: ", "Error al escribir en el socket");

                    Toast.makeText(getContext(), "Error al interntar imprimir texto", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Log.e("MENSAJE: ", "Socket nulo");

                myLabel.setText("Impresora no conectada");
            }

        } catch (Exception e) {
            Toast.makeText(getContext(),"sendData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // close the connection to bluetooth printer.
    private void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth cerrado");
        } catch (Exception e) {
            Toast.makeText(getContext(),"closeBT: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }
    public static byte[] getByteString(String str/*, int bold, int font, int widthsize, int heigthsize*/) {

        if (str.length() == 0)
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
        // command[2] = ((byte) bold);
        command[3] = 27;
        command[4] = 77;
        //command[5] = ((byte) font);
        command[6] = 29;
        command[7] = 33;
        //command[8] = (byte) (intToWidth[widthsize] + intToHeight[heigthsize]);

        System.arraycopy(strData, 0, command, 9, strData.length);
        return command;
    }

    public void obtenerTimbrado(){
        Access_Timbrado tim = new Access_Timbrado(getContext());
        Cursor e= tim.getTimbradoActivo();
        if(e.moveToNext()){
            timbActual=e.getString(1);
            fdesde = e.getString(2);
            fhasta= e.getString(3);
        }else{
            timbActual="0";
            fdesde = "0";
            fhasta= "0";
        }
    }

    public void obtenerEmpresa(){
        Access_Empresa emp = new Access_Empresa(getContext());
        Cursor e = emp.getEmpresas();
        if(e.moveToNext()){
            empRZ = e.getString(1);
            empRUC = e.getString(2);
            empDir = e.getString(3);
            empTel = e.getString(4);
            empCiu = e.getString(6);
        }else{
            empRZ = "sin especificar";
            empRUC = "sin especificar";
            empDir = "sin especificar";
            empTel = "sin especificar";
            empCiu = "sin especificar";
        }

    }

}