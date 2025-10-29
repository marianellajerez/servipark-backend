package com.servipark.backend.service;

import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.model.Vehiculo;
import com.servipark.backend.repository.TipoVehiculoRepository;
import com.servipark.backend.repository.VehiculoRepository;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import com.servipark.backend.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final TipoVehiculoRepository tipoVehiculoRepository;

    @Autowired
    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository,
                               TipoVehiculoRepository tipoVehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.tipoVehiculoRepository = tipoVehiculoRepository;
    }

    @Override
    @Transactional
    public Vehiculo findOrCreateVehiculo(String placa, Long idTipoVehiculo) {
        String placaFormateada = formatPlaca(placa);

        if (placaFormateada == null || placaFormateada.isEmpty()) {
            throw new ReglaNegocioException("vehiculo.error.placa.vacia");
        }

        return vehiculoRepository.findByPlaca(placaFormateada)
                .orElseGet(() -> crearNuevoVehiculo(placaFormateada, idTipoVehiculo));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehiculo> findByPlaca(String placa) {
        String placaFormateada = formatPlaca(placa);
        if (placaFormateada == null || placaFormateada.isEmpty()) {
            return Optional.empty();
        }
        return vehiculoRepository.findByPlaca(placaFormateada);
    }

    private String formatPlaca(String placa) {
        if (placa == null) return null;
        return placa.toUpperCase().replaceAll("\\s+", "");
    }

    private Vehiculo crearNuevoVehiculo(String placaFormateada, Long idTipoVehiculo) {
        TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(idTipoVehiculo)
                .filter(TipoVehiculo::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "tarifa.error.tipoVehiculo.noEncontrado.id", idTipoVehiculo
                ));

        Vehiculo nuevoVehiculo = new Vehiculo();
        nuevoVehiculo.setPlaca(placaFormateada);
        nuevoVehiculo.setTipoVehiculo(tipoVehiculo);
        return vehiculoRepository.save(nuevoVehiculo);
    }
}