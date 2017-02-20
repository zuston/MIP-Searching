package io.github.zuston;

import io.github.zuston.Bean.TestBean;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zuston on 17-2-20.
 */
@RestController
public class MainController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/index")
    public TestBean index(@RequestParam(value="name",defaultValue = "shacha")String name){
        return new TestBean(String.format(template,name), (int) counter.incrementAndGet());
    }

    public static void main(String[] args) {
        SpringApplication.run(MainController.class,args);
    }
}
