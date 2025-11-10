package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrarPagoActivity : BaseActivity() { // Hereda de BaseActivity

    private lateinit var etDniNoSocio: EditText
    private lateinit var etMontoPago: EditText
    private lateinit var btnConfirmarPago: Button
    private lateinit var btnAtras: Button
    private lateinit var admin: AdminSQLiteOpenHelper
    private var noSocioDni: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_pago)

        etDniNoSocio = findViewById(R.id.etDniNoSocio)
        etMontoPago = findViewById(R.id.etMontoPago)
        btnConfirmarPago = findViewById(R.id.btnConfirmarPago)
        btnAtras = findViewById(R.id.btnAtras)

        admin = AdminSQLiteOpenHelper(this)

        noSocioDni = intent.getLongExtra("NOSOCIO_DNI", -1)

        if (noSocioDni == -1L) {
            Toast.makeText(this, "Error: No se pudo obtener el DNI del no socio", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        etDniNoSocio.setText(noSocioDni.toString())

        btnConfirmarPago.setOnClickListener {
            registrarPago()
        }

        btnAtras.setOnClickListener {
            finish()
        }
    }

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

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaPago = sdf.format(Date()) // Fecha actual

        try {
            admin.addPago(noSocioDni, monto, fechaPago)
            Toast.makeText(this, "Pago registrado exitosamente", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al registrar el pago: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}