package com.example.club_deportivo_dam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ModuloSociosBuscadorDni : BaseActivity() { // Hereda de BaseActivity

    private var clienteEncontrado: Cliente? = null
    private var dniEncontrado: Long = -1

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
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        val tvNombreResultado = findViewById<TextView>(R.id.tvNombreResultado)
        val tvApellidoResultado = findViewById<TextView>(R.id.tvApellidoResultado)
        val tvMailResultado = findViewById<TextView>(R.id.tvMailResultado)
        val tvCondicionResultado = findViewById<TextView>(R.id.tvCondicionResultado)

        btnBuscar.setOnClickListener {
            val dniStr = etDni.text.toString()
            // Limpiar resultados anteriores
            clienteEncontrado = null
            dniEncontrado = -1
            tvNombreResultado.text = ""
            tvApellidoResultado.text = ""
            tvMailResultado.text = ""
            tvCondicionResultado.text = ""

            if (dniStr.isNotEmpty()) {
                val dni = dniStr.toLongOrNull()
                if (dni == null) {
                    Toast.makeText(this, "El DNI ingresado no es válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val admin = AdminSQLiteOpenHelper(this)
                val cliente = admin.getClientePorDni(dni)

                if (cliente != null) {
                    clienteEncontrado = cliente
                    dniEncontrado = dni
                    tvNombreResultado.text = cliente.nombre
                    tvApellidoResultado.text = cliente.apellido
                    tvMailResultado.text = cliente.mail
                    tvCondicionResultado.text = if (cliente.esSocio) "Socio" else "No Socio"
                } else {
                    Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegistrar.setOnClickListener {
            if (clienteEncontrado == null || dniEncontrado == -1L) {
                Toast.makeText(this, "Primero debe buscar y encontrar un cliente válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (clienteEncontrado!!.esSocio) {
                val intent = Intent(this, RegistrarCuotaActivity::class.java)
                intent.putExtra("SOCIO_DNI", dniEncontrado)
                startActivity(intent)
            } else {
                val intent = Intent(this, RegistrarPagoActivity::class.java)
                intent.putExtra("NOSOCIO_DNI", dniEncontrado)
                startActivity(intent)
            }
        }

        btnBorrarCliente.setOnClickListener {
            val dniStr = etDni.text.toString()

            if (dniStr.isNotEmpty()) {
                val dni = dniStr.toLongOrNull()
                if (dni == null) {
                    Toast.makeText(this, "El DNI ingresado no es válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val admin = AdminSQLiteOpenHelper(this)
                val deletedRows = admin.deleteCliente(dni)

                if (deletedRows > 0) {
                    Toast.makeText(this, "Cliente borrado exitosamente", Toast.LENGTH_SHORT).show()
                    // Limpiar campos
                    etDni.text.clear()
                    tvNombreResultado.text = ""
                    tvApellidoResultado.text = ""
                    tvMailResultado.text = ""
                    tvCondicionResultado.text = ""
                    clienteEncontrado = null
                    dniEncontrado = -1
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