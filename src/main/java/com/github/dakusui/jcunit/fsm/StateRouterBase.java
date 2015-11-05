package com.github.dakusui.jcunit.fsm;

import com.github.dakusui.jcunit.core.Checks;
import com.github.dakusui.jcunit.core.Utils;

import java.util.*;

/**
 * Routes to a given state.
 *
 * @param <SUT> Software under test.
 */
public class StateRouterBase<SUT> implements StateRouter<SUT> {
  private final FSM<SUT>                               fsm;
  private final List<State<SUT>>                       destinations;
  private final Map<State<SUT>, ScenarioSequence<SUT>> routes;
  private final EdgeLister<SUT>                        lister;

  public StateRouterBase(FSM<SUT> fsm, EdgeLister lister) {
    Checks.checknotnull(fsm);
    Checks.checknotnull(lister);
    this.destinations = Collections.unmodifiableList(Utils.dedup(fsm.states()));
    this.routes = new LinkedHashMap<State<SUT>, ScenarioSequence<SUT>>();
    this.lister = lister;
    for (State<SUT> each : destinations) {
      if (each.equals(fsm.initialState())) {
        //noinspection unchecked
        this.routes.put(each, (ScenarioSequence<SUT>) ScenarioSequence.EMPTY);
      } else {
        this.routes.put(each, null);
      }
    }
    this.fsm = fsm;
    traverse(fsm.initialState(), new LinkedList<Edge<SUT>>(), new LinkedHashSet<State<SUT>>());
    List<State<SUT>> unreachableDestinations = new ArrayList<State<SUT>>(this.destinations.size());
    for (State<SUT> each : this.destinations) {
      if (this.routes.get(each) == null) {
        unreachableDestinations.add(each);
      }
    }
    Checks.checktest(
        unreachableDestinations.size() == 0,
        "The states '%s' can't be reached from the initial state of the given FSM.",
        unreachableDestinations,
        this.fsm.initialState()
    );
  }

  @Override
  public ScenarioSequence<SUT> routeTo(State<SUT> state) {
    Checks.checkcond(this.destinations.contains(state));
    return this.routes.get(state);
  }

  private void traverse(State<SUT> state, List<Edge<SUT>> path, Set<State<SUT>> visited) {
    for (Edge<SUT> each : lister.possibleEdgesFrom(state)) {
      State<SUT> next = next(state, each);
      if (next == State.Void.getInstance())
        return;
      if (visited.contains(next))
        continue;
      visited.add(next);
      List<Edge<SUT>> pathToNext = new LinkedList<Edge<SUT>>(path);
      pathToNext.add(each);

      if (this.destinations.contains(next)) {
        this.routes.put(next, buildScenarioSequenceFromTransitions(pathToNext));
      }
      traverse(next, pathToNext, visited);
    }
  }

  private ScenarioSequence<SUT> buildScenarioSequenceFromTransitions(final List<Edge<SUT>> pathToNext) {
    return new ScenarioSequence.Base<SUT>() {
      @Override
      public int size() {
        return pathToNext.size();
      }

      @Override
      public State<SUT> state(int i) {
        Checks.checkcond(i >= 0 && i < size());
        State<SUT> ret = StateRouterBase.this.fsm.initialState();
        for (int c = 0; c < i; c++) {
           ret = next(ret, new Edge<SUT>(action(i), args(i)));
        }
        return ret;
      }

      @Override
      public Action<SUT> action(int i) {
        return pathToNext.get(i).action;
      }

      @Override
      public Object arg(int i, int j) {
        return this.args(i).values()[j];
      }

      @Override
      public boolean hasArg(int i, int j) {
        Checks.checkcond(j >= 0);
        return args(i).size() > j;
      }

      @Override
      public Args args(int i) {
        return pathToNext.get(i).args;
      }

      @Override
      public String toString() {
        return PrivateUtils.toString(this);
      }
    };
  }

  private State<SUT> next(State<SUT> state, Edge<SUT> t) {
    return state.expectation(t.action, t.args).state;
  }
}
