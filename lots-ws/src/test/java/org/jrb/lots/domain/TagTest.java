package org.jrb.lots.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagTest {

	private final static Logger LOG = LoggerFactory.getLogger(TagTest.class);

	@Test
	public void test() {
		LOG.info("BEGIN: test()");
		try {

			final Tag tag1 = new Tag.Builder()
					.setName("ABC")
					.markAsCreated()
					.build();
			LOG.info("tag1 = {}", tag1);
			assertNotNull(tag1);
			assertEquals("ABC", tag1.getName());
			assertNull(tag1.getDescription());
			assertNotNull(tag1.getCreatedOn());
			assertNotNull(tag1.getUpdatedOn());

			final Tag tag2 = new Tag.Builder()
					.setName("DEF")
					.setDescription("Manifest #2")
					.markAsCreated()
					.build();
			LOG.info("tag2 = {}", tag2);
			assertNotNull(tag2);
			assertEquals("DEF", tag2.getName());
			assertEquals("Manifest #2", tag2.getDescription());
			assertNotNull(tag2.getCreatedOn());
			assertNotNull(tag2.getUpdatedOn());

			final Tag tag3 = new Tag.Builder()
					.from(tag2)
					.build();
			LOG.info("tag3 = {}", tag3);
			assertNotNull(tag3);
			assertEquals("DEF", tag3.getName());
			assertEquals("Manifest #2", tag3.getDescription());
			assertNotNull(tag3.getCreatedOn());
			assertNotNull(tag3.getUpdatedOn());

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test()");
	}

}
