package signature.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
    @GetMapping("/")
    public String start() {
        return "index";
    }

    @PostMapping("/")
    public String submitForm() {
        System.out.println("hello");
        return "register_success";
    }

}
