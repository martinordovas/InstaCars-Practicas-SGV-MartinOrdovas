/*
 * Software Grupo V, S.L.
 */
package com.martin.dal;

import com.google.common.collect.Lists;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.log4j.Logger;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.QueryByExamplePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 *
 * @author jagimeno
 */
public class JpaUtil {
    
    private EntityManagerFactory emf = null;

    private static JpaUtil instance = null;

    private JpaUtil(){
        this.emf = Persistence.createEntityManagerFactory("InstaCarsPU");
    }

    /**
     *
     * @return
     */
    public final EntityManagerFactory getEntityManagerFactory(){
        return emf;
    }

    /**
     *
     * @return
     */
    public static JpaUtil getInstance(){
        if(instance == null) {
            instance = new JpaUtil();
        }
        return instance;
    }

    /**
     * Return the entity manager.
     * @return EntityManager.
     */
    public final EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Hacer un rollback de forma segura el DAO.
     * @param dao 
     * @param closeClass 
     */
    public static final void secureRollback( CrudDAO dao, Class closeClass ){
        try{ 
            if( dao != null ){
                dao.rollbackTransaction();
            } 
        }catch(Exception ex){ 
            Logger.getLogger( closeClass.getName()).error(ex); 
        } 
    }
    
    /**
     * Cerrar de forma segura un dao.
     * @param dao
     * @param closeClass 
     */
    public static final void secureDaoClose( CrudDAO dao, Class closeClass ){
        try{ 
            if( dao != null && dao.isOpen() ){
                dao.close();
            } 
        }catch(Exception ex){ 
            Logger.getLogger( closeClass.getName()).error(ex); 
        } 
    }    
    
    /**
     *
     */
    public final void closeEntityManagerFactory(){
        if( emf != null && emf.isOpen() ){
            emf.close();
        }
        emf = null;
    }
    
    /**
     * Force open entity manager factory.
     */
    public final void forceOpenEntityManagerFactory(){
        closeEntityManagerFactory();        
        emf = Persistence.createEntityManagerFactory( "InstaCarsPU" );
    }    
    
    /**
     * Return a CrudDAO with a new (beginned) transaction.
     * @return CrudDAO
     */
    public static CrudDAO getTransactionalDAO(){
        EntityManager em = getInstance().getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        return getDAO(em);
    }

    /**
     * Return a CrudDAO. Implicit transaction
     * @return CrudDAO
     */
    public static CrudDAO getDAO(){
        return getDAO(getInstance().getEntityManager());
    }

    /**
     * Return a CrudDAO with the passed by EntityManager,
     * this method allows to join transactions.
     * @param em Create new crud dao using given entity manager.
     * @return CrudDAO
     */
    public static CrudDAO getDAO(EntityManager em){
        return new CrudDAOBean(em);
    }
    
    /**
     *
     * @param type
     * @param example
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ReadAllQuery mountReadAllQuery(Class<?> type, Object example) {

        ReadAllQuery readAllQuery = new ReadAllQuery(type);

        QueryByExamplePolicy policy = new QueryByExamplePolicy();
        policy.addSpecialOperation(String.class, "containsSubstringIgnoringCase");

        readAllQuery.setReferenceClass(type);
        readAllQuery.setExampleObject(example);

        readAllQuery.setQueryByExamplePolicy(policy);

        return readAllQuery;
}
    
    /**
     *
     * @param readAllQuery
     * @param sortField
     * @param sortAscending
     * @return
     */
    public static ReadAllQuery addOrdering(ReadAllQuery readAllQuery, String sortField, boolean sortAscending ){
        readAllQuery.addOrdering((getOrderByExpresion(readAllQuery.getExpressionBuilder(), sortField, sortAscending)));
        return readAllQuery;
    }
    
    /**
     *
     * @param readAllQuery
     * @param sortField
     * @param sortAscending
     * @return
     */
    public static ReadAllQuery addOrdering(ReadAllQuery readAllQuery, String[] sortField, boolean[] sortAscending ){
        for(Expression ex : getOrderByExpresion(readAllQuery.getExpressionBuilder(), sortField, sortAscending)){
            readAllQuery.addOrdering(ex);
        }                
        return readAllQuery;
    }
    
    /**
     *
     * @param type
     * @param example
     * @return
     */
    public static ReadAllQuery mountCountQuery(Class<?> type, Object example) {
        //ReportQuery es una subclase de readAllQuery
        ReportQuery reportQuery = new ReportQuery();

        QueryByExamplePolicy policy = new QueryByExamplePolicy();
        policy.addSpecialOperation(String.class, "containsSubstringIgnoringCase");
        reportQuery.setQueryByExamplePolicy(policy);

        reportQuery.setReferenceClass(type);
        reportQuery.setExampleObject(example);
        reportQuery.addCount();
        reportQuery.returnSingleValue();

        return reportQuery;
    }
    
    /**
     *
     * @param builder
     * @param sortField
     * @param sortAscending
     * @return
     */
    public static List<Expression> getOrderByExpresion(ExpressionBuilder builder, String[] sortField, boolean[] sortAscending) {
        
        List<Expression> expresions = Lists.newArrayList();
        
        // no es obligatorio aÃ±adir el sort se completa con ascending false para
        // todos los campos
        if ((sortField != null && sortAscending == null) || (sortField != null && sortAscending.length < sortField.length)) {
            // los arrays de boolean se inicializan a false;
            boolean fixedSort[] = new boolean[sortField.length];
            if (sortAscending != null) {
                    System.arraycopy(sortAscending, 0, fixedSort, 0, sortAscending.length);
            }
            sortAscending = fixedSort;
        }

        if (sortField != null) {                
                for (int i = 0; i < sortField.length; i++) {
                        String field = sortField[i];
                        boolean sort = sortAscending[i];
                        expresions.add(getOrderByExpresion(builder, field, sort));

                }
        }
        return expresions;
    }

    /**
     *
     * @param builder
     * @param sortField
     * @param sortAscending
     * @return
     */
    public static Expression getOrderByExpresion(ExpressionBuilder builder, String sortField, boolean sortAscending) {
            String fields[] = sortField.split("\\.");
            Expression expression;
            // es una FK que puede ser null
            if (fields.length > 1) {
                    expression = builder.getAllowingNull(fields[0]);
            } else {
                    // es un campo
                    expression = builder.get(fields[0]);
            }

            for (int i = 1; i < fields.length; i++) {
                    // es una FK que puede ser null
                    if (i < fields.length - 1) {
                            expression = expression.getAllowingNull(fields[i]);
                    } else {
                            // es un campo
                            expression = expression.get(fields[i]);
                    }
            }
            if (sortAscending) {
                    expression = expression.ascending();
            } else {
                    expression = expression.descending();
            }
            return (expression);
    }
    

}
