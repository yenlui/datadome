package com.datadome.product.apache;

import lombok.Builder;

/**
 * ApacheLog
 */
@Builder
public record AccessLog(Request request,Response response,String sourceLine) {
  /*
     * %h is the remote host (ie the client IP)
%l is the identity of the user determined by identd (not usually used since not reliable)
%u is the user name determined by HTTP authentication
%t is the time the request was received.
%r is the request line from the client. ("GET / HTTP/1.0")
%>s is the status code sent from the server to the client (200, 404 etc.)
%b is the size of the response to the client (in bytes)
Referer is the Referer header of the HTTP request (containing the URL of the page from which this request was initiated) if any is present, and "-" otherwise.
User-agent is the browser identification string.
     * 
     */

}
