import { Badge } from "antd";
import { Dispaly, Download, Edit, Delete, Search } from "../../buttons/buttons";
import {
    DownloadOutlined,
    EyeOutlined
  } from "@ant-design/icons";

export const BadgeCell = ({ countData, display, disabled, onDispalyModal }) =>
    <Badge count={countData} showZero>
        <Dispaly
            tooltipTitle="Mostra dettagli"
            type="primary"
            shape="square"
            icon={<EyeOutlined />}
            disabled={!display || disabled || countData === 0}
            onClick={onDispalyModal}
        />
    </Badge>

export const BadgeBlobsCell = ({ countBlobs, display, disabled, onDisplayBlob }) =>
    <Badge count={countBlobs} showZero>
        <Dispaly
            tooltipTitle="Mostra dettagli"
            type="primary"
            shape="square"
            icon={<DownloadOutlined />}
            disabled={!display || disabled || countBlobs === 0}
            onClick={onDisplayBlob}
        />
    </Badge>

export const DownloadCell = ({ download, disabled, onDownloadClick }) =>
    <Download
        tooltipTitle="Download"
        type="primary"
        shape="square"
        disabled={!download || disabled}
        onClick={onDownloadClick}
    />

export const ViewCell = ({ view, disabled, onViewClick }) =>
    <Search
        tooltipTitle="Dettaglio/Visualizza"
        type="primary"
        shape="square"
        disabled={!view || disabled}
        onClick={onViewClick}
    />

export const EditCell = ({ edit, disabled, onEditClick }) =>
    <Edit
        tooltipTitle="Dettaglio/Modifica"
        type="primary"
        shape="square"
        disabled={!edit || disabled}
        onClick={onEditClick}
    />

export const DeleteCell = ({ canDelete, disabled, onDeleteClick }) =>
    <Delete
        disabled={!canDelete || disabled}
        tooltipTitle="Cancella"
        type="danger"
        shape="square"
        onClick={onDeleteClick}
    />