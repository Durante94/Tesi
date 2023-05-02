import { useMemo } from "react";
import { Checkbox } from "antd";
import { BadgeCell, DeleteCell, DownloadCell, EditCell, ViewCell, BadgeBlobsCell } from "../tables/cells/buttonCells";
import { SymbolCell } from "../tables/cells/symbolCells";
import { TimestampCell, StringCell, SelectCell, SimpleSelectCell, NumberCell, BooleanCell } from "../tables/cells/TextCells";

export const RowRenderer = ({ tableName, rowKeyName, text, record, column }) => {
    if (column.symbol !== "none")
        return <SymbolCell {...{ ...record, ...column, text }} />

    switch (column.type) {
        case "badge":
            return <BadgeCell {...record} />
        case "badgeblob":
            return <BadgeBlobsCell {...record} />
        case "download":
            return <DownloadCell {...record} />
        case "view":
            return <ViewCell {...record} />
        case "edit":
            return <EditCell {...record} />
        case "delete":
            return <DeleteCell {...{ ...record, canDelete: record.delete }} />
        case "timestamp":
            return <TimestampCell {...{ text }} />
        case "string":
            return <StringCell {...{ ...column, ...record, text, tableName, rowKeyName, hasLength: column.maxLength, id: record[rowKeyName] }} />
        case "select":
            return <SelectCell {...{ ...column, ...record, text, tableName, rowKeyName, id: record[rowKeyName] }} />
        case "enum":
        case "customSelect":
            return <SimpleSelectCell {...{ text, ...column, options: column.options || column.optionMap }} />
        case "number":
            return <NumberCell {...{ ...column, ...record, text, tableName, rowKeyName, id: record[rowKeyName] }} />
        case "boolean":
            return <BooleanCell {...{ ...column, ...record, text, tableName, rowKeyName, id: record[rowKeyName] }} />
        default:
            return null;
    }
}

export const TitleRenderer = ({
    data,
    dataIndex,
    type,
    readOnly,
    title,
    filters,
    multi,
    options,
    optionMap = {},
    onCheck,
    tableName
}) => {
    const checkedCount = useMemo(() => data.filter(r => r[dataIndex]).length, [data, dataIndex]);
    const filteredValue = useMemo(() => {
        let value = '';
        if (filters[dataIndex] && filters[dataIndex].length > 0) {
            if (type === "enum") {
                const filteredValues = filters[dataIndex].map(filt => options[filt]);
                value = `[${(multi ? filteredValues.join() : filteredValues[0])}]`;
            }
            else if (type === "customSelect") {
                const filteredValues = filters[dataIndex].map(filt => optionMap[filt]);
                value = `[${(multi ? filteredValues.join() : filteredValues[0])}]`;
            }
            else if (type === "select") {
                const filteredValues = filters[dataIndex].map(filt => options.find(c => c.value === filt).descr);
                value = `[${(multi ? filteredValues.join() : filteredValues[0])}]`;
            }
            else if (Array.isArray(filters[dataIndex]))
                value = `[${filters[dataIndex][0]}]`;
            else
                value = `[${filters[dataIndex]}]`
        }
        return value;
    }, [filters, dataIndex, type, multi, optionMap, options]);

    return type !== "boolean" || readOnly || !data
        ?
        <>
            {title} <span style={{ fontWeight: "bolder", float: "right", color: "#1890ff", wordBreak: "break-word" }}>{filteredValue}</span>
        </>
        :
        <span>
            {onCheck
                ?
                (<Checkbox
                    checked={data.length && checkedCount === data.length}
                    disabled={!data.length}
                    indeterminate={checkedCount > 0 && checkedCount < data.length}
                    onChange={(e) => {
                        if (onCheck) {
                            onCheck(tableName, dataIndex, e.target.checked);
                        }
                    }}
                >
                    {title}
                </Checkbox>)
                :
                title}
        </span>;
}