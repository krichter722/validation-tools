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
package richtercloud.validation.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import richtercloud.validation.tools.annotations.Skip;

/**
 * A thread-safe implementation of {@link FieldRetriever} caching previously
 * requested results.
 *
 * @author richter
 */
public class CachedFieldRetriever implements FieldRetriever {
    /**
     * A cache for return values of {@link #retrieveRelevantFields(java.lang.Class)
     * }.
     */
    private Map<Class<?>, List<Field>> relevantFieldsCache = new HashMap<>();
    private Semaphore semaphore = new Semaphore(1);

    /**
     * Recursively retrieves all fields from the inheritance hierachy of
     * {@code entityClass}, except {@code static} and {@code transient} fields
     * and those annotated with {@link Skip}.
     *
     * Results are cached in order to ensure that future calls return the same
     * result for the same argument value.
     *
     * @param clazz
     * @return
     */
    @Override
    public List<Field> retrieveRelevantFields(Class<?> clazz) throws FieldRetrievalException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz mustn't be null");
        }
        List<Field> retValue = new LinkedList<>();
        try {
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                throw new FieldRetrievalException(ex);
            }
            List<Field> retValueCandidate = this.relevantFieldsCache.get(clazz);
            if (retValueCandidate != null) {
                return new LinkedList<>(retValueCandidate); //return a copy in order to avoid ConcurrentModificationException
            }
            Class<?> hierarchyPointer = clazz;
            while (hierarchyPointer != null //Class.getSuperclass returns null for topmost interface
                    && !hierarchyPointer.equals(Object.class)) {
                retValue.addAll(Arrays.asList(hierarchyPointer.getDeclaredFields()));
                hierarchyPointer = hierarchyPointer.getSuperclass();
            }
            Set<Field> seenEntityClassFields = new HashSet<>();
            ListIterator<Field> entityClassFieldsIt = retValue.listIterator();
            while (entityClassFieldsIt.hasNext()) {
                Field entityClassFieldsNxt = entityClassFieldsIt.next();
                if (Modifier.isStatic(entityClassFieldsNxt.getModifiers())) {
                    entityClassFieldsIt.remove();
                    continue;
                }
                if (Modifier.isTransient(entityClassFieldsNxt.getModifiers())) {
                    entityClassFieldsIt.remove();
                    continue;
                }
                if (seenEntityClassFields.contains(entityClassFieldsNxt)) {
                    entityClassFieldsIt.remove();
                    continue;
                }
                Skip entityClassFieldNxtSkip = entityClassFieldsNxt.getAnnotation(Skip.class);
                if(entityClassFieldNxtSkip != null) {
                    entityClassFieldsIt.remove();
                    continue;
                }
                seenEntityClassFields.add(entityClassFieldsNxt);
                entityClassFieldsNxt.setAccessible(true);
            }
            this.relevantFieldsCache.put(clazz, retValue);
        }finally {
            semaphore.release();
        }
        return retValue;
    }
}
