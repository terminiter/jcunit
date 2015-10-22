package com.github.dakusui.jcunit.tests.factor;

import com.github.dakusui.jcunit.annotations.FactorField;
import com.github.dakusui.jcunit.core.JCUnit;
import com.github.dakusui.jcunit.core.factor.SimpleLevelsProvider;
import com.github.dakusui.jcunit.ututils.Metatest;
import com.github.dakusui.jcunit.ututils.UTUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

public class ArrayFactorLevelTest {
  @Test
  public void testNormal() {
    new Normal().runTests();
  }

  @Test
  public void testDuplicated() {
    new Duplicated().runTests();
  }


  @RunWith(JCUnit.class)
  public static class Normal extends Metatest {
    @FactorField(intLevels = { 1 })
    public int test;

    @FactorField(levelsProvider = LevelsProvider$factor1.class, providerParams = {})
    public String[] factor1;

    public Normal() {
      super(2, 0, 0);
    }

    public static class LevelsProvider$factor1 extends SimpleLevelsProvider {
      @Override
      protected Object[] values() {
        return new String[][] {
            new String[] { "Hello", "world" },
            new String[] { "Howdy" }
        };
      }
    }

    @Before
    public void before() {
      UTUtils.configureStdIOs();
    }

    @org.junit.Test
    public void test() {
      UTUtils.stdout().println(Arrays.toString(factor1));
    }
  }

  @RunWith(JCUnit.class)
  public static class Duplicated extends Metatest {
    @FactorField(intLevels = { 1 })
    public int test;

    @FactorField(levelsProvider = LevelsProvider$factor1.class, providerParams = {})
    public String[] factor1;

    public static class LevelsProvider$factor1 extends SimpleLevelsProvider {
      @Override
      protected Object[] values() {
        return new String[][] {
            new String[] { "Hello", "world" },
            new String[] { "Hello", "world" },
            new String[] { "Howdy" }
        };
      }
    }

    public Duplicated() {
      super(3, 0, 0);
    }

    @Before
    public void before() {
      UTUtils.configureStdIOs();
    }

    @Test
    public void test() {
      UTUtils.stdout().println(Arrays.toString(factor1));
    }
  }
}
