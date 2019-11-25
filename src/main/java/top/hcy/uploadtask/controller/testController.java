package top.hcy.uploadtask.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @RequestMapping("/test")
    public Object test(int val1,long val2,double val3){

        System.out.println("val1 === "+ val1);
        System.out.println("val2 === "+ val2);
        System.out.println("val3 === "+ val3);

        return null;
    }
}
