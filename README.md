<h1 align="center">
guava-retrying3
</h1>

<div align="center">

A basic guava retry library.

[![GitHub stars](https://img.shields.io/github/stars/itning/guava-retrying3.svg?style=social&label=Stars)](https://github.com/itning/guava-retrying3/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/itning/guava-retrying3.svg?style=social&label=Fork)](https://github.com/itning/guava-retrying3/network/members)
[![GitHub watchers](https://img.shields.io/github/watchers/itning/guava-retrying3.svg?style=social&label=Watch)](https://github.com/itning/guava-retrying3/watchers)
[![GitHub followers](https://img.shields.io/github/followers/itning.svg?style=social&label=Follow)](https://github.com/itning?tab=followers)

[![GitHub issues](https://img.shields.io/github/issues/itning/guava-retrying3.svg)](https://github.com/itning/guava-retrying3/issues)
[![GitHub license](https://img.shields.io/github/license/itning/guava-retrying3.svg)](https://github.com/itning/guava-retrying3/blob/main/LICENSE)
[![GitHub last commit](https://img.shields.io/github/last-commit/itning/guava-retrying3.svg)](https://github.com/itning/guava-retrying3/commits)
[![GitHub release](https://img.shields.io/github/release/itning/guava-retrying3.svg)](https://search.maven.org/artifact/io.github.itning/guava-retrying3)
[![GitHub repo size in bytes](https://img.shields.io/github/repo-size/itning/guava-retrying3.svg)](https://github.com/itning/guava-retrying3)
![HitCount](https://hitcount.itning.com/?u=itning&r=guava-retrying3)
[![language](https://img.shields.io/badge/language-JAVA-green.svg)](https://github.com/itning/guava-retrying3)

</div>


# Notice
This is a personal maintenance version, which is not compatible with the API of the [original library](https://github.com/rholder/guava-retrying)

**Incompatible changes**

- Package structure modification
- [RetryListener](https://github.com/itning/guava-retrying3/blob/main/src/main/java/io/github/itning/retry/listener/RetryListener.java#L34) now only calls back the onRetry method when a retry occurs
- Replace the project build tool from gradle to maven
- Package name from `com.github.rholder.retry.*` to `io.github.itning.retry.*`

# What is this?
The guava-retrying module provides a general purpose method for retrying arbitrary Java code with specific stop, retry,
and exception handling capabilities that are enhanced by Guava's predicate matching.

This is a fork of the excellent RetryerBuilder code posted [here](http://code.google.com/p/guava-libraries/issues/detail?id=490)
by Jean-Baptiste Nizet (JB).  I've added a Gradle build for pushing it up to my little corner of Maven Central so that
others can easily pull it into their existing projects with minimal effort.  It also includes
exponential and Fibonacci backoff [WaitStrategies](http://rholder.github.io/guava-retrying/javadoc/2.0.0/com/github/rholder/retry/WaitStrategies.html)
that might be useful for situations where more well-behaved service polling is preferred.

# Maven
```xml
<dependency>
    <groupId>io.github.itning</groupId>
    <artifactId>guava-retrying3</artifactId>
    <version>3.0.5</version>
</dependency>
```

## TIP

If you don't use `io.github.itning.retry.strategy.limit.FixedAttemptTimeLimit` in your code, you can exclude `guava` dependency

```xml
<dependency>
    <groupId>io.github.itning</groupId>
    <artifactId>guava-retrying3</artifactId>
    <version>3.0.5</version>
    <exclusions>
        <exclusion>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

# Gradle

```groovy
implementation "io.github.itning:guava-retrying3:3.0.5"
```

# Quickstart
A minimal sample of some of the functionality would look like:

```java
Callable<Boolean> callable = new Callable<Boolean>() {
    public Boolean call() throws Exception {
        return true; // do something useful here
    }
};

Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
        .retryIfResult(Predicates.<Boolean>isNull())
        .retryIfExceptionOfType(IOException.class)
        .retryIfRuntimeException()
        .withStopStrategy(StopStrategies.stopAfterAttempt(3))
        .build();
try {
    retryer.call(callable);
} catch (RetryException e) {
    e.printStackTrace();
} catch (ExecutionException e) {
    e.printStackTrace();
}
```

This will retry whenever the result of the `Callable` is null, if an `IOException` is thrown, or if any other
`RuntimeException` is thrown from the `call()` method. It will stop after attempting to retry 3 times and throw a
`RetryException` that contains information about the last failed attempt. If any other `Exception` pops out of the
`call()` method it's wrapped and rethrown in an `ExecutionException`.

# Exponential Backoff

Create a `Retryer` that retries forever, waiting after every failed retry in increasing exponential backoff intervals
until at most 5 minutes. After 5 minutes, retry from then on in 5 minute intervals.

```java
Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
        .retryIfExceptionOfType(IOException.class)
        .retryIfRuntimeException()
        .withWaitStrategy(WaitStrategies.exponentialWait(100, 5, TimeUnit.MINUTES))
        .withStopStrategy(StopStrategies.neverStop())
        .build();
```
You can read more about [exponential backoff](http://en.wikipedia.org/wiki/Exponential_backoff) and the historic role
it played in the development of TCP/IP in [Congestion Avoidance and Control](http://ee.lbl.gov/papers/congavoid.pdf).

# Fibonacci Backoff

Create a `Retryer` that retries forever, waiting after every failed retry in increasing Fibonacci backoff intervals
until at most 2 minutes. After 2 minutes, retry from then on in 2 minute intervals.

```java
Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
        .retryIfExceptionOfType(IOException.class)
        .retryIfRuntimeException()
        .withWaitStrategy(WaitStrategies.fibonacciWait(100, 2, TimeUnit.MINUTES))
        .withStopStrategy(StopStrategies.neverStop())
        .build();
```

Similar to the `ExponentialWaitStrategy`, the `FibonacciWaitStrategy` follows a pattern of waiting an increasing amount
of time after each failed attempt.

Instead of an exponential function it's (obviously) using a
[Fibonacci sequence](https://en.wikipedia.org/wiki/Fibonacci_numbers) to calculate the wait time.

Depending on the problem at hand, the `FibonacciWaitStrategy` might perform better and lead to better throughput than
the `ExponentialWaitStrategy` - at least according to
[A Performance Comparison of Different Backoff Algorithms under Different Rebroadcast Probabilities for MANETs](http://www.comp.leeds.ac.uk/ukpew09/papers/12.pdf).

The implementation of `FibonacciWaitStrategy` is using an iterative version of the Fibonacci because a (naive) recursive
version will lead to a [StackOverflowError](http://docs.oracle.com/javase/7/docs/api/java/lang/StackOverflowError.html)
at a certain point (although very unlikely with useful parameters for retrying).

Inspiration for this implementation came from [Efficient retry/backoff mechanisms](https://paperairoplane.net/?p=640).

# Building from source
The guava-retrying module uses a [maven](https://maven.apache.org/)-based build system. 
The only prerequisites are [Git](https://help.github.com/articles/set-up-git) and JDK 1.8+.

## check out sources
`git clone git://github.com/itning/guava-retrying3.git`

## compile and test, build all jars
`./mvn `

## install all jars into your local Maven cache
`./mvn install`

# License
The guava-retrying module is released under version 2.0 of the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0).

# Contributors
* Jean-Baptiste Nizet (JB)
* Jason Dunkelberger (dirkraft)
* Diwaker Gupta (diwakergupta)
* Jochen Schalanda (joschi)
* Shajahan Palayil (shasts)
* Olivier Grégoire (fror)
* Andrei Savu (andreisavu)
* (tchdp)
* (squalloser)
* Yaroslav Matveychuk (yaroslavm)
* Stephan Schroevers (Stephan202)
* Chad (voiceinsideyou)
* Kevin Conaway (kevinconaway)
* Alberto Scotto (alb-i986)
