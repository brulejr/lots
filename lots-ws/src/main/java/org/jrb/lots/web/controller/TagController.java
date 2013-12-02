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
package org.jrb.lots.web.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jrb.commons.service.ServiceException;
import org.jrb.commons.web.MessageResponse;
import org.jrb.commons.web.ResponseUtils;
import org.jrb.commons.web.controller.CrudControllerUtils;
import org.jrb.commons.web.controller.CrudControllerUtils.CreateEntityCallback;
import org.jrb.commons.web.controller.CrudControllerUtils.DeleteEntityCallback;
import org.jrb.commons.web.controller.CrudControllerUtils.FindEntityCallback;
import org.jrb.commons.web.controller.CrudControllerUtils.RetrieveEntitiesCallback;
import org.jrb.commons.web.controller.CrudControllerUtils.UpdateEntityCallback;
import org.jrb.lots.domain.Tag;
import org.jrb.lots.service.tag.DuplicateTagException;
import org.jrb.lots.service.tag.InvalidTagException;
import org.jrb.lots.service.tag.TagService;
import org.jrb.lots.service.tag.TagServiceException;
import org.jrb.lots.service.tag.UnknownTagException;
import org.jrb.lots.web.response.TagListResponse;
import org.jrb.lots.web.response.TagResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Thing URI controller for the Lists Of ThingS (LOTS) application.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
@Controller
@RequestMapping("/api/tag")
public class TagController {

	@Autowired
	private TagService tagService;

	@Autowired
	private ResponseUtils responseUtils;

	private CrudControllerUtils<Tag, TagResponse, TagListResponse> controllerUtils;

	@PostConstruct
	public void init() {
		this.controllerUtils = new CrudControllerUtils<Tag, TagResponse, TagListResponse>(responseUtils);
	}

	/**
	 * RESTful CRUD endpoint to create a tag.
	 * 
	 * @param tag
	 *            the tag to be created.
	 * @return a Spring MVC response containing the newly-created tag
	 * @throws DuplicateTagException
	 *             if attempt made to create a tag that already exists
	 * @throws InvalidTagException
	 *             if the tag to be created does not pass the established
	 *             validation rules
	 * @throws TagServiceException
	 *             if an unexpected error occurred while creating a tag
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<TagResponse> createTag(@RequestBody final Tag tag)
			throws DuplicateTagException, InvalidTagException, TagServiceException {

		return controllerUtils.createEntity(
				tag,
				Tag.class,
				TagResponse.class,
				getClass(),
				new CreateEntityCallback<Tag>() {
					@Override
					public Tag createEntity(Tag submitted) throws ServiceException {
						return tagService.createTag(submitted);
					}
				});
	}

	/**
	 * RESTful CRUD endpoint to create a tag.
	 * 
	 * @param tagId
	 *            the identifier of the tag to be deleted
	 * @return a Spring MVC response containing the deletion status
	 * @throws UnknownTagException
	 *             if attempt made to delete an unregistered tag
	 * @throws TagServiceException
	 *             if an unexpected error occurred while deleting a tag
	 */
	@RequestMapping(value = "{tagId}", method = RequestMethod.DELETE)
	public ResponseEntity<MessageResponse> deleteTag(@PathVariable final Long tagId)
			throws UnknownTagException, TagServiceException {

		return controllerUtils.deleteEntity(
				tagId,
				Tag.class,
				TagResponse.class,
				TagController.class,
				new DeleteEntityCallback<Tag>() {
					@Override
					public void deleteEntity(Long entityId) throws ServiceException {
						tagService.deleteTag(tagId);
					}
				});
	}

	/**
	 * RESTful CRUD endpoint to find an existing tag.
	 * 
	 * @param tagId
	 *            the identifier of the desired tag
	 * @return a Spring MVC response containing the found entity
	 * @throws UnknownTagException
	 *             if attempt made to locate an unregistered tag
	 * @throws TagServiceException
	 *             if an unexpected error occurred while finding a tag
	 */
	@RequestMapping(value = "{tagId}", method = RequestMethod.GET)
	public ResponseEntity<TagResponse> findEntity(@PathVariable final Long tagId)
			throws UnknownTagException, TagServiceException {

		return controllerUtils.findEntity(
				tagId,
				Tag.class,
				TagResponse.class,
				TagController.class,
				new FindEntityCallback<Tag>() {
					@Override
					public Tag findEntity(Long entityId) throws ServiceException {
						return tagService.findTag(tagId);
					}
				});
	}

	/**
	 * RESTful CRUD endpoint to retrieve existing tags.
	 * 
	 * @return a Spring MVC response containing the entity list
	 * @throws TagServiceException
	 *             if an unexpected error occurred while retrieving the tags
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<TagListResponse> retrieveTags() throws TagServiceException {

		return controllerUtils.retrieveEntities(
				TagListResponse.class,
				new RetrieveEntitiesCallback<Tag>() {
					@Override
					public List<Tag> retrieveEntities() throws ServiceException {
						return tagService.retrieveTags(null);
					}
				});

	}

	/**
	 * RESTful CRUD endpoint to update an existing tag.
	 * 
	 * @param tagId
	 *            the identifier of the tag to be updated
	 * @param tag
	 *            the tag updates
	 * @return a Spring MVC response containing the updated tag
	 * @throws InvalidTagException
	 *             if the tag updates do not pass the established validation
	 *             rules
	 * @throws UnknownTagException
	 *             if attempt made to updated an unregistered tag
	 * @throws TagServiceException
	 *             if an unexpected error occurred while updating a tag
	 */
	@RequestMapping(value = "{tagId}", method = RequestMethod.PATCH)
	public ResponseEntity<TagResponse> updateTag(
			@PathVariable final Long tagId,
			@RequestBody final Tag tag)
			throws InvalidTagException, UnknownTagException, TagServiceException {
		return controllerUtils.updateEntity(
				tagId,
				tag,
				Tag.class,
				TagResponse.class,
				TagController.class,
				new UpdateEntityCallback<Tag>() {
					@Override
					public Tag updateEntity(Long entityId, Tag submitted) throws ServiceException {
						final Tag existing = tagService.findTag(tagId);
						final Tag.Builder builder = new Tag.Builder().from(existing);
						if (controllerUtils.different(submitted.getName(), existing.getName())) {
							builder.setName(submitted.getName());
						}
						if (controllerUtils.different(submitted.getDescription(), existing.getDescription())) {
							builder.setDescription(submitted.getDescription());
						}
						return tagService.updateTag(builder.build());
					}
				});
	}

}
