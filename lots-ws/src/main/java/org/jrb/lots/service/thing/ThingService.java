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

import org.jrb.lots.domain.Thing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines the contract for a service that manages things.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
public interface ThingService {

	Thing createThing(Thing thing) 
			throws DuplicateThingException, InvalidThingException, ThingServiceException;

	void deleteThing(Long id)
			throws UnknownThingException, ThingServiceException;

	void deleteThing(String name)
			throws UnknownThingException, ThingServiceException;

	Thing findThing(Long id)
			throws UnknownThingException, ThingServiceException;

	Thing findThing(Long id, boolean allowUnknown)
			throws UnknownThingException, ThingServiceException;

	Thing findThing(String name)
			throws UnknownThingException, ThingServiceException;

	Thing findThing(String name, boolean allowUnknown)
			throws UnknownThingException, ThingServiceException;

	List<Thing> retrieveThings(Thing criteria)
			throws ThingServiceException;

	Page<Thing> retrieveThings(Thing criteria, Pageable pageable)
			throws ThingServiceException;

	Thing updateThing(Thing thing)
			throws InvalidThingException, ThingServiceException;

}
