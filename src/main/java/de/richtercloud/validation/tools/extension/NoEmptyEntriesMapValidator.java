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

import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author richter
 */
public class NoEmptyEntriesMapValidator implements ConstraintValidator<NoEmptyEntriesMap, Map<?, ?>> {

    @Override
    public void initialize(NoEmptyEntriesMap constraintAnnotation) {
        //do nothing
    }

    /**
     * Validates that a non-{@code null} map doesn't contain {@code null} or the
     * empty string in the key or value set. {@code null} is valid.
     * @param value the value to check
     * @param context the context passed by the validation framework
     * @return {@code true} if {@code value} is {@code null} (in order to be
     * able to combine with {@code @NotNull} or doesn't contain {@code null} or
     * the empty string in the key and value set
     */
    /*
    internal implementation notes:
    - ignore the fact that checking whether list contains "" is a waste for
    lists which don't contain strings
    */
    @Override
    public boolean isValid(Map<?, ?> value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        return !value.keySet().contains(null) && !value.keySet().contains("")
                && !value.values().contains(null) && !value.values().contains("");
    }
}
