/*
 * Software Grupo V, S.L.
 */
package com.martin.dal;

import java.util.List;
import java.util.Map;

/**
 *
 * @author vgimeno
 */
public interface CrudDAO {

    /**
     *
     */
    void commitTransaction();
    
    /**
     *
     */
    void rollbackTransaction();
    
    /**
     * 
     * @return 
     */
    boolean isOpen();
    
    /**
     *
     */
    void close();
    
    public boolean isTransactionActive();
    
    /**
     *
     * @param <T>
     * @param t
     * @return
     */
    <T> T create(T t);
    
    /**
     *
     * @param <T>
     * @param t
     * @return
     */
    <T> T createSinFlushRefresh(T t);
    
    /**
     *
     * @param <T>
     * @param type
     * @param id
     * @return
     */
    <T> T find(Class<T> type, Object id);

    /**
     *
     * @param <T>
     * @param t
     * @return
     */
    <T> T update(T t);

    /**
     *
     * @param obj
     */
    void delete(Object obj);

    /**
     *
     * @param type
     * @param id
     */
    void delete(Class<?> type, Object id);

    /**
     *
     * @param queryName
     */
    void deleteWithNamedQuery(String queryName);

    /**
     *
     * @param namedQueryName
     * @param parameters
     */
    void deleteWithNamedQuery(String namedQueryName, Map<String, Object> parameters);

//    /**
//     *
//     * @param sQuery
//     * @param parameters
//     * @return
//     */
//    int executeUpdateWithQuery(String sQuery, Map<String, Object> parameters);
    
    /**
     *
     * @param namedQueryName
     * @param parameters
     * @return
     */
    int executeUpdateWithNamedQuery(String namedQueryName, Map<String, Object> parameters);
    
    /**
     * 
     * @param sQuery
     * @param parameters
     * @return 
     */
    public int executeUpdateWithQuery(String sQuery, Map<String, Object> parameters);

    /**
     * 
     * @param sQuery
     * @return 
     */
    public int executeUpdateWithNativeQuery(String sQuery);

    /**
     * 
     * @param sQuery
     * @param parameters
     * @return 
     */
    public int executeUpdateWithNativeQuery(String sQuery, Map<Integer, Object> parameters);    
    
//    /**
//     *
//     * @param sQuery
//     * @return
//     */
//    int executeUpdateWithNativeQuery(String sQuery);
    
    /**
     *
     * @param <T>
     * @param type
     * @param id
     * @return
     */
    <T> T reference(Class<T> type, Object id);

    /**
     *
     * @param type
     * @return
     */
    int count(Class<?> type);

    /**
     *
     * @param type
     * @param example
     * @return
     */
    int count(Class<?> type, Object example);
    
    /**
     *
     * @param queryStr
     * @param parameters
     * @return
     */
    int findSingleIntNative(String queryStr, Map<Integer, Object> parameters );

    /**
     *
     * @param <T>
     * @param type
     * @param sortField
     * @return
     */
    <T> List<T> doList(Class<?> type, String sortField);

    /**
     *
     * @param <T>
     * @param type
     * @param sortField
     * @param sortAscending
     * @return
     */
    <T> List<T> doList(Class<?> type, String sortField, boolean sortAscending);

    /**
     *
     * @param <T>
     * @param type
     * @param firstRow
     * @param rowsPerPage
     * @return
     */
    <T> List<T> doList(Class<?> type,  int firstRow, int rowsPerPage );

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
    <T> List<T> doList(Class<?> type,  int firstRow, int rowsPerPage, String sortField, boolean sortAscending);

    /**
     *
     * @param <T>
     * @param type
     * @param example
     * @return
     */
    <T> List<T> doListQuery(Class<?> type, Object example );

    /**
     *
     * @param <T>
     * @param type
     * @param example
     * @param sortField
     * @return
     */
    <T> List<T> doListQuery(Class<?> type, Object example, String sortField );

    /**
     *
     * @param <T>
     * @param type
     * @param example
     * @param sortField
     * @param sortAscending
     * @return
     */
    <T> List<T> doListQuery(Class<?> type, Object example, String sortField, boolean sortAscending);

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
    <T> List<T> doListQuery(Class<?> type, Object example, int firstRow, int rowsPerPage, String sortField, boolean sortAscending);

    /**
     *
     * @param <T>
     * @param queryName
     * @return
     */
    <T> List<T> findWithNamedQuery(String queryName);

    /**
     *
     * @param <T>
     * @param queryName
     * @param resultLimit
     * @return
     */
    <T> List<T> findWithNamedQuery(String queryName, int resultLimit);

