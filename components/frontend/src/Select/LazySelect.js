import { Select } from "antd";
import { useCallback, useEffect, useReducer, useRef } from "react";

const initState = {
    filterVal: null,
    options: new Map(),
    loading: true,
    numBatch: 0,
    selectedValue: null
}
const reducer = (state, action) => {
    switch (action.type) {
        case "filter":
            return { ...initState, filterVal: action.payload };
        case "scroll":
            return { ...state, numBatch: state.numBatch + 1 };
        case "options":
            const newOptions = new Map(state.options);
            action.payload.options.forEach(opt => newOptions.set(opt[action.payload.key], opt));
            return { ...state, options: newOptions, loading: false };
        case "loading":
            return { ...state, loading: true };
        case "external-change-value":
            if (state.selectedValue !== action.payload)
                return { ...initState, selectedValue: action.payload };
            return state;
        default:
            return state;
    }
}

export const LazySelect = ({
    value,
    width = 180,
    placeholder = "",
    defaultValue,
    className = "",
    optionLabelProp = "label",
    fieldNames = { value: "value", label: "label", options: "options" },
    disabled = false,
    status,
    onChange = () => { },
    fetchOptions = async () => [],
}) => {
    const [{ filterVal, options, loading, numBatch, selectedValue }, dispatch] = useReducer(reducer, { ...initState, selectedValue: value });
    const abortController = useRef(null);//memorizza qualcosa che cambia programmaticamente (da codice) senza causare rendering
    const fetchCallback = useCallback(async (execute = true) => { //memorizzo una funzione
        if (!execute) return;//caso remoto -> opzione select in cui dropdown è visibile, se dropdown è nascosta non scarico le opzioni. 
        //fai fetch o meno

        abortController.current = new AbortController() //salvo in ref, aggiorno poi stato con chiamata rest col dispatch . 
        dispatch({ type: `options`, payload: { key: fieldNames.value, options: await fetchOptions({ filter: filterVal, selectedPage: numBatch, id: selectedValue }, abortController.current.signal) } });
    }, [filterVal, numBatch, selectedValue, fetchOptions, dispatch, fieldNames.value]);   //

    const filterOption = useCallback((input, option) => option[optionLabelProp].toLowerCase().indexOf(input.toLowerCase()) >= 0, [optionLabelProp]);

    const onSearch = useCallback(filterInput => dispatch({ type: "filter", payload: filterInput }), [dispatch]) // dice se stai digitando sulla select

    const onPopupScroll = useCallback(e => {
        const { target } = e;
        if (Math.round(target.scrollTop) + target.offsetHeight === target.scrollHeight) {
            dispatch({ type: "scroll" });
        }
    }, [dispatch]);// se arrivi al fondo scarica nuove opt elemento target  è javascript, vede se è al fondo della dropdown

    useEffect(() => {
        fetchCallback();
        return () => abortController.current.abort();
    }, [fetchCallback]); // cambio qualcosa -> fa partire chiam rest, return restituisce . 

    useEffect(() => {
        dispatch({ type: "external-change-value", payload: value });
    }, [value]);

    return (
        <Select
            {...{ value, disabled, loading, placeholder, className, status, optionLabelProp, fieldNames, filterOption, onChange, onPopupScroll, onSearch }}
            options={[...options.values()]}
            showSearch
            defaultValue={defaultValue ? defaultValue : null}
            style={{ width }}
            optionFilterProp="children"
        />
    );
};
