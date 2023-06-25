package com.example.fact_express.Actividades;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.fact_express.CRUD.Access_Timbrado;
import com.example.fact_express.R;

public class Editar_Timbrado extends AppCompatActivity {
    private int timbradoEditar;
    private EditText timbrado, desde, hasta;
    private TextView  est;
    public Switch switchE;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_timbrado);
        Bundle extras = this.getIntent().getExtras();
        if(extras!=null){
            timbradoEditar = extras.getInt("idtimbrado");
        }
        timbrado = findViewById(R.id.id_timb_mod);
        desde = findViewById(R.id.id_desde_mod);
        hasta = findViewById(R.id.id_hasta_mod);
        est = findViewById(R.id.id_estado_modif);
        switchE = findViewById(R.id.id_switch_mod);

        reflejarCampos();
        if(est.getText().toString().equals("Activo")){
            switchE.setChecked(true);
            est.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }else{
            switchE.setChecked(false);
            est.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    public void onClick(View view){
        if(view.getId()==R.id.id_switch_mod){
            if(switchE.isChecked()){
                est.setText("Activo");
                est.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }else{
                est.setText("Inactivo");
                est.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }
    }

    public void reflejarCampos(){
        Access_Timbrado db = Access_Timbrado.getInstance(getApplicationContext());
        db.openReadable();
        Cursor c = db.getTimbrado_a_Modificar(timbradoEditar);
        try {
            if(c.moveToNext()){
                timbrado.setText(c.getString(1));
                timbrado.requestFocus();
                desde.setText(c.getString(2));
                hasta.setText(c.getString(3));
                est.setText(c.getString(4));

            }
            db.close();
        }finally {}
    }
    public void Modificar(View v){
        if(timbrado.getText().toString().trim().isEmpty()){
            timbrado.requestFocus();
        }else if(desde.getText().toString().trim().isEmpty()){
            desde.requestFocus();
        }else if(hasta.getText().toString().trim().isEmpty()){
            hasta.requestFocus();
        }else{
            Access_Timbrado db = Access_Timbrado.getInstance(getApplicationContext());
            db.openWritable();
            ContentValues values = new ContentValues();
            values.put("descripcion",timbrado.getText().toString());
            values.put("fechadesde",desde.getText().toString());
            values.put("fechahasta",hasta.getText().toString());
            values.put("estado",est.getText().toString());
            long respuesta =db.ActualizarTimbrado(values,timbradoEditar);
            db.close();
            if(respuesta >0){
                Toast.makeText(this, "Modificación realizada exitosamente", Toast.LENGTH_LONG).show();
                Limpiar_y_volver_a_Lista();
            }else{
                Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void Limpiar_y_volver_a_Lista(){
        Intent i = new Intent(getApplicationContext(), Listar_Timbrado.class);
        startActivity(i);
        timbrado.setText("");
        desde.setText("");
        hasta.setText("");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, Listar_Timbrado.class);
        startActivity(i);
        finish();
    }
}