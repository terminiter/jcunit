package com.github.dakusui.jcunit.fsm;

import com.github.dakusui.jcunit.core.Checks;
import com.github.dakusui.jcunit.core.tuples.Tuple;

/**
 * An interface that represents a sequence of scenarios.
 *
 * @param <SUT> A software (class) under test.
 */
public interface ScenarioSequence<SUT> {
  abstract class Base<SUT> implements ScenarioSequence<SUT> {
    @Override
    public <T> void perform(T context, Type type, SUT sut, Story.Observer observer) {
      Checks.checknotnull(observer);
      observer.startSequence(type, this);
      try {
        for (int i = 0; i < this.size(); i++) {
          Scenario<SUT> each = this.get(i);
          Expectation.Result result = null;
          observer.run(type, each, sut);
          boolean passed = false;
          try {
            if (i == 0) {
              if (!each.given.check(sut)) {
                throw new Expectation.Result.Builder("Precondition was not satisfied.").addFailedReason(String.format("SUT(%s) isn't in state '%s'", sut, each.given)).build();
              };
            }
            Object r = each.perform(context, sut);
            passed = true;
            ////
            // each.perform(sut) didn't throw an exception
            //noinspection unchecked,ThrowableResultOfMethodCallIgnored
            result = each.then().checkReturnedValue(context, sut, r, observer);
          } catch (Expectation.Result r) {
            result = r;
          } catch (Throwable t) {
            if (!passed) {
              //noinspection unchecked,ThrowableResultOfMethodCallIgnored
              result = each.then().checkThrownException(context, sut, t, observer);
            } else {
              throw new RuntimeException("Expectation#checkReturnedValue threw an exception. This is considered to be a framework side's bug.", t);
            }
          } finally {
            if (result != null) {
              if (result.isSuccessful())
                observer.passed(type, each, sut);
              else
                observer.failed(type, each, sut, result);
              result.throwIfFailed();
            }
          }
        }
      } finally {
        observer.endSequence(type, this);
      }
    }

    @Override
    public Scenario<SUT> get(int i) {
      Checks.checkcond(i >= 0);
      Checks.checkcond(i < this.size());
      State<SUT> given = this.state(i);
      Action<SUT> when = this.action(i);
      Args with = this.args(i);
      return new Scenario<SUT>(given, when, with);
    }

    @Override
    public String toString() {
      return FSMUtils.toString(this);
    }
  }

