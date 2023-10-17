# JUC 并发编程

https://www.bilibili.com/video/BV1Kw411Z7dF

## 概述

### 线程状态

```java
    public enum State {
        /**
         * Thread state for a thread which has not yet started.
         */
        NEW,

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         */
        RUNNABLE,

        /**
         * Thread state for a thread blocked waiting for a monitor lock.
         * A thread in the blocked state is waiting for a monitor lock
         * to enter a synchronized block/method or
         * reenter a synchronized block/method after calling
         * {@link Object#wait() Object.wait}.
         */
        BLOCKED,

        /**
         * Thread state for a waiting thread.
         * A thread is in the waiting state due to calling one of the
         * following methods:
         * <ul>
         *   <li>{@link Object#wait() Object.wait} with no timeout</li>
         *   <li>{@link #join() Thread.join} with no timeout</li>
         *   <li>{@link LockSupport#park() LockSupport.park}</li>
         * </ul>
         *
         * <p>A thread in the waiting state is waiting for another thread to
         * perform a particular action.
         *
         * For example, a thread that has called {@code Object.wait()}
         * on an object is waiting for another thread to call
         * {@code Object.notify()} or {@code Object.notifyAll()} on
         * that object. A thread that has called {@code Thread.join()}
         * is waiting for a specified thread to terminate.
         */
        WAITING,

        /**
         * Thread state for a waiting thread with a specified waiting time.
         * A thread is in the timed waiting state due to calling one of
         * the following methods with a specified positive waiting time:
         * <ul>
         *   <li>{@link #sleep Thread.sleep}</li>
         *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
         *   <li>{@link #join(long) Thread.join} with timeout</li>
         *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
         *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
         * </ul>
         */
        TIMED_WAITING,

        /**
         * Thread state for a terminated thread.
         * The thread has completed execution.
         */
        TERMINATED;
    }
```



## Lock 接口

### synchronized

| **使用位置**       | **作用范围**   | **被锁的对象** | **示例代码**                                          |
| ------------------ | -------------- | -------------- | ----------------------------------------------------- |
| 方法               | 实例方法       | 类的实例对象   | public synchronized void method() { .......}          |
| 静态方法           | 类对象         |                | public static synchronized void method() { .......}   |
| 代码块             | 实例对象       | 类的实例对象   | synchronized (this) { .......}                        |
| class对象          | 类对象         |                | synchronized (synchronizedScopeDemo.class) { .......} |
| 任意实例对象object | 实例对象object |                | final String lock = "";synchronized (lock) { .......} |



### Lock

```java
class X {
    private final ReentrantLock lock = new ReentrantLock();
    // ...
    
    public void m() {
        lock.lock(); // block until condition holds
        try {
            // ... method body
        } finally {
            lock.unlock();
        }
    }
}
```



Lock 和 synchronized 有以下几点不同：

1. Lock 是一个接口，而 synchronized 是 Java 中的关键字，synchronized 是内置的语言实现；
2. synchronized 在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象的发生；而 Lock 在发生异常时，不会自动释放锁，可能造成死锁现象，需要在 finally 块中释放锁；
3. Lock 可以让等待锁的线程响应中断，而使用 synchronized 时，等待的线程会一直等待下去，不会响应中断；
4. 通过 Lock 可以知道有没有成功获取锁，synchronized 做不到；
5. Lock 可以提高多个线程进行读操作的效率。

在性能上来说，如果竞争资源不激烈，两者的性能差不多；而当竞争资源非常激烈时（大量线程同时竞争），此时 Lock 的性能要远远优于 synchronized。



## 线程间通信

### wait()、notify() 和 notifyAll()

`wait()`、`notify()` 和 `notifyAll()` 这些方法必须在同步上下文中使用。否则，Java会抛出 `IllegalMonitorStateException`。可以在方法上添加 `synchronized` 关键字或者在调用 `wait()` 、`notify()`和 `notifyAll()` 的方法内部使用 `synchronized`。

`wait()`应始终在循环中使用，否则可能产生虚假唤醒。

