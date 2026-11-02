# Java Garbage Collection (GC) – Complete Guide
## Understanding, Choosing, Configuring and Troubleshooting Garbage Collectors (Java 8–25)

---

# 1. What is Garbage Collection?

Imagine you have a house with limited storage space.

Every time you buy something, you put it inside the house. After some time, you no longer need certain items. If you never remove those items, eventually your house will become full, and you won't have space for anything new.

Java applications have a similar problem.

Whenever your Java application creates an object, memory is allocated to store that object.

For example:

```java
public void processPayment() {

    Payment payment = new Payment();

    payment.setAmount(1000);

    System.out.println(payment.getAmount());
}
```

When the method finishes, the `payment` object may no longer be needed.

Java Garbage Collection automatically identifies objects that are no longer reachable and reclaims their memory.

**In simple terms:**

Garbage Collection is the JVM's housekeeping mechanism. It identifies unused objects and frees memory so that the application can continue running.

### Important points

- Java developers normally don't need to manually free object memory.
- GC runs automatically when the JVM decides it is necessary.
- You cannot guarantee exactly when GC will run.
- GC can pause application threads, depending on the collector and collection phase.
- Different GC algorithms have different performance characteristics.

---

# 2. How Does Java Allocate Memory?

Before understanding GC, let's understand where Java stores objects.

The JVM has different memory areas.

| Memory area | What it stores | Simple explanation |
|---|---|---|
| Heap | Java objects | Storage room for objects |
| Stack | Method calls and local variables | Working desk for each thread |
| Metaspace | Class metadata | Information about loaded Java classes |
| Code Cache | Compiled machine code | Optimized instructions |
| Direct Memory | Native buffers | Memory outside the Java heap |

The most important area for Garbage Collection is the **Heap**.

## 2.1 Understanding Heap Memory

Consider this:

```java
Customer customer = new Customer();

customer.setName("Arun");
```

There are two important things:

1. The `customer` reference is stored in a stack frame when it is a local variable.
2. The actual `Customer` object is stored in the heap.

Think of the reference as an address pointing to an object in the storage room.

```text
              STACK
        ------------------
        | customer       |
        | Reference      |
        |                |
        ------------------
                |
                | Points to
                v
              HEAP
        ------------------
        | Customer       |
        | Name: Arun     |
        ------------------
```

When the object is no longer reachable from any GC root, it becomes eligible for garbage collection.

GC roots include:

- References from active thread stacks.
- Static fields.
- Active class-loader references.
- JNI references.
- Other JVM-managed roots.

**Important:** An object being eligible for GC does not mean that it will be immediately removed.

---

# 3. How Does Garbage Collection Work?

Let's understand using a simple example.

```java
public void process() {

    Customer customer1 = new Customer();

    Customer customer2 = new Customer();

    customer1 = null;

    customer2 = null;
}
```

Initially:

```text
             HEAP

        -----------------
        | Customer 1    |
        -----------------

        -----------------
        | Customer 2    |
        -----------------
```

Both objects are referenced by local variables.

After setting references to null:

```java
customer1 = null;
customer2 = null;
```

Neither object is reachable through those local references.

If no other references exist, both objects become eligible for GC.

The JVM can reclaim their memory during a future collection.

## 3.1 Four Basic Steps

### Step 1 – Mark

The JVM identifies objects that are still reachable.

### Step 2 – Identify garbage

Objects that are not reachable are considered garbage.

### Step 3 – Reclaim

The JVM reclaims memory occupied by garbage objects.

### Step 4 – Compact or evacuate

Depending on the collector, live objects may be moved together to reduce fragmentation and make future allocations easier.

Not every GC performs these operations in exactly the same way or order.

---

# 4. Why Do We Need Different Garbage Collectors?

Imagine your application is processing 10,000 payment requests.

During processing, it creates thousands of objects:

- Payment objects.
- Customer objects.
- Request objects.
- Response objects.
- JSON objects.
- Database result objects.

Many of these objects are temporary.

The JVM needs to clean them up.