    /**
     *
     * @param <T>
     * @param queryName
     * @param resultLimit
     * @param firsResult
     * @return
     */
    <T> List<T> findWithNamedQuery(String queryName, int resultLimit, int firsResult);

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @param parameters
     * @return
     */
    <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters);

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @param parameters
     * @param resultLimit
     * @return
     */
    <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit);

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @param parameters
     * @param resultLimit
     * @param firsResult
     * @return
     */
    <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit, int firsResult);

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @return
     */
    <T> T findSingleWithNamedQuery(String namedQueryName);

    /**
     *
     * @param <T>
     * @param namedQueryName
     * @param parameters
     * @return
     */
    <T> T findSingleWithNamedQuery(String namedQueryName, Map<String, Object> parameters);

//    /**
//     *
//     * @param <T>
//     * @param sql
//     * @param type
//     * @return
//     */
//    <T> List<T> findWithNativeQuery(String sql, Class<T> type);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param type
//     * @return
//     */
//    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, Class<T> type);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @param type
//     * @return
//     */
//    <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, Class<T> type);
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
//    <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, int firsResult, Class<T> type);
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
//    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, Class<T> type);
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
//    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult, Class<T> type);
//
//    /**
//     *
//     * @param <T>
//     * @param sql
//     * @return
//     */
//    <T> List<T> findWithNativeQuery(String sql);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @return
//     */
//    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @return
//     */
//    <T> List<T> findWithNativeQuery(String queryStr, int resultLimit);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @param firsResult
//     * @return
//     */
//    <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, int firsResult);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param resultLimit
//     * @return
//     */
//    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit);
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
//    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult);
//
    /**
     *
     * @param <T>
     * @param queryStr
     * @return
     */
    <T> T findSingleWithNativeQuery(String queryStr);

    /**
     *
     * @param <T>
     * @param queryStr
     * @param parameters
     * @return
     */
    <T> T findSingleWithNativeQuery(String queryStr, Map<Integer, Object> parameters);

//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @return
//     */
//    <T> List<T> findWithQuery(String queryStr);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @return
//     */
//    <T> List<T> findWithQuery(String queryStr, int resultLimit);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param resultLimit
//     * @param firsResult
//     * @return
//     */
//    <T> List<T> findWithQuery(String queryStr, int resultLimit, int firsResult);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @return
//     */
//    <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters);
//
//    /**
//     *
//     * @param <T>
//     * @param queryStr
//     * @param parameters
//     * @param resultLimit
//     * @return
//     */
//    <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters, int resultLimit);
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
//    <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters, int resultLimit, int firsResult);
//
    /**
     *
     * @param <T>
     * @param queryStr
     * @return
     */
    <T> T findSingleWithQuery(String queryStr);

    /**
     *
     * @param <T>
     * @param queryStr
     * @param parameters
     * @return
     */
    <T> T findSingleWithQuery(String queryStr, Map<String, Object> parameters);

    /**
     *
     */
    void invalidateAll();
    
    /**
     *
     * @param clazz
     */
    void invalidateClass(Class clazz);
    
    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @return
     */
    <T> List<T> findWithQuery(String queryStr);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param resultLimit
     * @return
     */
    <T> List<T> findWithQuery(String queryStr, int resultLimit);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param resultLimit
     * @param firsResult
     * @return
     */
    <T> List<T> findWithQuery(String queryStr, int resultLimit, int firsResult);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @return
     */
    <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @param resultLimit
     * @return
     */
    <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters, int resultLimit);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @param resultLimit
     * @param firsResult
     * @return
     */
    <T> List<T> findWithQuery(String queryStr, Map<String, Object> parameters, int resultLimit, int firsResult);
    
     /**
     * Ejecuta una consulta
     * @param <T>
     * @param sql
     * @param type
     * @return
     */
    <T> List<T> findWithNativeQuery(String sql, Class<T> type);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @param type
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, Class<T> type);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param resultLimit
     * @param type
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, Class<T> type);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param resultLimit
     * @param firsResult
     * @param type
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, int firsResult, Class<T> type);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @param resultLimit
     * @param type
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, Class<T> type);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @param resultLimit
     * @param firsResult
     * @param type
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult, Class<T> type);
    
    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @param resultLimit
     * @param firsResult
     * @param type
     * @param hints
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult, Class<T> type, Map<String,Object> hints);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param sql
     * @return
     */
    <T> List<T> findWithNativeQuery(String sql);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param resultLimit
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, int resultLimit);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param resultLimit
     * @param firsResult
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, int resultLimit, int firsResult);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @param resultLimit
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit);

    /**
     * Ejecuta una consulta
     * @param <T>
     * @param queryStr
     * @param parameters
     * @param resultLimit
     * @param firsResult
     * @return
     */
    <T> List<T> findWithNativeQuery(String queryStr, Map<Integer, Object> parameters, int resultLimit, int firsResult);

    
}
