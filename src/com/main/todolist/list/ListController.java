package com.main.todolist.list;

import com.main.todolist.db.DB;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import model.Task;

public class ListController implements Initializable {

    private DB db = DB.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCategories();

        loadTableColumns();

        loadTableRows();
        
        dayTF.addEventHandler(KeyEvent.KEY_TYPED, allowOnlyDecimals(5));
    }

    @FXML
    private TextField taskTF;

    @FXML
    private ComboBox<String> categoryCB;

    @FXML
    private TextField dayTF;

    @FXML
    private DatePicker dateDP;

    @FXML
    private TableView<Task> tasksTable;

    @FXML
    private TableColumn<Task, Integer> idColumn;

    @FXML
    private TableColumn<Task, String> taskColumn;

    @FXML
    private TableColumn<Task, Integer> dayColumn;

    @FXML
    private TableColumn<Task, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Task, String> categoryColumn;

    @FXML
    private TableColumn<Task, String> statusColumn;

    @FXML
    private Button saveButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label warningsLabel;

    @FXML
    private Label rowCountLabel;

    @FXML
    private TextField searchTF;

    @FXML
    private Button solvedButton;

    @FXML
    private RadioButton allRadio;

    @FXML
    private RadioButton solvedRadio;

    @FXML
    private RadioButton notSolvedRadio;

    @FXML
    private Button saveCategoryButton;

    @FXML
    private Button deleteCategoryButton;

    @FXML
    private CheckBox deleteAllCheck;

    @FXML
    void allRadioSelected(ActionEvent event) {
        db.setFilter("");
        loadTableRows();
    }

    @FXML
    void deleteButtonPressed(ActionEvent event) {
        if(confirmDialog("Вы уверены?")){
             if (deleteAllCheck.isSelected()) {
            db.iud("truncate table tasks");
            msg("Все задачи удалены");
            loadTableRows();
        } else {

            if (tasksTable.getSelectionModel().getSelectedItem() == null) {
                msg("Задача не выбрана");
            } else {

                db.iud("delete from tasks where id=" + selectedId + "");
                msg("Задача удалена");
                loadTableRows();

            }

        }

        }
        
       
    }

    @FXML
    void deleteCategoryButtonPressed(ActionEvent event) {
        if(confirmDialog("Вы уверены?")){
        if (categoryCB.getSelectionModel().getSelectedItem() == null) {
            msg("В списке нету выбранной категории");
        } else {
            db.iud("delete from categories where category='" + categoryCB.getSelectionModel().getSelectedItem() + "'");
            msg("Категория успешно удалена");
            loadCategories();
        }
                 
        }
    }

    @FXML
    void notSolvedRadioSelected(ActionEvent event) {
        db.setFilter("where status='Не выполнено' ");
        loadTableRows();
    }

    @FXML
    void saveButtonPressed(ActionEvent event) {
        String task = taskTF.getText().trim();
        String taskDayString = dayTF.getText();
        String categoryString = categoryCB.getSelectionModel().getSelectedItem();
        LocalDate date = dateDP.getValue();

        if (task.equals("")) {
            msg("Задача не должна быть пустой");
        } else {
            if (taskDayString.equals("")) {
                msg("День Задачи должен быть назначен");
            } else {

                if (categoryString == null) {
                    msg("Категория не должна быть пустой");
                } else {
                    if (date == null) {
                        msg("Дата записи задачи не должна быть пустой");
                    } else {
                        db.iud("insert into tasks (task,day,register,category,status)"
                                + "value ('" + task + "','" + taskDayString + "','" + date + "','"
                                + categoryString + "','Не выполнено')");
                        msg("Успешная запись");
                        loadTableRows();
                    }
                }

            }
        }

    }

    @FXML
    void saveCategoryButtonPressed(ActionEvent event) {
        String category = JOptionPane.showInputDialog("Введите категрию");
        if (category == null) {
            msg("Выберите категорию");
        } else {
            if (category.equals("")) {
                msg("Не оставляйте пустой категорию");
            } else {

                if (db.hasRowInTableForThisCondition("categories", "where category='" + category + "'")) {
                    msg("Эта категория уже существует");
                } else {
                    db.iud("insert into categories (category) value ('" + category + "')");
                    msg("Категория успешно записана");
                    loadCategories();
                }

            }
        }
    }

    @FXML
    void searchTFKeyReleased(KeyEvent event) {

        String search = searchTF.getText().trim();
        db.setFilter("where id like '%" + search + "%' or task like '%" + search + "%' "
                + "or day like '%" + search + "%' or register like '%" + search + "%' "
                + "or category like '%" + search + "%' or status like '%" + search + "%' ");

        loadTableRows();
    }

    @FXML
    void solvedButtonPressed(ActionEvent event) {
        if (tasksTable.getSelectionModel().getSelectedItem() == null) {
            msg("Задача не выбрана");
        } else {
            String status = solvedButton.getText();
            if (status.equals("Не выполнено")) {
                db.iud("update tasks set status='Не выполнено' where id=" + selectedId + "");
                msg("Статус успешно изменен");
                loadTableRows();
            } else {
                db.iud("update tasks set status='Выполнено' where id=" + selectedId + "");
                msg("Статус успешно изменен");
                loadTableRows();
            }
        }

    }

    @FXML
    void solvedRadioSelected(ActionEvent event) {
        db.setFilter("where status='Выполнено' ");
        loadTableRows();
    }

    private int selectedId;

    @FXML
    void tasksTablePressed(MouseEvent event) {
        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {

        } else {
            selectedId = selectedTask.getId();
            taskTF.setText(selectedTask.getTask());
            dayTF.setText(String.valueOf(selectedTask.getDay()));
            dateDP.setValue(selectedTask.getRegister());
            categoryCB.getSelectionModel().select(selectedTask.getCategory());

            String taskStatus = selectedTask.getStatus();
            if (taskStatus.equals("Не выполнено")) {
                solvedButton.setText("Выполнено");
            } else {
                solvedButton.setText("Не выполнено");
            }

        }
    }

    @FXML
    void updateButtonPressed(ActionEvent event) {
        if (tasksTable.getSelectionModel().getSelectedItem() == null) {
            msg("Задача не выбрана");
        } else {

            String task = taskTF.getText().trim();
            String taskDayString = dayTF.getText();
            String categoryString = categoryCB.getSelectionModel().getSelectedItem();
            LocalDate date = dateDP.getValue();

            if (task.equals("")) {
                msg("Задача не должна быть пустой");
            } else {
                if (taskDayString.equals("")) {
                    msg("День Задачи должен быть назначен");
                } else {

                    if (categoryString == null) {
                        msg("Категория не должна быть пустой");
                    } else {
                        if (date == null) {
                            msg("Дата записи задачи не должна быть пустой");
                        } else {
                            db.iud("update tasks set task='" + task + "',day='" + taskDayString
                                    + "',register='" + date + "',category='" + categoryString
                                    + "' where id=" + selectedId + "");
                            msg("Успешное обновление ");
                            loadTableRows();
                        }
                    }

                }
            }

        }
    }

    private void msg(String message) {
        warningsLabel.setText(message);
    }

    private void loadCategories() {

        categoryCB.getItems().clear();
        categoryCB.getItems().addAll(db.getColumnRowsFromTableAsList("categories", "category", ""));
        categoryCB.getSelectionModel().select(0);

    }

    private void loadTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("register"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadTableRows() {
        tasksTable.setItems(db.getTasks());
        rowCountLabel.setText(String.valueOf(tasksTable.getItems().size()));
    }

    public static EventHandler<KeyEvent> allowOnlyDecimals(final Integer maxLength) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                TextField text = (TextField)e.getSource();
                if (text.getText().length() >= maxLength) {
                    e.consume();
                }
                if (!e.getCharacter().matches("[0-9.]")) {
                    if (text.getText().contains(".")&&e.getCharacter().matches("[.]")) {
                        e.consume();
                    } else 
                        if (text.getText().length() == 0 && e.getCharacter().matches("[.]")) {
                            e.consume();
                        } else {
                            e.consume();
                        }
                    
                }
            }
        };
    }

    
    
    public static boolean confirmDialog(String message){
        boolean result=false;
        JDialog.setDefaultLookAndFeelDecorated(true);
        int response=JOptionPane.showConfirmDialog(null, message,"",JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if(response==JOptionPane.NO_OPTION){
        result=false;
        }else if(response==JOptionPane.YES_OPTION){
            result=true;
        }else if(response==JOptionPane.CLOSED_OPTION){
            result=false;
        }
        return result;       
    }
    
    
}
