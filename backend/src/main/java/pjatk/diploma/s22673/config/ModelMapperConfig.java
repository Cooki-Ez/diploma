package pjatk.diploma.s22673.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pjatk.diploma.s22673.dto.EmployeeDTO;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.util.SetToEnumSetConverter;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(EmployeeDTO.class, Employee.class)
                .addMappings(mapper -> mapper
                        .using(new SetToEnumSetConverter())
                        .map(EmployeeDTO::getRoles, Employee::setRoles)
                );
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        return modelMapper;
    }
}