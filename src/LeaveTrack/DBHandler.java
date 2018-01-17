package LeaveTrack;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.exit;

class DBHandler {
    private Connection con;

    DBHandler() {
        try {
            String dbURL = "jdbc:sqlite:LeaveTrack.db3";
            con = DriverManager.getConnection(dbURL);
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(ex.getMessage());
            alert.setContentText(ex.toString());

            alert.showAndWait();
            exit();
        }
    }

    //================================================================================
    // Handle Annual Leaves
    //================================================================================

    boolean newAnnualLeave(AnnualLeave data) {
        String sql = "INSERT INTO annual VALUES(null,'"
                + data.getFromSQL() + "','"
                + data.getUntilSQL() + "','"
                + data.getDays() + "')";
        try {
            Statement sta = con.createStatement();
            sta.execute(sql);

            sta.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    void getAnnualLeaves(ObservableList<AnnualLeave> list, Year year) {
        try {
            list.clear();
            Statement sta = con.createStatement();
            ResultSet res = sta.executeQuery("SELECT * FROM annual WHERE leave_from >= date('" +
                    LocalDate.of(year.getValue(), 1, 1).format(DateTimeFormatter.ISO_DATE) +
                    "') AND leave_until <= date('" +
                    LocalDate.of(year.getValue(), 12, 31).format(DateTimeFormatter.ISO_DATE) +
                    "')");

            while (res.next()) {
                list.add(new AnnualLeave(
                        LocalDate.parse(res.getString("leave_from")),
                        LocalDate.parse(res.getString("leave_until")),
                        res.getInt("days"),
                        res.getInt("id")));
            }
            res.close();
            sta.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    boolean setAnnualLeave(AnnualLeave data) {
        try {
            Statement sta = con.createStatement();
            sta.execute("UPDATE annual SET leave_from = '" + data.getFromSQL() +
                    "', leave_until = '" + data.getUntilSQL() +
                    "', days = '" + data.getDays() +
                    "' WHERE id = '" + data.getId() + "'");

            sta.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    boolean deleteAnnualLeave(int id) {
        try {
            Statement sta = con.createStatement();
            sta.execute("DELETE FROM annual WHERE id = '"
                    + id + "'");

            sta.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    List<Year> getYears() {
        List<Year> years = new ArrayList<>();
        try {
            Statement sta = con.createStatement();
            ResultSet res = sta.executeQuery("SELECT strftime('%Y', leave_from) AS years FROM annual GROUP BY years");

            while (res.next()) {
                years.add(Year.parse(res.getString("years")));
            }
            res.close();
            sta.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return years;
    }
}
