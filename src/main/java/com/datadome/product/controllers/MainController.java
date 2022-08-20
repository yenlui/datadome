package com.datadome.product.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.TRACE;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.controllers.services.AccessLogBuilder;
import com.datadome.product.services.detection.DetectionService;
import com.datadome.product.services.routing.BlockService;
import com.datadome.product.services.routing.ForwardService;
import javax.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MainController {
  @Setter(onMethod = @__({ @Autowired }))
  private DetectionService detectionService;

  @Setter(onMethod = @__({ @Autowired }))
  private AccessLogBuilder accessLogBuilder;

  @Setter(onMethod = @__({ @Autowired }))
  private BlockService blockService;

  @Setter(onMethod = @__({ @Autowired }))
  private ForwardService forwardService;

  @RequestMapping(
    path = "**",
    method = { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE }
  )
  public void proxy(
    @RequestHeader("date") String dateTimeString,
    HttpServletRequest request
  ) {
    long start = System.currentTimeMillis();

    try {
      AccessLog accessLog = accessLogBuilder.fromRequest(
        dateTimeString,
        request
      );

      if (detectionService.processAccessLog(accessLog)) {
        // block request
        blockService.blockRequest();
      }
      // forward to actual resource
      forwardService.forwardRequest();
    } finally {
      log.info("Handled request in {} ms", System.currentTimeMillis() - start);
    }
  }
}
