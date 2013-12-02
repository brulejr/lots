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

import java.util.List;

import org.jrb.lots.domain.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines the contract for a service that manages tags.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
public interface TagService {

	void clearCache();

	void clearCache(Long id);

	void clearCache(String name);
	
	Tag createTag(Tag tag) 
			throws DuplicateTagException, InvalidTagException, TagServiceException;

	Tag createTag(String name, String description) 
			throws DuplicateTagException, InvalidTagException, TagServiceException;

	void deleteTag(Long id)
			throws UnknownTagException, TagServiceException;

	void deleteTag(String name)
			throws UnknownTagException, TagServiceException;

	Tag findTag(Long id)
			throws UnknownTagException, TagServiceException;

	Tag findTag(String name)
			throws UnknownTagException, TagServiceException;

	List<Tag> retrieveTags(Tag criteria)
			throws TagServiceException;

	Page<Tag> retrieveTags(Tag criteria, Pageable pageable)
			throws TagServiceException;

	Tag updateTag(Tag tag)
			throws InvalidTagException, TagServiceException;

}
