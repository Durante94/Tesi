import { useCallback, useEffect } from "react";
import { GenericButton } from "./buttons/buttons";
import { getDetail } from "./rest/crud";
import { Form } from "antd";

export const FormContent = ({ edit, id, dispatch }) => {
    const close = useCallback(() => dispatch({ type: "detail", payload: { detail: false } }), [dispatch]);

    const [form] = Form.useForm();

    useEffect(() => {
        if (id)
            getDetail(id).then(obj => form.setFieldsValue(obj));
    }, [id, form]);

    return <Form {...{ form }}>
        <GenericButton text="Test" type="primary" onClick={close} />
    </Form>;
}