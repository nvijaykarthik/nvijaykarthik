Below is the complete Markdown document. You can copy everything into a file named:

`Java_8_to_25_Complete_Features_Guide.md`

Java 8–25 | Language features, APIs, JVM, Garbage Collection and production tuning

# Java 8 to Java 25 – Complete Feature Reference

## 1. Introduction

This document covers the important features introduced from Java 8 through Java 25, including:

- Java language enhancements.
- Collections and Stream API.
- Functional programming.
- Concurrency and multithreading.
- Virtual threads.
- Records, sealed classes and pattern matching.
- JVM and performance improvements.
- Memory management.
- Garbage Collection algorithms and configuration.
- GC monitoring, logging and troubleshooting.
- Enterprise migration considerations.

Release terminology:

- Final: Production-ready permanent feature.
- Preview: Available for evaluation but requires preview flags.
- Incubator: Experimental API that may change.
- Experimental: JVM feature that may require special flags and may not be available in every distribution.

Note: This guide covers the major developer-facing features and important API/JVM enhancements. Java releases also contain thousands of smaller API additions, fixes and implementation changes.

# 2. Java 8 – Major Features

Java 8 introduced functional programming capabilities into Java.

## 2.1 Lambda Expressions

Lambda expressions allow us to pass behaviour as an argument.

Before Java 8:

```
Runnable runnable = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running task");
    }
};
```

Java 8:

```
Runnable runnable = () -> {
    System.out.println("Running task");
};

new Thread(runnable).start();
```

Single-line lambda:

```
Runnable runnable = () -> System.out.println("Hello");
```

Lambda with parameters:

```
BiFunction<Integer, Integer, Integer> addition =
        (a, b) -> a + b;

System.out.println(addition.apply(10, 20));
```

## 2.2 Functional Interfaces

A functional interface contains exactly one abstract method.

```
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}
```

Implementation:

```
Calculator addition = (a, b) -> a + b;

Calculator multiplication = (a, b) -> a * b;

System.out.println(addition.calculate(10, 20));
System.out.println(multiplication.calculate(10, 20));
```

Important built-in functional interfaces:

| Interface      | Purpose                             |
| -------------- | ----------------------------------- |
| Predicate      | Returns boolean                     |
| Function       | Accepts input and returns output    |
| Consumer       | Accepts input, returns nothing      |
| Supplier       | Supplies a value                    |
| BiFunction     | Accepts two inputs                  |
| UnaryOperator  | Input and output have the same type |
| BinaryOperator | Two inputs and same-type output     |

Example:

```
Predicate<Integer> isEven = n -> n % 2 == 0;

Function<String, Integer> getLength = String::length;

Consumer<String> print = System.out::println;

Supplier<UUID> uuidSupplier = UUID::randomUUID;
```

## 2.3 Stream API

Streams allow declarative processing of collections.

Traditional approach:

```
List<String> names = Arrays.asList(
        "Arun", "Bala", "Ajay", "Kumar"
);

List<String> result = new ArrayList<>();

for (String name : names) {
    if (name.startsWith("A")) {
        result.add(name.toUpperCase());
    }
}
```

Java 8:

```
List<String> result = names.stream()
        .filter(name -> name.startsWith("A"))
        .map(String::toUpperCase)
        .collect(Collectors.toList());
```

Important Stream operations:

| Operation   | Purpose                 |
| ----------- | ----------------------- |
| filter()    | Filter elements         |
| map()       | Transform elements      |
| flatMap()   | Flatten nested streams  |
| sorted()    | Sort elements           |
| distinct()  | Remove duplicates       |
| limit()     | Limit results           |
| skip()      | Skip elements           |
| reduce()    | Aggregate values        |
| collect()   | Collect results         |
| forEach()   | Iterate                 |
| count()     | Count elements          |
| anyMatch()  | Check if any match      |
| allMatch()  | Check all elements      |
| noneMatch() | Check no elements match |

Example – Group employees by department:

```
Map<String, List<Employee>> employeesByDepartment =
        employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment
                ));
```

Example – Calculate total salary:

```
double totalSalary = employees.stream()
        .mapToDouble(Employee::getSalary)
        .sum();
```

Example – Find maximum salary:

```
Optional<Employee> highestPaid =
        employees.stream()
                .max(Comparator.comparing(Employee::getSalary));
```

## 2.4 Method References

Method references provide shorter lambda syntax.

```
names.forEach(name -> System.out.println(name));
```

Can become:

```
names.forEach(System.out::println);
```

Types:

```
// Static method
Function<String, Integer> parser = Integer::parseInt;

// Instance method
Consumer<String> printer = System.out::println;

// Constructor reference
Supplier<ArrayList<String>> listSupplier = ArrayList::new;
```

## 2.5 Default Methods in Interfaces

Interfaces can contain implementation methods.

```
interface PaymentService {

    void processPayment();

    default void validate() {
        System.out.println("Default validation");
    }
}
```

This allowed interfaces to evolve without forcing all existing implementations to implement newly introduced methods.

## 2.6 Static Methods in Interfaces

```
interface Utility {

    static void print() {
        System.out.println("Utility");
    }
}
```

Usage:

```
Utility.print();
```

## 2.7 Optional

Helps represent a value that may be absent.

```
Optional<String> name = Optional.of("Java");

System.out.println(name.get());
```

Safer usage:

```
Optional<Customer> customer =
        repository.findById(id);

String category = customer
        .map(Customer::getCategory)
        .orElse("UNKNOWN");
```

Important methods:

- `of()`
- `ofNullable()`
- `empty()`
- `isPresent()`
- `ifPresent()`
- `map()`
- `flatMap()`
- `orElse()`
- `orElseGet()`
- `orElseThrow()`

## 2.8 New Date and Time API

The `java.time` package introduced immutable date/time classes.

```
LocalDate today = LocalDate.now();

LocalDate tomorrow = today.plusDays(1);

LocalTime currentTime = LocalTime.now();

LocalDateTime dateTime = LocalDateTime.now();

ZonedDateTime indiaTime =
        ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

Instant timestamp = Instant.now();
```

Difference between dates:

```
Period period = Period.between(
        LocalDate.of(2025, 1, 1),
        LocalDate.now()
);
```

