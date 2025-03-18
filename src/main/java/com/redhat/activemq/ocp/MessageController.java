package com.redhat.activemq.ocp;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MessageController {

    @Autowired
    private Producer producer;

    @GetMapping("/")
    public String index() {

        return "redirect:/send";
    }

    @GetMapping("/send")
    public String send(Model model) {

        model.addAttribute("sendForm", new SendForm());

        return "home";
    }

    @PostMapping(value = "send", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String add(SendForm sendForm, Model model) {

        producer.sendMessage("Some useless message content.", sendForm.numberOfMessages);

        model.addAttribute("numberOfMessages", sendForm.numberOfMessages);

        return "sent";
    }

    @Data
    public static class SendForm {

        private int numberOfMessages;

    }

}
