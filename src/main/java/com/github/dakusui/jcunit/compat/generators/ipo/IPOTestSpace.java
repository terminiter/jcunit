package com.github.dakusui.jcunit.compat.generators.ipo;

import com.github.dakusui.enumerator.Combinator;
import com.github.dakusui.enumerator.Enumerator;
import com.github.dakusui.jcunit.exceptions.JCUnitRuntimeException;

import java.util.*;

/**
 * A class that describes the test space in which the test runs generated by
 * <code>IPO</code> class should cover.
 *
 * @author hiroshi
 */
public class IPOTestSpace {
  /**
   * The value domains. Each element of this field represents the domain of each
   * parameter.
   */
  Object[][] domains;

  /**
   * Creates an object of this class.
   *
   * @param domains Domains of the parameters.
   */
  public IPOTestSpace(Object[][] domains) {
    if (domains == null) {
      throw new NullPointerException();
    }
    if (domains.length < 2) {
      throw new IllegalArgumentException();
    }
    for (Object[] d : domains) {
      if (d == null) {
        throw new NullPointerException();
      }
    }
    this.domains = domains;
  }

  /**
   * Retruns a number of domains handled by this object.
   *
   * @return A number of domains.
   */
  int numDomains() {
    return domains.length;
  }

  /**
   * Returns a domain for specified parameter by <code>i</code>
   *
   * @param i ID of the parameter. The origin is 1, not 0.
   * @return The domain for Parameter Fi.
   */
  public Object[] domainOf(int i) {
    if (i == 0) {
      throw new IllegalArgumentException();
    }
    return domains[i - 1];
  }

  /**
   * Returns an index of the value <code>v</code> in parameter <code>i</code>.
   *
   * @param i ID of the parameter. The origin is 1, not 0.
   * @param v The valued searched from the domain of the parameter.
   * @return Index of <code>v</code> in the domain of the parameter
   * @throws JCUnitRuntimeException If <code>v</code> doesn't belong to the domain.
   */
  public int indexOf(int i, Object v) {
    int ret = 0;
    for (Object cur : domainOf(i)) {
      ret++;
      if (cur == v) {
        return ret;
      }
    }
    String msg = String.format("%s coudln't be found in the parameter:%d (%s)",
        v, i, Arrays.toString(this.domainOf(i)));
    throw new JCUnitRuntimeException(msg, null);
  }

  /**
   * Returns nth value of parameter Fi.
   *
   * @param i ID of the parameter Fi.
   * @param n index of the parameter Fi.
   * @return the nth value of parameter Fi.
   */
  public Object value(int i, int n) {
    if (i == 0) {
      throw new IllegalArgumentException();
    }
    if (n == 0) {
      throw new IllegalArgumentException();
    }
    return domains[i - 1][n - 1];
  }

  public Object[][] domains() {
    return this.domains;
  }

  public Set<IPOValueTuple.ValueTriple> createAllTriples() {
    Set<IPOValueTuple.ValueTriple> ret = new HashSet<IPOValueTuple.ValueTriple>();
    List<Integer> indexes = new LinkedList<Integer>();
    for (int i = 0; i < domains.length; i++) {
      indexes.add(i);
    }
    Enumerator<Integer> enumerator = new Combinator<Integer>(indexes, 3);
    for (List<Integer> attrIndexes : enumerator) {
      // //
      // map integers 0 from max to all possible patterns;
      long max = 1;
      for (int attrIndex : attrIndexes) {
        max *= domains[attrIndex].length;
      }
      assert max != 0;
      for (long i = 0; i < max; i++) {
        long locator = i;
        List<IPOValueTuple.Attr> attrs = new LinkedList<IPOValueTuple.Attr>();
        Collections.reverse(attrIndexes);
        for (int index : attrIndexes) {
          attrs.add(0,
              new IPOValueTuple.Attr(index + 1, domains[index][(int) locator
                  % domains[index].length]));
          locator /= domains[index].length;
        }
        ret.add(new IPOValueTuple.ValueTriple(attrs));
      }
    }
    return ret;
  }
}