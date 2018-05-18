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
package de.richtercloud.validation.tools;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Handles order of relevant fields (e.g. JPA-based implementations might want
 * to return the {@code @Id} field at the first position always) and exclusion
 * of irrelevant fields for different implementations and use cases (e.g.
 * JPA-based implementations might always want to exclude {@code @Transient}
 * annotated fields. Different components might need different retrievers (e.g.
 * read-only component might want to include read-only fields, forms not).
 *
 * Implementations might take care about caching and such.
 *
 * @author richter
 */
/*
internal implementation notes:
- It's unlikely to avoid retrieving all fields in a superclass and then removing
from them since that bloats up code complexity for the sake of saving a few
comparisons - limited by a realistic number of class fields, i.e. most certainly
below 50.
*/
public interface FieldRetriever {

    /**
     * Retrieves relevant fields of {@code clazz}.What that means (especially
 the order of the list or whether superclass' fields ought to be included)
 is up to the implementation.
     *
     * @param clazz
     * @return the list of relevant field as specified by implementation,
     * never {@code null}
     */
    /*
     internal implementation notes:
     - return a List in order to be able to modify order (it's nice to have
     @Id annotated property first)
     */
    List<Field> retrieveRelevantFields(Class<?> clazz);
}
