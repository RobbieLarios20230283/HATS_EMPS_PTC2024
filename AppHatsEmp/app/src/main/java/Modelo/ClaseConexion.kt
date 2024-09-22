package Modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
    fun cadenaConexion():Connection? {


        try {
            val ip = "jdbc:oracle:thin:@192.168.0.6:1521:xe"
            val usuario = "SYSTEM"
            val contrasena = "1234567"

     val conexion = DriverManager.getConnection(ip, usuario, contrasena)
            return conexion
        }catch (e: Exception){
            println("El error es este: $e")
            return null
        }
    }
}