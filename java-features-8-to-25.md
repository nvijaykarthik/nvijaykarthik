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




# Java 8–25: Complete Feature Guide

## How to read this guide

For every feature, understand these six things:

- **What:** What the feature does.
- **Why introduced:** What problem existed before it.
- **Advantages:** What benefits it provides.
- **When to use:** Practical scenarios where it is useful.
- **How to use:** Syntax and code example.
- **Before vs. after:** How the new approach compares with the old approach.

> Note: This guide focuses on important developer-facing features, rather than every internal JVM change or every individual JEP. Preview and incubator features are identified separately.

---

# Java 8

Java 8 was a major release that introduced functional programming concepts into Java.

## 1. Lambda Expressions

**What:** A lambda expression allows you to pass behavior (a block of code) as an argument without creating an anonymous class.

**Why introduced:** Before Java 8, implementing small pieces of behavior required verbose anonymous inner classes. This made collection processing and callback-based programming unnecessarily lengthy.

**Advantages:**
- Less boilerplate code.
- Easier collection processing.
- Supports functional programming.
- Makes behavior reusable and composable.

**When to use:**
- Collection sorting and filtering.
- Event handling and callbacks.
- Thread execution.
- Stream operations.

**How to use:**

Before Java 8:
```java
Runnable task = new Runnable() {
    @Override
    public void run() {
        System.out.println("Processing");
    }
};
```

Java 8:
```java
Runnable task = () ->
    System.out.println("Processing");

new Thread(task).start();
```

Collection example:
```java
List<String> names =
    Arrays.asList("John", "David", "Alex");

names.forEach(name ->
    System.out.println(name));
```

## 2. Functional Interfaces

**What:** An interface containing exactly one abstract method. It can be implemented using a lambda expression.

**Why introduced:** Lambda expressions needed a standard target type to represent behavior in a type-safe way.

**Advantages:**
- Enables lambda expressions.
- Encourages reusable behavior.
- Simplifies callbacks and functional APIs.

**When to use:**
- When passing behavior into methods.
- When creating reusable validation or processing logic.
- When working with streams and asynchronous operations.

**How to use:**
```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

public class Main {
    public static void main(String[] args) {
        Calculator addition = (a, b) -> a + b;

        System.out.println(addition.calculate(10, 20));
    }
}
```

Important built-in functional interfaces:

| Interface | Purpose |
|---|---|
| `Predicate<T>` | Tests a condition and returns boolean |
| `Function<T,R>` | Converts one value into another |
| `Consumer<T>` | Accepts a value and performs an action |
| `Supplier<T>` | Supplies a value |
| `BiFunction<T,U,R>` | Accepts two values and returns a result |

Example:
```java
Predicate<Integer> isEven =
    number -> number % 2 == 0;

System.out.println(isEven.test(10));
```

## 3. Stream API

**What:** Streams allow you to process collections of data using operations such as filter, map, sort and collect.

**Why introduced:** Traditional collection processing often required loops, temporary collections and repetitive conditional logic.

**Advantages:**
- More readable data-processing code.
- Supports functional-style operations.
- Supports parallel processing.
- Separates what you want to do from how iteration happens.

**When to use:**
- Filtering customer or transaction records.
- Transforming DTOs.
- Aggregating data.
- Sorting and grouping collections.

**How to use:**

Before Java 8:
```java
List<Integer> result = new ArrayList<>();

for (Integer number : numbers) {
    if (number > 10) {
        result.add(number);
    }
}
```

Java 8:
```java
List<Integer> result = numbers.stream()
    .filter(number -> number > 10)
    .collect(Collectors.toList());
```

More operations:
```java
List<String> names = List.of("John", "David", "Alex");

List<String> result = names.stream()
    .filter(name -> name.startsWith("A"))
    .map(String::toUpperCase)
    .sorted()
    .collect(Collectors.toList());
```

**Important:** Streams do not automatically make processing faster. Use parallel streams only after considering data size, workload and thread-safety.

## 4. Method References

**What:** A shorter way to refer to an existing method using `::`.

**Why introduced:** Lambda expressions sometimes contain nothing more than a call to an existing method.

**Advantages:**
- Less code.
- Better readability.
- Reuses existing methods.

**When to use:** When a lambda simply forwards its input to another method.

**How to use:**
```java
List<String> names =
    Arrays.asList("John", "David", "Alex");

// Lambda
names.forEach(name -> System.out.println(name));

// Method reference
names.forEach(System.out::println);
```

## 5. Default and Static Methods in Interfaces

**What:** Interfaces can contain methods with implementations.

**Why introduced:** Adding a new abstract method to an existing interface could break existing implementations. Default methods allow interfaces to evolve more safely.

**Advantages:**
- Supports interface evolution.
- Reduces duplicated implementation.
- Allows utility methods inside interfaces.

**When to use:**
- Adding common behavior to an interface.
- Evolving shared APIs.
- Providing interface-level utility methods.

**How to use:**
```java
interface PaymentService {

    void processPayment();

    default void validatePayment() {
        System.out.println("Validating payment");
    }

    static void audit() {
        System.out.println("Payment audit");
    }
}
```

## 6. Optional

**What:** A container that may or may not contain a value.

**Why introduced:** Null values frequently caused `NullPointerException`. Optional makes the possibility of a missing result explicit.

**Advantages:**
- Makes missing values visible in APIs.
- Encourages explicit handling.
- Reduces accidental null dereferencing.

**When to use:**
- Repository methods that may not find a record.
- Service methods returning optional results.
- Avoiding ambiguous null return contracts.

