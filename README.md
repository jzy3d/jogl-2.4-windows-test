# jogl-2.4-windows-test

This test intend to verify that a JOGL 2.4 program is able to get the appropriate GL Profile on *Windows 10* computers. It is known to succeed on MacOS, Ubuntu, but fail for some Windows Adapters.

Instructions for this test
* Run `JoglContextTestCase` with no VM args and check the line containing "Context GL version" which will be hopefully > 1.1 otherwise the test fail.
* Run `JoglContextTestCase` with VM args `-Dsun.java2d.noddraw=true` and check the line containing "Context GL version" which will be hopefully > 1.1 otherwise the test fail.
* Run `JoglContextTestCase` with VM args `-Dsun.java2d.noddraw=true -Dsun.java2d.opengl=True` and check the line containing "Context GL version" which will be hopefully > 1.1 otherwise the test fail.


