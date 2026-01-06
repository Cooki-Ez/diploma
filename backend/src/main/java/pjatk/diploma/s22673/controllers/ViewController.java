package pjatk.diploma.s22673.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/create-leave-request")
    public String createLeaveRequestPage(Model model) {
        return "create-leave-request";
    }

    @GetMapping("/create-leave")
    public String createLeavePage() {
        return "create-leave";
    }

    @GetMapping("/leaves-view")
    public String leavesPage() {
        return "leaves";
    }

    @GetMapping("/evaluate-leave-request/{id}")
    public String evaluateLeaveRequestPage(@PathVariable Integer id, Model model) {
        model.addAttribute("leaveRequestId", id);
        return "evaluate-leave-request";
    }
}
