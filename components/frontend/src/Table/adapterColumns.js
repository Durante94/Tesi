import { SearchOutlined } from '@ant-design/icons';
import { RowRenderer, TitleRenderer } from "./adapterRow";

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

const addSearchInCol = (dataIndex, colLabel, colType, options = {}, multi = false) => ({
  filterDropdown: (filterDropDownProps) => <SearchTablePanel {...filterDropDownProps} {...{ colLabel, colType, dataIndex, options, multi }} />,
  filterIcon: (filtered) => (<SearchOutlined style={{ fontSize: 20, color: filtered ? '#1890ff' : "#000" }} />),
  onFilter: (value, record) => {
    switch (colType) {
      case "string":
        return (record[dataIndex] || '').toString().toLowerCase().includes(value.toLowerCase());
      case "enum":
      case "number":
        return (record[dataIndex] || -1).toString().includes(value.toString());
      default:
        return true;
    }
  },
});

export const adapterColumns = (tableName, rowKeyName, columns, data, onCheck, sort = {}, filters = {}) =>
  columns.map((cl, i) => {
    const searchProps = cl.search ? addSearchInCol(cl.dataIndex, cl.title, cl.type, cl.options || cl.optionMap, cl.multi) : {};

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
      ...searchProps
    };
  });