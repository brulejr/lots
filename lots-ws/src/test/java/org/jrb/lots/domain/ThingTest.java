package org.jrb.lots.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThingTest {

	private final static Logger LOG = LoggerFactory.getLogger(ThingTest.class);

	@Test
	public void test() {
		LOG.info("BEGIN: test()");
		try {

			final Tag thing1 = new Tag.Builder()
					.setName("ABC")
					.markAsCreated()
					.build();
			LOG.info("thing1 = {}", thing1);
			assertNotNull(thing1);
			assertEquals("ABC", thing1.getName());
			assertNull(thing1.getDescription());
			assertNotNull(thing1.getCreatedOn());
			assertNotNull(thing1.getUpdatedOn());

			final Tag thing2 = new Tag.Builder()
					.setName("DEF")
					.setDescription("Manifest #2")
					.markAsCreated()
					.build();
			LOG.info("thing2 = {}", thing2);
			assertNotNull(thing2);
			assertEquals("DEF", thing2.getName());
			assertEquals("Manifest #2", thing2.getDescription());
			assertNotNull(thing2.getCreatedOn());
			assertNotNull(thing2.getUpdatedOn());

			final Tag thing3 = new Tag.Builder()
					.from(thing2)
					.build();
			LOG.info("thing3 = {}", thing3);
			assertNotNull(thing3);
			assertEquals("DEF", thing3.getName());
			assertEquals("Manifest #2", thing3.getDescription());
			assertNotNull(thing3.getCreatedOn());
			assertNotNull(thing3.getUpdatedOn());

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test()");
	}

}
