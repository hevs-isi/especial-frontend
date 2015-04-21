# An embedded systems programming language (ESPeciaL)

> *ESPeciaL* proposes a novel approach aimed at simplifying the programming of embedded systems based on the dataflow paradigm.

This programming framework removes the need of low-level programming in C/C++, as the application is written by connecting blocks that produce and consume data. Thus, an embedded application can be described in terms of ready-to-use blocks that correspond to various micro-controller peripherals and program function (multiplexers, logic gates, etc.). The user application itself is written as an embedded Scala DSL (Domain-Specific Language).

From that code, the *ESPeciaL* compiler then generates the corresponding C++ code which can be tailored - using different back-ends - to match different embedded systems (such as `Arm Cortex M` boards) or a QEMU-based simulation environment.

## Demonstration applications

To demonstrate the validity of the approach, typical embedded systems applications have been implemented using *ESPeciaL*. A few demonstration applications are available in the [apps package](https://github.com/hevs-isi/especial-frontend/tree/master/src/test/scala/hevs/especial/apps).
* Have a look at the [fan PID regulation code](https://github.com/hevs-isi/especial-frontend/blob/master/src/test/scala/hevs/especial/apps/FanPid.scala) for a real-world demonstration application.

## Back-end

The *ESPeciaL* C++ back-end, the Hardware Abstraction Layer and the modified version of the QEMU emulator are available in [that repository](https://github.com/hevs-isi/especial-backend).

## Dependencies

See the [`third_party`](https://github.com/hevs-isi/especial-frontend/tree/master/third_party) folder for details.