category: Quasar
date: 2015-09-08
title: Clustering
---


Quasar is able to run on a cluster, thereby letting actors and channels communicate across machines. The Quasar cluster runs on top of Galaxy, Parallel Universe’s in-memory data grid.

In this version, clustering is pretty rudimentary, but essential features should work: actors can be made discoverable on the network, messages can be passed among actors on different nodes, and an actor on a failing node will behave as expected of a dying actor with respect to exit messages sent to other, remote, watching it or linked to it.

Enabling Clustering
First, you will need to add the co.paralleluniverse:quasar-galaxy artifact as a dependency to your project, and set some Galaxy cluster properties. At the very least you will need to set "galaxy.nodeId", which will have to be a different short value for each master node. If you’re running several nodes on the same machine, you will also need to set "galaxy.port" and "galaxy.slave_port". These properties can be set in several ways. The simplest is to define them as JVM system properties (as -D command line arguments).However, you can also set them in the Galaxy configuration XML files or in a properties file. Please refer to the Galaxy documentation for more detail.

Then, to make an actor discoverable cluster-wide, all you need to do is register it with the register method of the Actor class.

That’s it. The actor is now known throughout the cluster, and can be accessed by calling ActorRegistry.getActor on any node.

An actor doesn’t have to be registered in order to be reachable on the network. Registering it simply makes it discoverable. If we pass an ActorRef of local actor in a message to a remote actor, the remote actor will be able to send messages to the local actor as well.

Cluster Configuration
For instructions on how to configure the Galaxy cluster, please refer to Galaxy’s getting started guide.

Actor Migration
Running actors can migrate from one cluster node to another, while preserving their state. Migration happens in two steps. First an actor migrates, which suspends it and makes its internal state available to the cluster, and then it is hired by another cluster node an resumed.

Actors that support migration, must implement the (empty) marker interface MigratingActor. Then, in order to migrate, an actor must call one of two methods: migrateAndRestart or migrate. migrateAndRestart suspends the actor in such a way that when it is later hired, it will be restarted (i.e., its doRun method will be called again and run from the top), but the current value of the actor’s fields will be preserved, while migrate suspends the fiber the actor is running in (and is therefore available only for actors running in fibers), so that when the actor is hired, it will continue execution from the point the migrate method was called. The hire method hires and resumes the actor.
