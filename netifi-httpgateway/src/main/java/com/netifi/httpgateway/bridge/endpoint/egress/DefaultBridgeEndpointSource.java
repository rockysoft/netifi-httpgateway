package com.netifi.httpgateway.bridge.endpoint.egress;

import com.google.protobuf.Empty;
import com.netifi.httpgateway.bridge.endpoint.source.BridgeEndpointSource;
import com.netifi.httpgateway.bridge.endpoint.source.EndpointJoinEvent;
import com.netifi.httpgateway.bridge.endpoint.source.Event;
import io.netty.buffer.ByteBuf;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;

public class DefaultBridgeEndpointSource implements BridgeEndpointSource {
  private final Logger logger = LogManager.getLogger(DefaultBridgeEndpointSource.class);

  private Supplier<Set<String>> serviceNameSupplier;

  private String group;

  private FluxProcessor<Event, Event> processor;

  public DefaultBridgeEndpointSource(
      Supplier<Set<String>> serviceNameSupplier,
      String group,
      FluxProcessor<Event, Event> processor) {
    this.serviceNameSupplier = serviceNameSupplier;
    this.group = group;
    this.processor = processor;
  }

  @Override
  public Flux<Event> streamEndpointEvents(Empty message, ByteBuf metadata) {
    Flux<Event> initial =
        Flux.fromStream(
                serviceNameSupplier
                    .get()
                    .stream()
                    .filter(
                        s -> {
                          if (s.contains("netifi-gateway")) {
                            logger.info("service {} has netifi-gateway in the name - skipping", s);
                            return false;
                          }
                          logger.info(
                              "service {} doesn't netifi-gateway in the name - emitting", s);
                          return true;
                        }))
            .map(
                serviceNames ->
                    Event.newBuilder()
                        .setGroup(group)
                        .setJoinEvent(
                            EndpointJoinEvent.newBuilder().setServiceName(serviceNames).build())
                        .build());

    return processor.startWith(initial).onBackpressureBuffer(256, BufferOverflowStrategy.ERROR);
  }
}
