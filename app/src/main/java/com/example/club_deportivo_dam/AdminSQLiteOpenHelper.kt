package com.example.club_deportivo_dam

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Estructuras de datos para manejar la información de forma ordenada.
data class Cliente(val nombre: String, val apellido: String, val mail: String, val esSocio: Boolean)
data class SocioMoroso(val dni: Long, val nombre: String, val apellido: String, val vencimiento: String)
data class PagoNoSocio(val id: Int, val dni: Long, val monto: Double, val fecha: String?)
data class PagoSocio(val id: Int, val dni: Long, val monto: Double, val fecha: String?, val vencimiento: String?)

// Clase principal para gestionar la base de datos SQLite.
class AdminSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    // Define constantes para la base de datos.
    companion object {
        const val DATABASE_VERSION = 7 // Versión actualizada
        const val DATABASE_NAME = "club_deportivo.db"
    }

    // Constructor secundario para facilitar la inicialización.
    constructor(context: Context) : this(context, DATABASE_NAME, null, DATABASE_VERSION)

    // Crea las tablas de la base de datos por primera vez.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios(user TEXT PRIMARY KEY, pass TEXT)")
        db.execSQL("CREATE TABLE socios(dni INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, mail TEXT)")
        db.execSQL("CREATE TABLE noSocio(dni INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, mail TEXT)")
        db.execSQL("CREATE TABLE cuota(cuota_id INTEGER PRIMARY KEY AUTOINCREMENT, socio_dni INTEGER, monto REAL, fecha_pago TEXT, fecha_vencimiento TEXT, FOREIGN KEY(socio_dni) REFERENCES socios(dni))")
        db.execSQL("CREATE TABLE pago(pago_id INTEGER PRIMARY KEY AUTOINCREMENT, nosocio_dni INTEGER, monto REAL, fecha_pago TEXT, FOREIGN KEY(nosocio_dni) REFERENCES noSocio(dni))")
        // Inserta datos de prueba.
        db.execSQL("INSERT INTO socios (dni, nombre, apellido, mail) VALUES (12345678, 'Juan', 'Pérez', 'juanperez12@gmail.com')")
        db.execSQL("INSERT INTO cuota (socio_dni, monto, fecha_pago, fecha_vencimiento) VALUES (12345678, 100.0, '2023-06-01', '2023-07-01')")

        // Inserta el usuario administrador por defecto.
        val values = ContentValues()
        values.put("user", "admin")
        values.put("pass", "admin")
        db.insert("usuarios", null, values)
    }

    // Actualiza la base de datos si la versión cambia. Elimina las tablas antiguas y las vuelve a crear.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS noSocio")
        db.execSQL("DROP TABLE IF EXISTS cuota")
        db.execSQL("DROP TABLE IF EXISTS pago")
        onCreate(db)
    }

    // --- Funciones de escritura ---

    // Agrega un nuevo socio a la tabla `socios`.
    fun addSocio(dni: Long, nombre: String, apellido: String, mail: String) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("dni", dni)
                put("nombre", nombre)
                put("apellido", apellido)
                put("mail", mail)
            }
            db.insert("socios", null, values)
        }
    }

    // Agrega un nuevo no-socio a la tabla `noSocio`.
    fun addNoSocio(dni: Long, nombre: String, apellido: String, mail: String) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("dni", dni)
                put("nombre", nombre)
                put("apellido", apellido)
                put("mail", mail)
            }
            db.insert("noSocio", null, values)
        }
    }

    // Registra una nueva cuota para un socio en la tabla `cuota`.
    fun addCuota(socioDni: Long, monto: Double, fechaPago: String, fechaVencimiento: String) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("socio_dni", socioDni)
                put("monto", monto)
                put("fecha_pago", fechaPago)
                put("fecha_vencimiento", fechaVencimiento)
            }
            db.insert("cuota", null, values)
        }
    }

    // Registra un nuevo pago para un no-socio en la tabla `pago`.
    fun addPago(noSocioDni: Long, monto: Double, fechaPago: String) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("nosocio_dni", noSocioDni)
                put("monto", monto)
                put("fecha_pago", fechaPago)
            }
            db.insert("pago", null, values)
        }
    }

    // Elimina un cliente (socio o no-socio) de la base de datos usando su DNI.
    fun deleteCliente(dni: Long): Int {
        return writableDatabase.use { db ->
            var deletedRows = db.delete("socios", "dni=?", arrayOf(dni.toString()))
            if (deletedRows == 0) {
                deletedRows = db.delete("noSocio", "dni=?", arrayOf(dni.toString()))
            }
            deletedRows
        }
    }


    // --- Funciones de lectura ---

    // Verifica si un usuario y contraseña existen en la tabla `usuarios`.
    fun checkUser(user: String, pass: String): Boolean {
        readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM usuarios WHERE user=? AND pass=?", arrayOf(user, pass)).use { cursor ->
                return cursor.count > 0
            }
        }
    }

    // Busca un cliente por DNI en las tablas `socios` y `noSocio`.
    fun getClientePorDni(dni: Long): Cliente? {
        readableDatabase.use { db ->
            db.rawQuery("SELECT nombre, apellido, mail FROM socios WHERE dni=?", arrayOf(dni.toString())).use { cursor ->
                if (cursor.moveToFirst()) {
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                    val mail = cursor.getString(cursor.getColumnIndexOrThrow("mail"))
                    return Cliente(nombre, apellido, mail, true)
                }
            }

            db.rawQuery("SELECT nombre, apellido, mail FROM noSocio WHERE dni=?", arrayOf(dni.toString())).use { cursor ->
                if (cursor.moveToFirst()) {
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                    val mail = cursor.getString(cursor.getColumnIndexOrThrow("mail"))
                    return Cliente(nombre, apellido, mail, false)
                }
            }
        }
        return null
    }

    // Verifica si un DNI ya existe en la tabla `socios` o `noSocio`.
    fun dniExiste(dni: Long): Boolean {
        readableDatabase.use { db ->
            db.rawQuery("SELECT dni FROM socios WHERE dni=?", arrayOf(dni.toString())).use { cursor ->
                if (cursor.count > 0) return true
            }
            db.rawQuery("SELECT dni FROM noSocio WHERE dni=?", arrayOf(dni.toString())).use { cursor ->
                if (cursor.count > 0) return true
            }
        }
        return false
    }

    // Obtiene una lista de todos los socios con cuotas vencidas o que nunca pagaron.
    fun getMorosos(): List<SocioMoroso> {
        val morosos = mutableListOf<SocioMoroso>()
        val query = "SELECT s.dni, s.nombre, s.apellido, MAX(c.fecha_vencimiento) as vencimiento FROM socios s LEFT JOIN cuota c ON s.dni = c.socio_dni GROUP BY s.dni HAVING MAX(c.fecha_vencimiento) IS NULL OR date(MAX(c.fecha_vencimiento)) < date('now', 'localtime')"

        readableDatabase.use { db ->
            db.rawQuery(query, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    val dniCol = cursor.getColumnIndexOrThrow("dni")
                    val nombreCol = cursor.getColumnIndexOrThrow("nombre")
                    val apellidoCol = cursor.getColumnIndexOrThrow("apellido")
                    val vencimientoCol = cursor.getColumnIndexOrThrow("vencimiento")
                    do {
                        val dni = cursor.getLong(dniCol)
                        val nombre = cursor.getString(nombreCol)
                        val apellido = cursor.getString(apellidoCol)
                        val vencimiento = cursor.getString(vencimientoCol) ?: "Nunca pagó"
                        morosos.add(SocioMoroso(dni, nombre, apellido, vencimiento))
                    } while (cursor.moveToNext())
                }
            }
        }
        return morosos
    }

    // Obtiene el historial de pagos de todos los socios.
    fun getPagosSocios(): List<PagoSocio> {
        val pagos = mutableListOf<PagoSocio>()
        val query = "SELECT cuota_id, socio_dni, monto, fecha_pago, fecha_vencimiento FROM cuota ORDER BY fecha_pago DESC"

        readableDatabase.use { db ->
            db.rawQuery(query, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    val idCol = cursor.getColumnIndexOrThrow("cuota_id")
                    val dniCol = cursor.getColumnIndexOrThrow("socio_dni")
                    val montoCol = cursor.getColumnIndexOrThrow("monto")
                    val fechaCol = cursor.getColumnIndexOrThrow("fecha_pago")
                    val vencCol = cursor.getColumnIndexOrThrow("fecha_vencimiento")

                    do {
                        val id = cursor.getInt(idCol)
                        val dni = cursor.getLong(dniCol)
                        val monto = cursor.getDouble(montoCol)
                        val fecha = cursor.getString(fechaCol)
                        val vencimiento = cursor.getString(vencCol)
                        pagos.add(PagoSocio(id, dni, monto, fecha, vencimiento))
                    } while (cursor.moveToNext())
                }
            }
        }
        return pagos
    }

    // Obtiene el historial de pagos de todos los no-socios.
    fun getPagosNoSocios(): List<PagoNoSocio> {
        val pagos = mutableListOf<PagoNoSocio>()
        val query = "SELECT pago_id, nosocio_dni, monto, fecha_pago FROM pago ORDER BY fecha_pago DESC"

        readableDatabase.use { db ->
            db.rawQuery(query, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    val idCol = cursor.getColumnIndexOrThrow("pago_id")
                    val dniCol = cursor.getColumnIndexOrThrow("nosocio_dni")
                    val montoCol = cursor.getColumnIndexOrThrow("monto")
                    val fechaCol = cursor.getColumnIndexOrThrow("fecha_pago")

                    do {
                        val id = cursor.getInt(idCol)
                        val dni = cursor.getLong(dniCol)
                        val monto = cursor.getDouble(montoCol)
                        val fecha = cursor.getString(fechaCol)
                        pagos.add(PagoNoSocio(id, dni, monto, fecha))
                    } while (cursor.moveToNext())
                }
            }
        }
        return pagos
    }
}