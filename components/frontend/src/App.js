import { useCallback, useReducer } from "react";
import { Layout } from "antd";
import { getForTable } from "./rest/crud";
import { crudColumns } from "./columns/columns";
import './App.css';
import { TableContent } from "./TableContent";

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

  const { Header, Content, Footer } = Layout;

  return (
    <Layout>
      <Header className="header">Header</Header>
      <Content className="content">
        {detail
          ?
          "SURPRISE MADAFAKKA"
          :
          <TableContent {...{ viewState, dispatch }} />
        }
      </Content>
      <Footer className="footer">Footer</Footer>
    </Layout>
  );
}

export default App;
