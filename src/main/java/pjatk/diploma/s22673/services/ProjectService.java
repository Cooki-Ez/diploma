package pjatk.diploma.s22673.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.diploma.s22673.exceptions.ProjectDoesNotExistException;
import pjatk.diploma.s22673.models.Project;
import pjatk.diploma.s22673.repositories.ProjectRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project findById(int id) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null)
            throw new ProjectDoesNotExistException("Project with id " + id + " does not exist");
        return project;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public Project save(Project project, int id) {
        project.setId(id);
        return projectRepository.save(project);
    }

    @Transactional
    public void delete(int id) {
        projectRepository.deleteById(id);
    }
}