  ScenarioSequence<?> EMPTY = new ScenarioSequence() {
    @Override
    public void perform(Object context, Type type, Object sut, Story.Observer observer) {
      // Does nothing since this is an emptry scenario object.
      observer.skipSequence(type, this);
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public Scenario<?> get(int i) {
      throw new IllegalStateException();
    }

    @Override
    public State<?> state(int i) {
      throw new IllegalStateException();
    }

    @Override
    public Action<?> action(int i) {
      throw new IllegalStateException();
    }

    @Override
    public Object arg(int i, int j) {
      throw new IllegalStateException();
    }

    @Override
    public boolean hasArg(int i, int j) {
      throw new IllegalStateException();
    }

    @Override
    public Args args(int i) {
      throw new IllegalStateException();
    }

    @Override
    public String toString() {
      return "Empty Story:[]";
    }
  };

  /**
   * Performs this scenario with given {@code sut} object.
   *
   * @param sut An objects that represents software under test.
   */
  <T> void perform(T context, Type type, SUT sut, Story.Observer observer);

  /**
   * Returns the number of scenarios in this sequence
   *
   */
  int size();

  /**
   * Returns the {@code i}-th scenario in this sequence.
   *
   * @param i history index
   */
  Scenario<SUT> get(int i);

  /**
   * Returns the {@code i}-th state in this sequence.
   * Since {@code null} isn't allowed as a level for state factors, you can tell if the corresponding
   * factor already has a value or not by simply checking this method returns non-null.
   *
   * @param i history index
   */
  State<SUT> state(int i);

  /**
   * Returns the {@code i}-th action in this sequence.
   * Since {@code null} isn't allowed as a level for action factors, you can tell if the corresponding
   * factor already has a value or not by simply checking this method returns non-null.
   *
   * @param i history index
   */
  Action<SUT> action(int i);

  /**
   * Returns {@code j}-th element of {@code i}-th argument list.
   *
   * @param i history index
   * @param j index for argument
   */
  Object arg(int i, int j);

  /**
   * Checks if {@code i}-th argument list has the {@code i}-th element.
   *
   * @param i history index
   * @param j index for argument
   */
  boolean hasArg(int i, int j);

  /**
   * Returns arguments object of {@code i}-th action.
   *
   * @param i history index
   */
  Args args(int i);

  enum Type {
    setUp,
    main,
    optional
  }

  /**
   * Builds a {@code Story} object from a {@code Tuple} using  a given {@code FSMFactorbs}.
   *
   * @param <SUT> A class of software under test.
   */
  class BuilderFromTuple<SUT> {
    private FSMFactors factors;
    private Tuple      tuple;
    private String     fsmName;

    public BuilderFromTuple() {
    }

    public BuilderFromTuple<SUT> setFSMFactors(FSMFactors factors) {
      this.factors = factors;
      return this;
    }

    public BuilderFromTuple<SUT> setTuple(Tuple tuple) {
      this.tuple = tuple;
      return this;
    }

    public BuilderFromTuple<SUT> setFSMName(String fsmName) {
      this.fsmName = fsmName;
      return this;
    }

    public ScenarioSequence<SUT> build() {
      Checks.checknotnull(tuple);
      Checks.checknotnull(factors);
      Checks.checknotnull(fsmName);
      Checks.checkcond(factors.historyLength(fsmName) > 0);
      return new ScenarioSequence.Base<SUT>() {
        @Override
        public Scenario<SUT> get(int i) {
          Checks.checkcond(i >= 0);
          Checks.checkcond(i < this.size());
          State<SUT> given = this.state(i);
          Action<SUT> when = this.action(i);
          Args with = this.args(i);
          return new Scenario<SUT>(given, when, with);
        }

        @Override
        public State<SUT> state(int i) {
          Checks.checkcond(i >= 0);
          Checks.checkcond(i < this.size());
          //noinspection unchecked
          return (State<SUT>) tuple.get(factors.stateFactorName(fsmName, i));
        }

        @Override
        public Action<SUT> action(int i) {
          Checks.checkcond(i >= 0);
          Checks.checkcond(i < this.size());
          //noinspection unchecked
          return (Action<SUT>) tuple.get(factors.actionFactorName(fsmName, i));
        }

        @Override
        public Object arg(int i, int j) {
          Checks.checkcond(i >= 0);
          Checks.checkcond(i < this.size());
          Checks.checkcond(j >= 0);
          Checks.checkcond(j < action(i).numParameterFactors());
          return tuple.get(factors.paramFactorName(fsmName, i, j));
        }

        @Override
        public boolean hasArg(int i, int j) {
          Checks.checkcond(i >= 0);
          Checks.checkcond(i < this.size());
          Checks.checkcond(j >= 0);
          Checks.checkcond(j < action(i).numParameterFactors());
          return tuple.containsKey(factors.paramFactorName(fsmName, i, j));
        }

        @Override
        public Args args(int i) {
          Checks.checkcond(i >= 0);
          Checks.checkcond(i < this.size());
          Object[] values = new Object[action(i).numParameterFactors()];
          for (int j = 0; j < values.length; j++) {
            values[j] = tuple.get(factors.paramFactorName(fsmName, i, j));
          }
          return new Args(values);
        }

        @Override
        public int size() {
          return factors.historyLength(fsmName);
        }

        @Override
        public String toString() {
          return FSMUtils.toString(this);
        }
      };
    }
  }
}
