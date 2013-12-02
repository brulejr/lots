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
import org.jrb.lots.domain.Thing;
import org.jrb.lots.service.thing.DuplicateThingException;
import org.jrb.lots.service.thing.InvalidThingException;
import org.jrb.lots.service.thing.ThingService;
import org.jrb.lots.service.thing.ThingServiceException;
import org.jrb.lots.service.thing.UnknownThingException;
import org.jrb.lots.web.response.ThingListResponse;
import org.jrb.lots.web.response.ThingResponse;
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
@RequestMapping("/api/thing")
public class ThingController {

	@Autowired
	private ThingService thingService;

	@Autowired
	private ResponseUtils responseUtils;

	private CrudControllerUtils<Thing, ThingResponse, ThingListResponse> controllerUtils;

	@PostConstruct
	public void init() {
		this.controllerUtils = new CrudControllerUtils<Thing, ThingResponse, ThingListResponse>(responseUtils);
	}

	/**
	 * RESTful CRUD endpoint to create a thing.
	 * 
	 * @param thing
	 *            the thing to be created.
	 * @return a Spring MVC response containing the newly-created thing
	 * @throws DuplicateThingException
	 *             if attempt made to create a thing that already exists
	 * @throws InvalidThingException
	 *             if the thing to be created does not pass the established
	 *             validation rules
	 * @throws ThingServiceException
	 *             if an unexpected error occurred while creating a thing
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ThingResponse> createThing(@RequestBody final Thing thing)
			throws DuplicateThingException, InvalidThingException, ThingServiceException {

		return controllerUtils.createEntity(
				thing,
				Thing.class,
				ThingResponse.class,
				getClass(),
				new CreateEntityCallback<Thing>() {
					@Override
					public Thing createEntity(Thing submitted) throws ServiceException {
						return thingService.createThing(submitted);
					}
				});
	}

	/**
	 * RESTful CRUD endpoint to create a thing.
	 * 
	 * @param thingId
	 *            the identifier of the thing to be deleted
	 * @return a Spring MVC response containing the deletion status
	 * @throws UnknownThingException
	 *             if attempt made to delete an unregistered thing
	 * @throws ThingServiceException
	 *             if an unexpected error occurred while deleting a thing
	 */
	@RequestMapping(value = "{thingId}", method = RequestMethod.DELETE)
	public ResponseEntity<MessageResponse> deleteThing(@PathVariable final Long thingId)
			throws UnknownThingException, ThingServiceException {

		return controllerUtils.deleteEntity(
				thingId,
				Thing.class,
				ThingResponse.class,
				ThingController.class,
				new DeleteEntityCallback<Thing>() {
					@Override
					public void deleteEntity(Long entityId) throws ServiceException {
						thingService.deleteThing(thingId);
					}
				});
	}

	/**
	 * RESTful CRUD endpoint to find an existing thing.
	 * 
	 * @param thingId
	 *            the identifier of the desired thing
	 * @return a Spring MVC response containing the found entity
	 * @throws UnknownThingException
	 *             if attempt made to locate an unregistered thing
	 * @throws ThingServiceException
	 *             if an unexpected error occurred while finding a thing
	 */
	@RequestMapping(value = "{thingId}", method = RequestMethod.GET)
	public ResponseEntity<ThingResponse> findEntity(@PathVariable final Long thingId)
			throws UnknownThingException, ThingServiceException {

		return controllerUtils.findEntity(
				thingId,
				Thing.class,
				ThingResponse.class,
				ThingController.class,
				new FindEntityCallback<Thing>() {
					@Override
					public Thing findEntity(Long entityId) throws ServiceException {
						return thingService.findThing(thingId);
					}
				});
	}

	/**
	 * RESTful CRUD endpoint to retrieve existing things.
	 * 
	 * @return a Spring MVC response containing the entity list
	 * @throws ThingServiceException
	 *             if an unexpected error occurred while retrieving the things
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<ThingListResponse> retrieveThings() throws ThingServiceException {

		return controllerUtils.retrieveEntities(
				ThingListResponse.class,
				new RetrieveEntitiesCallback<Thing>() {
					@Override
					public List<Thing> retrieveEntities() throws ServiceException {
						return thingService.retrieveThings(null);
					}
				});

	}

	/**
	 * RESTful CRUD endpoint to update an existing thing.
	 * 
	 * @param thingId
	 *            the identifier of the thing to be updated
	 * @param thing
	 *            the thing updates
	 * @return a Spring MVC response containing the updated thing
	 * @throws InvalidThingException
	 *             if the thing updates do not pass the established validation
	 *             rules
	 * @throws UnknownThingException
	 *             if attempt made to updated an unregistered thing
	 * @throws ThingServiceException
	 *             if an unexpected error occurred while updating a thing
	 */
	@RequestMapping(value = "{thingId}", method = RequestMethod.PATCH)
	public ResponseEntity<ThingResponse> updateThing(
			@PathVariable final Long thingId,
			@RequestBody final Thing thing)
			throws InvalidThingException, UnknownThingException, ThingServiceException {
		return controllerUtils.updateEntity(
				thingId,
				thing,
				Thing.class,
				ThingResponse.class,
				ThingController.class,
				new UpdateEntityCallback<Thing>() {
					@Override
					public Thing updateEntity(Long entityId, Thing submitted) throws ServiceException {
						final Thing existing = thingService.findThing(thingId);
						final Thing.Builder builder = new Thing.Builder().from(existing);
						if (controllerUtils.different(submitted.getName(), existing.getName())) {
							builder.setName(submitted.getName());
						}
						if (controllerUtils.different(submitted.getDescription(), existing.getDescription())) {
							builder.setDescription(submitted.getDescription());
						}
						return thingService.updateThing(builder.build());
					}
				});
	}

}
