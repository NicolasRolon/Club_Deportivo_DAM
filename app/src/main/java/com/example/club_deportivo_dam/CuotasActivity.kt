package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class CuotasActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuotas)

        val lvMorosos = findViewById<ListView>(R.id.lvMorosos)
        val btnAtras = findViewById<Button>(R.id.btnAtras)

        // Obtiene los datos de los socios morosos desde la base de datos.
        val admin = AdminSQLiteOpenHelper(this)
        val listaMorosos = admin.getMorosos()

        // Formatea los datos para mostrarlos en el ListView.
        val listaMorososString = mutableListOf<String>()
        if (listaMorosos.isNotEmpty()) {
            for (socio in listaMorosos) {
                val texto = "DNI: ${socio.dni}\n${socio.nombre} ${socio.apellido}\nVencimiento: ${socio.vencimiento}"
                listaMorososString.add(texto)
            }
        } else {
            listaMorososString.add("¡Felicidades! No hay socios morosos.")
        }

        // Crea el adaptador y lo asigna al ListView.
        // Uso un layout personalizado (list_item_black_text) para que el texto se vea negro.
        val adapter = ArrayAdapter(this, R.layout.list_item_black_text, listaMorososString)
        lvMorosos.adapter = adapter

        // Configura el botón para volver atrás.
        btnAtras.setOnClickListener {
            finish() // Cierra la actividad actual.
        }
    }
}