```java
class Share {
    int value = 0;

    public synchronized void incr() throws InterruptedException {
        while (value != 0) {
            wait();
        }

        value++;
        System.out.println(Thread.currentThread().getName() + "::" + value);

        notifyAll();
    }

    public synchronized void decr() throws InterruptedException {
        while (value != 1) {
            wait();
        }

        value--;
        System.out.println(Thread.currentThread().getName() + "::" + value);

        notifyAll();
    }
}

public class Demo {
    public static void main(String[] args) {
        Share share = new Share();

        new Thread(() -> {
            try {
                while (true) share.incr();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "A").start();

        new Thread(() -> {
            try {
                while (true) share.decr();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "B").start();
    }
}
```



### Condition

```java
class Share {
    int value = 0;
    
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void incr() throws InterruptedException {
        lock.lock();
        try {
            while (value != 0) {
                // 等待
            	condition.await();
        	}
        	value++;
        	System.out.println(Thread.currentThread().getName() + "::" + value); 
            // 唤醒
            condition.signalAll();
        } finally {
        	lock.unlock();   
        }
    }

    public synchronized void decr() throws InterruptedException {
        lock.lock();
        try {
			while (value != 1) {
                // 等待
            	condition.await();
        	}
        	value--;
        	System.out.println(Thread.currentThread().getName() + "::" + value); 
            // 唤醒
            condition.signalAll();
        } finally {
        	lock.unlock();
        }
    }
}
```



## 线程间定制化通信

线程按规定的顺序运行

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Share {
    private int flag = 1;

    private final Lock lock = new ReentrantLock();

    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    public void print5(int round) throws InterruptedException {
        lock.lock();
        try {
            while (flag != 1) {
                c1.await();
            }
            System.out.println("-----" + round + "-----");
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName());
            }
            flag = 2;
            c2.signal();
        } finally {
            lock.unlock();
        }
    }

    public void print10(int round) throws InterruptedException {
        lock.lock();
        try {
            while (flag != 2) {
                c2.await();
            }
            System.out.println("-----" + round + "-----");
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName());
            }
            flag = 3;
            c3.signal();
        } finally {
            lock.unlock();
        }
    }

    public void print15(int round) throws InterruptedException {
        lock.lock();
        try {
            while (flag != 3) {
                c3.await();
            }
            System.out.println("-----" + round + "-----");
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName());
            }
            flag = 1;
            c1.signal();
        } finally {
            lock.unlock();
        }
    }
}

public class Demo {
    public static void main(String[] args) {
        Share share = new Share();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.print5(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.print10(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    share.print15(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "C").start();
    }
}
```



## 集合的线程安全

ArrayList 在读写同时进行时，可能会产生 ConcurrentModificationException。CopyOnWriteArrayList 使用写时复制技术解决。

HashSet 线程不安全。CopyOnWriteArraySet 解决。

HashMap 线程不安全。ConcurrentHashMap 解决。



## 多线程锁

### synchronized 的锁

synchronized 实现同步的基础：Java 中的每一个对象都可以作为锁。

具体表现为以下3种形式：

- 对于普通同步方法，锁是当前实例对象
- 对于静态同步方法，锁是当前类的 Class对象
- 对于同步方法块，锁是 synchronized 括号里配置的对象



### 公平锁和非公平锁

```java
// 非公平锁
Lock lock = new ReentrantLock();
Lock lock = new ReentrantLock(false);

// 公平锁
Lock lock = new ReentrantLock(true);
```

非公平锁：仅有少量线程执行任务，效率高

公平锁：线程轮流执行任务，效率低



### 可重入锁

synchronized（隐式）和 Lock（显式）都是可重入锁。

```java
public class Demo {
    public static void main(String[] args) {
        Object o = new Object();
        
        new Thread(() -> {
            synchronized(o) {
                System.out.println(Thread.currentThread().getName() + " 外层");
                
                synchronized(o) {
                	System.out.println(Thread.currentThread().getName() + " 中层");
                
                	synchronized(o) {
                		System.out.println(Thread.currentThread().getName() + " 内层");
                        
                        // ...
            		}
            	}
            }
        }, "t1").start();
    }
}
```

以 synchronized 为例，调用下面的 add 方法，会抛出栈溢出异常。

```java
public class Demo {
    public synchronized void add() {
        add();
    }
}
```



### 死锁

jstack 检测死锁



## Callable 接口

相比于继承 Thread 和实现 Runnable 两种方法，实现 Callable 的方法可以有返回值。

Runnable 接口有实现类 FutureTask，FutureTask 构造可以传递 Callable。



## 辅助类

### CountDownLatch

```java
public class Demo {
    public static void main(String[] args) {
        
        CountDownLatch countDownLatch = new CountDownLatch(6);
        
        for(int i = 1; i <= 6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "号同学离开了教室");
                countDownLatch.countDown();
            },String.valueOf(i)).start();
        }
        
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "班长锁门了");
    }
}
```



### CyclicBarrier

```java
public class Demo {
    
