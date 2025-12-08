package pjatk.diploma.s22673.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pjatk.diploma.s22673.models.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