**How to use:**
```java
Optional<String> customer =
    Optional.ofNullable(findCustomer());

String name = customer.orElse("Unknown");
```

Better:
```java
Optional<Customer> customer =
    customerRepository.findById(100L);

customer.ifPresent(System.out::println);
```

Avoid using Optional indiscriminately for every field, method parameter or entity property.

## 7. New Date and Time API

**What:** The `java.time` package provides modern date and time classes.

**Why introduced:** Older `Date` and `Calendar` APIs were difficult to use, mutable and confusing around time zones.

**Advantages:**
- Immutable date/time objects.
- Better time-zone handling.
- Clear separation of date, time and timestamp concepts.
- Easier calculations.

**When to use:**
- Payment timestamps.
- Transaction dates.
- Business-day calculations.
- Time-zone conversion.

**How to use:**
```java
LocalDate date = LocalDate.now();

LocalDate paymentDate =
    LocalDate.of(2026, 11, 2);

LocalDate nextWeek =
    paymentDate.plusDays(7);

ZonedDateTime indiaTime =
    ZonedDateTime.now(
        ZoneId.of("Asia/Kolkata"));
```

## 8. CompletableFuture

**What:** An API for asynchronous computation and combining results from multiple tasks.

**Why introduced:** Traditional `Future` allowed retrieving a result, but offered limited support for chaining asynchronous operations and combining tasks.

**Advantages:**
- Asynchronous execution.
- Chaining operations.
- Combining independent tasks.
- Centralized exception handling.

**When to use:**
- Calling multiple independent APIs.
- Parallel service calls.
- Background processing.
- Combining results from different systems.

**How to use:**
```java
CompletableFuture<String> future =
    CompletableFuture.supplyAsync(() -> "Payment received");

future.thenApply(String::toUpperCase)
      .thenAccept(System.out::println);
```

Combining two calls:
```java
CompletableFuture<String> customer =
    CompletableFuture.supplyAsync(() -> "Customer A");

CompletableFuture<Integer> balance =
    CompletableFuture.supplyAsync(() -> 5000);

CompletableFuture<Void> combined =
    customer.thenCombine(balance,
        (name, amount) -> name + " : " + amount)
    .thenAccept(System.out::println);
```

## 9. Parallel Streams

**What:** Allows stream operations to execute using multiple threads, typically through the common ForkJoinPool.

**Why introduced:** Processing large collections sequentially can be slow when the work can be divided across CPU cores.

**Advantages:**
- Potentially faster CPU-intensive processing.
- Less manual thread-management code.

**When to use:**
- Large independent datasets.
- CPU-intensive operations.
- Operations without shared mutable state.

**How to use:**
```java
List<Integer> numbers =
    IntStream.rangeClosed(1, 1_000_000)
        .boxed()
        .collect(Collectors.toList());

long count = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .count();
```

Do not use parallel streams blindly for database calls or network requests. They can overload shared resources and introduce concurrency problems.

---

# Java 9

## 1. Java Platform Module System

**What:** Introduced modules to organize applications into explicit groups of packages and dependencies.

**Why introduced:** Large applications had difficulty managing dependencies and exposing internal implementation details. The JDK itself also needed stronger modular organization.

**Advantages:**
- Explicit dependencies.
- Strong encapsulation.
- Better application structure.
- Smaller custom runtime images with `jlink`.

**When to use:**
- Large applications with clear module boundaries.
- Reusable platform libraries.
- Applications where strong encapsulation is important.

**How to use:**

`module-info.java`:
```java
module com.example.payment {
    requires java.sql;

    exports com.example.payment.api;
}
```

This declares a dependency on `java.sql` and exposes the API package.

## 2. JShell

**What:** An interactive Java shell for executing Java statements without creating a complete class and application.

**Why introduced:** Developers needed a faster way to experiment with Java expressions and APIs.

**Advantages:**
- Quick experimentation.
- Easier learning.
- Faster testing of small code snippets.

**When to use:** Testing expressions, exploring APIs and demonstrating Java concepts.

**How to use:**
```text
jshell

int amount = 1000;
int tax = 100;

System.out.println(amount + tax);
```

## 3. Factory Methods for Collections

**What:** Java 9 introduced convenience factory methods such as `List.of()`, `Set.of()` and `Map.of()`.

**Why introduced:** Creating small collections previously required multiple statements and sometimes mutable collections.

**Advantages:**
- Concise initialization.
- Unmodifiable collections.
- Less boilerplate.

**When to use:** Fixed configuration lists, lookup values and constant mappings.

**How to use:**
```java
List<String> currencies =
    List.of("INR", "USD", "EUR");

Set<String> statuses =
    Set.of("SUCCESS", "FAILED");

Map<String, Integer> limits =
    Map.of("BASIC", 1000, "PREMIUM", 5000);
```

These collections are unmodifiable. They are not replacements for mutable collections when updates are required.

## 4. Private Interface Methods

**What:** Interfaces can contain private helper methods used by their default and static methods.

**Why introduced:** Default methods could otherwise contain duplicated internal logic.

**Advantages:** Reuses implementation inside an interface without exposing helper methods publicly.

**When to use:** When multiple default methods share internal logic.

**How to use:**
```java
interface Payment {
    default void validate() {
        log("Validating payment");
    }

    default void process() {
        log("Processing payment");
    }

    private void log(String message) {
        System.out.println(message);
    }
}
```

## 5. Stream API Improvements

**What:** Java 9 added methods such as `takeWhile`, `dropWhile`, `ofNullable` and `iterate` with a termination condition.

**Why introduced:** Stream pipelines needed more convenient ways to process sequences and handle nullable inputs.

**Advantages:** More expressive stream operations and less manual filtering.

