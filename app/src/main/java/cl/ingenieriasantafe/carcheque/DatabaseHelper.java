package cl.ingenieriasantafe.carcheque;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "carchequer.db";
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_PLANTAS = "plantas";
    public static final String TABLA_VEHICULOS = "vehiculos";

    //USUARIOS TABLE
    public static final int ID = 0;
    public static final String LOG_ID = "log_id";
    public static final String NOMBRE = "nombre";
    public static final String APELLIDO = "apellido";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String UNEGOCIO = "unegocio";
    public static final String ESTADO = "estado";

    //PLANTAS
    public static final String ID_LOG_PLANTAS = "id";
    public static final String NOMBRE_LOG_PLANTA = "nombre";
    public static final String UBICACION_PLANTA = "ubicacion";

    //VEHICULOS
    public static final String ID_VEHICULOS = "id";
    public static final String PATENTE = "patente";
    public static final String ESTADO_VEHICULO = "estado";
    public static final String PROPIETARIO = "propietario";
    public static final String MARCA = "marca";
    public static final String M3 = "m3";

    //CHOFERES
    public static final String ID_CHOFERES="id";
    public static final String NOMBRE_CHOFER="nombre";
    public static final String APELLIDO_CHOFER="apellido";

    //REGISTROS - RECEPCION MATERIAL
    public static final String ID_RECEPCION_MATERIAL = "id";
    public static final String PLANTA_RECEPCION_MATERIAL = "planta";
    public static final String MATERIAL_RECEPCION_MATERIAL = "tipomaterial";
    public static final String PATENTE_RECEPCION_MATERIAL = "patente";
    public static final String M3_RECEPCION_MATERIAL = "m3";
    public static final String KM_RECEPCION_MATERIAL = "km";
    public static final String OBRA_RECEPCION_MATERIAL = "obra";
    public static final String CAMINO_RECEPCION_MATERIAL ="camino";
    public static final String FECHA_RECEPCION_MATERIAL = "fecha";
    public static final String HORA_RECEPCION_MATERIAL = "hora";
    public static final String CHOFER_RECEPCION_MATERIAL = "chofer";
    public static final String USUARIO_RECEPCION_MATERIAL = "usuario";
    public static final String ESTADO_RECEPCION_MATERIAL = "estado";

    final String CREAR_TABLA_USUARIOS = "CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT,log_id TEXT unique, nombre TEXT, apellido TEXT, username TEXT, password TEXT, estado TEXT, unegocio TEXT)";
    final String CREAR_TABLA_LISTADO_PLANTAS = "CREATE TABLE plantas (id TEXT unique, nombre TEXT, ubicacion TEXT)";
    final String CREAR_TABLA_VEHICULOS = "CREATE TABLE vehiculos (id TEXT unique, patente TEXT unique, estado TEXT, propietario TEXT, marca TEXT, m3 TEXT)";
    final String CREAR_TABLA_RECEPCION = "CREATE TABLE recepcion (id INTEGER PRIMARY KEY AUTOINCREMENT, planta TEXT, tipomaterial TEXT, patente TEXT, m3 TEXT, km TEXT, obra TEXT, camino TEXT, fecha TEXT, hora TEXT, usuario TEXT, chofer TEXT, estado TEXT)";
    final String CREAR_TABLA_CHOFERES = "CREATE TABLE choferes (id TEXT unique, nombre TEXT, apellido TEXT)";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null,7);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_USUARIOS);
        db.execSQL(CREAR_TABLA_LISTADO_PLANTAS);
        db.execSQL(CREAR_TABLA_VEHICULOS);
        db.execSQL(CREAR_TABLA_RECEPCION);
        db.execSQL(CREAR_TABLA_CHOFERES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");


        db.execSQL(CREAR_TABLA_USUARIOS);
    }


    public boolean LoginUsers(String username, String password){

        String[] columns = {
                LOG_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = USERNAME + " = ? " + " AND " + PASSWORD + " = ? ";

        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLA_USUARIOS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0 ){
            return true;
        }
        return false;
    }

    public boolean RegistroRecepcion(String planta, String tipomaterial, String patente, String m3, String km, String fecha, String hora, String username, String chofer, String estado,String obra, String camino){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(PLANTA_RECEPCION_MATERIAL, planta );
            contentValues.put(MATERIAL_RECEPCION_MATERIAL,tipomaterial);
            contentValues.put(PATENTE_RECEPCION_MATERIAL,patente);
            contentValues.put(M3_RECEPCION_MATERIAL, m3);
            contentValues.put(KM_RECEPCION_MATERIAL,km);
            contentValues.put(OBRA_RECEPCION_MATERIAL,obra);
            contentValues.put(CAMINO_RECEPCION_MATERIAL,camino);
            contentValues.put(FECHA_RECEPCION_MATERIAL, fecha);
            contentValues.put(HORA_RECEPCION_MATERIAL,hora);
            contentValues.put(USUARIO_RECEPCION_MATERIAL,username);
            contentValues.put(CHOFER_RECEPCION_MATERIAL, chofer);
            contentValues.put(ESTADO_RECEPCION_MATERIAL,estado);

            int resultado = (int) db.insert("recepcion",null,contentValues);
            db.close();
            if (resultado == -1){
                return false;
            }else{
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean RegistroUsuarios(String log_id, String nombre, String apellido, String username, String password,String unegocio, String estado){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(LOG_ID, log_id );
            contentValues.put(NOMBRE,nombre);
            contentValues.put(APELLIDO,apellido);
            contentValues.put(USERNAME, username);
            contentValues.put(PASSWORD,password);
            contentValues.put(UNEGOCIO,unegocio);
            contentValues.put(ESTADO, estado);

            db.insert("usuarios",null,contentValues);
            db.close();

               return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    public List<Vehiculos> search (String keyword){
        List<Vehiculos> vh = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT patente, m3 FROM vehiculos where patente like ?"
                    , new String[] {"%" + keyword + "%"});
            if (cursor.moveToFirst()){
                vh = new ArrayList<Vehiculos>();
                do {
                    Vehiculos vehiculos = new Vehiculos();
                    vehiculos.setPatente(cursor.getString(0));
                    vehiculos.setM3(cursor.getString(1));
                    vh.add(vehiculos);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            vh = null;
        }
        return vh;
    }

    public List<Choferes> searchchoferes (String keyword){
        List<Choferes> vh = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT nombre, apellido FROM choferes where nombre like ?"
                    , new String[] {"%" + keyword + "%"});
            if (cursor.moveToFirst()){
                vh = new ArrayList<Choferes>();
                do {
                    Choferes choferes = new Choferes();
                    choferes.setNombre(cursor.getString(0));
                    choferes.setApellido(cursor.getString(1));
                    vh.add(choferes);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            vh = null;
        }
        return vh;
    }


    public boolean ExisteVehiculo(String id){
        boolean VehiculoExiste = false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT patente FROM vehiculos WHERE id = '"+id+"'",null);        try {

            if (cursor.getCount()<=0){

                VehiculoExiste = false;
            }else{

                VehiculoExiste = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return VehiculoExiste;
    }

    public boolean RegistroVehiculos(String id, String patente, String estado, String marca, String propietario, String m3){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(ID_VEHICULOS, id );
            contentValues.put(PATENTE,patente);
            contentValues.put(ESTADO_VEHICULO,estado);
            contentValues.put(MARCA,marca);
            contentValues.put(PROPIETARIO,propietario);
            contentValues.put(M3,m3);

            db.insert("vehiculos",null,contentValues);
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean RegistroChoferes(String id,String nombre, String apellido ){
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(ID_CHOFERES, id );
            contentValues.put(NOMBRE_CHOFER,nombre);
            contentValues.put(APELLIDO_CHOFER,apellido);

            db.insert("choferes",null,contentValues);
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean ExisteChofer(String id){
        boolean ChoferExiste = false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM choferes WHERE id = '"+id+"'",null);
        try {

            if (cursor.getCount()<=0){

                ChoferExiste = false;
            }else{

                ChoferExiste = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return ChoferExiste;
    }


}
