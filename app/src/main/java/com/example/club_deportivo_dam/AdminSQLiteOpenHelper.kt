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

    /**
     * Busca un cliente por su DNI en ambas tablas (socios y noSocio).
     * @return Un objeto Cliente si se encuentra, o null si no existe.
     */
    fun getClientePorDni(dni: String): Cliente? {
        val db = this.readableDatabase
        // Primero, busca en la tabla de socios
        var cursor = db.rawQuery("SELECT nombre, apellido, mail FROM socios WHERE dni=?", arrayOf(dni))
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val apellido = cursor.getString(1)
            val mail = cursor.getString(2)
            cursor.close()
            db.close()
            return Cliente(nombre, apellido, mail, true) // Es socio
        }
        cursor.close() // Cerramos el primer cursor si no encontró nada

        // Si no es socio, busca en la tabla de no socios
        cursor = db.rawQuery("SELECT nombre, apellido, mail FROM noSocio WHERE dni=?", arrayOf(dni))
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val apellido = cursor.getString(1)
            val mail = cursor.getString(2)
            cursor.close()
            db.close()
            return Cliente(nombre, apellido, mail, false) // No es socio
        }
        cursor.close() // Cerramos el segundo cursor
        db.close() // Cerramos la base de datos
        return null // No se encontró en ninguna tabla
    }
}
