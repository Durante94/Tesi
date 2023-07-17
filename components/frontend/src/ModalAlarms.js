import { useCallback, useEffect, useState } from "react";
import { Modal, Badge } from "antd";
import { Delete, GenericButton } from "./buttons/buttons";

export const ModalAlarms = ({ alarmList, dispatch }) => {
    const [open, setOpen] = useState(false);

    const formatAlarm = useCallback((obj) => {
        const formatOpt = { year: "numeric", month: "numeric", day: "numeric", hour: "numeric", minute: "numeric", second: "numeric" };
        let msg = '';
        switch (obj.type) {
            case "connection":
                msg += `Connection lost at ${new Date(obj.time).toLocaleString("it-IT", formatOpt)}, last seen at ${new Date(obj.lastHB).toLocaleString("it-IT", formatOpt)}`;
                break;
            default:
                break;
        }
        return msg;
    }, []),
        onClose = useCallback(() => setOpen(false), [setOpen]);

    useEffect(() => {
        if (open && alarmList.length === 0)
            setOpen(false);
    }, [alarmList, open, setOpen])

    return <>
        <Badge count={alarmList.length}>
            <GenericButton
                text="Alarms"
                disabled={alarmList.length === 0}
                danger
                className="cp-show-alarm"
                onClick={() => setOpen(true)}
            />
        </Badge>
        <Modal title={"Active Alarms"} closable centered footer width={"60vw"} {...{ open }} onCancel={onClose}>
            {alarmList.map((pair, key) => <p {...{ key }}>
                <b>Agent {pair[0]}</b>: <span style={{ marginRight: 5 }}>{formatAlarm(pair[1])}</span>
                <Delete danger={true} onClick={() => dispatch({ type: "toggle-alarm", payload: pair[0] })} />
            </p>)}
        </Modal>
    </>
};