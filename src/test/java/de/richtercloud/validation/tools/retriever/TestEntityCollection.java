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
package de.richtercloud.validation.tools.retriever;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author richter
 */
public class TestEntityCollection {
    private String a = "a";
    private List<Integer> bs = new LinkedList<>(Arrays.asList(1));
    private List<Boolean> gs = new LinkedList<>(Arrays.asList(Boolean.TRUE));
    private List<TestEntity> testEntitys = new LinkedList<>(Arrays.asList(new TestEntity()));

    public TestEntityCollection() {
    }
}
