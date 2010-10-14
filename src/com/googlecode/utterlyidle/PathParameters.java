package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;

import static com.googlecode.totallylazy.Sequences.sequence;

class PathParameters extends Parameters{
  public PathParameters pathParameters(Pair<String, String>... pairs) {
      return (PathParameters) sequence(pairs).foldLeft(new PathParameters(), pairIntoParameters());
  }

    public static Callable2<Parameters, Pair<String, String>, Parameters> pairIntoParameters() {
        return new Callable2<Parameters, Pair<String, String>, Parameters>() {
            public Parameters call(Parameters result, Pair<String, String> pair) throws Exception {
                return result.add(pair.first(), pair.first());
            }
        };
    }
}