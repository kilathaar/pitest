package org.pitest.internal.classloader;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class DirectoryClassPathRootTest {

  private DirectoryClassPathRoot testee;

  @Test
  public void testGetDataReturnsNullForUnknownClass() throws Exception {
    this.testee = new DirectoryClassPathRoot(new File("foo"));
    assertNull(this.testee.getData("bar"));
  }

  @Test
  public void testReturnsClassNames() {
    final File root = new File("target/test-classes/"); // this is going to be
    // flakey as hell
    this.testee = new DirectoryClassPathRoot(root);
    assertTrue(this.testee.classNames().contains(
        DirectoryClassPathRootTest.class.getName()));
  }

}
