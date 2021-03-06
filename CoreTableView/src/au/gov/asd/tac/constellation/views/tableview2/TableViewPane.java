/*
 * Copyright 2010-2021 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.views.tableview2;

import au.gov.asd.tac.constellation.graph.Attribute;
import au.gov.asd.tac.constellation.graph.Graph;
import au.gov.asd.tac.constellation.graph.GraphAttribute;
import au.gov.asd.tac.constellation.graph.GraphElementType;
import au.gov.asd.tac.constellation.graph.ReadableGraph;
import au.gov.asd.tac.constellation.graph.attribute.interaction.AbstractAttributeInteraction;
import au.gov.asd.tac.constellation.graph.manager.GraphManager;
import au.gov.asd.tac.constellation.graph.processing.GraphRecordStoreUtilities;
import au.gov.asd.tac.constellation.graph.schema.visual.concept.VisualConcept;
import au.gov.asd.tac.constellation.plugins.PluginExecution;
import au.gov.asd.tac.constellation.utilities.color.ConstellationColor;
import au.gov.asd.tac.constellation.utilities.datastructure.ImmutableObjectCache;
import au.gov.asd.tac.constellation.utilities.datastructure.ThreeTuple;
import au.gov.asd.tac.constellation.utilities.datastructure.Tuple;
import au.gov.asd.tac.constellation.utilities.icon.UserInterfaceIconProvider;
import au.gov.asd.tac.constellation.utilities.text.SeparatorConstants;
import au.gov.asd.tac.constellation.views.tableview2.io.TableViewPreferencesIOUtilities;
import au.gov.asd.tac.constellation.views.tableview2.state.TableViewState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javax.swing.SwingUtilities;
import org.controlsfx.control.table.TableFilter;
import org.openide.util.HelpCtx;

/**
 * Table View Pane.
 *
 * TODO: some javafx classes no are longer supported, fix it.
 *
 * @author elnath
 * @author cygnus_x-1
 * @author antares
 */
public final class TableViewPane extends BorderPane {

    private static final Logger LOGGER = Logger.getLogger(TableViewPane.class.getName());

    private static final Object LOCK = new Object();

    private static final String ATTEMPT_PROCESS_JAVAFX = "Attempting to process on the JavaFX Application Thread";
    private static final String ATTEMPT_PROCESS_EDT = "Attempting to process on the EDT";

    private static final String ALL_COLUMNS = "Show All Columns";
    private static final String DEFAULT_COLUMNS = "Show Default Columns";
    private static final String KEY_COLUMNS = "Show Key Columns";
    private static final String NO_COLUMNS = "Show No Columns";
    private static final String COLUMN_VISIBILITY = "Column Visibility";
    private static final String SELECTED_ONLY = "Selected Only";
    private static final String ELEMENT_TYPE = "Element Type";
    private static final String COPY_CELL = "Copy Cell";
    private static final String COPY_ROW = "Copy Row";
    private static final String COPY_COLUMN = "Copy Column";
    private static final String COPY_COLUMN_UNIQUE = "Copy Column (Unique)";
    private static final String COPY_TABLE = "Copy Table";
    private static final String COPY_TABLE_SELECTION = "Copy Table (Selection)";
    private static final String EXPORT_CSV = "Export to CSV";
    private static final String EXPORT_CSV_SELECTION = "Export to CSV (Selection)";
    private static final String EXPORT_XLSX = "Export to Excel";
    private static final String EXPORT_XLSX_SELECTION = "Export to Excel (Selection)";
    private static final String FILTER_CAPTION = "Filter:";

    private static final ImageView COLUMNS_ICON = new ImageView(UserInterfaceIconProvider.COLUMNS.buildImage(16));
    private static final ImageView SELECTED_VISIBLE_ICON = new ImageView(UserInterfaceIconProvider.VISIBLE.buildImage(16, ConstellationColor.CHERRY.getJavaColor()));
    private static final ImageView ALL_VISIBLE_ICON = new ImageView(UserInterfaceIconProvider.VISIBLE.buildImage(16));
    private static final ImageView VERTEX_ICON = new ImageView(UserInterfaceIconProvider.NODES.buildImage(16));
    private static final ImageView TRANSACTION_ICON = new ImageView(UserInterfaceIconProvider.TRANSACTIONS.buildImage(16));
    private static final ImageView COPY_ICON = new ImageView(UserInterfaceIconProvider.COPY.buildImage(16));
    private static final ImageView EXPORT_ICON = new ImageView(UserInterfaceIconProvider.UPLOAD.buildImage(16));
    private static final ImageView SETTINGS_ICON = new ImageView(UserInterfaceIconProvider.SETTINGS.buildImage(16));
    private static final ImageView MENU_ICON_SOURCE = new ImageView(UserInterfaceIconProvider.MENU.buildImage(16));
    private static final ImageView MENU_ICON_DESTINATION = new ImageView(UserInterfaceIconProvider.MENU.buildImage(16));
    private static final ImageView MENU_ICON_TRANSACTION = new ImageView(UserInterfaceIconProvider.MENU.buildImage(16));

    private static final int WIDTH = 120;
    private static final int DEFAULT_MAX_ROWS_PER_PAGE = 500;
    private int maxRowsPerPage = DEFAULT_MAX_ROWS_PER_PAGE;

    private final ToggleGroup pageSizeToggle = new ToggleGroup();

    private final TableViewTopComponent parent;
    private final CopyOnWriteArrayList<ThreeTuple<String, Attribute, TableColumn<ObservableList<String>, String>>> columnIndex;
    private final Map<Integer, ObservableList<String>> elementIdToRowIndex;
    private final Map<ObservableList<String>, Integer> rowToElementIdIndex;
    private Change<? extends TableColumn<ObservableList<String>, ?>> lastChange;
    private List<ObservableList<String>> previousPageRows = null;
    private final Set<ObservableList<String>> selectedOnlySelectedRows = new HashSet<>();

    private final TableView<ObservableList<String>> table;
    private TableFilter<ObservableList<String>> filter;
    private final BorderPane progress;
    private SortedList<ObservableList<String>> sortedRowList;
    private List<ObservableList<String>> filteredRowList;
    private Pagination pagination;
    private final ToolBar toolbar;

    private Button columnVisibilityButton;
    private ToggleButton selectedOnlyButton;
    private Button elementTypeButton;
    private MenuButton copyButton;
    private MenuButton exportButton;
    private MenuButton preferencesButton;

    private final ReadOnlyObjectProperty<ObservableList<String>> selectedProperty;
    private final ChangeListener<ObservableList<String>> tableSelectionListener;
    private final ListChangeListener selectedOnlySelectionListener;

    private boolean sortingListenerActive = false;
    private final ChangeListener<? super Comparator<? super ObservableList<String>>> tableComparatorListener;
    private final ChangeListener<? super TableColumn.SortType> tableSortTypeListener;

    // Store details of sort order changes made upon column order change or table
    // preference loading - these are used to reinstate the sorting after data update
    private String sortByColumnName = "";
    private TableColumn.SortType sortByType = TableColumn.SortType.ASCENDING;

    private final ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture;

    /**
     * Cache strings used in table cells to significantly reduce memory used by
     * the same string repeated in columns and rows.
     */
    private ImmutableObjectCache displayTextCache;

    private enum UpdateMethod {
        ADD,
        REMOVE,
        REPLACE
    }

