package com.example.tvcontrolcenter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val bgColors = listOf(
        0xFF101820.toInt(),
        0xFF172A3A.toInt(),
        0xFF25213B.toInt(),
        0xFF18352F.toInt()
    )
    private var bgIndex = 0
    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    private fun buildUi() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 24, 36, 24)
            setBackgroundColor(bgColors[bgIndex])
            isFocusableInTouchMode = true
        }

        val title = TextView(this).apply {
            text = "TV CONTROL CENTER"
            textSize = 28f
            setTextColor(0xFFFFFFFF.toInt())
        }
        val subtitle = TextView(this).apply {
            text = "Sony Google TV · herramientas seguras"
            textSize = 16f
            setTextColor(0xFFB8C7D9.toInt())
            setPadding(0, 0, 0, 20)
        }
        root.addView(title)
        root.addView(subtitle)

        val scroll = ScrollView(this)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        addAction("Cambiar fondo de esta app") {
            bgIndex = (bgIndex + 1) % bgColors.size
            root.setBackgroundColor(bgColors[bgIndex])
        }
        addAction("Elegir imagen (vista previa)") {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            try {
                startActivityForResult(intent, 100)
            } catch (_: Exception) {
                toast("No se pudo abrir el selector de imágenes.")
            }
        }
        addAction("Ajustes de privacidad / anuncios") {
            openSettingsIntent(Intent(Settings.ACTION_PRIVACY_SETTINGS))
        }
        addAction("Ajustes de aplicaciones") {
            openSettingsIntent(Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS))
        }
        addAction("Ajustes de red / DNS privado") {
            openSettingsIntent(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        addAction("Administrar aplicaciones (incluye apps del sistema)") {
            showApps()
        }
        addAction("Información y límites") {
            AlertDialog.Builder(this)
                .setTitle("Qué puede hacer esta versión")
                .setMessage(
                    "Esta app puede mostrar aplicaciones que Android permita consultar " +
                    "y abrir la información de cada una. En esa pantalla, usa " +
                    "Desinstalar o Deshabilitar si el sistema ofrece esa opción. " +
                    "Algunas apps protegidas por Sony o Google TV no se pueden quitar " +
                    "desde una app normal. El cambio de fondo solo afecta a esta app."
                )
                .setPositiveButton("Entendido", null)
                .show()
        }

        setContentView(root)
        content.getChildAt(0)?.requestFocus()
    }

    private fun addAction(label: String, action: () -> Unit) {
        val button = Button(this).apply {
            text = label
            textSize = 16f
            isFocusable = true
            isFocusableInTouchMode = true
            setOnClickListener { action() }
        }
        content.addView(
            button,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        )
    }

    private fun openSettingsIntent(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (_: Exception) {
                toast("Este ajuste no está disponible en este dispositivo.")
            }
        }
    }

    private fun showApps() {
        val pm = packageManager
        val apps = try {
            pm.getInstalledApplications(0)
                .filter { it.packageName != packageName }
                .sortedBy { pm.getApplicationLabel(it).toString().lowercase() }
        } catch (_: Exception) {
            emptyList()
        }

        if (apps.isEmpty()) {
            toast(
                "Android no mostró aplicaciones. Revisa el permiso QUERY_ALL_PACKAGES " +
                "en AndroidManifest.xml."
            )
            return
        }

        val labels = apps.map { app ->
            val name = pm.getApplicationLabel(app).toString()
            val isSystem =
                (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            if (isSystem) "$name · Sistema" else "$name · Instalada"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Administrar aplicaciones")
            .setItems(labels) { _, which ->
                val app = apps[which]
                val infoIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${app.packageName}")
                )
                try {
                    startActivity(infoIntent)
                } catch (_: Exception) {
                    toast("No se pudo abrir la información de esta app.")
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    @Deprecated("Compatibilidad con Android 10–12")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data?.data != null) {
            toast("Imagen seleccionada. Esta versión aún no la aplica como fondo.")
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
