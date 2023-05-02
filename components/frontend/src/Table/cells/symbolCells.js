import { Tag, Tooltip, Spin } from "antd";

const customSymbol = (type, text) => {
    switch (type) {
        case "circle":
            return (
                <svg height="26" width="26">
                    <circle cx="13" cy="13" r="12" stroke="black" strokeWidth="1" fill={text || "gray"} />
                </svg>
            );
        case "rhombus":
            return (
                <svg height="28" width="28" viewBox="12 12 24 24">
                    <path
                        strokeWidth="1"
                        fill={text || "gray"}
                        stroke="black"
                        d="M34.46,22.68 L25.36,13.58C24.6,12.8 23.5,12.8 22.72,13.58L13.62,22.68C12.84,23.46 12.84,24.56 13.62,25.32L22.72,34.42C23.5,35.2 24.6,34.12 25.36,34.42L34.46,25.32C35.12,24.54 35.12,23.46 34.46,22.68Z"
                    ></path>
                </svg>
            );
        case "tag":
            switch (text) {
                case "KO": return <Tag color="volcano">KO</Tag>
                case "SI": return <Tag color="green">SI</Tag>
                case "NO": return <Tag color="red">NO</Tag>
                case "OK": return <Tag color="green">OK</Tag>
                case "INIT": return <Tag color="default">INIT</Tag>
                case "WARN": return <Tag color="yellow">WARN</Tag>
                case "MODIFIED": return <Tag color="yellow">MODIFIED</Tag>
                case "ADDED": return <Tag color="darkSeaGreen">ADDED</Tag>
                case "REMOVED": return <Tag color="violet">REMOVED</Tag>
                case "V": return <Tag color="orange">V</Tag>
                case "LOADING": return <Spin size={"small"} />
                case "ERRORS":
                case "SI_DANGER": return <Tag color="red">SI</Tag>;
                case "NO_PROBLEM": return <Tag color="green">NO</Tag>;
                default: return <Tag color="default">{text}</Tag>
            }
        default:
            return <span></span>;
    }
};

export const SymbolCell = ({ symbol, tooltipSymbol, dataIndex, text }) =>
    tooltipSymbol
        ?
        <Tooltip title={tooltipSymbol[dataIndex]}>{customSymbol(symbol, text)}</Tooltip>
        :
        customSymbol(symbol, text);