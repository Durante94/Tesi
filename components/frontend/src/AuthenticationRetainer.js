import { useEffect, useState } from "react";
import axios from "axios";
import { unauthRedirectHandler } from "common-layout";

export const AuthenticationRetainer = () => {
    const [timeSpan, setTimeSpan] = useState({ value: 50 });

    useEffect(() => {
        const timeout = setTimeout(() => {
            unauthRedirectHandler(axios.get("/gateway/auth/refresh")).then(resp => setTimeSpan({ value: resp.data })).catch(e => {
                console.error(e);
                setTimeSpan({ value: 3 });
            })
        }, timeSpan.value * 1000);

        return () => clearTimeout(timeout);
    }, [timeSpan])

    return null;
}
