package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrarPagoActivity : BaseActivity() {

    private lateinit var etDniNoSocio: EditText
    private lateinit var etMontoPago: EditText
    private lateinit var btnConfirmarPago: Button
    private lateinit var btnAtras: Button
    private lateinit var admin: AdminSQLiteOpenHelper
    private var noSocioDni: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_pago)

        // Obtiene las referencias a las vistas del layout.
        etDniNoSocio = findViewById(R.id.etDniNoSocio)
        etMontoPago = findViewById(R.id.etMontoPago)
        btnConfirmarPago = findViewById(R.id.btnConfirmarPago)
        btnAtras = findViewById(R.id.btnAtras)

        admin = AdminSQLiteOpenHelper(this)

        // Obtiene el DNI del no socio pasado desde la actividad anterior.
        noSocioDni = intent.getLongExtra("NOSOCIO_DNI", -1)

        // Si el DNI no es válido, muestra un error y cierra la actividad.
        if (noSocioDni == -1L) {
            Toast.makeText(this, "Error: No se pudo obtener el DNI del no socio", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Muestra el DNI del no socio en el campo de texto.
        etDniNoSocio.setText(noSocioDni.toString())

        // Configura el listener para el botón de confirmar.
        btnConfirmarPago.setOnClickListener {
            registrarPago()
        }

        // Configura el listener para el botón de atrás.
        btnAtras.setOnClickListener {
            finish()
        }
    }

    // Registra el pago en la base de datos.
    private fun registrarPago() {
        val montoStr = etMontoPago.text.toString()
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoStr.toDoubleOrNull()
        if (monto == null) {
            Toast.makeText(this, "El monto ingresado no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtiene la fecha actual para registrar el pago.
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaPago = sdf.format(Date())

        // Guarda el pago en la base de datos.
        try {
            admin.addPago(noSocioDni, monto, fechaPago)
            Toast.makeText(this, "Pago registrado exitosamente", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad después de registrar.
        } catch (e: Exception) {
            Toast.makeText(this, "Error al registrar el pago: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}