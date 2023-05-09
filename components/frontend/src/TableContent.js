import { Col, Row } from "antd";
import { useCallback } from "react";
import { getForTable } from "./rest/crud";
import { crudColumns } from "./columns/columns";
import { AntTable } from "./Table/AntTable";
import { GenericButton } from "./buttons/buttons";

export const TableContent = ({ viewState = {}, dispatch = () => { } }) => {
    const restData = useCallback(payload => getForTable(payload), []);
    const getColumns = useCallback(() => crudColumns(), []);
    const updateViewState = useCallback(state => dispatch({ type: "table", payload: state }), [dispatch]);
    const onCheck = useCallback(async (tableName, dataIndex, value, id) => console.log(tableName, dataIndex, value, id), []);
    const onRowChange = useCallback(async (tableName, dataIndex, value, id) => {
        if (dataIndex === "enable") {
            await onCheck(tableName, dataIndex, value, id);
        }
    }, [onCheck]);
    const onRowView = useCallback(id => dispatch({ type: "detail", payload: false }), [dispatch]);
    const onRowEdit = useCallback(id => dispatch({ type: "detail", payload: true }), [dispatch]);
    const onRowDelete = useCallback(id => console.log(id), []);

    return <>
        <Row justify="center" style={{ height: "calc(100% - 40px)", marginBottom: 4 }}>
            <Col span={24}>
                <AntTable
                    {...{ restData, getColumns, viewState, onRowChange, onRowView, onRowEdit, onRowDelete, onCheck }}
                    rowKey="id"
                    rowName="name"
                    updateViewRange={updateViewState}
                />
            </Col>
        </Row>
        <Row justify="end" >
            <Col span={3}>
                <GenericButton text="Add Device" type="primary" width="auto" />
            </Col>
        </Row>
    </>

}