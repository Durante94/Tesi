import { Table } from "antd";
import { useCallback, useEffect, useReducer } from "react";
import { adapterColumns } from "./adapterColumns";

const initialState = {
    loading: true,
    dataSource: [],
    columns: [],
    pagination: {
        pageSizeOptions: [],
        current: 1,
        pageSize: 10,
        total: 0
    },
    filters: {},
    sort: {}
},
    reducer = (state, action) => {
        switch (action.type) {
            case "columns":
                return { ...state, columns: action.payload.columns };
            case "data":
                return {
                    ...state,
                    dataSource: action.payload.data,
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
            default:
                return state;
        }
    };

export const AntTable = ({ tableName = "", rowKey, viewState = {}, restData = async () => ({ data: [], total: 0, pageSizeOptions: [] }), getColumns = () => ({ title: '', columns: [] }), updateViewRange = () => { }, onCheck = () => { } }) => {
    const [{ loading, dataSource, columns, pagination, filters, sort }, dispatch] = useReducer(reducer, {
        ...initialState,
        ...viewState,
        pagination: {
            ...initialState.pagination,
            ...viewState.pagination
        }
    });

    useEffect(() => dispatch({ type: "columns", payload: getColumns() }), [dispatch, getColumns]);

    useEffect(() => {
        restData(JSON.stringify({ ...filters, sort, pageSize: pagination.pageSize, selectedPage: pagination.current }))
            .then(resp => dispatch({ type: "data", payload: resp }))
    }, [dispatch, restData, filters, sort, pagination.current, pagination.pageSize]);

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

    return <Table
        size="small"
        style={{ height: "100%" }}
        pagination={{
            showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items`,
            ...pagination
        }}
        columns={adapterColumns(tableName, rowKey, columns, dataSource, onCheck, sort, filters)}
        {...{ dataSource, loading, onChange, rowKey }}
    />
}