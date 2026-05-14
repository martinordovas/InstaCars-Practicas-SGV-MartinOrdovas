package com.martin.ctrl;

import com.martin.dal.CrudDAO;
import com.martin.model.Vehiculo;
import com.martin.model.VehiculoPK;
import com.martin.model.Marca;
import com.martin.dal.JpaUtil;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "vehiculoCtrl")
@ViewScoped
public class VehiculoCtrl implements Serializable {

    private Vehiculo vehiculo;
    private Vehiculo nuevoVehiculo;

    private boolean edit = false;
    private List<Marca> listaMarcas;

    @PostConstruct
    public void init() {
        listaMarcas = JpaUtil.getDAO().findWithNamedQuery("Marca.findAll");
        
        vehiculo = new Vehiculo();
        vehiculo.setVehiculoPK(new VehiculoPK());
    }

    public void prepararNuevo() {
        this.nuevoVehiculo = new Vehiculo();
        this.nuevoVehiculo.setVehiculoPK(new VehiculoPK());
        this.nuevoVehiculo.setEstado("DISPONIBLE");
    }
    
    public String save() {
        CrudDAO dao = null;
        try {
            dao = JpaUtil.getTransactionalDAO();
            if (vehiculo.getMarca() != null) {
                vehiculo.getVehiculoPK().setIdMarca(vehiculo.getMarca().getId());
            }

            if (edit) {
                dao.update(vehiculo);
            } else {
                dao.create(vehiculo);
            }
            dao.commitTransaction();
            PrimeFaces.current().executeScript("PF('dlgVehiculo').hide();");
            return "listadoVehiculos?faces-redirect=true";
        } catch (Exception e) {
            return null;
        }
    }

    public Vehiculo getVehiculo() { 
        return vehiculo; 
    }
    
    public void setVehiculo(Vehiculo vehiculo) { 
        this.vehiculo = vehiculo; 
    }
    
    public List<Marca> getListaMarcas() { 
        return listaMarcas; 
    }
    
    public boolean isEdit() { 
        return edit; 
    }
    
    public Vehiculo getNuevoVehiculo() {
        if (nuevoVehiculo == null) {
            prepararNuevo();
        }
        return nuevoVehiculo;
    }

    public void setNuevoVehiculo(Vehiculo nuevoVehiculo) {
        this.nuevoVehiculo = nuevoVehiculo;
    }
}