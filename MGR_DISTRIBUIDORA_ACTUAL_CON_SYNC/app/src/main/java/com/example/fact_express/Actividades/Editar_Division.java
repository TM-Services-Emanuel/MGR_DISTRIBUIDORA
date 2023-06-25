package com.example.fact_express.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fact_express.CRUD.Access_Division;
import com.example.fact_express.R;

public class Editar_Division extends AppCompatActivity {
    private int divisionEditar;
    private EditText division;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_division);
        Bundle extras = this.getIntent().getExtras();
        if(extras!=null){
            divisionEditar = extras.getInt("iddivision");
        }
        division = findViewById(R.id.id_division_mod);

        reflejarCampos();

    }
    public void reflejarCampos(){
        Access_Division db = Access_Division.getInstance(getApplicationContext());
        db.openReadable();
        Cursor c = db.getDivision_a_Modificar(divisionEditar);
        try {
            if(c.moveToNext()){
                division.setText(c.getString(1));
                division.requestFocus();

            }
            db.close();
        }finally {}
    }
    public void Modificar(View v){
        if(division.getText().toString().trim().isEmpty()){
            division.requestFocus();
        }else{
            Access_Division db = Access_Division.getInstance(getApplicationContext());
            db.openWritable();
            ContentValues values = new ContentValues();
            values.put("descripcion",division.getText().toString());
            long respuesta =db.ActualizarDivision(values, divisionEditar);
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
        Intent i = new Intent(getApplicationContext(), Listar_Division.class);
        startActivity(i);
        division.setText("");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, Listar_Division.class);
        startActivity(i);
        finish();
    }
}