But there is a problem.

**While GC is working, it may need to pause application threads.**

Consider:

```text
Application processing:

Request 1  ---->
Request 2  ---->
Request 3  ---->
Request 4  ---->

GC starts

Request 1  PAUSED
Request 2  PAUSED
Request 3  PAUSED
Request 4  PAUSED

GC finishes

Request 1  ---->
Request 2  ---->
Request 3  ---->
Request 4  ---->
```

This pause is called a **Stop-The-World (STW) pause**.

Depending on the collector, some GC phases can run concurrently with application threads, while other phases require pauses.

Different garbage collectors try to solve different problems.

| Requirement | What matters most |
|---|---|
| High throughput | Complete as much application work as possible |
| Low latency | Keep application pauses very short |
| Small memory footprint | Minimize memory overhead |
| Large heap | Handle large amounts of memory efficiently |
| Predictable pauses | Reduce unexpected response-time spikes |

This is why Java provides multiple garbage collectors.

---

# 5. Types of Garbage Collectors

The major HotSpot garbage collectors are:

1. Serial GC
2. Parallel GC
3. G1 GC
4. ZGC
5. Shenandoah GC
6. Epsilon GC

Let's understand each one.

---

# 6. Serial Garbage Collector

## 6.1 What is Serial GC?

Imagine one person cleaning an entire office.

Only one person does the cleaning. Everyone else must wait while the cleaning happens.

Serial GC works similarly.

It uses one GC thread to perform collection work.

## 6.2 How Does It Work?

```text
Application Threads
----------------------------
Thread 1
Thread 2
Thread 3
Thread 4

        PAUSE

Serial GC Thread
----------------------------
Clean unused objects

        RESUME

Application Threads
----------------------------
Thread 1
Thread 2
Thread 3
Thread 4
```

It performs stop-the-world collections.

## 6.3 Advantages

- Simple.
- Low GC-thread overhead.
- Suitable for small heaps.
- Can perform well in small applications.

## 6.4 Disadvantages

- Uses only one GC thread.
- Application threads are paused during collection.
- Not suitable for demanding high-concurrency workloads.

## 6.5 When Should You Use It?

Use Serial GC when:

- You have a small Java application.
- The application runs with a small heap.
- CPU resources are limited.
- High concurrency is not required.
- GC pause duration is not critical.

Examples:

- Small standalone utilities.
- Simple command-line applications.
- Small batch programs.

## 6.6 How to Enable Serial GC

```bash
java -XX:+UseSerialGC -jar application.jar
```

Configure heap:

```bash
java \
  -XX:+UseSerialGC \
  -Xms256m \
  -Xmx512m \
  -jar application.jar
```

---

# 7. Parallel Garbage Collector

## 7.1 What is Parallel GC?

Imagine 10 people cleaning the same office instead of one person.

The cleaning can finish faster because multiple people work simultaneously.

Parallel GC uses multiple threads to perform GC operations.

It is also called the **Throughput Collector**.

## 7.2 How Does It Work?

```text
Application Threads
-------------------------
Thread 1
Thread 2
Thread 3
Thread 4

       PAUSE

Parallel GC
-------------------------
GC Thread 1
GC Thread 2
GC Thread 3
GC Thread 4

All work together

       RESUME

Application Threads
```

## 7.3 Advantages

- Multiple GC threads.
- Good throughput.
- Can reclaim memory efficiently.
- Suitable for CPU-intensive batch workloads where pauses are acceptable.

## 7.4 Disadvantages

- Stop-the-world pauses.
- Pauses can become noticeable with larger heaps.
- Not ideal for applications requiring consistently low response times.

## 7.5 When Should You Use It?

Use Parallel GC when:

- You want maximum application throughput.
- Longer pauses are acceptable.
- You run batch processing.
- You perform large data transformations.
- You don't have strict response-time requirements.

Examples:

- Nightly reconciliation.
- Large file processing.
- Batch payment processing.
- Data migration jobs.

## 7.6 How to Enable Parallel GC

