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

import de.richtercloud.validation.tools.retriever.TestEntitySubclass;
import de.richtercloud.validation.tools.retriever.HierarchySubclass;
import de.richtercloud.validation.tools.retriever.TestEntity;
import de.richtercloud.validation.tools.retriever.HierarchySuperclass;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author richter
 */
public class CachedFieldRetrieverTest {

    @Test
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public void testRetrieveRelevantFields() throws NoSuchFieldException {
        Class<?> entityClass = TestEntity.class;
        CachedFieldRetriever instance = new CachedFieldRetriever();
        List<Field> expResult = new LinkedList<>(Arrays.asList(TestEntity.class.getDeclaredField("a"), TestEntity.class.getDeclaredField("m")));
        List<Field> result = instance.retrieveRelevantFields(entityClass);
        assertEquals(expResult, result);
        entityClass = TestEntitySubclass.class;
        expResult = new LinkedList<>(Arrays.asList(TestEntity.class.getDeclaredField("a"), TestEntity.class.getDeclaredField("m"), TestEntitySubclass.class.getDeclaredField("b")));
        result = instance.retrieveRelevantFields(entityClass);
        assertEquals(new HashSet<>(expResult), new HashSet<>(result)); //ReflectionFormBuilder doesn't give any guarantees about the order of returned fields
    }

    @Test
    public void testGenerateInheritanceHierarchy() {
        List<Class<?>> expResult = new LinkedList<>(Arrays.asList(HierarchySubclass.class,
                HierarchySuperclass.class));
        List<Class<?>> result = CachedFieldRetriever.generateInheritanceHierarchy(HierarchySubclass.class);
        assertEquals(expResult, result);
    }
}