**When to use:** Ordered data processing, nullable stream sources and bounded iteration.

**How to use:**
```java
List<Integer> numbers =
    List.of(1, 2, 3, 4, 5, 1);

numbers.stream()
    .takeWhile(n -> n < 4)
    .forEach(System.out::println);
```

`takeWhile` stops at the first element that fails the condition for an ordered stream.

---

# Java 10

## 1. Local Variable Type Inference (`var`)

**What:** Allows the compiler to infer a local variable's type from its initializer.

**Why introduced:** Repeating long generic type declarations made code verbose and harder to read.

**Advantages:**
- Less repetitive code.
- Cleaner declarations.
- Type safety is retained because Java remains statically typed.

**When to use:** Local variables where the type is obvious from the expression.

**How to use:**
```java
var customerName = "John";
var amount = 1000;
var customers = new ArrayList<String>();
```

Without `var`:
```java
ArrayList<String> customers =
    new ArrayList<String>();
```

Important restrictions:
```java
// Invalid: no initializer
// var value;

// Invalid: cannot use var as a field type
// private var name;
```

Use descriptive variable names. Avoid `var` when it hides the meaning of a complex expression.

## 2. Application Class-Data Sharing (AppCDS)

**What:** Extends class-data sharing capabilities to application classes.

**Why introduced:** JVM startup can be slower when classes must repeatedly be loaded and processed.

**Advantages:**
- Can reduce startup time.
- Can reduce memory footprint through shared class metadata.

**When to use:** Applications with frequent JVM startups or many application classes, after measuring the benefit.

**How to use:**
```bash
java -Xshare:dump
```

For application-specific archives, create and use an AppCDS archive with the appropriate `-Xshare` and `-XX:ArchiveClassesAtExit` JVM options. The exact workflow depends on the application and JDK version.

---

# Java 11

Java 11 is an LTS release and introduced useful APIs and runtime improvements.

## 1. HTTP Client API

**What:** Standard HTTP client in `java.net.http`.

**Why introduced:** Older HTTP communication approaches often required external libraries or the older `HttpURLConnection` API.

**Advantages:**
- Standard Java API.
- Synchronous and asynchronous requests.
- HTTP/2 support.
- WebSocket support.

**When to use:** REST API integrations and HTTP communication between microservices.

**How to use:**
```java
HttpClient client = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://example.com/api"))
    .GET()
    .build();

HttpResponse<String> response =
    client.send(request,
        HttpResponse.BodyHandlers.ofString());

System.out.println(response.body());
```

Asynchronous:
```java
client.sendAsync(request,
        HttpResponse.BodyHandlers.ofString())
    .thenApply(HttpResponse::body)
    .thenAccept(System.out::println);
```

## 2. String API Improvements

**What:** Java 11 introduced methods including `isBlank`, `strip`, `stripLeading`, `stripTrailing`, `repeat`, `lines` and `formatted`.

**Why introduced:** Common string operations required extra utility code.

**Advantages:** Cleaner and more readable string manipulation.

**When to use:** Input validation, text processing, formatting and message generation.

**How to use:**
```java
String value = "  Java  ";

System.out.println(value.isBlank());
System.out.println(value.strip());
System.out.println("Java ".repeat(3));
```

## 3. Files API Improvements

**What:** Java 11 introduced convenient methods for reading and writing strings.

**Why introduced:** Simple file operations previously required more boilerplate.

**Advantages:** Easier text-file handling.

**When to use:** Configuration files, small text files and simple file-based integrations.

**How to use:**
```java
Path path = Path.of("message.txt");

Files.writeString(path, "Payment successful");

String content = Files.readString(path);

System.out.println(content);
```

## 4. Running Java Source Files Directly

**What:** Run a single source file without explicitly compiling it first.

**Why introduced:** Reduce friction when running small programs and scripts.

**Advantages:** Faster experimentation and simpler command-line utilities.

**When to use:** Small utilities, learning and scripts.

**How to use:**
```bash
java Hello.java
```

---

# Java 12

## 1. Switch Expressions (Preview in Java 12)

**What:** Allows a `switch` expression to return a value.

**Why introduced:** Traditional switch statements required repetitive assignments, `break` statements and careful fall-through handling.

**Advantages:**
- Concise decision logic.
- Fewer accidental fall-through bugs.
- Directly returns a value.

**When to use:** Mapping statuses, transaction types or categories to results.

**How to use:**

Modern form:
```java
String result = switch (status) {
    case "SUCCESS" -> "Completed";
    case "FAILED" -> "Rejected";
    default -> "Pending";
};
```

This feature became permanent in Java 14. Java 12 and 13 versions required preview-feature enablement.

---

# Java 13

## 1. Text Blocks (Preview)

**What:** Multiline string literals using triple quotes.

**Why introduced:** Writing multiline SQL, JSON, XML and HTML using normal string literals required many escape characters and concatenations.

**Advantages:**
- More readable multiline strings.
- Less escaping.
- Easier maintenance of embedded SQL and JSON.

**When to use:** SQL queries, JSON payloads, XML and multiline templates.

**How to use:**
```java
String json = """
    {
      "customer": "ABXYSE",
      "status": "SUCCESS"
    }
    """;
```

Text blocks became permanent in Java 15.

---

# Java 14

## 1. Switch Expressions (Permanent)

**What:** Switch can now return values using arrow labels.

**Why introduced:** To make conditional expressions more concise and safer.

**Advantages:** Less boilerplate and fewer fall-through mistakes.

**When to use:** Mapping one input to one result.

