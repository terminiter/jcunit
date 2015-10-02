package com.github.dakusui.jcunit.examples.fsm.methodoverloading;

import com.github.dakusui.jcunit.core.*;
import com.github.dakusui.jcunit.fsm.Expectation;
import com.github.dakusui.jcunit.fsm.FSMLevelsProvider;
import com.github.dakusui.jcunit.fsm.FSMUtils;
import com.github.dakusui.jcunit.fsm.Story;
import com.github.dakusui.jcunit.fsm.spec.ActionSpec;
import com.github.dakusui.jcunit.fsm.spec.FSMSpec;
import com.github.dakusui.jcunit.fsm.spec.ParametersSpec;
import com.github.dakusui.jcunit.fsm.spec.StateSpec;
import com.github.dakusui.jcunit.generators.RandomTupleGenerator;
import com.github.dakusui.jcunit.tests.generators.RandomTupleGeneratorTest;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JCUnit.class)
public class MethodOverloadingTest {
  public class MethodOverloading {
    public String m() {
      return "m()";
    }
    public String m(String s) {
      return "m(s)";
    }
    public String m_(String s, int i) {
      return "m(s,i)";
    }
    public String m_(int i) {
      return "m(i)";
    }
    public String m_(Object o) {
      return "m(o)";
    }
  }
  public enum MethodOverloadingSpec implements FSMSpec<MethodOverloading> {
    @StateSpec I {

    };
    @ParametersSpec
    public static final Object[][] m$s = new Object[][] {
        new Object[] { "hello" }
    };

    @ActionSpec(parametersSpec = "m$s")
    public Expectation<MethodOverloading> m(Expectation.Builder<MethodOverloading> b, String s) {
      return b.valid(I, CoreMatchers.equalTo("m(s)")).build();
    }

    @ActionSpec
    public Expectation<MethodOverloading> m(Expectation.Builder<MethodOverloading> b) {
      return b.valid(I, CoreMatchers.equalTo("m()")).build();
    }

    @Override
    public boolean check(MethodOverloading methodOverloading) {
      return true;
    }
  }

  @FactorField(levelsProvider = FSMLevelsProvider.class)
  public Story<MethodOverloading, MethodOverloadingSpec> primary;

  @Test
  public void test() {
    FSMUtils.performStory(this, "primary", new MethodOverloading());
  }
}