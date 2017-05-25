package cn.standardai.api.statistic;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cn.standardai.api.core.bean.PropertyConfig;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties({PropertyConfig.class})
public class Application {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
		//SpringApplication.run(Application.class);
		SpringApplication app = new SpringApplication(Application.class);
		app.addListeners(new ApplicationStartup());
        app.run(args);
	}
}
