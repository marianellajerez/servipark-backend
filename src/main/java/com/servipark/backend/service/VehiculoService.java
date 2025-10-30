package com.servipark.backend.service;

import com.servipark.backend.model.Vehiculo;
import com.servipark.backend.model.Ticket; // Se necesita para el método del historial en TicketService
import java.util.List;
import java.util.Optional;

public interface VehiculoService {

    /**
     * Busca un vehículo por placa o lo crea si no existe.
     */
    Vehiculo findOrCreateVehiculo(String placa, Long idTipoVehiculo);

    /**
     * Busca un vehículo por su placa formateada.
     */
    Optional<Vehiculo> findByPlaca(String placa);

    // NOTA: Este método fue movido a TicketService, pero si VehiculoService lo implementa
    // como helper, necesita la firma aquí:
    // List<Ticket> findAllTicketsByPlaca(String placa);

    // Sin embargo, basándonos en tu VehiculoServiceImpl actual, solo necesita los dos métodos de arriba.
}