Difference between timestamps:

```
Duration duration =
        Duration.between(startTime, endTime);
```

## 2.9 CompletableFuture

Asynchronous programming.

```
CompletableFuture<String> future =
        CompletableFuture.supplyAsync(() -> {
            return "Payment processed";
        });

System.out.println(future.get());
```

Chaining:

```
CompletableFuture.supplyAsync(() -> "Payment")
        .thenApply(value -> value + " Successful")
        .thenAccept(System.out::println);
```

Exception handling:

```
CompletableFuture.supplyAsync(() -> {
    throw new RuntimeException("Failure");
})
.exceptionally(ex -> "Default response");
```

For blocking production workloads, configure a dedicated executor instead of automatically relying on the common ForkJoinPool.

## 2.10 Other Java 8 Features

- Base64 encoding and decoding.
- Repeating annotations.
- Type annotations.
- Parameter name reflection with `-parameters`.
- StringJoiner.
- Map enhancements (`computeIfAbsent`, `merge`, `getOrDefault`).
- Parallel array sorting.
- Unsigned integer operations.
- Metaspace replacing PermGen.

Example:

```
String result = String.join(
        ",", "Java", "Spring", "Kafka"
);

String encoded = Base64.getEncoder()
        .encodeToString("hello".getBytes(StandardCharsets.UTF_8));
```

# 3. Java 9 – Major Features

## 3.1 Java Platform Module System

Java 9 introduced modules.

Before Java 9, applications commonly used JAR files and the classpath.

Java 9 introduced a module system with explicit dependencies and package exports.

Example:

`module-info.java`

```
module com.payment.application {

    requires java.net.http;

    exports com.payment.api;
}
```

Important keywords:

| Keyword  | Purpose                        |
| -------- | ------------------------------ |
| module   | Declare module                 |
| requires | Dependency                     |
| exports  | Expose package                 |
| opens    | Allow reflection               |
| uses     | Declare service dependency     |
| provides | Provide service implementation |

## 3.2 JShell

Interactive Java programming environment.

```
jshell
```

Example:

```
int a = 10;
int b = 20;

System.out.println(a + b);
```

Useful for testing Java expressions without creating a complete project.

## 3.3 Factory Methods for Collections

Java 9 introduced `List.of()`, `Set.of()` and `Map.of()`.

```
List<String> names =
        List.of("Java", "Python", "Go");

Set<Integer> numbers =
        Set.of(10, 20, 30);

Map<String, Integer> status =
        Map.of("SUCCESS", 200, "FAILED", 500);
```

These collections are unmodifiable and reject null values.

## 3.4 Private Methods in Interfaces

```
interface Payment {

    default void paymentCreated() {
        audit("CREATED");
    }

    default void paymentUpdated() {
        audit("UPDATED");
    }

    private void audit(String message) {
        System.out.println(message);
    }
}
```

## 3.5 Stream API Enhancements

New methods:

- `takeWhile()`
- `dropWhile()`
- `ofNullable()`
- `iterate()` overload

Example:

```
List<Integer> numbers =
        List.of(2, 4, 6, 7, 8);

numbers.stream()
        .takeWhile(n -> n % 2 == 0)
        .forEach(System.out::println);
```

Output:

```
2
4
6
```

## 3.6 Optional Enhancements

```
optional.ifPresentOrElse(
        System.out::println,
        () -> System.out.println("No value")
);
```

Other methods:

- `or()`
- `stream()`
- `ifPresentOrElse()`

## 3.7 Process API Improvements

Java 9 introduced `ProcessHandle`.

```
ProcessHandle current =
        ProcessHandle.current();

System.out.println(current.pid());

System.out.println(current.info());
```

Useful for process management and monitoring.

## 3.8 InputStream.transferTo()

```
try (InputStream input =
             Files.newInputStream(Path.of("input.txt"));
     OutputStream output =
             Files.newOutputStream(Path.of("output.txt"))) {

    input.transferTo(output);
}
```

## 3.9 HTTP/2 Client Incubator

Java 9 introduced an incubating HTTP client, which became standard in Java 11.

## 3.10 Unified JVM Logging

Introduced unified logging using `-Xlog`.

Example:

```
java -Xlog:gc* -jar application.jar
```

GC log with file output:

```
java -Xlog:gc*:file=gc.log -jar application.jar
```

## 3.11 Garbage Collection Changes

- G1 became the default collector in typical server configurations.
- CMS was deprecated.
- Unified JVM logging was introduced.
- G1 received several performance improvements.

# 4. Java 10 – Major Features

## 4.1 Local Variable Type Inference – var

Java 10 introduced `var`.

```
var name = "Java";

var age = 25;

var customers = new ArrayList<Customer>();
```

The compiler determines the variable's static type.

It is not dynamic typing.

Invalid:

```
var value = null;
```

Because the compiler cannot infer the type.

## 4.2 Collection Copy Methods

```
List<String> original =
        List.of("Java", "Spring");

List<String> copy =
        List.copyOf(original);
```

Also:

- `Set.copyOf()`
- `Map.copyOf()`

These produce unmodifiable copies.

## 4.3 Optional Enhancement

```
String value = Optional.of("Java")
        .orElseThrow();
```

## 4.4 Application Class Data Sharing

AppCDS can improve startup and memory sharing by archiving application classes.

```
java -XX:ArchiveClassesAtExit=app.jsa -jar application.jar
```

Reuse the archive:

```
java -XX:SharedArchiveFile=app.jsa -jar application.jar
```

## 4.5 Other Features

- Parallel full GC for G1.
- Improved container awareness.
- Experimental Graal JIT compiler support in applicable builds.
- Additional APIs for unmodifiable collectors.

# 5. Java 11 – LTS

Java 11 introduced several useful features for enterprise development.

## 5.1 Standard HTTP Client

Java 11 standardized the HTTP Client API.

```
HttpClient client = HttpClient.newHttpClient();

HttpRequest request =
        HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://example.com/api"
                ))
                .GET()
                .build();

HttpResponse<String> response =
        client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

System.out.println(response.body());
```

Supports:

