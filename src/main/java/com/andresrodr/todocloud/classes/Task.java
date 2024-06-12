package com.andresrodr.todocloud.classes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private int id;
    private int userId;
    private String title;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String state;

    // Constructor
    public Task(String title, String state) {
        this.userId = User.getCurrentUser().getId();
        this.title = title;
        this.state = state;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor vacío
    public Task() {}

    // Getters y setters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    // Método para hidratar el objeto Task desde un ResultSet
    public static Task hydrate(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.id = rs.getInt("id");
        task.userId = rs.getInt("userId");
        task.title = rs.getString("title");
        task.createdAt = rs.getTimestamp("createdAt");
        task.updatedAt = rs.getTimestamp("updatedAt");
        task.state = rs.getString("state");
        return task;
    }

    // Método para guardar la tarea en la base de datos
    public boolean save() {
        Connection conn = Database.connect();
        if (conn != null) {
            try {
                String query = "INSERT INTO task (userId, title, createdAt, updatedAt, state) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, this.userId);
                stmt.setString(2, this.title);
                stmt.setTimestamp(3, this.createdAt);
                stmt.setTimestamp(4, this.updatedAt);
                stmt.setString(5, this.state);
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                    }
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Método para obtener todas las tareas del usuario actual
    public static List<Task> findByCurrentUser() {
        Connection conn = Database.connect();
        List<Task> tasks = new ArrayList<>();
        if (conn != null) {
            try {
                String query = "SELECT * FROM task WHERE userId = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, User.getCurrentUser().getId());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    tasks.add(Task.hydrate(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }

    // Método para actualizar la tarea
    public boolean update() {
        Connection conn = Database.connect();
        if (conn != null) {
            try {
                String query = "UPDATE task SET title = ?, updatedAt = ?, state = ? WHERE id = ? AND userId = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, this.title);
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setString(3, this.state);
                stmt.setInt(4, this.id);
                stmt.setInt(5, this.userId);
                int affectedRows = stmt.executeUpdate();

                return affectedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Task findById(int id) {
        Connection conn = Database.connect();
        if (conn != null) {
            try {
                String query = "SELECT * FROM task WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Task task = new Task();
                    task.id = rs.getInt("id");
                    task.userId = rs.getInt("userId");
                    task.title = rs.getString("title");
                    task.state = rs.getString("state");
                    return task;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean delete() {
        Connection conn = Database.connect();
        if (conn != null) {
            try {
                String query = "DELETE FROM task WHERE id = ?";
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
}
