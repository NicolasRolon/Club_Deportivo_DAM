package com.example.club_deportivo_dam

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

// Activity base que contiene la lógica común para el menú de opciones (ej: Cerrar Sesión).
// Heredo de esta clase en las demás Activities para reutilizar el menú.
abstract class BaseActivity : AppCompatActivity() {

    // Crea el menú de opciones en la ActionBar.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Infla el layout del menú (main_menu.xml) para mostrarlo.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Maneja los clics en los items del menú.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Si se presiona "Cerrar Sesión".
            R.id.action_logout -> {
                // Navega a la pantalla de login (FirstAppActivity).
                val intent = Intent(this, FirstAppActivity::class.java)
                // Limpia el historial de pantallas para que el usuario no pueda volver atrás.
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                // Cierra la actividad actual.
                finish()
                true
            }
            // Si es otro item, usa el comportamiento por defecto.
            else -> super.onOptionsItemSelected(item)
        }
    }
}