**How to use:**
```java
int fee = switch (paymentType) {
    case "NEFT" -> 2;
    case "RTGS" -> 25;
    case "IMPS" -> 5;
    default -> throw new IllegalArgumentException();
};
```

For multiple statements, use `yield`:
```java
int fee = switch (paymentType) {
    case "RTGS" -> {
        System.out.println("High-value transfer");
        yield 25;
    }
    default -> 5;
};
```

## 2. Helpful NullPointerExceptions

**What:** JVM provides more detailed information about which expression caused a null dereference.

**Why introduced:** Traditional NullPointerExceptions often did not clearly identify which reference in a complex expression was null.

**Advantages:**
- Faster debugging.
- Easier root-cause analysis.
- Less time spent tracing nested expressions.

**When to use:** Debugging production and development failures.

**How to use:**
```java
customer.getAccount().getAddress().getCity();
```

When one of these references is null, newer JVMs can provide a more specific diagnostic message.

## 3. Records (Preview)

**What:** A compact way to create classes whose primary purpose is carrying data.

**Why introduced:** Data-carrier classes required repetitive fields, constructors, getters, `equals`, `hashCode` and `toString`.

**Advantages:**
- Less boilerplate.
- Clear data structure.
- Generated accessors and object methods.
- Shallow immutability.

**When to use:** DTOs, API response models, projections and immutable data carriers.

**How to use:**
```java
record Payment(
    String transactionId,
    double amount,
    String status
) {}

Payment payment =
    new Payment("TXN1001", 5000, "SUCCESS");

System.out.println(payment.amount());
```

Records became permanent in Java 16.

---

# Java 15

## 1. Text Blocks (Permanent)

**What:** Multiline strings using `"""`.

**Why introduced:** To make embedded multiline text easier to write and maintain.

**Advantages:** Cleaner SQL, JSON, XML and HTML strings.

**When to use:** Embedded textual content.

**How to use:**
```java
String sql = """
    SELECT *
    FROM PAYMENT
    WHERE STATUS = 'FAILED'
    """;
```

## 2. Sealed Classes (Preview)

**What:** Allows a class or interface to specify which types can extend or implement it.

**Why introduced:** Traditional inheritance allowed any accessible class to extend a non-final class. Some domain models need controlled inheritance.

**Advantages:**
- Controlled class hierarchies.
- Better domain modelling.
- Helps exhaustive pattern matching.

**When to use:** Payment types, account categories, transaction outcomes and other fixed domain hierarchies.

**How to use:**
```java
sealed interface PaymentResult
    permits Success, Failure {}

final class Success implements PaymentResult {}

final class Failure implements PaymentResult {}
```

Sealed classes became permanent in Java 17.

## 3. Hidden Classes

**What:** Classes intended for use by frameworks and runtime-generated code, rather than ordinary application code.

**Why introduced:** Frameworks and dynamic language runtimes needed a better mechanism for generating classes that should not be discovered and used like normal classes.

**Advantages:**
- Supports framework internals.
- Better lifecycle management for generated classes.
- Useful for dynamic runtime implementations.

**When to use:** Usually framework or library developers, not normal business application code.

**How to use:** Typically accessed through APIs such as `MethodHandles.Lookup.defineHiddenClass`; most application developers should let their framework handle this.

---

# Java 16

## 1. Records (Permanent)

**What:** Records are concise data-carrier classes.

**Why introduced:** To remove repetitive code from classes whose main purpose is holding data.

**Advantages:** Less boilerplate, clear data models and generated accessors.

**When to use:** DTOs, immutable request objects and query results.

**How to use:**
```java
public record Customer(
    Long id,
    String name,
    String category
) {}
```

## 2. Pattern Matching for `instanceof`

**What:** Combines type checking and casting in one operation.

**Why introduced:** Traditional `instanceof` required a separate cast after checking the type.

**Advantages:**
- Less code.
- Avoids repeated casts.
- Improves readability.

**When to use:** Processing objects whose runtime type may vary.

**How to use:**

Before:
```java
if (obj instanceof String) {
    String value = (String) obj;
    System.out.println(value.length());
}
```

After:
```java
if (obj instanceof String value) {
    System.out.println(value.length());
}
```

## 3. Stream `toList()`

**What:** A convenient terminal operation that collects stream results into an unmodifiable list.

**Why introduced:** The common `collect(Collectors.toList())` operation was verbose for simple collection creation.

**Advantages:** Cleaner code.

**When to use:** Creating a list from a stream when you do not need to modify the result.

**How to use:**
```java
List<String> names =
    Stream.of("John", "David", "Alex")
        .filter(name -> name.startsWith("A"))
        .toList();
```

The returned list is unmodifiable.

## 4. Unix-Domain Socket Channels

**What:** Supports communication between processes on the same machine through Unix-domain sockets.

**Why introduced:** Local processes often needed efficient communication without using network TCP sockets.

**Advantages:** Efficient local inter-process communication.

**When to use:** Specialized local services and infrastructure integrations.

**How to use:** Use `StandardProtocolFamily.UNIX` with the Java NIO socket channel APIs.

---

# Java 17

Java 17 is an LTS release with important language and runtime improvements.

## 1. Sealed Classes (Permanent)

**What:** Restricts which classes can extend a class or implement an interface.

**Why introduced:** To provide controlled inheritance and model a known set of alternatives.

**Advantages:**
- Better domain modelling.
- Explicit inheritance rules.
- Safer use with pattern matching.

**When to use:** Fixed business hierarchies such as payment outcomes or account types.

**How to use:**
```java
sealed interface Payment
    permits CardPayment, BankPayment {}

final class CardPayment implements Payment {}

final class BankPayment implements Payment {}
```

