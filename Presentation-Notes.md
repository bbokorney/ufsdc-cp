# Outline

* Introduce some basic concurrent programming concepts
* Write some code: take a "tour" of some concurrent programming topics
* Make an IRC like group chat system

# Basic Ideas
Who has
* taken OS?
* heard of processes, threads, and scheduling?
* done any concurrent/parallel programming?
* used locking, race conditions, atomic operations, deadlocks, etc.?

## Terminology
* Difference between concurrent and parallel programming

**concurrent** programming == writing code that has the *potential* to execute in *parallel* if executed on the appropriate hardware (a machine with multiple CPUs/cores)
**parallel** programming == concurrent when the program actually runs in parallel
**multi-threaded** programs == any programs that use multiple threads (duh)

In other words, these are the same when you run on hardware that allows parallel execution. For all intents and purposes, think of these terms as referring to the same thing.

## Things happen in parallel
Most modern CPU architectures have more than 1 core. These cores can execute instructions at the same time.

## Different Kinds of Parallel Programmming 
There are different ways that we can use parallel programming to help us. Each of these styles is solving a different problem.
- Multi-threaded services
	- Webservers, DNS servers, databases, chat systems
	- Almost any application that supports multiple connections
- Parallel computations
	- big computations which can easily be decomposed into problems
	- usually on architectures with hundreds of cores (like GPUs)
	- languages like CUDA
	- example: Bitcoin mining
- Distributed computations
	- parallelize programs across multiple machines
	- not always considered "parallel" programming, but it accomplishes the same thing in a different way
	- REALLY big computations
		- long running
		- lots of data
	- "Big Data"
	- example: Map Reduce

## Why do we use parallel programming?

Parallel programming gives us lots of benefits. Two of the most important are **responsiveness** and **resource utilization**.

### Responsiveness

This is the property that services which handle thousands of users are very interested in.

- Multi-threaded applications can handle thousands of different tasks at simultaneously
- Clients of a service don't want to have to wait forever for a response 
- We can't allow one user to take over all the resources
- You just can't achieve this with a single-threaded service (as we'll see later)

### Resource Utilization 
This is the property that 

- We don't make CPUs faster anymore. We just add more cores!
- Remember Amdahl's Law?
- To effectively utilize all of the computational resources of modern architectures, we have to take advantage of the multiple cores 	or even multiple machines.
- The theory is if you have a task that takes a certain amount of time `T`, if you split it up between `N` differenet cores/computers, the computation takes `T/N` time to complete (in theory)!

## This is a big field
This *very* brief overview only scratches the surface. Today we're just going to focus on multi-threaded applications/services.

# Let's CODE!!

## Example 1 - Blocking and Sleeping

Who has ever written a program like this?

```java
import java.util.Scanner;

class Main {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("What's your name? ");
		String name = in.nextLine();
		System.out.println("Nice to meet you " + name + "!");
	}
}
```

I think we all probably know what this does.

But have you ever thought about what's going on here?

```java
String name = in.nextLine();
```
What happens when we get to this line? Somehow, the entire program just automagically stops and waits for user input. This isn't magic; it's called **blocking**. A thread blocks when it calls a function and waits until that function returns. While blocking, the thread is completely halted, doing nothing.

A related concept to blocking is **sleeping**, which is nearly identical to blocking in that the thread temporarily halts its execution, but in this case it's done for a predetermined amount of time.

Let's look at an example.

```java
import java.util.Scanner;

class BlockAndSleep {
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		System.out.print("How many seconds should I sleep for? ");
		int sleepTime = in.nextInt();
		System.out.println("Sleeping for " + sleepTime + " seconds...");
		Thread.sleep(sleepTime * 1000);
		System.out.println("Nap time over!");
	}
}
```

The important part here is the call to `Thread.sleep()`, which sleeps the current thread for the specified number of milliseconds.

Okay, this is boring. So I don't put all of you to sleep, let's move on and actually make some threads!

## Example 2 - Simple Thread Creation

So here's our first thread.

```java
class VerboseForLoop {
	public static void main(String[] args) {
		for(int i = 0; i < 10; ++i) {
			NumPrinter np = new NumPrinter(i);
			Thread t = new Thread(np);
			t.start();
		}
	}	
}

class NumPrinter implements Runnable {
	int num;
	public NumPrinter(int num) {
		this.num = num;
	}
	public void run() {
		System.out.println(num);
	}
}
```

