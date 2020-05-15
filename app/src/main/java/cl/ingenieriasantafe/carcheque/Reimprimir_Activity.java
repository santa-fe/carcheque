package cl.ingenieriasantafe.carcheque;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Reimprimir_Activity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper myDB;
    ProgressDialog progressDialog;


    private static final String TAG = Reimprimir_Activity.class.getName();
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;


    ArrayList<Registro_recepcion> listarecepcion;
    ArrayList<String> listarecepciondeldia;
    Registro_recepcion reg = new Registro_recepcion();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reimprimir_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDB = new DatabaseHelper(this);
        listView = (ListView) findViewById(R.id.listview_reimprimir);
        progressDialog = new ProgressDialog(Reimprimir_Activity.this);

        listadorecepcion();
        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listarecepciondeldia);
        listView.setAdapter(adaptador);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final AlertDialog.Builder alertalistado = new AlertDialog.Builder(Reimprimir_Activity.this);
                alertalistado.setTitle("Reimprimir vale N. " + listarecepcion.get(position).getId() + "");
                alertalistado.setMessage("¿Estas seguro de reimprimir?");
                alertalistado.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String nrovale = "" + listarecepcion.get(position).getId();
                        String planta = "" + listarecepcion.get(position).getPlanta();
                        String tipomaterial = "" + listarecepcion.get(position).getTipomaterial();
                        String patente = "" + listarecepcion.get(position).getPatente();
                        String m3 = "" + listarecepcion.get(position).getM3();
                        String km = "" + listarecepcion.get(position).getKm();
                        String obra = "" + listarecepcion.get(position).getObra();
                        String camino = "" + listarecepcion.get(position).getCamino();
                        String fecha = "" + listarecepcion.get(position).getFecha();
                        String hora = "" + listarecepcion.get(position).getHora();
                        String usuario = "" + listarecepcion.get(position).getUsername();
                        String chofer = "" + listarecepcion.get(position).getChofer();

                        try {
                            reimprimir(nrovale,planta,tipomaterial,patente,m3,km,obra,camino,fecha,hora,usuario,chofer);
                            Intent intent = new Intent(Reimprimir_Activity.this, FormularioRecepcion_Activity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                alertalistado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertalistado.show();


            }
        });


    }

    private void reimprimir(final String nrovale, final String planta, final String tipomaterial, final String patente, final String m3, final String km, final String obra, final String camino, final String fecha
            , final String hora, final String usuario, final String chofer) {

            progressDialog.setTitle("Generando impresion");
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

                            Handler mHandler = new Handler(Looper.getMainLooper());

                            Thread.sleep(6000);

                            String msg2 = " " + " " + " " + " " + "Ingenieria y Construcciones" + " " + " " + " " + "\n" +
                                    " " + " " + " " + "" + " " + " " + " " + " " + " " + "" + " " + "\n" +
                                    " " + "Vale: " + nrovale + "\n" +
                                    " " + "Fecha: " + fecha + " " + hora + "\n" +
                                    " " + "Planta: " + planta+ "\n" +
                                    " " + "Patente: " + patente + "\n" +
                                    " " + "Tipo material: " + tipomaterial + "\n" +
                                    " " + "M3: " + m3 + "\n" +
                                    " " + "Obra: " + obra + "\n" +
                                    " " + "Camino: " + camino+ "\n" +
                                    " " + "Kilometro: " + km + "\n" +
                                    " " + "Chofer: " + chofer+ "\n" +
                                    " " + "Usuario: " + usuario + "\n" +
                                    " " + "\n" +
                                    " " + "\n" +
                                    " " + " " + " " + " " + " " + " " + "Recepcion Material" + " " + " " + " " + "\n" +
                                    " "+" "+" "+" "+" "+" "+" "+" *Copia Reemplazo* "+" "+" "+" "+"\n"+
                                    " " + "\n" +
                                    " " + "\n" +
                                    " " + "\n";
                            os.write(msg2.getBytes());

                            mBluetoothSocket.close();
                            Thread.sleep(3000);
                            progressDialog.dismiss();
                            Intent intent = new Intent(Reimprimir_Activity.this, FormularioRecepcion_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    private void listadorecepcion() {
        SQLiteDatabase db = myDB.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);

        listarecepcion = new ArrayList<Registro_recepcion>();
        Cursor cursor = db.rawQuery("SELECT id, planta,tipomaterial,patente,m3,km,obra,camino,fecha,hora,usuario,chofer FROM recepcion WHERE fecha='" + fecha + "'", null);

        while (cursor.moveToNext()) {
            reg = new Registro_recepcion();
            reg.setId(cursor.getInt(0));
            reg.setPlanta(cursor.getString(1));
            reg.setTipomaterial(cursor.getString(2));
            reg.setPatente(cursor.getString(3));
            reg.setM3(cursor.getString(4));
            reg.setKm(cursor.getString(5));
            reg.setObra(cursor.getString(6));
            reg.setCamino(cursor.getString(7));
            reg.setFecha(cursor.getString(8));
            reg.setHora(cursor.getString(9));
            reg.setUsername(cursor.getString(10));
            reg.setChofer(cursor.getString(11));
            listarecepcion.add(reg);
        }
        obtener_recepcionadosdeldia();
    }

    private void obtener_recepcionadosdeldia() {
        listarecepciondeldia = new ArrayList<String>();

        for (int i = 0; i < listarecepcion.size(); i++) {
            listarecepciondeldia.add("N. Vale: " + listarecepcion.get(i).getId() + "\n" +
                    "Planta: " + listarecepcion.get(i).getPlanta() + "\n" +
                    "Material: " + listarecepcion.get(i).getTipomaterial() + "\n" +
                    "Patente: " + listarecepcion.get(i).getPatente() + "\n" +
                    "M3: " + listarecepcion.get(i).getM3() + "\n" +
                    "KM: " + listarecepcion.get(i).getKm() + "\n" +
                    "Obra: " + listarecepcion.get(i).getObra() + "\n" +
                    "Camino: " + listarecepcion.get(i).getCamino() + "\n" +
                    "Fecha: " + listarecepcion.get(i).getFecha() + "\n" +
                    "Hora: " + listarecepcion.get(i).getHora() + "\n" +
                    "Usuario: " + listarecepcion.get(i).getUsername() + "\n" +
                    "Chofer: " + listarecepcion.get(i).getChofer());
        }

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