## 2. Enhanced Pseudo-Random Number Generators

**What:** Introduces a more flexible API for working with random-number generator algorithms.

**Why introduced:** The older random APIs offered less flexibility when selecting and using different algorithms.

**Advantages:** Better algorithm selection and a consistent API.

**When to use:** Simulations, randomized testing and non-security-sensitive random values.

**How to use:**
```java
RandomGenerator generator =
    RandomGenerator.getDefault();

int value = generator.nextInt(100);
```

For security-sensitive values, use `SecureRandom`, not a general-purpose generator.

## 3. Strong Encapsulation of JDK Internals

**What:** Makes many internal JDK APIs inaccessible by default.

**Why introduced:** Applications and frameworks had become dependent on internal implementation details that could change between JDK releases.

**Advantages:**
- Better platform integrity.
- More maintainable JDK.
- Encourages use of supported APIs.

**When to use:** Important when migrating legacy applications or upgrading frameworks.

**How to use:** Replace internal APIs with supported public APIs. Temporary migration options such as `--add-opens` may be needed for older libraries, but should not be treated as a permanent solution.

## 4. Foreign Function and Memory API (Incubator)

**What:** Provides an API for Java applications to interact with native code and memory outside the Java heap.

**Why introduced:** JNI can be complex and unsafe to use.

**Advantages:** More modern native interoperability and memory access.

**When to use:** Specialized native integrations, high-performance libraries and system-level applications.

**How to use:** The API was incubating in Java 17 and evolved in later releases. Use the API appropriate to your target JDK rather than copying code across versions.

---

# Java 18

## 1. UTF-8 as Default Charset

**What:** UTF-8 became the default charset for standard Java APIs.

**Why introduced:** Default character encoding varied across operating systems and environments, causing inconsistent text handling.

**Advantages:**
- More predictable text encoding.
- Fewer environment-specific bugs.
- Better interoperability.

**When to use:** Text-file processing, APIs and cross-platform applications.

**How to use:**
```java
byte[] bytes =
    "Payment".getBytes(StandardCharsets.UTF_8);
```

Prefer explicitly specifying the charset when the data format requires a particular encoding.

## 2. Simple Web Server

**What:** A minimal HTTP server included with the JDK.

**Why introduced:** Developers needed a quick way to test HTTP requests and serve simple content without setting up a full server.

**Advantages:** Convenient for development and testing.

**When to use:** Local testing, demonstrations and simple development scenarios.

**How to use:**
```bash
jwebserver
```

This is not a replacement for a production application server.

## 3. Code Snippets in Java API Documentation

**What:** Adds better support for including and validating code snippets in Javadoc.

**Why introduced:** Documentation examples can become outdated or incorrect.

**Advantages:** More maintainable API documentation.

**When to use:** Building shared Java libraries and documenting APIs.

**How to use:**
```java
/**
 * Processes a payment.
 *
 * {@snippet :
 * Payment payment = new Payment();
 * payment.process();
 * }
 */
```

---

# Java 19

## 1. Virtual Threads (Preview)

**What:** Lightweight threads managed by the JVM rather than mapping every Java thread directly to an operating-system thread.

**Why introduced:** Traditional platform threads are relatively expensive. Applications handling many concurrent I/O operations can hit thread and resource limits.

**Advantages:**
- Supports very high concurrency.
- Keeps a straightforward thread-per-task programming model.
- Reduces the cost of blocking I/O workloads.

**When to use:**
- High-concurrency REST services.
- Applications making many blocking API calls.
- I/O-bound microservices.

**How to use:**

Preview in Java 19:
```java
Thread thread =
    Thread.ofVirtual().start(() ->
        System.out.println("Virtual thread"));
```

Virtual threads became permanent in Java 21.

## 2. Structured Concurrency (Incubator)

**What:** Treats related concurrent tasks as one logical unit of work.

**Why introduced:** When several independent tasks run concurrently, managing cancellation, failure and completion individually becomes difficult.

**Advantages:**
- Easier task coordination.
- More consistent cancellation.
- Better error handling.

**When to use:** A request that needs data from multiple independent services.

**How to use:** Java 19 introduced an incubating API. The API evolved significantly in later releases, so use the version-specific API for your JDK.

## 3. Record Patterns (Preview)

**What:** Allows extracting record components through pattern matching.

**Why introduced:** Code frequently needed to check a record's type and then call multiple accessors manually.

**Advantages:** Concise data extraction and easier pattern matching.

**When to use:** Processing structured records and nested domain data.

**How to use:** Record patterns became permanent in Java 21.

---

# Java 20

## 1. Virtual Threads (Second Preview)

**What:** A second preview of lightweight JVM-managed threads.

**Why introduced:** To refine the virtual-thread model and prepare it for permanent adoption.

**Advantages:** Scalable concurrency for blocking workloads.

**When to use:** High-concurrency I/O applications.

**How to use:** The API continued evolving toward its permanent Java 21 form.

## 2. Scoped Values (Incubator)

**What:** A mechanism for sharing immutable data with methods and child tasks within a bounded execution scope.

**Why introduced:** `ThreadLocal` can be difficult to manage, especially with large numbers of threads and nested task execution.

**Advantages:**
- Clearer data-sharing scope.
- Immutable shared values.
- Useful with virtual threads.

**When to use:** Request context, tracing identifiers and security context propagation, after evaluating the API version available in your JDK.

**How to use:** The API evolved through incubator and preview stages before becoming permanent in Java 25.

## 3. Pattern Matching for Switch (Preview)

**What:** Allows switch to match object types and extract values.

