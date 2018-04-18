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
package richtercloud.validation.tools.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author richter
 */
public class Bean2Validator implements ConstraintValidator<ValidBean2, Bean2> {
    /**
     * Since PBT with jqwik are executed sequentially (as opposed to parallel)
     * this is the simplest way to manipulate the validation result. Since
     * PowerMockito doesn't work with JUnit 5 (which is required by jqwik) (see
     * https://github.com/junit-team/junit5/issues/201 for details) it's
     * the only way as well.
     */
    public static boolean retValue;

    @Override
    public void initialize(ValidBean2 constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(Bean2 value, ConstraintValidatorContext context) {
        return retValue;
    }
}
