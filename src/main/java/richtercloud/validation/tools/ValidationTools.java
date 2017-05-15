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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;

/**
 *
 * @author richter
 */
public class ValidationTools {

    /**
     * Builds a useful message from multiple constraint violations
     * @param violations the detected constraint violations to build the message
     * from
     * @param instance the instance which causes the constaint violation(s)
     * @param fieldRetriever the field retriever to use to enhance the message
     * with field information
     * @param html whether or not to include HTML (e.g. for multiline Swing
     * labels) in the return value
     * @return the built message
     */
    /*
    internal implementation notes:
    - Set<ConstraintViolation<?>> must not be further parameterized because
    ConstraintViolationException.getConstraintVioloations doesn't allow
    parameterization and returns Set<ConstraintViolation<?>>
    */
    public static String buildConstraintVioloationMessage(Set<ConstraintViolation<?>> violations,
            Object instance,
            FieldRetriever fieldRetriever,
            FieldNameLambda fieldNameLambda,
            boolean html) {
        StringBuilder messageBuilder = new StringBuilder(1000);
        if(html) {
            messageBuilder.append("<html>");
        }
        messageBuilder.append("The following constraints are violated:");
        if(html) {
            messageBuilder.append("<br/>");
        }else {
            messageBuilder.append("\n");
        }
        for(ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            List<String> propertyPathSplit = new LinkedList<>(Arrays.asList(propertyPath.split("\\."))); //an empty string causes a list with "" to be returned by "".split("\\."") (not necessarily intuitive)
            String fieldName = propertyPath;
            //there's no way to retrieve information about the field or
            //class on which the validation annotation has been specified
            //-> retrieval of this information is done with the property
            //path of the ConstraintViolation

            //violations which occur at the class level of the root instance
            //have an empty property path -> just display the message in a
            //separate line
            //note: both violations which occur at the root and at
            //a property have a property path with 1 node (which doesn't
            //necessarily make sense) -> there's no way to evaluate retrieve
            //the field names if a nested violation occurs -> split
            //propertyPath.toString() at `.`.
            if(!propertyPath.isEmpty()) {
                if(propertyPathSplit.size() > 1) {
                    throw new IllegalArgumentException("Property path of violation is larger than 2 nodes. This isn't supported yet.");
                }
                if(!propertyPathSplit.isEmpty()) {
                    //should be always true because it's already checked
                    //that propertyPath.toString isn't empty, but check
                    //nevertheless
                    String violationFieldName = propertyPathSplit.get(0);
                    Field violationField = null;
                    List<Field> classFields = fieldRetriever.retrieveRelevantFields(instance.getClass());
                    for(Field classField : classFields) {
                        if(classField.getName().equals(violationFieldName)) {
                            violationField = classField;
                            break;
                        }
                    }
                    if(violationField == null) {
                        throw new IllegalArgumentException("validation violoation constraint on field which isn't part of the validated instance");
                    }
                    String specialFieldName = fieldNameLambda.getFieldName(violationField);
                    if(specialFieldName != null) {
                        fieldName = specialFieldName;
                    }
                    messageBuilder.append(fieldName);
                    messageBuilder.append(": ");
                }
            }
            messageBuilder.append(violation.getMessage());
            if(html) {
                messageBuilder.append("<br/>");
            }else {
                messageBuilder.append("\n");
            }
        }
        messageBuilder.append("Fix the corresponding values in the components.");
        if(html) {
            messageBuilder.append("</html>");
        }
        String message = messageBuilder.toString();
        return message;
    }

    private ValidationTools() {
    }
}
