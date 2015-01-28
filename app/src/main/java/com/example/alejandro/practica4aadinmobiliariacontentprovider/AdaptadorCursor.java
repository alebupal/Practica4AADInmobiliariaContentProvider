package com.example.alejandro.practica4aadinmobiliariacontentprovider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Alejandro on 26/01/2015.
 */
public class AdaptadorCursor extends CursorAdapter{

    private int recurso;
    private static LayoutInflater i;
    private Context contexto;
    private TextView tvCalle,tvPrecio,tvTipo,tvLocalidad;
    private ImageView iv;


    public AdaptadorCursor(Context co, int recurso, Cursor cu) {
        super(co, cu, true);
        this.recurso = recurso;
        this.contexto = co;
        this.i = (LayoutInflater)co.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

         LayoutInflater i = LayoutInflater.from(parent.getContext());
        View v = i.inflate(R.layout.lista_detalle, parent, false);
        return v;




    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        tvCalle=(TextView)view.findViewById(R.id.tvDireccion);
        tvPrecio=(TextView)view.findViewById(R.id.tvPrecio);
        tvTipo=(TextView)view.findViewById(R.id.tvTipo);
        tvLocalidad=(TextView)view.findViewById(R.id.tvLocalidad);
        iv = (ImageView)view.findViewById(R.id.ivFoto);

        tvCalle.setText(cursor.getString(1).toString());
        tvPrecio.setText(cursor.getString(4).toString());
        tvTipo.setText(cursor.getString(3).toString());
        tvLocalidad.setText(cursor.getString(2).toString());

        if(cursor.getString(3).toString().equals("Casa")){
            iv.setImageResource(R.drawable.casa);
        } else if(cursor.getString(3).equals("Piso")){
            iv.setImageResource(R.drawable.piso);
        }else if(cursor.getString(3).equals("Cochera")){
            iv.setImageResource(R.drawable.cochera);
        }

    }
}
