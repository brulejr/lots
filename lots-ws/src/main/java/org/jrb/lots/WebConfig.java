/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Jon Brule
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.jrb.lots;

import java.util.List;

import org.jrb.commons.web.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Web configuration for the List Of ThingS (LOTS) application.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
@Configuration
public class WebConfig {

	/**
	 * Defines callback methods to customize the web application.
	 * 
	 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
	 */
	static class WebMvcConfigurer extends WebMvcConfigurerAdapter {

		@Override
		public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
			converters.add(mappingJackson2HttpMessageConverter());
		}

		private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

			// assemble json mapper
			final ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JodaModule());
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

			// assemble json message converter
			final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setObjectMapper(objectMapper);

			return converter;
		}
		
	}

	@Autowired
	private Environment env;

	@Bean
	public ResponseUtils responseUtils() {
		return new ResponseUtils();
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		final int port = env.getRequiredProperty("http.server.port", Integer.class);
		final JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
		factory.setPort(port);
		return factory;
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurerAdapter() {
		return new WebMvcConfigurer();
	}

}