```bash
java -XX:+UseParallelGC -jar application.jar
```

With heap configuration:

```bash
java \
  -XX:+UseParallelGC \
  -Xms2g \
  -Xmx2g \
  -jar application.jar
```

Set the GC thread count if necessary:

```bash
-XX:ParallelGCThreads=4
```

Don't set this arbitrarily. The JVM usually chooses a reasonable value based on available CPU resources.

---

# 8. G1 Garbage Collector

## 8.1 What is G1 GC?

G1 means Garbage First.

Imagine a large office building with 100 rooms.

Instead of cleaning the entire building at once, you divide it into rooms.

You inspect each room and identify which rooms contain the most rubbish.

You clean the rooms that give you the greatest benefit first.

G1 works in a similar way.

It divides the Java heap into multiple regions and prioritizes regions that contain a large amount of reclaimable garbage.

## 8.2 How Does G1 Work?

Traditional generational collectors describe memory using:

- Young Generation.
- Old Generation.

G1 still uses generational concepts, but physically manages the heap using regions.

```text
             G1 HEAP

-------------------------------------
| Region | Region | Region | Region  |
-------------------------------------
| Eden   | Eden   | Survivor         |
-------------------------------------
| Old    | Old    | Old              |
-------------------------------------
| Free   | Free   | Free             |
-------------------------------------
```

The actual regions can have different roles over time.

### G1 Collection Process

1. New objects are allocated.
2. Young regions are collected regularly.
3. Objects that survive can be promoted.
4. G1 identifies old regions containing reclaimable memory.
5. G1 selects regions for mixed collection.
6. Live objects are evacuated and unused space is reclaimed.

G1 also has concurrent marking phases to understand which old regions contain garbage.

## 8.3 Important G1 Concepts

### Young GC

Collects young-generation objects.

### Concurrent Marking

Identifies live objects in the old generation while application threads continue running.

### Mixed GC

Collects young regions and selected old regions together.

### Full GC

A fallback full-heap collection can happen under certain conditions. It can cause significant pauses.

## 8.4 Advantages

- Good balance between throughput and latency.
- Region-based memory management.
- Suitable for medium and large heaps.
- Supports pause-time goals.
- General-purpose server collector.

## 8.5 Disadvantages

- More complex than Serial and Parallel GC.
- Requires additional GC CPU resources.
- Pause-time goals are not guaranteed.
- Can still experience long pauses or Full GC under memory pressure.

## 8.6 When Should You Use G1?

G1 is a good starting choice for most enterprise Java applications.

Use it for:

- Spring Boot microservices.
- REST APIs.
- Banking applications.
- Payment processing services.
- Applications with moderate or large heaps.
- General enterprise workloads.

For many applications, G1 provides a good balance without requiring complex tuning.

## 8.7 How to Enable G1

```bash
java -XX:+UseG1GC -jar application.jar
```

Production-style example:

```bash
java \
  -XX:+UseG1GC \
  -Xms2g \
  -Xmx2g \
  -XX:MaxGCPauseMillis=200 \
  -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags \
  -jar application.jar
```

### Important G1 Parameters

| Parameter | Meaning |
|---|---|
| `-XX:+UseG1GC` | Enable G1 |
| `-Xms2g` | Initial heap size |
| `-Xmx2g` | Maximum heap size |
| `-XX:MaxGCPauseMillis=200` | Desired pause-time target |
| `-XX:ParallelGCThreads=4` | Parallel GC worker threads |
| `-XX:ConcGCThreads=2` | Concurrent GC threads |
| `-XX:+UseStringDeduplication` | Deduplicate eligible strings |

The pause-time target is a tuning goal, not a guarantee.

---

# 9. ZGC – Z Garbage Collector

## 9.1 What is ZGC?

Imagine a hospital emergency room.

Patients are continuously arriving. You cannot stop all hospital activities every time the staff cleans a room.

You need cleaning to happen while the hospital continues operating.

ZGC is designed to achieve very short GC pauses while performing most of its work concurrently with application threads.

