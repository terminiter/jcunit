package com.github.dakusui.jcunit.core.tuples;

import com.github.dakusui.jcunit.core.Utils;

import java.util.TreeMap;

public class TupleImpl extends TreeMap<String, Object> implements Tuple {
  public Tuple cloneTuple() {
    return (Tuple) super.clone();
  }

  @Override public boolean isSubtupleOf(Tuple another) {
    return isSubtupleOf(this, another);
  }

  static boolean isSubtupleOf(Tuple a, Tuple b) {
    Utils.checknotnull(a);
    Utils.checknotnull(b);
    if (!b.keySet().containsAll(a.keySet())) {
      return false;
    }
    for (String k : a.keySet()) {
      if (!Utils.eq(a.get(k), b.get(k))) {
        return false;
      }
    }
    return true;

  }
}