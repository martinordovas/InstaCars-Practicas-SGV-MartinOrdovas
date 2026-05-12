/*
 * Software Grupo V, S.L.
 */
package com.martin.dal;

/**
 *
 * @author jagimeno
 */
public interface BeanPaginatorInterface {

    /**
     * Actual page.
     * @return Integer
     */
    public Integer getCurrentPage();

    /**
     * Get number of pages.
     * @return Integer
     */
    public Integer getTotalPages();
    
    /**
     * Actual page.
     * @return Integer
     */
    public Integer getTotalRows();

    /**
     * Go to first page.
     * @return String View ID.
     */
    public String pageFirst();

    /**
     * Go to last page.
     * @return String View ID.
     */
    public String pageLast();

    /**
     * Go to next page.
     * @return String View ID.
     */
    public String pageNext();

    /**
     * Go to previous page.
     * @return String View ID.
     */
    public String pagePrevious();
    
    /**
     * Check if element has been selected. True if element is inside selectedElements.
     * @param element
     * @return boolean
     */
    public boolean isSelectedElement(Object element);
    
    /**
     * Get element to add to selectedElements list.
     * @param element
     * @return Object
     */
    public Object getSelectableElement(Object element);
    
}
