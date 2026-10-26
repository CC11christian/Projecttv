

package com.example.tvcontrolcenter

import android.app.Activity

import android.app.AlertDialog

import android.content.Intent

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

            openSettingsIntent(

                Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)

            )

        }

        addAction("Ajustes de red / DNS privado") {

            openSettingsIntent(Intent(Settings.ACTION_WIRELESS_SETTINGS))

        }

        addAction("Ver aplicaciones que puedo abrir") {

            showApps()

        }

        addAction("Información y límites") {

            AlertDialog.Builder(this)

                .setTitle("Qué puede hacer esta versión")

                .setMessage(

                    "Esta app puede abrir ajustes del sistema y mostrar " +

                    "apps con icono de inicio. Para desinstalar una app, " +

                    "abre su información y usa la opción del sistema si " +

                    "está disponible. No puede quitar anuncios de todo " +

                    "Google TV ni borrar apps protegidas. El cambio de " +

                    "fondo solo afecta a esta app."

                )

                .setPositiveButton("Entendido", null)

                .show()

        }

        setContentView(root)

        root.requestFocus()

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

        val apps = (

            pm.queryIntentActivities(

                Intent(Intent.ACTION_MAIN)

                    .addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER), 0

            ) +

            pm.queryIntentActivities(

                Intent(Intent.ACTION_MAIN)

                    .addCategory(Intent.CATEGORY_LAUNCHER), 0

            )

        )

            .distinctBy { it.activityInfo.packageName }

            .sortedBy {

                it.loadLabel(pm).toString().lowercase()

            }

        if (apps.isEmpty()) {

            toast("No se encontraron aplicaciones visibles.")

            return

        }

        val labels = apps.map {

            it.loadLabel(pm).toString()

        }.toTypedArray()

        AlertDialog.Builder(this)

            .setTitle("Aplicaciones · selecciona para administrar")

            .setItems(labels) { _, which ->

                val pkg = apps[which].activityInfo.packageName

                val info = Intent(

                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,

                    Uri.parse("package:$pkg")

                )

                try {

                    startActivity(info)

                } catch (_: Exception) {

                    toast("No se pudo abrir la información de la app.")

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

        if (requestCode == 100 &&

            resultCode == RESULT_OK &&

            data?.data != null

        ) {

            toast(

                "Imagen seleccionada. Esta versión aún no la aplica como fondo."

            )

        }

    }

    private fun toast(message: String) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }

}
