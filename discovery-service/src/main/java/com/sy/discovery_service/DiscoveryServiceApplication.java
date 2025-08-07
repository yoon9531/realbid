package com.sy.discovery_service;

import com.netflix.appinfo.AmazonInfo;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServiceApplication.class, args);
	}

	/*
	* To make eureka server application configured to be AWS-aware,
	* customize the EurekaInstanceConfigBean
	* */

//	@Bean
//	@Profile("!default")
//	public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {
//		EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
//		AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
//		b.setDataCenterInfo(info);
//		return b;
//	}
}
