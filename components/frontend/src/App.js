import { useCallback, useReducer } from "react";
import { Layout } from "antd";
import { AntTable } from "./Table/AntTable";
import './App.css';
import { getForTable } from "./rest/crud";
import { crudColumns } from "./columns/columns";

const initialState = {
  viewState: {},
  detail: false,
  edit: false,
}, reducer = (state, action) => {
  switch (action.type) {
    case "table":
      return { ...state, viewState: action.payload };
    case "deatail":
      return { ...state, detail: true, edit: action.payload };
    default:
      break;
  }
};

function App() {
  const [{ viewState, detail, edit }, dispatch] = useReducer(reducer, initialState);
  const restData = useCallback(payload => getForTable(payload), []);
  const getColumns = useCallback(() => crudColumns(), []);
  const updateViewState = useCallback(state => dispatch({ type: "table", payload: state }), [dispatch]);

  const { Header, Content, Footer } = Layout;

  return (
    <Layout>
      <Header className="header">Header</Header>
      <Content className="content">
        {detail
          ?
          null
          :
          <AntTable {...{ restData, getColumns, viewState }} updateViewRange={updateViewState} />}
      </Content>
      <Footer className="footer">Footer</Footer>
    </Layout>
  );
}

export default App;
