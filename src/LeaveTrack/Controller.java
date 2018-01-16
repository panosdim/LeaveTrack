package LeaveTrack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.prefs.Preferences;

import static org.controlsfx.control.decoration.Decorator.addDecoration;
import static org.controlsfx.control.decoration.Decorator.removeAllDecorations;

public class Controller {
    public ComboBox<Year> cmbYear;
    public ComboBox<Integer> cmbLeaveTotal;
    public DatePicker dtpFrom;
    public DatePicker dtpUntil;
    public Button btnAdd;
    public TableView<AnnualLeave> tblLeaves;
    public TableColumn<AnnualLeave, Integer> idColumn;
    public TableColumn<AnnualLeave, LocalDate> fromColumn;
    public TableColumn<AnnualLeave, LocalDate> untilColumn;
    public TableColumn<AnnualLeave, Integer> daysColumn;
    public TextField txtRemain;
    private final DBHandler db = new DBHandler();
    private final ObservableList<AnnualLeave> annualLeaves = FXCollections.observableArrayList();
    private AnnualLeave selAnnualLeave;
    public Button btnSave;
    public Button btnClear;
    public Button btnEdit;
    public Button btnDelete;
    private ValidationSupport leaveValidation = new ValidationSupport();
    private Preferences prefs = Preferences.userNodeForPackage(LeaveTrack.Main.class);

