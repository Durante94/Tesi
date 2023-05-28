import { useCallback, useEffect, useRef } from "react";
import SockJsClient from 'react-stomp';

export const WebSocket = ({ dispatch, request }) => {
    const client = useRef();
    const sendMessage = useCallback(msg => client.current.sendMessage('/configRequest', JSON.stringify(msg)), [client]);
    const onMessage = useCallback(msg => {
        console.log(msg);
        dispatch(msg);
    }, [dispatch]);

    useEffect(() => {
        if (request)
            sendMessage(request);
    }, [request, sendMessage])

    return <SockJsClient
        {...{ onMessage }}
        url="/ws-message"
        topics={['/topic/configResponse', '/topic/alarm']}
        ref={cli => { client.current = cli }}
        onConnect={() => { console.log("WS Connected") }}
        onDisconnect={() => { console.log("WS Disconnected") }}
        onConnectFailure={err => console.log(err)}
    />
};