/*
 * Software Grupo V, S.L.
 */
package com.martin.dal;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.QueryByExamplePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * FIXME Se ha añadido .setParameter("voidvoid", "voidvoid") a algunas consultas para evitar la calidad, esto no se puede quedar así
 * @author vgimeno
 */
public class CrudDAOBean implements CrudDAO {

    private static final Logger LOG = Logger.getLogger(CrudDAOBean.class.getName());
    
    private EntityManager em;

    /**
     * Default constructor.
     * @param em
     */
    public CrudDAOBean(EntityManager em) {
        
        this.em = em;
    }

    @Override
    public boolean isTransactionActive(){
        return this.em.getTransaction() != null && this.em.getTransaction().isActive();
    }
    
    /**
     *
     * @return
     */
    public EntityManager getEntityManager(){
        return em;
    }

    /**
     *
     */
    @Override
    public void commitTransaction(){
        EntityTransaction et = this.em.getTransaction();
        et.commit();
    }

    /**
     *
     */
    @Override
    public void rollbackTransaction(){
        EntityTransaction et = this.em.getTransaction();
        et.rollback();
    }

    /**
     *
     * @param <T>
     * @param t
     * @return
     */
    @Override
    public <T> T create(T t) {
        this.referenceFks(t);
        em.persist(t);
        em.flush();
        em.refresh(t);
        return t;
    }

    /**
     *
     * @param <T>
     * @param t
     * @return
     */
    @Override
    public <T> T createSinFlushRefresh(T t) {
        this.referenceFks(t);
        em.persist(t);
//        em.flush();
//        em.refresh(t);
        return t;
    }

    private <T> T referenceFks(T t) {
        try {
            Field fields[] = t.getClass().getDeclaredFields();
            for (Field field : fields) {
                // si es un campo FK (entonces debe de ser un objeto de persistencia con id)
                if (field.getAnnotation(ManyToOne.class) != null || field.getAnnotation(OneToOne.class) != null) {
                    boolean isFieldAccesible = field.isAccessible();
                    field.setAccessible(true);
                    // obtenemos el objeto para buscar su codigo
                    Object objectFK = field.get(t);
                    if (objectFK != null) {
                        Field fieldsFK[] = objectFK.getClass().getDeclaredFields();
                        for (Field fieldID : fieldsFK) {
                            if (fieldID.getAnnotation(Id.class) != null) {
                                boolean isFieldIDAccesible = fieldID.isAccessible();
                                fieldID.setAccessible(true);
                                Object valueID = fieldID.get(objectFK);
                                if (valueID != null) {
                                    field.set(t, this.reference(objectFK.getClass(), valueID));
                                } else {
                                    // si el codigo es null directamente el
                                    // objeto entero es null
                                    field.set(t, null);
                                }
                                fieldID.setAccessible(isFieldIDAccesible);
                            }
                        }
                    }
                    // lo dejamos como estaba.
                    field.setAccessible(isFieldAccesible);

                }
            }
        } catch (Exception e) {
            //SWSLogger.getLogger(getClass()).error("Ha ocurrido un error al referenciar las FK", e);
            throw new RuntimeException("Ha ocurrido un error al referenciar las FK", e);
        }
        return (t);
    }

    /**
     *
     * @param <T>
     * @param type
     * @param id
     * @return
     */
    @Override
    public <T> T find(Class<T> type, Object id) {
        return em.find(type, id);
    }

    /**
     *
     * @param <T>
     * @param t
     * @return
     */
    @Override
    public <T> T update(T t) {
        this.referenceFks(t);
        em.merge(t);
        //		em.flush();
        //		em.refresh(retT);
        return t;
    }

    /**
     *
     * @param obj
     */
    @Override
    public void delete(Object obj) {
        obj = em.merge(obj);
        em.remove(obj);
    }

    /**
     *
     * @param type
     * @param id
     */
    @Override
    public void delete(Class<?> type, Object id) {
        Object ref = em.getReference(type, id);
        em.remove(ref);
    }

    /**
     *
     * @param namedQueryName
     */
    @Override
    public void deleteWithNamedQuery(String namedQueryName) {
        em.createNamedQuery(namedQueryName).executeUpdate();
    }

