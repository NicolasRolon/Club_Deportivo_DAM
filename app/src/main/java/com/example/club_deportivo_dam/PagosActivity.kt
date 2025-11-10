package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog

class PagosActivity : BaseActivity() { // Hereda de BaseActivity

    private lateinit var lvPagos: ListView
    private lateinit var btnVerSocios: Button
    private lateinit var btnVerNoSocios: Button
    private lateinit var btnAtras: Button
    private lateinit var admin: AdminSQLiteOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos)

        lvPagos = findViewById(R.id.lvPagos)
        btnVerSocios = findViewById(R.id.btnVerSocios)
        btnVerNoSocios = findViewById(R.id.btnVerNoSocios)
        btnAtras = findViewById(R.id.btnAtras)

        admin = AdminSQLiteOpenHelper(this)

        // Cargar pagos de socios por defecto
        cargarPagosSocios()

        btnVerSocios.setOnClickListener {
            cargarPagosSocios()
        }

        btnVerNoSocios.setOnClickListener {
            cargarPagosNoSocios()
        }

        btnAtras.setOnClickListener {
            finish()
        }
    }

    private fun cargarPagosSocios() {
        val listaPagos = admin.getPagosSocios()
        val listaPagosString = mutableListOf<String>()

        if (listaPagos.isNotEmpty()) {
            for (pago in listaPagos) {
                val fechaPago = pago.fecha ?: "No disponible"
                val fechaVencimiento = pago.vencimiento ?: "No disponible"
                val texto = "ID: ${pago.id}\nDNI: ${pago.dni}\nMonto: $${pago.monto}\nFecha: $fechaPago\nVencimiento: $fechaVencimiento"
                listaPagosString.add(texto)
            }
        } else {
            listaPagosString.add("No hay pagos de socios registrados.")
        }

        val adapter = ArrayAdapter(this, R.layout.list_item_black_text, listaPagosString)
        lvPagos.adapter = adapter
    }

    private fun cargarPagosNoSocios() {
        val listaPagos = admin.getPagosNoSocios()
        val listaPagosString = mutableListOf<String>()

        if (listaPagos.isNotEmpty()) {
            for (pago in listaPagos) {
                val fechaPago = pago.fecha ?: "No disponible"
                val texto = "ID: ${pago.id}\nDNI: ${pago.dni}\nMonto: $${pago.monto}\nFecha: $fechaPago"
                listaPagosString.add(texto)
            }
        } else {
            listaPagosString.add("No hay pagos de no socios registrados.")
        }

        val adapter = ArrayAdapter(this, R.layout.list_item_black_text, listaPagosString)
        lvPagos.adapter = adapter
    }
}