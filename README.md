![GitHub](https://img.shields.io/github/license/studio42gmbh/base42)
![GitHub top language](https://img.shields.io/github/languages/top/studio42gmbh/base42)
![GitHub last commit](https://img.shields.io/github/last-commit/studio42gmbh/base42)
![GitHub issues](https://img.shields.io/github/issues/studio42gmbh/base42)
![GitHub build](https://img.shields.io/github/actions/workflow/status/studio42gmbh/base42/maven.yml)


# Base 42

This is the library that contains all basic helpers and utils used across projects of Studio 42.

Have a great day!

Benjamin Schiller

> "Look up to the stars not down on your feet. Be curious!" _Stephen Hawking 1942 - 2018_


## Features

Helpers for:
* Arrays
* Beans
* JIT Compilation
* Console (ANSI)
* Object Cross Conversion (i.e. from String to Long)
* Date
* Files
* Module
* Resources
* Testing
* Strings
* Validation
* Zip

## Changelist

- 2025-12-04 Added SingleFileWatcher for tracking changes of a file easily


## Future Plans

* Expand where necessary to wrap basic functionality nicely

## Usage

Either just use the pom:
```
<dependency>
  <groupId>de.s42</groupId>
  <artifactId>base42</artifactId>
  <version>1.0.6</version>
</dependency> 
```
Or if you want the latest trunk from code:
* Download project
* Compile (project files used with Netbeans best)
* Add as maven dependency to your project locally
* Use the helpers you need

Find the Javadoc here: https://studio42gmbh.github.io/base42/javadoc/
