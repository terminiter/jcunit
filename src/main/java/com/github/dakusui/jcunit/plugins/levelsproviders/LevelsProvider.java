package com.github.dakusui.jcunit.plugins.levelsproviders;

import com.github.dakusui.jcunit.core.utils.BaseBuilder;
import com.github.dakusui.jcunit.core.utils.Checks;
import com.github.dakusui.jcunit.plugins.Plugin;
import com.github.dakusui.jcunit.runners.core.RunnerContext;
import com.github.dakusui.jcunit.runners.standard.annotations.FactorField;
import com.github.dakusui.jcunit.runners.standard.annotations.Value;

import java.util.Arrays;

/**
 * Implementations of this class must have a public constructor without any parameters.
 */
public interface LevelsProvider extends Plugin {
  /**
   * Returns a number of levels generated by this object.
   */
  int size();

  /**
   * Returns {@code n}-th level of this factor.
   */
  Object get(int n);

  /**
   * A model class to be used to implement a {@code LevelsProvider}.
   */
  abstract class Base extends Plugin.Base implements LevelsProvider {
    public Base() {
    }
  }

  class FromFactorField implements BaseBuilder<LevelsProvider> {
    private final FactorField                  factorField;
    private final RunnerContext                runnerContext;
    private final Plugin.Param.Resolver<Value> resolver;

    public FromFactorField(FactorField factorField, RunnerContext runnerContext) {
      this.factorField = Checks.checknotnull(factorField);
      this.runnerContext = Checks.checknotnull(runnerContext);
      this.resolver = new Value.Resolver();
    }

    @SuppressWarnings("unchecked")
    @Override
    public LevelsProvider build() {
      Plugin.Factory<LevelsProvider, Value> factory = new Plugin.Factory<LevelsProvider, Value>(
          (Class<LevelsProvider>) this.factorField.levelsProvider(),
          this.resolver,
          this.runnerContext
      );
      //noinspection ConstantConditions
      return factory.create(Arrays.asList(this.factorField.args()));
    }
  }
}