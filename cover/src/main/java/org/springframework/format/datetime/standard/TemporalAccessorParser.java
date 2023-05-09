/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.format.datetime.standard;

import org.springframework.format.Parser;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * {@link Parser} implementation for a JSR-310 {@link TemporalAccessor},
 * using a {@link DateTimeFormatter} (the contextual one, if available).
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.0
 * @see DateTimeContextHolder#getFormatter
 * @see LocalDate#parse(CharSequence, DateTimeFormatter)
 * @see LocalTime#parse(CharSequence, DateTimeFormatter)
 * @see LocalDateTime#parse(CharSequence, DateTimeFormatter)
 * @see ZonedDateTime#parse(CharSequence, DateTimeFormatter)
 * @see OffsetDateTime#parse(CharSequence, DateTimeFormatter)
 * @see OffsetTime#parse(CharSequence, DateTimeFormatter)
 */
public final class TemporalAccessorParser implements Parser<TemporalAccessor> {

	private final Class<? extends TemporalAccessor> temporalAccessorType;

	private final DateTimeFormatter formatter;

	@Nullable
	private final String[] fallbackPatterns;

	@Nullable
	private final Object source;


	/**
	 * Create a new TemporalAccessorParser for the given TemporalAccessor type.
	 * @param temporalAccessorType the specific TemporalAccessor class
	 * (LocalDate, LocalTime, LocalDateTime, ZonedDateTime, OffsetDateTime, OffsetTime)
	 * @param formatter the base DateTimeFormatter instance
	 */
	public TemporalAccessorParser(Class<? extends TemporalAccessor> temporalAccessorType, DateTimeFormatter formatter) {
		this(temporalAccessorType, formatter, null, null);
	}

	TemporalAccessorParser(Class<? extends TemporalAccessor> temporalAccessorType, DateTimeFormatter formatter,
                           @Nullable String[] fallbackPatterns, @Nullable Object source) {

		this.temporalAccessorType = temporalAccessorType;
		this.formatter = formatter;
		this.fallbackPatterns = fallbackPatterns;
		this.source = source;
	}


	@Override
	public TemporalAccessor parse(String text, Locale locale) throws ParseException {
		try {
			return doParse(text, locale, this.formatter);
		}
		catch (DateTimeParseException ex) {
			if (!ObjectUtils.isEmpty(this.fallbackPatterns)) {
				for (String pattern : this.fallbackPatterns) {
					try {
						DateTimeFormatter fallbackFormatter = DateTimeFormatterUtils.createStrictDateTimeFormatter(pattern);
						return doParse(text, locale, fallbackFormatter);
					}
					catch (DateTimeParseException ignoredException) {
						// Ignore fallback parsing exceptions since the exception thrown below
						// will include information from the "source" if available -- for example,
						// the toString() of a @DateTimeFormat annotation.
					}
				}
			}
			if (this.source != null) {
				throw new DateTimeParseException(
						String.format("Unable to parse date time value \"%s\" using configuration from %s", text, this.source),
						text, ex.getErrorIndex(), ex);
			}
			// else rethrow original exception
			throw ex;
		}
	}

	private TemporalAccessor doParse(String text, Locale locale, DateTimeFormatter formatter) throws DateTimeParseException {
		// 改动此处，添加yyyy-MM-dd HH:mm:ss支持
		DateTimeFormatter formatterToUse = DateTimeContextHolder.getFormatter(formatter, locale);
		if (text.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
			formatterToUse = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		}
		if (LocalDate.class == this.temporalAccessorType) {
			return LocalDate.parse(text, formatterToUse);
		}
		else if (LocalTime.class == this.temporalAccessorType) {
			return LocalTime.parse(text, formatterToUse);
		}
		else if (LocalDateTime.class == this.temporalAccessorType) {
			return LocalDateTime.parse(text, formatterToUse);
		}
		else if (ZonedDateTime.class == this.temporalAccessorType) {
			return ZonedDateTime.parse(text, formatterToUse);
		}
		else if (OffsetDateTime.class == this.temporalAccessorType) {
			return OffsetDateTime.parse(text, formatterToUse);
		}
		else if (OffsetTime.class == this.temporalAccessorType) {
			return OffsetTime.parse(text, formatterToUse);
		}
		else {
			throw new IllegalStateException("Unsupported TemporalAccessor type: " + this.temporalAccessorType);
		}
	}

}
