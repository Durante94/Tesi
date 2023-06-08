import { Table, Modal } from "antd";
import { useCallback, useEffect, useMemo, useReducer } from "react";
import { adapterColumns } from "./adapterColumns";

const initialState = {
    loading: true,
    data: [],
    columns: [],
    pagination: {
        pageSizeOptions: [],
        current: 1,
        pageSize: 10,
        total: 0
    },
    filters: {},
    sort: {},
    refresh: false
},
    reducer = (state, action) => {
        switch (action.type) {
            case "columns":
                return { ...state, columns: action.payload.columns };
            case "data":
                return {
                    ...state,
                    data: action.payload.data,
                    pagination: {
                        ...state.pagination,
                        total: action.payload.total,
                        pageSizeOptions: action.payload.pageSizeOptions
                    },
                    loading: false
                };
            case "pagination":
                return {
                    ...state,
                    ...action.payload,
                    pagination: {
                        ...state.pagination,
                        ...action.payload.pagination
                    }
                };
            case "loading":
                return {
                    ...state,
                    loading: true
                }
            case "refresh":
                return {
                    ...state,
                    refresh: !state.refresh
                }
            default:
                return state;
        }
    };

export const AntTable = ({
    tableName = "",
    rowKey,
    rowName = "",
    viewState = {},
    style = {},
    restData = async () => ({ data: [], total: 0, pageSizeOptions: [] }),
    getColumns = () => ({ title: '', columns: [] }),
    updateViewRange = () => { },
    onCheck,
    onRowChange = async () => { },
    onRowView = async () => { },
    onRowEdit = async () => { },
    onRowDelete = async () => { }
}) => {
    const [{ loading, data, columns, pagination, filters, sort, refresh }, dispatch] = useReducer(reducer, {
        ...initialState,
        ...viewState,
        pagination: {
            ...initialState.pagination,
            ...viewState.pagination
        }
    });

    useEffect(() => dispatch({ type: "columns", payload: getColumns() }), [dispatch, getColumns]);

    useEffect(() => {
        dispatch({ type: "loading" });
        restData(JSON.stringify({ ...filters, sort, pageSize: pagination.pageSize, selectedPage: pagination.current }))
            .then(resp => dispatch({ type: "data", payload: resp }))
    }, [dispatch, restData, filters, sort, pagination.pageSize, pagination.current, refresh]);

    const onChange = useCallback((pagination, updatedFilters, sorter, extra) => {
        const newFilters = Object.keys(updatedFilters).reduce((prev, curr) => {
            const newProp = {};
            if (columns.filter(col => col.dataIndex === curr).reduce((_, col) => col.multi || false, false))
                newProp[curr] = updatedFilters[curr] || [];
            else
                newProp[curr] = (updatedFilters[curr] || []).at(0);
            return { ...prev, ...newProp };
        }, filters),
            stateObj = {
                filters: newFilters,
                sort: { [sorter.columnKey]: sorter.order },
                pagination: {
                    current: pagination.current,
                    pageSize: pagination.pageSize
                }
            };
        dispatch({ type: "pagination", payload: stateObj });
        updateViewRange(stateObj);
    }, [columns, filters, dispatch, updateViewRange]);

    const innerOnCheck = useCallback(async (tableName, dataIndex, value, id) => {
        await onCheck(tableName, dataIndex, value, id);
        dispatch({ type: "refresh" });
    }, [onCheck]);

    const dataSource = useMemo(() => data.map(row => ({
        ...row,
        onChange: async (tableName, dataIndex, value, id) => {
            await onRowChange(tableName, dataIndex, value, id);
            dispatch({ type: "refresh" })
        },
        onViewClick: async () => {
            dispatch({ type: "loading" });
            await onRowView(row[rowKey]);
        },
        onEditClick: async () => {
            dispatch({ type: "loading" });
            await onRowEdit(row[rowKey]);
        },
        onDeleteClick: () => Modal.warn({
            centered: true,
            title: "Warning",
            content: `Are you sure you want to cancel ${row[rowName]}?`,
            onOk: async () => {
                await onRowDelete(row[rowKey]);
                dispatch({ type: "refresh" })
            },
            closable: true
        })
    })), [rowKey, rowName, data, onRowChange, onRowView, onRowEdit, onRowDelete]);

    return <Table
        size="small"
        scroll={{ y: "60vh", x: "100vw" }}
        pagination={{
            showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items`,
            position: "bottom-left",
            ...pagination
        }}
        columns={adapterColumns(tableName, rowKey, columns, dataSource, onCheck ? innerOnCheck : false, sort, filters)}
        {...{ dataSource, loading, onChange, rowKey, style }}
    />
}