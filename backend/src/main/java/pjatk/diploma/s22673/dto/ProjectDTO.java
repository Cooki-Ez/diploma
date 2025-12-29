package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pjatk.diploma.s22673.models.Importance;

import java.sql.Timestamp;
import java.util.List;

public class ProjectDTO {
    private int id;

    @NotNull(message = "Name cannot be empty")
    @Size(min = 1, max = 50)
    private String name;

    private String description;

    @NotNull(message = "Start date cannot be empty")
    private Timestamp startDate;

    private Timestamp endDate;

    private Importance importance;
    private List<Integer> employeeIds;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    public List<Integer> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Integer> employeeIds) {
        this.employeeIds = employeeIds;
    }
}