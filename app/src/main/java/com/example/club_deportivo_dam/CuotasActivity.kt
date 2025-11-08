package com.example.club_deportivo_dam

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class CuotasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuotas)

        val lvMorosos = findViewById<ListView>(R.id.lvMorosos)
        val btnAtras = findViewById<Button>(R.id.btnAtras)

        // 1. Obtener los datos de la base de datos
        val admin = AdminSQLiteOpenHelper(this, "club_deportivo.db", null, 1)
        val listaMorosos = admin.getMorosos()

        // 2. Formatear los datos para el ListView
        val listaMorososString = mutableListOf<String>()
        if (listaMorosos.isNotEmpty()) {
            for (socio in listaMorosos) {
                val texto = "DNI: ${socio.dni}\n${socio.nombre} ${socio.apellido}\nVencimiento: ${socio.vencimiento}"
                listaMorososString.add(texto)
            }
        } else {
            listaMorososString.add("¡Felicidades! No hay ningun socio moroso.")
        }

        // 3. Crear el adaptador y asignarlo al ListView
        // Usamos un layout simple de Android para cada item de la lista
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaMorososString)
        lvMorosos.adapter = adapter

        // 4. Configurar el botón de atrás
        btnAtras.setOnClickListener {
            finish()
        }
    }
}