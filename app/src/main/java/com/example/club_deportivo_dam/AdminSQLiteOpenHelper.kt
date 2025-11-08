package com.example.club_deportivo_dam

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data classes para empaquetar la información de forma ordenada
data class Cliente(val nombre: String, val apellido: String, val mail: String, val esSocio: Boolean)
data class SocioMoroso(val dni: String, val nombre: String, val apellido: String, val vencimiento: String)

class AdminSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios(user TEXT PRIMARY KEY, pass TEXT)")
        db.execSQL("CREATE TABLE socios(dni TEXT PRIMARY KEY, nombre TEXT, apellido TEXT, mail TEXT)")
        db.execSQL("CREATE TABLE noSocio(dni TEXT PRIMARY KEY, nombre TEXT, apellido TEXT, mail TEXT)")
        // Tabla para registrar los pagos de cuotas de los socios
        db.execSQL("CREATE TABLE cuota(cuota_id INTEGER PRIMARY KEY AUTOINCREMENT, socio_dni TEXT, monto REAL, fecha_pago TEXT, fecha_vencimiento TEXT, FOREIGN KEY(socio_dni) REFERENCES socios(dni))")

        val values = ContentValues()
        values.put("user", "admin")
        values.put("pass", "admin")
        db.insert("usuarios", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS noSocio")
        db.execSQL("DROP TABLE IF EXISTS cuota")
        onCreate(db)
    }

    // ... (funciones addSocio, addNoSocio, etc. se mantienen igual)

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

    /**
     * Registra el pago de una cuota para un socio.
     */
    fun addCuota(socioDni: String, monto: Double, fechaPago: String, fechaVencimiento: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("socio_dni", socioDni)
        values.put("monto", monto)
        values.put("fecha_pago", fechaPago)
        values.put("fecha_vencimiento", fechaVencimiento)
        db.insert("cuota", null, values)
        db.close()
    }

    /**
     * Obtiene una lista de todos los socios cuya cuota ha vencido o que nunca han pagado.
     * @return Una lista de objetos SocioMoroso.
     */
    fun getMorosos(): List<SocioMoroso> {
        val morosos = mutableListOf<SocioMoroso>()
        val db = this.readableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = sdf.format(Date())

        // Consulta SQL para encontrar socios con cuotas vencidas o sin pagos.
        val query = """
            SELECT s.dni, s.nombre, s.apellido, MAX(c.fecha_vencimiento)
            FROM socios s
            LEFT JOIN cuota c ON s.dni = c.socio_dni
            GROUP BY s.dni
            HAVING MAX(c.fecha_vencimiento) IS NULL OR MAX(c.fecha_vencimiento) < ?
            """
        
        val cursor = db.rawQuery(query, arrayOf(fechaActual))

        if (cursor.moveToFirst()) {
            do {
                val dni = cursor.getString(0)
                val nombre = cursor.getString(1)
                val apellido = cursor.getString(2)
                val vencimiento = cursor.getString(3) ?: "Nunca pagó"
                morosos.add(SocioMoroso(dni, nombre, apellido, vencimiento))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return morosos
    }
}