Its design targets very low pause times, including for large heaps.

## 9.2 How Does ZGC Work?

Traditional collectors can pause application threads for significant GC work.

ZGC performs most of its work concurrently.

Simplified view:

```text
Application Threads
--------------------------------
Request 1  ---------------------
Request 2  ---------------------
Request 3  ---------------------
Request 4  ---------------------

ZGC Threads
--------------------------------
Marking       ------------------
Relocation    ------------------
Reference     ------------------
Processing    ------------------

Short synchronization pauses
where required
```

ZGC uses techniques such as colored pointers and load barriers to manage object references while collection is happening concurrently.

You do not need to understand those internals to choose it.

The important point is:

**ZGC aims to minimize the time application threads are stopped for GC.**

## 9.3 Advantages

- Very low pause-time design.
- Supports large heaps.
- Performs most GC work concurrently.
- Useful for latency-sensitive applications.
- Generational mode improves collection of short-lived objects.

## 9.4 Disadvantages

- Concurrent collection consumes CPU.
- Requires sufficient memory headroom.
- Very low pauses do not mean zero pauses.
- May not outperform G1 for every workload.
- Requires performance testing.

## 9.5 When Should You Use ZGC?

Use ZGC when:

- Your application has strict latency requirements.
- GC pauses affect API response times.
- You have a large heap.
- You observe p99 or p99.9 response-time spikes associated with GC.
- Your application must maintain responsiveness during memory collection.

Examples:

- Real-time financial transaction processing.
- High-volume payment APIs.
- Trading platforms.
- Large-memory microservices.
- Latency-sensitive enterprise applications.

## 9.6 How to Enable ZGC

Java 21 and later:

```bash
java \
  -XX:+UseZGC \
  -Xms4g \
  -Xmx4g \
  -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags \
  -jar application.jar
```

For Java 21, generational ZGC can be explicitly enabled:

```bash
-XX:+UseZGC -XX:+ZGenerational
```

Generational ZGC became a product feature in Java 21.

In Java 23 and later, generational ZGC is the default ZGC mode. Avoid carrying old experimental flags into newer JDKs without checking the release documentation.

---

# 10. Shenandoah Garbage Collector

## 10.1 What is Shenandoah?

Shenandoah is another low-pause garbage collector.

Think of it as a cleaning team that works in the background while people continue using the building.

Like ZGC, it performs significant work concurrently with application execution.

## 10.2 How Does It Work?

Shenandoah uses concurrent marking and evacuation.

Simplified:

```text
Application Threads
----------------------------
Working continuously

Shenandoah GC
----------------------------
Mark live objects
Identify garbage
Move live objects
Reclaim old regions

Short synchronization pauses
where required
```

It uses forwarding pointers and barriers to support concurrent relocation.

## 10.3 Advantages

- Low pause-time design.
- Suitable for larger heaps.
- Performs much of its work concurrently.
- Useful for latency-sensitive services.

## 10.4 Disadvantages

- Concurrent GC consumes CPU.
- Requires sufficient memory headroom.
- Not available in every JDK distribution.
- Requires workload-specific benchmarking.

## 10.5 When Should You Use Shenandoah?

Consider Shenandoah when:

- You need low GC pauses.
- Your application is latency-sensitive.
- You run large-memory services.
- Your JDK distribution supports it.
- Benchmarking shows better results for your workload.

## 10.6 How to Enable Shenandoah

```bash
java \
  -XX:+UseShenandoahGC \
  -Xms4g \
  -Xmx4g \
  -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags \
  -jar application.jar
```

Availability and support depend on the JDK vendor and build.

---

# 11. Epsilon Garbage Collector

## 11.1 What is Epsilon?

Epsilon is a no-operation garbage collector.

Imagine a house where you keep adding furniture but never remove anything.

Eventually, the house becomes full.

Epsilon behaves similarly: it allocates memory but does not reclaim garbage.

## 11.2 When Should You Use It?

Useful for:

- GC benchmarking.
- Measuring allocation rates.
- Short-lived controlled applications.
- Testing memory limits.

