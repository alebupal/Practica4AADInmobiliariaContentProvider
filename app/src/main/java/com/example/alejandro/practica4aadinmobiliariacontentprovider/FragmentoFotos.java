package com.example.alejandro.practica4aadinmobiliariacontentprovider;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoFotos extends Fragment {
    private View v;
    private int id;
    private int posicion=0;
    private ArrayList<Bitmap> arrayFotos;
    private Button btSiguiente,btAnterior,btBorrar,btAnadir;
    private int IDACTIVIDADFOTO=2;

    String nombrefoto;

    public FragmentoFotos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_fragmento_fotos, container, false);
        return v;
    }

    public void primeraFoto(ArrayList<Bitmap> arrayFotos){

        ImageView iv = (ImageView)v.findViewById(R.id.imageView);
        if(arrayFotos.size()==0){
            Drawable myDrawable = getResources().getDrawable(R.drawable.foto);
            iv.setImageDrawable(myDrawable);
        }else{
            iv.setImageBitmap(arrayFotos.get(0));
        }
    }
    public void ultimaFoto(ArrayList<Bitmap> arrayFotos){

        ImageView iv = (ImageView)v.findViewById(R.id.imageView);
        if(arrayFotos.size()==0){
            Drawable myDrawable = getResources().getDrawable(R.drawable.foto);
            iv.setImageDrawable(myDrawable);
        }else{
            iv.setImageBitmap(arrayFotos.get(arrayFotos.size()));
        }
    }

    public void avanzarFoto(ArrayList<Bitmap> arrayFotos,int pos){
        ImageView iv = (ImageView)v.findViewById(R.id.imageView);
        iv.setImageBitmap(arrayFotos.get(pos));
    }

    public ArrayList<Bitmap> insertarFotos(ArrayList<Bitmap> arrayFotos,int id,File carpetaFotos){
        Log.v("id pulsado2",id+"");
        String[] archivosCarpetaFotos = carpetaFotos.list();
        arrayFotos = new ArrayList<Bitmap>();
        Bitmap bm;
        for (int i=0;i<archivosCarpetaFotos.length;i++){
            if (archivosCarpetaFotos[i].indexOf("inmueble_"+id) != -1){
                bm = BitmapFactory.decodeFile(carpetaFotos.getAbsolutePath() + "/" + archivosCarpetaFotos[i]);
                arrayFotos.add(bm);
            }
        }
        return arrayFotos;
    }

}
