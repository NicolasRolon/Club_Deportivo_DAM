package com.example.club_deportivo_dam

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

// Esta es nuestra Activity "Maestra".
// Contiene la lógica común para el menú de opciones.
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflamos el menú (main_menu.xml) que creamos.
        // Esto añade la opción "Cerrar Sesión" a la barra de la aplicación.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Verificamos si la opción que se ha pulsado es la de "Cerrar Sesión".
        return when (item.itemId) {
            R.id.action_logout -> {
                // Creamos el Intent para ir a la pantalla de login.
                val intent = Intent(this, FirstAppActivity::class.java)
                // Añadimos los flags para limpiar el historial de pantallas.
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                // Cerramos la actividad actual.
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}