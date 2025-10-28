package com.servipark.backend.service;

import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.model.Vehiculo;
import com.servipark.backend.repository.TipoVehiculoRepository;
import com.servipark.backend.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    private String formatPlaca(String placa) {
        if (placa == null) return null;
        return placa.toUpperCase().replaceAll("\\s+", "");
    }

    @Override
    @Transactional
    public Vehiculo findOrCreateVehiculo(String placa, Long idTipoVehiculo) {
        String placaFormateada = formatPlaca(placa);
        if (placaFormateada == null || placaFormateada.isEmpty()) {
            throw new RuntimeException("La placa no puede estar vacía.");
        }
        Optional<Vehiculo> vehiculoExistente = vehiculoRepository.findByPlaca(placaFormateada);
        if (vehiculoExistente.isPresent()) {
            return vehiculoExistente.get();
        } else {
            TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(idTipoVehiculo)
                    .filter(TipoVehiculo::isActivo)
                    .orElseThrow(() -> new RuntimeException("Error: Tipo de vehículo activo no encontrado con ID: " + idTipoVehiculo));
            Vehiculo nuevoVehiculo = new Vehiculo();
            nuevoVehiculo.setPlaca(placaFormateada);
            nuevoVehiculo.setTipoVehiculo(tipoVehiculo);
            return vehiculoRepository.save(nuevoVehiculo);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehiculo> findByPlaca(String placa) {
        String placaFormateada = formatPlaca(placa);
        return vehiculoRepository.findByPlaca(placaFormateada);
    }
}