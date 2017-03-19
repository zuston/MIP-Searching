package io.github.zuston;

import io.github.zuston.Bean.ConditionsBean;
import io.github.zuston.Service.BaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zuston on 17-2-20.
 */
@RestController
public class MainController {

    @RequestMapping("/m/search")
    public String index(@RequestBody ConditionsBean conditionsBean){
//        return "{\"c\":[{\"name\":\"zuston\",\"age\":20},{\"name\":\"shacha\",\"age\":32}]}";
        return BaseService.getInfo(conditionsBean);
//        return new TestBean(conditionsBean.condition.get(0).name,1);

    }



    public static void main(String[] args) {
        SpringApplication.run(MainController.class,args);
    }
}
