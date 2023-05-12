import { useReducer } from "react";
import { Badge, Layout, Modal } from "antd";
import { TableContent } from "./TableContent";
import { FormContent } from "./FormContent";
import './App.css';
import { WebSocket } from "./WebSocket";
import { GenericButton } from "./buttons/buttons";

const initialState = {
  viewState: {},
  detail: false,
  edit: false,
  id: null,
  configuration: {},
  alarms: new Map()
}, reducer = (state, action) => {
  switch (action.type) {
    case "table":
      return { ...state, viewState: action.payload, detail: false };
    case "detail":
      return { ...state, ...action.payload };
    case "config":
      return { ...state, configuration: action.payload };
    case "alarm":
      const alarms = new Map(state.alarms);
      alarms.set(action.payload.id, action.payload.message);
      return { ...state, alarms }
    default:
      return state;
  }
};

function App() {
  const [{ viewState, detail, edit, id, configuration, alarms }, dispatch] = useReducer(reducer, initialState);

  const { Header, Content, Footer } = Layout;

  return (
    <Layout>
      <Header className="header">My IOT Device Handler</Header>
      <Content className="content">
        {detail
          ?
          <FormContent {...{ edit, id, configuration, dispatch }} />
          :
          <TableContent {...{ viewState, dispatch }} />
        }
      </Content>
      <Footer className="footer">
        <Badge count={alarms.length}>
          <GenericButton
            text="Alarms"
            disabled={alarms.length === 0}
            onClick={() => Modal.info({

            })}
          />
        </Badge>
      </Footer>
      <WebSocket {...{ dispatch }} />
    </Layout>
  );
}

export default App;
