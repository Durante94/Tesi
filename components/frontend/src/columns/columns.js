export const crudColumns = () => ({
    title: '',
    columns: [
        {
            title: 'Nome',
            key: "name",
            dataIndex: 'name',
            type: "string",
            symbol: "none",
            sorter: true,
            search: true
        },
        {
            title: 'Descrizione',
            key: "description",
            dataIndex: 'description',
            type: "string",
            symbol: "none",
            sorter: true,
            search: true
        },
        {
            title: 'Funzione',
            key: "function",
            dataIndex: 'function',
            type: "string",
            symbol: "none",
            sorter: true,
            search: true
        },
        {
            title: 'Ampiezza',
            key: "amplitude",
            dataIndex: 'amplitude',
            type: "number",
            symbol: "none",
            sorter: true,
            search: true
        },
        {
            title: 'Frequenza',
            key: "frequency",
            dataIndex: 'frequency',
            type: "number",
            symbol: "none",
            sorter: true,
            search: true
        },
        {
            title: 'Abilitato',
            key: "enable",
            dataIndex: 'enable',
            type: "boolean",
            symbol: "none",
            sorter: true
        },
    ]
})