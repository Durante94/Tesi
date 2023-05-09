import { notification } from "antd";
import axios from "axios";

const BASE_URL = "http://192.168.128.6:7080/",
    GET_ALL = BASE_URL + "api/crud";

export const getForTable = async (stringPayload) => {
    return { "total": 20, "data": [{ "name": "prova 1", "description": "descTest -1", "amplitude": 1.0, "frequency": 9.0, "function": "sin", "enable": false, "agentId": "a", "id": 0, "view": true, "edit": true, "delete": true }, { "name": "prova 2", "description": "descTest -2", "amplitude": 3.9, "frequency": 9.7, "function": "cos", "enable": false, "agentId": "b", "id": 1, "view": true, "edit": true, "delete": true }, { "name": "prova 3", "description": "descTest -3", "amplitude": 5.8, "frequency": 41.8, "function": "sin", "enable": false, "agentId": "c", "id": 2, "view": true, "edit": true, "delete": true }, { "name": "avorp 1", "description": "tsetCsed -1", "amplitude": -6.3, "frequency": 74.69, "function": "cos", "enable": false, "agentId": "d", "id": 3, "view": true, "edit": true, "delete": true }, { "name": "test 2", "description": "testDesc -2", "amplitude": -15.63, "frequency": 954.368, "function": "sin", "enable": false, "agentId": "e", "id": 4, "view": true, "edit": true, "delete": true }, { "name": "prova 1", "description": "descTest -1", "amplitude": 1.0, "frequency": 9.0, "function": "sin", "enable": false, "agentId": "a", "id": 5, "view": true, "edit": true, "delete": true }, { "name": "prova 2", "description": "descTest -2", "amplitude": 3.9, "frequency": 9.7, "function": "cos", "enable": false, "agentId": "b", "id": 6, "view": true, "edit": true, "delete": true }, { "name": "prova 3", "description": "descTest -3", "amplitude": 5.8, "frequency": 41.8, "function": "sin", "enable": false, "agentId": "c", "id": 7, "view": true, "edit": true, "delete": true }, { "name": "avorp 1", "description": "tsetCsed -1", "amplitude": -6.3, "frequency": 74.69, "function": "cos", "enable": false, "agentId": "d", "id": 8, "view": true, "edit": true, "delete": true }, { "name": "test 2", "description": "testDesc -2", "amplitude": -15.63, "frequency": 954.368, "function": "sin", "enable": false, "agentId": "e", "id": 9, "view": true, "edit": true, "delete": true }, { "name": "prova 1", "description": "descTest -1", "amplitude": 1.0, "frequency": 9.0, "function": "sin", "enable": false, "agentId": "a", "id": 10, "view": true, "edit": true, "delete": true }, { "name": "prova 2", "description": "descTest -2", "amplitude": 3.9, "frequency": 9.7, "function": "cos", "enable": false, "agentId": "b", "id": 11, "view": true, "edit": true, "delete": true }, { "name": "prova 3", "description": "descTest -3", "amplitude": 5.8, "frequency": 41.8, "function": "sin", "enable": false, "agentId": "c", "id": 12, "view": true, "edit": true, "delete": true }, { "name": "avorp 1", "description": "tsetCsed -1", "amplitude": -6.3, "frequency": 74.69, "function": "cos", "enable": false, "agentId": "d", "id": 13, "view": true, "edit": true, "delete": true }, { "name": "test 2", "description": "testDesc -2", "amplitude": -15.63, "frequency": 954.368, "function": "sin", "enable": false, "agentId": "e", "id": 14, "view": true, "edit": true, "delete": true }, { "name": "prova 1", "description": "descTest -1", "amplitude": 1.0, "frequency": 9.0, "function": "sin", "enable": false, "agentId": "a", "id": 15, "view": true, "edit": true, "delete": true }, { "name": "prova 2", "description": "descTest -2", "amplitude": 3.9, "frequency": 9.7, "function": "cos", "enable": false, "agentId": "b", "id": 16, "view": true, "edit": true, "delete": true }, { "name": "prova 3", "description": "descTest -3", "amplitude": 5.8, "frequency": 41.8, "function": "sin", "enable": false, "agentId": "c", "id": 17, "view": true, "edit": true, "delete": true }, { "name": "avorp 1", "description": "tsetCsed -1", "amplitude": -6.3, "frequency": 74.69, "function": "cos", "enable": false, "agentId": "d", "id": 18, "view": true, "edit": true, "delete": true }, { "name": "test 2", "description": "testDesc -2", "amplitude": -15.63, "frequency": 954.368, "function": "sin", "enable": false, "agentId": "e", "id": 19, "view": true, "edit": true, "delete": true }], "pageSizeOptions": ["10", "20"] };

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