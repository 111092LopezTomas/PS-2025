package org.example.escenalocal.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.entities.VentaEntradaEntity;
import org.example.escenalocal.repositories.VentaEntradaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
public class VentaController {

  private final VentaEntradaRepository ventaEntradaRepository;

  @GetMapping("/{id}/qr")
  public ResponseEntity<byte[]> getQr(@PathVariable Long id) throws Exception {
    VentaEntradaEntity venta = ventaEntradaRepository.findById(id).orElseThrow();

    if (venta.getQrToken() == null) {
      return ResponseEntity.badRequest().body(null);
    }

    String qrContent = "ESCENALOCAL:" + venta.getQrToken();

    BitMatrix matrix = new QRCodeWriter()
      .encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(matrix, "PNG", out);

    return ResponseEntity.ok()
      .contentType(MediaType.IMAGE_PNG)
      .body(out.toByteArray());
  }

}
