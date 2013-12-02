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
package org.jrb.lots.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Domain entity for a thing for the List Of ThingS (LOTS) application.
 * 
 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
 */
@Entity
@JsonInclude(Include.NON_EMPTY)
public class Thing implements Identifiable<Long> {

	/**
	 * Builder for a {@link Thing} domain entity.
	 * 
	 * @author <a href="mailto:brulejr@gmail.com">Jon Brule</a>
	 */
	public static class Builder {

		private final Thing thing;

		public Builder() {
			thing = new Thing();
		}

		public Builder addTag(final Tag tag) {
			thing.tags.add(tag);
			return this;
		}

		public Builder addTags(final Collection<Tag> tags) {
			if (tags != null) {
				thing.tags.addAll(tags);
			}
			return this;
		}

		public Builder addTagname(final String tagname) {
			thing.tagnames.add(tagname);
			return this;
		}

		public Builder addTagnames(final Collection<String> tagnames) {
			if (tagnames != null) {
				thing.tagnames.addAll(tagnames);
			}
			return this;
		}

		public Thing build() {
			return thing;
		}

		public Builder from(final Thing doc) {
			thing.createdOn = doc.createdOn;
			thing.description = doc.description;
			thing.id = doc.id;
			thing.name = doc.name;
			thing.updatedOn = doc.updatedOn;
			thing.version = doc.version;
			return this;
		}

		public Builder markAsCreated() {
			final Date timestamp = new Date();
			thing.createdOn = timestamp;
			thing.updatedOn = timestamp;
			return this;
		}

		public Builder markAsUpdated() {
			final Date timestamp = new Date();
			thing.updatedOn = timestamp;
			return this;
		}

		public Builder setDescription(final String description) {
			thing.description = description;
			return this;
		}

		public Builder setName(final String name) {
			thing.name = name;
			return this;
		}

		public Builder setTags(final Set<Tag> tags) {
			thing.tags = tags;
			return this;
		}

		public Builder setTagnames(final Set<String> tagnames) {
			thing.tagnames = tagnames;
			return this;
		}

	}

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true, nullable = false)
	private String name;

	@Column(nullable = true)
	private String description;

	@Column(nullable = false)
	private Date createdOn;

	@Column(nullable = false)
	private Date updatedOn;

	@Version
	private Integer version;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Tag> tags = new HashSet<>(0);

	@Transient
	private Set<String> tagnames = new HashSet<>(0);

	private Thing() {
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Collection<Tag> getTags() {
		return tags;
	}

	public Collection<String> getTagnames() {
		return tagnames;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public Integer getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " ["
				+ (id != null ? "id=" + id + ", " : "")
				+ (name != null ? "name=" + name + ", " : "")
				+ (version != null ? "version=" + version + ", " : "")
				+ (description != null ? "description=" + description + ", " : "")
				+ (createdOn != null ? "createdOn=" + DateFormatUtils.ISO_DATETIME_FORMAT.format(createdOn) + ", " : "")
				+ (updatedOn != null ? "updatedOn=" + DateFormatUtils.ISO_DATETIME_FORMAT.format(updatedOn) + ", " : "")
				+ (tagnames != null ? "tagnames=" + tagnames : "")
				+ "]";
	}
}