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
package org.jrb.lots.service.tag;

import java.io.IOException;
import java.util.List;

import org.jrb.commons.service.NamedKey;
import org.jrb.lots.domain.Tag;
import org.jrb.lots.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/**
 * JPA implementation of a {@link TagService}.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
@Service("tagService")
public class TagServiceImpl implements TagService {

	private final static Logger LOG = LoggerFactory.getLogger(TagService.class);

	@Autowired
	private TagRepository tagRepository;

	private final int maxCacheSize = 1000;

	private final LoadingCache<NamedKey, Tag> tagCache = CacheBuilder.newBuilder()
			.maximumSize(maxCacheSize)
			.build(new CacheLoader<NamedKey, Tag>() {
				@Override
				public Tag load(final NamedKey key) throws TagServiceException {
					try {
						return loadTag(key);
					} catch (final Throwable t) {
						LOG.error(t.getMessage(), t);
						throw new TagServiceException(t.getMessage(), t);
					}
				}
			});

	@Override
	public void clearCache() {
		tagCache.invalidateAll();
	}

	@Override
	public void clearCache(final Long id) {
		tagCache.invalidate(new NamedKey(id));
	}

	@Override
	public void clearCache(final String name) {
		tagCache.invalidate(new NamedKey(name));
	}

	@Override
	public Tag createTag(final String name, final String description)
			throws DuplicateTagException, InvalidTagException, TagServiceException {
		try {
			final Tag tag = new Tag.Builder()
					.setName(name)
					.setDescription(description)
					.markAsCreated().build();
			return tagRepository.save(tag);
		} catch (final DataIntegrityViolationException e) {
			throw new DuplicateTagException("Duplicate tag! name = " + name, e);
		} catch (final IllegalArgumentException e) {
			throw new InvalidTagException("Invalid tag! name = " + name, e);
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to create tag! name = " + name, t);
		}
	}

	@Override
	public Tag createTag(final Tag submitted)
			throws DuplicateTagException, InvalidTagException, TagServiceException {
		try {
			final Tag tag = new Tag.Builder()
					.setName(submitted.getName())
					.setDescription(submitted.getDescription())
					.markAsCreated().build();
			return tagRepository.save(tag);
		} catch (final DataIntegrityViolationException e) {
			throw new DuplicateTagException("Duplicate tag! submitted = " + submitted, e);
		} catch (final IllegalArgumentException e) {
			throw new InvalidTagException("Invalid tag! submitted = " + submitted, e);
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to create tag! submitted = " + submitted, t);
		}
	}

	@Override
	public void deleteTag(final Long id) throws UnknownTagException, TagServiceException {
		try {
			final Tag tag = tagRepository.findOne(id);
			if (tag != null) {
				tagRepository.delete(tag);
				tagCache.invalidate(new NamedKey(id));
			} else {
				throw new UnknownTagException("Tag is unknown! id = " + id);
			}
		} catch (final UnknownTagException e) {
			throw e;
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to delete tag! id = " + id, t);
		}
	}

	@Override
	public void deleteTag(final String name) throws UnknownTagException, TagServiceException {
		try {
			final Tag tag = tagRepository.findByName(name);
			if (tag != null) {
				tagRepository.delete(tag);
				tagCache.invalidate(new NamedKey(name));
			} else {
				throw new UnknownTagException("Tag is unknown! name = " + name);
			}
		} catch (final UnknownTagException e) {
			throw e;
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to delete tag! name = " + name, t);
		}
	}

	@Override
	public Tag findTag(final Long id) throws UnknownTagException, TagServiceException {
		try {
			return tagCache.get(new NamedKey(id));
		} catch (final InvalidCacheLoadException e) {
			throw new UnknownTagException("Tag is unknown! id = " + id, e);
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to find tag! id = " + id, t);
		}
	}

	@Override
	public Tag findTag(final String name) throws UnknownTagException, TagServiceException {
		try {
			return tagCache.get(new NamedKey(name));
		} catch (final InvalidCacheLoadException e) {
			throw new UnknownTagException("Tag is unknown! name = " + name, e);
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to find tag! name = " + name, t);
		}
	}

	private Tag loadTag(final NamedKey key) throws IOException {
		final Tag tag = (key.hasId())
				? tagRepository.findOne(key.getId())
				: tagRepository.findByName(key.getName());
		return tag;
	}

	@Override
	public List<Tag> retrieveTags(final Tag criteria) throws TagServiceException {
		try {
			return Lists.newArrayList(tagRepository.findAll());
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to retrieve tags! criteria = " + criteria, t);
		}
	}

	@Override
	public Page<Tag> retrieveTags(final Tag criteria, final Pageable pageable) throws TagServiceException {
		try {
			return tagRepository.findAll(pageable);
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to retrieve tags! criteria = " + criteria, t);
		}
	}

	@Override
	public Tag updateTag(final Tag tag) throws InvalidTagException, TagServiceException {
		try {
			final Tag toBeUpdated = new Tag.Builder()
					.from(tagRepository.findOne(tag.getId()))
					.setName(tag.getName())
					.setDescription(tag.getDescription())
					.markAsUpdated()
					.build();
			final Tag updatedTag = tagRepository.save(toBeUpdated);
			tagCache.invalidate(new NamedKey(tag.getId()));
			return updatedTag;
		} catch (final IllegalArgumentException e) {
			throw new InvalidTagException("Invalid tag! tag = " + tag, e);
		} catch (final Throwable t) {
			throw new TagServiceException("Unable to create tag! tag = " + tag, t);
		}
	}

}
