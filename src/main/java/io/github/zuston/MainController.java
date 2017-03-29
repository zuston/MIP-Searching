package io.github.zuston;

import io.github.zuston.Bean.ConditionsBean;
import io.github.zuston.Service.BaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zuston on 17-2-20.
 */
@RestController
public class MainController {

    @RequestMapping("/m/search")
    public String index(@RequestBody ConditionsBean conditionsBean){
        return BaseService.getInfo(conditionsBean);
    }


    @RequestMapping(value = "/m/s",method = RequestMethod.GET)
    public String s(@RequestParam("expression")String expression,@RequestParam("page")int page){
        System.out.println(expression);
        return BaseService.getInfo(expression,page);
    }

    public static void main(String[] args) {
        SpringApplication.run(MainController.class,args);
    }
}
