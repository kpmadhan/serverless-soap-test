package com.amazonaws.serverless.sample.springboot2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;


@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
	 final Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		LOGGER.info("Log:: Invoking messageDispatcherServlet");
		LOGGER.info("Log:: App Name" + applicationContext.getApplicationName());
		
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		servlet.setDispatchOptionsRequest(true);
		
		
		LOGGER.info("Log:: getContextConfigLocation" + servlet.toString());
		LOGGER.info("Log:: getContextAttribute     " + servlet.getContextId());
		
		ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/ws/*");
		LOGGER.info("Log:: getContextConfigLocation" +bean.getUrlMappings().toString());
		return bean;
	}

	@Bean(name = "kountries")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
		LOGGER.info("Log:: Invoking defaultWsdl11Definition");
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("CountriesPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://spring.io/guides/gs-producing-web-service");
		wsdl11Definition.setSchema(countriesSchema);
		return wsdl11Definition;
	}
	
	@Bean(name = "countries")
	public SimpleWsdl11Definition countries() {
		return new SimpleWsdl11Definition(new ClassPathResource("countries.wsdl"));
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("countries.xsd"));
	}
}
