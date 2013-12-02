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
package org.jrb.lots.service.thing;

import java.util.List;

import org.jrb.lots.domain.Tag;
import org.jrb.lots.domain.Thing;
import org.jrb.lots.repository.ThingRepository;
import org.jrb.lots.service.tag.TagService;
import org.jrb.lots.service.tag.TagServiceException;
import org.jrb.lots.service.tag.UnknownTagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

/**
 * JPA implementation of a {@link ThingService}.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
@Service("thingService")
public class ThingServiceImpl implements ThingService {

	@Autowired
	private ThingRepository thingRepository;

	@Autowired
	private TagService tagService;

	@Override
	public Thing createThing(final Thing submitted)
			throws DuplicateThingException, InvalidThingException, ThingServiceException {
		try {
			final Thing.Builder thingBuilder = new Thing.Builder()
					.setName(submitted.getName())
					.setDescription(submitted.getDescription())
					.addTags(submitted.getTags());
			if (submitted.getTagnames() != null) {
				for (final String tagname : submitted.getTagnames()) {
					thingBuilder.addTag(linkTag(tagname));
				}
			}
			final Thing thing = thingBuilder.markAsCreated().build();
			return thingRepository.save(thing);
		} catch (final DataIntegrityViolationException e) {
			throw new DuplicateThingException("Duplicate thing! submitted = " + submitted, e);
		} catch (final IllegalArgumentException e) {
			throw new InvalidThingException("Invalid thing! submitted = " + submitted, e);
		} catch (final Throwable t) {
			throw new ThingServiceException("Unable to create thing! submitted = " + submitted, t);
		}
	}

	@Override
	public void deleteThing(final Long id) throws UnknownThingException, ThingServiceException {
		try {
			final Thing thing = thingRepository.findOne(id);
			if (thing != null) {
				thingRepository.delete(thing);
			} else {
				throw new UnknownThingException("Thing is unknown! id = " + id);
			}
		} catch (final UnknownThingException e) {
			throw e;
		} catch (final Throwable t) {
			throw new ThingServiceException("Unable to delete thing! id = " + id, t);
		}
	}

	@Override
	public void deleteThing(final String name) throws UnknownThingException, ThingServiceException {
		try {
			final Thing thing = thingRepository.findByName(name);
			if (thing != null) {
				thingRepository.delete(thing);
			} else {
				throw new UnknownThingException("Thing is unknown! name = " + name);
			}
		} catch (final UnknownThingException e) {
			throw e;
		} catch (final Throwable t) {
			throw new ThingServiceException("Unable to delete thing! name = " + name, t);
		}
	}

	@Override
	public Thing findThing(final Long id) throws UnknownThingException, ThingServiceException {
		return findThing(id, false);
	}

	@Override
	public Thing findThing(final Long id, final boolean allowUnknown) throws UnknownThingException, ThingServiceException {
		try {
			Thing thing = thingRepository.findOne(id);
			if (thing != null) {
				if (thing.getTags() != null) {
					final Thing.Builder thingBuilder = new Thing.Builder().from(thing);
					for (final Tag tag : thing.getTags()) {
						thingBuilder.addTagname(tag.getName());
					}
					thing = thingBuilder.build();
				}
				return thing;
			} else if (!allowUnknown) {
				throw new UnknownThingException("Thing is unknown! id = " + id);
			}
			return thing;
		} catch (final UnknownThingException e) {
			throw e;
		} catch (final Throwable t) {
			throw new ThingServiceException("Unable to find thing! id = " + id, t);
		}
	}

	@Override
	public Thing findThing(final String name) throws UnknownThingException, ThingServiceException {
		return findThing(name, false);
	}

	@Override
	public Thing findThing(final String name, final boolean allowUnknown) throws UnknownThingException, ThingServiceException {
		try {
			Thing thing = thingRepository.findByName(name);
			if (thing != null) {
				if (thing.getTags() != null) {
					final Thing.Builder thingBuilder = new Thing.Builder().from(thing);
					for (final Tag tag : thing.getTags()) {
						thingBuilder.addTagname(tag.getName());
					}
					thing = thingBuilder.build();
				}
				return thing;
			} else if (!allowUnknown) {
				throw new UnknownThingException("Thing is unknown! name = " + name);
			}
			return thing;
		} catch (final UnknownThingException e) {
			throw e;
		} catch (final Throwable t) {
			throw new ThingServiceException("Unable to find thing! name = " + name, t);
		}
	}

	private Tag linkTag(final String tagname) throws ThingServiceException {
		try {
			try {
				final Tag tag = tagService.findTag(tagname);
				return tag;
			} catch (final UnknownTagException e) {
				final Tag tag = new Tag.Builder().setName(tagname).build();
				return tagService.createTag(tag);
			}
		} catch (final TagServiceException e) {
			throw new ThingServiceException("Unable to create tag [" + tagname + "] for thing!", e);
		}
	}

	@Override
	public List<Thing> retrieveThings(final Thing criteria) throws ThingServiceException {
		try {
			return Lists.newArrayList(thingRepository.findAll());
		} catch (final Throwable t) {
			throw new ThingServiceException("Unable to retrieve things! criteria = " + criteria, t);
		}
	}

	@Override
	public Page<Thing> retrieveThings(final Thing criteria, final Pageable pageable) throws ThingServiceException {
		try {
			return thingRepository.findAll(pageable);
		} catch (final Throwable t) {
			throw new ThingServiceException("Unable to retrieve things! criteria = " + criteria, t);
		}
	}

	@Override
	public Thing updateThing(final Thing thing) throws InvalidThingException, ThingServiceException {
		try {
			final Thing updated = new Thing.Builder()
					.from(thingRepository.findOne(thing.getId()))
					.setName(thing.getName())
					.setDescription(thing.getDescription())
					.markAsUpdated()
					.build();
			return thingRepository.save(updated);
		} catch (final IllegalArgumentException e) {
			throw new InvalidThingException("Invalid thing! thing = " + thing, e);
		} catch (final Throwable t) {
			throw new ThingServiceException("Unable to create thing! thing = " + thing, t);
		}
	}

}