It is not suitable for ordinary long-running production applications.

## 11.3 How to Enable Epsilon

```bash
java \
  -XX:+UseEpsilonGC \
  -Xms256m \
  -Xmx256m \
  -jar allocation-test.jar
```

Eventually, the application may run out of heap memory.

---

# 12. Comparison of All Garbage Collectors

| Feature | Serial | Parallel | G1 | ZGC | Shenandoah | Epsilon |
|---|---|---|---|---|---|---|
| Multiple GC threads | No | Yes | Yes | Yes | Yes | No GC work |
| Stop-the-world work | Significant | Significant | Some phases | Very short phases | Very short phases | No collection |
| Low-latency design | No | No | Balanced | Yes | Yes | Not applicable |
| Throughput focus | No | Yes | Balanced | Latency-focused | Latency-focused | Benchmarking |
| Large heap suitability | Limited | Workload-dependent | Yes | Yes | Yes | No |
| Generational | Yes | Yes | Yes | Yes | Yes in modern versions | No |
| General enterprise choice | Rarely | Batch workloads | Yes | Specific workloads | Specific workloads | No |

---

# 13. Which GC Should You Choose?

This is the most important section.

Don't select a garbage collector simply because it is the newest one.

Choose it based on your application's actual requirements.

## Scenario 1 – Spring Boot REST API

Suppose you have:

- 3 application instances.
- Each instance has a 2 GB heap.
- Requests are processed continuously.
- Normal API response time is 100–300 ms.
- GC pauses are not causing noticeable issues.

**Recommendation: G1 GC.**

Why?

- Good general-purpose performance.
- Good balance between throughput and latency.
- Well suited to enterprise workloads.

Configuration:

```bash
-XX:+UseG1GC
-Xms2g
-Xmx2g
-XX:MaxGCPauseMillis=200
```

## Scenario 2 – High-Volume Payment Processing

Suppose you have:

- Thousands of payment requests.
- Many temporary objects.
- High transaction volume.
- Batch processing.
- Throughput is more important than occasional pauses.

**Recommendation: Start with G1 or benchmark Parallel GC.**

Why?

Parallel GC can offer high throughput, but it may introduce longer pauses.

If payment processing has strict API response-time requirements, G1 or ZGC may be more appropriate.

## Scenario 3 – Low-Latency Banking Application

Suppose you have:

- Payment APIs.
- Strict response-time requirements.
- p99 latency is important.
- GC pauses create transaction response spikes.
- Large heap.

**Recommendation: Benchmark ZGC against G1.**

Why?

ZGC is designed for very short GC pauses.

Example:

```bash
-XX:+UseZGC
-Xms4g
-Xmx4g
```

## Scenario 4 – Nightly Reconciliation Batch

Suppose you have:

- Millions of records.
- Large data transformations.
- Long-running batch jobs.
- Processing throughput is important.
- Occasional pauses are acceptable.

**Recommendation: Benchmark Parallel GC and G1.**

Parallel GC is worth testing if maximum throughput is the primary objective.

## Scenario 5 – Small Utility Application

Suppose you have:

- 200 MB heap.
- One or two threads.
- No strict latency requirements.

**Recommendation: Serial GC can be suitable.**

---

# 14. Important GC Concepts You Should Understand

## 14.1 Young Generation

Most newly allocated objects are short-lived.

Example:

```java
public void processRequest() {

    PaymentRequest request = new PaymentRequest();

    PaymentResponse response = new PaymentResponse();
}
```

These objects may become garbage quickly.

Young-generation collection handles this kind of object allocation efficiently.

## 14.2 Old Generation

Some objects remain alive for a long time.

Example:

```java
public class ApplicationCache {

    private static final Map<String, Customer> CACHE =
            new ConcurrentHashMap<>();
}
```

Objects retained in a long-lived cache can consume memory for a long time.

An object can be promoted into old-generation regions when it survives young collections.

## 14.3 Minor GC

Traditionally, a Minor GC refers to a collection of the young generation.

