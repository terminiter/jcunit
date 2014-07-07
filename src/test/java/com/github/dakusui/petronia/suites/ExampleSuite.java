package com.github.dakusui.petronia.suites;

import com.github.dakusui.petronia.examples.Example;
import com.github.dakusui.petronia.examples.MethodFinderTest;
import com.github.dakusui.petronia.examples.TestArrayGeneratorsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ Example.class, MethodFinderTest.MethodFinderTest1.class,
    MethodFinderTest.MethodFinderTest2.class, TestArrayGeneratorsTest.class })
public class ExampleSuite {
}
