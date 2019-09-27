package cl.ingenieriasantafe.carcheque;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class FormularioRecepcion_Activity extends AppCompatActivity {

    private static final String TAG = FormularioRecepcion_Activity.class.getName();
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    ProgressDialog progressDialog;
    ArrayList<Registro_recepcion> listaregistrosrecepcion;
    Registro_recepcion registro_recepcion = new Registro_recepcion();
    public static final String APIEnvioRecepcion = "http://santafeinversiones.org/api/material-obra/registros";
    DatabaseHelper myDB = new DatabaseHelper(this);
    Spinner tipomaterial, plantaspinner, caminospinner;
    private AutoCompleteTextView pt,nombrechofer;
    EditText m3, kilometraje;
    Button recepcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulariorecepcion_layout);
        caminospinner = (Spinner) findViewById(R.id.CaminoSpinner);
        tipomaterial = (Spinner) findViewById(R.id.tipomaterialSpinner);
        plantaspinner = (Spinner) findViewById(R.id.plantaSpinner);
        recepcion = (Button) findViewById(R.id.btnrecepcion);
        progressDialog = new ProgressDialog(FormularioRecepcion_Activity.this);
        pt = findViewById(R.id.patentesRecepcion);
        m3 = (EditText) findViewById(R.id.txtm3Recepcion);
        kilometraje = (EditText) findViewById(R.id.txtkilometroRecepcion);
        nombrechofer =findViewById(R.id.Chofer_Recepcion);

        //ADAPTADORES DE SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.materiales_string, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipomaterial.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.plantas_string, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plantaspinner.setAdapter(adapter1);


        Cargapatentes();
        Cargachoferes();
        recepcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recepcionmaterial();
            }
        });



        Carga_caminos();
        SegundoPlano sp = new SegundoPlano();
        sp.execute();

    }


    private void Carga_caminos() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String obra = preferences.getString("UNEGOCIO", "NO EXISTE NINGUNA OBRA");

        if (obra.equalsIgnoreCase("rincon de lobos")) {
            caminosl11();
            plantas_rincon_lobos();
        } else if (obra.equalsIgnoreCase("talca norte")) {
            caminostalcanorte();
        } else if (obra.equalsIgnoreCase("pencahue")){
            caminospencahue();
            plantas_especifica_pencahue();
        } else if(obra.equalsIgnoreCase("paredones")){
            caminosparedones();
            plantas_paredones();
        }
    }

    private void caminosl11() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.caminos_l11_string, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caminospinner.setAdapter(adapter);

    }
    private void caminostalcanorte() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.caminos_talcanorte, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caminospinner.setAdapter(adapter);
    }
    private void caminospencahue(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.caminos_pencahue, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caminospinner.setAdapter(adapter);
    }

    private void caminosparedones(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.caminos_paredones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caminospinner.setAdapter(adapter);
    }
    private void plantas_rincon_lobos(){
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.plantas_rincon_lobos, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plantaspinner.setAdapter(adapter1);
    }

    private void plantas_especifica_pencahue(){
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.plantas_especifica_pencahue, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plantaspinner.setAdapter(adapter1);
    }

    private void plantas_paredones(){
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.plantas_paredones, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plantaspinner.setAdapter(adapter1);
    }


    private void Cargapatentes() {
        List<Vehiculos> patents = new ArrayList<Vehiculos>();
        final PatentesSearchAdapter patentesSearchAdapter = new PatentesSearchAdapter(getApplicationContext(), patents);
        pt.setThreshold(1);
        pt.setAdapter(patentesSearchAdapter);

        pt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                m3.setText(patentesSearchAdapter.getItem(position).getM3());
            }
        });
    }

    private void Cargachoferes() {
        List<Choferes> choferes = new ArrayList<Choferes>();
        final ChoferesSearchAdapter choferesSearchAdapter = new ChoferesSearchAdapter(getApplicationContext(), choferes);
        nombrechofer.setThreshold(1);
        nombrechofer.setAdapter(choferesSearchAdapter);
    }

    private void recepcionmaterial() {

        int metroscubicos = Integer.parseInt(m3.getText().toString());

        if (pt.getText().toString().equals("") || m3.getText().toString().equals("") || kilometraje.getText().toString().equals("")
                || nombrechofer.getText().toString().equals("")) {

            Toast.makeText(getApplicationContext(), "POR FAVOR LLENE TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
        } else {
            if (tipomaterial.getSelectedItem().toString().equalsIgnoreCase("seleccione material") || plantaspinner.getSelectedItem().toString().equalsIgnoreCase("seleccione planta")
                    || caminospinner.getSelectedItem().toString().equalsIgnoreCase("seleccione camino")) {
                Toast.makeText(getApplicationContext(), "SELECCIONE VALORES VALIDOS", Toast.LENGTH_SHORT).show();
            } else if(metroscubicos<=25) {
                SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                String nombre = preferences.getString("NAME", "NO EXISTE CREDENCIAL");
                String apellido = preferences.getString("LASTNAME", "NO EXISTE CREDENCIAL");

                SharedPreferences preferences1 = getSharedPreferences("obra_app", Context.MODE_PRIVATE);
                final String obra = preferences1.getString("obra", "Sin obra");


                final String username = nombre + " " + apellido;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                final String fecha = dateFormat.format(date);
                final String hora = horaformat.format(date);

               Boolean registro = myDB.RegistroRecepcion(plantaspinner.getSelectedItem().toString(), tipomaterial.getSelectedItem().toString(),
                        pt.getText().toString().toUpperCase(), m3.getText().toString(), kilometraje.getText().toString(),
                        fecha, hora, username, nombrechofer.getText().toString().toUpperCase(), "PENDIENTE", obra, caminospinner.getSelectedItem().toString());

                /**  if (registro == true) {
                    progressDialog.setTitle("Guardando recepción");
                    progressDialog.setMessage("Espere un momento......");
                    progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.setMax(100);
                    progressDialog.show();

                   new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SQLiteDatabase db = myDB.getWritableDatabase();
                                Cursor cursor = db.rawQuery("SELECT id FROM recepcion ORDER BY id DESC", null);
                                if (cursor.moveToFirst() == true) {
                                    String nvale = cursor.getString(0);

                                    SharedPreferences preferences = getSharedPreferences("printer", Context.MODE_PRIVATE);
                                    String mask = preferences.getString("mask", "");

                                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                                    if ((mBluetoothAdapter == null) || (!mBluetoothAdapter.isEnabled())) {
                                        throw new Exception("Bluetooth adapter no esta funcionando o no esta habilitado");
                                    }

                                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mask);
                                    mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
                                    mBluetoothAdapter.cancelDiscovery();
                                    mBluetoothSocket.connect();

                                    OutputStream os = mBluetoothSocket.getOutputStream();
                                    InputStream is = mBluetoothSocket.getInputStream();

                                    String msg = " " + " " + " " + " " + "Ingenieria y Construcciones" + " " + " " + " " + "\n" +
                                            " " + " " + " " + "" + " " + " " + " " + " " + " " + "" + " " + "\n" +
                                            " " + "Vale: " + nvale + "\n" +
                                            " " + "Fecha: " + fecha + " " + hora + "\n" +
                                            " " + "Planta: " + plantaspinner.getSelectedItem().toString() + "\n" +
                                            " " + "Patente: " + pt.getText().toString().toUpperCase() + "\n" +
                                            " " + "Tipo material: " + tipomaterial.getSelectedItem().toString() + "\n" +
                                            " " + "M3: " + m3.getText().toString() + "\n" +
                                            " " + "Obra: " + obra + "\n" +
                                            " " + "Camino: " + caminospinner.getSelectedItem().toString() + "\n" +
                                            " " + "Kilometro: " + kilometraje.getText().toString() + "\n" +
                                            " " + "Chofer: " + nombrechofer.getText().toString().toUpperCase() + "\n" +
                                            " " + "Usuario: " + username + "\n" +
                                            " " + "\n";
                                    os.write(msg.getBytes());
                                    Bitmap bitmap = QRCode.from(msg).withSize(380, 250).bitmap();
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] qr = Utils.decodeBitmap(bitmap);
                                    os.write(qr);
                                    String msg9 = " " + " " + " " + " " + " " + " " + "Recepcion Material" + " " + " " + " " + "\n" +
                                            " " + " " + " " + " " + " " + " " + " " + " " + " " + "Copia Externo " + " " + " " + " " + "\n" +
                                            " " + "\n" +
                                            " " + "\n" +
                                            " " + "\n";
                                    Handler mHandler = new Handler(Looper.getMainLooper());
                                    os.write(msg9.getBytes());

                                    Thread.sleep(6000);

                                    String msg2 = " " + " " + " " + " " + "Ingenieria y Construcciones" + " " + " " + " " + "\n" +
                                            " " + " " + " " + "" + " " + " " + " " + " " + " " + "" + " " + "\n" +
                                            " " + "Vale: " + nvale + "\n" +
                                            " " + "Fecha: " + fecha + " " + hora + "\n" +
                                            " " + "Planta: " + plantaspinner.getSelectedItem().toString() + "\n" +
                                            " " + "Patente: " + pt.getText().toString().toUpperCase() + "\n" +
                                            " " + "Material: " + tipomaterial.getSelectedItem().toString() + "\n" +
                                            " " + "M3: " + m3.getText().toString() + "\n" +
                                            " " + "Obra: " + obra + "\n" +
                                            " " + "Camino: " + caminospinner.getSelectedItem().toString() + "\n" +
                                            " " + "Kilometro: " + kilometraje.getText().toString() + "\n" +
                                            " " + "Chofer: " + nombrechofer.getText().toString().toUpperCase() + "\n" +
                                            " " + "Usuario: " + username + "\n" +
                                            " " + "\n" +
                                            " " + "\n" +
                                            " " + "\n" +
                                            " " + "Nombre:........................" + "\n" +
                                            " " + "\n" +
                                            " " + "\n" +
                                            " " + "Firma:........................." + "\n" +
                                            " " + "\n" +
                                            " " + "\n" +
                                            " " + " " + " " + " " + " " + " " + "Recepcion Material" + " " + " " + " " + "\n" +
                                            " " + " " + " " + " " + " " + " " + " " + " " + " " + " " + "Copia Obra " + " " + " " + " " + "\n" +
                                            " " + "\n" +
                                            " " + "\n" +
                                            " " + "\n";
                                    os.write(msg2.getBytes());

                                    mBluetoothSocket.close();
                                    Thread.sleep(3000);
                                    progressDialog.dismiss();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Recepcion generada correctamente", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    Intent intent = new Intent(FormularioRecepcion_Activity.this, FormularioRecepcion_Activity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }else{
                Toast.makeText(this, "LOS M3 DEBEN SER INFERIOR A 25", Toast.LENGTH_SHORT).show();
            }**/
            }
        }
    }

    private void SendPostData(){
        SQLiteDatabase db = myDB.getReadableDatabase();
        final String estado = "PENDIENTE";

        listaregistrosrecepcion = new ArrayList<Registro_recepcion>();
        Cursor cursor = db.rawQuery("SELECT id, planta, tipomaterial, patente, m3, km ,fecha, hora, usuario,chofer,obra, camino FROM recepcion WHERE estado='" + estado + "' ORDER BY id ASC", null);

        while(cursor.moveToNext()){
            registro_recepcion = new Registro_recepcion();
            registro_recepcion.setId(cursor.getInt(0));
            registro_recepcion.setPlanta(cursor.getString(1));
            registro_recepcion.setTipomaterial(cursor.getString(2));
            registro_recepcion.setPatente(cursor.getString(3));
            registro_recepcion.setM3(cursor.getString(4));
            registro_recepcion.setKm(cursor.getString(5));
            registro_recepcion.setFecha(cursor.getString(6));
            registro_recepcion.setHora(cursor.getString(7));
            registro_recepcion.setUsername(cursor.getString(8));
            registro_recepcion.setChofer(cursor.getString(9));
            registro_recepcion.setObra(cursor.getString(10));
            registro_recepcion.setCamino(cursor.getString(11));

            listaregistrosrecepcion.add(registro_recepcion);
        }

        Log.i("LISTA",""+listaregistrosrecepcion.size());

        for(int i = 0; i<listaregistrosrecepcion.size(); i++){

            final int id = listaregistrosrecepcion.get(i).getId();
            final String planta = listaregistrosrecepcion.get(i).getPlanta().toString();
            final String tipomaterial = listaregistrosrecepcion.get(i).getTipomaterial().toString();
            final String patente = listaregistrosrecepcion.get(i).getPatente().toString();
            final String m3 = listaregistrosrecepcion.get(i).getM3().toString();
            final String km = listaregistrosrecepcion.get(i).getKm().toString();
            final String fecha = listaregistrosrecepcion.get(i).getFecha().toString();
            final String hora = listaregistrosrecepcion.get(i).getHora().toString();
            final String username = listaregistrosrecepcion.get(i).getUsername().toString();
            final String chofer = listaregistrosrecepcion.get(i).getChofer().toString();
            final String obra = listaregistrosrecepcion.get(i).getObra().toString();
            final String camino = listaregistrosrecepcion.get(i).getCamino().toString();

            listaregistrosrecepcion.remove(i);
            Log.i("FINFOR", ""+listaregistrosrecepcion.size());

            String nvale = String.valueOf(id);

            final RequestParams params = new RequestParams();
            params.put("nrovale", nvale);
            params.put("patente", patente);
            params.put("m3", m3);
            params.put("tipom", tipomaterial);
            params.put("fecha", fecha);
            params.put("hora", hora);
            params.put("planta", planta);
            params.put("chofer", chofer);
            params.put("usuario", username);
            params.put("km", km);
            params.put("obra", obra);
            params.put("camino", camino);

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(APIEnvioRecepcion, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                String response = new String(responseBody).toUpperCase();
                                Log.i("REQUEST", ""+response);
                                SQLiteDatabase db = myDB.getReadableDatabase();
                                db.execSQL("UPDATE recepcion SET estado='ENVIADO' WHERE id='"+id+ "'");
                                Log.i("UPDATE","CAMBIO DE ESTADO ID: "+id);
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            String response = new String(responseBody).toUpperCase();
                            Log.i("REQUEST FAIL",""+error.toString()+" AA "+response);
                        }
                    });
                }
            };
            handler.post(runnable);
        }
    }

    public void TareaSync() {
        try {
            eliminardatos();
            SendPostData();
            Thread.sleep(4000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public class SegundoPlano extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            TareaSync();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            SegundoPlano sp = new SegundoPlano();
            sp.execute();
        }
    }


    private void eliminardatos() {

        SQLiteDatabase db = myDB.getWritableDatabase();
        String estado = "ENVIADO";
        listaregistrosrecepcion = new ArrayList<Registro_recepcion>();

        Cursor cursor = db.rawQuery("SELECT id FROM recepcion WHERE estado='" + estado + "'", null);

        while (cursor.moveToNext()) {
            registro_recepcion = new Registro_recepcion();
            registro_recepcion.setId(cursor.getInt(0));
            listaregistrosrecepcion.add(registro_recepcion);
        }
        for (int i = 0; i < listaregistrosrecepcion.size(); i++) {

            db.execSQL("DELETE FROM recepcion WHERE id=" + listaregistrosrecepcion.get(i).getId() + "");
            Log.i("ELIM","ELIMINADO"+ listaregistrosrecepcion.get(i));
        }


    }


}
