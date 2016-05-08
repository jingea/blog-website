category: JavaSE
date: 2015-09-08
title: Quasar’s Actor System
---


To use the terms we’ve learned so far, an actor is a strand that owns a single channel with some added lifecycle management and error handling. But this reductionist view of actors does them little justice. Actors are fundamental building blocks that are combined to build a fault-tolerant application. If you are familiar with Erlang, Quasar actors are just like Erlang processes.

An actor is a self-contained execution unit with well-defined inputs and outputs. Actors communicate with other actors (as well as regular program threads and fibers) by passing messages.

Note: Actors may write to and read from channels other than their own mailbox. In fact, actors can do whatever regular fibers can.

Creating Actors
All actors extends the Actor class. The constructor takes the actor’s name (which does not have to be unique, and may even be null), and its mailbox settings (of type MailboxConfig).

MailboxConfig defines the mailbox size (the number of messages that can wait in the mailbox channel), with -1 specifying an unbounded mailbox, and an overflow policy. The overflow policy works the same as for plain channels, except that the THROW policy doesn’t cause an exception to be thrown in the sender if the mailbox capacity is exceeded, but rather throws an exception into the receiving actor (the exception will be thrown when the actor next blocks on a receive).

An actor is required to implement the doRun method. This method is the actor body, and is run when the actor is spawned.

It is preferable to subclass BasicActor rather than Actor; BasicActor provides the ability to perform selective receives (more on that later).

Spawning Actors
Actors can run in any strand – fiber or thread, although you’d usually want to run them in fibers. Actor implements SuspendableCallable so you may run it by setting it as the target of a fiber or a thread (via Strand.toRunnable(SuspendableCallable))). A simpler way to start an actor is by calling

actor.spawn()
which assigns the actor to a newly created fiber and starts it, or

actor.spawnThread()
which assigns the actor to a newly created thread and starts.

An actor can be joined, just like a fiber. Calling get on an actor will join it and return the value returned by doRun.

Note: Just like fibers, spawning an actor is a very cheap operation in both computation and memory. Do not fear creating many (thousands, tens-of-thousands or even hundreds-of-thousands) actors.

Sending and Receiving Messages, ActorRef
The spawn method returns an instance of ActorRef. All (almost) interactions with an actor take place through its ActorRef, which can also be obtained by calling ref() on the actor. The ActorRef is used as a level of indirection that provides additional isolation for the actor (and actors are all about isolation). It enables things like hot code swapping and more.

ActorRef.self() is a static function that returns the currently executing actor’s ref, and Actor.self() is a protected member function that returns an actor’s ref. Use them to obtain and share an actor’s ref with other actors.

Note: An actor must never pass a direct reference to itself to other actors or to be used on other strands. However, it may share its ActorRef freely. In fact, no class should hold a direct Java reference to an actor object other than classes that are part of the actor.

The ActorRef allows sending messages to the actor’s mailbox. In fact, ActorRef implements SendPort so it can be used just like a channel.

An actor receives a message by calling the receive method. The method blocks until a message is available in the mailbox, and then returns it. Another version of receive blocks up to a given duration, and returns null if no message is received by that time.

Normally, an actor is implements a loop similar to this one:

@Override
protected Void doRun() {
    for(;;) {
        Object msg = receive();
        // process message
        if (thatsIt())
            break;
    }
    return null;
}
Note: Because messages can be read by the actor at any time, you must take great care to only send messages that are immutable, or, at the very least, ensure that the sender does not retain a reference to the message after it is sent. Failing to do so may result in nasty race-condition bugs.

Actors vs. Channels
One of the reasons of providing a different receive function for actors is because programming with actors is conceptually different from just using fibers and channels. I think of channels as hoses pumping data into a function, or as sort of like asynchronous parameters. A fiber may pull many different kinds of data from many different channels, and combine the data in some way.

