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

    @GetMapping("/evaluate-leave-request")
    public String evaluateLeaveRequestPage(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            model.addAttribute("leaveRequestId", id);
        }
        return "evaluate-leave-request";
    }
}
