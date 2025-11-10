package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegistrarCuotaActivity : BaseActivity() { // Hereda de BaseActivity

    private lateinit var etDniSocio: EditText
    private lateinit var etMontoCuota: EditText
    private lateinit var btnConfirmarCuota: Button
    private lateinit var btnAtras: Button
    private lateinit var admin: AdminSQLiteOpenHelper
    private var socioDni: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cuota)

        etDniSocio = findViewById(R.id.etDniSocio)
        etMontoCuota = findViewById(R.id.etMontoCuota)
        btnConfirmarCuota = findViewById(R.id.btnConfirmarCuota)
        btnAtras = findViewById(R.id.btnAtras)

        admin = AdminSQLiteOpenHelper(this)

        socioDni = intent.getLongExtra("SOCIO_DNI", -1)

        if (socioDni == -1L) {
            Toast.makeText(this, "Error: No se pudo obtener el DNI del socio", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        etDniSocio.setText(socioDni.toString())

        btnConfirmarCuota.setOnClickListener {
            registrarCuota()
        }

        btnAtras.setOnClickListener {
            finish()
        }
    }

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

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaPago = sdf.format(Date()) // Fecha actual

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30) // Añadir 30 días
        val fechaVencimiento = sdf.format(calendar.time)

        try {
            admin.addCuota(socioDni, monto, fechaPago, fechaVencimiento)
            Toast.makeText(this, "Cuota registrada exitosamente", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al registrar la cuota: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}