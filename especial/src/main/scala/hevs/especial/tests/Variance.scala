package hevs.especial.tests

// A Tour of Scala: Generic Classes
// http://www.scala-lang.org/old/node/113.html
object Var1 {
  // Subtyping of generic types is INVARIANT.
  // Stack[T] is only a subtype of Stack[S] iff S = T
  val stack = new Stack[Int]

  // Mutable stacks of an arbitrary element type T
  class Stack[T] {
    var elems: List[T] = Nil

    def push(x: T) {
      elems = x :: elems
    }

    def top: T = elems.head

    def pop() {
      elems = elems.tail
    }
  }

  stack.push(1)
  println(stack.top)
  stack.push('a') // 'a' toInt = 97
  println(stack.top)
  stack.pop()
  println(stack.top)
  // The type defined by the class Stack[T] is subject to invariant subtyping
  // regarding the type parameter.
  // Result
  // 97
  // 1
}

// A Tour of Scala: Variances
// http://www.scala-lang.org/old/node/129.html
object Var2 {

  // The annotation +T declares type T to be used only in covariant positions.
  // -T would declare T to be used only in contravariant positions.

  // Stack[T] is a subtype of Stack[S] if T is a subtype of S
  // Opposite holds for type parameters that are tagged with a -

  class Stack[+A] {
    def push[B >: A](elem: B): Stack[B] = new Stack[B] {
      override def top: B = elem

      override def pop: Stack[B] = Stack.this

      override def toString = elem.toString + " " +
        Stack.this.toString()
    }

    override def toString = ""

    def top: A = sys.error("no element on stack")

    def pop: Stack[A] = sys.error("no element on stack")
  }

  object VariancesTest extends App {
    var s: Stack[Any] = new Stack().push("hello")
    s = s.push(new Object())
    s = s.push(7)
    println("Stack is: " + s)
  }
}

