import { useCallback, useReducer } from "react";
import { Layout } from "antd";
import { AntTable } from "./Table/AntTable";
import { getForTable } from "./rest/crud";
import { crudColumns } from "./columns/columns";
import './App.css';

const initialState = {
  viewState: {},
  detail: false,
  edit: false,
}, reducer = (state, action) => {
  switch (action.type) {
    case "table":
      return { ...state, viewState: action.payload, detail: false };
    case "detail":
      return { ...state, detail: true, edit: action.payload };
    default:
      return state;
  }
};

function App() {
  const [{ viewState, detail, edit }, dispatch] = useReducer(reducer, initialState);
  const restData = useCallback(payload => getForTable(payload), []);
  const getColumns = useCallback(() => crudColumns(), []);
  const updateViewState = useCallback(state => dispatch({ type: "table", payload: state }), [dispatch]);
  const onRowView = useCallback(id => dispatch({ type: "detail", payload: false }), [dispatch]);
  const onRowEdit = useCallback(id => dispatch({ type: "detail", payload: true }), [dispatch]);
  const onRowDelete = useCallback(id => console.log(id), []);

  const { Header, Content, Footer } = Layout;

  return (
    <Layout>
      <Header className="header">Header</Header>
      <Content className="content">
        {detail
          ?
          "SURPRISE MADAFAKKA"
          :
          <AntTable {...{ restData, getColumns, viewState, onRowView, onRowEdit, onRowDelete }} rowKey="id" rowName="name" updateViewRange={updateViewState} />}
      </Content>
      <Footer className="footer">Footer</Footer>
    </Layout>
  );
}

export default App;
