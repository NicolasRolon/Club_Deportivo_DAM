package com.example.club_deportivo_dam

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Data class para empaquetar la información del cliente de forma ordenada
data class Cliente(val nombre: String, val apellido: String, val mail: String, val esSocio: Boolean)

class AdminSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios(user TEXT PRIMARY KEY, pass TEXT)")
        db.execSQL("CREATE TABLE socios(dni TEXT PRIMARY KEY, nombre TEXT, apellido TEXT, mail TEXT)")
        db.execSQL("CREATE TABLE noSocio(dni TEXT PRIMARY KEY, nombre TEXT, apellido TEXT, mail TEXT)")

        val values = ContentValues()
        values.put("user", "admin")
        values.put("pass", "admin")
        db.insert("usuarios", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS noSocio")
        onCreate(db)
    }

    fun checkUser(user: String, pass: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE user=? AND pass=?", arrayOf(user, pass))
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

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

    fun getClientePorDni(dni: String): Cliente? {
        val db = this.readableDatabase
        var cursor = db.rawQuery("SELECT nombre, apellido, mail FROM socios WHERE dni=?", arrayOf(dni))
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val apellido = cursor.getString(1)
            val mail = cursor.getString(2)
            cursor.close()
            db.close()
            return Cliente(nombre, apellido, mail, true) // Es socio
        }
        cursor.close()

        cursor = db.rawQuery("SELECT nombre, apellido, mail FROM noSocio WHERE dni=?", arrayOf(dni))
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val apellido = cursor.getString(1)
            val mail = cursor.getString(2)
            cursor.close()
            db.close()
            return Cliente(nombre, apellido, mail, false) // No es socio
        }
        cursor.close()
        db.close()
        return null
    }

    /**
     * Verifica si un DNI ya existe en la tabla de socios o no socios.
     * @return True si el DNI ya existe, False en caso contrario.
     */
    fun dniExiste(dni: String): Boolean {
        val db = this.readableDatabase
        var cursor = db.rawQuery("SELECT dni FROM socios WHERE dni=?", arrayOf(dni))
        var existe = cursor.count > 0
        cursor.close()

        if (existe) {
            db.close()
            return true
        }

        cursor = db.rawQuery("SELECT dni FROM noSocio WHERE dni=?", arrayOf(dni))
        existe = cursor.count > 0
        cursor.close()
        
        db.close()
        return existe
    }
}
