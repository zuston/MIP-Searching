package io.github.zuston;

import io.github.zuston.Listener.DbInitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by zuston on 17-2-20.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        /**
         * 数据库连接池初始化
         */
        application.addListeners(new DbInitListener());
        application.run(args);
    }
}