- HTTP/1.1
- HTTP/2
- Synchronous requests
- Asynchronous requests
- WebSocket

Asynchronous example:

```
client.sendAsync(
        request,
        HttpResponse.BodyHandlers.ofString()
).thenApply(HttpResponse::body)
 .thenAccept(System.out::println);
```

## 5.2 String Enhancements

```
String value = "  Java  ";

System.out.println(value.isBlank());

System.out.println(value.strip());

System.out.println("Java".repeat(3));

"Java\nSpring\nKafka"
        .lines()
        .forEach(System.out::println);
```

Methods:

- `isBlank()`
- `strip()`
- `stripLeading()`
- `stripTrailing()`
- `repeat()`
- `lines()`

## 5.3 Files API

```
String content =
        Files.readString(Path.of("input.txt"));

Files.writeString(
        Path.of("output.txt"),
        "Java 11"
);
```

## 5.4 var in Lambda Parameters

```
BiFunction<String, String, String> function =
        (var a, var b) -> a + b;
```

## 5.5 Single-File Source Execution

```
java Hello.java
```

No separate compilation step is required for this simple source-file launch mode.

## 5.6 TLS 1.3

Java 11 added standard TLS 1.3 support.

## 5.7 Java Flight Recorder

JFR became available in OpenJDK.

Useful for:

- CPU profiling.
- Memory allocation analysis.
- GC analysis.
- Thread monitoring.
- Lock contention analysis.

## 5.8 Garbage Collection

- ZGC introduced as experimental.
- Epsilon GC introduced as experimental.
- G1 continued as the default server collector in typical configurations.

# 6. Java 12 – Major Features

## 6.1 Switch Expressions – Preview

Switch expressions allow returning values.

```
int result = switch (operation) {

    case "ADD" -> 10;

    case "SUBTRACT" -> 20;

    default -> 0;
};
```

In Java 12, this was a preview feature and its syntax evolved before finalization in Java 14.

## 6.2 JVM Constants API

Preview API for working with symbolic descriptions of class-file constants.

## 6.3 Garbage Collection

- Shenandoah GC introduced as experimental in OpenJDK.
- G1 abortable mixed collections.
- G1 improved memory return behaviour.
- Default CDS archives.

# 7. Java 13 – Major Features

## 7.1 Text Blocks – Preview

Text blocks simplify multi-line strings.

Before:

```
String json =
        "{\n" +
        "  \"name\": \"Java\",\n" +
        "  \"version\": 13\n" +
        "}";
```

Java 13:

```
String json = """
        {
            "name": "Java",
            "version": 13
        }
        """;
```

Text blocks became final in Java 15.

## 7.2 Switch Expressions – Second Preview

Continued refinement of switch expressions.

## 7.3 Other Features

- Dynamic CDS archives.
- Socket API reimplementation.
- Foreign Memory Access API incubator.
- ZGC improvements, including returning unused memory.

# 8. Java 14 – Major Features

## 8.1 Switch Expressions – Final

```
String status = switch (code) {

    case 200 -> "SUCCESS";

    case 400, 404 -> "CLIENT ERROR";

    case 500 -> "SERVER ERROR";

    default -> "UNKNOWN";
};
```

Using `yield`:

```
int result = switch (operation) {

    case "ADD" -> {
        audit();
        yield 10 + 20;
    }

    default -> 0;
};
```

## 8.2 Records – Preview

Records simplify data carrier classes.

```
record Customer(
        String id,
        String name
) {}
```

Usage:

```
Customer customer =
        new Customer("C100", "Arun");

System.out.println(customer.id());
```

Records became final in Java 16.

## 8.3 Pattern Matching for instanceof – Preview

Before:

```
if (obj instanceof String) {

    String value = (String) obj;

    System.out.println(value);
}
```

Java 14:

```
if (obj instanceof String value) {

    System.out.println(value);
}
```

Final in Java 16.

## 8.4 Helpful NullPointerExceptions

Java can provide more meaningful information about which expression caused a null pointer exception.

Example:

```
payment.getCustomer().getAddress().getCity();
```

The JVM can identify the null component in the expression.

## 8.5 Other Features

- JFR event streaming.
- Packaging tool `jpackage` incubator.
- Foreign Memory Access API second incubator.
- CMS garbage collector removed.
- G1 NUMA-aware memory allocation improvements.

# 9. Java 15 – Major Features

## 9.1 Text Blocks – Final

```
String query = """
        SELECT *
        FROM PAYMENT
        WHERE STATUS = 'FAILED'
        """;
```

## 9.2 Sealed Classes – Preview

Restricts which classes can extend a parent class or implement an interface.

```
sealed interface PaymentResult
        permits Success, Failure {
}

final class Success implements PaymentResult {
}

final class Failure implements PaymentResult {
}
```

Subclasses must be declared as:

- `final`
- `sealed`
- `non-sealed`

Sealed classes became final in Java 17.

## 9.3 Hidden Classes

Useful for frameworks generating classes dynamically.

## 9.4 Other Features

- ZGC production feature.
- Shenandoah production feature in supporting OpenJDK builds.
- EdDSA cryptographic signatures.
- Nashorn JavaScript engine removed.
- Records continued in preview.

# 10. Java 16 – Major Features

## 10.1 Records – Final

```
record Employee(
        int id,
        String name,
        double salary
) {}
```

Java automatically generates:

- Constructor.
- Accessor methods.
- `equals()`.
- `hashCode()`.
- `toString()`.

Example:

```
Employee employee =
        new Employee(100, "Arun", 50000);

System.out.println(employee.name());
```

Records are shallowly immutable.

## 10.2 Pattern Matching for instanceof – Final

```
if (obj instanceof String text) {

    System.out.println(text.toUpperCase());
}
```

## 10.3 Stream toList()

```
List<String> names = employees.stream()
        .map(Employee::name)
        .toList();
```

The returned list is unmodifiable.

## 10.4 Stream mapMulti()

Provides an alternative to `flatMap()` for emitting zero or more results per input.

```
Stream.of(
        List.of(1, 2),
        List.of(3, 4)
).<Integer>mapMulti(
        (list, downstream) ->
                list.forEach(downstream)
).forEach(System.out::println);
```

