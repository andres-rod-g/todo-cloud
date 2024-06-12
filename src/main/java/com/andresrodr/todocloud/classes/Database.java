package com.andresrodr.todocloud.classes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static Connection connect(){
        // Connect to the database
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/todocloud", "root", "");
        } catch (SQLException e){
            System.out.println("Error al conectar a la base de datos");
            e.printStackTrace();
            return null;
        }
    }
}
