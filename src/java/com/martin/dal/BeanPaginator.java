/*
 * Software Grupo V, S.L.
 */
package com.martin.dal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jagimeno
 * @param <T>
 */
public abstract class BeanPaginator<T> implements Serializable, BeanPaginatorInterface {

    private static final Log log = LogFactory.getLog(BeanPaginator.class);
    private static final long serialVersionUID = -441282864503702942L;
    // DEFAULTS ---------------------------------------------------------------------------------
    public static final int DEFAULT_ROWS_PER_PAGE = 20; // Default rows per page (max amount of rows to be displayed at once).
    public static final int DEFAULT_PAGE_RANGE = 20; // Default page range (max amount of page links to be displayed at once).
    public static final String DEFAULT_SORT_FIELD = "#DEFAULT"; // Default sort field.
    public static final boolean DEFAULT_SORT_ASCENDING = true; // Default sort direction.
    public static final String DEFAULT_ASCENDING_IMG = "/images/arrow_up.gif"; //Default img for sort ascending.
    public static final String DEFAULT_DESCENDING_IMG = "/images/arrow_down.gif"; //Default img for sort ascending.
    // Properties ---------------------------------------------------------------------------------
    // Data.
    private List<T> dataList;
    private int totalRows;
    // Paging.
    private int firstRow;
    private int rowsPerPage;
    private int totalPages;
    private int pageRange;
    private Integer[] pages;
    private int currentPage;
    private int gotoPage;
    // Sorting.
    private String defaultSortField; //EDU DIXIT
    private String sortField;
    private boolean sortAscending;
    private final String ascending_img;
    private final String descending_img;
    private boolean firstLoad = false;
    private boolean selectablePaginator = false;
    // Selects objects
    private List selectedElements = new ArrayList();

    // Constructors -------------------------------------------------------------------------------
    public BeanPaginator() {
        // Set default values somehow (properties files?).
        rowsPerPage = DEFAULT_ROWS_PER_PAGE;
        pageRange = DEFAULT_PAGE_RANGE;
        sortField = DEFAULT_SORT_FIELD;
        defaultSortField = DEFAULT_SORT_FIELD;
        sortAscending = DEFAULT_SORT_ASCENDING;
        ascending_img = DEFAULT_ASCENDING_IMG;
        descending_img = DEFAULT_DESCENDING_IMG;
        configureBeanPaginator();
    }

    /**
     * Constructor with rows per page.
     * @param rowsPerPage Rows per page.
     */
    public BeanPaginator(int rowsPerPage) {
        this();
        this.rowsPerPage = rowsPerPage;
    }

    /**
     * Constructor
     * @param rowsPerPage
     * @param sortField 
     */
    public BeanPaginator(int rowsPerPage, String sortField) {
        this();
        this.rowsPerPage = rowsPerPage;
        this.sortField = sortField;
        this.defaultSortField   = sortField;
    }

    /**
     * Constructor.
     * @param rowsPerPage
     * @param pageRange 
     */
    public BeanPaginator(int rowsPerPage, int pageRange) {
        this();
        this.rowsPerPage = rowsPerPage;
        this.pageRange = pageRange;
    }
    /**
     * Constructor.
     * @param rowsPerPage
     * @param pageRange
     * @param sortField 
     */
    public BeanPaginator(int rowsPerPage, int pageRange, String sortField) {
        this();
        this.rowsPerPage = rowsPerPage;
        this.pageRange = pageRange;
        this.sortField = sortField;
        this.defaultSortField   = sortField;
    }

    /**
     * Constructor
     * @param sortField 
     */
    public BeanPaginator(String sortField) {
        this();
        this.sortField = sortField;
        this.defaultSortField   = sortField;
    }

    /**
     * Constructor
     * @param sortField
     * @param sortAscending 
     */
    public BeanPaginator(String sortField, boolean sortAscending) {
        this();
        this.sortField = sortField;
        this.sortAscending = sortAscending;
        this.defaultSortField   = sortField;
    }

    /**
     * Constructor
     * @param rowsPerPage
     * @param pageRange
     * @param sortField
     * @param sortAscending 
     */
    public BeanPaginator(int rowsPerPage, int pageRange, String sortField, boolean sortAscending) {
        this();
        this.rowsPerPage = rowsPerPage;
        this.pageRange = pageRange;
        this.sortField = sortField;
        this.sortAscending = sortAscending;
        this.defaultSortField   = sortField;
    }

