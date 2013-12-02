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
package org.jrb.lots.web;

import org.jrb.commons.web.MessageResponse;
import org.jrb.commons.web.ResponseUtils;
import org.jrb.lots.service.tag.DuplicateTagException;
import org.jrb.lots.service.tag.InvalidTagException;
import org.jrb.lots.service.tag.UnknownTagException;
import org.jrb.lots.service.thing.DuplicateThingException;
import org.jrb.lots.service.thing.InvalidThingException;
import org.jrb.lots.service.thing.UnknownThingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handles application-wide errors for the Lists Of ThingS (LOTS) application.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private final static Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@Autowired
	private ResponseUtils utils;

	/**
	 * Converts one of several client-based bad request exceptions into an HTTP
	 * 400 response with an error body. The mapped exceptions are as follows:
	 * <ul>
	 * <li>{@link InvalidTagException}</li>
	 * <li>{@link InvalidThingException}</li>
	 * </ul>
	 * 
	 * @param e
	 *            the client exception
	 * @return the error body
	 */
	@ExceptionHandler({ InvalidTagException.class, InvalidThingException.class })
	public ResponseEntity<MessageResponse> handleClientBadRequest(final Exception e) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(e.getMessage(), e);
		}
		return utils.createMessageResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Converts one of several client-based conflict exceptions into an HTTP 409
	 * response with an error body. The mapped exceptions are as follows:
	 * <ul>
	 * <li>{@link DuplicateTagException}</li>
	 * <li>{@link DuplicateThingException}</li>
	 * </ul>
	 * 
	 * @param e
	 *            the client exception
	 * @return the error body
	 */
	@ExceptionHandler({ DuplicateTagException.class, DuplicateThingException.class })
	public ResponseEntity<MessageResponse> handleConflictError(final Exception e) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(e.getMessage(), e);
		}
		return utils.createMessageResponse(e.getMessage(), HttpStatus.CONFLICT);
	}

	/**
	 * Converts one of several client-based not found exceptions into an HTTP
	 * 404 response with an error body. The mapped exceptions are as follows:
	 * <ul>
	 * <li>{@link UnknownTagException}</li>
	 * <li>{@link UnknownThingException}</li>
	 * </ul>
	 * 
	 * @param e
	 *            the client exception
	 * @return the error body
	 */
	@ExceptionHandler({ UnknownTagException.class, UnknownThingException.class })
	public ResponseEntity<MessageResponse> handleNotFoundError(final Exception e) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(e.getMessage(), e);
		}
		return utils.createMessageResponse(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	/**
	 * Converts any remaining unmatched server-based errors into an HTTP 500
	 * response with an error body.
	 * 
	 * @param e
	 *            the server exception
	 * @return the error body
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<MessageResponse> handleServerError(final Exception e) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(e.getMessage(), e);
		}
		return utils.createMessageResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
