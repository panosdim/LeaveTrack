package LeaveTrack;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnnualLeave {
    private final ObjectProperty<LocalDate> from;
    private final ObjectProperty<LocalDate> until;
    private final IntegerProperty id;
    private final IntegerProperty days;

    AnnualLeave(
            LocalDate from,
            LocalDate until,
            int days,
            int id) {
        this.from = new SimpleObjectProperty<>(from);
        this.until = new SimpleObjectProperty<>(until);
        this.id = new SimpleIntegerProperty(id);
        this.days = new SimpleIntegerProperty(days);
    }

    public LocalDate getFrom() {
        return from.get();
    }

    public ObjectProperty<LocalDate> fromProperty() {
        return from;
    }

    public final String getFromSQL() {
        return from.get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public LocalDate getUntil() {
        return until.get();
    }

    public ObjectProperty<LocalDate> untilProperty() {
        return until;
    }

    public final String getUntilSQL() {
        return until.get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getDays() {
        return days.get();
    }

    public IntegerProperty daysProperty() {
        return days;
    }

    public void setFrom(LocalDate from) {
        this.from.set(from);
    }

    public void setUntil(LocalDate until) {
        this.until.set(until);
    }

    public void setDays(int days) {
        this.days.set(days);
    }
}
