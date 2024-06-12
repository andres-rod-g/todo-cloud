package com.andresrodr.todocloud;

import com.andresrodr.todocloud.classes.Task;
import com.andresrodr.todocloud.classes.User;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TaskController {
    @FXML
    protected TextField createTaskField;

    @FXML
    protected VBox taskPanel; // Contenedor para las tareas

    @FXML
    protected PieChart taskPieChart; // PieChart para las estadísticas

    @FXML
    protected VBox taskTab;

    @FXML
    protected VBox statsTab;

    // As soon as the controller is created, delete the example task element
    public void initialize() {
        // Load existing tasks for the current user
        loadTasks();
    }

    @FXML
    protected void onExitButtonClick() {
        System.exit(0);
    }

    @FXML
    protected void onMinimizeButtonClick(Event event) {
        // Minimize the window
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    protected void onKeyPressedWhileWrittingTask(KeyEvent event) {
        // If key was Enter, and the field is not empty, create the task, and createTaskField is the focused field
        if (event.getEventType().getName().equals("KEY_PRESSED") && event.getCode().toString().equals("ENTER") && !createTaskField.getText().isEmpty() && createTaskField.isFocused()) {
            System.out.println("Create the task");

            // Create the task
            createTask(createTaskField.getText());
            createTaskField.clear(); // Clear the text field after creating the task
        }
    }

    @FXML
    protected void onExportToPdfButtonClick() {
        // Usar FileChooser para permitir al usuario seleccionar la ruta y el nombre del archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("tasks_list.pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                createPdfFromTasks(Task.findByCurrentUser(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onExportToCsvButtonClick() {
        // Usar FileChooser para permitir al usuario seleccionar la ruta y el nombre del archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("tasks_list.csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                createCsvFromTasks(Task.findByCurrentUser(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createPdfFromTasks(List<Task> tasks, File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(25, 750);

        contentStream.showText("Lista de Tareas");
        contentStream.newLine();
        contentStream.newLine();

        for (Task task : tasks) {
            contentStream.showText("- " + task.getTitle() + " [" + task.getState() + "]");
            contentStream.newLine();
        }

        contentStream.endText();
        contentStream.close();

        document.save(file);
        document.close();

        System.out.println("PDF guardado en: " + file.getAbsolutePath());
    }

    private void createCsvFromTasks(List<Task> tasks, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Escribir encabezado
            writer.append("ID,UserID,Title,CreatedAt,UpdatedAt,State\n");

            // Escribir filas de datos
            for (Task task : tasks) {
                writer.append(String.valueOf(task.getId())).append(",");
                writer.append(String.valueOf(task.getUserId())).append(",");
                writer.append(task.getTitle()).append(",");
                writer.append(task.getCreatedAt().toString()).append(",");
                writer.append(task.getUpdatedAt().toString()).append(",");
                writer.append(task.getState()).append("\n");
            }
        }

        System.out.println("CSV guardado en: " + file.getAbsolutePath());
    }

    @FXML
    protected void onLogoutButtonClick() {
        // Limpiar el currentUser
        User.logout();

        // Cargar la vista de inicio de sesión
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Stage stage = (Stage) taskPanel.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para crear una tarea
    private void createTask(String taskTitle) {
        Task task = new Task(taskTitle, "pending");
        if (task.save()) {
            System.out.println("Task created: " + task.getTitle());
            addTaskToUI(task);
            updatePieChart();
        } else {
            System.out.println("Failed to create task");
        }
    }

    // Método para cargar las tareas existentes en la interfaz de usuario
    private void loadTasks() {
        List<Task> tasks = Task.findByCurrentUser();
        for (Task task : tasks) {
            addTaskToUI(task);
        }
        updatePieChart(); // Actualizar el gráfico después de cargar las tareas
    }

    // Método para agregar una tarea a la interfaz de usuario
    private void addTaskToUI(Task task) {
        try {
            // Load the task element FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("task-element.fxml"));
            AnchorPane taskElement = loader.load();

            // Get the controller for the task element
            TaskElementController controller = loader.getController();

            // Set the task details
            controller.taskCheckBox.setText(task.getTitle());
            controller.taskCheckBox.setSelected(task.getState().equals("completed"));
            controller.taskId.setText(String.valueOf(task.getId()));

            // Add the cloned element to the task panel
            taskPanel.getChildren().add(taskElement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para actualizar el gráfico de pastel
    private void updatePieChart() {
        List<Task> tasks = Task.findByCurrentUser();
        long completedCount = tasks.stream().filter(task -> task.getState().equals("completed")).count();
        long pendingCount = tasks.size() - completedCount;

        PieChart.Data completedData = new PieChart.Data("Completadas", completedCount);
        PieChart.Data pendingData = new PieChart.Data("Pendientes", pendingCount);

        taskPieChart.getData().clear();
        taskPieChart.getData().addAll(completedData, pendingData);
    }

    @FXML
    protected void onTaskTabClick() {
        taskTab.setVisible(true);
        statsTab.setVisible(false);
    }

    @FXML
    protected void onStatsTabClick() {
        updatePieChart();
        taskTab.setVisible(false);
        statsTab.setVisible(true);
    }
}
