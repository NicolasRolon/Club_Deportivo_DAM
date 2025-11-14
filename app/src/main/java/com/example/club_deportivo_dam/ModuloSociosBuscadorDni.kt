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

class ModuloSociosBuscadorDni : BaseActivity() {

    private var clienteEncontrado: Cliente? = null
    private var dniEncontrado: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modulo_socios_buscador_dni)
        // Ajusta el padding de la vista principal para que no se superponga con las barras del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtiene las referencias a las vistas del layout.
        val etDni = findViewById<EditText>(R.id.etDni)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnAtras = findViewById<Button>(R.id.btnAtras)
        val btnBorrarCliente = findViewById<Button>(R.id.btnBorrarCliente)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        val tvNombreResultado = findViewById<TextView>(R.id.tvNombreResultado)
        val tvApellidoResultado = findViewById<TextView>(R.id.tvApellidoResultado)
        val tvMailResultado = findViewById<TextView>(R.id.tvMailResultado)
        val tvCondicionResultado = findViewById<TextView>(R.id.tvCondicionResultado)

        // Configura el listener para el botón de buscar.
        btnBuscar.setOnClickListener {
            val dniStr = etDni.text.toString()
            // Limpia los resultados de la búsqueda anterior.
            limpiarResultados()

            if (dniStr.isNotEmpty()) {
                val dni = dniStr.toLongOrNull()
                if (dni == null) {
                    Toast.makeText(this, "El DNI ingresado no es válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val admin = AdminSQLiteOpenHelper(this)
                val cliente = admin.getClientePorDni(dni)

                // Si se encuentra el cliente, actualiza la UI con sus datos.
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

        // Configura el listener para el botón de registrar pago o cuota.
        btnRegistrar.setOnClickListener {
            if (clienteEncontrado == null || dniEncontrado == -1L) {
                Toast.makeText(this, "Primero debe buscar y encontrar un cliente válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Decide a qué pantalla navegar según si el cliente es socio o no.
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

        // Configura el listener para el botón de borrar cliente.
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
                    limpiarResultados()
                    etDni.text.clear()
                } else {
                    Toast.makeText(this, "No se encontró un cliente con ese DNI para borrar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI para borrar", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura el listener para el botón de atrás.
        btnAtras.setOnClickListener {
            finish()
        }
    }

    // Limpia los campos de texto de los resultados de la búsqueda.
    private fun limpiarResultados() {
        findViewById<TextView>(R.id.tvNombreResultado).text = ""
        findViewById<TextView>(R.id.tvApellidoResultado).text = ""
        findViewById<TextView>(R.id.tvMailResultado).text = ""
        findViewById<TextView>(R.id.tvCondicionResultado).text = ""
        clienteEncontrado = null
        dniEncontrado = -1
    }
}