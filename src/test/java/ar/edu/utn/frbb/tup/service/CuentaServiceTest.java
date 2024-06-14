package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCuentaExiste()
            throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNoSoportadaException {
        // Arrange
        Cuenta cuentaExistente = new Cuenta();
        cuentaExistente.setNumeroCuenta(12345);
        long dniTitular = 123456789;

        when(cuentaDao.find(12345)).thenReturn(cuentaExistente);

        // Act & Assert
        assertThrows(CuentaAlreadyExistsException.class,
                () -> cuentaService.darDeAltaCuenta(cuentaExistente, dniTitular));
    }

    @Test
    public void testTipoCuentaNoSoportada() {
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.DOLARES);

        doReturn(null).when(cuentaDao).find(anyLong());

        assertThrows(TipoCuentaNoSoportadaException.class, () -> {
            cuentaService.darDeAltaCuenta(cuenta, 12345678);
        });
    }

    @Test
    public void testClienteYaTieneCuentaDeEseTipo() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNoSoportadaException {

        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.PESOS);

        Cliente cliente = new Cliente();
        cliente.setDni(12345678);
        cliente.addCuenta(cuenta);
        cliente.setNombre("Pepe");
        cliente.setApellido("Rino");
        cliente.setFechaNacimiento(LocalDate.of(1978, 3,25));
        cliente.setTipoPersona(TipoPersona.PERSONA_FISICA);

        // when(clienteDao.find(12345678, true)).thenReturn(cliente);
        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);
        doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta, cliente.getDni());
        assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, cliente.getDni()));
    }

    @Test
    public void testDarDeAltaCuentaTipoNuevo() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNoSoportadaException, ClienteAlreadyExistsException {
        // Arrange
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setMoneda(TipoMoneda.PESOS);

        Cliente cliente = new Cliente();

        cliente.setDni(12345678);
        cliente.setNombre("asd");
        cliente.setApellido("asdfgh");

        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);
        cuentaService.darDeAltaCuenta(cuenta, 12345678);

        verify(cuentaDao, times(1)).save(cuenta);
    }
}