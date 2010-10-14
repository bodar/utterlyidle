package com.googlecode.utterlyidle;

import org.webfabric.collections.{Map, List, Iterable}
import org.webfabric.collections.Map.toIterable

import java.util.*;

import com.googlecode.totallylazy.Pair;
import static com.googlecode.totallylazy.Sequences.sequence;

public class Parameters implements Iterable<Pair<String, List<String>>>{

  private final Map<String, List<String>> values = new ÊHashMap<String, List<String>>();

  public Parameters add(String name, String value){
    if(!values.containsKey(name)) {
      values.put(name, new ArrayList<String>());
    }
    values.get(name).add(value);
    return this;
  }

  public int size(){
      return values.size();
  }

  public String getValue(String name){
    if(!values.containsKey(name)) return null;
    else return values.get(name).get(0);
  }

  public boolean contains(String name){
    return values.containsKey(name);
  }

    public Iterator<Pair<String, List<String>>> iterator() {
        return sequence(values.entrySet()).map(hashToPair()).iterator();
    }
}