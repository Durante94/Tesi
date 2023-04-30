import { Table } from "antd";
import { useCallback, useEffect, useReducer } from "react";

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
                    pagination: {
                        ...state.pagination,
                        ...action.payload
                    }
                };
            default:
                return state;
        }
    };

export const AntTable = ({ viewState = {}, restData = async () => ({ data: [], total: 0, pageSizeOptions: [] }), restColumns = () => ({ title: '', columns: [] }), updateViewRange = () => { } }) => {
    const [{ loading, dataSource, columns, pagination, filters, sort }, dispatch] = useReducer(reducer, {
        ...initialState,
        ...viewState,
        pagination: {
            ...initialState.pagination,
            ...viewState.pagination
        }
    });

    useEffect(() => dispatch({ action: "columns", payload: restColumns() }), [dispatch, restColumns]);

    useEffect(() => dispatch({ type: "data", payload: restData(JSON.stringify({ ...filters, sort, pageSize: pagination.pageSize, selectedPage: pagination.current })) }),
        [dispatch, restData, filters, sort, pagination.current, pagination.pageSize]);

    const onChange = useCallback((pagination, filters, sorter, extra) => { }, []);

    return <Table
        size="small"
        style={{ height: "100%" }}
        pagination={{
            showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items`,
            ...pagination
        }}
        {...{ dataSource, columns, loading }}
    />
}