import { RowRenderer, TitleRenderer } from "./adapterRow";
import { SymbolCell } from "../tables/cells/symbolCells";
import { SelectCell, StringCell, TimestampCell, SimpleSelectCell, NumberCell, BooleanCell } from "../tables/cells/TextCells";

const columnFixer = (colName, rowKeyName, colHasSymbol) => {
  if (colName === rowKeyName || colName === "expandable") {
    return "left";
  }
  if (colName === "view" || colName === "edit" || colName === "delete" || colName === "download" || colName === "badge" || colHasSymbol) {
    return "right";
  }
  return false;
};

const columnAligner = (colType, colHasSymbol) => {
  if (colType === "number")
    return "right"
  else if (colHasSymbol || colType === "boolean")
    return "center"
  else
    return "left";
}

const sorterType = (type, key, inPlace = true) => {
  if (inPlace) {
    switch (type) {
      case "enum":
      case "number":
        return (a, b) => a[key] - b[key];
      case "string":
        return (a, b) => {
          console.log(a, b, key);
          return a[key] || "".localeCompare(b[key])
        };
      default:
        break;
    }
  } else
    return true;
};

export const adapterColumns = (tableName, rowKeyName, columns, data, onCheck, sort = {}, filters = {}, addSearchInCol = () => ({}), handleResize = () => ({})) =>
  columns.map((cl, i) => {
    const searchProps = cl.search ? addSearchInCol(cl.dataIndex, cl.title, cl.type, cl.options || cl.optionMap, cl.multi) : {},
      resizeProps = cl.resize
        ?
        {
          onHeaderCell: (column) => ({
            width: column.width,
            onResize: handleResize(i),
          })
        }
        :
        {};

    return {
      title: <TitleRenderer {...{ data, ...cl, filters, tableName, onCheck }} />,
      dataIndex: cl.dataIndex,
      key: cl.key,
      sorter: cl.sorter,
      filteredValue: filters[cl.dataIndex] || [],
      width: cl.width,
      type: cl.type,
      fixed: columnFixer(cl.key, rowKeyName, cl.symbol === "rhombus" || cl.symbol === "tag" || cl.symbol === "circle" || cl.fixed),
      align: columnAligner(cl.type, cl.symbol === "rhombus" || cl.symbol === "tag" || cl.symbol === "circle"),
      render: (text, record) => <RowRenderer {...{ tableName, rowKeyName, text, record, column: cl }} />,
      ...searchProps,
      ...resizeProps
    };
  });

export const customRender = (
  text,
  type,
  symbol,
  record,
  dataIndex,
  readOnly,
  editable,
  tableName,
  rowKeyName,
  uniqueValues,
  maxLength,
  options = [],
  width = 0
) => {
  if (symbol !== "none")
    return <SymbolCell {...{ ...record, editable, symbol, dataIndex, text }} />;

  switch (type) {
    case "timestamp":
      return <TimestampCell {...{ text }} />;
    case "string":
      return <StringCell {...{ ...record, editable, uniqueValues, readOnly, text, hasLength: maxLength, tableName, rowKeyName, id: record[rowKeyName], dataIndex }} />
    case "select":
      return <SelectCell {...{ ...record, editable, readOnly, text, tableName, rowKeyName, id: record[rowKeyName], dataIndex, options }} />
    case "enum":
    case "customSelect":
      return <SimpleSelectCell />;
    case "number":
      return <NumberCell {...{ ...record, editable, readOnly, text, dataIndex, tableName, id: record[rowKeyName] }} />
    case "boolean":
      return <BooleanCell {...{ ...record, editable, text, tableName, rowKeyName, id: record[rowKeyName], dataIndex }} />;
    default:
      break;
  }
};