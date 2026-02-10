# Windows Common Dialogs

![GitHub](https://img.shields.io/github/license/Zukaritasu/jdialogs) ![GitHub all releases](https://img.shields.io/github/downloads/Zukaritasu/jdialogs/total) ![Maven Central](https://img.shields.io/maven-central/v/com.zukadev/jdialogs)

The jdialogs.jar library contains several classes that display dialog windows such as file chooser, fonts, colors, etc...

All dialog windows are native to the Windows system, so the jdialogs.jar library comes with a DLL to call the necessary native functions using JNI.

*The native DLL is automatically extracted and loaded from the JAR, no manual setup required.*

From native code (C++) the windows common dialogs are implemented where with the use of JNI (Java Native Interface) you can have a native communication with the Windows API. In general all the dialog boxes of the Windows API have a window as a parent of the dialog box to receive notifications and messages, so the handle of the Java AWT windows is extracted for a better composition of them.

This library was not made with the intention of being cross-platform with other operating systems but if you intend to contribute to this project to make it cross-platform or fix any bugs then welcome!!!

## Available Windows Common Dialogs

At the moment the following classes are available. Some of them are still missing, for example to print a document, but for the moment these are the most common ones.

* **ColorDialog**: Standard Windows color picker.
* **FolderBrowserDialog**: Native directory selection.
* **FontDialog**: System font and style selector.
* **FileDialog**: Native Open/Save file dialogs.
* **MessageBox**: Standard Windows alert and confirmation boxes.

## Stability & Support

This library bridges Java with native Windows APIs using JNI. While it is designed for stability and native performance, please consider the following:

* **Thread Safety**: The library is currently being tested for concurrent environments. If you encounter unexpected behavior when using multiple threads, please report it via an Issue.
* **Maintenance**: As Java (Oracle/OpenJDK) and the Windows API evolve, updates to the native C++ source code may be required to maintain long-term compatibility.
* **Bug Reports**: Your feedback is essential. If you find a bug or experience a crash, please open an **Issue** or submit a **Pull Request**. Your contributions help make this library more robust for everyone!

## Requirements 

* **Windows** 10 64 bit
* **Java** version 17 or later versions

## Dependency

Maven
```xml
<dependency>
    <groupId>com.zukadev</groupId>
    <artifactId>jdialogs</artifactId>
    <version>2.0.14</version>
    <scope>compile</scope>
</dependency>
```

Gradle
```gradle
implementation 'com.zukadev:jdialogs:2.0.14'
```

## More information

* [Examples](./examples)
