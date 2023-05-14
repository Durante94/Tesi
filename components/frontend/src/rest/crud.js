import { notification } from "antd";
import axios from "axios";

const BASE_URL = "/api/",
    GET_ALL = BASE_URL + "crud",
    GET_AGENTS = BASE_URL + 'agent',
    GET_DETAIL = GET_ALL + "/";

export const getForTable = async stringPayload => {
    const params = new URLSearchParams();
    params.append("filter", stringPayload)
    try {
        const response = await axios.get(GET_ALL, { params });
        return response.data;
    } catch (error) {
        notification.error({
            message: "Errore caricamento",
            description: "Impossibile ottenere i dati",
            duration: 6
        })
        console.error(error);
        return {
            total: 0,
            pageSizeOptions: [],
            data: []
        };
    }
};

export const getDetail = async id => {
    try {
        const response = await axios.get(GET_DETAIL + id);
        return response.data;
    } catch (error) {
        notification.error({
            message: "Errore caricamento",
            description: "Impossibile ottenere i dati",
            duration: 6
        })
        console.error(error);
        return {
            total: 0,
            pageSizeOptions: [],
            data: []
        };
    }
};

export const getAgents = async (payload, signal) => {
    const params = new URLSearchParams();
    params.append("filter", payload)
    try {
        const response = await axios.get(GET_AGENTS, { params, signal });
        return response.data;
    } catch (error) {
        if (!axios.isCancel(error)) {
            notification.error({
                message: "Errore caricamento",
                description: "Impossibile ottenere i dati",
                duration: 6
            })
            console.error(error);
        }
        return [];
    }
};

export const checkClick = async (tableName, dataIndex, value, id) => {
    try {
        await axios.post(`${GET_DETAIL}${dataIndex}/${value}`, { id });
    } catch (error) {
        if (error.response.status === 400) {
            notification.error({
                message: "Errore salvataggio",
                description: "No devices",
                duration: 6
            })
        } else if (error.response.status === 404) {
            notification.error({
                message: "Errore salvataggio",
                description: "Device non trovato",
                duration: 6
            })
        } else {
            notification.error({
                message: "Errore salvataggio",
                description: "Impossibile salvare i dati",
                duration: 6
            })
            console.error(error);
        }
        throw error;
    }
};

export const deleteData = async id => {
    try {
        await axios.delete(GET_DETAIL + id);
    } catch (error) {
        notification.error({
            message: "Errore",
            description: "Impossibile cancellare il device",
            duration: 6
        })
        console.error(error);
        return {
            total: 0,
            pageSizeOptions: [],
            data: []
        };
    }
};

export const saveData = async obj => {
    try {
        await axios.post(GET_ALL, obj);
    } catch (error) {
        if (error.response.status === 409) {
            let description;
            if (error.response.data.name === obj.name)
                description = "Nome già in uso"
            else if (error.response.data.agentId === obj.agentId)
                description = "Adapter già in uso";
            notification.error({
                message: "Errore salvataggio",
                description,
                duration: 6
            })
        } else if (error.response.status === 404) {
            notification.error({
                message: "Errore salvataggio",
                description: "Device non trovato",
                duration: 6
            })
        } else {
            notification.error({
                message: "Errore salvataggio",
                description: "Impossibile salvare i dati",
                duration: 6
            })
            console.error(error);
        }
        throw error;
    }
};