    /**
     * Constructor
     * @param rowsPerPage
     * @param pageRange
     * @param sortField
     * @param sortAscending
     * @param ascending_img
     * @param descending_img 
     */
    public BeanPaginator(int rowsPerPage, int pageRange, String sortField, boolean sortAscending, String ascending_img, String descending_img) {
        this.rowsPerPage = rowsPerPage;
        this.pageRange = pageRange;
        this.sortField = sortField;
        this.sortAscending = sortAscending;
        this.ascending_img = ascending_img;
        this.descending_img = descending_img;
        this.defaultSortField   = sortField;
        configureBeanPaginator();
    }

    protected void configureBeanPaginator() {
    }

    // Paging actions -----------------------------------------------------------------------------
    @Override
    public String pageFirst() {
        page(0);
        return null;//same view
    }

    @Override
    public String pageNext() {
        page(firstRow + rowsPerPage);
        return null;//same view
    }
    
    @Override
    public String pagePrevious() {
        final int temp = firstRow - rowsPerPage;
        page(temp < 0 ? 0 : temp);
        return null;//same view
    }

    @Override
    public String pageLast() {
        page(totalRows - ((totalRows % rowsPerPage != 0) ? totalRows % rowsPerPage : rowsPerPage));
        return null;//same view
    }
    
    public void page(ActionEvent event) {
        Object page = ((UICommand) event.getComponent()).getValue();
        if( page instanceof Integer ){
            page(((Integer) ((UICommand) event.getComponent()).getValue() - 1) * rowsPerPage);
        }else if( page instanceof String ){
            page((new Integer((String)((UICommand) event.getComponent()).getValue()) - 1) * rowsPerPage);
        }
    }

    /**
     * Algunos componentes ponen Listener al final automáticamente para saber que es un metodo que
     * proviene de un actionListener, así que duplicamos el page y mantenemos el antiguo por compatibilidad.
     * @param event 
     */
    public void pageListener(ActionEvent event) {
        page(event);
    }
    
    private void page(int firstRow) {
        this.firstRow = firstRow;
        loadDataList(); // Load requested page.
    }

    public void pageNextListener(ActionEvent event){
        pageNext();
    }
    
    public void pagePreviousListener(ActionEvent event){
        pagePrevious();
    }
    
    public void pageFirstListener(ActionEvent event){
        pageFirst();
    }
    
    public void pageLastListener(ActionEvent event){
        pageLast();
    }
    
    /**
     * Ir a la página que esta guardada en el gotoPage.
     */
    public void gotoPageAction(){
        if( gotoPage <= 0 ){
            gotoPage = 1;
        }
        page( (gotoPage - 1) * rowsPerPage );
    }
    
    public void gotoPageMethod(int page){
        if( page <= 0 ){
            page = 1;
        }
        page( (page - 1) * rowsPerPage );
    }
    
    // Sorting actions ----------------------------------------------------------------------------
    /**
     * Sort table with given attribute "sortField".
     * @param event 
     */
    public void sort(ActionEvent event) {
        String sortFieldAttribute = (String) event.getComponent().getAttributes().get("sortField");

        // If the same field is sorted, then reverse order, else sort the new field ascending.
        if (sortField.equals(sortFieldAttribute)) {
            sortAscending = !sortAscending;
        } else {
            sortField = sortFieldAttribute;
            sortAscending = true;
        }

        pageFirst(); // Go to first page and load requested page.
    }
    
    /**
     * Change sort with given parameters. Manual list reload its mandatory.
     * @param sortField
     * @param sortAscending 
     */
    public void changeSort(String sortField, boolean sortAscending ){
        this.sortField = sortField;
        this.sortAscending = sortAscending;
    }

    // Loaders ------------------------------------------------------------------------------------
    /**
     * Reset paginator.
     */
    public void reset() {
        dataList    = new ArrayList<T>();
        totalRows   = 0;
        firstRow    = 0;
        currentPage = 0;
        totalPages  = 0;
        pages       = new Integer[0];
    }

