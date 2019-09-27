package cl.ingenieriasantafe.carcheque;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SeleccionObra_Activity extends AppCompatActivity {

    protected static final String TAG = "TAG";
    private ListView listView;
    ProgressDialog progressDialog;
    public static final String APIVEHICULOS = "http://santafeinversiones.org/api/vehiculos";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    DatabaseHelper myDB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionobra);
        listView = (ListView)findViewById(R.id.listview);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.obras_string,android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        progressDialog = new ProgressDialog(SeleccionObra_Activity.this);
        myDB = new DatabaseHelper(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final String obra = (listView.getItemAtPosition(position).toString());

                AlertDialog.Builder builder = new AlertDialog.Builder(SeleccionObra_Activity.this);
                builder.setTitle("Confirmar obra: "+ obra);
                builder.setMessage("¿Estas seguro de confirmar tu obra?");
                builder.setPositiveButton("Aceptar",    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences preferences = getSharedPreferences("obra_app",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("obra",obra);
                        editor.commit();
                        ActualizacionAplicacion();
                        Toast.makeText(SeleccionObra_Activity.this, "Seleccionaste la obra: "+obra, Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
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


                    Thread.sleep(5000);
                    progressDialog.dismiss();
                    Intent intent = new Intent(SeleccionObra_Activity.this, FormularioRecepcion_Activity.class);
                    startActivity(intent);
                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
