import { useReducer } from "react";
import { Layout } from "antd";
import { TableContent } from "./TableContent";
import { FormContent } from "./FormContent";
import './App.css';

const initialState = {
  viewState: {},
  detail: false,
  edit: false,
  id: null
}, reducer = (state, action) => {
  switch (action.type) {
    case "table":
      return { ...state, viewState: action.payload, detail: false };
    case "detail":
      return { ...state, ...action.payload };
    default:
      return state;
  }
};

function App() {
  const [{ viewState, detail, edit, id }, dispatch] = useReducer(reducer, initialState);

  const { Header, Content, Footer } = Layout;

  return (
    <Layout>
      <Header className="header">My IOT Device Handler</Header>
      <Content className="content">
        {detail
          ?
          <FormContent {...{ edit, id, dispatch }} />
          :
          <TableContent {...{ viewState, dispatch }} />
        }
      </Content>
      <Footer className="footer"></Footer>
    </Layout>
  );
}

export default App;
