package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarCliente : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etDni = findViewById<EditText>(R.id.etDni)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etMail = findViewById<EditText>(R.id.etMail)
        val cbEsSocio = findViewById<CheckBox>(R.id.cbEsSocio)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnAtras = findViewById<Button>(R.id.btnAtras)

        btnRegistrar.setOnClickListener {
            val dni = etDni.text.toString()
            val nombre = etNombre.text.toString()
            val apellido = etApellido.text.toString()
            val mail = etMail.text.toString()

            if (dni.isNotEmpty() && nombre.isNotEmpty() && apellido.isNotEmpty() && mail.isNotEmpty()) {
                val admin = AdminSQLiteOpenHelper(this)

                if (admin.dniExiste(dni)) {
                    Toast.makeText(this, "Ya existe un cliente con ese DNI", Toast.LENGTH_SHORT).show()
                } else {
                    try {
                        if (cbEsSocio.isChecked) {
                            admin.addSocio(dni, nombre, apellido, mail)
                            Toast.makeText(this, "Socio registrado exitosamente", Toast.LENGTH_SHORT).show()
                        } else {
                            admin.addNoSocio(dni, nombre, apellido, mail)
                            Toast.makeText(this, "Cliente (No Socio) registrado exitosamente", Toast.LENGTH_SHORT).show()
                        }

                        // Limpiamos todos los campos después del registro
                        etDni.text.clear()
                        etNombre.text.clear()
                        etApellido.text.clear()
                        etMail.text.clear()
                        cbEsSocio.isChecked = false

                    } catch (e: Exception) {
                        Toast.makeText(this, "Error al registrar el cliente: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnAtras.setOnClickListener {
            finish() // Cierra la actividad actual y vuelve a la anterior
        }
    }
}