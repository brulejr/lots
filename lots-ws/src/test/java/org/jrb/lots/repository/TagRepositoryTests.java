package org.jrb.lots.repository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.jrb.lots.SpringApplication;
import org.jrb.lots.domain.Tag;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringApplication.class, loader = SpringApplicationContextLoader.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TagRepositoryTests {

	private final static Logger LOG = LoggerFactory.getLogger(TagRepositoryTests.class);

	@Autowired
	private TagRepository repository;

	private void createTag(final String name, final String description) throws IOException {
		final Tag tag = new Tag.Builder()
				.setName(name)
				.setDescription(description)
				.markAsCreated()
				.build();
		repository.save(tag);
	}

	@Test
	public void test01_createTags() {
		LOG.info("BEGIN: test01_createTags()");
		try {

			createTag("ABC", "Test1");
			createTag("DEF", "Test2");
			createTag("GHI", "Test3");
			createTag("JKL", "Test4");
			createTag("MNO", "Test5");

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test01_createTags()");
	}

	@Test
	public void test02_findsTags() {
		LOG.info("BEGIN: test02_findsTags()");
		try {

			final Tag domain1 = repository.findByName("ABC");
			assertNotNull(domain1);
			assertNotNull(domain1.getId());
			assertEquals("ABC", domain1.getName());
			assertEquals("Test1", domain1.getDescription());
			assertNotNull(domain1.getCreatedOn());
			assertNotNull(domain1.getUpdatedOn());

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test02_findsTags()");
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void test03_duplicateTag() {
		LOG.info("BEGIN: test02_findsTags()");
		try {

			createTag("ABC", "TestA");

		} catch (DataIntegrityViolationException e) {
			throw e;
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test03_duplicateTag()");
	}

	@Test
	public void test04_retrieveTags() {
		LOG.info("BEGIN: test04_retrieveTags()");
		try {

			final Page<Tag> allTags = repository.findAll(new PageRequest(0, 10));
			assertThat(allTags.getTotalElements(), is(5L));
			assertThat(allTags.getNumberOfElements(), is(5));

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test04_retrieveTags()");
	}
	
}