**Why introduced:** Traditional switch was limited and type-based branching often required long `if-else` chains.

**Advantages:** More expressive branching and better support for sealed hierarchies.

**When to use:** Processing different domain types or event types.

**How to use:** Became permanent in Java 21.

---

# Java 21

Java 21 is an LTS release and one of the most important releases for modern enterprise Java.

## 1. Virtual Threads (Permanent)

**What:** Lightweight threads managed by the JVM.

**Why introduced:** To improve scalability for applications that need to handle many concurrent blocking operations.

**Advantages:**
- Large numbers of concurrent tasks.
- Simpler thread-per-request code.
- Less need for complex asynchronous callback chains in many I/O workloads.

**When to use:**
- Spring Boot REST APIs with blocking calls.
- Applications calling several external services.
- I/O-heavy workloads.

**How to use:**
```java
try (var executor =
         Executors.newVirtualThreadPerTaskExecutor()) {

    Future<String> future = executor.submit(() -> {
        Thread.sleep(1000);
        return "Payment completed";
    });

    System.out.println(future.get());
}
```

Important: Virtual threads improve concurrency, not the speed of CPU-intensive calculations. They also do not increase database connection-pool capacity. Size downstream pools according to the actual resource limits.

## 2. Record Patterns (Permanent)

**What:** Extracts record components directly during pattern matching.

**Why introduced:** Developers needed a concise way to inspect and unpack structured data.

**Advantages:** Less accessor code and cleaner nested data processing.

**When to use:** Processing records and structured domain objects.

**How to use:**
```java
record Customer(String name, int age) {}

static void process(Object obj) {
    if (obj instanceof Customer(String name, int age)) {
        System.out.println(name + " " + age);
    }
}
```

## 3. Pattern Matching for Switch (Permanent)

**What:** Allows switch statements and expressions to match types and use patterns.

**Why introduced:** Long `instanceof` and `if-else` chains were difficult to maintain.

**Advantages:**
- Cleaner conditional logic.
- Works well with sealed classes.
- Supports more expressive domain modelling.

**When to use:** Processing different payment or transaction types.

**How to use:**
```java
static String describe(Object value) {
    return switch (value) {
        case String text -> "Text: " + text;
        case Integer number -> "Number: " + number;
        case null -> "No value";
        default -> "Other";
    };
}
```

## 4. Sequenced Collections

**What:** Introduces interfaces such as `SequencedCollection`, `SequencedSet` and `SequencedMap`.

**Why introduced:** Collection APIs did not provide a common way to access first and last elements across different ordered collection types.

**Advantages:**
- Consistent first/last access.
- Easier reverse-order views.
- More uniform collection handling.

**When to use:** Ordered queues, lists, maps and sets where access to both ends is required.

**How to use:**
```java
SequencedCollection<String> names =
    new ArrayList<>();

names.addFirst("John");
names.addLast("David");

System.out.println(names.getFirst());
System.out.println(names.getLast());

System.out.println(names.reversed());
```

## 5. String Templates (Preview)

**What:** A proposed feature for embedding expressions into string templates using processors.

**Why introduced:** String concatenation and formatting can be error-prone, especially when dealing with structured data.

**Advantages:** Intended to make interpolation and specialized string processing more expressive.

**When to use:** Do not use this as a production Java 21 feature. It was previewed in Java 21 and subsequently withdrawn. Use text blocks, formatting APIs or safe template libraries instead.

## 6. Generational ZGC

**What:** ZGC gained generational collection capabilities.

**Why introduced:** Generational garbage collection takes advantage of the observation that many objects become unreachable quickly, while some survive for a long time.

**Advantages:**
- Better allocation and collection efficiency for many workloads.
- Low-pause collection characteristics.
- Useful for large heaps and latency-sensitive applications.

**When to use:** Applications where GC pauses are a measured latency problem.

**How to use:**
```bash
java -XX:+UseZGC -XX:+ZGenerational -jar app.jar
```

Check the exact JVM options supported by the JDK distribution and release you deploy.

---

# Java 22

## 1. Foreign Function and Memory API (Permanent)

**What:** Provides a supported Java API for calling native functions and accessing native memory.

**Why introduced:** JNI was complex and required native code and additional build steps.

**Advantages:**
- Easier native interoperability.
- More controlled native-memory access.
- Useful for performance-sensitive integrations.

**When to use:** Integrating with native libraries, system APIs or specialized high-performance components.

**How to use:**
```java
// Illustrative API usage; native library setup is also required.
Linker linker = Linker.nativeLinker();
SymbolLookup lookup =
    linker.defaultLookup();
```

The complete implementation also needs a native function symbol, a function descriptor and a downcall handle.

## 2. Stream Gatherers (Preview)

**What:** Allows developers to define custom intermediate stream operations.

**Why introduced:** The built-in stream operations do not cover every possible transformation pattern.

**Advantages:** Custom reusable stream transformations.

**When to use:** Advanced data-processing pipelines requiring custom intermediate operations.

**How to use:** Gatherers became permanent in Java 24. Use the API from the relevant JDK release.

## 3. Class-File API (Preview)

**What:** A standard API for parsing, generating and transforming Java class files.

**Why introduced:** Frameworks and tools often relied on third-party libraries or internal JDK APIs to manipulate bytecode.

**Advantages:**
- Standardized class-file processing.
- Better support for framework and tooling development.
- Less dependence on internal APIs.

**When to use:** Bytecode tools, instrumentation libraries and framework development.

**How to use:** Use the `java.lang.classfile` API available in later JDK versions where it became permanent.

---

# Java 23

## 1. Markdown Documentation Comments

**What:** Allows Markdown syntax in Java documentation comments.

