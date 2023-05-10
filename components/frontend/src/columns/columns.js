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
            sorter: false,
            width: 100
        },
        {
            title: '',
            key: "view",
            dataIndex: 'view',
            type: "view",
            symbol: "none",
            width: 50
        },
        {
            title: '',
            key: "edit",
            dataIndex: 'edit',
            type: "edit",
            symbol: "none",
            width: 50
        },
        {
            title: '',
            key: "delete",
            dataIndex: 'delete',
            type: "delete",
            symbol: "none",
            width: 50
        }
    ]
})