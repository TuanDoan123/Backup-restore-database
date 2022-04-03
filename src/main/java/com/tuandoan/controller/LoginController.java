package com.tuandoan.controller;

import com.tuandoan.jdbc.Connect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private Connect connect;

    @Autowired
    public LoginController(Connect connect){
        this.connect = connect;
    }

    @GetMapping("/login")
    public String showLoginForm(){
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String userName,
                        @RequestParam String password,
                        Model model){
        connect.setUserName(userName);
        connect.setPassword(password);
        if(connect.getConnection() == null) {
            connect.setUserName(null);
            connect.setPassword(null);
            model.addAttribute("err", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "login";
        }
        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(){
        connect.setUserName(null);
        connect.setPassword(null);
        return "redirect:/login";
    }
}
