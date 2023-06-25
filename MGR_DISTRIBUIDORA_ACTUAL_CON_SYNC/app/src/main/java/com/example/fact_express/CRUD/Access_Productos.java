package com.example.fact_express.CRUD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fact_express.Conexion.DatabaseOpenHelper;

public class Access_Productos {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static Access_Productos instance;
    Cursor registros = null;
    Cursor Filtrar=null;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    public Access_Productos(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static Access_Productos getInstance(Context context) {
        if (instance == null) {
            instance = new Access_Productos(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void openWritable() {
        this.db = openHelper.getWritableDatabase();
    }

    public void openReadable() {
        this.db = openHelper.getReadableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (db != null) {
            this.db.close();
        }
    }

    public Cursor getProductos(){
        this.openWritable();
        registros = db.rawQuery("Select * from v_productos order by idproducto asc", null);
        return registros;
    }

    public Cursor getFiltrarProductos(String texto){
        this.openReadable();
        Filtrar = db.rawQuery("select * from v_productos where cod_interno like '%"+texto+"' or " +
                "cod_barra like '%"+texto+"' or descripcion like '%"+texto+"%'order by idproducto asc", null);
        return  Filtrar;
    }

    public long insertarProductos(String cod_interno, String cod_barra, String descripcion, String precio, int idunidad, int iddivision, int idiva){
        ContentValues values = new ContentValues();
        values.put("cod_interno", cod_interno);
        values.put("cod_barra", cod_barra);
        values.put("descripcion", descripcion);
        values.put("precio_venta", precio);
        values.put("stock", 0);
        values.put("estado", "S");
        values.put("um_idunidad", idunidad);
        values.put("division_iddivision", iddivision);
        values.put("iva_idiva", idiva);

        long insertado = db.insert("productos",null,values);

        return insertado;
    }
    public long InsertarProductosServer(int idproducto,String cod_interno, String cod_barra, String descripcion,
                                        String precio, int stock, String estado, int idunidad, int iddivision, int idiva,
                                        String prom, String cant_prom, int precio_prom, String porc_prom){
        ContentValues values = new ContentValues();
        values.put("idproducto", idproducto);
        values.put("cod_interno", cod_interno);
        values.put("cod_barra", cod_barra);
        values.put("descripcion", descripcion);
        values.put("precio_venta", precio);
        values.put("stock", stock);
        values.put("estado", estado);
        values.put("um_idunidad", idunidad);
        values.put("division_iddivision", iddivision);
        values.put("iva_idiva", idiva);
        values.put("prom", prom);
        values.put("cant_prom", cant_prom);
        values.put("precio_prom", precio_prom);
        values.put("porc_prom", porc_prom);

        long insertado= db.insert("productos",null,values);

        return insertado;
    }

    public Cursor getProducto_a_modificar(int productoEditar) {
        this.openReadable();
        registros = db.rawQuery("Select * from v_productos where idproducto="+productoEditar, null);
        return  registros;
    }

    public long ActualizarProduto(ContentValues values, int productoEditar) {
        this.openWritable();
        long accion = db.update("productos",values, "idproducto="+productoEditar,null);
        return accion;
    }
    public long EliminarProducto(int ID) {
        ContentValues values = new ContentValues();
        values.put("estado", "N");
        this.openWritable();
        long accion = db.update("productos",values, "idproducto ="+ID,null);
        return accion;
    }

    public void borrarProductos() {
        this.openWritable();
        db.execSQL("DELETE FROM productos");
        this.close();
    }

}
