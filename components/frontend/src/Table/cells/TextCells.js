import { Tooltip, Input, Select, Checkbox, InputNumber } from "antd";
import moment from "moment";

const TextCellWrap = View => (props) => props.editable ? <View {...props} /> : props.text;

export const TimestampCell = ({ text }) => moment(text).format("DD/MM/YYYY HH:mm:ss")

const StringCellInput = ({
    uniqueValues,
    warning,
    notification,
    readOnly,
    disabled,
    conditional,
    text,
    hasLength,
    maxLength,
    tableName,
    rowKeyName,
    id,
    dataIndex,
    onChange
}) => {
    const title = uniqueValues
        ?
        warning
            ?
            warning
            :
            notification
                ?
                notification
                :
                null
        :
        null;
    return (
        <Tooltip {...{ title }}>
            <Input
                disabled={readOnly || disabled || (conditional != null ? conditional : false)}
                value={text}
                status={uniqueValues && warning != null ? 'error' : null}
                maxLength={hasLength ? maxLength : null}
                className="cp-descrInOut"
                onChange={(e) => {
                    const { value } = e.target;
                    if (onChange) {
                        onChange(
                            tableName,
                            rowKeyName,
                            id,
                            dataIndex,
                            value);
                    }
                }}
            />
        </Tooltip>
    );
}

export const StringCell = TextCellWrap(StringCellInput);

const SelectCellInput = ({
    readOnly,
    disabled,
    conditional,
    text,
    onChange,
    tableName,
    rowKeyName,
    id,
    dataIndex,
    options,
    filterBy
}) => (
    <Select
        disabled={readOnly || disabled || (conditional != null ? conditional : false)}
        value={text}
        style={{ width: "100%" }}
        dropdownMatchSelectWidth={false}
        className="cp-select"
        fieldNames={{ value: "value", label: "descr" }}
        onChange={(value) => {
            if (onChange) {
                onChange(
                    tableName,
                    rowKeyName,
                    id,
                    dataIndex,
                    value
                );
            }
        }}
        options={options.reduce((renderedOpts, opt) => filterBy == null || filterBy === opt.parentId
            ?
            renderedOpts.concat([{ ...opt, className: "cp-selectOpt" }])
            :
            renderedOpts,
            [])}
    />
);

export const SelectCell = TextCellWrap(SelectCellInput);

export const SimpleSelectCell = ({ text, options }) => <span>{options[text] || text || ''}</span>;

const NumberCellInput = ({
    readOnly,
    conditional,
    text,
    dataIndex,
    tableName,
    id,
    unitOfMeasure,
    rowKeyName,
    onChange
}) => (
    <InputNumber
        disabled={readOnly || (conditional != null ? conditional : false)}
        value={text}
        style={{ width: "100%" }}
        controls={dataIndex === 'multiply' || dataIndex === 'offset' ? false : true}
        addonAfter={tableName === "phasorsList" && dataIndex === 'offset' && unitOfMeasure ? unitOfMeasure : ""}
        onChange={(value) => {
            if (onChange) {
                onChange(
                    tableName,
                    rowKeyName,
                    id,
                    dataIndex,
                    value
                );
            }
        }}
    />
);

export const NumberCell = TextCellWrap(NumberCellInput);

export const BooleanCell = ({
    readOnly,
    conditional,
    text,
    onChange,
    tableName,
    rowKeyName,
    id,
    dataIndex
}) => <span>
        <Checkbox
            disabled={readOnly || (conditional != null ? conditional : false)}
            checked={text}
            onChange={(e) => {
                if (onChange) {
                    onChange(tableName, dataIndex, e.target.checked, id);
                }
            }}
        />
    </span>;