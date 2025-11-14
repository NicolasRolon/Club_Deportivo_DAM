package com.example.club_deportivo_dam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FirstAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_app)

        val etName = findViewById<EditText>(R.id.etName)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)

        // Configura el listener para el botón de iniciar sesión.
        btnIniciarSesion.setOnClickListener {
            val user = etName.text.toString()
            val pass = etPassword.text.toString()

            // Valida que los campos no estén vacíos.
            if (user.isNotEmpty() && pass.isNotEmpty()) {
                val admin = AdminSQLiteOpenHelper(this)
                // Verifica las credenciales en la base de datos.
                if (admin.checkUser(user, pass)) {
                    // Si son correctas, navega al menú principal.
                    val intent = Intent(this, Activity_MenuPrincipal::class.java)
                    startActivity(intent)
                } else {
                    // Si son incorrectas, muestra un mensaje de error.
                    Toast.makeText(this, "El usuario o contraseña son incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Si los campos están vacíos, pide al usuario que los complete.
                Toast.makeText(this, "Por favor, ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
