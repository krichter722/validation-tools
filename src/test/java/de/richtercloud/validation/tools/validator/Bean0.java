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
package de.richtercloud.validation.tools.validator;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 * @author richter
 */
@ValidBean0
public class Bean0 {
    @NotNull
    @Valid
    private Bean1 bean1;
    @NotNull
    private String property0;

    public Bean0(Bean1 bean1, String property0) {
        this.bean1 = bean1;
        this.property0 = property0;
    }
}