It is generally less expensive than a full-heap collection.

## 14.4 Major GC

The term Major GC is commonly used for old-generation collection, but its exact meaning can vary between JVM implementations and tools.

## 14.5 Full GC

Full GC generally means a full-heap collection.

It can be expensive and cause long stop-the-world pauses.

Frequent Full GC is worth investigating.

## 14.6 Memory Leak

Java has automatic GC, but Java applications can still have memory leaks.

Example:

```java
public class Cache {

    private static final List<Object> objects =
            new ArrayList<>();

    public static void add(Object object) {
        objects.add(object);
    }
}
```

If objects are continuously added and never removed, they remain reachable.

GC cannot reclaim them.

Eventually, the application may throw:

```text
OutOfMemoryError: Java heap space
```

---

# 15. How to Enable GC Logging

GC logs help you understand what the JVM is doing.

Java 9 and later:

```bash
-Xlog:gc*
```

Production example:

```bash
-Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags:filecount=5,filesize=20M
```

This configuration:

- Enables GC and safepoint logging.
- Writes logs to a file.
- Includes timestamps and uptime.
- Keeps five rotating log files.
- Limits each file to approximately 20 MB.

For Java 8, unified logging is not available. Use legacy options:

```bash
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-Xloggc:gc.log
```

---

# 16. How to Monitor GC in Production

Important JVM tools:

## 16.1 jstat

Check GC statistics:

```bash
jstat -gcutil <pid> 1000
```

It displays GC-related statistics every second.

## 16.2 jcmd

Get heap information:

```bash
jcmd <pid> GC.heap_info
```

Get JVM flags:

```bash
jcmd <pid> VM.flags
```

Get class histogram:

```bash
jcmd <pid> GC.class_histogram
```

## 16.3 Heap Dump

```bash
jcmd <pid> GC.heap_dump /tmp/heap.hprof
```

Analyze the dump using Eclipse Memory Analyzer (MAT).

## 16.4 Java Flight Recorder

Start a recording:

```bash
jcmd <pid> JFR.start \
  name=production \
  settings=profile \
  duration=5m \
  filename=/tmp/application.jfr
```

Analyze it using Java Mission Control.

You can inspect:

- Object allocation.
- GC pauses.
- CPU usage.
- Thread contention.
- Memory usage.

---

# 17. How to Identify GC Problems

Consider the following symptoms.

| Symptom | Possible reason |
|---|---|
| API response time suddenly increases | GC pause or CPU pressure |
| Frequent Full GC | Heap pressure or allocation problem |
| Heap constantly increases | Memory leak or growing cache |
| High CPU usage | GC overhead or application workload |
| OutOfMemoryError | Insufficient heap or retained objects |
| Kubernetes OOMKilled | Container memory limit exceeded |
| High p99 latency | GC pauses, CPU throttling or other bottlenecks |

Remember that not every performance issue is caused by GC.

Always correlate GC logs with application metrics.

---

# 18. GC in Kubernetes – Important for Microservices

This is particularly important when running Java applications in containers.

Suppose your Kubernetes deployment has:

```yaml
resources:
  requests:
    memory: "1Gi"
  limits:
    memory: "2Gi"
```

Does this mean you can configure a 2 GB Java heap?

Not necessarily.

The container memory limit includes:

- Java heap.
- Metaspace.
- Thread stacks.
- Direct memory.
- JIT code cache.
- GC native memory.
- Native libraries.

If the heap consumes 2 GB, there may be no memory left for other JVM components.

Kubernetes can terminate the container due to memory pressure.

## Recommended Example

For a container with a 2 GB memory limit:

```bash
java \
  -XX:+UseG1GC \
  -XX:InitialRAMPercentage=25 \
  -XX:MaxRAMPercentage=60 \
  -Xlog:gc*,safepoint:stdout:time,uptime,level,tags \
  -jar application.jar
```

This allows the JVM to size its heap based on available container memory.

The actual heap size should be validated using real workload measurements.

## Important Kubernetes Monitoring