**Why introduced:** Traditional Javadoc formatting could be verbose and difficult to maintain.

**Advantages:** More readable documentation source.

**When to use:** Documenting libraries and public APIs.

**How to use:**
```java
/// Processes a payment.
///
/// - Validates the request
/// - Checks the account
/// - Submits the transaction
public void processPayment() {
}
```

## 2. Stream Gatherers (Preview)

**What:** Custom intermediate stream operations.

**Why introduced:** To extend streams beyond the fixed set of built-in intermediate operations.

**Advantages:** Reusable custom stream-processing logic.

**When to use:** Specialized transformations, grouping and windowing.

**How to use:** Preview in Java 23; permanent in Java 24.

## 3. Unnamed Variables and Patterns (Preview)

**What:** Allows `_` to represent a variable or pattern component that is intentionally unused.

**Why introduced:** Developers sometimes need to match or receive a value without using it.

**Advantages:** Less clutter and clearer intent.

**When to use:** Exception handling, record patterns and switch branches where a matched value is not needed.

**How to use:**
```java
try {
    processPayment();
} catch (IOException _) {
    System.out.println("Unable to process file");
}
```

This feature became permanent in Java 22.

## 4. Module Import Declarations (Preview)

**What:** Allows importing all packages exported by a module.

**Why introduced:** Reduces repetitive imports when using modular APIs.

**Advantages:** Shorter import declarations.

**When to use:** Applications that use modules and want simpler imports.

**How to use:** Previewed in Java 23 and became permanent in Java 25.

---

# Java 24

## 1. Stream Gatherers (Permanent)

**What:** Supports custom intermediate operations in the Stream API.

**Why introduced:** Standard operations such as `map`, `filter` and `flatMap` cannot express every transformation cleanly.

**Advantages:** More flexible stream processing.

**When to use:** Custom grouping, windowing and specialized data transformations.

**How to use:**
```java
List<Integer> result =
    Stream.of(1, 2, 3, 4)
        .gather(Gatherers.windowFixed(2))
        .map(window -> window.stream()
            .mapToInt(Integer::intValue)
            .sum())
        .toList();
```

## 2. Class-File API (Permanent)

**What:** Standard API for working with Java class files.

**Why introduced:** To provide a supported alternative to internal APIs and external bytecode tooling dependencies.

**Advantages:** Standardized class-file parsing and generation.

**When to use:** Framework development, bytecode analysis and instrumentation.

**How to use:** Use the `java.lang.classfile` API and its builders, models and transforms. This is mainly for infrastructure and framework developers.

## 3. Virtual Thread Improvements

**What:** Java 24 improves virtual-thread behavior, including allowing virtual threads to use `synchronized` without the pinning limitation that affected earlier releases in certain cases.

**Why introduced:** Some synchronized blocking operations could prevent a carrier thread from being reused efficiently.

**Advantages:** Better scalability for workloads that use synchronized code with virtual threads.

**When to use:** Applications migrating to virtual threads, especially those with blocking synchronized sections.

**How to use:** Continue using normal synchronization where appropriate; test the actual workload on Java 24 or newer.

## 4. Ahead-of-Time Class Loading and Linking

**What:** Improves startup by allowing class-loading and linking work to be performed ahead of application execution.

**Why introduced:** Applications with many classes can spend significant time loading and linking during startup.

**Advantages:** Potentially faster startup and more predictable warm-up.

**When to use:** Applications with frequent restarts, serverless workloads and short-lived processes.

**How to use:** Use the AOT cache and related JVM options supported by the deployed JDK.

---

# Java 25

Java 25 is an LTS release.

## 1. Scoped Values (Permanent)

**What:** Provides a way to share immutable data with methods and child tasks within a bounded scope.

**Why introduced:** ThreadLocal can be difficult to manage and expensive in environments with many lightweight threads.

**Advantages:**
- Cleaner request-context propagation.
- Immutable shared data.
- Fits virtual-thread and structured-concurrency designs.

**When to use:**
- Correlation IDs.
- Request metadata.
- Tracing context.
- Immutable user/request context.

**How to use:**
```java
static final ScopedValue<String> REQUEST_ID =
    ScopedValue.newInstance();

public static void main(String[] args) {
    ScopedValue.where(REQUEST_ID, "REQ-1001")
        .run(() -> processRequest());
}

static void processRequest() {
    System.out.println(REQUEST_ID.get());
}
```

## 2. Module Import Declarations (Permanent)

**What:** Allows importing all packages exported by a module.

**Why introduced:** To reduce repetitive import statements when using modular APIs.

**Advantages:** Simpler imports for modular applications.

**When to use:** Applications using the Java module system.

**How to use:**
```java
import module java.base;
```

This is not a replacement for understanding dependencies or module boundaries.

## 3. Compact Source Files and Instance Main Methods (Permanent)

**What:** Makes it possible to write simple Java programs without the traditional public class and static main method boilerplate.

**Why introduced:** Beginners and small scripts should not need to learn every advanced Java construct before writing a simple program.

**Advantages:** Easier learning and shorter small programs.

**When to use:** Learning, demonstrations and small command-line programs.

**How to use:**
```java
void main() {
    IO.println("Hello Java 25");
}
```

## 4. Flexible Constructor Bodies (Permanent)

**What:** Allows certain statements to execute before an explicit `super(...)` or `this(...)` constructor invocation.

**Why introduced:** Traditional constructor rules prevented some validation and preparation from happening before superclass construction.

**Advantages:** More flexible constructor initialization and validation.

**When to use:** Specialized inheritance designs where subclass constructor arguments need validation before invoking the superclass constructor.

