package org.pitest.mutationtest.incremental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.pitest.classinfo.ClassIdentifier;
import org.pitest.classinfo.ClassName;
import org.pitest.classinfo.HierarchicalClassId;
import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.execute.MutationStatusTestPair;
import org.pitest.mutationtest.results.DetectionStatus;
import org.pitest.mutationtest.results.MutationResult;
import org.pitest.util.PitXmlDriver;
import org.pitest.util.Unchecked;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;

public class XStreamHistoryStore implements HistoryStore {

  private final static XStream                                  XSTREAM_INSTANCE  = configureXStream();

  private final WriterFactory                                   outputFactory;
  private final BufferedReader                                  input;
  private final Map<MutationIdentifier, MutationStatusTestPair> previousResults   = new HashMap<MutationIdentifier, MutationStatusTestPair>();
  private final Map<ClassName, HierarchicalClassId>                 previousClassPath = new HashMap<ClassName, HierarchicalClassId>();

  public XStreamHistoryStore(final WriterFactory output,
      final Option<Reader> input) {
    this.outputFactory = output;
    this.input = createReader(input);
  }

  private static XStream configureXStream() {
    final XStream xstream = new XStream(new PitXmlDriver());
    xstream.alias("fullClassId", HierarchicalClassId.class);
    xstream.alias("classId", ClassIdentifier.class);
    xstream.alias("name", ClassName.class);
    xstream.alias("result", IdResult.class);
    xstream.alias("statusTestPair", MutationStatusTestPair.class);
    xstream.alias("status", DetectionStatus.class);
    xstream.useAttributeFor(ClassIdentifier.class, "name");
    xstream.useAttributeFor(ClassIdentifier.class, "hash");
    return xstream;
  }

  private BufferedReader createReader(final Option<Reader> input) {
    if (input.hasSome()) {
      return new BufferedReader(input.value());
    }
    return null;
  }

  public void recordClassPath(final Collection<HierarchicalClassId> ids) {
    final PrintWriter output = this.outputFactory.create();
    output.println(ids.size());
    for (final HierarchicalClassId each : ids) {
      output.println(toXml(each));
    }
    output.flush();

  }

  public void recordResult(final MutationResult result) {
    final PrintWriter output = this.outputFactory.create();
    output.println(toXml(new IdResult(result.getDetails().getId(), result
        .getStatusTestPair())));
    output.flush();
  }

  public Map<MutationIdentifier, MutationStatusTestPair> getHistoricResults() {
    return this.previousResults;
  }

  public Map<ClassName, HierarchicalClassId> getHistoricClassPath() {
    return this.previousClassPath;
  }

  public void initialize() {
    if (this.input != null) {
      restoreClassPath();
      restoreResults();
      try {
        this.input.close();
      } catch (final IOException e) {
        throw Unchecked.translateCheckedException(e);
      }
    }
  }

  private void restoreResults() {
    String line;
    try {
      line = this.input.readLine();
      while (line != null) {
        final IdResult result = (IdResult) fromXml(line);
        this.previousResults.put(result.id, result.status);
        line = this.input.readLine();
      }
    } catch (final IOException e) {
      throw Unchecked.translateCheckedException(e);
    }

  }

  private void restoreClassPath() {
    try {
      final long classPathSize = Long.valueOf(this.input.readLine());
      for (int i = 0; i != classPathSize; i++) {
        final HierarchicalClassId ci = (HierarchicalClassId) fromXml(this.input
            .readLine());
        this.previousClassPath.put(ci.getName(), ci);
      }
    } catch (final IOException e) {
      throw Unchecked.translateCheckedException(e);
    }
  }

  private static Object fromXml(final String xml) {
    return XSTREAM_INSTANCE.fromXML(xml);
  }

  private static String toXml(final Object o) {
    final Writer writer = new StringWriter();
    XSTREAM_INSTANCE.marshal(o, new CompactWriter(writer));
    return writer.toString().replaceAll("\n", "");
  }

  private static class IdResult {
    final MutationIdentifier     id;
    final MutationStatusTestPair status;

    IdResult(final MutationIdentifier id, final MutationStatusTestPair status) {
      this.id = id;
      this.status = status;
    }

  }

}