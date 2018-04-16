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

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Size;

/**
 *
 * @author richter
 */
public class Bean1 {
    @Valid
    @Size(min = 1, message = "Bean1.bean2s mustn't be empty")
    private List<Bean2> bean2s;

    public Bean1(List<Bean2> bean2s) {
        this.bean2s = bean2s;
    }
}
