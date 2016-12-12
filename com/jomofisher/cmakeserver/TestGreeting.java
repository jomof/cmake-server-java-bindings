package com.jomofisher.cmakeserver;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestGreeting {
  @Test
  public void testSomething() throws Exception {
    assertEquals(Greeting.sayHi(), 192);
  }
}
