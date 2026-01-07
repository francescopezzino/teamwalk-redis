package com.fp.teamwalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@SpringBootApplication(exclude = {DataRedisRepositoriesAutoConfiguration.class})
@SpringBootApplication
public class TeamwalkRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamwalkRedisApplication.class, args);
	}

}
