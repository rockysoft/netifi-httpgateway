package com.netifi.consul.v1.agent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = AgentWeights.Builder.class)
public class AgentWeights {

  @JsonProperty("Passing")
  private final int Passing;

  @JsonProperty("Warning")
  private final int Warning;

  private AgentWeights(Builder builder) {
    Passing = builder.Passing;
    Warning = builder.Warning;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public int getPassing() {
    return Passing;
  }

  public int getWarning() {
    return Warning;
  }

  @JsonPOJOBuilder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class Builder {

    @JsonProperty("Passing")
    private int Passing;

    @JsonProperty("Warning")
    private int Warning;

    private Builder() {}

    public Builder withPassing(int val) {
      Passing = val;
      return this;
    }

    public Builder withWarning(int val) {
      Warning = val;
      return this;
    }

    public AgentWeights build() {
      return new AgentWeights(this);
    }
  }
}
