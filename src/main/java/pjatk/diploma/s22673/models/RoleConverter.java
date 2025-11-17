package pjatk.diploma.s22673.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<EnumSet<EmployeeRole>,String> {
    @Override
    public String convertToDatabaseColumn(EnumSet<EmployeeRole> employeeRoleEnumSet) {
        if (employeeRoleEnumSet == null)
            return null;
        return employeeRoleEnumSet.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public EnumSet<EmployeeRole> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty())
            return null;
        String[] types = s.split(",");
        return EnumSet.copyOf(
                Arrays.stream(types)
                        .map(EmployeeRole::valueOf)
                        .collect(Collectors.toSet())
        );
    }
}
