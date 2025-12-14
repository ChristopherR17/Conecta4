package com.christopher.conecta4

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var linearLayoutBotones: LinearLayout
    private lateinit var tvTurno: TextView
    private lateinit var btnReiniciar: Button

    private val filas = 6
    private val columnas = 7
    private var tablero = Array(filas) { Array(columnas) { 0 } }
    private var turnoRojo = true
    private var juegoTerminado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tableLayout = findViewById(R.id.tableLayout)
        linearLayoutBotones = findViewById(R.id.linearLayoutBotones)
        tvTurno = findViewById(R.id.tvTurno)
        btnReiniciar = findViewById(R.id.btnReiniciar)

        btnReiniciar.setOnClickListener {
            reiniciarJuego()
        }

        crearBotonesColumnas()
        crearTablero()
    }

    private fun crearBotonesColumnas() {
        linearLayoutBotones.removeAllViews()

        for (col in 0 until columnas) {
            val boton = Button(this).apply {
                text = (col + 1).toString()
                tag = col
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                setOnClickListener {
                    if (!juegoTerminado) {
                        soltarFicha(col)
                    }
                }
            }
            linearLayoutBotones.addView(boton)
        }
    }

    private fun crearTablero() {
        tableLayout.removeAllViews()

        for (fila in 0 until filas) {
            val tableRow = TableRow(this).apply {
                layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )
            }

            for (col in 0 until columnas) {
                val imageView = ImageView(this).apply {
                    setImageResource(R.drawable.ficha_vacia)
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(2, 2, 2, 2)
                    }
                }
                tableRow.addView(imageView)
            }
            tableLayout.addView(tableRow)
        }
    }

    private fun soltarFicha(columna: Int) {
        for (fila in filas - 1 downTo 0) {
            if (tablero[fila][columna] == 0) {
                tablero[fila][columna] = if (turnoRojo) 1 else 2
                actualizarFichaVista(fila, columna)

                if (verificarGanador(fila, columna)) {
                    juegoTerminado = true
                    val ganador = if (turnoRojo) "Rojo" else "Amarillo"
                    mostrarMensajeGanador(ganador)
                } else if (tableroLleno()) {
                    juegoTerminado = true
                    mostrarMensajeEmpate()
                } else {
                    turnoRojo = !turnoRojo
                    actualizarTurno()
                }
                return
            }
        }
        Toast.makeText(this, "Columna llena!", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarFichaVista(fila: Int, columna: Int) {
        val tableRow = tableLayout.getChildAt(fila) as TableRow
        val imageView = tableRow.getChildAt(columna) as ImageView

        when (tablero[fila][columna]) {
            1 -> imageView.setImageResource(R.drawable.ficha_roja)
            2 -> imageView.setImageResource(R.drawable.ficha_amarilla)
        }
    }

    private fun verificarGanador(fila: Int, columna: Int): Boolean {
        val jugador = tablero[fila][columna]

        var contador = 1
        for (c in columna - 1 downTo maxOf(columna - 3, 0)) {
            if (tablero[fila][c] == jugador) contador++ else break
        }
        for (c in columna + 1 until minOf(columna + 4, columnas)) {
            if (tablero[fila][c] == jugador) contador++ else break
        }
        if (contador >= 4) return true

        contador = 1
        for (f in fila - 1 downTo maxOf(fila - 3, 0)) {
            if (tablero[f][columna] == jugador) contador++ else break
        }
        for (f in fila + 1 until minOf(fila + 4, filas)) {
            if (tablero[f][columna] == jugador) contador++ else break
        }
        if (contador >= 4) return true

        contador = 1
        var f = fila - 1
        var c = columna - 1
        while (f >= 0 && c >= 0 && f >= fila - 3 && c >= columna - 3) {
            if (tablero[f][c] == jugador) contador++ else break
            f--
            c--
        }
        f = fila + 1
        c = columna + 1
        while (f < filas && c < columnas && f <= fila + 3 && c <= columna + 3) {
            if (tablero[f][c] == jugador) contador++ else break
            f++
            c++
        }
        if (contador >= 4) return true

        contador = 1
        f = fila - 1
        c = columna + 1
        while (f >= 0 && c < columnas && f >= fila - 3 && c <= columna + 3) {
            if (tablero[f][c] == jugador) contador++ else break
            f--
            c++
        }
        f = fila + 1
        c = columna - 1
        while (f < filas && c >= 0 && f <= fila + 3 && c >= columna - 3) {
            if (tablero[f][c] == jugador) contador++ else break
            f++
            c--
        }
        return contador >= 4
    }

    private fun tableroLleno(): Boolean {
        for (fila in 0 until filas) {
            for (col in 0 until columnas) {
                if (tablero[fila][col] == 0) return false
            }
        }
        return true
    }

    private fun actualizarTurno() {
        val jugador = if (turnoRojo) "Rojo" else "Amarillo"
        tvTurno.text = "Turno: $jugador"
    }

    private fun mostrarMensajeGanador(ganador: String) {
        AlertDialog.Builder(this)
            .setTitle("¡Juego Terminado!")
            .setMessage("¡El jugador $ganador ha ganado!")
            .setPositiveButton("Reiniciar") { dialog, _ ->
                reiniciarJuego()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun mostrarMensajeEmpate() {
        AlertDialog.Builder(this)
            .setTitle("¡Juego Terminado!")
            .setMessage("¡Empate!")
            .setPositiveButton("Reiniciar") { dialog, _ ->
                reiniciarJuego()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun reiniciarJuego() {
        tablero = Array(filas) { Array(columnas) { 0 } }

        for (fila in 0 until filas) {
            val tableRow = tableLayout.getChildAt(fila) as TableRow
            for (col in 0 until columnas) {
                val imageView = tableRow.getChildAt(col) as ImageView
                imageView.setImageResource(R.drawable.ficha_vacia)
            }
        }

        turnoRojo = true
        juegoTerminado = false
        actualizarTurno()
    }
}