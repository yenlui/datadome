package com.datadome.product.apache;

import org.springframework.http.HttpMethod;

import lombok.Builder;

@Builder
public record Query(HttpMethod method, String query,String protocol){}
