package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas_entradas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaEntradaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Usuario comprador
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  private UsuarioEntity usuario;

  // Tipo de entrada comprada
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento"),
    @JoinColumn(name = "id_tipos_entrada", referencedColumnName = "id_tipos_entrada")
  })
  private EventoTiposEntradaEntity tipoEntradaEvento;

  // Fecha de la compra (creas la venta cuando el pago se aprueba)
  private LocalDateTime fechaVenta = LocalDateTime.now();

  // Cantidad comprada
  private int cantidad;

  // Precio unitario snapshot
  private BigDecimal precioUnitario;

  // ID del pago en Mercado Pago
  @Column(name = "payment_id")
  private Long paymentId;

  // Estado del pago (approved, pending, rejected, refunded…)
  @Column(name = "estado_pago")
  private String estadoPago;

  // Preferencia o referencia externa (usás EVT-17 por ejemplo)
  @Column(name = "external_reference")
  private String externalReference;

  // Fecha que Mercado Pago actualiza (para auditoría)
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;

  // Último status_detail que te manda MP (opcional pero útil)
  @Column(name = "status_detail")
  private String statusDetail;

  // Monto total (calculado)
  public BigDecimal getMontoTotal() {
    if (precioUnitario == null) {
      return BigDecimal.ZERO;
    }
    return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
  }

  @Column(name = "qr_token", unique = true)
  private String qrToken;
}
