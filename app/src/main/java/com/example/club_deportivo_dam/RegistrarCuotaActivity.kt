package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegistrarCuotaActivity : BaseActivity() {

    private lateinit var etDniSocio: EditText
    private lateinit var etMontoCuota: EditText
    private lateinit var btnConfirmarCuota: Button
    private lateinit var btnAtras: Button
    private lateinit var admin: AdminSQLiteOpenHelper
    private var socioDni: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cuota)

        // Obtiene las referencias a las vistas del layout.
        etDniSocio = findViewById(R.id.etDniSocio)
        etMontoCuota = findViewById(R.id.etMontoCuota)
        btnConfirmarCuota = findViewById(R.id.btnConfirmarCuota)
        btnAtras = findViewById(R.id.btnAtras)

        admin = AdminSQLiteOpenHelper(this)

        // Obtiene el DNI del socio pasado desde la actividad anterior.
        socioDni = intent.getLongExtra("SOCIO_DNI", -1)

        // Si el DNI no es válido, muestra un error y cierra la actividad.
        if (socioDni == -1L) {
            Toast.makeText(this, "Error: No se pudo obtener el DNI del socio", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Muestra el DNI del socio en el campo de texto.
        etDniSocio.setText(socioDni.toString())

        // Configura el listener para el botón de confirmar.
        btnConfirmarCuota.setOnClickListener {
            registrarCuota()
        }

        // Configura el listener para el botón de atrás.
        btnAtras.setOnClickListener {
            finish()
        }
    }

    // Registra la cuota en la base de datos.
    private fun registrarCuota() {
        val montoStr = etMontoCuota.text.toString()
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoStr.toDoubleOrNull()
        if (monto == null) {
            Toast.makeText(this, "El monto ingresado no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Calcula la fecha de pago (hoy) y la fecha de vencimiento (en 30 días).
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaPago = sdf.format(Date())

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val fechaVencimiento = sdf.format(calendar.time)

        // Guarda la cuota en la base de datos.
        try {
            admin.addCuota(socioDni, monto, fechaPago, fechaVencimiento)
            Toast.makeText(this, "Cuota registrada exitosamente", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad después de registrar.
        } catch (e: Exception) {
            Toast.makeText(this, "Error al registrar la cuota: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}