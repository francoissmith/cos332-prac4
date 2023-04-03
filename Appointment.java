/*
=================================================================
| PROJECT:              | COS 332 Practical 4                   |
|-----------------------|---------------------------------------|
| PROJECT PROGRAMMER:   | Francois Smith                        |
|-----------------------|---------------------------------------|
| STUDENT NUMBER:       | u19314486                             |
|-----------------------|---------------------------------------|
| DUE DATE:             | 03-04-2023                            |
=================================================================
*/
import java.sql.Date;

public class Appointment {
    private int id;
    private String name;
    private String date;
    private String time;

    public Appointment(int id, String name, String date, String time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return Date.valueOf(date);
    }

    public String getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Appointment [date=" + date + ", name=" + name + ", time=" + time + "]";
    }
}
