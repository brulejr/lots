package org.jrb.lots.service.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.jrb.lots.SpringApplication;
import org.jrb.lots.domain.Tag;
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

/**
 * Unit test cases for {@link TagServiceImpl}.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringApplication.class)
@ActiveProfiles("LOCAL")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TagServiceTest {

	private final static Logger LOG = LoggerFactory.getLogger(TagServiceTest.class);

	@Autowired
	private TagService tagService;
	
	private Tag createTag(final String name, final String description) throws IOException, TagServiceException {
		final Tag tag = tagService.createTag(name, description);
		assertNotNull(tag);
		assertNotNull(tag.getId());
		assertEquals(name, tag.getName());
		assertEquals(description, tag.getDescription());
		assertNotNull(tag.getCreatedOn());
		assertNotNull(tag.getUpdatedOn());
		return tag;
	}
	
	@Test
	public void test01_CreateTags() {
		LOG.info("BEGIN: test01_CreateTags()");
		try {

			// create first tag
			createTag("TAG_1", "This is tag #1");

			// create second tag
			createTag("TAG_2", "This is tag #2");

			// create second tag
			createTag("TAG_3", "This is tag #3");

			// create second tag
			createTag("TAG_4", "This is tag #4");

			// attempt to recreate first tag
			try {
				tagService.createTag("TAG_1", "This is a dupe");
				fail("Unsuccessfully created a duplicate tag!");
			} catch (final DuplicateTagException e) {
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test01_CreateTags()");
	}

	@Test
	public void test02_FindTags() {
		LOG.info("BEGIN: test02_FindTags()");
		try {

			// find first tag
			final Tag tag1 = tagService.findTag("TAG_1");
			assertNotNull(tag1);
			assertNotNull(tag1.getId());
			assertEquals("TAG_1", tag1.getName());
			assertEquals("This is tag #1", tag1.getDescription());
			assertNotNull(tag1.getCreatedOn());
			assertNotNull(tag1.getUpdatedOn());

			// find first tag
			final Tag tag2 = tagService.findTag(2L);
			assertNotNull(tag2);
			assertNotNull(tag2.getId());
			assertEquals("TAG_2", tag2.getName());
			assertEquals("This is tag #2", tag2.getDescription());
			assertNotNull(tag2.getCreatedOn());
			assertNotNull(tag2.getUpdatedOn());

			try {
				tagService.findTag("TAG_X");
				fail("Unsuccessfully found an unknown tag!");
			} catch (final UnknownTagException e) {
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test02_FindTags()");
	}

	@Test
	public void test03_RetrieveTags() {
		LOG.info("BEGIN: test03_RetrieveTags()");
		try {

			// find first tag
			final List<Tag> tags = tagService.retrieveTags(null);
			assertNotNull(tags);
			assertEquals(4, tags.size());

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test03_RetrieveTags()");
	}

	@Test
	public void test04_UpdateTags() {
		LOG.info("BEGIN: test04_UpdateTags()");
		try {

			// update existing tag
			final Tag tag = new Tag.Builder()
				.from(tagService.findTag("TAG_1"))
				.setName("ABC")
				.setDescription("123")
				.build();
			final Tag updated = tagService.updateTag(tag);
			assertNotNull(updated);
			assertNotNull(updated.getId());
			assertEquals("ABC", updated.getName());
			assertEquals("123", updated.getDescription());
			assertNotNull(updated.getCreatedOn());
			assertNotNull(updated.getUpdatedOn());

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test04_UpdateTags()");
	}

	@Test
	public void test05_DeleteTags() {
		LOG.info("BEGIN: test05_DeleteTags()");
		try {

			// delete existing tag
			tagService.deleteTag("ABC");
			assertEquals(3, tagService.retrieveTags(null).size());

			// delete existing tag
			tagService.deleteTag(2L);
			assertEquals(2, tagService.retrieveTags(null).size());

			// attempt to delete non-existing tag
			try {
				tagService.deleteTag("TAG_X");
				fail("Unsuccessfully deleted an unknown tag!");
			} catch (final UnknownTagException e) {
			}

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			fail(t.getMessage());
		}
		LOG.info("END: test05_DeleteTags()");
	}

}
