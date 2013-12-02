package org.jrb.lots.service.thing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.jrb.lots.Application;
import org.jrb.lots.domain.Thing;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

/**
 * Unit test cases for {@link ThingServiceImpl}.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("LOCAL")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThingServiceTest {

	private final static Logger LOG = LoggerFactory.getLogger(ThingServiceTest.class);

	@Autowired
	private ThingService thingService;

	private Thing createThing(final String name, final String description, final String... tagnames) throws ThingServiceException {
		final Thing thing = new Thing.Builder()
				.setName(name)
				.setDescription(description)
				.setTagnames(tagnames != null ? Sets.newHashSet(tagnames) : null)
				.build();
		final Thing createdThing = thingService.createThing(thing);
		assertNotNull(createdThing);
		assertNotNull(createdThing.getId());
		assertEquals(name, createdThing.getName());
		assertEquals(description, createdThing.getDescription());
		assertNotNull(createdThing.getCreatedOn());
		assertNotNull(createdThing.getUpdatedOn());
		return thing;
	}

	@Test
	public void test01_CreateThings() {
		LOG.info("BEGIN: test01_CreateThings()");
		try {

			// create first thing
			createThing("THING_1", "This is thing #1");

			// create second thing
			createThing("THING_2", "This is thing #2");

			// create second thing
			createThing("THING_3", "This is thing #3");

			// create second thing
			createThing("THING_4", "This is thing #4");

			// attempt to recreate first thing
			try {
				createThing("THING_1", "This is thing #1");
				fail("Unsuccessfully created a duplicate tag!");
			} catch (final DuplicateThingException e) {
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test01_CreateThings()");
	}

	@Test
	public void test02_CreateThingsWithTags() {
		LOG.info("BEGIN: test02_CreateThingsWithTags()");
		try {

			// create first thing
			LOG.info("Creating thing 1 with tags...");
			final Thing thing1 = createThing("THING_T1", "This is thing #1T", "A", "B", "C");
			LOG.info("thing1 = {}", thing1);

			// create second thing
			LOG.info("Creating thing 2 with tags...");
			final Thing thing2 = createThing("THING_T2", "This is thing #2T", "A", "C", "E");
			LOG.info("thing2 = {}", thing2);

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test02_CreateThingsWithTags()");
	}

	@Test
	public void test03_FindThings() {
		LOG.info("BEGIN: test03_FindThings()");
		try {

			// find first thing
			final Thing thing1 = thingService.findThing("THING_1");
			LOG.info("thing1 = {}", thing1);
			assertNotNull(thing1);
			assertNotNull(thing1.getId());
			assertEquals("THING_1", thing1.getName());
			assertEquals("This is thing #1", thing1.getDescription());
			assertNotNull(thing1.getCreatedOn());
			assertNotNull(thing1.getUpdatedOn());

			// find second thing
			final Thing thing2 = thingService.findThing("THING_T1");
			LOG.info("thing2 = {}", thing2);
			assertNotNull(thing2);
			assertNotNull(thing2.getId());
			assertEquals("THING_T1", thing2.getName());
			assertEquals("This is thing #1T", thing2.getDescription());
			assertNotNull(thing2.getCreatedOn());
			assertNotNull(thing2.getUpdatedOn());

			// find unknown thing by name
			try {
				thingService.findThing("THING_X");
				fail("Unsuccessfully found an unknown thing!");
			} catch (final UnknownThingException e) {
			}
			try {
				thingService.findThing("THING_X", false);
				fail("Unsuccessfully found an unknown thing!");
			} catch (final UnknownThingException e) {
			}
			assertNull(thingService.findThing("THING_X", true));

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test03_FindThings()");
	}

	@Test
	public void test04_RetrieveThings() {
		LOG.info("BEGIN: test04_RetrieveThings()");
		try {

			// find first thing
			final List<Thing> things = thingService.retrieveThings(null);
			assertNotNull(things);
			assertEquals(6, things.size());

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test04_RetrieveThings()");
	}

	@Test
	public void test05_DeleteThings() {
		LOG.info("BEGIN: test05_DeleteThings()");
		try {

			// delete existing thing
			thingService.deleteThing("THING_1");
			assertEquals(5, thingService.retrieveThings(null).size());

			// attempt to delete non-existing thing
			try {
				thingService.deleteThing("THING_X");
				fail("Unsuccessfully deleted an unknown thing!");
			} catch (final UnknownThingException e) {
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test05_DeleteThings()");
	}

}