Here now we have to talk about some Java specific stuff about threading. First is the `Thread` class. You saw how we called its static `sleep()` method in the previous example. This is a class Java provides to gives access to threads, of course! The `Thread` class has a few methods. The most important method of this class (and the only one we're using here) is the `start()` method, which actually starts a thread once it's constructed

There are several ways to create threads, but the way we're doing it here is the most common, which is to give the `Thread` an instance of an object which implements the `Runnable` interface. The `Runnable` interface is incredibly simple, so we'll show it here.

```java
interface Runnable {
	void run();
}
```

Yep, that's it. The `Runnable` interface has just one method, `run()`. When we give a `Thread` an instance of a `Runnable`, when the thread begins executing, it simply calls the `run()` method of the `Runnable` we gave it, and that `run()` method is executed on the new thread.

Let's go back to our example. We see that in the `main()` method, we have a simple for loop. For each iteration of the loop, we create an instance of the `NumPrinter` class, passing `i` into the constructor. Next, we create  a new `Thread`, passing our `NumPrinter` into its constructor. Finally, we call the `start()` method on the `Thread` class. Calling `start()` does *not* block, but instead returns immediately, allowing the loop to continue onto the next iteration.

Now let's look at the `NumPrinter` class. We can see from its declaration that the class implements the `Runnable` interface we talked about. This is why we can pass an instance of this class into th constructor of the `Thread` we make. The rest of the class has a very straightforward implementation. All we do is store an instance of a number, and when the the `run()` method gets called, we just print that number to standard out. Easy!

We're not going to run this code yet. First, we're going to rewrite it to make it more concise.

## Example 3 - Anonymous Classes

### A crash course on anonymous classes
Now I'm going to rewrite this example, making it much more concise. It's going to look a lot different, but it's going to do the *exact* same thing. To make the code more concise, we're going to use a feature of Java called *anonymous classes*, which are instances of a class which we have not defined already. This sounds really confusing I'm sure, so let's just look at the code.

```java
class ConciseForLoop {
	public static void main(String[] args) {
		for(int i = 0; i < 10; ++i) {
			final int currentNum = i;
			new Thread(new Runnable() {
				public void run() {
					System.out.println(currentNum);
				}
			}).start();
		}
	}	
}
```

The first thing you should notice is our `NumPrinter` class is gone. We no longer need to define it ahead of time because we're replacing it with an anonymous class which does the same thing. The declaration of the for loop is identical to the previous example. Let's focus in on the body of the for loop.

```java
final int currentNum = i;
new Thread(new Runnable() {
	public void run() {
		System.out.println(currentNum);
	}
}).start();

```
The first thin we do is declare a new `final int` and we copy the value of `i` into it. Ignore this for now. Next we see that we're calling the constructor of the `Thread` class. We're not actually saving a reference to this `Thread` object after we create it. If you noticed in the previous example, as soon as we executed `t.start()`, we didn't need that object anymore, so there was no point in keeping the reference to it. Here, we're just creating the`Thread` and immediately calling  `start()`.

Now let's look at that `new Runnable()` part. This is our anonymous class. What we're saying is we want a new object which has the interface of Runnable, and then between the enclosing brackets, we define the implementations of each of the methods for that interface. If we were creating an anonymous class of an interface with multiple methods, we'd define their implementations one after another in the enclosing brackets, just like we would in a class definition. Here, we just have the single `void run()` method to implement. We implement this method with a single print statement, just like in the previous example. The only difference is now we're printing the value of `currentNum`. Take note: `currentNum` was *not* defined in our class. How are we able to access within this anonymous class? This is called a *closure*, and it's a feature of anonymous classes. Any final variables which are defined in the scope in which we define our anonymous class are also within the scope of the definition of the anonymous class. Note: any *final* variables, which is exactly why we copy the value of `i` into a `final int`. For those of you who don't remember what a `final` variable is, it's simply a variable who's value cannot be modified. If we tried to print out the value of `i` in our `Runnable`, we'd get a compile error.

Okay, that was a lot of info that is not necessarily related to threading itself, but I wanted to include this example because this a very common pattern for defining `Runnable` objects in Java, and is useful when the implementation of your `Runnable` is short. We'll see an example later on where we use a `Runnable` which a more complicated implementation, and we'll actually define a class for our `Runnable`.

### Let's run this code!
So what actually happens when we run this code? It looks like the output should be obvious. But if you've never done any concurrent programming before, I'm going to guess that the actual output will surprise you. Here's the output I got when I ran this code:

```
1
9
8
7
3
6
5
4
2
0
```
At this point, you might be scratching your head, wondering why this didn't print `0-9` in order. As if it's weird enough that the numbers didn't print in the order that we created the threads to print them, let's look at the output of another execution:

```
0
2
3
6
5
4
1
9
8
7
```

It's completely different from the first execution!! This isn't some kind of joke I'm playing on you, these are actual outputs from running the above code. So what's going on?

The above executions highlight a very important property of almost all code executed in parallel: *non-determinism*, which is simply the property that statements being executed by multiple threads can be executed in any arbitrary order. This also means that the executions of threads can be interleaved, which is to say that if I have threads *A* and *B*, each executing instructions *a1-a3* and *b1-b3*, respectively, This is a possible execution:

```
a1
b1
a2
b2
b3
a3 
```

It is important to note that statements from the *same* thread won't be reordered*, but statements from *different* threads can be interleaved in any order.

*This is not entirely true, as compilers will reorder statements in order to speed up execution of programs. This happens in sequential programs as well. But of course, the compiler will only reorder statements which don't depend on each other, so you can still reason about code with the assumption that statements actually execute in the order you define them.

To explore this idea of statements being interleaved, let's move on to our next example.

```java
class NestedForLoops {
	public static void main(String[] args) {
		for(int i = 0; i < 3; ++i) {
			new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < 5; ++i) {
						System.out.println("["+Thread.currentThread().getName()+"] "+i);
					}
				}
			}).start();
		}
	}
}
```

This example stil has the same basic structure as our previous one, where we have a loop in our `main()` methods which starts up a number of threads. But now we've added what each thread is going to do. Now, each thread has a for loop of it's own in which each iteration of the loop it's going to print the value of the loop counter as well as the ID of the current thread. In Java, threads are assigned an ID, which we can access with the static call to `Thread.currentThread().getName()`. We want to print the name of our current thread so we can see how the statements of the threads get interleaved. Let's look at an example output:

```
[Thread-0] 0
[Thread-2] 0
[Thread-1] 0
[Thread-2] 1
[Thread-0] 1
[Thread-2] 2
[Thread-1] 1
[Thread-2] 3
[Thread-0] 2
[Thread-2] 4
[Thread-1] 2
[Thread-0] 3
[Thread-1] 3
[Thread-0] 4
[Thread-1] 4
```

As you can see, there is no guarantee on the ordering of which thread will get to execute it's print statement. But do notice that the ordering on the print statments for each *individual* thread has been preserved. If we take out the printing for threads 1 and 2 and just look at the output for thread 0, we see 

```
[Thread-0] 0
[Thread-0] 1
[Thread-0] 2
[Thread-0] 3
[Thread-0] 4
```

that thread 0 executed each iteration of its own for loop sequentially. The same goes for threads 1 and 2 as well.

A quick aside: notice that we're using a loop counter of `i` in both the outer and inner for loop. How is this allowed? When referencing a variable inside the scope of an anonymous class, that variable references the version of that variable in the "closest" scope. In this case, because we defined `i` inside of our anonymous class, that is the version of `i` we're using in the inner for loop. In the previous example, when we referenced `currentNum`, the compiler first looked in the scope of the anonymous class for the declaration of that variable. It could not find it there, and so then moved to the next closest scope, which was that of the for loop. There it found the declaration of `currentNum`, and thus that is the variable we are referencing inside of our `Runnable`.

## Example 4 - The Dangers of Non-Determinism

This whole concept that programs can execute in a non-deterministic manner might be off-putting to you, and it should. All of the old techniques and guarantees we had when dealing with sequential programs is gone. And this ends up having some servious implications on the *correctness* of our programs. Let's look at our next example.

```java
class NonWaitingUnsafeCounter {
	
	static int counter;

	static void increment() {
		counter++;
	}

	public static void main(String[] args) throws InterruptedException {
		int numThreads = Integer.parseInt(args[0]);
		final int numIncrements = Integer.parseInt(args[1]);
		Thread[] threads = new Thread[numThreads];
		for(int i = 0; i < numThreads; ++i) {
			final int currentNum = i;
			threads[i] = new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < numIncrements	; ++i) {
						increment();
					}
				}
			});
			threads[i].start();
		}

		System.out.println(counter);
	}

}
```

This is example may look silly, but it's going to illustrate one of the fundamental dangers of concurrent programming. All we've done here is defined a `int` as a counter, and a method `increment()` which just increments our counter variable. Each thread is again has it's own for loop, and each time through the loop it calls the `increment()` method. The last thing we do in this code is print the value of the counter after creating all of the threads. We're also using command line arguments to tell our program how many threads to create and how many times each thread should increment the counter. Therefore, the final value of our counter should be `numThreads * numIncrements`. Seems straightforward enough, let's run it and see what happens.

```
$ java NonWaitingUnsafeCounter 10 10
87
```

Um.... wat? If my multiplication skills serve me, I think that value should have been 100, right? Let's try again.

```
$ java NonWaitingUnsafeCounter 10 10
60
```

Again, we get the wrong result. So what's going on?

Well, one possibility might be what we saw earlier on where our `main()` method finished executing before our thread did. If our main method prints the value of the counter before each thread is done executing, this would of course cause the value to be incorrect. So let's modify this program to wait for each thread to complete, and then we should see the correct value.

```java
class WaitingUnsafeCounter {
	
	static int counter;

	static void increment() {
		counter++;
	}

	public static void main(String[] args) throws InterruptedException {
		int numThreads = Integer.parseInt(args[0]);
		final int numIncrements = Integer.parseInt(args[1]);
		Thread[] threads = new Thread[numThreads];
		for(int i = 0; i < numThreads; ++i) {
			final int currentNum = i;
			threads[i] = new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < numIncrements; ++i) {
						increment();
					}
				}
			});
			threads[i].start();
		}

		for(int i = 0; i < threads.length; ++i) {
			threads[i].join();
		}

		System.out.println(counter);

	}

}
```

This code is nearly identical to the previous example. All we're doing now is creating an array containing each of the threads we create, and then before we print out the value of our counter, we go through our array of threads, and `join()` with each of them. Note that if a thread has already completed it's execution, `join()` returns immediately. The effect of this loop is that our main method will block until all threads have completed execution. We then print out the value of our counter. This should fix our problem, right? Let's run this code:

```
$ java WaitingUnsafeCounter 100 100
9782
```

Nope, still wrong. Now you may have re run the code with 10 threads and 10 increments per thread and saw a correct value, or maybe it was also incorrect. Either way, this code does still not produce a correct result for every input, and now I'll explain why.

The core of the problem here is still non-determinism, but the way it's causing our program to produce an incorrect result is much more subtle than the example where the numbers were printed out of order, and it to fully understand why it's not working, we have to think about this program in terms of the assembly which is being executed.

How to we increment an integer in a program? In Java and probably every other higher level language, it's just one statement. But remember, this higher level code needs to be converted into assembly. How do we increment a number in assembly?

This isn't going to be an actual assembly language, I just want to illustrate the point. Let's assume the value we want to increment is in memory adddress `$a`.


```
lw $tmp1 $a 			# load the int from memory at address $a into register $tmp1
addi $tmp2 $tmp1 1 	# add 1 to the value in $tmp1, store result in $tmp2
sw $tmp2 $a 			# store the value in $tmp2 to memory at address $a
```

First, we load the value from memory into the register `$tmp1`. Net we add 1 to the value of `$tmp1` and store that result in register `$tmp2`. Finally, we store the value in `$tmp2` back in the memory address of `$a`.

It seems so simple. So what could go wrong here? Well, let's imagine that there are now two threads trying to execute these three instructions and think about how we could interleave them.

```
Assume the value at $a is 0
Thread 1 							Thread 2

1. lw $tmp1 $a 												# $tmp1 = 0
2. 									lw $tmp3 $a  			# $tmp3 = 0	
3. addi $tmp2 $tmp1 1 										# $tmp2 = 1
4. 									addi $tmp4 $tmp3 1		# $tmp4 = 1
5.	sw $tmp2 $a 											# $a = 1
6. 									sw $tmp4 $a 			# $a = 1 <- BAD!!!! 
															# should be $a = 2 because 
															# two increments have taken place
```

As you can see, two attempts to increment the value produced an incorrect result of one of the increments overwriting the value of the other. How do we fix this?

## Example 5 - Making Things Safer With Synchronization
What in order fix the problem we saw above, we need to ensure that each thread is able to execute these three instructions without being interrupted by another thread. In the multi-threading world, we would say that the increment operation needs to be done *atomically*, which means it is guaranteed to happen without interruption.

If we want prevent threads from accessing incrementing our counter at the same time, clearly the threads are going to have to take turns and wait for each other. A thread must be able to detect when another thread is attempting to perform the increment, wait until the other thread is done, and then be alerted when that thread has completed it's increment. The most common mechanism by which to do this is called the *lock*.

Locks are simple objects which have (basically) two methods: `lock()`, and `unlock()`. The semantics of these methods work as follows.

`lock()` attempts to acquire the lock. If the lock is currently free (no one else has tried to get it), this method returns immediately, and the current thread now has the lock. If another thread does have the lock, the call to `lock()` blocks, and only returns when the lock becomes free.

'unlock()' of course release the lock and alerts threads who are waiting on it that it has been released so they can attempt to acquire it. Calling `unlock()` will only do something if you currently have the lock, preventing a thread that doesn't own the lock from unlocking it.

Let's jump into some code that will show how locks work in Java, which should clear things up.

```java
import java.util.concurrent.locks.*;

class LockingCounter {
	
	static int counter;

	static Lock lock = new ReentrantLock();

	static void increment() {
		counter++;
	}

	public static void main(String[] args) throws InterruptedException {
		int numThreads = Integer.parseInt(args[0]);
		Thread[] threads = new Thread[numThreads];
		for(int i = 0; i < numThreads; ++i) {
			final int currentNum = i;
			threads[i] = new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < 10; ++i) {
						try {
							lock.lock();
							increment();
						} finally {
							lock.unlock();
						}
					}
				}
			});
			threads[i].start();
		}

		for(int i = 0; i < threads.length; ++i) {
			threads[i].join();
		}

		System.out.println(counter);

	}

}
```

This code is identical to the previous example, we've only added a few lines to add a lock. We statically intialize the lock to be a `ReentrantLock`. It's unimportant for this example what a reentrant lock is, but briefly it's a lock you can call lock on multiple times without unlocking. Let's zoom in on body of the for loop to see how the lock is used.

```java
try {
	lock.lock();
	increment();
} finally {
	lock.unlock();
}
```

You can see that we've surrounded our use of the lock with a `try/finally` block. You might be familiar with this construct from exception handling. The reason we use it here is that is is *critical* that we remember to unlock the lock. If one thread acquired the lock and forgot to unlock it, all of the other threads would be stuck waiting for it to unlock (which would never happen), and our program would just sit there forever in a state called *deadlock*.

Given how critical it is to unlock our lock, it is a good practice to always wrap usages of a lock with a try/finally block where you call `lock()` in the `try` block and `unlock()` in the finally block. That way, no matter what happens after you acquire the lock, it always gets unlocked in the end.

First, we make our call to `lock()`. Remember, this will either return immediately if the lock is free, or it will block until we can get the lock. Either way, after our call to `lock()` returns, we are safe to go ahead and increment our counter by calling the `increment()` method. When we're done with the increment, we then release the lock to let other threads increment the counter.

So now that we've added this, what happens when we run this code now?

```
$ java LockingCounter 100 100
10000
```

We can re-run this as many times as we want, and it's going to give the correct answer.

As a final example, I'll show you another way to make the use of the lock even more concise. 

In the previous example, we saw that we acquired the lock, called the `increment()` method, and then released the lock. It is often the case that there is just one method we need to call with the lock. Because this is such a common case, Java has some syntax that will make this much easier for us to write.

```java
class SynchronizedCounter {
	
	static int counter;

	static synchronized void increment() {
		counter++;
	}

	public static void main(String[] args) throws InterruptedException {
		int numThreads = Integer.parseInt(args[0]);
		final int numIncrements = Integer.parseInt(args[1]);
		Thread[] threads = new Thread[numThreads];
		for(int i = 0; i < numThreads; ++i) {
			final int currentNum = i;
			threads[i] = new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < numIncrements; ++i) {
						increment();
					}
				}
			});
			threads[i].start();
		}

		for(int i = 0; i < threads.length; ++i) {
			threads[i].join();
		}

		System.out.println(counter);

	}

}
```

You might be wondering what's different between this example and the one we saw before the lock was introduced. But look closely at the declaration of our `increment()` method, and you'll see the `synchronized` keyword. Declaring a method as synchronized ensures that only one thread will execute the method at one time. Essentially, you can imagine that before each call to this method, threads will have to acquire a lock, and after they finish executing the method, they will release the lock. But Java handles all these details for you.

```
$ java SynchronizedCounter 100 100
10000
```

We can see that this program will behave identical to our previous example, but with less code.

# Conclusion
I know that was a lot of info, but I wanted everyone to get a good idea of the most basic concepts of concurrent programming. There's much, much more to explore in this field. Hopefully now you'll have the confidence to learn more on your own.


Order of code files:
1. Block.java
2. BlockAndSleep.java
3. SleepAndWait.java
4. VerboseForLoop.java
5. ConciseForLoop.java
6. NestedForLoops.java
7. NonWaitingUnsafeCounter.java
8. WaitingUnsafeCounter.java
9. LockingCounter.java
10. SynchronizedCounter.java