    /**
     * Execute queries to load data and count rows.
     */
    public void loadDataList() {

        //Check parameters if neccesary
        if (!validParameters()) {
            reset();
            return;
        }

        if( log.isDebugEnabled() ){
            log.debug("Calling 'loadDataList' to refresh object list and total rows count.");
        }
        
        // Load list and totalCount.
        try {
            
            prepareQuery();
            
            totalRows = doCountQuery();        
            
            //Comprobar si la pagina actual no contiene resultados
            if( firstRow > totalRows ){
                firstRow = 0;
            }
            
            //Recuperar listado de elementos
            dataList = doListQuery(firstRow, rowsPerPage, sortField, sortAscending);
            
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se ha podido obtener el listado del paginador.", "Ha ocurrido un error en el paginador."));
            reset();
            return;
        }

        // Set currentPage, totalPages and pages.
        currentPage = (totalRows / rowsPerPage) - ((totalRows - firstRow) / rowsPerPage) + 1;
        totalPages = (totalRows / rowsPerPage) + ((totalRows % rowsPerPage != 0) ? 1 : 0);
        
        int pagesLength = Math.min(pageRange, totalPages);
        pages = new Integer[pagesLength];

        // firstPage must be greater than 0 and lesser than totalPages-pageLength.
        int firstPage = Math.min(Math.max(0, currentPage - (pageRange / 2)), totalPages - pagesLength);

        // Create pages (page numbers for page links).
        for (int i = 0; i < pagesLength; i++) {
            pages[i] = ++firstPage;
        }
    }

    /**
     * This method must be overwritten when needed to check query parameters before doListQuery and doCountQuery
     * are invoked.
     * @return true parameters are ok, false otherwise.
     */
    public boolean validParameters() {
        return true;
    }

    /**
     * 
     */
    public void prepareQuery() {
        
    }
    
    /**
     * Execute query list.
     * @param firstRow
     * @param rowsPerPage
     * @param sortField
     * @param sortAscending
     * @return List<T> Elements to show.
     */
    protected abstract List<T> doListQuery(int firstRow, int rowsPerPage, String sortField, boolean sortAscending);

    /**
     * Execute count query.
     * @return int Number of elements.
     */
    protected abstract int doCountQuery();
    
    
    /**
     * Check if element has been selected. True if element is inside selectedElements.
     * @param element
     * @return boolean
     */
    @Override
    public boolean isSelectedElement(Object element){
        return getSelectedElements().contains(element);
    }
    
    
    /**
     * Get element to add to selectedElements list.
     * @param element
     * @return Object
     */
    @Override
    public Object getSelectableElement(Object element){
        return element;
    }

    /**
     * Go to page value, used with execute action go to page
     * @return int
     */
    public int getGotoPage() {
        return gotoPage;
    }

    public void setGotoPage(int gotoPage) {
        this.gotoPage = gotoPage;
    }
    
    
    
    // Getters ------------------------------------------------------------------------------------
    /**
     * Get loaded data list (load it if its null).
     * @return List<T>
     */
    public List<T> getDataList() {
        if (dataList == null) {
            if (firstLoad) {
                loadDataList(); // Preload page for the 1st view.
            } else {
                reset();
            }
        }
        return dataList;
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }

    public void setFirstLoad(boolean firstLoad) {
        this.firstLoad = firstLoad;
    }

    @Override
    public Integer getTotalRows() {
        return totalRows;
    }

    public Integer getFirstRow() {
        return firstRow;
    }

    public Integer getRowsPerPage() {
        return rowsPerPage;
    }

    public Integer[] getPages() {
        return pages;
    }

    @Override
    public Integer getCurrentPage() {
        return currentPage;
    }

    @Override
    public Integer getTotalPages() {
        return totalPages;
    }

    public String getSortField() {
        return sortField;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public String getCurrentSort() {
        return sortAscending ? "Ascendente" : "Descendente";
    }

    public String getImgSort() {
        return sortAscending ? ascending_img : descending_img;
    }

    //Getters para comprobacion de enabled/disabled botones paginador
    public boolean isDisabledLast() {
        return firstRow + rowsPerPage >= totalRows;
    }

    public boolean isDisabledFirst() {
        return firstRow == 0;
    }

    public boolean isDisabledNext() {
        return firstRow + rowsPerPage >= totalRows;
    }

    public boolean isDisabledPrevious() {
        return firstRow == 0;
    }

    public List getSelectedElements() {
        return selectedElements;
    }

    public boolean isSelectablePaginator() {
        return selectablePaginator;
    }

    public void setSelectablePaginator(boolean selectablePaginator) {
        this.selectablePaginator = selectablePaginator;
    }
    
    /**
     * Restore default sort field value.
     */
    public void resetToDefaultSoftfield(){
        this.sortField = this.defaultSortField;
    }
    
    // Setters ------------------------------------------------------------------------------------
    /**
     * Set rows per page to show.
     * @param rowsPerPage Rows per page.
     */
    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }
}