    /**
     *
     * @param namedQueryName
     * @param parameters
     */
    @Override
    public void deleteWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNamedQuery(namedQueryName);
        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        query.executeUpdate();
    }

    /**
     *
     * @param <T>
     * @param type
     * @param id
     * @return
     */
    @Override
    public <T> T reference(Class<T> type, Object id) {
        return em.getReference(type, id);
    } 
    
    /**
     *
     * @param type
     * @return
     */
    @Override
    public int count(Class<?> type) {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setReferenceClass(type);
        reportQuery.addCount();
        reportQuery.returnSingleValue();

        Query query = JpaHelper.createQuery(reportQuery, em);
        Number n = (Number) query.getSingleResult();
        return (n.intValue());
    }
    
    /**
     *
     * @param type
     * @param example
     * @return
     */
    @Override
    public int count(Class<?> type, Object example) {
        ReportQuery reportQuery = new ReportQuery();

        QueryByExamplePolicy policy = new QueryByExamplePolicy();
        policy.addSpecialOperation(String.class, "containsSubstringIgnoringCase");
        //policy.removeFromValuesToExclude(new Integer(0));
        reportQuery.setQueryByExamplePolicy(policy);

        reportQuery.setReferenceClass(type);
        reportQuery.setExampleObject(example);
        reportQuery.addCount();
        reportQuery.returnSingleValue();

        Query query = JpaHelper.createQuery(reportQuery, em);
        Number n = (Number) query.getSingleResult();
        return (n.intValue());
    }
    
    /**
     *
     * @param queryStr
     * @param parameters
     * @return
     */
    @Override
    public int findSingleIntNative(String queryStr, Map<Integer, Object> parameters ){
        Number n = findSingleWithNativeQuery(queryStr, parameters);
        if( n == null) {
            return 0;
        }
        return n.intValue();
    }

    /**
     *
     * @param <T>
     * @param type
     * @param sortField
     * @return
     */
    @Override
    public <T> List<T> doList(Class<?> type, String sortField){
        return doList(type, 0, 0, sortField, true);
    }

    /**
     *
     * @param <T>
     * @param type
     * @param sortField
     * @param sortAscending
     * @return
     */
    @Override
    public <T> List<T> doList(Class<?> type, String sortField, boolean sortAscending){
        return doList(type, 0, 0, sortField, sortAscending);
    }

    /**
     *
     * @param <T>
     * @param type
     * @param firstRow
     * @param rowsPerPage
     * @return
     */
    @Override
    public <T> List<T> doList(Class<?> type,  int firstRow, int rowsPerPage ){
        return doList(type, firstRow, rowsPerPage, null, false);
    }

    /**
     *
     * @param <T>
     * @param type
     * @param firstRow
     * @param rowsPerPage
     * @param sortField
     * @param sortAscending
     * @return
     */
    @Override
    public <T> List<T> doList(Class<?> type,  int firstRow, int rowsPerPage, String sortField, boolean sortAscending){

        ReadAllQuery readAllQuery = new ReadAllQuery();
        readAllQuery.setReferenceClass(type);

        if (sortField != null) {
            if (sortAscending) {
                readAllQuery.addAscendingOrdering(sortField);
            } else {
                readAllQuery.addDescendingOrdering(sortField);
            }
        }
        Query query = JpaHelper.createQuery(readAllQuery, em);
        //estos parametros mejor ponerlos en la Query, en ReadAllQuery no funcionan del todo bien
        if (rowsPerPage > 0) {
            query.setMaxResults(rowsPerPage);
        }
        if (firstRow > 0) {
            query.setFirstResult(firstRow);
        }

        return (query.getResultList());
    }
    
    /**
     *
     * @param <T>
     * @param type
     * @param example
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> doListQuery(Class<?> type, Object example ){
        return doListQuery(type, example, null);
    }

    /**
     *
     * @param <T>
     * @param type
     * @param example
     * @param sortField
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> doListQuery(Class<?> type, Object example, String sortField ){
        return doListQuery(type, example, sortField, true);
    }

    /**
     *
     * @param <T>
     * @param type
     * @param example
     * @param sortField
     * @param sortAscending
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> doListQuery(Class<?> type, Object example, String sortField, boolean sortAscending){
        return doListQuery(type, example, 0, 0, sortField, sortAscending);
    }

    /**
     *
     * @param <T>
     * @param type
     * @param example
     * @param firstRow
     * @param rowsPerPage
     * @param sortField
     * @param sortAscending
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> doListQuery(Class<?> type, Object example, int firstRow, int rowsPerPage, String sortField, boolean sortAscending) {
        ReadAllQuery readAllQuery = new ReadAllQuery();

        QueryByExamplePolicy policy = new QueryByExamplePolicy();
        policy.addSpecialOperation(String.class, "containsSubstringIgnoringCase");
        readAllQuery.setQueryByExamplePolicy(policy);

        readAllQuery.setReferenceClass(type);
        readAllQuery.setExampleObject(example);

        if (sortField != null) {
            if (sortAscending) {
                readAllQuery.addAscendingOrdering(sortField);
            } else {
                readAllQuery.addDescendingOrdering(sortField);
            }
        }
        Query query = JpaHelper.createQuery(readAllQuery, em);
        //estos parametros mejor ponerlos en la Query, en ReadAllQuery no funcionan del todo bien
        if (rowsPerPage > 0) {
            query.setMaxResults(rowsPerPage);
        }
        if (firstRow > 0) {
            query.setFirstResult(firstRow);
        }

        return (query.getResultList());

    }
    
    /**
     *
     * @param namedQueryName
     * @param parameters
     * @return
     */
    @Override
    public int executeUpdateWithNamedQuery(String namedQueryName, Map<String, Object> parameters){
        Query query = em.createNamedQuery(namedQueryName);
        for (Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.executeUpdate();
    }
    
    /**
     * 
     * @param sQuery
     * @param parameters
     * @return 
     */
    @Override
    public int executeUpdateWithQuery(String sQuery, Map<String, Object> parameters) {
        Query query = em.createQuery(sQuery);
        for (Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.executeUpdate();
    }

    /**
     * 
     * @param sQuery
     * @return 
     */
    @Override
    public int executeUpdateWithNativeQuery(String sQuery) {
        Query query = em.createNativeQuery(sQuery);
        return query.executeUpdate();
    }

    /**
     * 
     * @param sQuery
     * @param parameters
     * @return 
     */
    @Override
    public int executeUpdateWithNativeQuery(String sQuery, Map<Integer, Object> parameters) {
        Query query = em.createNativeQuery(sQuery);
        for (Entry<Integer, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.executeUpdate();
    }    
    
    /**************** NAMED QUERYS **************/
    
    /**
     * 
     * @param <T>
     * @param namedQueryName
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNamedQuery(String namedQueryName) {
        return em.createNamedQuery(namedQueryName).getResultList();
    }

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @param parameters
     * @return
     */
    @Override
    public <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        return findWithNamedQuery(namedQueryName, parameters, 0);
    }

    /**
     *
     * @param <T>
     * @param queryName
     * @param resultLimit
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNamedQuery(String queryName, int resultLimit) {
        return em.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
    }

    /**
     *
     * @param <T>
     * @param queryName
     * @param resultLimit
     * @param firsResult
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNamedQuery(String queryName, int resultLimit, int firsResult) {
        return em.createNamedQuery(queryName).setMaxResults(resultLimit).setFirstResult(firsResult).getResultList();
    }

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @param parameters
     * @param resultLimit
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNamedQuery(namedQueryName);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @param parameters
     * @param resultLimit
     * @param firsResult
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit, int firsResult) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNamedQuery(namedQueryName);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        if (firsResult > 0) {
            query.setFirstResult(firsResult);
        }
        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T findSingleWithNamedQuery(String namedQueryName) {

        Query query = em.createNamedQuery(namedQueryName);

        T retValue = null;

        try {
            retValue = (T) query.getSingleResult();
        } catch (NoResultException nre) {
            // return null
        }
        return retValue;
    }

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T findSingleWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNamedQuery(namedQueryName);

        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        T retValue = null;

        try {
            retValue = (T) query.getSingleResult();
        } catch (NoResultException nre) {
            // return null
        }
        return retValue;
    }

    /************* NATIVE QUERYS ******************/
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String sql, Class<T> type) {
//        String s = sql;
//        return em.createNativeQuery(s, type).setParameter("voidvoid", "voidvoid").getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param type
//     * @return
//     */
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, Class<T> type) {
//        return findWithNativeQuery(queryStr, parameters, 0, type);
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @param type
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, Class<T> type) {
//        String s = queryStr;
//        return em.createNativeQuery(s, type).setParameter("voidvoid", "voidvoid").setMaxResults(resultLimit).getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @param firsResult
//     * @param type
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, int firsResult, Class<T> type) {
//        String s = queryStr;
//        return em.createNativeQuery(s, type).setParameter("voidvoid", "voidvoid").setMaxResults(resultLimit).setFirstResult(firsResult).getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param resultLimit
//     * @param type
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, Class<T> type) {
//        String s = queryStr;
//        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
//        Query query = em.createNativeQuery(s, type).setParameter("voidvoid", "voidvoid");
//        if (resultLimit > 0) {
//            query.setMaxResults(resultLimit);
//        }
//        for (Entry<Integer, Object> entry : rawParameters) {
//            query.setParameter(entry.getKey(), entry.getValue());
//        }
//        return query.getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param resultLimit
//     * @param firsResult
//     * @param type
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult, Class<T> type) {
//        String s = queryStr;
//        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
//        Query query = em.createNativeQuery(s, type).setParameter("voidvoid", "voidvoid");
//        if (resultLimit > 0) {
//            query.setMaxResults(resultLimit);
//        }
//        if (firsResult > 0) {
//            query.setFirstResult(firsResult);
//        }
//        for (Entry<Integer, Object> entry : rawParameters) {
//            query.setParameter(entry.getKey(), entry.getValue());
//        }
//        return query.getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param sql
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String sql) {
//        String s = sql;
//        return em.createNativeQuery(s).setParameter("voidvoid", "voidvoid").getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @return
//     */
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters) {
//        String s = queryStr;
//        return findWithNativeQuery(s, parameters, 0);
//    }

//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, int resultLimit) {
//        String s = queryStr;
//        return em.createNativeQuery(s).setParameter("voidvoid", "voidvoid").setMaxResults(resultLimit).getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @param firsResult
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, int firsResult) {
//        String s = queryStr;
//        return em.createNativeQuery(s).setParameter("voidvoid", "voidvoid").setMaxResults(resultLimit).setFirstResult(firsResult).getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param resultLimit
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit) {
//        String s = queryStr;
//        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
//        Query query = em.createNativeQuery(s).setParameter("voidvoid", "voidvoid");
//        if (resultLimit > 0) {
//            query.setMaxResults(resultLimit);
//        }
//        for (Entry<Integer, Object> entry : rawParameters) {
//            query.setParameter(entry.getKey(), entry.getValue());
//        }
//        return query.getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param resultLimit
//     * @param firsResult
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult) {
//        String s = queryStr;
//        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
//        Query query = em.createNativeQuery(s).setParameter("voidvoid", "voidvoid");
//        if (resultLimit > 0) {
//            query.setMaxResults(resultLimit);
//        }
//        if (firsResult > 0) {
//            query.setFirstResult(firsResult);
//        }
//        for (Entry<Integer, Object> entry : rawParameters) {
//            query.setParameter(entry.getKey(), entry.getValue());
//        }
//        return query.getResultList();
//    }
//
    /**
     *
     * @param <T>
     * @param queryStr
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T findSingleWithNativeQuery(String queryStr) {
        String s = queryStr;
        Query query = em.createNativeQuery(s).setParameter("voidvoid", "voidvoid");

        T retValue = null;
        try {
            retValue = (T) query.getSingleResult();
        } catch (NoResultException nre) {
            // return null
        }
        return retValue;
    }

    /**
     *
     * @param <T>
     * @param queryStr
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T findSingleWithNativeQuery(String queryStr, Map<Integer, Object> parameters) {
        String s = queryStr;
        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNativeQuery(s).setParameter("voidvoid", "voidvoid");

        for (Entry<Integer, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        T retValue = null;
        try {
            retValue = (T) query.getSingleResult();
        } catch (NoResultException nre) {
            // return null
        }
        return retValue;
    }

    /************* QUERY *************************/
    
//    /**
//     * 
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @return 
//     */
//    @Override
//    public <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters) {
//        String s = queryStr;
//        return findWithQuery(s, parameters, 0);
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithQuery(String queryStr) {
//        String s = queryStr;
//        return createQuery(s).getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithQuery(String queryStr, int resultLimit) {
//        return createQuery(queryStr).setMaxResults(resultLimit).getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @param firsResult
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithQuery(String queryStr, int resultLimit, int firsResult) {
//        return createQuery(queryStr).setMaxResults(resultLimit).setFirstResult(firsResult).getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param resultLimit
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters, int resultLimit) {
//        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
//        Query query = createQuery(queryStr);
//        if (resultLimit > 0) {
//            query.setMaxResults(resultLimit);
//        }
//        for (Entry<String, Object> entry : rawParameters) {
//            query.setParameter(entry.getKey(), entry.getValue());
//        }
//        return query.getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param resultLimit
//     * @param firsResult
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters, int resultLimit, int firsResult) {
//        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
//        Query query = createQuery(queryStr);
//        if (resultLimit > 0) {
//            query.setMaxResults(resultLimit);
//        }
//        if (firsResult > 0) {
//            query.setFirstResult(firsResult);
//        }
//        for (Entry<String, Object> entry : rawParameters) {
//            query.setParameter(entry.getKey(), entry.getValue());
//        }
//        return query.getResultList();
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> T findSingleWithQuery(String queryStr) {
//
//        Query query = createQuery(queryStr);
//
//        T retValue = null;
//        try {
//            retValue = (T) query.getSingleResult();
//        } catch (NoResultException nre) {
//            // return null
//        }
//        return retValue;
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> T findSingleWithQuery(String queryStr, Map<String, Object> parameters) {
//        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
//        Query query = createQuery(queryStr);
//
//        for (Entry<String, Object> entry : rawParameters) {
//            query.setParameter(entry.getKey(), entry.getValue());
//        }
//
//        T retValue = null;
//        try {
//            retValue = (T) query.getSingleResult();
//        } catch (NoResultException nre) {
//            // return null
//        }
//        return retValue;
//    }

    /**
     *
     */
    @Override
    public void invalidateAll() {
        ((JpaEntityManager)em.getDelegate()).getServerSession().getIdentityMapAccessor().invalidateAll();
    }
    
    /**
     *
     * @param clazz
     */
    @Override
    public void invalidateClass(Class clazz) {
        ((JpaEntityManager)em.getDelegate()).getServerSession().getIdentityMapAccessor().invalidateClass(clazz);
    }
   
    
//    /**
//     * 
//     * @param queryStr
//     * @return 
//     */
//    private Query createQuery(final String queryStr){
//        return em.createQuery( queryStr );
//    }
    
    
    @Override
     public <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters) {
        return findWithQuery(queryStr, parameters, 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithQuery(String queryStr) {
        return em.createQuery(queryStr).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithQuery(String queryStr, int resultLimit) {
        return em.createQuery(queryStr).setMaxResults(resultLimit).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithQuery(String queryStr, int resultLimit, int firsResult) {
        return em.createQuery(queryStr).setMaxResults(resultLimit).setFirstResult(firsResult).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters, int resultLimit) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = em.createQuery(queryStr);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters, int resultLimit, int firsResult) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = em.createQuery(queryStr);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        if (firsResult > 0) {
            query.setFirstResult(firsResult);
        }
        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }
    
        /**
     *
     * @param <T>
     * @param stringQuery
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T findSingleWithQuery(String stringQuery) {

        Query query = em.createQuery(stringQuery);

        T retValue = null;

        try {
            retValue = (T) query.getSingleResult();
        } catch (NoResultException nre) {
            // return null
        }
        return retValue;
    }

    /**
     *
     * @param <T>
     * @param stringQuery
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T findSingleWithQuery(String stringQuery, Map<String, Object> parameters) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = em.createQuery(stringQuery);

        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        T retValue = null;

        try {
            retValue = (T) query.getSingleResult();
        } catch (NoResultException nre) {
            // return null
        }
        return retValue;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String sql, Class<T> type) {
        return em.createNativeQuery(sql, type).getResultList();
    }

    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, Class<T> type) {
        return findWithNativeQuery(queryStr, parameters, 0, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, Class<T> type) {
        return em.createNativeQuery(queryStr, type).setMaxResults(resultLimit).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, int firsResult, Class<T> type) {
        return em.createNativeQuery(queryStr, type).setMaxResults(resultLimit).setFirstResult(firsResult).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, Class<T> type) {
        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNativeQuery(queryStr, type);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Entry<Integer, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult, Class<T> type) {
        return findWithNativeQuery(queryStr, parameters, resultLimit, firsResult, type, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult, Class<T> type, Map<String,Object> hints) {
        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNativeQuery(queryStr, type);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        if (firsResult > 0) {
            query.setFirstResult(firsResult);
        }
        for (Entry<Integer, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        if( hints != null ){
            for( Entry<String,Object> entry : hints.entrySet() ){
                query.setHint( entry.getKey(), entry.getValue() );
            }
        }
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String sql) {
        return em.createNativeQuery(sql).getResultList();
    }

    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters) {
        return findWithNativeQuery(queryStr, parameters, 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, int resultLimit) {
        return em.createNativeQuery(queryStr).setMaxResults(resultLimit).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, int firsResult) {
        return em.createNativeQuery(queryStr).setMaxResults(resultLimit).setFirstResult(firsResult).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit) {
        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNativeQuery(queryStr);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Entry<Integer, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult) {
        Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
        Query query = em.createNativeQuery(queryStr);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        if (firsResult > 0) {
            query.setFirstResult(firsResult);
        }
        for (Entry<Integer, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }
    
    @Override
    public boolean isOpen(){
        if (em != null) {
            return em.isOpen();
        }
        return false;
    }
    
    /**
     * 
     */
    @Override
    public void close() {
        try {
            if (em != null) {
                em.close();
            }
        } catch (Exception ignored) {
            LOG.error(ignored.getMessage(), ignored);
        }
    }    
    
}
