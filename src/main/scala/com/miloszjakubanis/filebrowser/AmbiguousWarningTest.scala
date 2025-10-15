package com.miloszjakubanis.filebrowser

class AmbiguousWarningTest {
  // This method looks like the second case is unreachable, but it's not
  def testAmbiguity(x: Any): String = x match {
    case s: String if s.isEmpty => "Empty string"
    case s: String => "Non-empty string"
    case _ => "Not a string"
  }

  // This method looks like it always returns, but the throw is reachable
  def trickyReturn(flag: Boolean): Int = {
    if (flag) return 1
    else if (!flag) return 2
    throw new RuntimeException("Should never reach here, but actually can if return is not used")
  }

  // Variable assigned but never used
  def unusedVariable(): Unit = {
    val x = 42 // Should trigger 'unused variable' warning
  }

  // Unreachable catch block (looks unreachable, but isn't)
  def unreachableCatch(): Unit = {
    try {
      throw new Exception("Test")
    } catch {
      case _: NullPointerException => println("Caught NPE")
      case _: Exception => println("Caught Exception")
    }
  }

  // Shadowed variable name
  def shadowedVariable(): Unit = {
    val x = "outer"
    {
      val x = "inner" // Shadows outer x
      println(x)
    }
    println(x)
  }

  // Suspicious equality check
  def suspiciousEquality(): Boolean = {
    val s = "string"
    val n = 123
    s == n // Comparing String and Int
  }

  // Redundant if condition
  def redundantIf(flag: Boolean): String = {
    if (flag == true) "Flag is true" else "Flag is false"
  }
}
