package com.redhat.activemq.ocp;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageController {

    @Value("${queue.name}")
    private String queueName;

    @Value("${topic.name}")
    private String topicName;

    @Autowired
    private QueueProducer queueProducer;

    @Autowired
    private TopicProducer topicProducer;

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {

    }

    @GetMapping("/")
    public String index() {

        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {

        model.addAttribute("sendForm", new SendForm());
        model.addAttribute("queue", queueName);
        model.addAttribute("topic", topicName);

        return "home";
    }

    @PostMapping(value = "sendToQueue", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String sendToQueue(SendForm sendForm, Model model) {

        queueProducer.sendMessage("Some useless message content.", sendForm.numberOfQueueMessages);

        model.addAttribute("numberOfMessages", sendForm.numberOfQueueMessages);
        model.addAttribute("destination", queueName);

        return "sent";
    }

    @PostMapping(value = "sendToTopic", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String sendToTopic(SendForm sendForm, Model model) {

        topicProducer.sendMessage("Some useless message content.", sendForm.numberOfTopicMessages);

        model.addAttribute("numberOfMessages", sendForm.numberOfTopicMessages);
        model.addAttribute("destination", topicName);

        return "sent";
    }


    @Data
    public static class SendForm {

        private int numberOfQueueMessages;

        private int numberOfTopicMessages;

    }

}
