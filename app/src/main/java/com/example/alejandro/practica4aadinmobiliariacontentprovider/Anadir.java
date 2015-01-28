package com.example.alejandro.practica4aadinmobiliariacontentprovider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class Anadir extends ActionBarActivity {

    private int id;
    private int precio;
    private String localidad, direccion, tipo;
    EditText etAnadirPrecio, etAnadirLocalidad, etAnadirDireccion;
    Spinner etAnadirTipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir);

        etAnadirPrecio = (EditText)findViewById(R.id.etAnadirPrecio);
        etAnadirLocalidad = (EditText)findViewById(R.id.etAnadirLocalidad);
        etAnadirDireccion = (EditText)findViewById(R.id.etAnadirDireccion);
        etAnadirTipo = (Spinner)findViewById(R.id.spinnerAnadirTipo);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tipoInmueble, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etAnadirTipo.setAdapter(adapter);
        etAnadirTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                tipo= parent.getItemAtPosition(pos).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void AnadirInmuble(View v) {
        Inmueble inmueble = new Inmueble();

        localidad = etAnadirLocalidad.getText().toString();
        direccion = etAnadirDireccion.getText().toString();
        precio = Integer.parseInt(etAnadirPrecio.getText().toString());
        int subido=0;
        id=0;


        inmueble.setDireccion(direccion);
        inmueble.setLocalidad(localidad);
        inmueble.setPrecio(precio);
        inmueble.setTipo(tipo);
        inmueble.setId(id);
        inmueble.setSubido(subido);

        if (etAnadirPrecio.getText().toString().equals("") == true || etAnadirLocalidad.getText().toString().equals("") == true || etAnadirDireccion.getText().toString().equals("") == true) {
            tostada(getString(R.string.vacio));
        } else {
            Intent result = new Intent();
            Bundle b = new Bundle();
            b.putSerializable("inmueble", inmueble);
            result.putExtras(b);
            setResult(Activity.RESULT_OK, result);
            finish();
            this.finish();
        }


        Log.v("tipo",""+tipo);

    }

    private void tostada(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
