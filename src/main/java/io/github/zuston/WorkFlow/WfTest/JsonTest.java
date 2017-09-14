package io.github.zuston.WorkFlow.WfTest;

import io.github.zuston.WorkFlow.WorkFlowEngine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zuston on 17-9-13.
 */
public class JsonTest {
    public static void main(String[] args) throws Exception {
        String path = "/home/zuston/dev/MIP-Searching/src/main/java/workflow/workflow.json";
        String json = readString(path);
        WorkFlowEngine workFlowEngine = new WorkFlowEngine();
        workFlowEngine.run(json);
    }

    private static String readString(String FILE_IN) throws IOException {
        StringBuffer str=new StringBuffer("");
        File file=new File(FILE_IN);
        try {
            FileReader fr=new FileReader(file);
            int ch = 0;
            while((ch = fr.read())!=-1 )
            {
                str.append((char)ch);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File reader出错");
        }
        return str.toString();
    }
}