    public TableViewPane(final TableViewTopComponent parent) {
        this.parent = parent;
        this.columnIndex = new CopyOnWriteArrayList<>();
        this.elementIdToRowIndex = new HashMap<>();
        this.rowToElementIdIndex = new HashMap<>();
        this.lastChange = null;

        this.toolbar = initToolbar();
        setLeft(toolbar);

        this.table = new TableView<>();
        table.itemsProperty().addListener((v, o, n) -> table.refresh());
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setPadding(new Insets(5));

        this.pagination = new Pagination();

        this.sortedRowList = new SortedList<>(FXCollections.observableArrayList());
        sortedRowList.comparatorProperty().bind(table.comparatorProperty());
        paginate(sortedRowList);

        // TODO: experiment with caching
        table.setCache(false);

        this.progress = new BorderPane();
        final ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);
        progress.setCenter(progressIndicator);

        this.tableSelectionListener = (v, o, n) -> {
            if (parent.getCurrentState() != null && !parent.getCurrentState().isSelectedOnly()) {
                TableViewUtilities.copySelectionToGraph(table, rowToElementIdIndex,
                        parent.getCurrentState().getElementType(), parent.getCurrentGraph());
            }
        };
        this.selectedProperty = table.getSelectionModel().selectedItemProperty();
        selectedProperty.addListener(tableSelectionListener);

        this.selectedOnlySelectionListener = c -> {
            if (parent.getCurrentState() != null && parent.getCurrentState().isSelectedOnly()) {
                final ObservableList<ObservableList<String>> rows = table.getItems();
                rows.forEach(row -> {
                    if (table.getSelectionModel().getSelectedItems().contains(row)) {
                        selectedOnlySelectedRows.add(row);
                    } else if (selectedOnlySelectedRows.contains(row)) {
                        // remove the row from selected items as it's no longer selected in the table
                        selectedOnlySelectedRows.remove(row);
                    } else {
                        // Do nothing
                    }
                });
            }
        };
        table.getSelectionModel().getSelectedItems().addListener(selectedOnlySelectionListener);

