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

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Path.Node;

/**
 *
 * @author richter
 */
public final class ValidationTools {

    /**
     * Builds a useful message from multiple constraint violations
     * @param violations the detected constraint violations to build the message
     *     from
     * @param instance the instance which causes the constaint violation(s)
     * @param fieldRetriever the field retriever to use to enhance the message
     *     with field information
     * @param pathDescriptionMap allows to replace the description constructed
     *     from paths with customized messages
     * @param fieldNameLambda allows to replace field names which are used in
     *     construction of the description with specified strings (has no effect
     *     if a description is found in {@code pathDescriptionMap})
     * @param skipPathes if {@code true} all pathes are omitted and violation
     *     messages only are written in a new line
     * @param outputMode how to generate the output (see {@link OutputMode} for
     *     details
     * @return the built message
     * @throws IllegalArgumentException if either of {@code violations},
     *     {@code fieldRetriever}, {@code pathDescriptionMap},
     *     {@code fieldNameLambda} or {@code outputMode} is {@code null} or
     *     {@code violations} is empty
     */
    /*
    internal implementation notes:
    - Set<ConstraintViolation<?>> must not be further parameterized because
    ConstraintViolationException.getConstraintVioloations doesn't allow
    parameterization and returns Set<ConstraintViolation<?>>
    - skipping of prepending of pathes could be expressed by
    `pathDescriptionMap == null`, but the use cases are just too different in
    order to be expressed by the same variable
    */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.AvoidDeeplyNestedIfStmts"
        })
    public static <T> String buildConstraintVioloationMessage(Set<ConstraintViolation<T>> violations,
            Object instance,
            FieldRetriever fieldRetriever,
            Map<Path, String> pathDescriptionMap,
            FieldNameLambda fieldNameLambda,
            boolean skipPathes,
            OutputMode outputMode) {
        if(violations == null) {
            throw new IllegalArgumentException("violations mustn't be null");
        }
        if(violations.isEmpty()) {
            throw new IllegalArgumentException("violations mustn't be empty");
        }
        if(fieldRetriever == null) {
            throw new IllegalArgumentException("fieldRetriever mustn't be null");
        }
        if(pathDescriptionMap == null) {
            throw new IllegalArgumentException("pathDescriptionMap mustn't be null");
        }
        if(fieldNameLambda == null) {
            throw new IllegalArgumentException("fieldNameLambda mustn't be null");
        }
        if(outputMode == null) {
            throw new IllegalArgumentException("outputMode mustn't be null");
        }
        StringBuilder messageBuilder = new StringBuilder(1000);
        if(outputMode == OutputMode.HTML_HTML) {
            messageBuilder.append("<html>");
        }else if(outputMode == OutputMode.HTML_DIV) {
            messageBuilder.append("<div>");
        }
        messageBuilder.append("The following constraints are violated:");
        if(outputMode == OutputMode.HTML_HTML
                || outputMode == OutputMode.HTML_DIV) {
            messageBuilder.append("<br/>");
        }else {
            messageBuilder.append('\n');
        }
        String pathString;
        for(ConstraintViolation<?> violation : violations) {
            if(!skipPathes) {
                pathString = pathDescriptionMap.get(violation.getPropertyPath());
                if(pathString == null) {
                    StringBuilder pathStringBuilder = new StringBuilder(1024);
                    Class<?> relativeFieldRoot = instance.getClass();
                    List<Path.Node> propertyPathNodes = Lists.newArrayList(violation.getPropertyPath());
                        //need a second view on property path because Node.isInIterable
                        //is only true after descending into the collection
                    int index = 0;
                    for(Node propertyPathNode : violation.getPropertyPath()) {
                        if(propertyPathNode.getKind() != ElementKind.PROPERTY
                                && propertyPathNode.getKind() != ElementKind.BEAN) {
                            throw new IllegalArgumentException(String.format("only "
                                    + "kinds %s and %s are supported",
                                    ElementKind.PROPERTY,
                                    ElementKind.BEAN));
                        }
                        //both PropertyNode and BeanNode (subclasses of Node) don't
                        //provide more information than Node
                        Field violationField = null;
                        if(propertyPathNode.getKind() == ElementKind.PROPERTY) {
                            String violationFieldName = propertyPathNode.getName();
                            List<Field> classFields = fieldRetriever.retrieveRelevantFields(relativeFieldRoot);
                            for(Field classField : classFields) {
                                if(classField.getName().equals(violationFieldName)) {
                                    violationField = classField;
                                    break;
                                }
                            }
                            if(violationField == null) {
                                throw new IllegalArgumentException("validation violoation constraint on field which isn't part of the validated instance");
                            }
                            if(index+1 < propertyPathNodes.size()) {
                                if(!propertyPathNodes.get(index+1).isInIterable()) {
                                    relativeFieldRoot = violationField.getType();
                                }else {
                                    //this can be handled well with Validation API 2.x which
                                    //requires Java EE 8 (assuming running a Java EE
                                    //environment) which is painful to setup on most
                                    //Java EE 8 servers
                                    if(!(violationField.getGenericType() instanceof ParameterizedType)) {
                                        throw new IllegalArgumentException("all collections involving validation need to be parameterized");
                                    }
                                    ParameterizedType violationFieldParameterizedType = (ParameterizedType) violationField.getGenericType();
                                    if(violationFieldParameterizedType.getActualTypeArguments().length != 1) {
                                        throw new IllegalArgumentException("only collections with one parameterized type are supported");
                                    }
                                    Type violoationFieldOnlyGenericType = violationFieldParameterizedType.getActualTypeArguments()[0];
                                    if(!(violoationFieldOnlyGenericType instanceof Class)) {
                                        throw new IllegalArgumentException(String.format(
                                                "the collection's parameterized type has "
                                                        + "to be a class (as opposed to "
                                                        + "other possibilites for generic "
                                                        + "types) (was %s)",
                                                violoationFieldOnlyGenericType));
                                    }
                                    relativeFieldRoot = (Class<?>) violoationFieldOnlyGenericType;
                                }
                            }
                            String specialFieldName = fieldNameLambda.getFieldName(violationField);
                            String fieldName;
                            if(specialFieldName != null) {
                                fieldName = specialFieldName;
                            }else {
                                fieldName = violationField.getName();
                            }
                            pathStringBuilder.append(fieldName);
                            pathStringBuilder.append(": ");
                                //adding : between property names is fine and makes
                                //descriptions appear nicer than when separated with .
                            index += 1;
                        }
                        //in case propertyPath.kind  == ElementKind.BEAN
                        //do nothing since it's fine if either the leaf is a
                        //leaf node with all previous pathes explaining the path
                        //or the violoation refers to the root bean in which
                        //case a message without path would be displayed which
                        //is very comprehensive as well
                    }
                    pathString = pathStringBuilder.toString();
                }
                messageBuilder.append(pathString);
            }
            messageBuilder.append(violation.getMessage());
            if(outputMode == OutputMode.HTML_HTML
                    || outputMode == OutputMode.HTML_DIV) {
                messageBuilder.append("<br/>");
            }else {
                messageBuilder.append('\n');
            }
        }
        messageBuilder.append("Fix the corresponding values in the components.");
        if(outputMode == OutputMode.HTML_HTML) {
            messageBuilder.append("</html>");
        }else if(outputMode == OutputMode.HTML_DIV) {
            messageBuilder.append("</div>");
        }
        return messageBuilder.toString();
    }

    public static <T> String buildConstraintVioloationMessage(Set<ConstraintViolation<T>> violations,
            Object instance,
            FieldRetriever fieldRetriever,
            FieldNameLambda fieldNameLambda,
            boolean skipPathes,
            OutputMode outputMode) {
        return buildConstraintVioloationMessage(violations,
                instance,
                fieldRetriever,
                new HashMap<>(), //pathDescriptionMap
                fieldNameLambda,
                skipPathes, //skipPathes
                outputMode);
    }

    public static <T> String buildConstraintVioloationMessage(Set<ConstraintViolation<T>> violations,
            Object instance,
            FieldRetriever fieldRetriever,
            FieldNameLambda fieldNameLambda,
            OutputMode outputMode) {
        return buildConstraintVioloationMessage(violations,
                instance,
                fieldRetriever,
                new HashMap<>(), //pathDescriptionMap
                fieldNameLambda,
                false, //skipPathes
                outputMode);
    }

    public static <T> String buildConstraintVioloationMessage(Set<ConstraintViolation<T>> violations,
            Object instance,
            FieldRetriever fieldRetriever,
            Map<Path, String> pathDescriptionMap,
            OutputMode outputMode) {
        return buildConstraintVioloationMessage(violations,
                instance,
                fieldRetriever,
                pathDescriptionMap,
            field -> field.getName(),
                false, //skipPathes
                outputMode);
    }

    public static <T> String buildConstraintVioloationMessage(Set<ConstraintViolation<T>> violations,
            Object instance,
            FieldRetriever fieldRetriever,
            OutputMode outputMode) {
        return buildConstraintVioloationMessage(violations,
                instance,
                fieldRetriever,
                new HashMap<>(), //pathDescriptionMap
            field -> field.getName(),
                false, //skipPathes
                outputMode);
    }

    private ValidationTools() {
    }
}
