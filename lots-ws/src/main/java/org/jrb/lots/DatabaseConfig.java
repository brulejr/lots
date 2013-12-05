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

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Database configuration for the List Of ThingS (LOTS) application.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
@Configuration
@ComponentScan({
	"org.jrb.lots.domain",
	"org.jrb.lots.repository"
})
public class DatabaseConfig {
	
	@Configuration
	@Profile(SpringApplication.ENV_LOCAL)
	static class Config_Local {
		
		@Bean
		public DataSource database() throws SQLException {
			final EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL)
				.setName("localdb")
				.addScript("sql/schema.sql")
				.build();
			return db;
		}
		
	}

}
