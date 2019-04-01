/**
 * Copyright 2018 Netifi Inc.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netifi.proteus.httpgateway.endpoint.factory;

import com.netifi.proteus.httpgateway.endpoint.Endpoint;

public class EndpointEvent {
  private String url;
  private Endpoint endpoint;
  private Type type;

  public EndpointEvent(String url, Endpoint endpoint, Type type) {
    this.url = url;
    this.endpoint = endpoint;
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public Endpoint getEndpoint() {
    return endpoint;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return "EndpointEvent{"
        + "url='"
        + url
        + '\''
        + ", endpoint="
        + endpoint
        + ", type="
        + type
        + '}';
  }

  public enum Type {
    ADD,
    REPLACE,
    DELETE
  }
}
