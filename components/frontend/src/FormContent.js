import { useCallback, useEffect, useReducer } from "react";
import { Checkbox, Col, Form, Input, InputNumber, Row } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
import { GenericButton } from "./buttons/buttons";
import { getAgents, getDetail, saveData } from "./rest/crud";
import { LazySelect } from "./Select/LazySelect";

const initialState = {
    initialValues: { "agentId": null },
    loading: true
},
    reducer = (state, action) => {
        if (action.type === "form")
            return { initialValues: action.payload, loading: false };
        else
            return { ...state, loading: action.payload };
    }

export const FormContent = ({ edit, id, dispatch, configuration }) => {
    const [{ initialValues, loading }, dispatchForm] = useReducer(reducer, initialState);
    const [form] = Form.useForm();
    const changedAgent = Form.useWatch("agentId", form);
    const { Item } = Form;

    const renderFormItem = useCallback(({ type, inputProps, ...props }) =>
        <Item {...{ ...props }}>
            {{
                "text": <Input allowClear disabled={!edit} {...inputProps} />,
                "number": <InputNumber disabled={!edit} decimalSeparator="." {...inputProps} />,
                "boolean": <Checkbox disabled={!edit} {...inputProps} />,
                "select": <LazySelect disabled={!edit} {...inputProps} />,
                // "": <input type="hidden" />
            }[type]}
        </Item>,
        [edit]);
    const renderColumn = useCallback(({ hidden, ...props }, key) => hidden
        ?
        renderFormItem({ ...props, key })
        :
        <Col xs={24} sm={24} md={12} lg={8} {...{ key }} style={{ padding: "10px 10px 0" }}>
            {renderFormItem(props)}
        </Col>,
        [renderFormItem])
    const close = useCallback(() => dispatch({ type: "detail", payload: { detail: false } }), [dispatch]),
        fetchOptions = useCallback(getAgents, []),
        onFinish = useCallback(values => {
            dispatchForm({ type: "loading", payload: true });
            saveData(values).then(() => close()).catch(() => dispatchForm({ type: "loading", payload: false }))
        }, [close, dispatchForm]);

    useEffect(() => {
        if (isFinite(parseInt(id)))
            getDetail(id).then(obj => dispatchForm({ type: "form", payload: obj }));
        else
            dispatchForm({ type: "loading", payload: false })
    }, [id, dispatchForm]);

    useEffect(() => {
        if (configuration && changedAgent === configuration.agentId) {
            form.setFieldsValue(configuration);
            dispatch({ type: "config", payload: null })
        }
    }, [dispatch, form, configuration, changedAgent])

    useEffect(() => form.setFieldsValue(initialValues), [form, initialValues]);

    const fieldsProps = [
        { name: "id", hidden: true, noStyle: true, type: "" },
        { name: "name", type: "text", label: "Name", required: true, rules: [{ required: true }] },
        { name: "description", type: "text", label: "Description" },
        { name: "agentId", type: "select", label: "Adapter", required: true, rules: [{ required: true }], inputProps: { width: "100%", fetchOptions, optionLabelProp: "value", fieldNames: { value: "value", label: "value" } } },
        { name: "amplitude", type: "number", label: "Configured Amplitude", required: true, rules: [{ required: true }], inputProps: { style: { width: "100%" }, disabled: true } },
        { name: "frequency", type: "number", label: "Configured Frequency", required: true, rules: [{ required: true }], inputProps: { style: { width: "100%" }, disabled: true } },
        { name: "function", type: "number", label: "Configured Function", required: true, rules: [{ required: true }], inputProps: { style: { width: "100%" }, disabled: true } },
        { name: "enable", type: "boolean", label: "Enable", required: true, rules: [{ required: true }], valuePropName: "checked", initialValue: false }
    ]

    return <Form {...{ form, onFinish }}
        size="small"
        labelAlign="left"
        labelCol={{ span: 9 }}
        wrapperCol={{ span: 15 }}
        onFinishFailed={obj => console.error(obj)}
        validateMessages={{
            required: "'${label}' is required"
        }}
    >
        {loading
            ?
            <div className="mask-overlay" style={{
                position: "absolute",
                opacity: 0.7,
                backgroundColor: "gray",
                zIndex: 1000,
                width: "100%",
                height: "100%",
                top: 0,
                left: 0,
                textAlign: "center",
                paddingTop: "5%"
            }}>
                <LoadingOutlined style={{ fontSize: 50, color: "white" }} />
            </div>
            :
            null}
        <Row>
            {fieldsProps.map(renderColumn)}
        </Row>
        <Row justify="end" style={{ margin: "0 10px" }}>
            <Col lg={{ span: 3 }} md={{ span: 4 }} sm={{ span: 5 }} xs={{ span: 5 }}>
                <GenericButton
                    text="Request Configuration"
                    width="auto"
                    disabled={!changedAgent || !edit}
                    onClick={() => {
                        /// TODO: cancellami
                        dispatch({
                            type: "config", payload: {
                                agentId: form.getFieldValue("agentId"),
                                amplitude: 8,
                                frequency: 5,
                                function: "test"
                            }
                        });
                    }}
                />
            </Col>
            <Col lg={{ offset: 15, span: 3 }} md={{ offset: 14, span: 3 }} sm={{ offset: 11, span: 4 }} xs={{ offset: 9, span: 5 }}>
                <GenericButton text="Save" type="primary" htmlType="submit" disabled={!edit || (isFinite(parseInt(id)) && changedAgent !== initialValues.agentId)} />
            </Col>
            <Col lg={{ span: 3 }} md={{ span: 3 }} sm={{ span: 4 }} xs={{ span: 5 }}>
                <GenericButton text="Close" type="primary" danger onClick={close} />
            </Col>
        </Row>
    </Form >;
}