## 10.5 Unix Domain Socket Channels

Supports local inter-process communication using Unix domain sockets.

## 10.6 Other Features

- Sealed classes second preview.
- `jpackage` final.
- Vector API incubator.
- Foreign Function & Memory API incubator.
- Strong encapsulation of JDK internals.
- Elastic Metaspace.

# 11. Java 17 – LTS

## 11.1 Sealed Classes – Final

```
sealed class Account
        permits SavingsAccount, CurrentAccount {
}

final class SavingsAccount extends Account {
}

non-sealed class CurrentAccount extends Account {
}
```

## 11.2 Pattern Matching for Switch – Preview

```
static String describe(Object value) {

    return switch (value) {

        case Integer i -> "Integer";

        case String s -> "String";

        case null -> "NULL";

        default -> "UNKNOWN";
    };
}
```

This became final in Java 21.

## 11.3 Enhanced Random Number Generator API

```
RandomGenerator generator =
        RandomGenerator.getDefault();

int value = generator.nextInt(100);
```

## 11.4 Other Features

- Strong encapsulation of JDK internals.
- Security Manager deprecated for removal.
- RMI Activation removed.
- Foreign Function & Memory API incubator.
- Context-specific deserialization filters.
- Always-strict floating-point semantics.
- macOS AArch64 support.

# 12. Java 18 – Major Features

## 12.1 UTF-8 Default Charset

Java 18 standardized UTF-8 as the default charset for standard Java APIs.

```
String content =
        Files.readString(Path.of("file.txt"));
```

For external file formats and network protocols, explicitly specify the required charset where appropriate.

## 12.2 Simple Web Server

Java provides a lightweight development web server.

```
jwebserver --port 8080 --directory ./public
```

It is intended for testing and development, not production hosting.

## 12.3 Javadoc Code Snippets

Javadoc supports code snippets using `{@snippet}`.

## 12.4 Other Features

- Core reflection reimplemented using method handles.
- Foreign Function & Memory API second incubator.
- Vector API second incubator.
- Finalization deprecated for removal.
- Pattern matching for switch continued in preview.

# 13. Java 19 – Major Features

## 13.1 Virtual Threads – Preview

Virtual threads are lightweight threads managed by the JVM.

Traditional:

```
new Thread(() -> {
    callService();
}).start();
```

Java 19 preview:

```
try (var executor =
             Executors.newVirtualThreadPerTaskExecutor()) {

    Future<String> future =
            executor.submit(() -> callService());

    System.out.println(future.get());
}
```

Virtual threads became final in Java 21.

## 13.2 Record Patterns – Preview

```
record Point(int x, int y) {
}

if (obj instanceof Point(int x, int y)) {

    System.out.println(x + "," + y);
}
```

## 13.3 Other Features

- Structured Concurrency incubator.
- Scoped Values incubator.
- Foreign Function & Memory API preview.
- Generational ZGC experimental.
- Vector API incubator.
- Linux/RISC-V port.

# 14. Java 20 – Major Features

Java 20 mainly continued preview and incubator development.

## 14.1 Record Patterns – Second Preview

```
record Address(String city) {
}

record Customer(String name, Address address) {
}

if (obj instanceof Customer(
        String name,
        Address(String city))) {

    System.out.println(name + " " + city);
}
```

## 14.2 Scoped Values – Incubator

Scoped values allow immutable data to be shared with methods in a bounded execution scope.

```
static final ScopedValue<String> REQUEST_ID =
        ScopedValue.newInstance();

public static void main(String[] args) {

    ScopedValue.where(REQUEST_ID, "REQ100")
            .run(() -> process());
}

static void process() {

    System.out.println(REQUEST_ID.get());
}
```

Scoped Values became final in Java 25.

## 14.3 Other Features

- Virtual threads second preview.
- Pattern matching for switch fourth preview.
- Structured Concurrency second incubator.
- Foreign Function & Memory API second preview.
- Generational ZGC development continued.

# 15. Java 21 – LTS

Java 21 is one of the most important Java releases for enterprise development.

## 15.1 Virtual Threads – Final

Virtual threads allow applications to support large numbers of concurrent tasks without creating one expensive operating-system thread per task.

Example:

```
try (ExecutorService executor =
             Executors.newVirtualThreadPerTaskExecutor()) {

    Future<String> task1 =
            executor.submit(() -> callPaymentService());

    Future<String> task2 =
            executor.submit(() -> callCustomerService());

    System.out.println(task1.get());
    System.out.println(task2.get());
}
```

Simpler thread creation:

```
Thread.startVirtualThread(() -> {
    System.out.println("Virtual thread");
});
```

Factory:

```
ThreadFactory factory =
        Thread.ofVirtual().factory();
```

### When should you use virtual threads?

Good use cases:

- REST API calls.
- Database calls.
- Synchronous network communication.
- I/O-heavy microservices.
- Applications with many concurrent requests.

Not a direct solution for:

- CPU-intensive computations.
- Increasing CPU capacity.
- Eliminating database connection limits.
- Eliminating downstream service bottlenecks.

Important: Do not create a traditional thread pool just to pool virtual threads. Create virtual threads per task and separately limit scarce resources such as database connections and external API concurrency.

## 15.2 Pattern Matching for Switch – Final

```
static String process(Object obj) {

    return switch (obj) {

        case Integer i when i > 100 ->
                "Large integer";

        case Integer i ->
                "Small integer";

        case String s ->
                "String";

        case null ->
                "NULL";

        default ->
                "UNKNOWN";
    };
}
```

## 15.3 Record Patterns – Final

```
record Money(
        String currency,
        BigDecimal amount
) {
}

record Payment(
        String id,
        Money money
) {
}
```

Pattern matching:

```
if (obj instanceof Payment(
        String id,
        Money(String currency, BigDecimal amount))) {

    System.out.println(id);
    System.out.println(currency);
    System.out.println(amount);
}
```

## 15.4 Sequenced Collections

New interfaces:

- `SequencedCollection`
- `SequencedSet`
- `SequencedMap`

Example:

```
SequencedCollection<String> values =
        new ArrayList<>();

values.addFirst("FIRST");
values.addLast("LAST");

System.out.println(values.getFirst());

System.out.println(values.getLast());

System.out.println(values.reversed());
```

