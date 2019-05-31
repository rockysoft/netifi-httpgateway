package com.netifi.httpgateway.bridge.endpoint.egress.pool;

import com.netifi.httpgateway.bridge.endpoint.egress.EgressEndpointFactorySupplier;
import com.netifi.httpgateway.bridge.endpoint.egress.lb.WeightedEgressEndpoint;
import com.netifi.httpgateway.bridge.endpoint.egress.lb.WeightedEgressEndpointFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/** Selects a factory from the pool at random. */
public class RandomSelectionWeightedEgressEndpointFactoryPool
    extends EgressEndpointFactoryPool<WeightedEgressEndpoint, WeightedEgressEndpointFactory> {

  private final List<WeightedEgressEndpointFactory> availableFactories;
  private final Set<WeightedEgressEndpointFactory> leasedFactories;
  private Set<WeightedEgressEndpointFactory> lastIncoming;

  public RandomSelectionWeightedEgressEndpointFactoryPool(
      EgressEndpointFactorySupplier<WeightedEgressEndpoint, WeightedEgressEndpointFactory>
          egressEndpointFactorySupplier) {
    super(egressEndpointFactorySupplier);
    this.availableFactories = new ArrayList<>();
    this.leasedFactories = new HashSet<>();
    this.lastIncoming = Collections.EMPTY_SET;
    egressEndpointFactorySupplier.get().doOnNext(this::handleEndpointFactorySet).subscribe();
  }

  @Override
  public void handleEndpointFactorySet(Set<WeightedEgressEndpointFactory> incoming) {
    Set<WeightedEgressEndpointFactory> oldFactories;
    synchronized (this) {
      Set<WeightedEgressEndpointFactory> newFactories = new HashSet<>(incoming);
      newFactories.remove(lastIncoming);

      availableFactories.addAll(newFactories);

      oldFactories = new HashSet<>(lastIncoming);
      oldFactories.remove(incoming);
    }

    for (WeightedEgressEndpointFactory f : oldFactories) {
      f.dispose();
    }
  }

  @Override
  public synchronized Optional<WeightedEgressEndpointFactory> lease() {
    if (availableFactories.isEmpty()) {
      return Optional.empty();
    } else {
      int i = ThreadLocalRandom.current().nextInt(0, availableFactories.size());
      WeightedEgressEndpointFactory factory = availableFactories.remove(i);
      leasedFactories.add(factory);
      return Optional.of(factory);
    }
  }

  @Override
  public synchronized void release(WeightedEgressEndpointFactory endpointFactory) {
    if (!endpointFactory.isDisposed()) {
      boolean removed = leasedFactories.remove(endpointFactory);
      if (removed) {
        availableFactories.add(endpointFactory);
      }
    }
  }

  @Override
  public int size() {
    return availableFactories.size();
  }
}