        this.tableComparatorListener = (v, o, n) -> paginateForSortListener();
        this.tableSortTypeListener = (v, o, n) -> paginateForSortListener();

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);

        displayTextCache = new ImmutableObjectCache();
    }

    private ToolBar initToolbar() {
        this.columnVisibilityButton = new Button();
        columnVisibilityButton.setGraphic(COLUMNS_ICON);
        columnVisibilityButton.setMaxWidth(WIDTH);
        columnVisibilityButton.setPadding(new Insets(5));
        columnVisibilityButton.setTooltip(new Tooltip(COLUMN_VISIBILITY));
        columnVisibilityButton.setOnAction(e -> {
            final ContextMenu contextMenu = initColumnVisibilityContextMenu();
            contextMenu.show(columnVisibilityButton, Side.RIGHT, 0, 0);
            e.consume();
        });

        this.selectedOnlyButton = new ToggleButton();
        selectedOnlyButton.setGraphic(ALL_VISIBLE_ICON);
        selectedOnlyButton.setMaxWidth(WIDTH);
        selectedOnlyButton.setPadding(new Insets(5));
        selectedOnlyButton.setTooltip(new Tooltip(SELECTED_ONLY));
        selectedOnlyButton.setOnAction(e -> {
            selectedOnlyButton.setGraphic(selectedOnlyButton.getGraphic().equals(SELECTED_VISIBLE_ICON) ? ALL_VISIBLE_ICON : SELECTED_VISIBLE_ICON);
            if (parent.getCurrentState() != null) {
                selectedOnlySelectedRows.clear();
                final TableViewState newState = new TableViewState(parent.getCurrentState());
                newState.setSelectedOnly(!parent.getCurrentState().isSelectedOnly());
                PluginExecution.withPlugin(new TableViewUtilities.UpdateStatePlugin(newState)).executeLater(parent.getCurrentGraph());
            }
            e.consume();
        });

        this.elementTypeButton = new Button();
        elementTypeButton.setGraphic(TRANSACTION_ICON);
        elementTypeButton.setMaxWidth(WIDTH);
        elementTypeButton.setPadding(new Insets(5));
        elementTypeButton.setTooltip(new Tooltip(ELEMENT_TYPE));
        elementTypeButton.setOnAction(e -> {
            elementTypeButton.setGraphic(elementTypeButton.getGraphic().equals(VERTEX_ICON) ? TRANSACTION_ICON : VERTEX_ICON);
            if (parent.getCurrentState() != null) {
                final TableViewState newState = new TableViewState(parent.getCurrentState());
                newState.setElementType(parent.getCurrentState().getElementType() == GraphElementType.TRANSACTION
                        ? GraphElementType.VERTEX : GraphElementType.TRANSACTION);
                PluginExecution.withPlugin(new TableViewUtilities.UpdateStatePlugin(newState)).executeLater(parent.getCurrentGraph());
            }
            e.consume();
        });

        this.copyButton = new MenuButton();
        copyButton.setGraphic(COPY_ICON);
        copyButton.setMaxWidth(WIDTH);
        copyButton.setPopupSide(Side.RIGHT);
        final MenuItem copyTableMenu = new MenuItem(COPY_TABLE);
        copyTableMenu.setOnAction(e -> {
            final String data = TableViewUtilities.getTableData(table, pagination, false, false);
            TableViewUtilities.copyToClipboard(data);
            e.consume();
        });
        final MenuItem copyTableSelectionMenu = new MenuItem(COPY_TABLE_SELECTION);
        copyTableSelectionMenu.setOnAction(e -> {
            final String selectedData = TableViewUtilities.getTableData(table, pagination, false, true);
            TableViewUtilities.copyToClipboard(selectedData);
            e.consume();
        });
        copyButton.getItems().addAll(copyTableMenu, copyTableSelectionMenu);

        this.exportButton = new MenuButton();
        exportButton.setGraphic(EXPORT_ICON);
        exportButton.setMaxWidth(WIDTH);
        exportButton.setPopupSide(Side.RIGHT);
        final MenuItem exportCsvItem = new MenuItem(EXPORT_CSV);
        exportCsvItem.setOnAction(e -> {
            if (parent.getCurrentGraph() != null) {
                TableViewUtilities.exportToCsv(table, pagination, false);
            }
            e.consume();
        });
        final MenuItem exportCsvSelectionItem = new MenuItem(EXPORT_CSV_SELECTION);
        exportCsvSelectionItem.setOnAction(e -> {
            if (parent.getCurrentGraph() != null) {
                TableViewUtilities.exportToCsv(table, pagination, true);
            }
            e.consume();
        });
        final MenuItem exportExcelItem = new MenuItem(EXPORT_XLSX);
        exportExcelItem.setOnAction(e -> {
            if (parent.getCurrentGraph() != null) {
                TableViewUtilities.exportToExcel(table, pagination, maxRowsPerPage, false, parent.getCurrentGraph().getId());
            }
            e.consume();
        });
        final MenuItem exportExcelSelectionItem = new MenuItem(EXPORT_XLSX_SELECTION);
        exportExcelSelectionItem.setOnAction(e -> {
            if (parent.getCurrentGraph() != null) {
                TableViewUtilities.exportToExcel(table, pagination, maxRowsPerPage, true, parent.getCurrentGraph().getId());
            }
            e.consume();
        });
        exportButton.getItems().addAll(exportCsvItem, exportCsvSelectionItem,
                exportExcelItem, exportExcelSelectionItem);

        this.preferencesButton = new MenuButton();
        preferencesButton.setGraphic(SETTINGS_ICON);
        preferencesButton.setMaxWidth(WIDTH);
        preferencesButton.setPopupSide(Side.RIGHT);
        final Menu setPageSize = createPageSizeMenu();
        final MenuItem savePrefsOption = new MenuItem("Save Table Preferences");
        savePrefsOption.setOnAction(e -> {

            if ((!table.getColumns().isEmpty()) && (GraphManager.getDefault().getActiveGraph() != null)) {
                TableViewPreferencesIOUtilities.savePreferences(parent.getCurrentState().getElementType(), table, maxRowsPerPage);
            }
            e.consume();
        });
        final MenuItem loadPrefsOption = new MenuItem("Load Table Preferences...");
        loadPrefsOption.setOnAction(e -> {
            if (GraphManager.getDefault().getActiveGraph() != null) {
                loadPreferences();
                //TODO: Replace need to sleep before paginating
                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                }
                paginate(sortedRowList);
            }
            e.consume();
        });
        preferencesButton.getItems().addAll(setPageSize, savePrefsOption, loadPrefsOption);

        final Button helpButton = new Button("", new ImageView(UserInterfaceIconProvider.HELP.buildImage(16, ConstellationColor.BLUEBERRY.getJavaColor())));
        helpButton.setTooltip(new Tooltip("Display help for Table View"));
        helpButton.setMaxWidth(WIDTH);
        helpButton.setOnAction(event -> {
            new HelpCtx(TableViewTopComponent.class.getName()).display();
        });

        final ToolBar toolbar = new ToolBar(columnVisibilityButton, selectedOnlyButton,
                elementTypeButton, new Separator(), copyButton, exportButton, preferencesButton, helpButton);
        toolbar.setOrientation(Orientation.VERTICAL);
        toolbar.setPadding(new Insets(5));

        return toolbar;
    }

    private ContextMenu initColumnVisibilityContextMenu() {
        final ContextMenu cm = new ContextMenu();
        final List<CustomMenuItem> columnCheckboxesSource = new ArrayList<>();
        final List<CustomMenuItem> columnCheckboxesDestination = new ArrayList<>();
        final List<CustomMenuItem> columnCheckboxesTransaction = new ArrayList<>();

        final MenuButton splitSourceButton = new MenuButton("Source");
        splitSourceButton.setGraphic(MENU_ICON_SOURCE);
        splitSourceButton.setMaxWidth(WIDTH);
        splitSourceButton.setPopupSide(Side.RIGHT);

        final MenuButton splitDestinationButton = new MenuButton("Destination");
        splitDestinationButton.setGraphic(MENU_ICON_DESTINATION);
        splitDestinationButton.setMaxWidth(WIDTH);
        splitDestinationButton.setPopupSide(Side.RIGHT);

        final MenuButton splitTransactionButton = new MenuButton("Transaction");
        splitTransactionButton.setGraphic(MENU_ICON_TRANSACTION);
        splitTransactionButton.setMaxWidth(WIDTH);
        splitTransactionButton.setPopupSide(Side.RIGHT);

        final CustomMenuItem allColumns = new CustomMenuItem(new Label(ALL_COLUMNS));
        allColumns.setHideOnClick(false);
        allColumns.setOnAction(e -> {
            updateVisibleColumns(parent.getCurrentGraph(), parent.getCurrentState(),
                    columnIndex, UpdateMethod.REPLACE);
            e.consume();
        });

        final CustomMenuItem defaultColumns = new CustomMenuItem(new Label(DEFAULT_COLUMNS));
        defaultColumns.setHideOnClick(false);
        defaultColumns.setOnAction(e -> {
            updateVisibleColumns(parent.getCurrentGraph(), parent.getCurrentState(),
                    columnIndex.stream()
                            .filter(columnTuple -> Character.isUpperCase(columnTuple.getSecond().getName().charAt(0)))
                            .collect(Collectors.toList()), UpdateMethod.REPLACE);
            e.consume();
        });

        final CustomMenuItem keyColumns = new CustomMenuItem(new Label(KEY_COLUMNS));
        keyColumns.setHideOnClick(false);
        keyColumns.setOnAction(e -> {
            if (parent.getCurrentGraph() != null) {
                final Set<GraphAttribute> keyAttributes = new HashSet<>();
                final ReadableGraph readableGraph = parent.getCurrentGraph().getReadableGraph();
                try {
                    final int[] vertexKeys = readableGraph.getPrimaryKey(GraphElementType.VERTEX);
                    for (final int vertexKey : vertexKeys) {
                        keyAttributes.add(new GraphAttribute(readableGraph, vertexKey));
                    }
                    final int[] transactionKeys = readableGraph.getPrimaryKey(GraphElementType.TRANSACTION);
                    for (final int transactionKey : transactionKeys) {
                        keyAttributes.add(new GraphAttribute(readableGraph, transactionKey));
                    }
                } finally {
                    readableGraph.release();
                }
                updateVisibleColumns(parent.getCurrentGraph(), parent.getCurrentState(),
                        columnIndex.stream()
                                .filter(columnTuple -> keyAttributes.stream()
                                .anyMatch(keyAttribute -> keyAttribute.equals(columnTuple.getSecond())))
                                .collect(Collectors.toList()), UpdateMethod.REPLACE);
                e.consume();
            }
        });

        final CustomMenuItem noColumns = new CustomMenuItem(new Label(NO_COLUMNS));
        noColumns.setHideOnClick(false);
        noColumns.setOnAction(e -> {
            columnIndex.forEach(columnTuple -> {
                columnTuple.getThird().setVisible(false);
            });
            updateVisibleColumns(parent.getCurrentGraph(), parent.getCurrentState(),
                    Collections.emptyList(), UpdateMethod.REPLACE);
            e.consume();
        });

        cm.getItems().addAll(allColumns, defaultColumns, keyColumns, noColumns, new SeparatorMenuItem());

        final Label columnFilterLabelSource = new Label(FILTER_CAPTION);
        final TextField columnFilterTextFieldSource = new TextField();
        final HBox filterBoxSource = new HBox();
        filterBoxSource.getChildren().addAll(columnFilterLabelSource, columnFilterTextFieldSource);
        final CustomMenuItem columnFilterSource = new CustomMenuItem(filterBoxSource);
        columnFilterSource.setHideOnClick(false);

        columnFilterTextFieldSource.setOnKeyReleased(event -> {
            final String filterTerm = columnFilterTextFieldSource.getText().toLowerCase().trim();
            columnCheckboxesSource.forEach(item -> {
                final String columnName = item.getId().toLowerCase();
                item.setVisible(filterTerm.isBlank() || columnName.contains(filterTerm));
            });
            event.consume();
        });
        splitSourceButton.getItems().add(columnFilterSource);

        final Label columnFilterLabelDestination = new Label(FILTER_CAPTION);
        final TextField columnFilterTextFieldDestination = new TextField();
        final HBox filterBoxDestination = new HBox();
        filterBoxDestination.getChildren().addAll(columnFilterLabelDestination, columnFilterTextFieldDestination);
        final CustomMenuItem columnFilterDestination = new CustomMenuItem(filterBoxDestination);
        columnFilterDestination.setHideOnClick(false);

        columnFilterTextFieldDestination.setOnKeyReleased(event -> {
            final String filterTerm = columnFilterTextFieldDestination.getText().toLowerCase().trim();
            columnCheckboxesDestination.forEach(item -> {
                final String columnName = item.getId().toLowerCase();
                item.setVisible(filterTerm.isBlank() || columnName.contains(filterTerm));
            });
            event.consume();
        });
        splitDestinationButton.getItems().add(columnFilterDestination);

        final Label columnFilterLabelTransaction = new Label(FILTER_CAPTION);
        final TextField columnFilterTextFieldTransaction = new TextField();
        final HBox filterBoxTransaction = new HBox();
        filterBoxTransaction.getChildren().addAll(columnFilterLabelTransaction, columnFilterTextFieldTransaction);
        final CustomMenuItem columnFilterTransaction = new CustomMenuItem(filterBoxTransaction);
        columnFilterTransaction.setHideOnClick(false);

        columnFilterTextFieldTransaction.setOnKeyReleased(event -> {
            final String filterTerm = columnFilterTextFieldTransaction.getText().toLowerCase().trim();
            columnCheckboxesTransaction.forEach(item -> {
                final String columnName = item.getId().toLowerCase();
                item.setVisible(filterTerm.isBlank() || columnName.contains(filterTerm));
            });
            event.consume();
        });
        splitTransactionButton.getItems().add(columnFilterTransaction);

        columnIndex.forEach(columnTuple -> {
            final String columnHeading = columnTuple.getFirst();
            if (null != columnHeading) {
                switch (columnHeading) {
                    case GraphRecordStoreUtilities.SOURCE:
                        columnCheckboxesSource.add(getColumnVisibility(columnTuple));
                        break;
                    case GraphRecordStoreUtilities.DESTINATION:
                        columnCheckboxesDestination.add(getColumnVisibility(columnTuple));
                        break;
                    case GraphRecordStoreUtilities.TRANSACTION:
                        columnCheckboxesTransaction.add(getColumnVisibility(columnTuple));
                        break;
                    default:
                        break;
                }
            }
        });

        if (!columnCheckboxesSource.isEmpty()) {
            splitSourceButton.getItems().addAll(columnCheckboxesSource);
            final CustomMenuItem sourceMenu = new CustomMenuItem(splitSourceButton);
            sourceMenu.setHideOnClick(false);
            cm.getItems().add(sourceMenu);
        }
        if (!columnCheckboxesDestination.isEmpty()) {
            splitDestinationButton.getItems().addAll(columnCheckboxesDestination);
            final CustomMenuItem destinationMenu = new CustomMenuItem(splitDestinationButton);
            destinationMenu.setHideOnClick(false);
            cm.getItems().add(destinationMenu);
        }
        if (!columnCheckboxesTransaction.isEmpty()) {
            splitTransactionButton.getItems().addAll(columnCheckboxesTransaction);
            final CustomMenuItem transactionMenu = new CustomMenuItem(splitTransactionButton);
            transactionMenu.setHideOnClick(false);
            cm.getItems().add(transactionMenu);
        }

        return cm;
    }

    private CustomMenuItem getColumnVisibility(final ThreeTuple<String, Attribute, TableColumn<ObservableList<String>, String>> columnTuple) {
        final CheckBox columnCheckbox = new CheckBox(columnTuple.getThird().getText());
        columnCheckbox.selectedProperty().bindBidirectional(columnTuple.getThird().visibleProperty());
        columnCheckbox.setOnAction(e -> {
            updateVisibleColumns(parent.getCurrentGraph(), parent.getCurrentState(), Arrays.asList(columnTuple),
                    ((CheckBox) e.getSource()).isSelected() ? UpdateMethod.ADD : UpdateMethod.REMOVE);
            e.consume();
        });

        final CustomMenuItem columnVisibility = new CustomMenuItem(columnCheckbox);
        columnVisibility.setHideOnClick(false);
        columnVisibility.setId(columnTuple.getThird().getText());
        return columnVisibility;
    }

    private void updateVisibleColumns(final Graph graph, final TableViewState state,
            final List<ThreeTuple<String, Attribute, TableColumn<ObservableList<String>, String>>> columns, final UpdateMethod updateState) {
        if (graph != null && state != null) {
            final TableViewState newState = new TableViewState(state);

            final List<Tuple<String, Attribute>> columnAttributes = new ArrayList<>();
            switch (updateState) {
                case ADD:
                    if (newState.getColumnAttributes() != null) {
                        columnAttributes.addAll(newState.getColumnAttributes());
                    }
                    columnAttributes.addAll(columns.stream()
                            .map(columnTuple -> Tuple.create(columnTuple.getFirst(), columnTuple.getSecond()))
                            .collect(Collectors.toList()));
                    break;
                case REMOVE:
                    if (newState.getColumnAttributes() != null) {
                        columnAttributes.addAll(newState.getColumnAttributes());
                    }
                    columnAttributes.removeAll(columns.stream()
                            .map(columnTuple -> Tuple.create(columnTuple.getFirst(), columnTuple.getSecond()))
                            .collect(Collectors.toList()));
                    break;
                case REPLACE:
                    columnAttributes.addAll(columns.stream()
                            .map(columnTuple -> Tuple.create(columnTuple.getFirst(), columnTuple.getSecond()))
                            .collect(Collectors.toList()));
                    break;
            }

            newState.setColumnAttributes(columnAttributes);
            PluginExecution.withPlugin(new TableViewUtilities.UpdateStatePlugin(newState)).executeLater(graph);
        }
    }

    private Menu createPageSizeMenu() {
        final Menu pageSizeMenu = new Menu("Set Page Size");
        final List<Integer> pageSizes = Arrays.asList(100, 250, 500, 1000);
        for (final Integer pageSize : pageSizes) {
            final RadioMenuItem pageSizeOption = new RadioMenuItem(pageSize.toString());
            pageSizeOption.setToggleGroup(pageSizeToggle);
            pageSizeOption.setOnAction(e -> {
                if (maxRowsPerPage != pageSize) {
                    maxRowsPerPage = pageSize;
                    paginate(sortedRowList);
                }
            });
            if (pageSize == DEFAULT_MAX_ROWS_PER_PAGE) {
                pageSizeOption.setSelected(true); // initially set the default as selected
            }
            pageSizeMenu.getItems().add(pageSizeOption);
        }
        return pageSizeMenu;
    }

    private ContextMenu initRightClickContextMenu(final TableCell<ObservableList<String>, String> cell) {
        final ContextMenu cm = new ContextMenu();

        final MenuItem copyCell = new MenuItem(COPY_CELL);
        copyCell.setOnAction(e -> {
            final String cellData = cell.getItem();
            TableViewUtilities.copyToClipboard(cellData);
            e.consume();
        });

        final MenuItem copyRow = new MenuItem(COPY_ROW);
        copyRow.setOnAction(e -> {
            final String rowData = cell.getTableRow().getItem().stream()
                    .reduce((cell1, cell2) -> cell1 + SeparatorConstants.COMMA + cell2).get();
            TableViewUtilities.copyToClipboard(rowData);
            e.consume();
        });

        final MenuItem copyColumn = new MenuItem(COPY_COLUMN);
        copyColumn.setOnAction(e -> {
            final String columnData = table.getItems().stream()
                    .map(item -> cell.getTableColumn().getCellObservableValue(item).getValue())
                    .reduce((cell1, cell2) -> cell1 + SeparatorConstants.COMMA + cell2).get();
            TableViewUtilities.copyToClipboard(columnData);
            e.consume();
        });

        final MenuItem copyColumnUnique = new MenuItem(COPY_COLUMN_UNIQUE);
        copyColumnUnique.setOnAction(e -> {
            final String uniqueColumnData = table.getItems().stream()
                    .map(item -> cell.getTableColumn().getCellObservableValue(item).getValue())
                    .collect(Collectors.toSet()).stream()
                    .reduce((cell1, cell2) -> cell1 + SeparatorConstants.COMMA + cell2).get();
            TableViewUtilities.copyToClipboard(uniqueColumnData);
            e.consume();
        });

        cm.getItems().addAll(copyCell, copyRow, copyColumn, copyColumnUnique);

        return cm;
    }

    /**
     * Save current sort order details, i.e. sort column name and order for
     * future reference. This required as the bespoke data loading in tables is
     * causing sort ordering to be removed - ie when users update column order.
     * By storing this sort information the values can be used to refresh the
     * sort order within updateSortOrder().
     *
     * @param columnName The name of the column sorting is being done on
     * @param sortType Direction of sorting
     */
    private void saveSortDetails(final String columnName, final TableColumn.SortType sortType) {
        sortByColumnName = columnName;
        sortByType = sortType;
    }

    /**
     * Extract any current table sort information and save this information. See
     * other saveSortDetails for reason this is done.
     */
    private void saveSortDetails() {
        if (table.getSortOrder() != null && table.getSortOrder().size() > 0) {
            // A column was selected to sort by, save its name and direction
            saveSortDetails(table.getSortOrder().get(0).getText(), table.getSortOrder().get(0).getSortType());
        } else {
            // no column is selected, clear any previously stored information.
            saveSortDetails("", TableColumn.SortType.ASCENDING);
        }
    }

    /**
     * If sort details have been stored, reapply this sorting to the tableview.
     *
     */
    private void updateSortOrder() {
        // Try to find column with name matching saved sort order/type details
        if (!sortByColumnName.isBlank()) {
            for (final TableColumn<ObservableList<String>, ?> column : table.getColumns()) {
                if (column.getText().equals(sortByColumnName)) {
                    column.setSortType(sortByType);
                    table.getSortOrder().setAll(column);
                    return;
                }
            }
        }
    }

    /**
     * Update the whole table using the graph.
     *
     * @param graph the graph to retrieve data from.
     * @param state the current table view state.
     */
    public void updateTable(final Graph graph, final TableViewState state) {
        final Thread thread = new Thread("Table View: Update Table") {
            @Override
            public void run() {
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }

                scheduledFuture = scheduledExecutorService.schedule(() -> {
                    updateToolbar(state);
                    if (graph != null) {
                        updateColumns(graph, state);
                        updateData(graph, state);
                        updateSelection(graph, state);
                        Platform.runLater(() -> {
                            updateSortOrder();
                        });
                    } else {
                        Platform.runLater(() -> {
                            table.getColumns().clear();
                        });
                    }
                }, 0, TimeUnit.MILLISECONDS);
            }
        };
        thread.start();
    }

    /**
     * Update the toolbar using the state.
     *
     * @param state the current table view state.
     */
    public void updateToolbar(final TableViewState state) {
        Platform.runLater(() -> {
            if (state != null) {
                selectedOnlyButton.setSelected(state.isSelectedOnly());
                selectedOnlyButton.setGraphic(state.isSelectedOnly()
                        ? SELECTED_VISIBLE_ICON : ALL_VISIBLE_ICON);
                elementTypeButton.setGraphic(state.getElementType() == GraphElementType.TRANSACTION
                        ? TRANSACTION_ICON : VERTEX_ICON);
            }
        });
    }

    /**
     * Update the columns in the table using the graph and state.
     * <p>
     * Note that column references are reused where possible to ensure certain
     * toolbar/menu operations to work correctly.
     * <p>
     * The entire method is synchronized so it should be thread safe and keeps
     * the locking logic simpler. Maybe this method could be broken out further.
     *
     * @param graph the graph to retrieve data from.
     * @param state the current table view state.
     */
    public void updateColumns(final Graph graph, final TableViewState state) {
        synchronized (LOCK) {
            if (graph != null && state != null) {

                if (Platform.isFxApplicationThread()) {
                    throw new IllegalStateException(ATTEMPT_PROCESS_JAVAFX);
                }

                if (SwingUtilities.isEventDispatchThread()) {
                    throw new IllegalStateException(ATTEMPT_PROCESS_EDT);
                }

                // clear current columnIndex, but cache the column objects for reuse
                final Map<String, TableColumn<ObservableList<String>, String>> columnReferenceMap = columnIndex.stream()
                        .collect(Collectors.toMap(columnTuple -> columnTuple.getThird().getText(),
                                c -> c.getThird(),
                                (e1, e2) -> e1
                        ));
                columnIndex.clear();

                // update columnIndex based on graph attributes
                final ReadableGraph readableGraph = graph.getReadableGraph();
                try {
                    final int sourceAttributeCount = readableGraph.getAttributeCount(GraphElementType.VERTEX);
                    for (int sourceAttributePosition = 0; sourceAttributePosition < sourceAttributeCount; sourceAttributePosition++) {
                        final int sourceAttributeId = readableGraph.getAttribute(GraphElementType.VERTEX, sourceAttributePosition);
                        final String sourceAttributeName = GraphRecordStoreUtilities.SOURCE + readableGraph.getAttributeName(sourceAttributeId);
                        final TableColumn<ObservableList<String>, String> column = columnReferenceMap.containsKey(sourceAttributeName)
                                ? columnReferenceMap.get(sourceAttributeName) : new TableColumn<>(sourceAttributeName);
                        columnIndex.add(ThreeTuple.create(GraphRecordStoreUtilities.SOURCE, new GraphAttribute(readableGraph, sourceAttributeId), column));
                    }

                    if (state.getElementType() == GraphElementType.TRANSACTION) {
                        final int transactionAttributeCount = readableGraph.getAttributeCount(GraphElementType.TRANSACTION);
                        for (int transactionAttributePosition = 0; transactionAttributePosition < transactionAttributeCount; transactionAttributePosition++) {
                            final int transactionAttributeId = readableGraph.getAttribute(GraphElementType.TRANSACTION, transactionAttributePosition);
                            final String transactionAttributeName = GraphRecordStoreUtilities.TRANSACTION + readableGraph.getAttributeName(transactionAttributeId);
                            final TableColumn<ObservableList<String>, String> column = columnReferenceMap.containsKey(transactionAttributeName)
                                    ? columnReferenceMap.get(transactionAttributeName) : new TableColumn<>(transactionAttributeName);
                            columnIndex.add(ThreeTuple.create(GraphRecordStoreUtilities.TRANSACTION, new GraphAttribute(readableGraph, transactionAttributeId), column));
                        }

                        final int destinationAttributeCount = readableGraph.getAttributeCount(GraphElementType.VERTEX);
                        for (int destinationAttributePosition = 0; destinationAttributePosition < destinationAttributeCount; destinationAttributePosition++) {
                            final int destinationAttributeId = readableGraph.getAttribute(GraphElementType.VERTEX, destinationAttributePosition);
                            final String destinationAttributeName = GraphRecordStoreUtilities.DESTINATION + readableGraph.getAttributeName(destinationAttributeId);
                            final TableColumn<ObservableList<String>, String> column = columnReferenceMap.containsKey(destinationAttributeName)
                                    ? columnReferenceMap.get(destinationAttributeName) : new TableColumn<>(destinationAttributeName);
                            columnIndex.add(ThreeTuple.create(GraphRecordStoreUtilities.DESTINATION, new GraphAttribute(readableGraph, destinationAttributeId), column));
                        }
                    }
                } finally {
                    readableGraph.release();
                }

                // if there are no visible columns specified, write the key columns to the state
                if (state.getColumnAttributes() == null) {
                    final ContextMenu columnVisibilityMenu = initColumnVisibilityContextMenu();
                    final MenuItem keyColumns = columnVisibilityMenu.getItems().get(2);
                    keyColumns.fire();
                    return;
                }

                // sort columns in columnIndex by state, prefix and attribute name
                columnIndex.sort((columnTuple1, columnTuple2) -> {
                    final int c1Index = state.getColumnAttributes().indexOf(Tuple.create(columnTuple1.getFirst(), columnTuple1.getSecond()));
                    final int c2Index = state.getColumnAttributes().indexOf(Tuple.create(columnTuple2.getFirst(), columnTuple2.getSecond()));
                    final String c1Type = columnTuple1.getFirst();
                    final String c2Type = columnTuple2.getFirst();

                    if (c1Index != -1 && c2Index != -1) {
                        return Integer.compare(c1Index, c2Index);
                    } else if (c1Index != -1 && c2Index == -1) {
                        return -1;
                    } else if (c1Index == -1 && c2Index != -1) {
                        return 1;
                    } else if (c1Type.equals(GraphRecordStoreUtilities.SOURCE) && c2Type.equals(GraphRecordStoreUtilities.TRANSACTION)
                            || c1Type.equals(GraphRecordStoreUtilities.SOURCE) && c2Type.equals(GraphRecordStoreUtilities.DESTINATION)
                            || c1Type.equals(GraphRecordStoreUtilities.TRANSACTION) && c2Type.equals(GraphRecordStoreUtilities.DESTINATION)) {
                        return -1;
                    } else if (c1Type.equals(GraphRecordStoreUtilities.DESTINATION) && c2Type.equals(GraphRecordStoreUtilities.TRANSACTION)
                            || c1Type.equals(GraphRecordStoreUtilities.DESTINATION) && c2Type.equals(GraphRecordStoreUtilities.SOURCE)
                            || c1Type.equals(GraphRecordStoreUtilities.TRANSACTION) && c2Type.equals(GraphRecordStoreUtilities.SOURCE)) {
                        return 1;
                    } else {
                        final String c1Name = columnTuple1.getSecond().getName();
                        final String c2Name = columnTuple2.getSecond().getName();
                        return c1Name.compareTo(c2Name);
                    }
                });

                // style and format columns in columnIndex
                columnIndex.forEach(columnTuple -> {
                    final TableColumn<ObservableList<String>, String> column = columnTuple.getThird();

                    // assign cells to columns
                    column.setCellValueFactory(cellData -> {
                        final int cellIndex = table.getColumns().indexOf(cellData.getTableColumn());
                        if (cellIndex < cellData.getValue().size()) {
                            return new SimpleStringProperty(cellData.getValue().get(cellIndex));
                        } else {
                            return null;
                        }
                    });

                    // assign values and styles to cells
                    column.setCellFactory(cellColumn -> {
                        return new TableCell<ObservableList<String>, String>() {
                            @Override
                            public void updateItem(final String item, final boolean empty) {
                                super.updateItem(item, empty);
                                if (!empty) {
                                    // set text in cell and style if it is null
                                    this.getStyleClass().remove("null-value");
                                    if (item != null) {
                                        this.setText(item);
                                    } else {
                                        this.setText("<No Value>");
                                        this.getStyleClass().add("null-value");
                                    }

                                    // color cell based on the attribute it represents
                                    this.getStyleClass().remove("element-source");
                                    this.getStyleClass().remove("element-transaction");
                                    this.getStyleClass().remove("element-destination");
                                    final String columnPrefix = columnIndex.stream()
                                            .filter(columnTuple -> columnTuple.getThird().equals(cellColumn))
                                            .map(columnTuple -> columnTuple.getFirst())
                                            .findFirst().orElse("");
                                    switch (columnPrefix) {
                                        case GraphRecordStoreUtilities.SOURCE:
                                            this.getStyleClass().add("element-source");
                                            break;
                                        case GraphRecordStoreUtilities.TRANSACTION:
                                            this.getStyleClass().add("element-transaction");
                                            break;
                                        case GraphRecordStoreUtilities.DESTINATION:
                                            this.getStyleClass().add("element-destination");
                                            break;
                                        default:
                                            // Code can't make it to here
                                            break;
                                    }

                                    // enable context menu on right-click
                                    this.setOnMouseClicked(me -> {
                                        if (me.getButton() == MouseButton.SECONDARY) {
                                            final ContextMenu contextMenu = initRightClickContextMenu(this);
                                            contextMenu.show(table, me.getScreenX(), me.getScreenY());
                                        }
                                    });
                                }
                            }
                        };
                    });
                });

                Platform.runLater(() -> {
                    selectedProperty.removeListener(tableSelectionListener);
                    table.getSelectionModel().getSelectedItems().removeListener(selectedOnlySelectionListener);

                    columnReferenceMap.forEach((columnName, column) -> column.setGraphic(null));

                    // set column visibility in columnIndex based on the state
                    columnIndex.forEach(columnTuple -> {
                        columnTuple.getThird().setVisible(state.getColumnAttributes().stream()
                                .anyMatch(a -> a.getFirst().equals(columnTuple.getFirst())
                                && a.getSecond().equals(columnTuple.getSecond())));
                    });

                    // add columns to table
                    table.getColumns().clear();
                    table.getColumns().addAll(columnIndex.stream().map(t -> t.getThird()).collect(Collectors.toList()));

                    // sort data if the column ordering changes
                    table.getColumns().addListener((final Change<? extends TableColumn<ObservableList<String>, ?>> change) -> {
                        if (lastChange == null || !lastChange.equals(change)) {
                            while (change.next()) {
                                if (change.wasReplaced() && change.getRemovedSize() == change.getAddedSize()) {
                                    saveSortDetails();
                                    final List<TableColumn<ObservableList<String>, String>> columnIndexColumns
                                            = columnIndex.stream()
                                                    .map(ci -> ci.getThird())
                                                    .collect(Collectors.toList());
                                    final List<ThreeTuple<String, Attribute, TableColumn<ObservableList<String>, String>>> orderedColumns
                                            = change.getAddedSubList().stream()
                                                    .map(c -> columnIndex.get(columnIndexColumns.indexOf(c)))
                                                    .filter(c -> (parent.getCurrentState().getColumnAttributes().contains(Tuple.create(c.getFirst(), c.getSecond()))))
                                                    .collect(Collectors.toList());
                                    updateVisibleColumns(parent.getCurrentGraph(), parent.getCurrentState(), orderedColumns, UpdateMethod.REPLACE);
                                }
                            }
                            lastChange = change;
                        }
                    });

                    table.getSelectionModel().getSelectedItems().addListener(selectedOnlySelectionListener);
                    selectedProperty.addListener(tableSelectionListener);
                });
            }
        }
    }

    /**
     * Allow user to select saved preferences file and update table view format
     * (displayed column/column order and sort order) to match values found in
     * saved preferences file.
     */
    private void loadPreferences() {
        synchronized (LOCK) {
            if (parent.getCurrentState() != null) {

                final List<TableColumn<ObservableList<String>, ?>> newColumnOrder = new ArrayList<>();
                final ThreeTuple<List<String>, Tuple<String, TableColumn.SortType>, Integer> tablePrefs
                        = TableViewPreferencesIOUtilities.getPreferences(parent.getCurrentState().getElementType(), maxRowsPerPage);

                // If no columns were found then the user abandoned load as saves cannot occur with 0 columns
                if (tablePrefs.getFirst().isEmpty()) {
                    return;
                }

                for (final String columnName : tablePrefs.getFirst()) {
                    // Loop through column names found in prefs and add associated columns to newColumnOrder list all set to visible.
                    for (final TableColumn<ObservableList<String>, ?> column : table.getColumns()) {
                        if (column.getText().equals(columnName)) {
                            final TableColumn<ObservableList<String>, ?> copy = column;
                            copy.setVisible(true);
                            newColumnOrder.add(copy);
                        }
                    }
                }

                // Populate orderedColumns with full column ThreeTuples corresponding to entires in newVolumnOrder and call updateVisibleColumns
                // to update table.
                final List<ThreeTuple<String, Attribute, TableColumn<ObservableList<String>, String>>> orderedColumns
                        = newColumnOrder.stream().map(c -> {
                            for (final ThreeTuple<String, Attribute, TableColumn<ObservableList<String>, String>> col : columnIndex) {
                                if (c.getText().equals(col.getThird().getText())) {
                                    return col;
                                }
                            }
                            // THe following can only happen
                            return columnIndex.get(newColumnOrder.indexOf(c));
                        }).collect(Collectors.toList());
                saveSortDetails(tablePrefs.getSecond().getFirst(), tablePrefs.getSecond().getSecond());
                updateVisibleColumns(parent.getCurrentGraph(), parent.getCurrentState(), orderedColumns, UpdateMethod.REPLACE);
                for (final Toggle t : pageSizeToggle.getToggles()) {
                    final RadioMenuItem pageSizeOption = (RadioMenuItem) t;
                    if (Integer.parseInt(pageSizeOption.getText()) == tablePrefs.getThird()) {
                        pageSizeOption.setSelected(true);
                        maxRowsPerPage = tablePrefs.getThird();
                        break;
                    }
                }
            }
        }
    }

    private Node createPage(final int pageIndex, final List<ObservableList<String>> rows) {
        if (rows != null) {
            // if the list of rows making up pages changes, we need to clear the list of selected rows
            if (previousPageRows == null || previousPageRows != rows) {
                selectedOnlySelectedRows.clear();
                previousPageRows = rows;
            }

            final int fromIndex = pageIndex * maxRowsPerPage;
            final int toIndex = Math.min(fromIndex + maxRowsPerPage, rows.size());

            selectedProperty.removeListener(tableSelectionListener);
            table.getSelectionModel().getSelectedItems().removeListener(selectedOnlySelectionListener);
            sortedRowList.comparatorProperty().removeListener(tableComparatorListener);

            //get the previous sort details so that we don't lose it upon switching pages
            TableColumn<ObservableList<String>, ?> sortCol = null;
            TableColumn.SortType sortType = null;
            if (!table.getSortOrder().isEmpty()) {
                sortCol = table.getSortOrder().get(0);
                sortType = sortCol.getSortType();
                sortCol.sortTypeProperty().removeListener(tableSortTypeListener);
            }

            sortedRowList.comparatorProperty().unbind();

            table.setItems(FXCollections.observableArrayList(rows.subList(fromIndex, toIndex)));

            //restore the sort details
            if (sortCol != null) {
                table.getSortOrder().add(sortCol);
                sortCol.setSortType(sortType);
                sortCol.sortTypeProperty().addListener(tableSortTypeListener);
            }

            sortedRowList.comparatorProperty().bind(table.comparatorProperty());
            updateSelectionFromFXThread(parent.getCurrentGraph(), parent.getCurrentState());

            if (parent.getCurrentState() != null && parent.getCurrentState().isSelectedOnly()) {
                final int[] selectedIndices = selectedOnlySelectedRows.stream()
                        .map(row -> table.getItems().indexOf(row)).mapToInt(i -> i).toArray();
                if (!selectedOnlySelectedRows.isEmpty()) {
                    table.getSelectionModel().selectIndices(selectedIndices[0], selectedIndices);
                }
            }

            sortedRowList.comparatorProperty().addListener(tableComparatorListener);
            table.getSelectionModel().getSelectedItems().addListener(selectedOnlySelectionListener);
            selectedProperty.addListener(tableSelectionListener);
        }

        return table;
    }

    protected void paginate(final List<ObservableList<String>> rows) {
        pagination = new Pagination(rows == null || rows.isEmpty() ? 1 : (int) Math.ceil(rows.size() / (double) maxRowsPerPage));
        pagination.setPageFactory(index -> createPage(index, rows));
        Platform.runLater(() -> {
            setCenter(pagination);
        });
    }

    private void paginateForSortListener() {
        if (!sortingListenerActive) {
            sortingListenerActive = true;
            paginate(sortedRowList);
            sortingListenerActive = false;
        }
    }

    /**
     * Update the data in the table using the graph and state.
     * <p>
     * The entire method is synchronized so it should be thread safe and keeps
     * the locking logic simpler. Maybe this method could be broken out further.
     *
     * @param graph the graph to retrieve data from.
     * @param state the current table view state.
     */
    public void updateData(final Graph graph, final TableViewState state) {
        synchronized (LOCK) {
            if (graph != null && state != null) {

                if (Platform.isFxApplicationThread()) {
                    throw new IllegalStateException(ATTEMPT_PROCESS_JAVAFX);
                }

                if (SwingUtilities.isEventDispatchThread()) {
                    throw new IllegalStateException(ATTEMPT_PROCESS_EDT);
                }

                // set progress indicator
                Platform.runLater(() -> {
                    setCenter(progress);
                });

                // update data on a new thread so as to not interrupt the progress indicator
                elementIdToRowIndex.clear();
                rowToElementIdIndex.clear();

                // build table data based on attribute values on the graph
                final List<ObservableList<String>> rows = new ArrayList<>();
                final ReadableGraph readableGraph = graph.getReadableGraph();
                try {
                    if (state.getElementType() == GraphElementType.TRANSACTION) {
                        final int selectedAttributeId = VisualConcept.TransactionAttribute.SELECTED.get(readableGraph);
                        final int transactionCount = readableGraph.getTransactionCount();
                        for (int transactionPosition = 0; transactionPosition < transactionCount; transactionPosition++) {
                            final int transactionId = readableGraph.getTransaction(transactionPosition);
                            boolean isSelected = false;
                            if (selectedAttributeId != Graph.NOT_FOUND) {
                                isSelected = readableGraph.getBooleanValue(selectedAttributeId, transactionId);
                            }
                            if (!state.isSelectedOnly() || isSelected) {
                                final ObservableList<String> rowData = FXCollections.observableArrayList();
                                columnIndex.forEach(columnTuple -> {
                                    final int attributeId = readableGraph.getAttribute(columnTuple.getSecond().getElementType(), columnTuple.getSecond().getName());
                                    final AbstractAttributeInteraction<?> interaction = AbstractAttributeInteraction.getInteraction(columnTuple.getSecond().getAttributeType());
                                    final Object attributeValue;
                                    switch (columnTuple.getFirst()) {
                                        case GraphRecordStoreUtilities.SOURCE:
                                            final int sourceVertexId = readableGraph.getTransactionSourceVertex(transactionId);
                                            attributeValue = readableGraph.getObjectValue(attributeId, sourceVertexId);
                                            break;
                                        case GraphRecordStoreUtilities.TRANSACTION:
                                            attributeValue = readableGraph.getObjectValue(attributeId, transactionId);
                                            break;
                                        case GraphRecordStoreUtilities.DESTINATION:
                                            final int destinationVertexId = readableGraph.getTransactionDestinationVertex(transactionId);
                                            attributeValue = readableGraph.getObjectValue(attributeId, destinationVertexId);
                                            break;
                                        default:
                                            attributeValue = null;
                                    }
                                    // avoid duplicate strings objects and make a massivse saving on memory use
                                    final String displayableValue = displayTextCache.deduplicate(interaction.getDisplayText(attributeValue));
                                    rowData.add(displayableValue);
                                });
                                elementIdToRowIndex.put(transactionId, rowData);
                                rowToElementIdIndex.put(rowData, transactionId);
                                rows.add(rowData);
                            }
                        }
                    } else {
                        final int selectedAttributeId = VisualConcept.VertexAttribute.SELECTED.get(readableGraph);
                        final int vertexCount = readableGraph.getVertexCount();
                        for (int vertexPosition = 0; vertexPosition < vertexCount; vertexPosition++) {
                            final int vertexId = readableGraph.getVertex(vertexPosition);
                            boolean isSelected = false;
                            if (selectedAttributeId != Graph.NOT_FOUND) {
                                isSelected = readableGraph.getBooleanValue(selectedAttributeId, vertexId);
                            }
                            if (!state.isSelectedOnly() || isSelected) {
                                final ObservableList<String> rowData = FXCollections.observableArrayList();
                                columnIndex.forEach(columnTuple -> {
                                    final int attributeId = readableGraph.getAttribute(columnTuple.getSecond().getElementType(), columnTuple.getSecond().getName());
                                    final AbstractAttributeInteraction<?> interaction = AbstractAttributeInteraction.getInteraction(columnTuple.getSecond().getAttributeType());
                                    final Object attributeValue = readableGraph.getObjectValue(attributeId, vertexId);
                                    final String displayableValue = interaction.getDisplayText(attributeValue);
                                    rowData.add(displayableValue);
                                });
                                elementIdToRowIndex.put(vertexId, rowData);
                                rowToElementIdIndex.put(rowData, vertexId);
                                rows.add(rowData);
                            }
                        }
                    }
                } finally {
                    readableGraph.release();
                }

                final CountDownLatch updateDataLatch = new CountDownLatch(1);

                Platform.runLater(() -> {
                    selectedProperty.removeListener(tableSelectionListener);
                    table.getSelectionModel().getSelectedItems().removeListener(selectedOnlySelectionListener);
                    sortedRowList.comparatorProperty().unbind();

                    // add table data to table
                    sortedRowList = new SortedList<>(FXCollections.observableArrayList(rows));

                    //need to set the table items to the whole list here so that the filter
                    //picks up the full list of options to filter before we paginate
                    table.setItems(FXCollections.observableArrayList(sortedRowList));

                    // add user defined filter to the table
                    filter = TableFilter.forTableView(table).lazy(true).apply();
                    filter.setSearchStrategy((t, u) -> {
                        try {
                            return u.toLowerCase().startsWith(t.toLowerCase());
                        } catch (final Exception ex) {
                            return false;
                        }
                    });
                    filter.getFilteredList().predicateProperty().addListener((v, o, n) -> {
                        sortedRowList.comparatorProperty().unbind();
                        filteredRowList = new FilteredList<>(FXCollections.observableArrayList(rows), filter.getFilteredList().getPredicate());
                        sortedRowList = new SortedList<>(FXCollections.observableArrayList(filteredRowList));
                        paginate(sortedRowList);
                    });
                    paginate(sortedRowList);
                    updateDataLatch.countDown();

                    table.getSelectionModel().getSelectedItems().addListener(selectedOnlySelectionListener);
                    selectedProperty.addListener(tableSelectionListener);
                });

                try {
                    updateDataLatch.await();
                } catch (final InterruptedException ex) {
                    LOGGER.log(Level.WARNING, "InterruptedException encountered while updating table data");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Update the table selection using the graph and state.
     * <p>
     * The entire method is synchronized so it should be thread safe and keeps
     * the locking logic simpler. Maybe this method could be broken out further.
     *
     * @param graph the graph to read selection from.
     * @param state the current table view state.
     */
    public void updateSelection(final Graph graph, final TableViewState state) {
        synchronized (LOCK) {
            if (graph != null && state != null) {

                if (Platform.isFxApplicationThread()) {
                    throw new IllegalStateException(ATTEMPT_PROCESS_JAVAFX);
                }

                if (SwingUtilities.isEventDispatchThread()) {
                    throw new IllegalStateException(ATTEMPT_PROCESS_EDT);
                }

                // get graph selection
                if (!state.isSelectedOnly()) {
                    final List<Integer> selectedIds = new ArrayList<>();
                    final ReadableGraph readableGraph = graph.getReadableGraph();
                    addToSelectedIds(selectedIds, readableGraph, state);

                    // update table selection
                    final int[] selectedIndices = selectedIds.stream().map(id -> elementIdToRowIndex.get(id))
                            .map(row -> table.getItems().indexOf(row)).mapToInt(i -> i).toArray();

                    Platform.runLater(() -> {
                        selectedProperty.removeListener(tableSelectionListener);
                        table.getSelectionModel().getSelectedItems().removeListener(selectedOnlySelectionListener);
                        table.getSelectionModel().clearSelection();
                        if (!selectedIds.isEmpty()) {
                            table.getSelectionModel().selectIndices(selectedIndices[0], selectedIndices);
                        }
                        table.getSelectionModel().getSelectedItems().addListener(selectedOnlySelectionListener);
                        selectedProperty.addListener(tableSelectionListener);
                    });
                }
            }
        }
    }

    /**
     * A version of the updateSelection(Graph, TableViewState) function which is
     * to be run on the JavaFX Application Thread
     *
     * @param graph the graph to read selection from.
     * @param state the current table view state.
     */
    private void updateSelectionFromFXThread(final Graph graph, final TableViewState state) {
        if (graph != null && state != null) {

            if (!Platform.isFxApplicationThread()) {
                throw new IllegalStateException("Not processing on the JavaFX Application Thread");
            }

            // get graph selection
            if (!state.isSelectedOnly()) {
                final List<Integer> selectedIds = new ArrayList<>();
                final int[][] selectedIndices = new int[1][1];
                final Thread selectedIdsThread = new Thread("Update Selection from FX Thread: Get Selected Ids") {
                    @Override
                    public void run() {
                        final ReadableGraph readableGraph = graph.getReadableGraph();
                        addToSelectedIds(selectedIds, readableGraph, state);

                        // update table selection
                        selectedIndices[0] = selectedIds.stream().map(id -> elementIdToRowIndex.get(id))
                                .map(row -> table.getItems().indexOf(row)).mapToInt(i -> i).toArray();
                    }
                };
                selectedIdsThread.start();
                try {
                    selectedIdsThread.join();
                } catch (final InterruptedException ex) {
                    LOGGER.log(Level.WARNING, "InterruptedException encountered while updating table selection");
                    Thread.currentThread().interrupt();
                }

                table.getSelectionModel().clearSelection();
                if (!selectedIds.isEmpty()) {
                    table.getSelectionModel().selectIndices(selectedIndices[0][0], selectedIndices[0]);
                }
            }
        }
    }

    /**
     * Adds vertex/transaction ids from a graph to a list of ids if the
     * vertex/transaction is selected
     *
     * @param selectedIds the list that is being added to
     * @param readableGraph the graph to read from
     * @param state the current table view state
     */
    private void addToSelectedIds(final List<Integer> selectedIds, final ReadableGraph readableGraph, final TableViewState state) {
        try {
            final boolean isVertex = state.getElementType() == GraphElementType.VERTEX;
            final int selectedAttributeId = isVertex
                    ? VisualConcept.VertexAttribute.SELECTED.get(readableGraph)
                    : VisualConcept.TransactionAttribute.SELECTED.get(readableGraph);
            final int elementCount = isVertex
                    ? readableGraph.getVertexCount()
                    : readableGraph.getTransactionCount();
            for (int elementPosition = 0; elementPosition < elementCount; elementPosition++) {
                final int elementId = isVertex
                        ? readableGraph.getVertex(elementPosition)
                        : readableGraph.getTransaction(elementPosition);
                if (selectedAttributeId != Graph.NOT_FOUND
                        && readableGraph.getBooleanValue(selectedAttributeId, elementId)) {
                    selectedIds.add(elementId);
                }
            }
        } finally {
            readableGraph.release();
        }
    }
}
