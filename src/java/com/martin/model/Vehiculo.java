/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author mogmojito
 */
@Entity
@Table(name = "vehiculo")
@NamedQueries({
    @NamedQuery(name = "Vehiculo.findAll", query = "SELECT v FROM Vehiculo v")
    , @NamedQuery(name = "Vehiculo.findByIdMarca", query = "SELECT v FROM Vehiculo v WHERE v.vehiculoPK.idMarca = :idMarca")
    , @NamedQuery(name = "Vehiculo.findByNumBastidor", query = "SELECT v FROM Vehiculo v WHERE v.vehiculoPK.numBastidor = :numBastidor")
    , @NamedQuery(name = "Vehiculo.findByMatricula", query = "SELECT v FROM Vehiculo v WHERE v.matricula = :matricula")
    , @NamedQuery(name = "Vehiculo.findByModelo", query = "SELECT v FROM Vehiculo v WHERE v.modelo = :modelo")
    , @NamedQuery(name = "Vehiculo.findByAnio", query = "SELECT v FROM Vehiculo v WHERE v.anio = :anio")
    , @NamedQuery(name = "Vehiculo.findByPrecio", query = "SELECT v FROM Vehiculo v WHERE v.precio = :precio")
    , @NamedQuery(name = "Vehiculo.findByKm", query = "SELECT v FROM Vehiculo v WHERE v.km = :km")
    , @NamedQuery(name = "Vehiculo.findByColor", query = "SELECT v FROM Vehiculo v WHERE v.color = :color")
    , @NamedQuery(name = "Vehiculo.findByCombustible", query = "SELECT v FROM Vehiculo v WHERE v.combustible = :combustible")
    , @NamedQuery(name = "Vehiculo.findByEstado", query = "SELECT v FROM Vehiculo v WHERE v.estado = :estado")
    , @NamedQuery(name = "Vehiculo.findByDescripcion", query = "SELECT v FROM Vehiculo v WHERE v.descripcion = :descripcion")})
public class Vehiculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected VehiculoPK vehiculoPK;
    @Column(name = "matricula")
    private String matricula;
    @Basic(optional = false)
    @Column(name = "modelo")
    private String modelo;
    @Basic(optional = false)
    @Column(name = "anio")
    private short anio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "precio")
    private BigDecimal precio;
    @Basic(optional = false)
    @Column(name = "km")
    private int km;
    @Column(name = "color")
    private String color;
    @Basic(optional = false)
    @Column(name = "combustible")
    private String combustible;
    @Basic(optional = false)
    @Column(name = "estado")
    private String estado;
    @Column(name = "descripcion")
    private String descripcion;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "vehiculo")
    private Venta venta;
    @JoinColumn(name = "id_marca", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Marca marca;

    public Vehiculo() {
    }

    public Vehiculo(VehiculoPK vehiculoPK) {
        this.vehiculoPK = vehiculoPK;
    }

    public Vehiculo(VehiculoPK vehiculoPK, String modelo, short anio, BigDecimal precio, int km, String combustible, String estado) {
        this.vehiculoPK = vehiculoPK;
        this.modelo = modelo;
        this.anio = anio;
        this.precio = precio;
        this.km = km;
        this.combustible = combustible;
        this.estado = estado;
    }

    public Vehiculo(int idMarca, String numBastidor) {
        this.vehiculoPK = new VehiculoPK(idMarca, numBastidor);
    }

    public VehiculoPK getVehiculoPK() {
        return vehiculoPK;
    }

    public void setVehiculoPK(VehiculoPK vehiculoPK) {
        this.vehiculoPK = vehiculoPK;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public short getAnio() {
        return anio;
    }

    public void setAnio(short anio) {
        this.anio = anio;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCombustible() {
        return combustible;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vehiculoPK != null ? vehiculoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vehiculo)) {
            return false;
        }
        Vehiculo other = (Vehiculo) object;
        if ((this.vehiculoPK == null && other.vehiculoPK != null) || (this.vehiculoPK != null && !this.vehiculoPK.equals(other.vehiculoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.martin.model.Vehiculo[ vehiculoPK=" + vehiculoPK + " ]";
    }
    
}
