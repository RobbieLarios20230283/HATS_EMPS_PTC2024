package Modelo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
    suspend fun CadenaConexion(): Connection? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "jdbc:oracle:thin:@192.168.0.6:1521:xe"
                val usuario = "system"
                val contrasena = "1234567"
                DriverManager.getConnection(url, usuario, contrasena)
            } catch (e: Exception) {
                println("Error en la cadena de conexión: $e")
                null
            }
        }
    }
}