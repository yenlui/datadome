package com.datadome.product.apache;

import java.net.URI;

import org.springframework.http.HttpMethod;

import lombok.Builder;

@Builder
public record Query(HttpMethod method, URI uri, String protocol){}
