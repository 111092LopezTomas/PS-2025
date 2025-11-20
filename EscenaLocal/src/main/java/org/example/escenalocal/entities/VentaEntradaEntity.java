package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  // Usuario que realizó la compra
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  private UsuarioEntity usuario;

  // Tipo de entrada que compró (VIP, General, etc.)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento"),
    @JoinColumn(name = "id_tipos_entrada", referencedColumnName = "id_tipos_entrada")
  })
  private EventoTiposEntradaEntity tipoEntradaEvento;

  // Cantidad comprada
  private int cantidad;

  // Precio unitario al momento de la compra (snapshot)
  private double precioUnitario;

  // Fecha y hora exacta de la venta
  private LocalDateTime fechaVenta = LocalDateTime.now();

  // Monto total de la compra
  public double getMontoTotal() {
    return cantidad * precioUnitario;
  }
}

