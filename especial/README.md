# ESPecIal

Original code from `P.-A. Mudry (mui AT hevs.ch) - 22.09.2014`.

## How to run

Just import the `sbt` project in IntelliJ IDEA (the [Scala plugin](https://www.jetbrains.com/idea/features/scala.html) must be installed).

-- OR --

The `sbteclipse` plugin is installed if you prefer to use Eclipse as IDE :( To import the project, simply run the following task using `sbt`:

	$ sbt
	> compile
	> eclipse

## How to test

To run tests (`sbt` must be installed):

	$ sbt test
	[...]
	[info] All tests passed.
