package com.github.dakusui.jcunit.tests.examples.fsm.inputhistory;


import com.github.dakusui.jcunit.testutils.Metatest;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class NaiveInteractionHistoryExampleTest {
  public static class AllPassing extends Metatest {
    public AllPassing() {
      super(com.github.dakusui.jcunit.examples.inputhistory.AllPassing.class, 19, 0, 0);
    }

    @Test
    public void testPassing() {
      runTests();
    }
  }
  public static class Failing extends Metatest {
    public Failing() {
      super(com.github.dakusui.jcunit.examples.inputhistory.Failing.class, 19, 13, 0);
    }

    @Test
    public void testFailing() {
      runTests();
    }
  }
}
