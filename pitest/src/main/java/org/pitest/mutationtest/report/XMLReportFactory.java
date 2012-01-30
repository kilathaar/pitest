/*
 * Copyright 2011 Henry Coles
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
package org.pitest.mutationtest.report;

import org.pitest.extension.TestListener;
import org.pitest.functional.F;
import org.pitest.mutationtest.CoverageDatabase;
import org.pitest.mutationtest.ListenerFactory;

public class XMLReportFactory implements ListenerFactory {

  private final ResultOutputStrategy outputStrategy;

  public XMLReportFactory(final ResultOutputStrategy outputStrategy) {
    this.outputStrategy = outputStrategy;
  }

  public TestListener getListener(final CoverageDatabase coverage,
      final long startTime, final SourceLocator locator) {
    return new XMLReportListener(this.outputStrategy);
  }

  public static F<ResultOutputStrategy, ListenerFactory> createFactoryFunction() {
    return new F<ResultOutputStrategy, ListenerFactory>() {

      public ListenerFactory apply(final ResultOutputStrategy outputStrategy) {
        return new XMLReportFactory(outputStrategy);
      }

    };
  }

}