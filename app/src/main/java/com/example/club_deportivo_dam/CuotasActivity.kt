package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class CuotasActivity : BaseActivity() { // Hereda de BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuotas)

        val lvMorosos = findViewById<ListView>(R.id.lvMorosos)
        val btnAtras = findViewById<Button>(R.id.btnAtras)

        // 1. Obtener los datos de la base de datos
        val admin = AdminSQLiteOpenHelper(this)
        val listaMorosos = admin.getMorosos()

        // 2. Formatear los datos para el ListView
        val listaMorososString = mutableListOf<String>()
        if (listaMorosos.isNotEmpty()) {
            for (socio in listaMorosos) {
                val texto = "DNI: ${socio.dni}\n${socio.nombre} ${socio.apellido}\nVencimiento: ${socio.vencimiento}"
                listaMorososString.add(texto)
            }
        } else {
            listaMorososString.add("¡Felicidades! No hay socios morosos!.")
        }

        // 3. Crear el adaptador y asignarlo al ListView
        // Usamos nuestro layout personalizado para asegurar que el texto sea negro
        val adapter = ArrayAdapter(this, R.layout.list_item_black_text, listaMorososString)
        lvMorosos.adapter = adapter

        // 4. Configurar el botón de atrás
        btnAtras.setOnClickListener {
            finish()
        }
    }
}