    private static final int NUMBER = 7;
    
    public static void main(String[] args) {
        
        // 需要经过7次屏障，触发回调
        CyclicBarrier cyclicBarrier = new CyclicBarrier(NUMBER, ()->{
            System.out.println("集齐 7 颗龙珠召唤神龙");
        });
        
        for(int i = 1; i <= 6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "星龙珠收集到了");
                // await方法经过一次屏障
                cyclicBarrier.await();
            },String.valueOf(i)).start();
        }
    }
}
```



### Semaphore

```java
public class Demo {    
    public static void main(String[] args) {
        
        Semaphore semaphore = new Semaphore(3);
        
        for(int i = 1; i <= 6; i++) {
            new Thread(()->{
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + "号车进入停车位");
                TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                System.out.println(Thread.currentThread().getName() + "号车离开停车位");
                semaphore.release();
            },String.valueOf(i)).start();
        }
    }
}
```



## 读写锁

### 锁的类型

悲观锁：不允许并发

乐观锁：CAS

表锁：会发生死锁

行锁：会发生死锁

读锁：共享锁，会发生死锁

写锁：独占锁，会发生死锁



### 使用读写锁

```java
class MyCache {
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    private volatile Map<String, Object> map = new HashMap<>();
    
    public void put(String key, Object value) {
        rwLock.writeLock().lock();
        try{
            System.out.println(Thread.currentThread().getName() + "进行写操作");
        	TimeUnit.MICROSECONDS.sleep(300);
        	map.put(key, value);
        	System.out.println(Thread.currentThread().getName() + "完成写操作");
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    public Object get(String key) {
        rwLock.readLock().lock();
        try {
            Object result = null;
        	System.out.println(Thread.currentThread().getName() + "进行读操作");
        	TimeUnit.MICROSECONDS.sleep(300);
        	result = map.get(key);
        	System.out.println(Thread.currentThread().getName() + "完成读操作");
        } finally {
            rwLock.readLock().unlock();
        }
        return result;
    }
}

public class Demo {
    public static void main(String[] args) {
        MyCache myCache = new MyCache();
        
        for(int i = 1; i<=5;i++){
            final int num = i;
            new Thread(()->{
                myCache.put(num, num);
            }, String.valueOf(i)).start();
        }
        
        for(int i = 1; i<=5;i++){
            final int num = i;
            new Thread(()->{
                myCache.get(num);
            }, String.valueOf(i)).start();
        }
    }
}
```



### 读写锁降级

获取写锁 =》获取读锁 =》释放写锁（写锁降级为读锁） =》释放读锁

```java
public class Demo {
    public static void main(String[] args) {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();
        
        writeLock.lock();
        System.out.prinln("获取写锁");
        
        readLock.lock();
        System.out.println("获取读锁");
        
        writeLock.unlock();
        System.out.prinln("释放写锁");
        
        readLock.unlock();
        System.out.println("释放读锁");
    }
}
```



## 阻塞队列

### 实现类

ArrayBlockingQueue

LinkedBlockingQueue

DelayQueue

。。。



### 方法

|      | 抛出异常  | 特殊值   | 阻塞   | 超时                 |
| ---- | --------- | -------- | ------ | -------------------- |
| 添加 | add(e)    | offer(e) | put(e) | offer(e, time, unit) |
| 删除 | remove()  | poll()   | take() | poll(time, unit)     |
| 检查 | element() | peak()   |        |                      |

```java
public class Demo {
    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(3);
        
        // 第一组
        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        System.out.println(blockingQueue.add("c"));
        System.out.println(blockingQueue.element());
        
        // System.out.println(blockingQueue.add("d"));
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        
        // 第二组
        System.out.println(blockingQueue.offer("a"));
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));
        System.out.println(blockingQueue.offer("d"));
        
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        
        // 第三组
        blockingQueue.offer("a");
        blockingQueue.offer("b");
        blockingQueue.offer("c");
        // blockingQueue.offer("d");
        
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        
        // 第四组
        System.out.println(blockingQueue.offer("a"));
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));
        System.out.println(blockingQueue.offer("d", 3L, TimeUnit.SECONDS));
    }
}
```



## 线程池

### 常用线程池

```java
public class Demo {
    public static void main(String[] args) {
        
        ExecutorService threadPool1 = Executors.newFixedThreadPool(5);
        
        ExecutorService threadPool2 = Executors.newSingleThreadExecutor();
        
        ExecutorService threadPool3 = Executors.newCachedThreadPool();
        
        try {
            for(int i = 1; i<=10;i++){
            	threadPool.execute(()->{
                	System.out.println(Thread.currentThread().getName() + "执行中");
            	})
        	}
        } finally {
            threadPool.shutdown();
        }
    }
}
```



### ThreadPoolExecutor

Executors 的方法通过 ThreadPoolExecutor 创建线程池

```java
public ThreadPoolExecutor(int corePoolSize,
                         int maximumPoolSize,
                         long keepAliveTime,
                         TimeUnit unit,
                         BlockingQueue<Runnable> workQueue,
                         ThreadFactory threadFactory,
                         RejectedExecutionHandler handler) {
    // ...
}
```

- corePoolSize：常驻线程数量（核心）
- maximumPoolSize：最大线程数量
- keepAliveTime：线程的存活时间
- unit：时间单位
- workQueue：阻塞队列
- threadFactory：线程工厂
- handler：拒绝执行处理器



### 线程池工作流程

调用 executor()，先使用常驻线程进行处理，常驻线程不足则进入阻塞队列，阻塞队列不足，则创建新线程处理新任务，阻塞队列的任务仍然等待，线程数达到最大数量后，调用拒绝执行处理器。



### 拒绝执行处理器

- AbortPolicy（默认）：直接抛出 RejectedExecutionException 异常阻止系统正常运行
- CallerRunsPolicy：一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将某些任务回退到调用者，从而降低新任务的流量。
- DiscardOldestPolicy：抛弃队列中等待最久的任务，然后把当前任务加入队列中，尝试再次提交当前任务。
- DiscardPolicy：该策略默默地丢弃无法处理的任务，不予任何处理也不抛出异常。如果允许任务丢失，这是最好的策略。



### 自定义线程池

```java
public class Demo {
    public static void main(String[] args) {
        ExcutorService ThreadPool = 
            new ThreadPoolexecutor(2,
                                   5,
                                   2L,
                                   TimeUnit.SECONDS,
                                   new ArrayBlockingQueue<>(3),
                                   Executors.defaultThreadFactory(),
                                   new ThreadPoolExecutor.AbortPolicy()
                                  );
    }
}
```



## 合并分支框架

Fork/join

```java
class MyTask extends RecursiveTask<Integer> {
    