## 15.5 Other Features

- Generational ZGC became a product feature.
- Structured Concurrency preview.
- Scoped Values preview.
- Foreign Function & Memory API preview.
- Unnamed classes and instance main methods preview.
- String templates preview, subsequently withdrawn.

# 16. Java 22 – Major Features

## 16.1 Foreign Function & Memory API – Final

Allows Java applications to interact with native code and memory without traditional JNI boilerplate.

Example:

```
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.*;

public class NativeExample {

    public static void main(String[] args) throws Throwable {

        Linker linker = Linker.nativeLinker();

        SymbolLookup lookup = linker.defaultLookup();

        MemorySegment symbol =
                lookup.find("strlen").orElseThrow();

        MethodHandle strlen =
                linker.downcallHandle(
                        symbol,
                        FunctionDescriptor.of(
                                JAVA_LONG,
                                ADDRESS
                        )
                );

        try (Arena arena = Arena.ofConfined()) {

            MemorySegment text =
                    arena.allocateFrom("Hello Java");

            long length =
                    (long) strlen.invoke(text);

            System.out.println(length);
        }
    }
}
```

Native access may require an explicit runtime option depending on the JDK.

## 16.2 Stream Gatherers – Preview

Allows custom intermediate stream operations.

```
List<List<Integer>> windows =
        Stream.of(1, 2, 3, 4, 5)
                .gather(Gatherers.windowFixed(2))
                .toList();
```

Gatherers became final in Java 24.

## 16.3 Unnamed Variables and Patterns – Final

Use `_` when the value is intentionally unused.

```
try {

    Integer.parseInt("ABC");

} catch (NumberFormatException _) {

    System.out.println("Invalid number");
}
```

## 16.4 Other Features

- Statements before `super()` preview.
- Class-File API preview.
- Multi-file source-code launch.
- Vector API incubator.
- String templates second preview, later withdrawn.

# 17. Java 23 – Major Features

## 17.1 Module Import Declarations – Preview

```
import module java.base;
```

Imports the packages exported by the specified module.

This became final in Java 25.

## 17.2 Primitive Types in Patterns – Preview

Explores primitive types in pattern matching.

This feature continued in preview through Java 25.

## 17.3 Markdown Documentation Comments

JavaDoc supports Markdown-style documentation comments.

## 17.4 Other Features

- Stream Gatherers second preview.
- Structured Concurrency fourth preview.
- Scoped Values third preview.
- Foreign Function & Memory API third preview.
- Class-File API second preview.
- Generational ZGC became the default ZGC mode.

# 18. Java 24 – Major Features

## 18.1 Stream Gatherers – Final

Custom intermediate stream operations.

Example – Fixed-size windows:

```
List<List<Integer>> batches =
        IntStream.rangeClosed(1, 10)
                .boxed()
                .gather(Gatherers.windowFixed(3))
                .toList();
```

Example – Sliding windows:

```
List<List<Integer>> windows =
        IntStream.rangeClosed(1, 5)
                .boxed()
                .gather(Gatherers.windowSliding(3))
                .toList();
```

## 18.2 Flexible Constructor Bodies – Preview

Allows validation and certain statements before invoking the parent constructor.

```
class Customer extends Person {

    Customer(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Name is mandatory"
            );
        }

        super(name);
    }
}
```

Final in Java 25.

## 18.3 Other Features

- Class-File API preview.
- Module Import Declarations second preview.
- Structured Concurrency fourth preview.
- Scoped Values fourth preview.
- Generational Shenandoah GC.
- Ahead-of-Time class loading/linking improvements.
- Compact Object Headers experimental.
- Quantum-resistant cryptographic improvements.

# 19. Java 25 – LTS

Java 25 is an important LTS release.

## 19.1 Compact Source Files and Instance Main Methods – Final

Traditional Java:

```
public class Hello {

    public static void main(String[] args) {

        System.out.println("Hello Java");
    }
}
```

Java 25:

```
void main() {

    IO.println("Hello Java 25");
}
```

This makes small programs and introductory examples simpler.

For enterprise applications, conventional class-based organization remains useful.

## 19.2 Module Import Declarations – Final

```
import module java.base;
```

Imports all exported packages of the module.

## 19.3 Flexible Constructor Bodies – Final

```
class Account extends BaseAccount {

    Account(BigDecimal amount) {

        if (amount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Invalid amount"
            );
        }

        super(amount);
    }
}
```

## 19.4 Scoped Values – Final

Scoped Values are an alternative to some ThreadLocal use cases.

```
public class RequestContext {

    static final ScopedValue<String> REQUEST_ID =
            ScopedValue.newInstance();

    public static void handle(String requestId) {

        ScopedValue.where(REQUEST_ID, requestId)
                .run(() -> process());
    }

    private static void process() {

        System.out.println(REQUEST_ID.get());
    }
}
```

Useful for:

- Request context.
- Correlation IDs.
- Security context propagation.
- Virtual-thread-based applications.
- Structured task execution.

## 19.5 Key Derivation Function API

Java 25 introduces a standard API for cryptographic key derivation.

Useful for deriving keys from secret material and associated inputs.

Use security-reviewed algorithms and parameters rather than implementing your own cryptographic algorithm.

## 19.6 Other Java 25 Features

- Primitive types in patterns: third preview.
- Structured Concurrency: fifth preview.
- Stable Values: preview.
- Vector API: tenth incubator.
- PEM encoding/decoding API: preview.
- Compact Object Headers: product feature.
- Ahead-of-Time command-line ergonomics.
- G1 remembered-set improvements.
- Continued JVM performance and security enhancements.

Example – Compact Object Headers, where supported:

```
java -XX:+UseCompactObjectHeaders -jar application.jar
```

Benchmark before enabling in production.

# 20. Garbage Collection – Complete Guide

Garbage Collection is one of the most important JVM topics for enterprise applications.

It directly impacts:

- Application response time.
- Throughput.
- Memory consumption.
- CPU utilization.
- Application availability.
- Container stability.

## 20.1 What is Garbage Collection?

Garbage Collection automatically reclaims memory occupied by objects that are no longer reachable by the application.

