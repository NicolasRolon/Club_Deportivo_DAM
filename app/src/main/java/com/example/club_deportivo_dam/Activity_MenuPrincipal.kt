package com.example.club_deportivo_dam

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity_MenuPrincipal : BaseActivity() { // Hereda de BaseActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)

        // 1. Botón Agregar Cliente
        val btnAgregarCliente = findViewById<ImageButton>(R.id.btnAgregarCliente)
        btnAgregarCliente.setOnClickListener {
            val intent = Intent(this, AgregarCliente::class.java)
            startActivity(intent)
        }

        // 2. Botón Ver Socios
        val btnVerSocios = findViewById<ImageButton>(R.id.btnVerSocios)
        btnVerSocios.setOnClickListener {
            val intent = Intent(this, ModuloSociosBuscadorDni::class.java)
            startActivity(intent)
        }

        // 3. Botón Cuotas
        val btnCuotas = findViewById<ImageButton>(R.id.btnCuotas)
        btnCuotas.setOnClickListener {
            val intent = Intent(this, CuotasActivity::class.java)
            startActivity(intent)
        }

        // 4. Botón Pagos
        val btnPagos = findViewById<ImageButton>(R.id.btnPagos)
        btnPagos.setOnClickListener {
            val intent = Intent(this, PagosActivity::class.java)
            startActivity(intent)
        }

        // El botón y la lógica de Cerrar Sesión se han movido a BaseActivity

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}