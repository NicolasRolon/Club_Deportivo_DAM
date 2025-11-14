package com.example.club_deportivo_dam

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGenerator(private val context: Context) {

    fun generarCarnetPdf(nombre: String, apellido: String, dni: Long) {
        // --- Conversión de DP a Píxeles --- 
        val dpi = context.resources.displayMetrics.density
        val widthInPixels = (400 * dpi).toInt()
        val heightInPixels = (225 * dpi).toInt()

        // 1. Inflar el layout XML del carnet
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.carnet_layout, null)

        // 2. Rellenar los datos en la vista
        val nombreCompleto = "$nombre $apellido"
        view.findViewById<TextView>(R.id.nombre_socio_carnet).text = nombreCompleto
        view.findViewById<TextView>(R.id.dni_socio_carnet).text = dni.toString()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaAlta = "Miembro desde: ${sdf.format(Date())}"
        view.findViewById<TextView>(R.id.fecha_alta_socio_carnet).text = fechaAlta

        // 3. Medir la vista con el tamaño correcto en PÍXELES
        view.measure(
            View.MeasureSpec.makeMeasureSpec(widthInPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightInPixels, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        // 4. Crear el documento PDF
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.measuredWidth, view.measuredHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // 5. Dibujar la vista en el lienzo del PDF
        view.draw(canvas)
        document.finishPage(page)

        // 6. Guardar el archivo PDF
        val fileName = "carnet_${nombre}_${apellido}.pdf"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Método moderno para Android 10 y superior (no necesita permisos)
                savePdfWithMediaStore(document, fileName)
            } else {
                // Método antiguo para versiones anteriores a Android 10
                savePdfLegacy(document, fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar el PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun savePdfWithMediaStore(document: PdfDocument, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use { outputStream ->
                document.writeTo(outputStream)
            }
            Toast.makeText(context, "Carnet guardado en Descargas", Toast.LENGTH_LONG).show()
        } else {
            throw Exception("URI del MediaStore fue nula")
        }
    }

    private fun savePdfLegacy(document: PdfDocument, fileName: String) {
        // Comprobar si tenemos permiso para escribir. Si no, no podemos continuar.
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Error: Permiso de almacenamiento no concedido.", Toast.LENGTH_LONG).show()
            // En una app real, aquí se iniciaría la petición de permiso al usuario.
            return
        }

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use {
            document.writeTo(it)
        }
        Toast.makeText(context, "Carnet guardado en Descargas", Toast.LENGTH_LONG).show()
    }
}