Example:

```
public void process() {

    Customer customer = new Customer();

    customer = null;
}
```

The original object may become eligible for garbage collection after its last reachable reference disappears.

Important: Setting a reference to null does not immediately trigger GC.

## 20.2 JVM Memory Structure

The JVM has multiple memory areas.

| Memory area         | Purpose                           |
| ------------------- | --------------------------------- |
| Heap                | Stores Java objects               |
| Metaspace           | Class metadata                    |
| Stack               | Method frames and local variables |
| PC Register         | Current instruction location      |
| Native Method Stack | Native method execution           |
| Code Cache          | JIT compiled code                 |
| Direct Memory       | Native/off-heap buffers           |

The most important memory area for GC is the Heap.

## 20.3 Heap Structure

Traditional generational heap layout:

```
                 JVM HEAP
                     |
          -----------------------
          |                     |
      Young Generation      Old Generation
          |
     ---------------
     |      |      |
   Eden  Survivor Survivor
           S0      S1
```

### Young Generation

New objects are commonly allocated in Eden.

Objects that survive collections may move through survivor regions and eventually be promoted to old generation.

### Old Generation

Long-lived objects are retained here in traditional generational collectors.

Examples:

- Application caches.
- Long-lived session objects.
- Static references.
- Frequently used domain objects.

Note: Not every GC uses exactly this physical heap layout. ZGC and Shenandoah have different internal designs.

## 20.4 Garbage Collection Algorithms

### Mark

Identify reachable objects.

### Sweep

Reclaim memory occupied by unreachable objects.

### Copy / Evacuate

Move live objects to another memory region and reclaim the source region.

### Compact

Move live objects together to reduce fragmentation.

### Stop-the-World

Application threads are paused while certain GC operations execute.

### Concurrent GC

GC performs significant work while application threads continue running.

# 21. Different Garbage Collectors in Java

The major HotSpot collectors are:

1. Serial GC.
2. Parallel GC.
3. G1 GC.
4. ZGC.
5. Shenandoah GC.
6. Epsilon GC.

## 21.1 Serial GC

Serial GC uses a single thread for garbage collection.

Enable:

```
java -XX:+UseSerialGC -jar application.jar
```

Characteristics:

- Simple implementation.
- Low overhead for small heaps.
- Stop-the-world collection.
- Not suitable for applications requiring high concurrency or low pause times.

Use cases:

- Small applications.
- Small heap environments.
- Simple batch jobs.

## 21.2 Parallel GC

Parallel GC uses multiple threads for garbage collection.

Enable:

```
java -XX:+UseParallelGC -jar application.jar
```

Characteristics:

- Multiple GC threads.
- High throughput.
- Stop-the-world collection.
- Suitable when throughput matters more than pause latency.

Use cases:

- Batch processing.
- Data transformation.
- High-throughput jobs.
- Applications where longer pauses are acceptable.

## 21.3 G1 Garbage Collector

G1 stands for Garbage First.

It divides the heap into multiple regions instead of relying on one large contiguous young/old layout.

Enable:

```
java -XX:+UseG1GC -jar application.jar
```

G1 characteristics:

- Region-based heap management.
- Generational collection.
- Parallel and concurrent phases.
- Mixed collections.
- Pause-time goal.
- Suitable for general-purpose server applications.

### G1 configuration example

```
java \
  -XX:+UseG1GC \
  -Xms2g \
  -Xmx2g \
  -XX:MaxGCPauseMillis=200 \
  -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags \
  -jar application.jar
```

Important options:

| Option                        | Purpose                      |
| ----------------------------- | ---------------------------- |
| `-XX:+UseG1GC`                | Enable G1                    |
| `-Xms2g`                      | Initial heap size            |
| `-Xmx2g`                      | Maximum heap size            |
| `-XX:MaxGCPauseMillis=200`    | Soft pause-time goal         |
| `-Xlog:gc*`                   | Enable GC logging            |
| `-XX:+UseStringDeduplication` | Deduplicate eligible strings |

G1's pause-time goal is not a hard guarantee.

## 21.4 ZGC

ZGC is designed for low-latency garbage collection.

Enable:

```
java \
  -XX:+UseZGC \
  -Xms4g \
  -Xmx4g \
  -Xlog:gc* \
  -jar application.jar
```

Characteristics:

- Concurrent collection.
- Low pause times.
- Suitable for large heaps.
- Designed for latency-sensitive workloads.
- Uses additional CPU and memory headroom for concurrent work.

Generational ZGC became a product feature in Java 21 and the default ZGC mode in later releases.

Use cases:

- High-volume REST APIs.
- Financial transaction applications.
- Low-latency microservices.
- Applications with strict p99/p99.9 latency requirements.

## 21.5 Shenandoah GC

Shenandoah is another low-pause collector.

Enable:

```
java \
  -XX:+UseShenandoahGC \
  -Xms4g \
  -Xmx4g \
  -Xlog:gc* \
  -jar application.jar
```

Characteristics:

- Concurrent evacuation.
- Low pause-time design.
- Suitable for latency-sensitive applications.
- Availability depends on the JDK distribution.

Check support in your exact JDK before using it.

## 21.6 Epsilon GC

Epsilon is a no-op garbage collector.

It allocates memory but does not reclaim it.

Enable:

```
java \
  -XX:+UseEpsilonGC \
  -Xms256m \
  -Xmx256m \
  -jar allocation-test.jar
```

Use cases:

- Allocation benchmarking.
- Controlled short-lived testing.
- Understanding memory allocation behaviour.

Do not use Epsilon for a normal long-running production service.

# 22. Which GC Should I Choose?

| Requirement                                        | Recommended starting point |
| -------------------------------------------------- | -------------------------- |
| Small application                                  | Serial                     |
| High throughput                                    | Parallel                   |
| General enterprise application                     | G1                         |
| Low-latency application                            | ZGC                        |
| Low-latency application on supporting distribution | Shenandoah                 |
| Allocation benchmark                               | Epsilon                    |

For most Spring Boot enterprise microservices, start with G1.

Evaluate ZGC or Shenandoah if measured latency requirements justify the change.

Never select a GC only because it is newer.

