import { notification } from "antd";
import axios from "axios";

const BASE_URL = "http://192.168.128.6:7080/",
    GET_ALL = BASE_URL + "api/crud";

export const getForTable = async (stringPayload) => {
    return { "total": 5, "data": [{ "name": "prova 1", "description": "descTest -1", "amplitude": 1.0, "frequency": 9.0, "function": "sin", "enable": false, "agentId": "a", "id": 0 }, { "name": "prova 2", "description": "descTest -2", "amplitude": 3.9, "frequency": 9.7, "function": "cos", "enable": false, "agentId": "b", "id": 1 }, { "name": "prova 3", "description": "descTest -3", "amplitude": 5.8, "frequency": 41.8, "function": "sin", "enable": false, "agentId": "c", "id": 2 }, { "name": "avorp 1", "description": "tsetCsed -1", "amplitude": -6.3, "frequency": 74.69, "function": "cos", "enable": false, "agentId": "d", "id": 3 }, { "name": "test 2", "description": "testDesc -2", "amplitude": -15.63, "frequency": 954.368, "function": "sin", "enable": false, "agentId": "e", "id": 4 }], "pageSizeOptions": ["5"] };

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