package org.example.escenalocal.payments;

import java.math.BigDecimal;
import java.util.List;

public record CreatePrefCommand(
  String externalReference,
  Long usuarioId,
  Long eventoId,
  Long tipoEntradaId,
  List<Item> items
) {
  public record Item(String id, String title, String description, int quantity, BigDecimal unitPrice) {}
}