Actors are a different abstraction. They are more like objects in object-oriented languages, assigned to a single thread. The mailbox serves as the object’s dispatch mechanism; it’s not a hose but a switchboard. It’s for this reason that actors often need to pattern-match their mailbox messages, while regular channels – each usually serving as a conduit for a single kind of data – don’t.

Selective Receive
An actor is a state machine. It usually encompasses some state and the messages it receives trigger state transitions. But because the actor has no control over which messages it receives and when (which can be a result of either other actors’ behavior, or even the way the OS schedules threads), an actor would be required to process any message and any state, and build a full state transition matrix, namely how to transition whenever any messages is received at any state.

This can not only lead to code explosion; it can lead to bugs. The key to managing a complex state machine is by not handling messages in the order they arrive, but in the order we wish to process them. If your actor extends BasicActor, there’s another form of the receive method that allows for selective receive. This method takes an instance of MessageProcessor, which selects messages out of the mailbox (a message is selected iff MessageProcessor.process returns a non-null value when it is passed the message). Alternatively (to extending BasicActor, you can make use of the SelectiveReceiveHelper class.

Let’s look at an example. Suppose we have this message class:

class ComplexMessage {
    enum Type { FOO, BAR, BAZ, WAT }
    final Type type;
    final int num;
    public ComplexMessage(Type type, int num) {
        this.type = type;
        this.num = num;
    }
}
Then, this call:

ComplexMessage m = receive(new MessageProcessor<ComplexMessage, ComplexMessage>() {
        public ComplexMessage process(ComplexMessage m)
          throws SuspendExecution, InterruptedException {
            switch (m.type) {
            case FOO:
            case BAR:
                return m;
            default:
                return null;
            }
        }
    });
will only return a message whose type value is FOO or BAR, but not BAZ. If a message of type BAZ is found in the mailbox, it will remain there and be skipped, until it is selected by a subsequent call to receive (selective or plain).

MessageProcessor.process can also process the message inline (rather than have it processed by the caller to receive), and even call a nested `receive:

protected List<Integer> doRun() throws SuspendExecution, InterruptedException {
    final List<Integer> list = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
        receive(new MessageProcessor<ComplexMessage, ComplexMessage>() {
            public ComplexMessage process(ComplexMessage m)
              throws SuspendExecution, InterruptedException {
                switch (m.type) {
                case FOO:
                    list.add(m.num);
                    receive(new MessageProcessor<ComplexMessage, ComplexMessage>() {
                        public ComplexMessage process(ComplexMessage m)
                          throws SuspendExecution, InterruptedException {
                            switch (m.type) {
                            case BAZ:
                                list.add(m.num);
                                return m;
                            default:
                                return null;
                            }
                        }
                    });
                    return m;
                case BAR:
                    list.add(m.num);
                    return m;
                default:
                    return null;
                }
            }
        });
    }
    return list;
}
If a FOO is received first, then the next BAZ will be added to the list following the FOO, even if a BAR is found in the mailbox after the FOO, because the nested receive in the case FOO: clause selects only a BAZ message.

Note: MessageProcessor will become much less cumbersome in Java 8 with the introduction of lambda expressions.

Note: A simple, fluent API for selecting messages based on simple criteria is provided by the MessageSelector class (in the co.paralleluniverse.actors.behaviors) package.

There are several actor systems that do not support selective receive, but Erlang does, and so does Quasar. The talk Death by Accidental Complexity, by Ulf Wiger, shows how using selective receive avoids implementing a full, complicated and error-prone transition matrix. In a different talk, Wiger compared non-selective (FIFO) receive to a tetris game where you must fit each piece into the puzzle as it comes, while selective receive turns the problem into a jigsaw puzzle, where you can look for a piece that you know will fit.

A word of caution: Using selective receive in your code may lead to deadlocks (because you’re essentially saying, I’m going to wait here until a specific message arrives). This can be easily avoided by always specifying a timeout (with the :after millis clause) when doing a selective receive. Selective receive is a powerful tool that can greatly help writing readable, maintainable message-handling code, but don’t over-use it.

Error Handling
The actor model does not only make concurrency easy; it also helps build fault-tolerant systems by compartmentalizing failure. Each actor is it’s own execution context - if it encounters an exception, only the actor is directly affected (like a thread, only actors are lightweight). Unlike regular functions/objects, where an exception has to be caught and handled immediately on the callstack, with actors we can completely separate code execution from error handling.

In fact, when using actors, it is often best to to follow the philosophy laid out by Joe Armstrong, Erlang’s chief designer, of “let it crash”. The idea is not to try and catch exceptions inside an actor, because attempting to catch and handle all exceptions is futile. Instead, we just let the actor crash, monitor its death elsewhere, and then take some action.

The principle of actor error handling is that an actor can be asked to be notified of another actor’s death and its cause. This is done through linking or watching.

Linking and Watching Actors
Linking two actors causes the death of one to throw an exception in the other. Two actors are linked with the link method of the Actor class, and can be unlinked with the unlink method. A link is symmetric: a.link(b) has the exact same effect of b.link(a). The next section explains in detail how the linking mechanism works.

A more robust way of being notified of actor death than linking is with a watch (called monitor in Erlang; this is one of the few occasions we have abandoned the Erlang function names). To make an actor watch another you use the watch method. When a watched actor, its watcher actor (or many watching actors) receives an ExitMessage, explained in the next section. Unlike links, watches are asymmetric (if A watches B, B does not necessarily watch A), and they are also composable: the watch method returns a watch-id object that identifies the particular watch; every ExitMessage contains that watch-id object that uniquely identifies the watch that caused the message to be received. If an actor calls the watch method several times with the same argument (i.e. it watches the same actor more than once), a message will be received for each of these different watches. A watch can be undone with the unwatch method.

Lifecycle Messages and Lifecycle Exceptions
When actor B that is linked to or watched by actor A dies, it automatically sends an ExitMessage to A. The message is put in A’s mailbox and retrieved when A calls receive or tryReceive, but it isn’t actually returned by those methods.

When receive (or tryReceive) is called, it takes the next message in the mailbox, and passes it to a protected method called filterMessage. Whatever filterMessage returns, that’s the message actually returned by receive (or tryReceive), but it filterMessage returns null, receive will not return and wait for the next message (and tryReceive will check if another message is already available, or otherwise return null). The default implementation of filterMessage always returns the message it received unless it is of type LifecycleMessage, in which case it passes it to the protected handleLifecycleMessage method.

handleLifecycleMessage examines the message. If it is an ExitMessage (which extends LifecycleMessage), it checks to see if it’s been sent as a result of a watch (by testing whether its getWatch method returns a non-null value). If it is, it’s silently ignored. But if it’s a result of a linked actor dying (getWatch() returns null), the method throws a LifecycleException. This exception is thrown, in turn, by actor A’s call to receive (or tryReceive). You can override handleLifecycleMessage to change this behavior.

If you do not want actor A to die if linked actor B does, you should surround the call to receive or tryReceive with a try {} catch(LifecycleException) {} block.

While you can override the filterMessage or the handleLifecycleMessage method, but will seldom have reason to override the latter, and almost never should override the former.

Registering Actors
Registering an actor gives it a public name that can be used to locate the actor. You register an actor with the register method of the Actor class, and unregister with the unregister method. To find an actor by its name, use the ActorRegistry.getActor static method.

If you’re running Quasar in a cluster configuration (see Clustering), registering an actor makes it globally available in the cluster. Calling ActorRegistry.getActor on any remote node would return a remote reference to the actor.

In addition, registering an actor automatically sets up monitoring for the actor, as explained in the next section.

Monitoring Actors
All actors running in a JVM instance are monitored by a MXBean registered with the name "co.paralleluniverse:type=Actors". For details, please consult the Javadoc.

In addition, you can create a an MXBean that monitors a specific actor by calling the actor’s monitor method. That MBean will be registered as "co.paralleluniverse:type=quasar,monitor=actor,name=ACTOR_NAME".This happens automatically when an actor is registered.

A monitored actor (either as a result of it being registered or of having called the monitor method) can have its MBean removed by calling the stopMonitor method.

Behaviors
Erlang’s designers have realized that many actors follow some common patterns - like an actor that receives requests for work and then sends back a result to the requester. They’ve turned those patterns into actor templates, called behaviors, in order to save people work and avoid some common errors. Erlang serves as the main inspiration to Quasar Actors, so some of these behaviors have been ported to Quasar.

Note: All behaviors use SLF4J loggers for logging.

RequestReplyHelper
A very common pattern that emerges when working with patterns is request-response, whereby a request message is sent to an actor, and a response is sent back to the sender of the request. While simple, some care must be taken to ensure that the response is matched with the correct request.

This behavior is implemented for you in the RequestReplyHelper class (in the co.paralleluniverse.actors.behaviors package).

To use it, the request message must extend co.paralleluniverse.actors.behaviors.RequestMessage. Suppose we have a IsDivisibleBy message class that extends RequestMessage. We can interact with a divisor-checking actor like so:

boolean result = RequestReplyHelper.call(actor, new IsDivisibleBy(100, 50));
And define the actor thus:

ActorRef<IsDivisibleBy> actor = new Actor<IsDivisibleBy, Void>(null, null) {
    protected Void doRun() {
        for(;;) {
            IsDivisibleBy msg = receive();
            try {
                boolean result = (msg.getNumber() % msg.getDivisor() == 0);
                RequestReplyHelper.reply(msg, result);
            } catch (ArithmeticException e) {
                RequestReplyHelper.replyError(msg, e);
            }
        }
    }
}.spawn();
In the case of an ArithmeticException (if the divisor is 0), the exception will be thrown by RequestReplyHelper.call.

One of the nicest things about the RequestReplyHelper class, is that the code calling call does not have to be an actor. It can be called by a regular thread (or fiber). But if you examine the code of the reply method, you’ll see that it simply sends a response message to the request’s sender, which is an actor. This is achieved by the call method creating a temporary virtual actor, that will receive the reply message.

Behavior Actors
Similarly to Erlang, Quasar includes “actor templates” for some common actor behaviors, called behavior actors. Their functionality is separated in two: the implementation, which extends BehaviorActor and standardize handling of standard messages, and the interface, which extends Behavior (which, in turn, extends ActorRef), and includes additional methods to those of ActorRef. It’s important to note that those interface methods do nothing more than assist in the creation and sending of said standard messages to the actor implementation. They employ no new construct.

By itself, BehaviorActor provides handling for ShutdownMessage, which, as its name suggests, requests an actor to shut itself down, along with the accompanying shutdown method in the Behavior class (the “interface” side). In addition, BehaviorActor defines standard initialization and termination methods which may be overriden. You should consult the Javadoc for more detail.

When a behavior actor is spawned, its spawn (or spawnThread) method returns its “interface” (which is also an ActorRef).

Note: Behavior actors usually have different constructors for convenience. Those that do not take an explicit MailboxConfig parameter, use the default configuration of an unbounded mailbox.

Server
The server behavior is an actor that implements a request-reply model. The behavior implementation is found in ServerActor, and the interface is Server.

You can implement a server actor by subclassing ServerActor and overriding the some or all of the methods:

init
terminate
handleCall
handleCast
handleInfo
handleTimeout
or by providing an instance of ServerHandler which implements these methods to the ServerActor constructor. Please consult the ServerActor JavaDoc for details.

The interface, Server, adds additional methods to ActorRef, such as call and cast, that allow sending synchronous (a request that waits for a response) or asynchronous (a request that does not wait for a response) requests to the server actor.

Proxy Server
Because the server behavior implements a useful and common synchronous request-reply pattern, and because this pattern is natively supported by Java in the form of a method call, Quasar includes an implementation of a server actor that uses the method call syntax: ProxyServerActor. Instead of defining message classes manually, a proxy server has an ActorRef that directly implements one or more interfaces; calling their methods automatically generates messages that are sent to the server actor, which then responds to the requests by calling the respective method on a given target object. This way, a server request becomes a simple method call. Note that the actor semantics are preserved: the target object’s methods are all run on a single strand, so there is no need to account for concurrent calls.

Lets look at an example. Suppose we have this interface:

public static interface A {
    int foo(String str, int x) throws SuspendExecution;
    void bar(int x) throws SuspendExecution;
}
We can then spawn the following actor:

Server a = new ProxyServerActor(false, new A() {
        public int foo(String str, int x) {
            return str.length() + x;
        }

        public void bar(int x) {
            System.out.println("x = " + x);
        }
    }).spawn();
To use the actor, we simply cast the ActorRef returned by spawn into our interface A. Every method invocation will be transformed into a message that, when received by the server actor, will be transformed back into a method call on the target:

((A)a).foo("hello", 5); // returns 10
Because the method calls are turned into messages that are processed by an actor on a separate strand, while the calling strand blocks until the result is returned, all of the interface’s methods must be suspendable. You can declare throws SuspendExecution on each method, annotate each method with @Suspendable, or annotate the entire interface, like so:

@Suspendable
public static interface A {
    int foo(String str, int x);
    void bar(int x);
}
This last option is particularly convenient if the methods’ implementation in the target is not suspendable.

For more details, please consult ProxyServerActor’s Javadoc.

EventSource
The event-source behavior is an actor that can be notified of event messages, which are delivered to event handlers which may be registered with the actor.

To create an event source actor, simply construct an instance of the EventSourceActor class. Event handlers are instances of EventHandler. Event handlers can be registered or unregistered with the actor, and events sent to the actor, through the behavior’s interface, the EventSource class.

Event handlers are called synchronously on the same strand as the actor’s and should not block the strand.

FiniteStateMachineActor
The finite-state-machine behavior is an actor that switches among a set of states and behaves differently in each.

To create a finite state machine actor, simply construct an instance of the FiniteStateMachineActor class. Each of the actor’s states is represented by a SuspendableCallable implementation returning the next state, or the special FiniteStateMachineActor.TERMINATE state to terminate the actor. You need to override the initialState method so that it returns the actor’s initial state. This class is best enjoyed using Java 8 lambda syntax, as in the following example:

new FiniteStateMachineActor() {
    @Override
    protected SuspendableCallable<SuspendableCallable> initialState() {
        return this::state1;
    }

    private SuspendableCallable<SuspendableCallable> state1() throws SuspendExecution, InterruptedException {
        return receive((m) -> {
            if ("a".equals(m))
                return this::state2;
            return null; // don't handle message
        });
    }

    private SuspendableCallable<SuspendableCallable> state2() throws SuspendExecution, InterruptedException {
        return receive((m) -> {
            if ("b".equals(m)) {
                System.out.println("Done!");
                return TERMINATE;
            }
            return null; // don't handle message
        });
    }
}.spawn();
Supervisors
The last behavior actor, the supervisor deserves a chapter of its own, as it’s at the core of the actor model’s error handling philosophy.

Actors provide fault isolation. When an exception occurs in an actor it can only (directly) take down that actor. Actors also provide fault detection and identification. As we’ve seen, other actors can be notified of an actor’s death, as well as its cause, via watches and links.

Like other behaviors, the supervisor is a behavior that codifies and standardizes good actor practices; in this case: fault handling. As its name implies, a supervisor is an actor that supervises one or more other actors and watches them to detect their death. When a supervised (or child) actor dies, the supervisor can take several pre-configured actions such as restarting the dead actor or killing and restarting all children. The supervisor might also choose to kill itself and escalate the problem, possibly to its own supervisor.

Actors performing business logic, “worker actors”, are supervised by a supervisor actor that detects when they die and takes one of several pre-configured actions. Supervisors may, in turn, be supervised by other supervisors, thus forming a supervision hierarchy that compartmentalizes failure and recovery.

The basic philosophy behind supervisor-based fault handling was named “let it crash” by Erlang’s designer, Joe Armstrong. The idea is that instead of trying to fix the program state after every expected exception, we simply let an actor crash when it encounters an unexpected condition and “reboot” it.

A supervisors works as follows: it has a number of children, worker actors or other supervisors that are registered to be supervised wither at the supervisor’s construction time or at a later time. Each child has a mode (represented by the Supervisor.ChildMode class): PERMANENT, TRANSIENT or TEMPORARY that determines whether its death will trigger the supervisor’s recovery event. When the recovery event is triggered, the supervisor takes action specified by its restart strategy - represented by the SupervisorActor.RestartStrategy class - or it will give up and fail, depending on predefined failure modes.

When a child actor in the PERMANENT mode dies, it will always trigger its supervisor’s recovery event. When a child in the TRANSIENT mode dies, it will trigger a recovery event only if it has died as a result of an exception, but not if it has simply finished its operation. A TEMPORARY child never triggers it supervisor’s recovery event.

A supervisor’s restart strategy determines what it does during a recovery event: A strategy of ESCALATE means that the supervisor will shut down (“kill”) all its surviving children and then die; a ONE_FOR_ONE strategy will restart the dead child; an ALL_FOR_ONE strategy will shut down all children and then restart them all; a REST_FOR_ONE strategy will shut down and restart all those children added to the supervisor after the dead child.

Children can be added to the supervisor actor either at construction time or later, with Supervisor’s addChild method. A child is added by passing a ChildSpec to the supervisor. The ChildSpec contains the means of how to start the actor, usually in the form of an ActorSpec (see the next section), or as an already constructed actor; the childs mode; and how many times an actor is allowed to be restarted in a given amount of time. If the actor is restarted too many times within the specified duration, the supervisor gives up and terminates (along with all its children) causing an escalation.

If an actor needs to know the identity of its siblings, it should add them to the supervisor manually (with Supervisor’s addChild method). For that, it needs to know the identity of its supervisor. To do that, you can construct the ActorSpec in the SupervisorActor’s Initializer or in SupervisorActor.init() method (subclass SupervisorActor), pass Actor.self() to the actor’s constructor, and add it to the supervisor with addChild. Alternatively, simply call Actor.self() in the child’s constructor. This works because the children are constructed from specs (provided they have not been constructed by the caller) during the supervisor’s run, so calling Actor.self() anywhere in the construction process would return the supervisor.

Actor Restarts
Restarting an actor means construction a new actor and spawning it. That is why the supervisor’s ChildSpec takes an instance of ActorBuilder. Usually, you’ll use ActorSpec as the builder instance. Sometimes, however, you’d like to add a running actor to the supervisor, and that is why ChildSpec has a constructor that takes an ActorRef. To restart such actors, the supervisor relies on the fact that ActorRefs to local actors implement ActorBuilder. When requested to build a new actor, they call the old actor’s reinstantiate method to create a clone of the old actor.

When an actor is restarted, the supervisor takes care to run it on the same type of strand (thread or fiber) as the old actor.

Hot Code Swapping
Hot code swapping is the ability to change your program’s code while it is running, with no need for a restart. Quasar actors support a limited and controlled, yet very useful, form of hot code swapping for actor code. Both plain actor implementations as well as behaviors can be loaded and swapped in at runtime.

Creating and Loading Code Modules
To create an upgraded version of an actor class or several of them, package the upgraded classes, along with any other accompanying classes into a jar file. When the jar is loaded, as we’ll see below, those classes that are marked as upgrades will replace their current versions. Only classes representing actor implementation (or actor behavior implementation) can be upgraded directly. Other classes might be upgraded as well if they store actor state as we’ll see in the next section. Actor (and behavior) upgrades must be explicitly or implicitly specified. To explicitly specify an upgrade, annotate the class with the @Upgrade annotation, or include its fully qualified name in a space-separated list as the value of the "Upgrade-Classes" attribute in the jar’s manifest. Alternatively, if the "Upgrade-Classes" attribute has the value *, all classes in the jar extending an actor or behavior class (or implementing a behavior interface like ServerHandler) will be automatically upgraded.

Once the jar is created, there are two ways to load it into the program. The first involves calling the reloadModule operation of the "co.paralleluniverse:type=ActorLoader" MBean, passing a URL for the jar; this can be done via any JMX console, such as VisualVM. The unloadModule operation can be used to unload the jar and revert actors to their previous implementation.

The second way is by designating a special module directory by setting the "co.paralleluniverse.actors.moduleDir" system property (this must be done when originally running the program). Then, any jar file copied into that directory will be automatically detected and loaded (this may take up to 10 seconds on some operating systems). A loaded jar that is removed from the module directory will be automatically unloaded.

Note: You might want to enable the "co.paralleluniverse.actors.ActorLoader" logger to view logs pertaining to hot code swapping.

State Upgrade
When an actor is upgraded (which might require an explicit call, as we’ll see in the next section), a new instance of the class’s new version will be created, and all of the actor’s state will be transferred to the new instance.

Actor state can be stored directly in primitive fields of the actor class, or in object fields that may, in turn, contain primitives or yet other objects. When an upgraded actor class is loaded, a new instance is created for each upgraded actor, and the old actor state is copied to it. Fields of the same name and type are copied as is. Reference (object) fields whose classes have upgraded versions in the loaded jar will be recursively replicated in the same way (fields will be copied by name). Whenever a new instance is created, any method marked with the @OnUpgrade annotation will be called. This will happen both for the actor class, as well as for any class holding actor state (i.e. found somewhere in the object graph starting at the actor) that undergoes an upgrade. An upgraded class can have more or fewer fields than its previous versions. Dropped fields will simply not be copied to the new version; newly added fields can be initialized in @OnUpgrade methods.

Swapping Plain Actors
Plain actor code is not swapped automatically – an actor must explicitly support swapping; therefore plain actors must be originally built with a possible upgrade in mind. As an actor runs, when it reaches a point where swapping in a new version makes sense (depending on your application logic, but often right before receiving a new message), it must call the checkCodeSwap method of the Actor class. If a new version of the actor class has been loaded, its doRun method will begin anew, after actor state has been copied. For that reason, initialization code found at the beginning of doRun must take into account the fact that it may be run when some or all actor state already initialized.

Swapping Behaviors
Unlike plain actors, behaviors can be swapped in without any early consideration (i.e. behaviors already call checkCodeSwap at appropriate points). Internal state will be copied, just as with plain actors.

Example
A complete hot code swapping example can be found in this GitHub repository.

Quasar-Kotlin Actors
Kotlin’s inline higher-order functions and the when construct enable a powerful and natural selective receive syntax:

receive(1000, TimeUnit.MILLISECONDS) {  // Fiber-blocking
    when (it) {
        is Msg -> {
            if (it.txt == "ping")
                it.from.send("pong")    // Fiber-blocking
        }
        "finished" -> {
            println("Pong received 'finished', exiting")
            return                      // Non-local return, exit actor
        }
        is Timeout -> {
            println("Pong timeout in 'receive', exiting")
            return                      // Non-local return, exit actor
        }
        else -> defer()
    }
}
This example highlights a few interesting capabilities:

Straightforward message picking as well as acting upon (even with further communication, if needed).
Deferring a message when it’s not yet possible (or handy) to extract it from the mailbox for use.
Non-local returns, for example to terminate the actor’s processing loop.
Handling of timeouts in the message-processing closure.
