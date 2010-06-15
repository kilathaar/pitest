/*
 * Copyright 2010 Henry Coles
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License. 
 */

package org.pitest.extension.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.pitest.extension.IsolationStrategy;

public class NamedClassesIsolationStrategy implements IsolationStrategy {

  private final Set<String> names = new LinkedHashSet<String>();

  public NamedClassesIsolationStrategy(final String... prefixes) {
    this(Arrays.asList(prefixes));
  }

  public NamedClassesIsolationStrategy(
      final Collection<String> excludedPackagePrefixes) {
    this.names.addAll(excludedPackagePrefixes);
  }

  public boolean shouldIsolate(final String name) {
    for (final String s : this.names) {
      if (name.equals(s)) {
        return true;
      }
    }
    return false;
  }

}
