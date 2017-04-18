package io.github.zuston;

import io.github.zuston.Bean.ConditionsBean;
import io.github.zuston.Service.BaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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
    public String s(@RequestParam("expression")String expression,@RequestParam("page")int page,@RequestParam("computed")int flag){
        System.out.println(expression);
        return BaseService.getInfo(expression,page,flag);
    }

    @RequestMapping(value = "/m/p",method = RequestMethod.GET)
    public String bili(@RequestParam("bili")String bili,@RequestParam("biliNumber")String biliNumber,@RequestParam("page")int page,@RequestParam("computed")int flag){
        return BaseService.getBiliInfo(bili,biliNumber,page,flag);
    }

    @RequestMapping(value = "/m/download", method = RequestMethod.GET)
    public String download(@RequestParam("bili")String bili,@RequestParam("biliNumber")String biliNumber) throws IOException, NoSuchAlgorithmException {
        return BaseService.downloadBiliInfo(bili,biliNumber);
    }

    @RequestMapping(value = "/m/info",method = RequestMethod.GET)
    public String info(@RequestParam("id")String id){
        return BaseService.getComplexInfo(id);
    }

    @RequestMapping(value = "/m/jsmol",produces = MediaType.TEXT_PLAIN_VALUE,method = RequestMethod.GET)
    public String jsmol(@RequestParam("id")String idd) throws IOException {
        return BaseService.getJSmolInfo(idd);
    }


    public static void main(String[] args) {
        SpringApplication.run(MainController.class,args);
    }
}
