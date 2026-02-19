  package org.example.escenalocal.controllers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

  @GetMapping("/success")
  public void success(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String query = request.getQueryString(); // payment_id=...&status=...

    response.sendRedirect("http://localhost:4200/checkout/success?" + query);
  }

  @GetMapping("/failure")
  public void failure(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect("http://localhost:4200/checkout/failure");
  }

  @GetMapping("/pending")
  public void pending(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect("http://localhost:4200/checkout/pending");
  }
}

