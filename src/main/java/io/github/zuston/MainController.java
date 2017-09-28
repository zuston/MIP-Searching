package io.github.zuston;

import io.github.zuston.Service.BaseServiceV2;
import io.github.zuston.Helper.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zuston on 17-2-20.
 */
@RestController
public class MainController {

    @Autowired
    private BaseServiceV2 baseServiceV2;

    @RequestMapping(value = "/m/s",method = RequestMethod.GET)
    public String s(@RequestParam("expression")String expression,@RequestParam("page")int page,@RequestParam("computed")int flag){
        expression = expression.replace("#","|");
        return baseServiceV2.basicSearch(expression,page,flag);
    }

    @RequestMapping(value = "/m/calculate",method = RequestMethod.GET)
    public String calculate(@RequestParam("expression")String expression,@RequestParam("computed")int flag){
        expression = expression.replace("#","|");
        return baseServiceV2.basicGetAllCalculate(expression,flag);
    }

    @RequestMapping(value = "/m/randomcalculate",method = RequestMethod.GET)
    public String rcalculate(@RequestParam("expression")String expression,@RequestParam("computed")int flag){
        expression = expression.replace("#","|");
        return baseServiceV2.basicGetRandomCalculate(expression,flag);
    }

    @RequestMapping(value = "/m/download", method = RequestMethod.GET)
    public void download(HttpServletResponse res,@RequestParam("expression")String expression,@RequestParam("computed")int flag) throws IOException, NoSuchAlgorithmException {
        baseServiceV2.basicExcelDownloadFunction(res,expression,flag);
    }

    @RequestMapping(value = "/m/jsmol",produces = MediaType.TEXT_PLAIN_VALUE,method = RequestMethod.GET)
    public String jsmol(@RequestParam("id")String idd) throws IOException {
        return baseServiceV2.basicJsmolFunctionFromMongoDb(idd);
    }

    @RequestMapping(value = "/m/filedownload", method = RequestMethod.GET)
    public void download(HttpServletResponse res,@RequestParam("mid")String mid,@RequestParam("filename")String filename){
        baseServiceV2.basicFileDownloadFunction(res,mid,filename);
    }

    @RequestMapping(value = "/m/userInfo",method = RequestMethod.GET)
    public String userInfo(@RequestParam("token")String token){
        return RedisHelper.getString(token+"Info");
    }

    @RequestMapping(value = "/m/info",method = RequestMethod.GET)
    public String info(@RequestParam("id")String id){
        return baseServiceV2.basicDetailInfoFunction(id);
    }

    @RequestMapping(value = "/m/imgload",method = RequestMethod.GET)
    public void imgload(HttpServletResponse response,@RequestParam("jobid")String jobid,@RequestParam("type")int type) throws IOException {
        baseServiceV2.basicImgLoad(response,jobid,type);
    }

    @RequestMapping(value = "/m/poscarDownload", method = RequestMethod.GET)
    public void poscarDownload(HttpServletResponse res,@RequestParam("expression")String expression,@RequestParam("computed")int flag) throws IOException, NoSuchAlgorithmException {
        baseServiceV2.basicPoscarDownloadFunction(res,expression,flag);
    }

    @RequestMapping(value = "/m/choosedPoscarDownload", method = RequestMethod.GET)
    public void choosedPoscarDownload(HttpServletResponse res,@RequestParam("mids")String mids,@RequestParam("computed")int flag) throws IOException {
        baseServiceV2.choosedPoscarDownloadFunction(res,mids,flag);
    }

    @RequestMapping(value = "/m/choosedExcelDownload", method = RequestMethod.GET)
    public void choosedExcelDownload(HttpServletResponse res,@RequestParam("mids")String mids,@RequestParam("computed")int flag) throws IOException, NoSuchAlgorithmException {
        baseServiceV2.choosedExcelDownloadFunction(res,mids,flag);
    }

    @RequestMapping(value = "/m/poscarAndExcelDownload", method = RequestMethod.GET)
    public void poscarAndExcelDownload(HttpServletResponse res,@RequestParam("expression")String expression,@RequestParam("computed")int flag) throws IOException, NoSuchAlgorithmException {
        baseServiceV2.basicPoscarAndExcelDownload(res,expression,flag);
    }

    public static void main(String[] args) {
        SpringApplication.run(MainController.class,args);
    }
}
