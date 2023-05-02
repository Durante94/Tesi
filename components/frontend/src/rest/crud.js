import { notification } from "antd";
import axios from "axios";

const BASE_URL = "/api/crud",
    GET_ALL = BASE_URL;

export const getForTable = async (stringPayload) => {
    try {
        const response = await axios.get(GET_ALL + "filter=" + stringPayload);
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