# 23. How to Configure GC in Spring Boot

GC configuration is supplied to the JVM, not through Spring application properties.

Example:

```
java \
  -Xms1g \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Xlog:gc*,safepoint:file=logs/gc.log:time,uptime,level,tags \
  -jar payment-service.jar
```

For ZGC:

```
java \
  -Xms2g \
  -Xmx2g \
  -XX:+UseZGC \
  -Xlog:gc*,safepoint:file=logs/gc.log:time,uptime,level,tags \
  -jar payment-service.jar
```

For Shenandoah:

```
java \
  -Xms2g \
  -Xmx2g \
  -XX:+UseShenandoahGC \
  -Xlog:gc*,safepoint:file=logs/gc.log:time,uptime,level,tags \
  -jar payment-service.jar
```

Remember that the collector must be supported by the deployed JDK.

# 24. GC Configuration in Kubernetes

Example deployment:

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-service

spec:
  replicas: 3

  selector:
    matchLabels:
      app: payment-service

  template:
    metadata:
      labels:
        app: payment-service

    spec:
      containers:
        - name: payment-service

          image: payment-service:latest

          resources:
            requests:
              memory: "1Gi"
              cpu: "500m"

            limits:
              memory: "2Gi"
              cpu: "2"

          env:
            - name: JAVA_TOOL_OPTIONS
              value: >-
                -XX:+UseG1GC
                -XX:InitialRAMPercentage=25
                -XX:MaxRAMPercentage=60
                -Xlog:gc*,safepoint:stdout:time,uptime,level,tags
```

Important:

The container memory limit includes more than the Java heap.

It must also accommodate:

- Metaspace.
- Thread stacks.
- Direct buffers.
- JIT code cache.
- Native libraries.
- GC native structures.
- Other JVM overhead.

Do not set `-Xmx` equal to the container's entire memory limit.

# 25. GC Logging and Monitoring

## 25.1 Enable GC Logs

Java 9 and later:

```
-Xlog:gc*
```

Write to a file:

```
-Xlog:gc*:file=gc.log:time,uptime,level,tags
```

Rolling logs:

```
-Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags:filecount=5,filesize=20M
```

## 25.2 Check JVM Flags

```
java -XX:+PrintCommandLineFlags -version
```

## 25.3 Inspect Running JVM

```
jcmd <pid> VM.flags
```

```
jcmd <pid> VM.command_line
```

## 25.4 Check Heap Information

```
jcmd <pid> GC.heap_info
```

## 25.5 Monitor GC Statistics

```
jstat -gcutil <pid> 1000
```

This samples GC statistics every second.

## 25.6 Generate Heap Dump

```
jcmd <pid> GC.heap_dump /tmp/heap.hprof
```

Open the heap dump using Eclipse Memory Analyzer (MAT).

Heap dumps can contain sensitive customer data and credentials. Protect them accordingly.

## 25.7 Java Flight Recorder

Start a recording:

```
jcmd <pid> JFR.start \
  name=production \
  settings=profile \
  duration=5m \
  filename=/tmp/application.jfr
```

JFR helps analyze:

- Object allocation.
- GC activity.
- CPU usage.
- Thread activity.
- Lock contention.
- Method execution.

Use Java Mission Control to inspect the recording.

# 26. GC Troubleshooting

## 26.1 OutOfMemoryError: Java heap space

Possible reasons:

- Memory leak.
- Large collections.
- Unbounded cache.
- Too many retained objects.
- Insufficient heap size.

Commands:

```
jcmd <pid> GC.heap_info

jcmd <pid> GC.class_histogram

