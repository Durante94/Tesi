import { Button, Tooltip } from "antd";
import {
  SearchOutlined,
  DownloadOutlined,
  DeleteOutlined,
  EditOutlined
} from "@ant-design/icons";

export const Confirm = ({
  type = "primary",
  htmlType = "",
  width = 150,
  onClick = () => { },
  form = "",
  disabled,
}) => (
  <Button className="cp-Conferma"
    type={type}
    htmlType={htmlType}
    style={{ width }}
    form={form}
    disabled={disabled}
    onClick={() => onClick()}
  >
    {" "}
    Conferma{" "}
  </Button>
);

export const Close = ({
  type = "primary",
  width = 140,
  onClick = () => { },
  disabled,
}) => (
  <Button className="cp-Chiudi"
    style={{ width }}
    onClick={() => onClick()}
    {...{
      type,
      disabled
    }}
  >
    {" "}
    Chiudi{" "}
  </Button>
);

export const Detail = ({
  type = "primary",
  width = 150,
  onClick = () => { },
  disabled,
}) => (
  <Button
    type={type}
    style={{ width }}
    disabled={disabled}
    onClick={() => onClick()}
  >
    {" "}
    Dettagli{" "}
  </Button>
);

export const Search = ({
  tooltipTitle,
  text,
  type,
  shape,
  width,
  onClick = () => { },
  disabled,
}) => (
  <Tooltip title={tooltipTitle}>
    {
      <Button className="cp-search"
        type={type}
        shape={shape}
        disabled={disabled}
        icon={<SearchOutlined />}
        style={{ width: width }}
        onClick={() => onClick()}
      >
        {text}
      </Button>
    }
  </Tooltip>
);

export const Download = ({
  tooltipTitle,
  type,
  shape,
  text,
  width,
  onClick = () => { },
  disabled,
}) => (
  <Tooltip title={tooltipTitle}>
    {
      <Button className="cp-download"
        type={type}
        shape={shape}
        disabled={disabled}
        icon={<DownloadOutlined />}
        style={{ width: width }}
        onClick={() => onClick()}
      >
        {text}
      </Button>
    }
  </Tooltip>
);

export const Delete = ({
  tooltipTitle,
  type,
  shape,
  onClick = () => { },
  disabled,
  danger,
}) => (
  <Tooltip title={tooltipTitle}>
    {
      <Button className="cp-delete"
        {...{ danger, type, shape, disabled, onClick }}
        icon={<DeleteOutlined />}
      ></Button>
    }
  </Tooltip>
);

export const Edit = ({
  tooltipTitle,
  type,
  shape,
  onClick = () => { },
  disabled,
}) => (
  <Tooltip title={tooltipTitle}>
    {
      <Button className="cp-edit"
        type={type}
        shape={shape}
        disabled={disabled}
        icon={<EditOutlined />}
        onClick={() => onClick()}
      ></Button>
    }
  </Tooltip>
);

export const Dispaly = ({
  type,
  shape,
  onClick = () => { },
  disabled,
  icon
}) => (
  <Button className="cp-Dispaly" icon={icon} {...{ type, shape, disabled, onClick }} />
);

export const GenericButton = ({
  type,
  shape,
  disabled,
  text,
  size,
  icon,
  width = 90,
  danger = false,
  loading = false,
  htmlType = "button",
  className = "",
  style,
  onClick = () => { },
}) => (
  <Button
    type={type}
    shape={shape}
    disabled={disabled}
    icon={icon}
    size={size}
    style={{ ...style, width }}
    danger={danger}
    loading={loading}
    htmlType={htmlType}
    className={className}
    onClick={onClick}
  >
    {text}
  </Button>
);
