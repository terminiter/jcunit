package com.github.dakusui.jcunit.examples.calc;

import com.github.dakusui.jcunit.annotations.FactorField;
import com.github.dakusui.jcunit.annotations.Generator;
import com.github.dakusui.jcunit.annotations.Param;
import com.github.dakusui.jcunit.annotations.TupleGeneration;
import com.github.dakusui.jcunit.core.*;
import com.github.dakusui.jcunit.core.factor.SimpleLevelsProvider;
import com.github.dakusui.jcunit.core.rules.JCUnitDesc;
import com.github.dakusui.jcunit.generators.IPO2TupleGenerator;
import com.github.dakusui.jcunit.ututils.Metatest;
import com.github.dakusui.jcunit.ututils.UTUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JCUnit.class)
public class NestedFieldExample extends Metatest {
  public NestedFieldExample() {
    super(
        31, // tests,
        0,  // failed
        0   // ignored
    );
  }

  @TupleGeneration(
      generator = @Generator(
          value = IPO2TupleGenerator.class,
          params = @Param("2")
      ))
  public static class Struct {
    @FactorField(intLevels = { 123, 456 })
    public int    a;
    @FactorField(stringLevels = { "A", "B", "C" })
    public String b;

    public String toString() {
      return a + ";" + b;
    }
  }

  @Rule
  public JCUnitDesc testDesc = new JCUnitDesc();

  @FactorField(levelsProvider = LevelsProvider$f1.class)
  public int     f1;
  public static class LevelsProvider$f1 extends SimpleLevelsProvider {
    @Override
    protected Object[] values() {
      return new Object[] { 1, 2, 3 };
    }
  }
  @FactorField(levelsProvider = LevelsProvider$f2.class)
  public long    f2;
  public static class LevelsProvider$f2 extends SimpleLevelsProvider {
    @Override
    protected Object[] values() {
      return new Object[] { 1L, 2L, 3L };
    }
  }
  @FactorField
  public Calc.Op op;

  @SuppressWarnings("unused") // This field is used by JCUnit.
  @FactorField
  public Struct struct;

  @Test
  public void test() throws Exception {
    Calc calc = new Calc();
    String result = "(N/A)";
    try {
      result = Integer.toString(calc.calc(this.op, f1, (int) f2));
    } finally {
      UTUtils.stdout().println(
          Utils.format(
              "testname=%s: f1=%d, op=%s, f2=%d = %s ; struct = (%s)",
              this.testDesc.getTestName(),
              f1,
              op,
              f2,
              result,
              struct));
    }
  }
}
