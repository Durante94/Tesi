import { useCallback, useRef } from "react";
import SockJsClient from 'react-stomp';

export const WebSocket = ({ dispatch }) => {
    // const client = useRef();
    // const sendMessage = msg => client.current.sendMessage('', msg);
    const onMessage = useCallback(msg => {
        console.log(msg);
        dispatch(msg);
    }, [dispatch]);

    return <SockJsClient
        {...{ onMessage }}
        url="/ws-message"
        topics={['/configResponse']}
        // ref={cli => {client.current = cli}}
        onConnect={() => { console.log("WS Connected") }}
        onDisconnect={() => { console.log("WS Disconnected") }}
        onConnectFailure={err => console.error(err)}
    />
};