package ar.edu.utn.frbb.tup.presentation.input;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.service.CuentaService;

public class ShowInfoCuenta {

    private CuentaService cuentaService;

    public ShowInfoCuenta() {
        this.cuentaService = new CuentaService();
    }

    public void mostrarInfoCuenta(long id) {
        Cuenta cuenta = cuentaService.find(id);

        if(cuenta == null) {
            System.out.println("Cuenta no encontrada!");
        }
        System.out.println("Información de la Cuenta: ");
        System.out.println("Cuenta id: " + cuenta.getNumeroCuenta());
        System.out.println("Tipo de Cuenta: " + cuenta.getTipoCuenta());
        System.out.println("Tipo de Moneda: " + cuenta.getMoneda());
        System.out.println("Balance: " + cuenta.getBalance());
        System.out.println("Titular: " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido());
        System.out.println("Fecha de Creación: " + cuenta.getFechaCreacion());
    }
}
