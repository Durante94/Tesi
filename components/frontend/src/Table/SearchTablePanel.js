import { useMemo, useState, useCallback, useRef, useEffect } from "react";
import { Select, Input, InputNumber, Space, Button, DatePicker } from "antd";
import { SearchOutlined } from '@ant-design/icons';

import moment from "moment";

export const SearchTablePanel = ({ colLabel, colType, selectedKeys, confirm, dataIndex, setSelectedKeys, clearFilters, options = {}, multi = false }) => {
    const [inputValue, setValue] = useState(multi ? selectedKeys || [] : selectedKeys[0] || '');
    const inputRef = useRef(null);

    useEffect(() => {
        if (inputRef.current)
            inputRef.current.focus();
    }, [inputRef]);

    const submitFilterValue = useCallback(async () => {
        await setSelectedKeys(Array.isArray(inputValue) ? inputValue : [inputValue]);
        confirm();
    }, [inputValue, confirm, setSelectedKeys]);

    const inputProps = useMemo(() => {
        const resultProp = {
            placeholder: `Cerca per ${colLabel}`,
            style: { marginBottom: 10, display: 'block', width: "100%" },
            onPressEnter: (event) => {
                event.stopPropagation();
                submitFilterValue();
            },
            ref: inputRef,
            //className: "cp-ricerca-" + dataIndex
        }
        if (multi)
            resultProp["mode"] = "multiple"; //filtro multiselect

        switch (colType) {
            case "customSelect":
            case "enum":
                resultProp["className"] = "cy-table-filt-select";
                resultProp["options"] = Object.keys(options instanceof Object ? options : {}).map(optKey => ({ value: optKey, label: options[optKey], className: "cy-filter-opt" }));
                break;
            case "select":
                resultProp["className"] = "cy-table-filt-select";
                resultProp["options"] = (Array.isArray(options) ? options : []).map(opt => ({ value: opt.value, label: opt.descr, className: "cy-filter-opt" }));
                break;
            default:
                break;
        }

        return resultProp;
    }, [colLabel, colType, multi, inputRef, submitFilterValue]);/*dataIndex, */

    return (<div style={{ padding: 15 }}>
        {{
            "string": (<Input {...inputProps} value={inputValue} onChange={e => setValue(e.target.value)} className="cp-inpNomeDev" />),
            "enum": <Select {...inputProps} value={inputValue} onChange={setValue} />,
            "select": <Select {...inputProps} value={inputValue} onChange={setValue} />,
            "customSelect": <Select {...inputProps} value={inputValue} onChange={setValue} />,
            "number": (<InputNumber {...inputProps} value={inputValue} onChange={setValue} />),
            // "ip_address": (<AntIpAddress {...inputProps} onChange={ip => assignValue(ip)} />),
            "timestamp": (<DatePicker
                {...inputProps}
                format="YYYY-MM-DD HH:mm:ss"
                showTime={{
                    defaultValue: moment("00:00:00", "HH:mm:ss"),
                }}
                value={inputValue}
                onChange={setValue} />),
            '': null,
            /*"boolean": (options && options instanceof Object
                ?
                <Select {...inputProps} value={inputValue} onChange={val => assignValue(val)}>
                    {Object.keys(options).map(optKey => <Option key={optKey} value={optKey}>{options[optKey]}</Option>)}
                </Select>
                :
                null),*/
        }[colType]}
        <Space style={{ margin: "10px 20px 0" }}>
            <Button
                type="primary"
                className="cp-cerca"
                onClick={submitFilterValue}
                icon={<SearchOutlined />}
                size="middle"
                style={{ width: 100 }}
                disabled={
                    ["string", /*"ip_address"*/].includes(colType)
                        ?
                        inputValue.length < 3
                        :
                        false}
            >
                Cerca
            </Button>
            <Button
                className="cp-reset"
                onClick={() => {
                    if (clearFilters) {
                        clearFilters();
                        confirm();
                        setValue(multi ? [] : '');
                    }
                }}
                size="middle"
                style={{ width: 100 }}
            >
                Reset
            </Button>
        </Space>
    </div>);
};