    private static final Image ERROR_IMAGE = new Image(GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/decoration-error.png").toExternalForm());

    public void initialize() {
        // Initialize annual leave days combo box
        String PREF_NAME = "annual_leave_days";
        Integer annualLeaveDays = prefs.getInt(PREF_NAME, 20);
        cmbLeaveTotal.getItems().add(20);
        cmbLeaveTotal.getItems().add(21);
        cmbLeaveTotal.getItems().add(22);
        cmbLeaveTotal.getItems().add(25);
        cmbLeaveTotal.getItems().add(26);
        cmbLeaveTotal.setValue(annualLeaveDays);
        cmbLeaveTotal.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                prefs.putInt(PREF_NAME, Integer.parseInt(newValue));
                removeAllDecorations(cmbLeaveTotal.getEditor());
            } catch (NumberFormatException ex) {
                addDecoration(cmbLeaveTotal.getEditor(), new GraphicDecoration(new ImageView(ERROR_IMAGE), Pos.BOTTOM_LEFT));
            }
        });
        cmbLeaveTotal.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                return object == null ? null : object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        });

        // Initialize Year combo box
        cmbYear.getItems().addAll(db.getYears());
        cmbYear.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            db.getAnnualLeaves(annualLeaves, newValue);
            tblLeaves.setItems(annualLeaves);
        }));
        cmbYear.getSelectionModel().selectLast();

        // Initialize annual leaves table
        tblLeaves.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                btnEdit.setDisable(true);
                btnDelete.setDisable(true);
            }
        }));
        // Bind Table columns to specific properties of Receipt Class
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        daysColumn.setCellValueFactory(cellData -> cellData.getValue().daysProperty().asObject());
        fromColumn.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        untilColumn.setCellValueFactory(cellData -> cellData.getValue().untilProperty());

        // Validation checks for adding new leave
        leaveValidation.registerValidator(dtpFrom, Validator.createEmptyValidator("From Date required"));
        leaveValidation.registerValidator(dtpUntil, Validator.createEmptyValidator("Until Date required"));

        // Initialize date pickers
        final Callback<DatePicker, DateCell> untilCellFactory = new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (dtpFrom.getValue() != null && item.isBefore(dtpFrom.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #EEEEEE;");
                        }
                        if (dtpFrom.getValue() != null && item.getYear() != dtpFrom.getValue().getYear()) {
                            setDisable(true);
                            setStyle("-fx-background-color: #EEEEEE;");
                        }
                    }
                };
            }
        };
        dtpUntil.setDayCellFactory(untilCellFactory);

        final Callback<DatePicker, DateCell> fromCellFactory = new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (dtpUntil.getValue() != null && item.isAfter(dtpUntil.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #EEEEEE;");
                        }
                        if (dtpUntil.getValue() != null && item.getYear() != dtpUntil.getValue().getYear()) {
                            setDisable(true);
                            setStyle("-fx-background-color: #EEEEEE;");
                        }
                    }
                };
            }
        };
        dtpFrom.setDayCellFactory(fromCellFactory);

        String pattern = "dd-MM-yyyy";
        dtpFrom.setPromptText(pattern);
        dtpFrom.setConverter(new StringConverter<>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate object) {
                return object == null ? null : dateFormatter.format(object);
            }

            @Override
            public LocalDate fromString(String string) {
                return LocalDate.parse(string, dateFormatter);
            }
        });

        dtpUntil.setPromptText(pattern);
        dtpUntil.setConverter(new StringConverter<>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate object) {
                return object == null ? null : dateFormatter.format(object);
            }

            @Override
            public LocalDate fromString(String string) {
                return LocalDate.parse(string, dateFormatter);
            }
        });

        // Initialize toolbar elements
        btnSave.managedProperty().bind(btnSave.visibleProperty());
        btnAdd.managedProperty().bind(btnAdd.visibleProperty());
        btnClear.managedProperty().bind(btnClear.visibleProperty());
        btnSave.setVisible(false);
        btnClear.setVisible(false);
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
    }

    public void add_leave() {
        if (leaveValidation.isInvalid()) {
            leaveValidation.setErrorDecorationEnabled(true);
            leaveValidation.initInitialDecoration();
            leaveValidation.redecorate();
            return;
        }

        AnnualLeave data = new AnnualLeave(
                dtpFrom.getValue(),
                dtpUntil.getValue(),
                GreekBankHolidays.calculateWorkingDays(dtpFrom.getValue(), dtpUntil.getValue()),
                -1);

        if (db.newAnnualLeave(data)) {
            System.out.println("Annual Leave added successfully.");
            dtpFrom.setValue(null);
            dtpUntil.setValue(null);
            db.getAnnualLeaves(annualLeaves, cmbYear.getValue());
            tblLeaves.setItems(annualLeaves);
            leaveValidation.setErrorDecorationEnabled(false);
        } else {
            System.out.println("Failed to add new Annual Leave.");
        }
    }

    public void update_leave() {
        if (leaveValidation.isInvalid()) {
            leaveValidation.setErrorDecorationEnabled(true);
            leaveValidation.initInitialDecoration();
            leaveValidation.redecorate();
            return;
        }

        selAnnualLeave.setFrom(dtpFrom.getValue());
        selAnnualLeave.setUntil(dtpUntil.getValue());
        selAnnualLeave.setDays(GreekBankHolidays.calculateWorkingDays(dtpFrom.getValue(), dtpUntil.getValue()));

        if (db.setAnnualLeave(selAnnualLeave)) {
            System.out.println("Annual Leave updated successfully.");
            dtpFrom.setValue(null);
            dtpUntil.setValue(null);
            db.getAnnualLeaves(annualLeaves, cmbYear.getValue());
            tblLeaves.setItems(annualLeaves);
            cancel_edit();
        } else {
            System.out.println("Failed to update Annual Leave.");
        }
    }

    public void cancel_edit() {
        btnAdd.setVisible(true);
        btnSave.setVisible(false);
        btnClear.setVisible(false);
        dtpFrom.setValue(null);
        dtpUntil.setValue(null);
        leaveValidation.setErrorDecorationEnabled(false);
    }

    public void edit_leave() {
        btnAdd.setVisible(false);
        btnSave.setVisible(true);
        btnClear.setVisible(true);
        selAnnualLeave = tblLeaves.getSelectionModel().getSelectedItem();
        dtpFrom.setValue(selAnnualLeave.getFrom());
        dtpUntil.setValue(selAnnualLeave.getUntil());
    }

    public void delete_leave() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setContentText("Do you really want to delete the selected leave?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (db.deleteAnnualLeave(tblLeaves.getSelectionModel().getSelectedItem().getId())) {
                System.out.println("Annual Leave Deleted successfully");
            }
            db.getAnnualLeaves(annualLeaves, cmbYear.getValue());
            tblLeaves.setItems(annualLeaves);
        }
    }
}
