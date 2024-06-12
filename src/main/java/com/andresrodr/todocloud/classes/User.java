package com.andresrodr.todocloud.classes;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class User {
    private int id;
    private String email;
    private String password;

    // Variable estática para almacenar el usuario actual
    private static User currentUser;

    // Constructor
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor vacío
    public User() {}

    // Getters y setters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    // Método para hidratar el objeto User desde un ResultSet
    public static User hydrate(ResultSet rs) throws SQLException {
        User user = new User();
        user.id = rs.getInt("id");
        user.email = rs.getString("email");
        user.password = rs.getString("password");
        return user;
    }

    // Método para registrar un nuevo usuario con contraseña encriptada
    public static boolean register(String email, String password) {
        Connection conn = Database.connect();
        if (conn != null) {
            try {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                String query = "INSERT INTO user (email, password) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, hashedPassword);
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    // Si el registro es exitoso, almacenar el usuario registrado en currentUser
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        User user = new User(email, hashedPassword);
                        user.id = rs.getInt(1);
                        currentUser = user;
                    }
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Método para iniciar sesión con verificación de contraseña encriptada
    public static User login(String email, String password) {
        Connection conn = Database.connect();
        if (conn != null) {
            try {
                String query = "SELECT * FROM user WHERE email = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, storedHashedPassword)) {
                        // Si el inicio de sesión es exitoso, almacenar el usuario logueado en currentUser
                        currentUser = User.hydrate(rs);
                        return currentUser;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Método para actualizar el usuario
    public boolean update() {
        Connection conn = Database.connect();
        if (conn != null) {
            try {
                String query = "UPDATE user SET email = ?, password = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, this.email);
                stmt.setString(2, this.password);
                stmt.setInt(3, this.id);
                int affectedRows = stmt.executeUpdate();

                return affectedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Método para eliminar el usuario
    public boolean delete() {
        Connection conn = Database.connect();
        if (conn != null) {
            try {
                String query = "DELETE FROM user WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, this.id);
                int affectedRows = stmt.executeUpdate();

                return affectedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void logout() {
        currentUser = null;
    }

}
