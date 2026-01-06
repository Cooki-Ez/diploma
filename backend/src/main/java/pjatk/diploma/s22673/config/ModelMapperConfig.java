package pjatk.diploma.s22673.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pjatk.diploma.s22673.dto.EmployeeDTO;
import pjatk.diploma.s22673.dto.LeaveRequestDTO;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.models.LeaveRequest;
import pjatk.diploma.s22673.util.SetToEnumSetConverter;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Register your custom converter for roles mapping
        modelMapper.typeMap(EmployeeDTO.class, Employee.class)
                .addMappings(mapper -> mapper
                        .using(new SetToEnumSetConverter())
                        .map(EmployeeDTO::getRoles, Employee::setRoles)
                );
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.typeMap(LeaveRequest.class, LeaveRequestDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getLeaveEvaluation() != null ? src.getLeaveEvaluation().getId() : null, LeaveRequestDTO::setLeaveEvaluationId);
        });

        return modelMapper;
    }
}