import { useCallback, useReducer } from "react";
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
      alarms.set(action.payload.id, action.payload);
      return { ...state, alarms }
    default:
      return state;
  }
}, formatAlarm = obj => {
  const formatOpt = { year: "numeric", month: "numeric", day: "numeric", hour: "numeric", minute: "numeric", second: "numeric" };
  let msg = '';
  switch (obj.type) {
    case "connection":
      msg += `Connection lost at ${new Date(obj.time).toLocaleString("it-IT", formatOpt)}, last seen at ${new Date(obj.lastHB).toLocaleString("it-IT", formatOpt)}`;
      break;
    default:
      break;
  }
  console.log(msg);
  return msg;
};

function App() {
  const [{ viewState, detail, edit, id, configuration, alarms }, dispatch] = useReducer(reducer, initialState);

  const { Header, Content, Footer } = Layout;

  const SavedWS = useCallback(() => <WebSocket {...{ dispatch }} />, [dispatch]);

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
        <Badge count={alarms.size}>
          <GenericButton
            text="Alarms"
            disabled={alarms.size === 0}
            danger
            onClick={() => Modal.info({
              title: "Active Alarms",
              content: <>{[...alarms.entries()].map((pair, key) => <p {...{ key }}><b>Agent {pair[0]}</b>: {formatAlarm(pair[1])}</p>)}</>,
              closable: true,
              centered: true,
              width: "60vw"
            })
            }
          />
        </Badge>
      </Footer>
      <SavedWS />
    </Layout>
  );
}

export default App;
