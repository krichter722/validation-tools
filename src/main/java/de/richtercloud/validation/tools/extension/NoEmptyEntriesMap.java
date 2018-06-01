/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.richtercloud.validation.tools.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Indicates that a {@link java.util.Map} field must not contain keys or values
 * which are the empty string or {@code null}.
 *
 * @author richter
 */
@Target( { ElementType.FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = NoEmptyEntriesMapValidator.class)
@Documented
public @interface NoEmptyEntriesMap {

    String message() default "Map must contain no null or empty keys as well as no null or empty values";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
