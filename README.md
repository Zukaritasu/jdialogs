# Windows Common Dialogs

![GitHub](https://img.shields.io/github/license/Zukaritasu/jdialogs) ![GitHub all releases](https://img.shields.io/github/downloads/Zukaritasu/jdialogs/total) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/Zukaritasu/jdialogs)

The jdialogs.jar library contains several classes that display dialog windows such as file chooser, fonts, colors, etc...

All dialog windows are native to the Windows system, so the jdialogs.jar library comes with a DLL to call the necessary native functions using JNI.

From native code (C++) the windows common dialogs are implemented where with the use of JNI (Java Native Interface) you can have a native communication with the Windows API. In general all the dialog boxes of the Windows API have a window as a parent of the dialog box to receive notifications and messages, so the handle of the Java AWT windows is extracted for a better composition of them.

This library was not made with the intention of being cross-platform with other operating systems but if you intend to contribute to this project to make it cross-platform or fix any bugs then welcome!!!

## Available Windows Common Dialogs

At the moment the following classes are available. Some of them are still missing, for example to print a document, but for the moment these are the most common ones.

* ColorDialog
* FolderBrowserDialog
* FontDialog
* FileDialog
* MessageBox

## Solution to future failures

The library has not been fully tested in case of the use of threads or any action that causes the library to crash in its operation. Also, as the implementation of Java by Oracle Corporation progresses, it may be possible that a change may have to be made to the source code written in C++. It is up to the developers to use this library and report the bugs to get a quick fix.

## Requirements 

* **Windows** 10 64 bit
* **Java** version 17 or later versions

## Dependency

```xml
<dependency>
	<groupId>com.zukadev</groupId>
	<artifactId>jdialogs</artifactId>
	<version>1.9.6-SNAPSHOT</version>
</dependency>
```

## More information

* [Examples](./examples)
* [Windows Common Dialogs Documentation](./javadoc)
