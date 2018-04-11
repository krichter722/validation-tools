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
package richtercloud.validation.tools.extension;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author richter
 */
public class NoEmptyEntriesListValidator implements ConstraintValidator<NoEmptyEntriesList, List<?>> {

    @Override
    public void initialize(NoEmptyEntriesList constraintAnnotation) {
        //do nothing
    }

    /**
     * Validates that a non-{@code null} list doesn't contain {@code null} or
     * the empty string. {@code null} is valid.
     * @param value the value to check
     * @param context the context passed by the validation framework
     * @return {@code true} if {@code value} is {@code null} (in order to be
     * able to combine with {@code @NotNull} or doesn't contain {@code null} or
     * the empty string
     */
    /*
    internal implementation notes:
    - ignore the fact that checking whether list contains "" is a waste for
    lists which don't contain strings
    */
    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        return !value.contains(null) && !value.contains("");
    }
}
