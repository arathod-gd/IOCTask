# IOCTask

Small Spring IoC example showing how `ApplicationContext` creates and wires beans.

## Project Structure

- `org.demoapp.Main` starts the container.
- `org.demoapp.config.AppConfig` is the Java configuration class.
- `org.demoapp.beans.Bar` depends on `Foo`.
- `org.demoapp.beans.Foo` is the interface.
- `org.demoapp.beans.FooImpl` is one implementation of `Foo`.

## Run

Requirements:

- Java 17
- Maven 3.9+

Build:

```bash
mvn clean package
```

Run:

```bash
mvn exec:java
```

Expected output is similar to:

```text
Initialized: Foo bean declared as interface return type
Initialized: Foo bean declared as implementation return type
Bar injected Foo -> Foo bean declared as implementation return type
Foo beans: [fooAsInterface, fooAsImplementation]
FooImpl beans: [fooAsInterface, fooAsImplementation]
```

## Questions

### What is the `ApplicationContext`?

`ApplicationContext` is Spring's IoC container. It is responsible for:

- creating beans
- resolving dependencies between beans
- managing bean lifecycle
- reading configuration from annotations, Java config, XML, or properties

In this project, `Main` creates an `AnnotationConfigApplicationContext` with `AppConfig.class`. After that, Spring scans the package, creates beans, injects `Foo` into `Bar`, and returns `Bar` when `context.getBean(Bar.class)` is called.

### What are the tradeoffs of different approaches to injecting beans?

The main approaches are:

#### Constructor injection

Example: `Bar(Foo foo)`

Pros:

- makes required dependencies explicit
- supports immutable fields with `final`
- easier to test
- fails fast if dependencies are missing

Cons:

- constructors can become large if a class has too many dependencies, which usually indicates the class has too many responsibilities

This is usually the best default and is what this project uses in `Bar`.

#### Setter injection

Pros:

- useful for optional dependencies
- allows reconfiguration after object creation

Cons:

- bean can exist in a partially initialized state
- dependencies are less obvious than with constructors

Use it when a dependency is truly optional.

#### Field injection

Pros:

- very short and easy to write

Cons:

- harder to test
- hides dependencies
- encourages reflection-based wiring
- not suitable for `final` fields

This is usually the weakest choice and is generally avoided in production code.

#### `@Bean` method injection / Java config

Pros:

- useful for third-party classes you cannot annotate
- explicit bean construction logic

Cons:

- can become verbose
- bean wiring can be split between configuration classes and components

Use this when you need manual control over creation.

### Why do we need to use `@Qualifier` when multiple of the same type are defined?

When Spring finds more than one bean of the same type, autowiring by type becomes ambiguous.

Example in this project:

- `fooAsInterface()` returns `Foo`
- `fooAsImplementation()` returns `FooImpl`

Now Spring sees multiple `Foo` candidates. If no bean is marked `@Primary` and no `@Qualifier` is used, Spring does not know which one to inject and throws `NoUniqueBeanDefinitionException`.

`@Qualifier` solves that by naming the exact bean to inject.

Example:

```java
public Bar(@Qualifier("fooAsInterface") Foo foo) {
    this.foo = foo;
}
```

`@Primary` is a default preference. `@Qualifier` is an explicit choice. Use `@Qualifier` when you want predictable wiring and there are multiple candidates.

### How to avoid loading of heavy beans on startup and decrease startup time?

Use lazy or conditional creation for expensive beans.

Common options:

- `@Lazy` on a bean or injection point so the bean is created only when first needed
- split heavy logic into a separate service that is called on demand instead of during bean creation
- use `@Conditional`, `@Profile`, or property-based conditions so expensive beans load only in specific environments
- avoid doing I/O, cache warmup, remote calls, or large computations inside constructors
- initialize caches asynchronously after startup if immediate availability is not required

Example:

```java
@Bean
@Lazy
public HeavyCache heavyCache() {
    return new HeavyCache();
}
```

Important tradeoff:

- lazy loading improves startup time
- first use becomes slower because initialization is deferred

### What are Spring lifecycle stages and methods?

Typical bean lifecycle:

1. Spring creates the bean instance.
2. Spring injects dependencies.
3. Spring calls awareness callbacks if implemented, such as `BeanNameAware` or `ApplicationContextAware`.
4. Spring runs pre-initialization post-processors.
5. Spring runs initialization callbacks.
6. Bean is ready for use.
7. When the context closes, Spring runs destruction callbacks.

Common lifecycle methods:

- `@PostConstruct` for initialization after dependency injection
- `InitializingBean.afterPropertiesSet()`
- custom `initMethod` on `@Bean`
- `@PreDestroy` before shutdown
- `DisposableBean.destroy()`
- custom `destroyMethod` on `@Bean`
- `BeanPostProcessor` for framework-level interception before and after initialization

Simple example:

```java
@Component
public class ExampleBean {

    @PostConstruct
    public void init() {
        System.out.println("Bean initialized");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Bean destroyed");
    }
}
```

## Notes About This Project

This version of the project demonstrates the assignment directly:

- `fooAsInterface()` declares a bean with return type `Foo`
- `fooAsImplementation()` declares a bean with return type `FooImpl`
- `Bar` injects `Foo` without using `@Qualifier`
- `fooAsImplementation()` is marked `@Primary`, so Spring injects that bean by default

### What is strange about defining the same object graph with different return types?

In this project, Spring still recognizes the concrete runtime type created by each `@Bean` method.

That means:

- `fooAsInterface()` is declared as `Foo`
- but because it returns a `FooImpl` object, Spring still exposes it for `FooImpl` lookups in this setup

So both of these match both beans:

- `getBeanNamesForType(Foo.class)`
- `getBeanNamesForType(FooImpl.class)`

The practical lesson here is:

- injecting by the interface keeps your code loosely coupled
- injecting by the implementation ties your code to `FooImpl`
- multiple matching beans still require `@Primary` or `@Qualifier`

### What happens if you try `@Autowired` on a `final` field?

This is the wrong pattern for required dependencies.

Example:

```java
@Autowired
private final Foo foo;
```

Problem:

- `final` fields must be initialized during construction
- field injection happens after the object is created
- constructor injection is the correct approach for mandatory dependencies

That is why `Bar` uses constructor injection:

```java
public Bar(Foo foo) {
    this.foo = foo;
}
```

### How is the correct bean injected without `@Qualifier`?

`fooAsImplementation()` is annotated with `@Primary`.

That tells Spring:

- if multiple `Foo` beans exist
- and no `@Qualifier` is provided
- prefer `fooAsImplementation()`

So `Bar` receives the correct `Foo` bean without needing `@Qualifier`.
