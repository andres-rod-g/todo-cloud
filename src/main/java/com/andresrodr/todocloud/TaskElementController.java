package com.andresrodr.todocloud;

import com.andresrodr.todocloud.classes.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TaskElementController {

    @FXML
    public CheckBox taskCheckBox;

    @FXML
    public ImageView trashIcon;

    @FXML
    public Label taskId;

    @FXML
    public AnchorPane taskElement;

    @FXML
    public void initialize() {
        // Add click event to the trash icon
        trashIcon.setOnMouseClicked(event -> deleteTask());

        // Add event listener to the checkbox to update task state
        taskCheckBox.setOnAction(event -> updateTaskState());
    }

    private void deleteTask() {
        int id = Integer.parseInt(taskId.getText());
        Task task = Task.findById(id);
        if (task != null && task.delete()) {
            // Remove the task element from the UI
            ((VBox) taskElement.getParent()).getChildren().remove(taskElement);
        } else {
            System.out.println("Failed to delete task");
        }
    }

    private void updateTaskState() {
        int id = Integer.parseInt(taskId.getText());
        Task task = Task.findById(id);
        if (task != null) {
            task.setState(taskCheckBox.isSelected() ? "completed" : "pending");
            if (!task.update()) {
                System.out.println("Failed to update task state");
            }
        }
    }
}
