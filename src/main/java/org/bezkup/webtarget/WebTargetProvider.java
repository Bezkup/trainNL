package org.bezkup.webtarget;

import jakarta.ws.rs.client.WebTarget;

import java.net.URI;

public interface WebTargetProvider {
    WebTarget getWebTarget(URI uri);
}