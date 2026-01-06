package pjatk.diploma.s22673.util;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import pjatk.diploma.s22673.models.EmployeeRole;

import java.util.EnumSet;
import java.util.Set;

public class SetToEnumSetConverter implements Converter<Set<EmployeeRole>, EnumSet<EmployeeRole>> {
    @Override
    public EnumSet<EmployeeRole> convert(MappingContext<Set<EmployeeRole>, EnumSet<EmployeeRole>> context) {
        Set<EmployeeRole> source = context.getSource();
        if (source == null || source.isEmpty()) {
            return EnumSet.noneOf(EmployeeRole.class);
        }
        return EnumSet.copyOf(source);
    }
}
