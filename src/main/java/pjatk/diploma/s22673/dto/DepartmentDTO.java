package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DepartmentDTO {
    private int id;

    @NotNull(message = "Location cannot be empty")
    @Size(min = 3, max = 50, message = "Location should be between 3 and 50 characters")
    private String location;

    @NotNull(message = "Name cannot be empty")
    @Size(min = 3, max = 50, message = "Name should be between 3 and 50 characters")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}