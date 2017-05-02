package io.github.zuston;

import io.github.zuston.Bean.ConditionsBean;
import io.github.zuston.Service.BaseService;
import io.github.zuston.Service.BaseServiceV2;
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

    @RequestMapping(value = "/m/s",method = RequestMethod.GET)
    public String s(@RequestParam("expression")String expression,@RequestParam("page")int page,@RequestParam("computed")int flag){
        return BaseServiceV2.basicInfoFunction(expression,page,flag);
    }

    @RequestMapping(value = "/m/download", method = RequestMethod.GET)
    public String download(@RequestParam("expression")String expression) throws IOException, NoSuchAlgorithmException {
        return BaseServiceV2.basicDownloadFunction(expression);
    }

    @RequestMapping(value = "/m/jsmol",produces = MediaType.TEXT_PLAIN_VALUE,method = RequestMethod.GET)
    public String jsmol(@RequestParam("id")String idd) throws IOException {
        return BaseServiceV2.basicJsmolFunction(idd);
    }


    /**
     * V1 版本废弃
     * @param bili
     * @param biliNumber
     * @param page
     * @param flag
     * @return
     */
    @RequestMapping(value = "/m/p",method = RequestMethod.GET)
    public String bili(@RequestParam("bili")String bili,@RequestParam("biliNumber")String biliNumber,@RequestParam("page")int page,@RequestParam("computed")int flag){
        return BaseService.getBiliInfo(bili,biliNumber,page,flag);
    }

    /**
     * V1 版本废弃
     * @param id
     * @return
     */
    @RequestMapping(value = "/m/info",method = RequestMethod.GET)
    public String info(@RequestParam("id")String id){
        return BaseServiceV2.basicDetailInfoFunction(id);
    }



    public static void main(String[] args) {
        SpringApplication.run(MainController.class,args);
    }
}