Monitor:

1. Container memory usage.
2. JVM heap usage.
3. Metaspace.
4. Direct memory.
5. Thread count.
6. GC pause time.
7. GC frequency.
8. CPU throttling.

---

# 19. GC Evolution – Java 8 to Java 25

| Java Version | Important GC Changes |
|---|---|
| Java 8 | Parallel and CMS commonly used; G1 available; Metaspace |
| Java 9 | G1 default in typical server configurations; CMS deprecated |
| Java 10 | Parallel full GC for G1 |
| Java 11 | ZGC and Epsilon introduced as experimental |
| Java 12 | Shenandoah introduced as experimental in OpenJDK |
| Java 13 | ZGC improved memory uncommit behaviour |
| Java 14 | CMS removed |
| Java 15 | ZGC and Shenandoah became production features in supporting builds |
| Java 16 | Elastic Metaspace |
| Java 17 | G1 and other collectors improved |
| Java 19 | Generational ZGC previewed experimentally |
| Java 21 | Generational ZGC became a product feature |
| Java 23 | Generational ZGC became the default ZGC mode |
| Java 24 | Generational Shenandoah |
| Java 25 | Compact Object Headers became a product feature; further G1 improvements |

---

# 20. Recommended GC Configuration for Enterprise Applications

For a typical Spring Boot microservice, start with:

```bash
java \
  -Xms1g \
  -Xmx1g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags \
  -jar payment-service.jar
```

For a latency-sensitive service, benchmark:

```bash
java \
  -Xms2g \
  -Xmx2g \
  -XX:+UseZGC \
  -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags \
  -jar payment-service.jar
```

For a batch processing service, benchmark:

```bash
java \
  -Xms4g \
  -Xmx4g \
  -XX:+UseParallelGC \
  -Xlog:gc*,safepoint:file=gc.log:time,uptime,level,tags \
  -jar reconciliation-service.jar
```

These are starting configurations, not universal production settings.

---

# 21. Final Decision Matrix

| Application type | First collector to evaluate | Why |
|---|---|---|
| Small standalone Java program | Serial | Simple and low overhead |
| Batch processing | Parallel | Throughput-oriented |
| Spring Boot REST service | G1 | General-purpose balance |
| Banking microservice | G1 | Reliable general-purpose starting point |
| High-volume payment API | G1 / ZGC | Balance versus latency |
| Real-time transaction API | ZGC | Low-pause design |
| Large heap application | G1 / ZGC / Shenandoah | Benchmark based on latency and throughput |
| Memory allocation benchmark | Epsilon | No reclamation overhead |

---

# 22. My Recommendation

For a modern enterprise Java environment:

**Start with G1 GC.**

Move to ZGC or Shenandoah only when measurements demonstrate that GC pauses are causing an actual latency problem.

Use Parallel GC when throughput is more important than pause time and benchmarks support it.

Use Serial GC for small, resource-constrained applications.

Avoid Epsilon for normal production services.

The most important lesson is:

**A good garbage collector cannot fix a memory leak, an unbounded cache, excessive object creation or an incorrectly sized container.**

Before changing GC, investigate:

1. How much memory your application allocates.
2. How much memory remains live after collection.
3. How frequently GC happens.
4. How long GC pauses last.
5. Whether GC is affecting API response times.
6. Whether the container has sufficient memory and CPU.

Choose your collector based on actual production measurements, not assumptions.

---

# 23. Official References

- [Java 25 Garbage Collection Tuning Guide](https://docs.oracle.com/en/java/javase/25/gctuning/)
- [Java 25 JVM Options](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html)
- [OpenJDK ZGC](https://wiki.openjdk.org/display/zgc)
- [OpenJDK Shenandoah](https://wiki.openjdk.org/display/shenandoah)
- [OpenJDK G1 GC](https://docs.oracle.com/en/java/javase/25/gctuning/garbage-first-garbage-collector.html)
- [Java Flight Recorder](https://docs.oracle.com/en/java/javase/25/jfapi/)

**End of Document**
