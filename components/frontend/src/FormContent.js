import { useCallback, useEffect } from "react";
import { GenericButton } from "./buttons/buttons";
import { getDetail } from "./rest/crud";
import { Checkbox, Col, Form, Input, InputNumber, Row } from "antd";

export const FormContent = ({ edit, id, dispatch }) => {
    const [form] = Form.useForm();
    const { Item } = Form;

    const close = useCallback(() => dispatch({ type: "detail", payload: { detail: false } }), [dispatch]);
    const renderFormItem = useCallback(({ type, inputProps, ...props }) =>
        <Item {...{ ...props }}
            labelCol={12}
            wrapperCol={12}  >
            {{
                "text": <Input allowClear disabled={!edit} {...inputProps} />,
                "number": <InputNumber disabled={!edit} decimalSeparator="." {...inputProps} />,
                "boolean": <Checkbox disabled={!edit} {...inputProps} />,
                "select": null
            }[type]}
        </Item>,
        [edit]);

    const renderColumn = useCallback((props, key) => props.hidden
        ?
        renderFormItem({ ...props, key })
        :
        <Col xs={24} sm={24} md={12} lg={8} {...{ key }} style={{ padding: "10px 10px 0" }}>
            {renderFormItem(props)}
        </Col>,
        [renderFormItem])
    useEffect(() => {
        if (id)
            getDetail(id).then(obj => form.setFieldsValue(obj));
    }, [id, form]);

    const fieldsProps = [
        { name: "id", hidden: true, noStyle: true, type: "" },
        { name: "name", type: "text", label: "Name", required: true, rules: [{ required: true }] },
        { name: "description", type: "text", label: "Description" },
        { name: "amplitude", type: "number", label: "Configured Amplitude", required: true, rules: [{ required: true }], inputProps: { style: { width: "100%" }, disabled: true } },
        { name: "frequency", type: "number", label: "Configured Frequency", required: true, rules: [{ required: true }], inputProps: { style: { width: "100%" }, disabled: true } },
        { name: "function", type: "number", label: "Configured Function", required: true, rules: [{ required: true }], inputProps: { style: { width: "100%" }, disabled: true } },
        { name: "enable", type: "boolean", label: "Enable", required: true, rules: [{ required: true }], valuePropName: "checked" },
        { name: "agentId", type: "select", label: "Adapter", required: true, rules: [{ required: true }], inputProps: { style: { width: "100%" } } }
    ]

    return <Form {...{ form }}
        size="small"
        labelAlign="left"
        onFinish={values => console.log(values)}
        onFinishFailed={obj => console.error(obj)}
        validateMessages={{
            required: "'${label}' is required"
        }}
    >
        <Row>
            {fieldsProps.map(renderColumn)}
        </Row>
        <Row justify="end" style={{ margin: "0 10px" }}>
            <Col lg={{ span: 3 }} md={{ span: 4 }} sm={{ span: 5 }} xs={{ span: 5 }}>
                <GenericButton text="Request Configuration" width="auto" />
            </Col>
            <Col lg={{ offset: 15, span: 3 }} md={{ offset: 14, span: 3 }} sm={{ offset: 11, span: 4 }} xs={{ offset: 9, span: 5 }}>
                <GenericButton text="Save" type="primary" htmlType="submit" />
            </Col>
            <Col lg={{ span: 3 }} md={{ span: 3 }} sm={{ span: 4 }} xs={{ span: 5 }}>
                <GenericButton text="Close" type="primary" danger onClick={close} />
            </Col>
        </Row>
    </Form>;
}