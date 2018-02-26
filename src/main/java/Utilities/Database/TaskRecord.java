package Utilities.Database;

/**
 * Created by ilkercan on 05/12/2017.
 */

public class TaskRecord {

    int id;
    String name;
    int termid;
    String deadline;
    int point;
    int totalpoints;

    int worklogid;
    int worklogpoints;

    public int getWorklogid() {
        return worklogid;
    }

    public void setWorklogid(int worklogid) {
        this.worklogid = worklogid;
    }

    public int getWorklogpoints() {
        return worklogpoints;
    }

    public void setWorklogpoints(int worklogpoints) {
        this.worklogpoints = worklogpoints;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTermid() {
        return termid;
    }

    public void setTermid(int termid) {
        this.termid = termid;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getTotalpoints() {
        return totalpoints;
    }

    public void setTotalpoints(int totalpoints) {
        this.totalpoints = totalpoints;
    }
}
