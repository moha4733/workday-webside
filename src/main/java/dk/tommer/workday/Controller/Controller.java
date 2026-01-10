package dk.tommer.workday.Controller;

import dk.tommer.workday.Entity.Role;
import dk.tommer.workday.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private UserService userService;

    @GetMapping("/create-user")
    @ResponseBody
    public String createUserTest(){
        userService.createUser("mo", "momo004@gmail.com", "123" , Role.ADMIN);
        return "bruger oprettet";
    }


    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/")
    public String home(){
        return "welcome";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(){
        return "admin.dashboard";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(){
        return "user-dashboard";
    }
}
