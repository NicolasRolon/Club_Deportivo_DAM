package com.example.club_deportivo_dam

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        // Creamos la tabla de usuarios para el login
        db.execSQL("CREATE TABLE usuarios(user TEXT PRIMARY KEY, pass TEXT)")

        // Creamos la tabla para guardar los datos de los socios
        db.execSQL("CREATE TABLE socios(dni TEXT PRIMARY KEY, nombre TEXT, apellido TEXT, mail TEXT)")

        // Creamos la tabla para guardar los datos de los NO socios
        db.execSQL("CREATE TABLE noSocio(dni TEXT PRIMARY KEY, nombre TEXT, apellido TEXT, mail TEXT)")

        // Insertamos un usuario de ejemplo para el login
        val values = ContentValues()
        values.put("user", "admin")
        values.put("pass", "admin")
        db.insert("usuarios", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // En una futura versión, si necesitas cambiar la estructura, aquí se maneja.
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS noSocio")
        onCreate(db)
    }

    /**
     * Comprueba si un usuario y contraseña son correctos.
     */
    fun checkUser(user: String, pass: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE user=? AND pass=?", arrayOf(user, pass))
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    /**
     * Añade un nuevo socio a la tabla 'socios'.
     */
    fun addSocio(dni: String, nombre: String, apellido: String, mail: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("dni", dni)
        values.put("nombre", nombre)
        values.put("apellido", apellido)
        values.put("mail", mail)

        db.insert("socios", null, values)
        db.close()
    }

    /**
     * Añade un nuevo cliente a la tabla 'noSocio'.
     */
    fun addNoSocio(dni: String, nombre: String, apellido: String, mail: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("dni", dni)
        values.put("nombre", nombre)
        values.put("apellido", apellido)
        values.put("mail", mail)

        db.insert("noSocio", null, values)
        db.close()
    }
}
