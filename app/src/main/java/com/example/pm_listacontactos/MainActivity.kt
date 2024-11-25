package com.example.pm_listacontactos

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pm_listacontactos.ui.theme.PM_ListaContactosTheme
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class MainActivity : ComponentActivity() {
    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PM_ListaContactosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)
                    var nombre by rememberSaveable { mutableStateOf("")}
                    var correo by rememberSaveable { mutableStateOf("")}
                    var hayNombre by remember { mutableStateOf(true) }
                    var hayCorreo by remember { mutableStateOf(true) }
                    var contactos by remember {mutableStateOf<MutableList<Contacto>>(mutableListOf())}
                    val file = File(filesDir, "contactos.csv")

                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    cargarContactos(contactos, file)

                    var contactosActualizado by remember {mutableStateOf(contactos)}

                    Column(
                        modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre de contacto") },
                            singleLine =  true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = correo,
                            onValueChange = { correo = it },
                            label = { Text("Correo electrónico") },
                            singleLine =  true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (!hayCorreo || !hayNombre) {
                            Text(
                                text = "Rellena todos los campos!!!",
                                color = Color.Red
                            )
                        }

                        Button(
                            onClick = {
                                hayNombre = nombre.isNotBlank()
                                hayCorreo = correo.isNotBlank()

                                if (hayNombre && hayCorreo) {
                                    contactosActualizado.add(Contacto(nombre, correo))
                                    contactos = contactosActualizado.toMutableList()
                                    guardarContactos(contactos, file)
                                    correo = ""
                                    nombre = ""
                                }
                            }
                        ) {
                            Text(
                                text = "Agregar"
                            )
                        }

                        LazyColumn {
                            itemsIndexed(contactos) { indice, contacto ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Text(text = contacto.nombre)
                                    Text(text = contacto.mail)
                                    IconButton(
                                        onClick = {
                                            contactosActualizado.removeAt(indice)
                                            contactos = contactosActualizado.toMutableList()
                                            guardarContactos(contactos, file)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Eliminar"
                                        )
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun cargarContactos(contactos: MutableList<Contacto>, file: File) {
    val f_in = FileReader(file)
    val lineas = f_in.readLines()
    for (linea in lineas) {
        val palabras = linea.split(";")
        contactos.add(Contacto(palabras[0], palabras[1]))
    }
    f_in.close()
}

private fun guardarContactos(contactos: MutableList<Contacto>, file: File) {
    val f_out = FileWriter(file)
    for (contacto in contactos) {
        f_out.write("${contacto.nombre};${contacto.mail}\n")
    }
    f_out.close()
}

data class Contacto(val nombre: String, val mail: String)