**How to use:**
```java
class Account {
    Account(String id) {
        System.out.println("Account: " + id);
    }
}

class SavingsAccount extends Account {
    SavingsAccount(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Invalid ID");
        }

        super(id);
    }
}
```

## 5. Primitive Types in Patterns, instanceof and switch (Preview)

**What:** Extends pattern matching to support primitive types.

**Why introduced:** Pattern matching previously focused mainly on reference types. Extending it to primitives makes the language more consistent.

**Advantages:** More expressive primitive-value matching and fewer conversion steps.

**When to use:** Experimentation and evaluation of the preview feature. Do not treat it as a permanent Java 25 language feature.

**How to use:** Enable preview features when compiling and running with a compatible JDK:
```bash
javac --release 25 --enable-preview Example.java
java --enable-preview Example
```

---

# Cross-Version Features: Important Java APIs and Concepts

The following are important capabilities that evolved across releases. They should also be documented using the same six-part structure.

## 1. Collection Framework Improvements

Explain:
- Why collection APIs evolved.
- Advantages of immutable collections.
- When to use List, Set, Map, Queue and concurrent collections.
- How to use factory methods, sequenced collections and stream operations.

## 2. Concurrency Improvements

Explain:
- Why Java moved from traditional threads toward ForkJoinPool, CompletableFuture and virtual threads.
- Advantages of each concurrency model.
- When to use platform threads, virtual threads, executors and asynchronous APIs.
- How to avoid shared-state and thread-pool problems.

## 3. JVM Performance Improvements

Explain:
- Why JVM startup, compilation and memory management have evolved.
- Advantages of class-data sharing, AOT caches and runtime optimizations.
- When to use these optimizations.
- How to measure before changing JVM settings.

---

# Garbage Collection: Apply the Same Explanation Structure

For every garbage collector, explain why it exists, what advantage it offers, when to use it and how to configure it.

| Collector | Why introduced / main goal | When to consider it |
|---|---|---|
| Serial GC | Simple, low-overhead collection | Small heaps and constrained applications |
| Parallel GC | Maximize throughput using multiple GC threads | Batch and CPU-oriented workloads |
| G1 GC | Balance throughput and pause-time goals | General-purpose enterprise services |
| ZGC | Minimize GC pause times | Large heaps and latency-sensitive applications |
| Shenandoah | Concurrent collection with low pause times | Supported JVM distributions and latency-sensitive workloads |
| Epsilon GC | No-op collector for controlled experiments | Benchmarking and short-lived processes |

## Example: G1 GC

**What:** A garbage collector that divides the heap into regions and collects selected regions to meet a pause-time goal.

**Why introduced:** Traditional collectors could have less predictable pauses or fail to balance throughput and latency for larger heaps.

**Advantages:**
- Predictable pause-time goal (not a hard guarantee).
- Handles large heaps.
- General-purpose balance between throughput and latency.

**When to use:** As a starting point for many enterprise Java applications, including Spring Boot services.

**How to use:**
```bash
java \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Xms2g \
  -Xmx2g \
  -jar application.jar
```

The pause-time setting is a goal, not a guaranteed maximum. Measure actual GC behavior before tuning.

## Example: ZGC

**What:** A low-latency garbage collector designed to perform most work concurrently with application threads.

**Why introduced:** Some applications cannot tolerate long stop-the-world garbage collection pauses.

**Advantages:**
- Very low pause times in suitable workloads.
- Supports large heaps.
- Useful for latency-sensitive systems.

**When to use:**
- Low-latency transaction services.
- Large-memory applications.
- Systems where GC pauses are a measured source of latency.

**How to use:**
```bash
java -XX:+UseZGC \
     -Xms4g \
     -Xmx4g \
     -jar application.jar
```

Use generational ZGC where supported and appropriate for your JDK.

---

# Recommended Comparison Format for Every Feature

At the end of each feature, include a comparison like this whenever an older approach exists:

| Aspect | Old approach | New approach |
|---|---|---|
| Code | More boilerplate | More concise |
| Readability | More repetitive | Easier to understand |
| Maintenance | More manual work | Less repetitive code |
| Use case | Traditional implementation | Modern implementation |

# Practical Learning and Adoption Path

1. **Java 8:** Learn lambdas, streams, Optional, Date/Time and CompletableFuture.
2. **Java 9–11:** Learn modules, collection factories, `var` and the HTTP Client.
3. **Java 12–17:** Learn switch expressions, text blocks, records, pattern matching and sealed classes.
4. **Java 18–21:** Learn UTF-8 defaults, virtual threads, record patterns, pattern matching for switch and sequenced collections.
5. **Java 22–25:** Learn the Foreign Function and Memory API, Stream Gatherers, Scoped Values, Class-File API and newer language improvements.
6. **JVM:** Learn garbage collectors, heap sizing, GC logs, JFR and JVM monitoring.

# Important Version and Preview Notes

- A feature introduced as a preview may change before becoming permanent.
- A preview feature generally requires `--enable-preview` when compiling and running with the matching JDK release.
- Do not assume a preview API from one JDK release is identical in another.
- Distinguish language features from JVM, API and tooling improvements.
- For enterprise migration, validate framework and library compatibility as well as Java source compatibility.

# References

- [Oracle Java Language Changes Summary](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-summary.html)
- [Oracle Java Language Changes by Release](https://docs.oracle.com/en/java/javase/25/language/java-language-changes-release.html)
- [Oracle Java 25 Release Notes](https://www.oracle.com/java/technologies/javase/25all-relnotes.html)
- [OpenJDK JEP Index](https://openjdk.org/jeps/0)