jcmd <pid> GC.heap_dump /tmp/heap.hprof
```

## 26.2 OutOfMemoryError: Metaspace

Possible reasons:

- Excessive class loading.
- Class-loader leaks.
- Runtime-generated classes.

Inspect:

```
jcmd <pid> VM.metaspace
```

Optional limit:

```
-XX:MaxMetaspaceSize=512m
```

Do not apply an arbitrary cap without measuring usage.

## 26.3 OutOfMemoryError: Direct buffer memory

Possible reasons:

- Excessive direct buffer allocation.
- NIO/Netty buffer retention.
- Network workload pressure.

Inspect native memory and direct-buffer usage.

## 26.4 Kubernetes OOMKilled

Possible reasons:

- Container memory limit exceeded.
- Heap too large.
- Excessive native memory.
- Too many threads.
- Direct memory usage.
- Sidecar/container resource pressure.

Monitor:

- Container RSS.
- JVM heap.
- Metaspace.
- Thread count.
- Direct memory.
- GC logs.

# 27. Garbage Collection Evolution – Java 8 to 25

| Java | Important GC changes                                                                  |
| ---- | ------------------------------------------------------------------------------------- |
| 8    | G1 available; Parallel commonly used as server default; PermGen replaced by Metaspace |
| 9    | G1 default in typical server configurations; CMS deprecated                           |
| 10   | Parallel full GC for G1                                                               |
| 11   | ZGC experimental; Epsilon experimental                                                |
| 12   | Shenandoah experimental in OpenJDK                                                    |
| 13   | ZGC returns unused committed memory more promptly                                     |
| 14   | CMS removed; G1 improvements                                                          |
| 15   | ZGC and Shenandoah production features in supporting builds                           |
| 16   | Elastic Metaspace                                                                     |
| 17   | G1, ZGC and Shenandoah improvements                                                   |
| 18   | Continued GC and JVM improvements                                                     |
| 19   | Generational ZGC experimental                                                         |
| 20   | Generational ZGC development continued                                                |
| 21   | Generational ZGC product feature                                                      |
| 22   | G1 region-pinning improvements                                                        |
| 23   | Generational ZGC default mode                                                         |
| 24   | Generational Shenandoah                                                               |
| 25   | Compact Object Headers product feature; G1 improvements                               |

# 28. Important JVM Performance Enhancements

Apart from GC, several JVM improvements have been introduced.

| Feature                | Release                     | Purpose                                  |
| ---------------------- | --------------------------- | ---------------------------------------- |
| Metaspace              | 8                           | Replaces PermGen                         |
| Modular runtime        | 9                           | Smaller and modular JDK                  |
| Unified logging        | 9                           | Standard JVM logging                     |
| AppCDS improvements    | 10                          | Startup and memory improvements          |
| Flight Recorder        | 11                          | Runtime diagnostics                      |
| Hidden classes         | 15                          | Framework/runtime class generation       |
| Elastic Metaspace      | 16                          | Better native metadata memory management |
| Virtual threads        | 21                          | Lightweight concurrency                  |
| Generational ZGC       | 21                          | Low-latency generational collection      |
| Foreign Memory API     | 22                          | Native/off-heap access                   |
| Compact Object Headers | 24 experimental; 25 product | Reduce object-header overhead            |

# 29. Java 8 vs Java 17 vs Java 21 vs Java 25

| Capability            | Java 8 | Java 17   | Java 21 | Java 25 |
| --------------------- | ------ | --------- | ------- | ------- |
| Lambda                | Yes    | Yes       | Yes     | Yes     |
| Streams               | Yes    | Yes       | Yes     | Yes     |
| Optional              | Yes    | Yes       | Yes     | Yes     |
| var                   | No     | Yes       | Yes     | Yes     |
| Records               | No     | Yes       | Yes     | Yes     |
| Sealed classes        | No     | Yes       | Yes     | Yes     |
| Switch expressions    | No     | Yes       | Yes     | Yes     |
| Pattern switch        | No     | Preview   | Yes     | Yes     |
| Virtual threads       | No     | No        | Yes     | Yes     |
| Sequenced collections | No     | No        | Yes     | Yes     |
| Foreign Memory API    | No     | Incubator | Preview | Final   |
| Stream Gatherers      | No     | No        | No      | Final   |
| Scoped Values         | No     | No        | Preview | Final   |
| Module imports        | No     | No        | No      | Final   |
| Compact source files  | No     | No        | Preview | Final   |

# 30. Enterprise Java Migration Recommendations

## 30.1 Moving from Java 8 to Java 17

Check:

- Spring Boot version compatibility.
- Third-party library compatibility.
- Removed Java EE and CORBA modules.
- Nashorn usage.
- Reflection and internal JDK APIs.
- TLS configuration.
- GC configuration.
- Build plugins.

## 30.2 Moving from Java 17 to Java 21

Focus on:

- Virtual threads.
- Pattern matching for switch.
- Record patterns.
- Sequenced collections.
- Generational ZGC.
- Structured Concurrency preview.
- Scoped Values preview.

## 30.3 Moving from Java 21 to Java 25

Focus on:

- Scoped Values.
- Compact source files and instance main methods.
- Module Import Declarations.
- Flexible Constructor Bodies.
- Key Derivation Function API.
- Stream Gatherers (already final in Java 24).
- Compact Object Headers.
- New preview features and API compatibility.

## 30.4 Maven Compiler Configuration

For Java 21:

```
<properties>
    <maven.compiler.release>21</maven.compiler.release>
</properties>
```

For Java 25:

```
<properties>
    <maven.compiler.release>25</maven.compiler.release>
</properties>
```

Use `--release` or Maven's `maven.compiler.release` rather than relying only on source and target settings.

# 31. Recommended Learning Path

For an experienced Java developer, I recommend the following order.

### Phase 1 – Java 8

- Lambda expressions.
- Functional interfaces.
- Streams.
- Optional.
- CompletableFuture.
- Date and Time API.

### Phase 2 – Java 9 to 11

- Module system.
- var.
- Collection factory methods.
- HTTP Client.
- String enhancements.
- Files enhancements.
- Unified logging.

### Phase 3 – Java 14 to 17

- Switch expressions.
- Records.
- Sealed classes.
- Pattern matching.
- JFR.

### Phase 4 – Java 19 to 21

- Virtual threads.
- Record patterns.
- Pattern matching for switch.
- Sequenced collections.
- Generational ZGC.

### Phase 5 – Java 22 to 25

- Foreign Function & Memory API.
- Stream Gatherers.
- Scoped Values.
- Module Import Declarations.
- Flexible Constructor Bodies.
- Compact Object Headers.

### Phase 6 – JVM Internals

- Heap memory.
- Metaspace.
- Stack memory.
- GC algorithms.
- G1.
- ZGC.
- Shenandoah.
- GC logging.
- JFR.
- Heap dumps.
- Native memory.
- Kubernetes memory management.

# 32. Official References

Use these official resources for detailed JEP descriptions, exact syntax, compatibility and JVM flags.

1. [Oracle Java Documentation](https://docs.oracle.com/en/java/javase/?utm_source=chatgpt.com)
2. [Java 25 Language Changes](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-release.html?utm_source=chatgpt.com)
3. [Java 25 Release Notes](https://www.oracle.com/java/technologies/javase/25-relnote-issues.html?utm_source=chatgpt.com)
4. [OpenJDK JEP Index](https://openjdk.org/jeps/0?utm_source=chatgpt.com)
5. [Java 25 Garbage Collection Tuning Guide](https://docs.oracle.com/en/java/javase/25/gctuning/index.html?utm_source=chatgpt.com)
6. [Java 25 API Documentation](https://docs.oracle.com/en/java/javase/25/docs/api/?utm_source=chatgpt.com)
7. [Java Flight Recorder](https://docs.oracle.com/en/java/javase/25/jfapi/?utm_source=chatgpt.com)

# Conclusion

Java has evolved significantly from Java 8 to Java 25.

The biggest changes to understand are:

1. Java 8: Functional programming, Streams and CompletableFuture.
2. Java 9: Modules and unified logging.
3. Java 10: var.
4. Java 11: Standard HTTP Client and String enhancements.
5. Java 14–17: Records, sealed classes, switch expressions and pattern matching.
6. Java 21: Virtual threads and modern concurrency.
7. Java 22: Foreign Function & Memory API.
8. Java 24: Stream Gatherers.
9. Java 25: Scoped Values, module imports and compact source files.

For enterprise microservices, Java 21 and Java 25 are particularly important releases to understand.

My recommendation: Learn Java 8–17 thoroughly, then spend additional time on Java 21 virtual threads, Java 25 Scoped Values and JVM garbage collection. These are especially valuable for modern enterprise application architecture.

End of document.
