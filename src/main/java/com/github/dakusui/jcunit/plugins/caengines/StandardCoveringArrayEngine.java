package com.github.dakusui.jcunit.plugins.caengines;

import com.github.dakusui.jcunit.core.factor.Factors;
import com.github.dakusui.jcunit.core.tuples.Tuple;
import com.github.dakusui.jcunit.core.utils.Checks;
import com.github.dakusui.jcunit.plugins.constraints.ConstraintBundle;

import java.util.List;

public class StandardCoveringArrayEngine extends CoveringArrayEngine.Base {
  private final int strength;

  public StandardCoveringArrayEngine(
      @Param(source = Param.Source.CONFIG, defaultValue = "2") int strength) {
    this.strength = strength;
  }

  @Override
  protected List<Tuple> generate(Factors factors, ConstraintBundle constraintBundle) {
    Checks.checknotnull(factors);
    Checks.checknotnull(constraintBundle);
    if (factors.size() < 2 || this.strength < 2) {
      return new SimpleCoveringArrayEngine().generate(factors, constraintBundle);
    }
    return new Ipo2CoveringArrayEngine(this.strength).generate(factors, constraintBundle);
  }
}
