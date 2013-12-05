package org.jrb.lots.repository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.jrb.lots.SpringApplication;
import org.jrb.lots.domain.Thing;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringApplication.class, loader = SpringApplicationContextLoader.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(SpringApplication.ENV_LOCAL)
public class ThingRepositoryTests {

	private final static Logger LOG = LoggerFactory.getLogger(ThingRepositoryTests.class);

	@Autowired
	private ThingRepository repository;

	private void createThing(final String name, final String description) throws IOException {
		final Thing thing = new Thing.Builder()
				.setName(name)
				.setDescription(description)
				.markAsCreated()
				.build();
		repository.save(thing);
	}

	@Test
	public void test01_createThings() {
		LOG.info("BEGIN: test01_createThings()");
		try {

			createThing("ABC", "Test1");
			createThing("DEF", "Test2");
			createThing("GHI", "Test3");
			createThing("JKL", "Test4");
			createThing("MNO", "Test5");

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test01_createThings()");
	}

	@Test
	public void test02_findsThings() {
		LOG.info("BEGIN: test02_findsThings()");
		try {

			final Thing domain1 = repository.findByName("ABC");
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
		LOG.info("END: test02_findsThings()");
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void test03_duplicateThing() {
		LOG.info("BEGIN: test02_findsThings()");
		try {

			createThing("ABC", "TestA");

		} catch (DataIntegrityViolationException e) {
			throw e;
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test03_duplicateThing()");
	}

	@Test
	public void test04_retrieveThings() {
		LOG.info("BEGIN: test04_retrieveThings()");
		try {

			final Page<Thing> allThings = repository.findAll(new PageRequest(0, 10));
			assertThat(allThings.getTotalElements(), is(5L));
			assertThat(allThings.getNumberOfElements(), is(5));

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test04_retrieveThings()");
	}
	
}
