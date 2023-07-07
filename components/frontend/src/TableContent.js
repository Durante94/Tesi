import { Space } from "antd";
import { useCallback } from "react";
import { checkClick, deleteData, getForTable } from "./rest/crud";
import { crudColumns } from "./columns/columns";
import { AntTable } from "./Table/AntTable";
import { GenericButton } from "./buttons/buttons";

export const TableContent = ({ viewState = {}, editEnable = false, dispatch = () => { } }) => {
    const restData = useCallback(payload => getForTable(payload), []);
    const getColumns = useCallback(() => crudColumns(), []);
    const updateViewRange = useCallback(state => dispatch({ type: "table", payload: state }), [dispatch]);
    const onCheck = useCallback(async (tableName, dataIndex, value, id) => {
        if (dataIndex === "enable")
            await checkClick(tableName, dataIndex, value, id)
    }, []);
    const onRowChange = useCallback(onCheck, [onCheck]);
    const onRowView = useCallback(id => dispatch({ type: "detail", payload: { detail: true, edit: false, id } }), [dispatch]);
    const onRowEdit = useCallback(id => dispatch({ type: "detail", payload: { detail: true, edit: true, id } }), [dispatch]);
    const onRowDelete = useCallback(deleteData, []);

    return <Space align="center" direction="vertical">
        <AntTable
            {...{ restData, getColumns, viewState, onRowChange, onRowView, onRowEdit, onRowDelete, updateViewRange, ...(editEnable ? { onCheck } : {}) }}
            rowKey="id"
            rowName="name"
        />
        <GenericButton
            text="Add Device"
            type="primary"
            width="auto"
            disabled={!editEnable}
            className="cp-add-dev"
            onClick={() => dispatch({ type: "detail", payload: { detail: true, edit: true, id: null } })} />
    </Space>
}