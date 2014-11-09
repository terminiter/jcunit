package com.github.dakusui.jcunit.experimentals.fsm.test;

import com.github.dakusui.jcunit.core.FactorField;
import com.github.dakusui.jcunit.core.Param;
import com.github.dakusui.jcunit.experimentals.fsm.ScenarioProvider;
import com.github.dakusui.jcunit.experimentals.fsm.ScenarioSequence;
import com.github.dakusui.jcunit.experimentals.fsm.sut.FlyingSpaghettiMonster;

public class FlyingSpaghettiTest {
  FlyingSpaghettiMonster sut;

  @FactorField(
      levelsProvider = ScenarioProvider.class,
      providerParams = {
          @Param("com.github.dakusui.jcunit.generators.IPO2TupleGeneratorBase"),
          @Param("2")
      }
  )
  public ScenarioSequence<FlyingSpaghettiMonster> scenarioSequence;
}
