package com.example.alejandro.practica4aadinmobiliariacontentprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Principal extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private AdaptadorCursor ac;
    private Cursor cu;
    private final int ANADIR = 0;
    private String tipoNuevo;
    private final int ACTIVIDAD_FOTOS = 2;
    private int HACER_FOTO=1;
    private ArrayList<Bitmap> arrayFotos;
    private int posicion=0;
    private int idFoto;
    private Button btAnterior,btSiguiente,btBorrar,btAnadir;
    private ContentValues datosInmuebleNuevo;
    private AlertDialog alerta;
    private String ip="192.168.1.37:8080";
    private Boolean error=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        cargarCursor();
        visualizarInmuebles();
        final ListView ls = (ListView) findViewById(R.id.listView);
        ac = new AdaptadorCursor(this, R.layout.lista_detalle, cu);
        ls.setAdapter(ac);
        registerForContextMenu(ls);


        btSiguiente = (Button)findViewById(R.id.btSiguiente);
        btAnterior = (Button)findViewById(R.id.btAnterior);
        btBorrar = (Button)findViewById(R.id.btBorrar);
        btAnadir = (Button)findViewById(R.id.btAnadir);

        final FragmentoFotos fFotos = (FragmentoFotos)getFragmentManager().findFragmentById(R.id.fragmentoFotos);
        final boolean horizontal = fFotos!=null && fFotos.isInLayout(); //Saber que orientación tengo

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Cursor c=(Cursor)ls.getItemAtPosition(position);
                if(horizontal){
                    btSiguiente.setEnabled(true);
                    btAnterior.setEnabled(true);
                    btBorrar.setEnabled(true);
                    btAnadir.setEnabled(true);
                    idFoto=Integer.parseInt(c.getString(0));
                    File carpetaFotos  = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+idFoto));
                    Log.v("id pulsado",c.getString(0));
                    arrayFotos=fFotos.insertarFotos(arrayFotos,Integer.parseInt(c.getString(0)),carpetaFotos);
                    fFotos.primeraFoto(arrayFotos);
                }else{
                    Intent i = new Intent(Principal.this,Fotos.class);
                    i.putExtra("id",c.getInt(0));
                    startActivityForResult(i, ACTIVIDAD_FOTOS);
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_anadir) {
            anadir();
        }else if (id == R.id.action_usuario) {
            nuevoUsuario();
        }else if (id == R.id.action_subir) {
            Subir s=new Subir();
            s.execute();
        }else if (id == R.id.action_ip) {
            cambiarip();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        cu.moveToPosition(index);

        if (id == R.id.action_borrar) {
            borrar(Integer.parseInt(cu.getString(0)));
        } else if (id == R.id.action_editar) {
            editar(index);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual, menu);
    }



    private void tostada(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void visualizarInmuebles() {
        final ListView ls = (ListView) findViewById(R.id.listView);
        ac = new AdaptadorCursor(this, R.layout.lista_detalle, cu);
        ls.setAdapter(ac);
        registerForContextMenu(ls);
        getLoaderManager().initLoader(0, null, this);
    }

    private void cargarCursor(){
        Uri uri = Contrato.TablaInmueble.CONTENT_URI;
        String[] projection = new String[]{
                Contrato.TablaInmueble._ID,
                Contrato.TablaInmueble.CALLE,
                Contrato.TablaInmueble.LOCALIDAD,
                Contrato.TablaInmueble.TIPO,
                Contrato.TablaInmueble.PRECIO,
                Contrato.TablaInmueble.SUBIDO};
        cu = this.getContentResolver().query(uri, projection, null, null, null);
    }

    public boolean anadir() {
        Intent i = new Intent(this, Anadir.class);
        startActivityForResult(i,ANADIR);
        return true;
    }

    private boolean editar(final int posicion) {

        final int id;
        final int precio,subido=0;
        final String localidad, direccion, tipo;


        Inmueble inmueble = inmueblePosicion(posicion);

        id = inmueble.getId();
        precio = inmueble.getPrecio();
        localidad = inmueble.getLocalidad();
        direccion = inmueble.getDireccion();
        tipo = inmueble.getTipo();

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.titulo_editar);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.editar, null);
        alert.setView(vista);

        final EditText etEditarPrecio, etEditarLocalidad, etEditarDireccion;
        final Spinner etEditarTipo;
        etEditarPrecio = (EditText) vista.findViewById(R.id.etEditarPrecio);
        etEditarLocalidad = (EditText) vista.findViewById(R.id.etEditarLocalidad);
        etEditarDireccion = (EditText) vista.findViewById(R.id.etEditarDireccion);

        etEditarTipo = (Spinner)vista.findViewById(R.id.spinnerEditarTipo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tipoInmueble, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etEditarTipo.setAdapter(adapter);

        etEditarPrecio.setText(precio+"");
        etEditarLocalidad.setText(localidad);
        etEditarDireccion.setText(direccion);

        if (tipo.equals("Piso")) {
            etEditarTipo.setSelection(0);
        } else if (tipo.equals("Casa")) {
            etEditarTipo.setSelection(1);
        }
        etEditarTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                tipoNuevo= parent.getItemAtPosition(pos).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (etEditarPrecio.getText().toString().equals("") == true || etEditarLocalidad.getText().toString().equals("") == true || etEditarDireccion.getText().toString().equals("") == true) {
                    tostada(getString(R.string.vacio));
                }else {
                    Inmueble inmuebleAntiguo = new Inmueble(id,precio,localidad,direccion,tipo,subido);
                    Inmueble inmuebleNuevo = new Inmueble(id,Integer.parseInt(etEditarPrecio.getText().toString()), etEditarLocalidad.getText().toString(), etEditarDireccion.getText().toString(),tipoNuevo,subido);
                    Uri uri = Contrato.TablaInmueble.CONTENT_URI;
                    datosInmuebleNuevo=datosInmueble(inmuebleNuevo);
                    datosInmuebleNuevo=datosInmueble(inmuebleNuevo);
                    String where = Contrato.TablaInmueble._ID + " = ?";
                    String [] args = new String[]{inmuebleAntiguo.getId()+""};
                    getContentResolver().update(uri, datosInmuebleNuevo, where, args);

                }
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }

    private boolean borrar(final int idInmueble) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.tituloBorrar));
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Uri uri = Contrato.TablaInmueble.CONTENT_URI;
                String where = Contrato.TablaInmueble._ID + " = ?";
                String[] args = new String[]{idInmueble+""};
                getContentResolver().delete(uri,where, args);
                eliminarCarpetaFotoPorID(idInmueble);
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }

    public void eliminarCarpetaFotoPorID(final int id){
        File carpetaFotos  = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES))+"/"+id);
        if (carpetaFotos.isDirectory()) {
            String[] children = carpetaFotos.list();
            for (int i = 0; i < children.length; i++) {
                new File(carpetaFotos, children[i]).delete();
            }
            carpetaFotos.delete();
        }

        /*
        carpetaFotos.delete();
        String[] archivosCarpetaFotos = carpetaFotos.list();
        for (int i=0;i<archivosCarpetaFotos.length;i++){
            if (archivosCarpetaFotos[i].indexOf("inmueble_"+id) != -1){
                File archivoaBorrar = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)), archivosCarpetaFotos[i]);
                archivoaBorrar.delete();

            }


        }*/
    }
    private Inmueble inmueblePosicion(int posicion){
        cu.moveToPosition(posicion);
        int id = cu.getInt(0);
        String calle = cu.getString(1);
        String localidad = cu.getString(2);
        String tipo = cu.getString(3);
        int precio = cu.getInt(4);
        Integer subido = Integer.parseInt(cu.getString(5));

        return new Inmueble(id, precio, localidad, calle, tipo, subido);
    }

    private ContentValues datosInmueble(Inmueble inmueble){
        ContentValues valores = new ContentValues();

        String localidad = inmueble.getLocalidad();
        String tipo = inmueble.getTipo();
        int precio = inmueble.getPrecio();
        String calle = inmueble.getDireccion();
        int subido = inmueble.getSubido();

        valores.put(Contrato.TablaInmueble.CALLE, calle);
        valores.put(Contrato.TablaInmueble.LOCALIDAD, localidad);
        valores.put(Contrato.TablaInmueble.TIPO, tipo);
        valores.put(Contrato.TablaInmueble.PRECIO, precio);
        valores.put(Contrato.TablaInmueble.SUBIDO, subido);

        return valores;
    }

    public void siguiente(View v){
        final FragmentoFotos fFotos = (FragmentoFotos)getFragmentManager().findFragmentById(R.id.fragmentoFotos);
        posicion++;
        Log.v("siguiente","boton");
        if(arrayFotos.size()==0){

        }else {
            if (posicion>arrayFotos.size()-1){
                posicion=arrayFotos.size()-1;
                Log.v("posicion1",posicion+"");
                Log.v("tamaño1",arrayFotos.size()+"");
                fFotos.avanzarFoto(arrayFotos,posicion);
            }else{
                Log.v("posicion2",posicion+"");
                Log.v("tamaño2",arrayFotos.size()+"");
                fFotos.avanzarFoto(arrayFotos,posicion);
            }}
    }

    public void anterior(View v){
        final FragmentoFotos ffotos = (FragmentoFotos)getFragmentManager().findFragmentById(R.id.fragmentoFotos);
        posicion--;
        Log.v("boton","anterior");
        if(arrayFotos.size()==0){

        }else {
            if (posicion<0){
                posicion=0;
                Log.v("posicion1",posicion+"");
                Log.v("tamaño1",arrayFotos.size()+"");
                ffotos.avanzarFoto(arrayFotos,posicion);
            }else{
                Log.v("posicion2",posicion+"");
                Log.v("tamaño2",arrayFotos.size()+"");
                ffotos.avanzarFoto(arrayFotos,posicion);
            }}
    }

    public void hacerFoto(View v){

        Intent i = new Intent ("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(i, HACER_FOTO);
    }

    public boolean eliminarFoto(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.tituloBorrarFoto));
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int cont=0;
                File carpetaFotos  = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+idFoto));
                String[] archivosCarpetaFotos = carpetaFotos.list();
                Log.v("nFotos",archivosCarpetaFotos.length+"");
                for (int i=0;i<archivosCarpetaFotos.length;i++){
                    if(archivosCarpetaFotos[i].indexOf("inmueble_"+idFoto) != -1){
                        if (cont==posicion) {
                            File archivoaBorrar = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES))+"/"+idFoto, archivosCarpetaFotos[i]);
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

    private String getFecha(){
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String formatteDate = df.format(date);
        return formatteDate;
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ANADIR) {
            Bundle b;
            Inmueble inmueble;
            ContentValues datosInmuebleNuevo;
            Uri uri = Contrato.TablaInmueble.CONTENT_URI;

            b = data.getExtras();
            inmueble = (Inmueble) b.getSerializable("inmueble");
            datosInmuebleNuevo = datosInmueble(inmueble);
            Log.v("asdasd",datosInmuebleNuevo.get(Contrato.TablaInmueble.LOCALIDAD).toString()+"");
            getContentResolver().insert(uri, datosInmuebleNuevo);
            cargarCursor();
            cu.moveToLast();

            Log.v("idUltimo",cu.getString(0)+"");
            File carpetaFotos  = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+(cu.getString(0))));
            carpetaFotos.mkdir();
            //ac.notifyDataSetChanged();
            tostada(getString(R.string.mensaje_anadir));

        }else if (resultCode == RESULT_OK && requestCode == HACER_FOTO) {

            Bitmap foto = (Bitmap)data.getExtras().get("data");
            FileOutputStream salida;
            String nombrefoto;
            try {
                String[] fecha=getFecha().split("-");
                nombrefoto="inmueble_"+idFoto+"_"+fecha[0]+"_"+fecha[1]+"_"+fecha[2]+"_"+fecha[3]+"_"+fecha[4]+"_"+fecha[5];
                salida = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+idFoto+"/"+nombrefoto+".jpg");
                foto.compress(Bitmap.CompressFormat.JPEG, 90, salida);
            } catch (FileNotFoundException e) {
            }

        }
    }


    private void guardarSharedPreferences(String usuario) {
        SharedPreferences pc;
        SharedPreferences.Editor ed;
        pc = getSharedPreferences("preferencia", MODE_PRIVATE);
        ed = pc.edit();
        ed.putString("usuario", usuario);
        ed.apply();
    }
    private String leerSharedPreferences() {
        SharedPreferences pc;
        pc = getSharedPreferences("preferencia", MODE_PRIVATE);
        return pc.getString("usuario","");
    }
    private boolean nuevoUsuario(){
        String usuarioActual = leerSharedPreferences();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Usuario nuevo");
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.usuario, null);
        alertDialog.setView(vista);

        final EditText texto = (EditText)vista.findViewById(R.id.etUsuario);
        texto.setText(usuarioActual);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                if(!texto.getText().toString().equals("")){
                    guardarSharedPreferences(texto.getText().toString());
                }
            }
        });
        alerta = alertDialog.create();
        alerta.show();
        return true;
    }

  /*LOADER*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Contrato.TablaInmueble.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null,Contrato.TablaInmueble.CALLE +" collate localized asc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ac.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ac.swapCursor(null);
    }

    class Subir extends AsyncTask<String,Integer,String> {
        ProgressDialog pb;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb=ProgressDialog.show(Principal.this, "Subida","Subiendo datos", true);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

        public String postFile(String urlPeticion, String nombreParametro, String nombreArchivo) {
            String resultado="";
            int status=0;
            try {
                URL url = new URL(urlPeticion);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setDoOutput(true);
                conexion.setRequestMethod("POST");
                FileBody fileBody = new FileBody(new File(nombreArchivo));
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
                multipartEntity.addPart(nombreParametro, fileBody);
                multipartEntity.addPart("nombre", new StringBody("valor"));
                conexion.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
                OutputStream out = conexion.getOutputStream();
                try {
                    multipartEntity.writeTo(out);
                } finally {
                    out.close();
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    resultado+=decodedString+"\n";
                }
                in.close();
                status = conexion.getResponseCode();
            } catch (MalformedURLException ex) {
                return ex.toString();
            } catch (IOException ex) {
                return ex.toString();
            }
            return resultado+"\n"+status;
        }

        @Override
        protected String doInBackground(String... params) {
            String r="";
            try {
                cu.moveToFirst();
                for (int i=0;i<cu.getCount();i++){

                    Log.v("tipo",cu.getString(3).toString());
                    URL url = new URL("http://"+ip+"/Practica4AADInmobiliariaHibernate/control?target=android&op=insert&action=op&tipo="
                            +cu.getString(3).toString()+"&calle="+cu.getString(1).toString().replace(" ","%20")+"&localidad="+cu.getString(2).toString().replace(" ","%20")+"&precio="+cu.getString(4).toString().replace(" ","%20")+"&usuario="+leerSharedPreferences().replace(" ","%20"));
                    BufferedReader b = new BufferedReader(new InputStreamReader(url.openStream()));
                    String idServidor = b.readLine();
                    b.close();
                    Log.v("idServidor",idServidor);
                    File carpetaFotos  = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+cu.getString(0).toString()));
                    String[] archivosCarpetaFotos = carpetaFotos.list();
                    for (int j=0;j<archivosCarpetaFotos.length;j++){
                        r=postFile("http://"+ip+"/Practica4AADInmobiliariaHibernate/control?target=inmueble&op=subir&action=op&idInmuebleFoto="+idServidor,"archivo",carpetaFotos.getAbsolutePath() + "/" + archivosCarpetaFotos[j]);
                        Log.v("foto",carpetaFotos.getAbsolutePath() + "/" + archivosCarpetaFotos[j]);

                    }
                    eliminarCarpetaFotoPorID(Integer.parseInt(cu.getString(0).toString()));
                    Uri uri= Contrato.TablaInmueble.CONTENT_URI;
                    String where= Contrato.TablaInmueble._ID+"=?";
                    String[] args= new String[]{cu.getString(0).toString()+""};
                    getContentResolver().delete(uri,where,args);
                    cu.moveToNext();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                error=true;
            } catch (IOException e) {
                e.printStackTrace();
                error=true;
            }

            return r;
        }

        @Override
        protected void onPostExecute(String strings) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            pb.dismiss();
            if(error==false) {
                tostada(getString(R.string.mensaje_subir));
            }else{
                tostada(getString(R.string.mensaje_error));
                error=false;
            }
            cargarCursor();
            visualizarInmuebles();
        }
    }
    public void cambiarip(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.m_ip));
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogoip, null);
        alert.setView(vista);

        final EditText etIP = (EditText)vista.findViewById(R.id.etIP);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                if(!etIP.getText().toString().trim().equals("")){
                    ip=etIP.getText().toString();
                }
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
    }


}
