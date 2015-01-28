package com.example.alejandro.practica4aadinmobiliariacontentprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Fotos extends Activity {

    private int id;
    private int posicion=0;
    private int IDACTIVIDADFOTO=2;
    private ArrayList<Bitmap> arrayFotos;
    String nombrefoto;

    private Button btAnterior,btSiguiente,btBorrar,btAnadir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos);

        btSiguiente = (Button)findViewById(R.id.btSiguiente);
        btAnterior = (Button)findViewById(R.id.btAnterior);
        btBorrar = (Button)findViewById(R.id.btBorrar);
        btAnadir = (Button)findViewById(R.id.btAnadir);

        btSiguiente.setEnabled(true);
        btAnterior.setEnabled(true);
        btBorrar.setEnabled(true);
        btAnadir.setEnabled(true);

        id = getIntent().getExtras().getInt("id");
        Log.v("id", id+"");
        final FragmentoFotos ffotos = (FragmentoFotos)getFragmentManager().findFragmentById(R.id.fragmentoFotos);
        arrayFotos=new ArrayList<Bitmap>();
        arrayFotos=insertarFotos(arrayFotos);
        ffotos.primeraFoto(arrayFotos);
    }

    public boolean eliminarFoto(View v){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.tituloBorrarFoto));
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int cont=0;
                File carpetaFotos  = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
                String[] archivosCarpetaFotos = carpetaFotos.list();
                for (int i=0;i<archivosCarpetaFotos.length;i++){
                    if (archivosCarpetaFotos[i].indexOf("inmueble_"+id) != -1){
                        if (cont==posicion) {
                            File archivoaBorrar = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)), archivosCarpetaFotos[i]);
                            // Log.v("archivoborrado",""+archivoaBorrar);
                            // Log.v("archivo",""+archivosCarpetaFotos[i]);
                            //Log.v("posicion",""+posicion);
                            archivoaBorrar.delete();
                        }
                        cont++;
                    }
                }
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }

    public void hacerFoto(View v){

        Intent i = new Intent ("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(i, IDACTIVIDADFOTO);

    }

    @Override
    public void onActivityResult(int pet, int res, Intent i) {
        if (res == RESULT_OK && pet == IDACTIVIDADFOTO) {
            Bitmap foto = (Bitmap)i.getExtras().get("data");
            FileOutputStream salida;
            try {
                String[] fecha=getFecha().split("-");
                nombrefoto="inmueble_"+id+"_"+fecha[0]+"_"+fecha[1]+"_"+fecha[2]+"_"+fecha[3]+"_"+fecha[4]+"_"+fecha[5];
                salida = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+nombrefoto+".jpg");
                foto.compress(Bitmap.CompressFormat.JPEG, 90, salida);
            } catch (FileNotFoundException e) {

            }
        }
    }

    private String getFecha(){

        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String formatteDate = df.format(date);
        return formatteDate;

    }

    public ArrayList<Bitmap> insertarFotos(ArrayList<Bitmap> arrayFotos){
        File carpetaFotos = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        String[] archivosCarpetaFotos = carpetaFotos.list();
        Bitmap bm;
        for (int i=0;i<archivosCarpetaFotos.length;i++){
            if (archivosCarpetaFotos[i].indexOf("inmueble_"+id) != -1){
                bm = BitmapFactory.decodeFile(carpetaFotos.getAbsolutePath() + "/" + archivosCarpetaFotos[i]);
                arrayFotos.add(bm);
            }
        }
        return arrayFotos;
    }

    public void siguiente(View v){
        final FragmentoFotos ffotos = (FragmentoFotos)getFragmentManager().findFragmentById(R.id.fragmentoFotos);
        arrayFotos=new ArrayList<Bitmap>();
        arrayFotos=insertarFotos(arrayFotos);
        posicion++;
        Log.v("siguiente","boton");
        if(arrayFotos.size()==0){

        }else {
            if (posicion > arrayFotos.size() - 1) {
                posicion = arrayFotos.size() - 1;
                Log.v("posicion1", posicion + "");
                Log.v("tamaño1", arrayFotos.size() + "");
                ffotos.avanzarFoto(arrayFotos, posicion);
            } else {
                Log.v("posicion2", posicion + "");
                Log.v("tamaño2", arrayFotos.size() + "");
                ffotos.avanzarFoto(arrayFotos, posicion);
            }
        }
    }

    public void anterior(View v){
        final FragmentoFotos ffotos = (FragmentoFotos)getFragmentManager().findFragmentById(R.id.fragmentoFotos);
        arrayFotos=new ArrayList<Bitmap>();
        arrayFotos=insertarFotos(arrayFotos);
        posicion--;
        Log.v("boton","anterior");
        if(arrayFotos.size()==0){

        }else {
            if (posicion < 0) {
                posicion = 0;
                Log.v("posicion1", posicion + "");
                Log.v("tamaño1", arrayFotos.size() + "");
                ffotos.avanzarFoto(arrayFotos, posicion);
            } else {
                Log.v("posicion2", posicion + "");
                Log.v("tamaño2", arrayFotos.size() + "");
                ffotos.avanzarFoto(arrayFotos, posicion);
            }
        }
    }
}