    private static final Integer VALUE = 10;
    private int begin;
    private int end;
    private int result;
    
    public MyTask(int begin, int end) {
        this.begin = begin;
        this.end = end;  	
    }
    
    protected Integer compute(){
        if((end-start) <= VALUE) {
            for (int i=begin; i<= end; i++){
                result = result + i;
            }
        } else {
            int middle = (begin + end)/2;
            MyTask leftTask = new MyTask(begin, middle);
            MyTask rightTask = new MyTask(middle, end);
            leftTask.fork();
            rightTask.fork();
            result = leftTask.join() + rightTask.join();
        }
        return result;
    }
}

public class Demo {
    public static void main(String[] args) {
        MyTask myTask = new MyTask(0, 100);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> forkJoinTask = forkJoinPool.submit(myTask);
        Integer result = forkJoinTask.get();
        System.out.println(result);
        forkJoinPool.shutdown();
    }
}
```





## 异步回调

```java
public class Demo {
    public static void main(String[] args) {
        // 异步调用，无返回值
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(()->{
            System.out.println(Thread.currentThread().getName() + "completableFuture1");
        });
        completableFuture1.get();
        
        // 异步调用，有返回值
        CompletableFuture<Void> completableFuture2 = CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread().getName() + "completableFuture2");
            return 1024;
        });
        completableFuture2.whenComplete((t, u)->{
            System.out.println("t=" + t);
            System.out.println("u=" + u);
        }).get();
    }
}
```

