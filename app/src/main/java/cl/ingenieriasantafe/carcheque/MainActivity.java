package cl.ingenieriasantafe.carcheque;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ObjectInputValidation;

public class MainActivity extends AppCompatActivity {

    Button login;
    public static final String APIUsuarios = "http://santafeinversiones.org/api/material-obra/cardchecker";
    public static final String APIVEHICULOS = "http://santafeinversiones.org/api/material-obra/camion";
    public static final String APICHOFERES = "http://santafeinversiones.org/api/material-obra/chofer";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    DatabaseHelper myDB;
    TextInputEditText usuario;
    TextInputEditText password;
    TextInputLayout lusers;
    TextInputLayout lpass;
    ProgressDialog progressDialog;
    private InputValidation inputValidation;
    FloatingActionButton ajustes;
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    public static final int MY_DEFAULT_TIMEOUT = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.btnlogin);
        myDB = new DatabaseHelper(this);
        ajustes = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        lusers = (TextInputLayout) findViewById(R.id.textinputLayoutusers);
        lpass = (TextInputLayout) findViewById(R.id.textInputLayoutpass);
        usuario = (TextInputEditText) findViewById(R.id.txtusuario);
        password = (TextInputEditText) findViewById(R.id.txtpassword);
        inputValidation = new InputValidation(this);
        progressDialog = new ProgressDialog(MainActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DescargaUsuarios();
            }
        }).start();


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Message1", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            }
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCredenciales();
                verifyFromSQLite();
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nuevoform = new Intent(MainActivity.this, Settings.class);
                startActivityForResult(nuevoform, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            if (resultCode==RESULT_OK){
                Toast.makeText(this, "Operacion exitosa", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Codigo incorrecto",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void verifyFromSQLite(){
        if (!inputValidation.isInputEditTextFilled((TextInputEditText) usuario, lusers, getString(R.string.error_message_error))){
            return;
        }
        if (!inputValidation.isInputEditTextFilled((TextInputEditText) password, lpass, getString(R.string.error_message_error))){
            return;
        }
        if (myDB.LoginUsers(usuario.getText().toString().trim(), password.getText().toString().trim())){

            ActualizacionAplicacion();

        } else{
            Toast.makeText(this, "Usuario o password incorrectos", Toast.LENGTH_SHORT).show();
        }
    }


    private void DescargaUsuarios(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIUsuarios, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    Log.i("CONEXION USUARIOS OK","Respuesta: "+response);
                    String json;

                    json= response.toString();
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(json);
                    Usuarios usuarios = new Usuarios();

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        usuarios.log_id = jsonObject.getString("id");
                        usuarios.nombre = jsonObject.getString("nombre");
                        usuarios.apellido = jsonObject.getString("apellido");
                        usuarios.username = jsonObject.getString("username");
                        usuarios.password = jsonObject.getString("password");
                        usuarios.unegocio = jsonObject.getString("unegocio");
                        usuarios.estado = jsonObject.getString("estado");

                        SQLiteDatabase db = myDB.getWritableDatabase();

                        Cursor cursor = db.rawQuery("SELECT log_id, estado, nombre, apellido, username, password,unegocio FROM usuarios WHERE log_id ='"+usuarios.log_id+"'",null);

                        if (cursor.getCount() <=0){
                            //EL USUARIO NO SE ENCUENTRA EN LA DB
                            myDB.RegistroUsuarios(usuarios.log_id,usuarios.nombre,usuarios.apellido,usuarios.username,usuarios.password,usuarios.unegocio,usuarios.estado);

                        }else{
                            if (cursor.moveToFirst() == true){
                                String nombre = cursor.getString(2);
                                String apellido = cursor.getString(3);
                                String username = cursor.getString(4);
                                String password = cursor.getString(5);
                                String unegocio = cursor.getString(6);
                                if (usuarios.estado.toString().equals("INACTIVO")){

                                    db.execSQL("DELETE FROM usuarios WHERE log_id='"+usuarios.log_id+"'");
                                }
                                else{
                                    if (username != usuarios.nombre){
                                        db.execSQL("UPDATE usuarios SET username='"+usuarios.username.toLowerCase()+"' WHERE log_id='"+usuarios.log_id+"'");
                                    }
                                    if (nombre != usuarios.nombre){
                                        db.execSQL("UPDATE usuarios SET nombre='"+usuarios.nombre+"' WHERE log_id='"+usuarios.log_id+"'");
                                    }
                                    if (apellido != usuarios.apellido){
                                        db.execSQL("UPDATE usuarios SET apellido='"+usuarios.apellido+"' WHERE log_id='"+usuarios.log_id+"'");
                                    }
                                    if (password != usuarios.password){
                                        db.execSQL("UPDATE usuarios SET password='"+usuarios.password+"' WHERE log_id='"+usuarios.log_id+"'");
                                    }
                                    if (unegocio != usuarios.unegocio){
                                        db.execSQL("UPDATE usuarios SET unegocio='"+usuarios.unegocio+"' WHERE log_id='"+usuarios.log_id+"'");
                                    }
                                }
                            }
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.i("ERROR USUARIOS:",error.toString());
            }
        });
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void guardarCredenciales(){

        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String username = usuario.getText().toString();


        SQLiteDatabase db = myDB.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT log_id, nombre, apellido,unegocio FROM usuarios WHERE username ='"+username+"'",null);

        if (cursor.moveToFirst() == true){

            String log_idusers = cursor.getString(0);
            String nameusers = cursor.getString(1);
            String lastnameusers = cursor.getString(2);
            String unegocio = cursor.getString(3);

            SharedPreferences.Editor editor =preferences.edit();
            editor.putString("LOG_ID", log_idusers);
            editor.putString("NAME", nameusers);
            editor.putString("LASTNAME",lastnameusers);
            editor.putString("UNEGOCIO",unegocio);

            editor.commit();
        }
    }


    private void DescargadePatentes(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIVEHICULOS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.i("CONEXION API PATENTES: ",response.toString());
                    String json;
                    json = response.toString();
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(json);
                    Vehiculos vehiculos = new Vehiculos();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        vehiculos.id = jsonObject.getString("id");
                        vehiculos.patente = jsonObject.getString("patente");
                        vehiculos.marca = jsonObject.getString("marca");
                        vehiculos.propietario = jsonObject.getString("propietario");
                        vehiculos.estado = jsonObject.getString("estado");
                        vehiculos.m3 = jsonObject.getString("m3");

                        SQLiteDatabase db = myDB.getWritableDatabase();

                        if (myDB.ExisteVehiculo(vehiculos.id ) == false){
                            //LA PATENTE NO SE ENCUENTRA REGISTRADA
                            myDB.RegistroVehiculos(vehiculos.id,vehiculos.patente,vehiculos.estado,vehiculos.marca,
                                    vehiculos.propietario,vehiculos.m3);

                        }else{
                            Cursor cursor = db.rawQuery("SELECT id, patente, marca, propietario, m3 FROM vehiculos WHERE id ='"+vehiculos.id+"'",null);
                            if (cursor.moveToFirst() == true){
                                String patente = cursor.getString(1);
                                String marca = cursor.getString(2);
                                String propietario = cursor.getString(3);
                                String m3 = cursor.getString(4);

                                if (patente != vehiculos.patente){
                                    db.execSQL("UPDATE vehiculos SET patente='"+vehiculos.patente+"' WHERE id='"+vehiculos.id+"'");
                                }
                                if (vehiculos.estado.equalsIgnoreCase("INACTIVO")){
                                    db.execSQL("DELETE FROM vehiculos WHERE id='"+vehiculos.id+"'");
                                }
                                if (marca != vehiculos.marca){
                                    db.execSQL("UPDATE vehiculos SET marca='"+vehiculos.marca+"' WHERE id='"+vehiculos.id+"'");
                                }
                                if (propietario != vehiculos.propietario){
                                    db.execSQL("UPDATE vehiculos SET propietario='"+vehiculos.propietario+"' WHERE id='"+vehiculos.id+"'");
                                }
                                if (m3 != vehiculos.m3){
                                    db.execSQL("UPDATE vehiculos SET m3='"+vehiculos.m3+"' WHERE id='"+vehiculos.id+"'");
                                }

                            }


                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("CONEXION API PATENTES:",error.toString());
            }
        });
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void ActualizacionAplicacion(){

        progressDialog.setTitle("Actualizando Informacion");
        progressDialog.setMessage("Obteniendo datos......");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    DescargaChoferes();
                    DescargadePatentes();
                    Thread.sleep(5000);
                    progressDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, FormularioRecepcion_Activity.class);
                    startActivity(intent);
                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void DescargaChoferes(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APICHOFERES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.i("CONEXION API CHOFERES: ",response.toString());
                    String json;
                    json = response.toString();
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(json);
                    Choferes choferes = new Choferes();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        choferes.id = jsonObject.getString("id");
                        choferes.nombre = jsonObject.getString("nombre");
                        choferes.apellido = jsonObject.getString("apellido");

                        SQLiteDatabase db = myDB.getWritableDatabase();

                        if (myDB.ExisteChofer(choferes.id ) == false){
                            //CHOFER NO REGISTRADO
                            myDB.RegistroChoferes(choferes.id,choferes.nombre,choferes.apellido);

                        }else{
                            Cursor cursor = db.rawQuery("SELECT id, nombre, apellido FROM choferes WHERE id ='"+choferes.id+"'",null);
                            if (cursor.moveToFirst() == true){
                                String nombre = cursor.getString(1);
                                String apellido = cursor.getString(2);

                                if (nombre != choferes.nombre){
                                    db.execSQL("UPDATE choferes SET nombre='"+choferes.nombre+"' WHERE id='"+choferes.id+"'");
                                }
                                if (apellido != choferes.apellido){
                                    db.execSQL("UPDATE choferes SET apellido='"+choferes.apellido+"' WHERE id='"+choferes.id+"'");
                                }

                            }


                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("CONEXION API CHOFERES:",error.toString());
            }
        });
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

}
