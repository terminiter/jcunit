package com.github.dakusui.jcunit.tests.core;

import com.github.dakusui.jcunit.core.tuples.Tuple;
import com.github.dakusui.jcunit.ututils.UTUtils;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FactorsTest {
  @Test
  public void generateAllPossibleTuples() {
    assertThat(
        new HashSet<Tuple>(
            UTUtils.defaultFactors.generateAllPossibleTuples(2)
        ),
        is(new HashSet<Tuple>(asList(new Tuple[]{
            UTUtils.tupleBuilder().put("A", "a1").put("B", "b1").build(),
            UTUtils.tupleBuilder().put("A", "a1").put("B", "b2").build(),
            UTUtils.tupleBuilder().put("A", "a2").put("B", "b1").build(),
            UTUtils.tupleBuilder().put("A", "a2").put("B", "b2").build()
        })))
    );
  }
}