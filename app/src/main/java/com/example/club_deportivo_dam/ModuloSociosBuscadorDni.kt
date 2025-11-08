package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ModuloSociosBuscadorDni : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modulo_socios_buscador_dni)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etDni = findViewById<EditText>(R.id.etDni)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnAtras = findViewById<Button>(R.id.btnAtras)
        val btnBorrarCliente = findViewById<Button>(R.id.btnBorrarCliente)

        val tvNombreResultado = findViewById<TextView>(R.id.tvNombreResultado)
        val tvApellidoResultado = findViewById<TextView>(R.id.tvApellidoResultado)
        val tvMailResultado = findViewById<TextView>(R.id.tvMailResultado)
        val tvCondicionResultado = findViewById<TextView>(R.id.tvCondicionResultado)

        btnBuscar.setOnClickListener {
            val dni = etDni.text.toString()

            if (dni.isNotEmpty()) {
                val admin = AdminSQLiteOpenHelper(this, "club_deportivo.db", null, 1)
                val cliente = admin.getClientePorDni(dni)

                if (cliente != null) {
                    tvNombreResultado.text = cliente.nombre
                    tvApellidoResultado.text = cliente.apellido
                    tvMailResultado.text = cliente.mail
                    tvCondicionResultado.text = if (cliente.esSocio) "Socio" else "No Socio"
                } else {
                    // Limpiamos los campos si no se encuentra el cliente
                    tvNombreResultado.text = ""
                    tvApellidoResultado.text = ""
                    tvMailResultado.text = ""
                    tvCondicionResultado.text = ""
                    Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        btnBorrarCliente.setOnClickListener {
            val dni = etDni.text.toString()

            if (dni.isNotEmpty()) {
                val admin = AdminSQLiteOpenHelper(this, "club_deportivo.db", null, 1)
                val deletedRows = admin.deleteCliente(dni)

                if (deletedRows > 0) {
                    Toast.makeText(this, "Cliente borrado exitosamente", Toast.LENGTH_SHORT).show()
                    // Limpiar campos
                    etDni.text.clear()
                    tvNombreResultado.text = ""
                    tvApellidoResultado.text = ""
                    tvMailResultado.text = ""
                    tvCondicionResultado.text = ""
                } else {
                    Toast.makeText(this, "No se encontró un cliente con ese DNI para borrar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI para borrar", Toast.LENGTH_SHORT).show()
            }
        }

        btnAtras.setOnClickListener {
            finish()
        }
    }
}