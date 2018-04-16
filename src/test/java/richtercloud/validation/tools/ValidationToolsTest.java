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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Path;
import javax.validation.Validation;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.WithNull;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matcher;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richtercloud.validation.tools.validator.Bean0;
import richtercloud.validation.tools.validator.Bean1;
import richtercloud.validation.tools.validator.Bean2;

/**
 *
 * @author richter
 */
public class ValidationToolsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationToolsTest.class);

    /**
     * Test of buildConstraintVioloationMessage method, of class ValidationTools.
     */
    @Property
    public void testBuildConstraintVioloationMessage(@ForAll boolean bean2Null,
            @ForAll int bean2Property0,
            @ForAll boolean bean1Null,
            @ForAll boolean bean1Bean2sNull,
            @ForAll boolean bean1bean2sEmpty,
            @ForAll @WithNull(0.5) String bean0Property0,
            @ForAll boolean bean0Valid,
            @ForAll boolean bean2Valid) {
        LOGGER.info("testBuildConstraintVioloationMessage");
        LOGGER.trace("testBuildConstraintVioloationMessage bean2Null: {}",
                bean2Null);
        LOGGER.trace("testBuildConstraintVioloationMessage bean2Property0: {}",
                bean2Property0);
        LOGGER.trace("testBuildConstraintVioloationMessage bean1Null: {}",
                bean1Null);
        LOGGER.trace("testBuildConstraintVioloationMessage bean1Bean2sNull: {}",
                bean1Bean2sNull);
        LOGGER.trace("testBuildConstraintVioloationMessage bean1bean2sEmpty: {}",
                bean1bean2sEmpty);
        LOGGER.trace("testBuildConstraintVioloationMessage bean0Property0: {}",
                bean0Property0);
        LOGGER.trace("testBuildConstraintVioloationMessage bean0Valid: {}",
                bean0Valid);
        LOGGER.trace("testBuildConstraintVioloationMessage bean2Valid: {}",
                bean2Valid);
        Bean2 bean2;
        if(bean2Null) {
            bean2 = null;
        }else {
            bean2 = new Bean2(bean2Property0);
        }
        Bean1 bean1;
        if(bean1Null) {
            bean1 = null;
        }else {
            List<Bean2> bean1Bean2s;
            if(bean1Bean2sNull) {
                bean1Bean2s = null;
            }else {
                //Bean1.bean2s has @Min(1)
                bean1Bean2s = new ArrayList<>();
                if(!bean1bean2sEmpty) {
                    bean1Bean2s.add(bean2);
                }
            }
            bean1 = new Bean1(bean1Bean2s);
        }
        Bean0 bean0 = new Bean0(bean1, bean0Property0);
        Set violations = Validation.buildDefaultValidatorFactory().getValidator().validate(bean0);
        FieldRetriever fieldRetriever = new CachedFieldRetriever();
        FieldNameLambda fieldNameLambda = field -> "+++"+field.getName()+",,,";
        Map<Path, String> pathDescriptionMap = new HashMap<>();
        OutputMode outputMode = OutputMode.PLAIN_TEXT;
        LOGGER.debug("validations.size: {}",
                violations.size());
        if(violations.isEmpty()) {
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> ValidationTools.buildConstraintVioloationMessage(violations,
                        bean0,
                        fieldRetriever,
                        pathDescriptionMap,
                        fieldNameLambda,
                        outputMode));
            return;
        }
        Matcher<String> expResult = equalTo("");
        if(bean1Null) {
            if(bean0Property0 == null) {
                expResult = anyOf(equalTo("The following constraints are violated:\n" +
                                "+++bean1,,,: darf nicht null sein\n" +
                                "+++property0,,,: darf nicht null sein\n" +
                                "Fix the corresponding values in the components."),
                        equalTo("The following constraints are violated:\n" +
                                "+++property0,,,: darf nicht null sein\n" +
                                "+++bean1,,,: darf nicht null sein\n" +
                                "Fix the corresponding values in the components."));
            }else {
                expResult = equalTo("The following constraints are violated:\n" +
                        "+++bean1,,,: darf nicht null sein\n" +
                        "Fix the corresponding values in the components.");
            }
        }else {
            if(bean0Property0 == null) {
                if(bean1Bean2sNull) {
                    expResult = equalTo("The following constraints are violated:\n" +
                            "+++property0,,,: darf nicht null sein\n" +
                            "Fix the corresponding values in the components.");
                        //bean1bean2sNull is valid
                }else {
                    if(!bean1bean2sEmpty) {
                        if(bean2Null) {
                            expResult = equalTo("The following constraints are violated:\n" +
                                    "+++property0,,,: darf nicht null sein\n" +
                                    "Fix the corresponding values in the components.");
                        }else {
                            if(bean2Property0 < 1) {
                                expResult = anyOf(equalTo("The following constraints are violated:\n" +
                                                "+++bean1,,,: +++bean2s,,,: +++property0,,,: muss größer oder gleich 1 sein\n" +
                                                "+++property0,,,: darf nicht null sein\n" +
                                                "Fix the corresponding values in the components."),
                                        equalTo("The following constraints are violated:\n" +
                                                "+++property0,,,: darf nicht null sein\n" +
                                                "+++bean1,,,: +++bean2s,,,: +++property0,,,: muss größer oder gleich 1 sein\n" +
                                                "Fix the corresponding values in the components."));
                            }else {
                                expResult = equalTo("The following constraints are violated:\n" +
                                        "+++property0,,,: darf nicht null sein\n" +
                                        "Fix the corresponding values in the components.");
                            }
                        }
                    }else {
                        expResult = anyOf(equalTo("The following constraints are violated:\n" +
                                        "+++property0,,,: darf nicht null sein\n" +
                                        "+++bean1,,,: +++bean2s,,,: Bean1.bean2s mustn't be empty\n" +
                                        "Fix the corresponding values in the components."),
                                equalTo("The following constraints are violated:\n" +
                                        "+++bean1,,,: +++bean2s,,,: Bean1.bean2s mustn't be empty\n" +
                                        "+++property0,,,: darf nicht null sein\n" +
                                        "Fix the corresponding values in the components."));
                    }
                }
            }else {
                if(bean1bean2sEmpty) {
                    expResult = equalTo("The following constraints are violated:\n" +
                                    "+++bean1,,,: +++bean2s,,,: Bean1.bean2s mustn't be empty\n" +
                                    "Fix the corresponding values in the components.");
                }else if(bean2Property0 < 1) {
                    expResult = equalTo("The following constraints are violated:\n" +
                                    "+++bean1,,,: +++bean2s,,,: +++property0,,,: muss größer oder gleich 1 sein\n" +
                                    "Fix the corresponding values in the components.");
                }
            }
        }
        String result = ValidationTools.buildConstraintVioloationMessage(violations,
                bean0,
                fieldRetriever,
                pathDescriptionMap,
                fieldNameLambda,
                outputMode);
        assertTrue(String.format("result was: %s",
                        result),
                expResult.matches(result));
    }

    @Test
    public void testBuildConstraintVioloationMessageExample() {
        LOGGER.info("testBuildConstraintVioloationMessageExample");
        testBuildConstraintVioloationMessage(false,
                -1848689372,
                false,
                false,
                false,
                " e6/H;",
                false,
                true);
    }
}
