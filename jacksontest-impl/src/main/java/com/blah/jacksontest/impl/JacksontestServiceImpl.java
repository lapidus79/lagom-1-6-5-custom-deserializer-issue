package com.blah.jacksontest.impl;

import akka.Done;
import akka.NotUsed;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.japi.Pair;
import com.fasterxml.jackson.databind.JsonNode;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.transport.BadRequest;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.blah.jacksontest.api.GreetingMessage;
import com.blah.jacksontest.api.JacksontestService;
import com.blah.jacksontest.impl.JacksontestCommand.*;
import play.libs.Json;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the JacksontestService.
 */
public class JacksontestServiceImpl implements JacksontestService {
    private final PersistentEntityRegistry persistentEntityRegistry;
    private final Duration askTimeout = Duration.ofSeconds(5);
    private ClusterSharding clusterSharding;

    @Inject
     public JacksontestServiceImpl(PersistentEntityRegistry persistentEntityRegistry, ClusterSharding clusterSharding) {
        this.clusterSharding = clusterSharding;
        // The persistent entity registry is only required to build an event stream for the TopicProducer
        this.persistentEntityRegistry = persistentEntityRegistry;

        // register the Aggregate as a sharded entity
        this.clusterSharding.init(
            Entity.of(
                JacksontestAggregate.ENTITY_TYPE_KEY,
                JacksontestAggregate::create
            )
        );
    }

    public static RootEntityState dummyState() {

        return RootEntityState.builder()
                .updatedAt(System.currentTimeMillis())
                .createdAt(System.currentTimeMillis())
                .updatedBy("bla")
                .child(
                        Child.builder()
                                .updatedAt(System.currentTimeMillis())
                                .updatedBy("dadasad").build()
                )
                .build();
    }

    @Override
    public ServiceCall<NotUsed, String> hello(String id) {
        return request -> {

            RootEntityState state = dummyState();
            String str = Json.prettyPrint(Json.toJson(state));
            JsonNode node = Json.parse(str);
            System.out.println("createdAt define in node " + node.get("createdAt"));
            // But after deserialization things turn ugly after jackson? has successfully
            // deserialized the Child object. After that it looks like the usage of a customer
            // deserializer halts the flow and all remaining attributes (that are lockated below child attribute)
            // are ignored and null values are passed instead in the constructor
            // Due to the @Nonnull annotation we then end up with an exception
            RootEntityState result = Json.fromJson(node, RootEntityState.class);
            assert (result).equals(state);

            return CompletableFuture.completedFuture( "success");
        };
    }

    @Override
    public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
        return request -> {
            // Look up the aggregete instance for the given ID.
            EntityRef<JacksontestCommand> ref = clusterSharding.entityRefFor(JacksontestAggregate.ENTITY_TYPE_KEY, id);
            // Tell the entity to use the greeting message specified.
            return ref.
                <JacksontestCommand.Confirmation>ask(replyTo -> new UseGreetingMessage(request.message, replyTo), askTimeout)
                .thenApply(confirmation -> {
                        if (confirmation instanceof JacksontestCommand.Accepted) {
                            return Done.getInstance();
                        } else {
                            throw new BadRequest(((JacksontestCommand.Rejected) confirmation).reason);
                        }
                    }
                );
        };
    }

    @Override
    public Topic<com.blah.jacksontest.api.JacksontestEvent> helloEvents() {
        // We want to publish all the shards of the hello event
        return TopicProducer.taggedStreamWithOffset(JacksontestEvent.TAG.allTags(), (tag, offset) ->
            // Load the event stream for the passed in shard tag
            persistentEntityRegistry.eventStream(tag, offset).map(eventAndOffset -> {
                // Now we want to convert from the persisted event to the published event.
                // Although these two events are currently identical, in future they may
                // change and need to evolve separately, by separating them now we save
                // a lot of potential trouble in future.
                com.blah.jacksontest.api.JacksontestEvent eventToPublish;

                if (eventAndOffset.first() instanceof JacksontestEvent.GreetingMessageChanged) {
                    JacksontestEvent.GreetingMessageChanged messageChanged = (JacksontestEvent.GreetingMessageChanged) eventAndOffset.first();
                    eventToPublish = new com.blah.jacksontest.api.JacksontestEvent.GreetingMessageChanged(
                        messageChanged.getName(), messageChanged.getMessage()
                    );
                } else {
                    throw new IllegalArgumentException("Unknown event: " + eventAndOffset.first());
                }

                // We return a pair of the translated event, and its offset, so that
                // Lagom can track which offsets have been published.
                return Pair.create(eventToPublish, eventAndOffset.second());
            })
        );
    }
}
