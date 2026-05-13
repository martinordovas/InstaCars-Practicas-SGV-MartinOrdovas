/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martin.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author mogmojito
 */
@Embeddable
public class VehiculoPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id_marca")
    private int idMarca;
    @Basic(optional = false)
    @Column(name = "num_bastidor")
    private String numBastidor;

    public VehiculoPK() {
    }

    public VehiculoPK(int idMarca, String numBastidor) {
        this.idMarca = idMarca;
        this.numBastidor = numBastidor;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getNumBastidor() {
        return numBastidor;
    }

    public void setNumBastidor(String numBastidor) {
        this.numBastidor = numBastidor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idMarca;
        hash += (numBastidor != null ? numBastidor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VehiculoPK)) {
            return false;
        }
        VehiculoPK other = (VehiculoPK) object;
        if (this.idMarca != other.idMarca) {
            return false;
        }
        if ((this.numBastidor == null && other.numBastidor != null) || (this.numBastidor != null && !this.numBastidor.equals(other.numBastidor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.martin.model.VehiculoPK[ idMarca=" + idMarca + ", numBastidor=" + numBastidor + " ]";
    }
    
}
